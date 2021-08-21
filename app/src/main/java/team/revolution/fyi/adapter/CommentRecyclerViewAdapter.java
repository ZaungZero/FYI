package team.revolution.fyi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mukesh.tinydb.TinyDB;

import java.util.List;

import team.revolution.fyi.R;
import team.revolution.fyi.model.Comment;
import team.revolution.fyi.model.Note;
import team.revolution.fyi.utils.Constant;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    private final List<Comment> commentList;
    private final LayoutInflater mInflater;
    private Context context;
    private ReplyItemClickListener mClickListener;
    private TinyDB tinyDB;

    public CommentRecyclerViewAdapter(Context context, List<Comment> data, ReplyItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.commentList = data;
        this.mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CommentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comment_list, parent, false);
        return new CommentRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentRecyclerViewAdapter.ViewHolder holder, int position) {
        tinyDB = new TinyDB(context);
        Comment comment = commentList.get(position);
        holder.txtComment.setText(comment.getComment());
        holder.txtComment.setTextSize(tinyDB.getInt(Constant.FONT_SIZE));
        holder.txtCommenter.setText(comment.getCommenter_name());
        holder.txtReply.setOnClickListener(v -> {
            if (mClickListener != null)
                mClickListener.onClickReplyFromAdapter(v, position, commentList);
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView txtComment, txtCommenter, txtReply;

        ViewHolder(View itemView) {
            super(itemView);
            txtComment = itemView.findViewById(R.id.txt_comment);
            txtCommenter = itemView.findViewById(R.id.txt_commenter);
            txtReply = itemView.findViewById(R.id.txt_reply);
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

    public Comment getItem(int id) {
        return commentList.get(id);
    }

    void setClickListener(ReplyItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ReplyItemClickListener {
        void onClickReplyFromAdapter(View v, int position, List<Comment> commentList);
    }

    public void removeAt(int position) {
        commentList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, commentList.size());
    }
}
