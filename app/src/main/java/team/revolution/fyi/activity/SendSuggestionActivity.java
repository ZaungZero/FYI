package team.revolution.fyi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import team.revolution.fyi.R;
import team.revolution.fyi.adapter.SuggestionRecyclerViewAdapter;
import team.revolution.fyi.firebase.FirebaseContract;
import team.revolution.fyi.model.Suggestion;
import team.revolution.fyi.model.UserAccount;
import team.revolution.fyi.utils.DateUtilities;
import team.revolution.fyi.utils.SpeedyLinearLayoutManager;

public class SendSuggestionActivity extends AppCompatActivity {
    private TextInputEditText edt_suggest;
    private AppCompatImageButton btn_send_suggest;
    private RecyclerView recyclerView_suggestion;
    private FirebaseAuth firebaseAuth;
    private UserAccount useUserAccount;
    private List<Suggestion> suggestionList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_suggestion);
        setTitle("Suggestion");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        defineId();

        firebaseAuth = FirebaseAuth.getInstance();
        useUserAccount = new UserAccount();
        suggestionList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.UserAccount.USERACCOUNT_TABLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                        if (firebaseAuth.getCurrentUser() != null) {
                            assert userAccount != null;
                            if (firebaseAuth.getCurrentUser().getUid().equals(userAccount.getUid()))
                                useUserAccount = userAccount;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edt_suggest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(edt_suggest.getText()).length() == 0)
                    btn_send_suggest.setVisibility(View.INVISIBLE);
                else {
                    btn_send_suggest.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_send_suggest.setOnClickListener(v -> {
            Suggestion suggestion = new Suggestion();
            suggestion.setSuggest(Objects.requireNonNull(edt_suggest.getText()).toString());
            if (useUserAccount.getGmail().length() != 0) {
                if (useUserAccount.getName().length() == 0)
                    suggestion.setSuggester(useUserAccount.getCustom_secret_code());
                else
                    suggestion.setSuggester(useUserAccount.getName());
                suggestion.setSuggestion_uid(useUserAccount.getUid());
            } else {
                suggestion.setSuggester("Anonymous");
                suggestion.setSuggestion_uid("Anonymous");
            }
            suggestion.setSuggested_timestamp(DateUtilities.currentTimeStamp());
            String key = FirebaseDatabase.getInstance().getReference(FirebaseContract.Suggestion.SUGGESTION_TABLE).push().getKey();
            assert key != null;
            FirebaseDatabase.getInstance().getReference(FirebaseContract.Suggestion.SUGGESTION_TABLE).child(key).setValue(suggestion).addOnCompleteListener(SendSuggestionActivity.this, task -> {
                if (task.isSuccessful()) {
                    edt_suggest.setText("");
                    btn_send_suggest.setVisibility(View.INVISIBLE);
                    setupSuggestionAdapter();
                }
            });
        });

        setupSuggestionAdapter();
    }

    private void setupSuggestionAdapter() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.Suggestion.SUGGESTION_TABLE);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Suggestion suggestion1 = dataSnapshot.getValue(Suggestion.class);
                        suggestionList.add(suggestion1);
                    }
                    recyclerView_suggestion.setLayoutManager(new SpeedyLinearLayoutManager(SendSuggestionActivity.this, SpeedyLinearLayoutManager.VERTICAL, false));
                    SuggestionRecyclerViewAdapter suggestionRecyclerViewAdapter = new SuggestionRecyclerViewAdapter(SendSuggestionActivity.this, suggestionList);
                    recyclerView_suggestion.setAdapter(suggestionRecyclerViewAdapter);
                    suggestionRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void defineId() {
        edt_suggest = findViewById(R.id.edt_suggest);
        btn_send_suggest = findViewById(R.id.imgBtn_send_suggest);
        recyclerView_suggestion = findViewById(R.id.recyclerView_Suggestion);
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
        Intent intent = new Intent(SendSuggestionActivity.this, NoteViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, R.anim.play_panel_close_background);
        finish();
    }
}
