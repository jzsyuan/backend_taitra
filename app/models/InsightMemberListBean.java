package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONPropertyName;

import javax.persistence.Embedded;


public class InsightMemberListBean {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("HS6")
    private String hsCode6;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("HS4")
    private String hsCode4;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("CNTY")
    private String cntyCode;
    /*
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("HSCODE_6")
    private String hsCode6;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("HSCODE_4")
    private String hsCode4;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("CNTYISO3")
    private String cntyCode;
    */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("activity_id")
    private String activityId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("exhibition_id")
    private String exhibitionId;

    public String getHsCode6() {
        return hsCode6;
    }

    public void setHsCode6(String hsCode6) {
        this.hsCode6 = hsCode6;
    }

    public String getHsCode4() {
        return hsCode4;
    }

    public void setHsCode4(String hsCode4) {
        this.hsCode4 = hsCode4;
    }

    public String getCntyCode() {
        return cntyCode;
    }

    public void setCntyCode(String cntyCode) {
        this.cntyCode = cntyCode;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(String exhibitionId) {
        this.exhibitionId = exhibitionId;
    }
}
