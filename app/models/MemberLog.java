package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.SqlUpdate;
import io.ebean.annotation.DbJsonB;
import play.libs.Json;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"MEMBER_LOG\"")
public class MemberLog extends BaseModel {
    @Column(name = "\"MEMBER_ID\"")
    private String memberId;

    @Column(name = "\"TYPE\"")
    private String type;

    @DbJsonB
    @Column(name = "\"CONTENT\"")
    private List<InsightMemberListBean> content;

    @Column(name = "\"MEMBER_LOG_UPDATE_DATETIME\"")
    private Timestamp memberLogUpdateDatetime;

    public void insert() {
        ArrayNode dataArray = Json.newArray();
        for (InsightMemberListBean insightMemberListBean : content) {
            ObjectNode node = Json.newObject();
            if (insightMemberListBean.getHsCode6() != null) {
                node.put("HSCODE_6", insightMemberListBean.getHsCode6());
            }
            if (insightMemberListBean.getHsCode4() != null) {
                node.put("HSCODE_4", insightMemberListBean.getHsCode4());
            }
            if (insightMemberListBean.getCntyCode() != null) {
                node.put("CNTYISO3", insightMemberListBean.getCntyCode());
            }
            if (insightMemberListBean.getActivityId() != null) {
                dataArray.add(insightMemberListBean.getActivityId());
            }
            if (insightMemberListBean.getExhibitionId() != null) {
                dataArray.add(insightMemberListBean.getExhibitionId());
            }
            if (!node.isEmpty()) {
                dataArray.add(node);
            }
        }

        String sql = "INSERT INTO \"MEMBER_LOG\" (\"MEMBER_ID\", \"TYPE\", \"CONTENT\", \"MEMBER_LOG_UPDATE_DATETIME\") values (?,?,?::jsonb,?)";
        SqlUpdate sqlUpdate = getEbeanServer().sqlUpdate(sql);
        sqlUpdate.setParameter(memberId);
        sqlUpdate.setParameter(type);
        sqlUpdate.setParameter(dataArray.toString());
        sqlUpdate.setParameter(memberLogUpdateDatetime);
        sqlUpdate.execute();
    }

    public void insert(String content) {
        String sql = "INSERT INTO \"MEMBER_LOG\" (\"MEMBER_ID\", \"TYPE\", \"CONTENT\", \"MEMBER_LOG_UPDATE_DATETIME\") values (?,?,?::jsonb,?)";
        SqlUpdate sqlUpdate = getEbeanServer().sqlUpdate(sql);
        sqlUpdate.setParameter(memberId);
        sqlUpdate.setParameter(type);
        sqlUpdate.setParameter(content);
        sqlUpdate.setParameter(memberLogUpdateDatetime);
        sqlUpdate.execute();
    }

    public ObjectNode toJson() {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.put("member_id", memberId);
        objectNode.put("type", type);
        objectNode.put("ts", System.currentTimeMillis());
        ArrayNode dataArray = objectNode.putArray("data");
        for (InsightMemberListBean insightMemberListBean : content) {
            ObjectNode node = Json.newObject();
            if (insightMemberListBean.getHsCode6() != null) {
                node.put("HSCODE_6", insightMemberListBean.getHsCode6());
            }
            if (insightMemberListBean.getHsCode4() != null) {
                node.put("HSCODE_4", insightMemberListBean.getHsCode4());
            }
            if (insightMemberListBean.getCntyCode() != null) {
                node.put("CNTYISO3", insightMemberListBean.getCntyCode());
            }
            if (insightMemberListBean.getActivityId() != null) {
                dataArray.add(insightMemberListBean.getActivityId());
            }
            if (insightMemberListBean.getExhibitionId() != null) {
                dataArray.add(insightMemberListBean.getExhibitionId());
            }
            if (!node.isEmpty()) {
                dataArray.add(node);
            }
        }
        return objectNode;
    }

    public ObjectNode toJson(JsonNode inputData) {
        ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
        objectNode.put("member_id", memberId);
        objectNode.put("type", type);
        objectNode.put("ts", System.currentTimeMillis());
        ArrayNode dataArray = objectNode.putArray("data");
        //dataArray = inputDataArray;

        return objectNode;
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

    public List<InsightMemberListBean> getContent() {
        return content;
    }

    public void setContent(List<InsightMemberListBean> content) {
        this.content = content;
    }

    public Timestamp getMemberLogUpdateDatetime() {
        return memberLogUpdateDatetime;
    }

    public void setMemberLogUpdateDatetime(Timestamp memberLogUpdateDatetime) {
        this.memberLogUpdateDatetime = memberLogUpdateDatetime;
    }
}
