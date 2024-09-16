package models;

import io.ebean.ExpressionList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "member_remove_recommend_list_temp")
public class MemberRemoveRecommendList extends BaseModel {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "type")
    private String type;

    @Column(name = "hs_code")
    private String hscode;

    @Column(name = "cnty")
    private String cnty;

    public static List<MemberRemoveRecommendList> findByMemberIdAndType(String memberId, String type) {
        return getEbeanServer().find(MemberRemoveRecommendList.class).where()
                .eq("member_id", memberId)
                .eq("type", type)
                .findList();
    }

    public static MemberRemoveRecommendList findBy(String memberId, String type, String hscode, String cnty) {
        ExpressionList<MemberRemoveRecommendList> expressionList = getEbeanServer().createQuery(MemberRemoveRecommendList.class).where();
        expressionList.eq("member_id", memberId);
        expressionList.eq("type", type);
        expressionList.eq("hscode", hscode);
        if (cnty != null && !cnty.isEmpty()) {
            expressionList.eq("cnty", cnty);
        }

        return expressionList.findOne();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getHscode() {
        return hscode;
    }

    public void setHscode(String hscode) {
        this.hscode = hscode;
    }

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }
}
