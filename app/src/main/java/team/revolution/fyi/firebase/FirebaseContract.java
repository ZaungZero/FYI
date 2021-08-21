package team.revolution.fyi.firebase;

public class FirebaseContract {
    public abstract class UserAccount {
        public static final String USERACCOUNT_TABLE = "UserAccount";
        public static final String USERACCOUNT_ID = "useraccount_id";
        public static final String USERACCOUNT_UID = "useraccount_uid";
        public static final String ROLE = "role";
        public static final String NOTE_LIST = "note_list";
        public static final String NAME = "name";
        public static final String CUSTOM_SECRET_CODE = "custom_secret_code";
        public static final String AUTHOR_NAME = "author_name";
        public static final String GMAIL = "gmail";
        public static final String PASSWORD = "password";
        public static final String WRITE_PERMISSION = "write_permission";
    }

    public abstract class Note {
        public static final String NOTE_TABLE = "Note";
        public static final String NOTE_ID = "note_id";
        public static final String NOTE_UID = "note_uid";
        public static final String NOTE = "note";
        public static final String NOTE_COUNT = "note_count";
        public static final String NOTE_LIST = "note_list";
        public static final String NOTE_PUBLIC = "note_public";
        public static final String NOTE_PINNED = "note_pinned";
    }

    public abstract class Comment {
        public static final String COMMENT_TABLE = "Comment";
        public static final String COMMENT_ID = "comment_id";
        public static final String COMMENT_UID = "comment_uid";
        public static final String COMMENT = "comment";
        public static final String COMMENT_COUNT = "comment_list";
        public static final String COMMENT_PUBLIC = "comment_public";
        public static final String COMMENT_PINNED = "comment_pinned";
    }

    public abstract class Request {
        public static final String REQUEST_TABLE = "Request";
        public static final String REQUEST_USER_UID = "request_user_uid";
    }

    public abstract class Suggestion {
        public static final String SUGGESTION_TABLE = "Suggestion";
        public static final String SUGGESTION_UID = "suggestion_uid";
        public static final String SUGGESTER_NAME = "suggester_name";
        public static final String SUGGESTION_TIME = "suggestion_time";
    }
}
