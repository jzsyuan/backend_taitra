package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"MEMBER_ACTIVITY\"")
public class MemberActivity extends BaseModel {

    @Column(name = "\"MEMBER_ID\"")
    private String memberId;

    @Column(name = "\"ACTIVITY_ID\"")
    private String activityId;

    @Column(name = "\"RATE\"")
    private String rate;

    @Column(name = "\"RANK\"")
    private Integer rank;

    @Column(name = "\"TYPE\"")
    private String type;

    @Column(name = "\"MEMBER_ACTIVITY_UPDATE_DATETIME\"")
    private Timestamp memberActivityUpdateDateTime;

    public static List<MemberActivity> findByMemberId(String memberId) {
        return getEbeanServer().find(MemberActivity.class).where()
                .eq("\"MEMBER_ID\"", memberId)
                .isNotNull("\"RANK\"")
                .orderBy("\"RANK\" ASC")
                .findList();
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getMemberActivityUpdateDateTime() {
        return memberActivityUpdateDateTime;
    }

    public void setMemberActivityUpdateDateTime(Timestamp memberActivityUpdateDateTime) {
        this.memberActivityUpdateDateTime = memberActivityUpdateDateTime;
    }
}
