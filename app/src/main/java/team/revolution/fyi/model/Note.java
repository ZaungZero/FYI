package team.revolution.fyi.model;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class Note {
    private int id = 0;
    private String note_uid = "";
    private String note = "";
    private String author_name = "";
    private String author_uid = "";
    private String author_custom_secret_code = "";
    private List<Comment> commentList;
    private Long note_time_stamp;
    private boolean note_public = false;
    private boolean note_pinned = false;

    public Note() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote_uid() {
        return note_uid;
    }

    public void setNote_uid(String note_uid) {
        this.note_uid = note_uid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_uid() {
        return author_uid;
    }

    public void setAuthor_uid(String author_uid) {
        this.author_uid = author_uid;
    }

    public String getAuthor_custom_secret_code() {
        return author_custom_secret_code;
    }

    public void setAuthor_custom_secret_code(String author_custom_secret_code) {
        this.author_custom_secret_code = author_custom_secret_code;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public Long getNote_time_stamp() {
        return note_time_stamp;
    }

    public void setNote_time_stamp(Long note_time_stamp) {
        this.note_time_stamp = note_time_stamp;
    }

    public boolean isNote_public() {
        return note_public;
    }

    public void setNote_public(boolean note_public) {
        this.note_public = note_public;
    }

    public boolean isNote_pinned() {
        return note_pinned;
    }

    public void setNote_pinned(boolean note_pinned) {
        this.note_pinned = note_pinned;
    }
}
