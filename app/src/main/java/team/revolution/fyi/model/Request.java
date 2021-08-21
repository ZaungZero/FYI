package team.revolution.fyi.model;

import androidx.annotation.Keep;

@Keep
public class Request {
    private String request_user_uid = "";
    private Long request_timestamp;

    public Request() {
    }

    public String getRequest_user_uid() {
        return request_user_uid;
    }

    public void setRequest_user_uid(String request_user_uid) {
        this.request_user_uid = request_user_uid;
    }

    public Long getRequest_timestamp() {
        return request_timestamp;
    }

    public void setRequest_timestamp(Long request_timestamp) {
        this.request_timestamp = request_timestamp;
    }
}
