package models;

import io.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"MEMBER_EXHIBITION\"")
public class MemberExhibition extends BaseModel {
    @Column(name = "\"MEMBER_ID\"")
    private String memberId;

    @Column(name = "\"EXHIBITION_ID\"")
    private String exhibitionId;

    @Column(name = "\"RATE\"")
    private String similarity;

    @Column(name = "\"RANK\"")
    private Integer rank;

    @Column(name = "\"TYPE\"")
    private String type;

    @Column(name = "\"MEMBER_EXHIBITION_UPDATE_DATETIME\"")
    private Timestamp memberExhibitionUpdateDateTime;

    public static List<MemberExhibition> findByMemberId(String memberId) {
        return getEbeanServer().find(MemberExhibition.class).where()
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

    public String getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(String exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public String getSimilarity() {
        return similarity;
    }

    public void setSimilarity(String similarity) {
        this.similarity = similarity;
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

    public Timestamp getMemberExhibitionUpdateDateTime() {
        return memberExhibitionUpdateDateTime;
    }

    public void setMemberExhibitionUpdateDateTime(Timestamp memberExhibitionUpdateDateTime) {
        this.memberExhibitionUpdateDateTime = memberExhibitionUpdateDateTime;
    }
}
