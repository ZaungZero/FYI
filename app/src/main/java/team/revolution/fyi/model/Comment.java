package team.revolution.fyi.model;

import androidx.annotation.Keep;

@Keep
public class Comment {
    private int id = 0;
    private String comment_uid = "";
    private String comment = "";
    private String commenter_id = "";
    private String commenter_name = "";
    private boolean comment_public = false;
    private String comment_reply_to = "";
    private String count = "";

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment_uid() {
        return comment_uid;
    }

    public void setComment_uid(String comment_uid) {
        this.comment_uid = comment_uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommenter_id() {
        return commenter_id;
    }

    public void setCommenter_id(String commenter_id) {
        this.commenter_id = commenter_id;
    }

    public String getCommenter_name() {
        return commenter_name;
    }

    public void setCommenter_name(String commenter_name) {
        this.commenter_name = commenter_name;
    }

    public boolean isComment_public() {
        return comment_public;
    }

    public void setComment_public(boolean comment_public) {
        this.comment_public = comment_public;
    }

    public String getComment_reply_to() {
        return comment_reply_to;
    }

    public void setComment_reply_to(String comment_reply_to) {
        this.comment_reply_to = comment_reply_to;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
