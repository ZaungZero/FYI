package team.revolution.fyi.model;

import androidx.annotation.Keep;

@Keep
public class Suggestion {
    private String suggest = "";
    private String suggester = "";
    private String suggestion_uid = "";
    private Long suggested_timestamp;

    public Suggestion() {
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public String getSuggester() {
        return suggester;
    }

    public void setSuggester(String suggester) {
        this.suggester = suggester;
    }

    public String getSuggestion_uid() {
        return suggestion_uid;
    }

    public void setSuggestion_uid(String suggestion_uid) {
        this.suggestion_uid = suggestion_uid;
    }

    public Long getSuggested_timestamp() {
        return suggested_timestamp;
    }

    public void setSuggested_timestamp(Long suggested_timestamp) {
        this.suggested_timestamp = suggested_timestamp;
    }
}
