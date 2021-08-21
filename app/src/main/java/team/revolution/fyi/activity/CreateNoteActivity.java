package team.revolution.fyi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.circularreveal.CircularRevealRelativeLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mukesh.tinydb.TinyDB;

import java.util.Objects;

import team.revolution.fyi.R;
import team.revolution.fyi.firebase.FirebaseContract;
import team.revolution.fyi.model.Note;
import team.revolution.fyi.utils.Constant;
import team.revolution.fyi.utils.DateUtilities;
import team.revolution.fyi.utils.DeviceUtils;

public class CreateNoteActivity extends AppCompatActivity {
    private TextInputEditText note_edt;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private MaterialButton add_note_btn;
    private CircularRevealRelativeLayout coordinatorLayout;
    private String note = "", note_uid = "";
    private TinyDB tinyDB;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_note);
        setTitle("Create note");
        MaterialToolbar topToolBar = findViewById(R.id.toolbar1);
        setSupportActionBar(topToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        defineId();

        tinyDB = new TinyDB(CreateNoteActivity.this);

        note_edt.setTextSize(tinyDB.getInt(Constant.FONT_SIZE));
        note_edt.setVerticalScrollBarEnabled(true);
        note_edt.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();

        note_edt.setTextIsSelectable(true);

        note_edt.setFocusableInTouchMode(true);
        if (intent != null) {
            if (intent.getIntExtra(Constant.WHERE, 0) == 7) {
                note_uid = tinyDB.getString(Constant.NOTE_UID);
                note = tinyDB.getString(Constant.NOTE_EDIT);
                note_edt.setText(note);
                add_note_btn.setText("Update");
            } else {
                note_edt.setText(tinyDB.getString(Constant.NOTE));
                DeviceUtils.showKeyboard(this);
                note_edt.setSelection(Objects.requireNonNull(note_edt.getText()).length());
                note_edt.requestFocus();
            }
        }

        mAuth = FirebaseAuth.getInstance();

        String CUSTOM_SECRET_CODE = tinyDB.getString(FirebaseContract.UserAccount.CUSTOM_SECRET_CODE);
        Log.e("CUSTOM_SECRET_CODE", CUSTOM_SECRET_CODE);
        String AUTHOR_NAME = tinyDB.getString(FirebaseContract.UserAccount.AUTHOR_NAME);
        Log.e("AUTHOR_NAME", AUTHOR_NAME);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        assert firebaseUser != null;
        note_edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                add_note_btn.setClickable(note_edt.getText().length() != 0);
                add_note_btn.setEnabled(note_edt.getText().length() != 0);
                tinyDB.putString(Constant.NOTE, note_edt.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        coordinatorLayout.setOnClickListener(v -> {
            note_edt.requestFocus();
            DeviceUtils.showKeyboard(CreateNoteActivity.this);
        });

        add_note_btn.setOnClickListener(v -> {
            if (Objects.requireNonNull(note_edt.getText()).length() != 0) {
                add_note_btn.setEnabled(false);
                String key;
                if (note_uid.length() != 0)
                    key = note_uid;
                else
                    key = databaseReference.child(FirebaseContract.UserAccount.USERACCOUNT_TABLE).child(firebaseUser.getUid()).child(FirebaseContract.Note.NOTE_TABLE).push().getKey();
                Note note = new Note();
                note.setNote_time_stamp(DateUtilities.currentTimeStamp());
                note.setNote(note_edt.getText().toString());
                note.setNote_uid(key);
                note.setAuthor_name(tinyDB.getString(FirebaseContract.UserAccount.AUTHOR_NAME));
                note.setAuthor_custom_secret_code(tinyDB.getString(FirebaseContract.UserAccount.CUSTOM_SECRET_CODE));
                note.setAuthor_uid(firebaseUser.getUid());
                note.setNote_pinned(false);
                note.setNote_public(false);
                assert key != null;
                databaseReference.child(FirebaseContract.UserAccount.USERACCOUNT_TABLE).child(firebaseUser.getUid()).child(FirebaseContract.Note.NOTE_TABLE).child(key).setValue(note).addOnCompleteListener(CreateNoteActivity.this, task -> {
                    if (task.isSuccessful()) {
                        add_note_btn.setEnabled(true);
                        tinyDB.putString(Constant.NOTE, "");
                        if (note_uid.length() != 0)
                            Toast.makeText(CreateNoteActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(CreateNoteActivity.this, "Added", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }
        });
    }

    private void defineId() {
        note_edt = findViewById(R.id.edt_note);
        add_note_btn = findViewById(R.id.btn_addNote);
        coordinatorLayout = findViewById(R.id.coordinator);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (note_uid.length() != 0)
            tinyDB.putString(Constant.NOTE_EDIT, "");
        Intent intent = new Intent(CreateNoteActivity.this, NoteViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, R.anim.play_panel_close_background);
        finish();
    }
}
