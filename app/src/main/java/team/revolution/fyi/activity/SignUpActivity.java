package team.revolution.fyi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private MaterialTextView register_txtView;
    private ShapeableImageView login_and_register_imgView;
    private TextInputLayout custom_code_txt_inputLayout, name_txt_inputLayout;
    private TextInputEditText custom_code_edt, name_edt, gmail_edt, password_edt;
    private MaterialButton login_or_register_btn;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private ProgressDialog progress;
    private int size = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_signup);

        defineId();

        register_txtView.setPaintFlags(register_txtView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child(FirebaseContract.UserAccount.USERACCOUNT_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                size = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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
    }

    private void onClick() {
        register_txtView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, R.anim.play_panel_close_background);
            finish();
        });

        login_or_register_btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(custom_code_edt.getText()))
                custom_code_edt.setError("Enter your desire custom code!");
            else if (TextUtils.isEmpty(gmail_edt.getText()))
                gmail_edt.setError("Enter your email!");
            else if (!Condition.isValidEmail(Objects.requireNonNull(gmail_edt.getText()).toString()))
                gmail_edt.setError("Your email is not valid!");
            else if (TextUtils.isEmpty(password_edt.getText()))
                password_edt.setError("Enter password!");
            else if (!Condition.isValidPassword(Objects.requireNonNull(password_edt.getText()).toString()))
                password_edt.setError("Password is contain a digit must occur at least once, a lower case letter must occur at least once, an upper case letter must occur at least once, a special character must occur at least once you can replace with your special characters, at least six places!");
            else {
                progress = new ProgressDialog(this);
                progress.setTitle("Loading");
                progress.setMessage("Please wait while sign up...");
                progress.setCancelable(false);
                progress.show();
                login_or_register_btn.setEnabled(false);
                mAuth.createUserWithEmailAndPassword(gmail_edt.getText().toString(), password_edt.getText().toString())
                        .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show())
                        .addOnCompleteListener(SignUpActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                try {
                                    if (user != null)
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        registerUserAccount(user);
                                                    }
                                                });

                                } catch (Exception e) {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                }
                            }
                        });
            }
        });
    }

    private void registerUserAccount(FirebaseUser user) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUid(user.getUid());
        userAccount.setId(size + 1);
        if (size == 0) {
            userAccount.setRole(Constant.ADMIN);
            userAccount.setWrite_permission(Constant.NOTE);
        } else {
            userAccount.setRole(Constant.VIEWER);
            userAccount.setWrite_permission(Constant.COMMENT);
        }
        userAccount.setCustom_secret_code(Objects.requireNonNull(custom_code_edt.getText()).toString());
        userAccount.setName(Objects.requireNonNull(name_edt.getText()).toString() + "");
        userAccount.setGmail(Objects.requireNonNull(gmail_edt.getText()).toString());
        userAccount.setPassword(Objects.requireNonNull(password_edt.getText()).toString());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.child(FirebaseContract.UserAccount.USERACCOUNT_TABLE).child(user.getUid()).setValue(userAccount).addOnCompleteListener(SignUpActivity.this, task -> {
            if (task.isSuccessful()) {
                progress.dismiss();
                login_or_register_btn.setEnabled(true);
                TinyDB tinyDB = new TinyDB(SignUpActivity.this);
                tinyDB.putString(FirebaseContract.UserAccount.CUSTOM_SECRET_CODE, custom_code_edt.getText().toString());
                tinyDB.putString(FirebaseContract.UserAccount.AUTHOR_NAME, name_edt.getText().toString() + "");
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
                alertDialogBuilder.setTitle("Success");
                alertDialogBuilder
                        .setMessage("A verification email is sent to your registered email, please click on the link and sign in again!")
                        .setCancelable(false)
                        .setPositiveButton("Sign In", (dialog, id) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent signInIntent = new Intent(SignUpActivity.this, SignInActivity.class);
                            SignUpActivity.this.finish();
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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
        Intent intent = new Intent(SignUpActivity.this, NoteViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }
}
