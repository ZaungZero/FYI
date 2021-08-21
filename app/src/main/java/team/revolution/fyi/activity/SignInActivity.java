package team.revolution.fyi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.tinydb.TinyDB;

import java.util.Objects;

import team.revolution.fyi.R;
import team.revolution.fyi.firebase.FirebaseContract;
import team.revolution.fyi.model.UserAccount;
import team.revolution.fyi.utils.Condition;
import team.revolution.fyi.utils.Constant;

public class SignInActivity extends AppCompatActivity {

    private MaterialTextView register_txtView;
    private ShapeableImageView login_and_register_imgView;
    private TextInputLayout custom_code_txt_inputLayout, name_txt_inputLayout;
    private TextInputEditText custom_code_edt, name_edt, gmail_edt, password_edt;
    private MaterialButton login_or_register_btn, resend_email_btn;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ProgressDialog progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_signin);

        defineId();

        register_txtView.setPaintFlags(register_txtView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mAuth = FirebaseAuth.getInstance();

        onClick();
    }

    private void defineId() {
        register_txtView = findViewById(R.id.txtView_register);
        login_and_register_imgView = findViewById(R.id.imgView_signUp_logo);
        custom_code_txt_inputLayout = findViewById(R.id.txt_inputLayout_custom_code);
        name_txt_inputLayout = findViewById(R.id.txt_inputLayout_name);
        custom_code_edt = findViewById(R.id.edt_custom_code);
        name_edt = findViewById(R.id.edt_name);
        gmail_edt = findViewById(R.id.edt_gmail);
        password_edt = findViewById(R.id.edt_password);
        login_or_register_btn = findViewById(R.id.btn_signUp);
        resend_email_btn = findViewById(R.id.btn_resend_email);
    }

    private void onClick() {
        register_txtView.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        login_or_register_btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(gmail_edt.getText()))
                gmail_edt.setError("Enter your email!");
            else if (!Condition.isValidEmail(Objects.requireNonNull(gmail_edt.getText()).toString()))
                gmail_edt.setError("Your email is not valid!");
            else if (TextUtils.isEmpty(password_edt.getText()))
                password_edt.setError("Enter password!");
            else {
                progress = new ProgressDialog(this);
                progress.setTitle("Loading");
                progress.setMessage("Please wait while sign in...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                login_or_register_btn.setEnabled(false);
                mAuth.signInWithEmailAndPassword(gmail_edt.getText().toString(), password_edt.getText().toString())
                        .addOnFailureListener(e -> Toast.makeText(SignInActivity.this, "Your login fail, " + e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnCompleteListener(SignInActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user1 = mAuth.getCurrentUser();
                                if (user1 != null) {
                                    if (user1.isEmailVerified()) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.UserAccount.USERACCOUNT_TABLE);
                                        reference.orderByChild(FirebaseContract.UserAccount.GMAIL).equalTo(gmail_edt.getText().toString()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getChildrenCount() != 0) {
                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                        progress.dismiss();
                                                        login_or_register_btn.setEnabled(true);
                                                        UserAccount userAccount = data.getValue(UserAccount.class);
                                                        assert userAccount != null;
                                                        TinyDB tinyDB = new TinyDB(SignInActivity.this);
                                                        tinyDB.putString(FirebaseContract.UserAccount.CUSTOM_SECRET_CODE, userAccount.getCustom_secret_code());
                                                        tinyDB.putString(FirebaseContract.UserAccount.AUTHOR_NAME, userAccount.getName());
                                                        Log.e("AUTHOR_NAME_IN", tinyDB.getString(FirebaseContract.UserAccount.AUTHOR_NAME));
                                                        Log.e("CUSTOM_SECRET_CODE_IN", tinyDB.getString(FirebaseContract.UserAccount.CUSTOM_SECRET_CODE));
                                                        switch (tinyDB.getString(Constant.WHERE)) {
                                                            case Constant.WRITE_NOTE: {
                                                                Intent intent;
                                                                if (userAccount.getRole().equals(Constant.MODERATOR) || userAccount.getRole().equals(Constant.ADMIN)) {
                                                                    intent = new Intent(SignInActivity.this, CreateNoteActivity.class);
                                                                } else {
                                                                    intent = new Intent(SignInActivity.this, NoteViewActivity.class);
                                                                }
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                overridePendingTransition(0, 0);
                                                                finish();
                                                                break;
                                                            }
                                                            case Constant.ONLY_LOGIN: {
                                                                Intent intent = new Intent(SignInActivity.this, NoteViewActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                overridePendingTransition(0, 0);
                                                                finish();
                                                                break;
                                                            }
                                                            case Constant.WRITE_COMMENT: {
                                                                Intent intent = new Intent(SignInActivity.this, NoteViewActivity.class);
                                                                intent.putExtra(Constant.WHERE, 1);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                overridePendingTransition(0, 0);
                                                                finish();
                                                                break;
                                                            }
                                                            case Constant.MODIFIED_NOTE: {
                                                                Intent intent = new Intent(SignInActivity.this, NoteViewActivity.class);
                                                                intent.putExtra(Constant.WHERE, 2);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                overridePendingTransition(0, 0);
                                                                finish();
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else {
                                        progress.dismiss();
                                        login_or_register_btn.setEnabled(true);
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignInActivity.this);
                                        alertDialogBuilder.setTitle("Alert");
                                        alertDialogBuilder
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setMessage("You need to verify email, please click on the link in your email and sign in again!")
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", (dialog, id) -> {
                                                    resend_email_btn.setVisibility(View.VISIBLE);
                                                    resend_email_btn.setOnClickListener(v1 -> {
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        try {
                                                            if (user != null)
                                                                user.sendEmailVerification()
                                                                        .addOnCompleteListener(task1 -> {
                                                                            Toast.makeText(SignInActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                                                                        });

                                                        } catch (Exception e) {
                                                        }
                                                    });
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onPause() {
        if (progress != null)
            progress.dismiss();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (progress != null)
            progress.dismiss();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignInActivity.this, NoteViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }
}
