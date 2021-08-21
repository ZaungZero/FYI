package team.revolution.fyi.model;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class UserAccount {
    private int id = 0;
    private String uid = "";
    private List<Note> noteList;
    private String name = "";
    private String custom_secret_code = "";
    private String gmail = "";
    private String password = "";
    private String role = "";
    private String write_permission = "";
    private Long sign_up_timestamp;

    public UserAccount() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustom_secret_code() {
        return custom_secret_code;
    }

    public void setCustom_secret_code(String custom_secret_code) {
        this.custom_secret_code = custom_secret_code;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getWrite_permission() {
        return write_permission;
    }

    public void setWrite_permission(String write_permission) {
        this.write_permission = write_permission;
    }

    public Long getSign_up_timestamp() {
        return sign_up_timestamp;
    }

    public void setSign_up_timestamp(Long sign_up_timestamp) {
        this.sign_up_timestamp = sign_up_timestamp;
    }

}
