package models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.DB;
import io.ebean.SqlUpdate;
import io.ebean.annotation.DbJsonB;
import io.ebean.annotation.Where;
import play.libs.Json;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"MEMBER_LIST\"")
public class MemberList extends BaseModel {
    @Column(name = "\"MEMBER_ID\"")
    private String memberId;

    @Column(name = "\"TYPE\"")
    private String type;

    @DbJsonB
    @Column(name = "\"CONTENT\"")
    private List<InsightMemberListBean> content;

    @Column(name = "\"MEMBER_LIST_UPDATE_DATETIME\"")
    private Timestamp memberListUpdateDatetime;

    public static MemberList findByMemberIdAndType(String memberId, String type) {
        return getEbeanServer().find(MemberList.class).where()
                .eq("\"MEMBER_ID\"", memberId)
                .eq("\"TYPE\"", type)
                .findOne();
    }

    public void updateByMemberIdAndType() {
        ArrayNode arrayNode = Json.newArray();
        for (InsightMemberListBean insightMemberListBean : content) {
            ObjectNode node = arrayNode.addObject();
            if (insightMemberListBean.getHsCode6() != null) {
                node.put("HS6", insightMemberListBean.getHsCode6());
            }
            if (insightMemberListBean.getHsCode4() != null) {
                node.put("HS4", insightMemberListBean.getHsCode4());
            }
            if (insightMemberListBean.getCntyCode() != null) {
                node.put("CNTY", insightMemberListBean.getCntyCode());
            }
            if (insightMemberListBean.getActivityId() != null) {
                node.put("activity_id", insightMemberListBean.getActivityId());
            }
            if (insightMemberListBean.getExhibitionId() != null) {
                node.put("exhibition_id", insightMemberListBean.getExhibitionId());
            }
        }

        String sql = "update \"MEMBER_LIST\" SET \"CONTENT\"='" + arrayNode.toString() + "', \"MEMBER_LIST_UPDATE_DATETIME\"=? WHERE \"MEMBER_ID\" = ? AND \"TYPE\" = ?";
        SqlUpdate sqlUpdate = getEbeanServer().sqlUpdate(sql);
        sqlUpdate.setParameter(memberListUpdateDatetime);
        sqlUpdate.setParameter(memberId);
        sqlUpdate.setParameter(type);
        sqlUpdate.execute();
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(List<InsightMemberListBean> content) {
        this.content = content;
    }

    public List<InsightMemberListBean> getContent() {
        return content;
    }

    public Timestamp getMemberListUpdateDatetime() {
        return memberListUpdateDatetime;
    }

    public void setMemberListUpdateDatetime(Timestamp memberListUpdateDatetime) {
        this.memberListUpdateDatetime = memberListUpdateDatetime;
    }
}
