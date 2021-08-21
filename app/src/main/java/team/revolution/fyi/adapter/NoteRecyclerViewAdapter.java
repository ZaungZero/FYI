package team.revolution.fyi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.circularreveal.CircularRevealRelativeLayout;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.mukesh.tinydb.TinyDB;

import java.util.List;

import team.revolution.fyi.R;
import team.revolution.fyi.model.Note;
import team.revolution.fyi.utils.Constant;
import team.revolution.fyi.utils.DateUtilities;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> {

    private final List<Note> noteList;
    private final LayoutInflater mInflater;
    private Context context;
    private ItemClickListener mClickListener;
    private TinyDB tinyDB;

    public NoteRecyclerViewAdapter(Context context, List<Note> data, ItemClickListener itemClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.noteList = data;
        this.mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.note_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tinyDB = new TinyDB(context);
        Note note = noteList.get(position);
        if (note.getAuthor_name().length() == 0)
            holder.txtAuthorName.setText(note.getAuthor_custom_secret_code());
        else
            holder.txtAuthorName.setText(note.getAuthor_name());
        holder.txtTime.setText(DateUtilities.timeStampToDateTime(note.getNote_time_stamp()));
        holder.txtLimitNote.setTextSize(tinyDB.getInt(Constant.FONT_SIZE));
        holder.txtFullNote.setTextSize(tinyDB.getInt(Constant.FONT_SIZE));
        holder.continue_reading_txtView.setTextSize(tinyDB.getInt(Constant.FONT_SIZE));
        holder.txtLimitNote.setText(note.getNote());
        holder.txtFullNote.setText(note.getNote());
        holder.menuAppCompatImageButton.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClickMenuFromAdapter(v, position, noteList);
            }
        });
        holder.commentCircularRevealRelativeLayout.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClickCommentFromAdapter(v, position, noteList);
            }
        });
        if (holder.txtFullNote.getLineCount() > 9) {
            holder.continue_reading_txtView.setVisibility(View.VISIBLE);
        } else
            holder.continue_reading_txtView.setVisibility(View.GONE);
        if (tinyDB.getBoolean(note.getNote_uid())) {
            holder.txtLimitNote.setVisibility(View.GONE);
            holder.txtFullNote.setVisibility(View.VISIBLE);
            holder.continue_reading_txtView.setVisibility(View.GONE);
        } else {
            holder.txtFullNote.setVisibility(View.GONE);
            holder.txtLimitNote.setVisibility(View.VISIBLE);
            holder.continue_reading_txtView.setVisibility(View.VISIBLE);
        }
        holder.txtLimitNote.setOnClickListener(v -> {
            tinyDB.putBoolean(note.getNote_uid(), true);
            holder.txtLimitNote.setVisibility(View.GONE);
            holder.txtFullNote.setVisibility(View.VISIBLE);
            holder.continue_reading_txtView.setVisibility(View.GONE);
        });
        holder.txtFullNote.setOnClickListener(v -> {
            tinyDB.putBoolean(note.getNote_uid(), false);
            holder.txtFullNote.setVisibility(View.GONE);
            holder.txtLimitNote.setVisibility(View.VISIBLE);
            holder.continue_reading_txtView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView txtAuthorName, txtTime, txtLimitNote, txtFullNote, continue_reading_txtView;
        MaterialCardView noteMaterialCardView;
        CircularRevealRelativeLayout commentCircularRevealRelativeLayout, menuCircularRevealRelativeLayout;
        AppCompatImageButton menuAppCompatImageButton;
        ShapeableImageView imgView_comment, imgView_comment_dark;

        ViewHolder(View itemView) {
            super(itemView);
            txtAuthorName = itemView.findViewById(R.id.txtView_authorName);
            txtTime = itemView.findViewById(R.id.txtView_time);
            noteMaterialCardView = itemView.findViewById(R.id.note_cardView);
            txtLimitNote = itemView.findViewById(R.id.txtView_note_limit);
            txtFullNote = itemView.findViewById(R.id.txtView_note_full);
            menuAppCompatImageButton = itemView.findViewById(R.id.imgView_menu);
            menuCircularRevealRelativeLayout = itemView.findViewById(R.id.layout_menu);
            commentCircularRevealRelativeLayout = itemView.findViewById(R.id.layout_comment);
            continue_reading_txtView = itemView.findViewById(R.id.txtView_continue_reading);
            imgView_comment = itemView.findViewById(R.id.imgView_comment);
            imgView_comment_dark = itemView.findViewById(R.id.imgView_comment_dark);
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

    public Note getItem(int id) {
        return noteList.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onClickMenuFromAdapter(View view, int position, List<Note> noteList);

        void onClickCommentFromAdapter(View view, int position, List<Note> noteList);
    }

    public void removeAt(int position) {
        noteList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, noteList.size());
    }
}