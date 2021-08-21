package team.revolution.fyi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.mukesh.tinydb.TinyDB;

import java.util.List;

import team.revolution.fyi.R;
import team.revolution.fyi.model.Note;
import team.revolution.fyi.model.UserAccount;
import team.revolution.fyi.utils.DateUtilities;

public class RequestRecyclerViewAdapter extends RecyclerView.Adapter<RequestRecyclerViewAdapter.ViewHolder> {
    private final List<UserAccount> requestList;
    private final LayoutInflater mInflater;
    private Context context;
    private RequestItemClickListener mClickListener;
    private TinyDB tinyDB;

    public RequestRecyclerViewAdapter(Context context, List<UserAccount> data, RequestItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.requestList = data;
        this.mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RequestRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.request_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        tinyDB = new TinyDB(context);
        UserAccount userAccount = requestList.get(position);
        if (userAccount.getName().length() == 0)
            holder.txtName.setText(userAccount.getCustom_secret_code());
        else
            holder.txtName.setText(userAccount.getName());
        holder.txtCustom_code.setText(userAccount.getCustom_secret_code());
        holder.txtEmail.setText(userAccount.getGmail());
        holder.txtRequest_date.setText(DateUtilities.timeStampToDate(userAccount.getSign_up_timestamp()));
        holder.btnAccept.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClickRequestFromAdapter(v, position, requestList,holder.btnAccept);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView txtName, txtCustom_code, txtRequest_date, txtEmail;
        MaterialButton btnAccept;

        ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name);
            txtCustom_code = itemView.findViewById(R.id.txt_custom_code);
            txtRequest_date = itemView.findViewById(R.id.txt_request_time);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            txtEmail = itemView.findViewById(R.id.txt_email);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public void removeItem(int position, List<Note> noteList) {
        noteList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, noteList.size());
    }

    public UserAccount getItem(int id) {
        return requestList.get(id);
    }

    void setClickListener(RequestItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface RequestItemClickListener {
        void onClickRequestFromAdapter(View view, int position, List<UserAccount> RequestList,MaterialButton materialButton);
    }

    public void removeAt(int position) {
        requestList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, requestList.size());
    }
}
