package team.revolution.fyi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.circularreveal.CircularRevealLinearLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import team.revolution.fyi.R;
import team.revolution.fyi.adapter.CommentRecyclerViewAdapter;
import team.revolution.fyi.adapter.NoteRecyclerViewAdapter;
import team.revolution.fyi.firebase.FirebaseContract;
import team.revolution.fyi.model.Comment;
import team.revolution.fyi.model.Note;
import team.revolution.fyi.model.Request;
import team.revolution.fyi.model.UserAccount;
import team.revolution.fyi.utils.Constant;
import team.revolution.fyi.utils.DateUtilities;
import team.revolution.fyi.utils.DeviceUtils;
import team.revolution.fyi.utils.SpeedyLinearLayoutManager;

public class NoteViewActivity extends AppCompatActivity implements NoteRecyclerViewAdapter.ItemClickListener, CommentRecyclerViewAdapter.ReplyItemClickListener, NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private RecyclerView noteRecyclerView, commentRecyclerView;
    private NoteRecyclerViewAdapter noteRecyclerViewAdapter;
    private List<Note> noteList;
    private DatabaseReference databaseReference;
    private FloatingActionButton fab;
    private SearchView searchView;
    private MaterialTextView txt_title;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CoordinatorLayout coordinator;
    private PopupWindow commentPopupWindow, noteTaskPopupWindow, signInPopupWindow, menuPopupWindow;
    private TinyDB tinyDB;
    private RecyclerView recyclerViewComment;
    private int what = 0;
    private FirebaseDatabase firebaseDatabase;
    private TextInputEditText edt_comment;
    private String comment_reply_to = "";
    private UserAccount useUserAccount;
    private PopupWindow requestPopupWindow;
    private MenuItem menuItem;
    private AppCompatImageButton imageButton_menu;

    public NoteViewActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_view);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("FYI");

        defineId();

        tinyDB = new TinyDB(this);

        if (!tinyDB.getString(Constant.WHEN).equals(Constant.SECOND)) {
            tinyDB.putInt(Constant.FONT_SIZE, 16);
            tinyDB.putString(Constant.THEME, Constant.LIGHT_MODE);
        }
        tinyDB.putString(Constant.WHEN, Constant.SECOND);

        AppCompatAutoCompleteTextView searchAutoCompleteTextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoCompleteTextView.setThreshold(1);

        searchView.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FirebaseCrashlytics.getInstance();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseContract.UserAccount.USERACCOUNT_TABLE);

        databaseReference.keepSynced(true);

        databaseReference.keepSynced(false);

        databaseReference = firebaseDatabase.getReference();

        swipeRefreshLayout.setRefreshing(true);

        setUpNoteRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            setUpNoteRecyclerView();
            if (searchView.isActivated())
                searchView.clearFocus();
        });

        noteRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab.show();
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }
        });

        searchView.setOnClickListener(v -> {
            if (!searchView.isActivated()) {
                searchView.clearFocus();
                setTimerSearchBar();
            }
        });

        imageButton_menu.setOnClickListener(this::menuPopupWindow);

        fab.setOnClickListener(view -> {
            if (firebaseAuth.getCurrentUser() != null && useUserAccount.getWrite_permission().equals(Constant.NOTE)) {
                dismissPopUpWindow();
                Intent intent = new Intent(NoteViewActivity.this, CreateNoteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            } else if (firebaseAuth.getCurrentUser() != null && useUserAccount.getWrite_permission().equals(Constant.COMMENT)) {
                showRequestToWritePopupWindow(view, firebaseAuth.getCurrentUser().getUid());
            } else {
                what = 1;
                showSignInPopupWindow(view);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                swipeRefreshLayout.setRefreshing(true);
                setUpSearchNoteRecyclerView(searchView.getQuery().toString());
                if (searchView.getQuery().toString().length() == 0)
                    setUpNoteRecyclerView();
                return false;
            }
        });
    }

    private void setTimerSearchBar() {
        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (searchView.getQuery().toString().length() == 0) {
                    new CountDownTimer(5000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            if (searchView.isActivated())
                                searchView.clearFocus();
                        }
                    }.start();
                } else
                    setTimerSearchBar();
            }
        }.start();
    }

    private void defineId() {
        noteRecyclerView = findViewById(R.id.recyclerView_Note);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.searchBar);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        coordinator = findViewById(R.id.coordinator);
        imageButton_menu = findViewById(R.id.imgBtn_menu);
    }

    private void setUpNoteRecyclerView() {
        noteList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.UserAccount.USERACCOUNT_TABLE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        UserAccount userAccount = data.getValue(UserAccount.class);
                        assert userAccount != null;
                        if (firebaseAuth.getCurrentUser() != null)
                            if (userAccount.getUid().equals(firebaseAuth.getCurrentUser().getUid())) {
                                useUserAccount = data.getValue(UserAccount.class);
                                assert useUserAccount != null;
                                if (useUserAccount.getRole().equals(Constant.ADMIN))
                                    tinyDB.putBoolean(Constant.ADMIN, true);
                            }
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.UserAccount.USERACCOUNT_TABLE).child(userAccount.getUid()).child(FirebaseContract.Note.NOTE_TABLE);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount() != 0) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Note note = dataSnapshot.getValue(Note.class);
                                        assert note != null;
                                        noteList.add(note);
                                    }
                                    noteRecyclerView.setLayoutManager(new SpeedyLinearLayoutManager(NoteViewActivity.this, SpeedyLinearLayoutManager.VERTICAL, false));
                                    noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(NoteViewActivity.this, noteList, NoteViewActivity.this);
                                    noteRecyclerView.setAdapter(noteRecyclerViewAdapter);
                                    noteRecyclerViewAdapter.notifyDataSetChanged();
                                }
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                } else
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.toString());
            }
        });
    }

    private void setUpSearchNoteRecyclerView(String txtKey) {
        noteList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.UserAccount.USERACCOUNT_TABLE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        UserAccount userAccount = data.getValue(UserAccount.class);
                        assert userAccount != null;
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.UserAccount.USERACCOUNT_TABLE).child(userAccount.getUid()).child(FirebaseContract.Note.NOTE_TABLE);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Note note = dataSnapshot.getValue(Note.class);
                                    assert note != null;
                                    String note_string = dataSnapshot.child(FirebaseContract.Note.NOTE).getValue(String.class);
                                    assert note_string != null;
                                    if (note_string.contains(txtKey) || userAccount.getName().contains(txtKey) || userAccount.getCustom_secret_code().contains(txtKey) || note_string.equalsIgnoreCase(txtKey) || userAccount.getName().equalsIgnoreCase(txtKey) || userAccount.getCustom_secret_code().equalsIgnoreCase(txtKey) || userAccount.getName().equals(txtKey) || userAccount.getCustom_secret_code().equals(txtKey))
                                        noteList.add(note);
                                }
                                noteRecyclerView.setLayoutManager(new SpeedyLinearLayoutManager(NoteViewActivity.this, SpeedyLinearLayoutManager.VERTICAL, false));
                                noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(NoteViewActivity.this, noteList, NoteViewActivity.this);
                                noteRecyclerView.setAdapter(noteRecyclerViewAdapter);
                                noteRecyclerViewAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError.toString());
            }
        });
    }

    private void setUpCommentRecyclerView(String author_uid, String commenter_id, String writer_id, String note_id, String txt_comment, RecyclerView commentRecyclerView, TextInputEditText edt_comment, boolean public_or_private) {
        TinyDB tinyDB = new TinyDB(this);
        List<Comment> commentList;
        commentList = new ArrayList<>();
        String key = FirebaseDatabase.getInstance().getReference(FirebaseContract.UserAccount.USERACCOUNT_TABLE).child(writer_id).child(FirebaseContract.Note.NOTE_TABLE).child(note_id).child(FirebaseContract.Comment.COMMENT_TABLE).push().getKey();
        Comment comment = new Comment();
        comment.setComment(txt_comment);
        comment.setComment_reply_to(comment_reply_to);
        comment.setCommenter_id(commenter_id);
        comment.setComment_uid(key);
        comment.setComment_public(public_or_private);
        String commenter_author_name, commenter_secret_code;
        commenter_author_name = tinyDB.getString(FirebaseContract.UserAccount.AUTHOR_NAME);
        commenter_secret_code = tinyDB.getString(FirebaseContract.UserAccount.CUSTOM_SECRET_CODE);
        if (commenter_author_name.length() != 0)
            comment.setCommenter_name(commenter_author_name);
        else
            comment.setCommenter_name(commenter_secret_code);
        assert key != null;
        FirebaseDatabase.getInstance().getReference(FirebaseContract.UserAccount.USERACCOUNT_TABLE).child(writer_id).child(FirebaseContract.Note.NOTE_TABLE).child(note_id).child(FirebaseContract.Comment.COMMENT_TABLE).child(key).setValue(comment).addOnCompleteListener(NoteViewActivity.this, task -> {
            if (task.isSuccessful()) {
                edt_comment.setText("");
                tinyDB.putString(Constant.COMMENT, "");
                Toast.makeText(this, "Add Comment", Toast.LENGTH_SHORT).show();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.UserAccount.USERACCOUNT_TABLE).child(writer_id).child(FirebaseContract.Note.NOTE_TABLE).child(note_id).child(FirebaseContract.Comment.COMMENT_TABLE);
                reference.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() != 0) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Comment comment = data.getValue(Comment.class);
                                assert comment != null;
                                if (comment.isComment_public())
                                    commentList.add(comment);
                                else if (firebaseAuth.getCurrentUser() != null) {
                                    if (comment.getComment_reply_to().equals(firebaseAuth.getCurrentUser().getUid()) || comment.getCommenter_id().equals(firebaseAuth.getCurrentUser().getUid()) || author_uid.equals(firebaseAuth.getCurrentUser().getUid()))
                                        commentList.add(comment);
                                }
                            }
                            recyclerViewComment.setLayoutManager(new SpeedyLinearLayoutManager(NoteViewActivity.this, SpeedyLinearLayoutManager.VERTICAL, false));
                            CommentRecyclerViewAdapter commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(NoteViewActivity.this, commentList, NoteViewActivity.this);
                            commentRecyclerView.setAdapter(commentRecyclerViewAdapter);
                            recyclerViewComment.scrollToPosition(Objects.requireNonNull(recyclerViewComment.getAdapter()).getItemCount() - 1);
                            commentRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public void showSignInPopupWindow(View view) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        View popupView = getLayoutInflater().inflate(R.layout.signin_popup_layout, null);

        MaterialTextView txtView_title, txtView_about;
        MaterialButton btn_signin;
        AppCompatImageButton img_cancel;
        CircularRevealLinearLayout linearLayout_signup;

        txtView_title = popupView.findViewById(R.id.txtView_title);
        txtView_about = popupView.findViewById(R.id.txtView_about);
        btn_signin = popupView.findViewById(R.id.btn_signin);
        linearLayout_signup = popupView.findViewById(R.id.clrl_signup);
        img_cancel = popupView.findViewById(R.id.img_cancel);

        img_cancel.setOnClickListener(v -> signInPopupWindow.dismiss());

        if (what == 1) {
            txtView_title.setText("Sign in to add Note");
            txtView_about.setText("Create a profile, create your stronger note and write your opinion for other author.");
            btn_signin.setOnClickListener(v -> {
                dismissPopUpWindow();
                tinyDB.putString(Constant.WHERE, Constant.WRITE_NOTE);
                Intent intent = new Intent(NoteViewActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
            linearLayout_signup.setOnClickListener(v -> {
                dismissPopUpWindow();
                tinyDB.putString(Constant.WHERE, Constant.WRITE_NOTE);
                Intent intent = new Intent(NoteViewActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        } else if (what == 2) {
            txtView_title.setText("Sign in to write opinion");
            txtView_about.setText("Create a profile, create your stronger note and write your opinion for other author.");
            btn_signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopUpWindow();
                    tinyDB.putString(Constant.WHERE, Constant.WRITE_COMMENT);
                    Intent intent = new Intent(NoteViewActivity.this, SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
            linearLayout_signup.setOnClickListener(v -> {
                dismissPopUpWindow();
                tinyDB.putString(Constant.WHERE, Constant.WRITE_COMMENT);
                Intent intent = new Intent(NoteViewActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        } else if (what == 3) {
            txtView_title.setText("Sign in to modify Note");
            txtView_about.setText("Create a profile, to save post, etc...");
            btn_signin.setOnClickListener(v -> {
                dismissPopUpWindow();
                tinyDB.putString(Constant.WHERE, Constant.MODIFIED_NOTE);
                Intent intent = new Intent(NoteViewActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
            linearLayout_signup.setOnClickListener(v -> {
                dismissPopUpWindow();
                tinyDB.putString(Constant.WHERE, Constant.MODIFIED_NOTE);
                Intent intent = new Intent(NoteViewActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        signInPopupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        signInPopupWindow.setTouchable(true);
        signInPopupWindow.setOutsideTouchable(true);
        signInPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        signInPopupWindow.setAnimationStyle(R.style.BottomPopupAnimation);
        signInPopupWindow.getContentView().setFocusableInTouchMode(true);
        signInPopupWindow.getContentView().setFocusable(true);
        signInPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (signInPopupWindow != null && signInPopupWindow.isShowing()) {
                        signInPopupWindow.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
        signInPopupWindow.getContentView().setOnDragListener((v, event) -> {
            signInPopupWindow.dismiss();
            return false;
        });
        signInPopupWindow.setAnimationStyle(R.style.BottomPopupAnimation);

        signInPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void showNoteTaskPopupWindow(View view, int position, List<Note> noteList) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        View popupView = getLayoutInflater().inflate(R.layout.note_task_popup_layout, null);
        CircularRevealLinearLayout circularRevealLinearLayout_edit_note, circularRevealLinearLayout_save_note, circularRevealLinearLayout_delete_note;
        circularRevealLinearLayout_edit_note = popupView.findViewById(R.id.clrl_edit_note);
        circularRevealLinearLayout_save_note = popupView.findViewById(R.id.clrl_save_note);
        circularRevealLinearLayout_delete_note = popupView.findViewById(R.id.clrl_delete_note);

        noteTaskPopupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);

        if (firebaseAuth.getCurrentUser() == null) {
            circularRevealLinearLayout_edit_note.setVisibility(View.GONE);
            circularRevealLinearLayout_save_note.setVisibility(View.VISIBLE);
            circularRevealLinearLayout_delete_note.setVisibility(View.GONE);
        } else if (noteList.get(position).getAuthor_uid().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())) {
            circularRevealLinearLayout_edit_note.setVisibility(View.VISIBLE);
            circularRevealLinearLayout_save_note.setVisibility(View.GONE);
            circularRevealLinearLayout_delete_note.setVisibility(View.VISIBLE);
        } else if (!noteList.get(position).getAuthor_uid().equals(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()) && firebaseAuth.getCurrentUser() != null) {
            circularRevealLinearLayout_edit_note.setVisibility(View.GONE);
            circularRevealLinearLayout_save_note.setVisibility(View.VISIBLE);
            circularRevealLinearLayout_delete_note.setVisibility(View.GONE);
        }

        circularRevealLinearLayout_save_note.setOnClickListener(v -> {
            if (firebaseAuth.getCurrentUser() == null) {
                what = 3;
                showSignInPopupWindow(view);
            } else {

            }
        });

        circularRevealLinearLayout_edit_note.setOnClickListener(v -> {
            dismissPopUpWindow();
            Intent intent = new Intent(NoteViewActivity.this, CreateNoteActivity.class);
            tinyDB.putString(Constant.NOTE_EDIT, noteList.get(position).getNote());
            tinyDB.putString(Constant.NOTE_UID, noteList.get(position).getNote_uid());
            intent.putExtra(Constant.WHERE, 7);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, R.anim.play_panel_close_background);
            finish();
        });

        circularRevealLinearLayout_delete_note.setOnClickListener(v -> {
            circularRevealLinearLayout_delete_note.setEnabled(false);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseContract.UserAccount.USERACCOUNT_TABLE)
                    .child(noteList.get(position).getAuthor_uid())
                    .child(FirebaseContract.Note.NOTE_TABLE);

            reference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() != 0) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Note note = dataSnapshot.getValue(Note.class);
                            assert note != null;
                            if (note.getNote_uid().equals(noteList.get(position).getNote_uid())) {
                                dataSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                                    if (task.isSuccessful())
                                        circularRevealLinearLayout_delete_note.setEnabled(true);
                                });
                            } else
                                noteList.add(note);
                        }
                        SpeedyLinearLayoutManager speedyLinearLayoutManager = new SpeedyLinearLayoutManager(NoteViewActivity.this, SpeedyLinearLayoutManager.VERTICAL, false);
                        speedyLinearLayoutManager.setReverseLayout(true);
                        noteRecyclerView.setLayoutManager(speedyLinearLayoutManager);
                        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(NoteViewActivity.this, noteList, NoteViewActivity.this);
                        noteRecyclerView.setAdapter(noteRecyclerViewAdapter);
                        noteRecyclerViewAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        noteTaskPopupWindow.setTouchable(true);
        noteTaskPopupWindow.setOutsideTouchable(true);
        noteTaskPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        noteTaskPopupWindow.setAnimationStyle(R.style.BottomPopupAnimation);
        noteTaskPopupWindow.getContentView().setFocusableInTouchMode(true);
        noteTaskPopupWindow.getContentView().setFocusable(true);
        noteTaskPopupWindow.getContentView().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (noteTaskPopupWindow != null && noteTaskPopupWindow.isShowing()) {
                    noteTaskPopupWindow.dismiss();
                }
                return true;
            }
            return false;
        });
        noteTaskPopupWindow.getContentView().setOnDragListener((v, event) -> {
            noteTaskPopupWindow.dismiss();
            return true;
        });
        noteTaskPopupWindow.setAnimationStyle(R.style.BottomPopupAnimation);

        noteTaskPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void showCommentPopupWindow(View view, String note_uid, String author_uid) {
        List<Comment> commentList = new ArrayList<>();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        View popupView = getLayoutInflater().inflate(R.layout.comment_popup_layout, null);

        SwitchMaterial switchMaterial = popupView.findViewById(R.id.switch_comment_public_or_private);
        MaterialTextView txtView_comment_public_or_public = popupView.findViewById(R.id.txtView_comment_public_or_public);
        recyclerViewComment = popupView.findViewById(R.id.recyclerView_Comment);
        edt_comment = popupView.findViewById(R.id.edt_comment);
        AppCompatImageButton imgBtn_send_comment = popupView.findViewById(R.id.imgBtn_send_comment);

        commentPopupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);

        if (firebaseAuth.getCurrentUser() == null)
            edt_comment.setOnClickListener(v -> {
                if (firebaseAuth.getCurrentUser() == null) {
                    what = 2;
                    commentPopupWindow.dismiss();
                    showSignInPopupWindow(view);
                }
            });
        else {
            edt_comment.setClickable(false);
            DeviceUtils.showKeyboard(this);
            edt_comment.requestFocus();
        }

        TinyDB tinyDB = new TinyDB(this);

        edt_comment.setText(tinyDB.getString(Constant.COMMENT));

        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    txtView_comment_public_or_public.setText("Public");
                else
                    txtView_comment_public_or_public.setText("Private");
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseContract.UserAccount.USERACCOUNT_TABLE)
                .child(author_uid)
                .child(FirebaseContract.Note.NOTE_TABLE)
                .child(note_uid)
                .child(FirebaseContract.Comment.COMMENT_TABLE);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Comment comment = data.getValue(Comment.class);
                        assert comment != null;
                        if (comment.isComment_public())
                            commentList.add(comment);
                        else if (firebaseAuth.getCurrentUser() != null) {
                            if (comment.getComment_reply_to().equals(firebaseAuth.getCurrentUser().getUid()) || comment.getCommenter_id().equals(firebaseAuth.getCurrentUser().getUid()) || author_uid.equals(firebaseAuth.getCurrentUser().getUid()))
                                commentList.add(comment);
                        }
                    }
                    recyclerViewComment.setLayoutManager(new SpeedyLinearLayoutManager(NoteViewActivity.this, SpeedyLinearLayoutManager.VERTICAL, false));
                    CommentRecyclerViewAdapter commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(NoteViewActivity.this, commentList, NoteViewActivity.this);
                    recyclerViewComment.setAdapter(commentRecyclerViewAdapter);
                    recyclerViewComment.scrollToPosition(Objects.requireNonNull(recyclerViewComment.getAdapter()).getItemCount() - 1);
                    commentRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (firebaseAuth.getCurrentUser() == null) {
            edt_comment.setCursorVisible(false);
            edt_comment.setFocusable(false);
            edt_comment.setFocusableInTouchMode(false);
            edt_comment.setClickable(false);
        }

        imgBtn_send_comment.setOnClickListener(v -> {
            setUpCommentRecyclerView(author_uid, Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid(), author_uid, note_uid, Objects.requireNonNull(edt_comment.getText()).toString(), recyclerViewComment, edt_comment, switchMaterial.isChecked());
        });

        edt_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(edt_comment.getText()).length() == 0)
                    imgBtn_send_comment.setVisibility(View.INVISIBLE);
                else {
                    imgBtn_send_comment.setVisibility(View.VISIBLE);
                    tinyDB.putString(Constant.COMMENT, Objects.requireNonNull(edt_comment.getText()).toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        commentPopupWindow.setTouchable(true);
        commentPopupWindow.setOutsideTouchable(true);
        commentPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        commentPopupWindow.setAnimationStyle(R.style.BottomPopupAnimation);
        commentPopupWindow.getContentView().setFocusableInTouchMode(true);
        commentPopupWindow.getContentView().setFocusable(true);
        commentPopupWindow.getContentView().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (commentPopupWindow != null && commentPopupWindow.isShowing()) {
                    commentPopupWindow.dismiss();
                }
                return true;
            }
            return false;
        });
        commentPopupWindow.getContentView().setOnDragListener((v, event) -> {
            commentPopupWindow.dismiss();
            return false;
        });
        commentPopupWindow.setAnimationStyle(R.style.BottomPopupAnimation);

        commentPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void showRequestToWritePopupWindow(View view, String user_uid) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        View popupView = getLayoutInflater().inflate(R.layout.request_to_write_popup_layout, null);

        requestPopupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);

        AppCompatImageButton btn_cancel = popupView.findViewById(R.id.btn_cancel);
        MaterialButton btn_request = popupView.findViewById(R.id.btn_request_to_write);

        btn_cancel.setOnClickListener(v -> requestPopupWindow.dismiss());

        if (tinyDB.getBoolean(Constant.PENDING)) {
            btn_request.setEnabled(false);
            btn_request.setText("Your request are pending!!!");
        }

        btn_request.setOnClickListener(v -> {
            Request request = new Request();
            request.setRequest_user_uid(user_uid);
            request.setRequest_timestamp(DateUtilities.currentTimeStamp());
            String key = FirebaseDatabase.getInstance().getReference(FirebaseContract.Request.REQUEST_TABLE).push().getKey();
            assert key != null;
            FirebaseDatabase.getInstance().getReference(FirebaseContract.Request.REQUEST_TABLE).child(key).setValue(request).addOnCompleteListener(NoteViewActivity.this, task -> {
                if (task.isSuccessful()) {
                    tinyDB.putBoolean(Constant.PENDING, true);
                    btn_request.setText("Your request are pending!!!");
                    btn_request.setEnabled(false);
                }
            });
        });
        requestPopupWindow.setTouchable(true);
        requestPopupWindow.setOutsideTouchable(true);
        requestPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        requestPopupWindow.setAnimationStyle(R.style.BottomPopupAnimation);
        requestPopupWindow.getContentView().setFocusableInTouchMode(true);
        requestPopupWindow.getContentView().setFocusable(true);
        requestPopupWindow.getContentView().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (requestPopupWindow != null && requestPopupWindow.isShowing()) {
                    requestPopupWindow.dismiss();
                }
                return true;
            }
            return false;
        });
        requestPopupWindow.getContentView().setOnDragListener((v, event) -> {
            requestPopupWindow.dismiss();
            return true;
        });
        requestPopupWindow.setAnimationStyle(R.style.BottomPopupAnimation);

        requestPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void menuPopupWindow(View view) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.menu_popup_layout, null);

        menuPopupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT, true);

        if (tinyDB.getBoolean(Constant.ADMIN))
            popupView.findViewById(R.id.action_request_list).setVisibility(View.VISIBLE);
        else
            popupView.findViewById(R.id.action_request_list).setVisibility(View.GONE);

        if (firebaseAuth.getCurrentUser() != null) {
            popupView.findViewById(R.id.action_login).setVisibility(View.GONE);
            popupView.findViewById(R.id.action_add_note).setPadding(30,30,0,0);
        } else {
            popupView.findViewById(R.id.action_login).setVisibility(View.VISIBLE);
        }

        popupView.findViewById(R.id.action_settings).setOnClickListener(v -> {
            dismissPopUpWindow();
            Intent intent = new Intent(NoteViewActivity.this, SettingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        popupView.findViewById(R.id.action_login).setOnClickListener(v -> {
            if (firebaseAuth.getCurrentUser() != null) {
                dismissPopUpWindow();
                Intent intent = new Intent(NoteViewActivity.this, CreateNoteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                dismissPopUpWindow();
                tinyDB.putString(Constant.WHERE, Constant.ONLY_LOGIN);
                Intent intent = new Intent(NoteViewActivity.this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            overridePendingTransition(0, 0);
            finish();
        });

        popupView.findViewById(R.id.action_send_suggestion).setOnClickListener(v -> {
            dismissPopUpWindow();
            Intent intent = new Intent(NoteViewActivity.this, SendSuggestionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        popupView.findViewById(R.id.action_logout).setOnClickListener(v -> {
            if (firebaseAuth.getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                popupView.findViewById(R.id.action_request_list).setVisibility(View.GONE);
                popupView.findViewById(R.id.action_login).setVisibility(View.VISIBLE);
                popupView.findViewById(R.id.action_add_note).setPadding(30,0,0,0);
                tinyDB.putBoolean(Constant.PENDING, false);
                tinyDB.putBoolean(Constant.ADMIN, false);
            }
        });

        popupView.findViewById(R.id.action_about).setOnClickListener(v -> {
            dismissPopUpWindow();
            Intent intent = new Intent(NoteViewActivity.this, AboutActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        popupView.findViewById(R.id.action_request_list).setOnClickListener(v -> {
            dismissPopUpWindow();
            Intent intent = new Intent(NoteViewActivity.this, RequestListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        popupView.findViewById(R.id.action_add_note).setOnClickListener(v -> {
            dismissPopUpWindow();
            if (firebaseAuth.getCurrentUser() != null && useUserAccount.getWrite_permission().equals(Constant.NOTE)) {
                dismissPopUpWindow();
                Intent intent = new Intent(NoteViewActivity.this, CreateNoteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            } else if (firebaseAuth.getCurrentUser() != null && useUserAccount.getWrite_permission().equals(Constant.COMMENT)) {
                showRequestToWritePopupWindow(view, firebaseAuth.getCurrentUser().getUid());
            } else {
                what = 1;
                showSignInPopupWindow(view);
            }
        });

        menuPopupWindow.setTouchable(true);
        menuPopupWindow.setOutsideTouchable(true);
        menuPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        menuPopupWindow.setAnimationStyle(R.style.RightPopupAnimation);
        menuPopupWindow.getContentView().setFocusableInTouchMode(true);
        menuPopupWindow.getContentView().setFocusable(true);
        menuPopupWindow.getContentView().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (menuPopupWindow != null && menuPopupWindow.isShowing()) {
                    menuPopupWindow.dismiss();
                }
                return true;
            }
            return false;
        });
        menuPopupWindow.getContentView().setOnDragListener((v, event) -> {
            menuPopupWindow.dismiss();
            return true;
        });
        menuPopupWindow.setAnimationStyle(R.style.RightPopupAnimation);

        menuPopupWindow.showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

    @Override
    public void onClickMenuFromAdapter(View view, int position, List<Note> noteList) {
        showNoteTaskPopupWindow(view, position, noteList);
    }

    @Override
    public void onClickCommentFromAdapter(View view, int position, List<Note> noteList) {
        showCommentPopupWindow(view, noteList.get(position).getNote_uid(), noteList.get(position).getAuthor_uid());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClickReplyFromAdapter(View v, int position, List<Comment> commentList) {
        if (firebaseAuth.getCurrentUser() != null) {
            edt_comment.setText(commentList.get(position).getCommenter_name() + " ");
            edt_comment.setSelection(Objects.requireNonNull(edt_comment.getText()).length());
            edt_comment.requestFocus();
            comment_reply_to = commentList.get(position).getCommenter_id();
        } else {
            commentPopupWindow.dismiss();
            what = 2;
            showSignInPopupWindow(v);
        }
    }

    @Override
    protected void onPause() {
        dismissPopUpWindow();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        dismissPopUpWindow();
        super.onDestroy();
    }

    private void dismissPopUpWindow() {
        if (commentPopupWindow != null)
            commentPopupWindow.dismiss();
        if (noteTaskPopupWindow != null)
            noteTaskPopupWindow.dismiss();
        if (signInPopupWindow != null)
            signInPopupWindow.dismiss();
        if (requestPopupWindow != null)
            requestPopupWindow.dismiss();
        if (menuPopupWindow != null)
            menuPopupWindow.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (commentPopupWindow != null) {
            tinyDB.putString(Constant.COMMENT, Objects.requireNonNull(edt_comment.getText()).toString() + " ");
            commentPopupWindow.dismiss();
        }
        super.onBackPressed();
    }
}