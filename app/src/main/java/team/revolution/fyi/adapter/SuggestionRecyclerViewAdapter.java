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
import team.revolution.fyi.model.Suggestion;
import team.revolution.fyi.model.UserAccount;
import team.revolution.fyi.utils.DateUtilities;

public class SuggestionRecyclerViewAdapter extends RecyclerView.Adapter<SuggestionRecyclerViewAdapter.ViewHolder> {
    private final List<Suggestion> suggestionList;
    private final LayoutInflater mInflater;
    private Context context;
    private RequestItemClickListener mClickListener;
    private TinyDB tinyDB;

    public SuggestionRecyclerViewAdapter(Context context, List<Suggestion> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.suggestionList = data;
    }

    @NonNull
    @Override
    public SuggestionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.suggestion_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SuggestionRecyclerViewAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        tinyDB = new TinyDB(context);
        Suggestion suggestion = suggestionList.get(position);
        holder.txtSuggester.setText(suggestion.getSuggester());
        holder.txtSuggestionDate.setText(DateUtilities.timeStampToDate(suggestion.getSuggested_timestamp()));
        holder.txtSuggestedText.setText(suggestion.getSuggest());
    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MaterialTextView txtSuggester, txtSuggestionDate, txtSuggestedText;
        MaterialButton btnAccept;

        ViewHolder(View itemView) {
            super(itemView);
            txtSuggester = itemView.findViewById(R.id.txt_suggester);
            txtSuggestionDate = itemView.findViewById(R.id.txt_suggestion_date);
            txtSuggestedText = itemView.findViewById(R.id.txt_suggested_text);
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

    public Suggestion getItem(int id) {
        return suggestionList.get(id);
    }

    void setClickListener(RequestItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface RequestItemClickListener {
        void onClickRequestFromAdapter(View view, int position, List<UserAccount> RequestList, MaterialButton materialButton);
    }

    public void removeAt(int position) {
        suggestionList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, suggestionList.size());
    }
}
