package team.revolution.fyi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import team.revolution.fyi.R;
import team.revolution.fyi.adapter.RequestRecyclerViewAdapter;
import team.revolution.fyi.firebase.FirebaseContract;
import team.revolution.fyi.model.Request;
import team.revolution.fyi.model.UserAccount;
import team.revolution.fyi.utils.Constant;
import team.revolution.fyi.utils.SpeedyLinearLayoutManager;

public class RequestListActivity extends AppCompatActivity implements RequestRecyclerViewAdapter.RequestItemClickListener {
    private List<Request> requestList;
    private List<UserAccount> userAccountList;
    private List<UserAccount> userAccountListForAdapter;
    private RecyclerView recyclerView_request;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        setTitle("Request List");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        defineId();

        userAccountList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.UserAccount.USERACCOUNT_TABLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                        userAccountList.add(userAccount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userAccountListForAdapter = new ArrayList<>();
        requestList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.Request.REQUEST_TABLE);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Request request = dataSnapshot.getValue(Request.class);
                        requestList.add(request);
                    }
                    for (Request r : requestList) {
                        for (UserAccount ua : userAccountList) {
                            if (ua.getUid().equals(r.getRequest_user_uid())) {
                                ua.setSign_up_timestamp(r.getRequest_timestamp());
                                userAccountListForAdapter.add(ua);
                            }
                        }
                    }
                    recyclerView_request.setLayoutManager(new SpeedyLinearLayoutManager(RequestListActivity.this, SpeedyLinearLayoutManager.VERTICAL, false));
                    RequestRecyclerViewAdapter requestRecyclerViewAdapter = new RequestRecyclerViewAdapter(RequestListActivity.this, userAccountListForAdapter, RequestListActivity.this);
                    recyclerView_request.setAdapter(requestRecyclerViewAdapter);
                    requestRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void defineId() {
        recyclerView_request = findViewById(R.id.recyclerView_request);
    }

    @Override
    public void onClickRequestFromAdapter(View view, int position, List<UserAccount> userRequestList, MaterialButton materialButton) {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.UserAccount.USERACCOUNT_TABLE);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount() != 0) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                            assert userAccount != null;
                            if (userAccount.getUid().equals(userRequestList.get(position).getUid())) {
                                Map<String, Object> hashMap = new HashMap<>();
                                hashMap.put(FirebaseContract.UserAccount.ROLE, Constant.MODERATOR);
                                hashMap.put(FirebaseContract.UserAccount.WRITE_PERMISSION, Constant.NOTE);
                                dataSnapshot.getRef().updateChildren(hashMap).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseContract.Request.REQUEST_TABLE);
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @SuppressLint("NotifyDataSetChanged")
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                if (snapshot1.getChildrenCount() != 0) {
                                                    for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                                        Request request = dataSnapshot1.getValue(Request.class);
                                                        assert request != null;
                                                        if (request.getRequest_user_uid().equals(userRequestList.get(position).getUid())) {
                                                            dataSnapshot1.getRef().removeValue();
                                                            materialButton.setText(R.string.accepted);
                                                            materialButton.setEnabled(false);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    }
                                });
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Log.e("RequestListActivity", e.getMessage());
        }
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
        Intent intent = new Intent(RequestListActivity.this, NoteViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, R.anim.play_panel_close_background);
        finish();
    }
}
