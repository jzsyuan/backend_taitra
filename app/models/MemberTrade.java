package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"MEMBER_TRADE\"")
public class MemberTrade extends BaseModel {
    @Column(name = "\"MEMBER_ID\"")
    private String memberId;

    @Column(name = "\"TRADE_HSCODE\"")
    private String tradeHscode;

    @Column(name = "\"TRADE_COUNTRY\"")
    private String tradeCountry;

    @Column(name = "\"RATE\"")
    private Double rate;

    @Column(name = "\"RANK\"")
    private Integer rank;

    @Column(name = "\"TYPE\"")
    private String type;

    @Column(name = "\"MEMBER_TRADE_UPDATE_DATETIME\"")
    private Timestamp memberTradeUpdateDatetime;

    public static List<MemberTrade> findByMemberIdAndType(String memberId, String type) {
        return getEbeanServer().find(MemberTrade.class).where()
                .eq("\"MEMBER_ID\"", memberId)
                .eq("\"TYPE\"", type)
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

    public String getTradeHscode() {
        return tradeHscode;
    }

    public void setTradeHscode(String tradeHscode) {
        this.tradeHscode = tradeHscode;
    }

    public String getTradeCountry() {
        return tradeCountry;
    }

    public void setTradeCountry(String tradeCountry) {
        this.tradeCountry = tradeCountry;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
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

    public Timestamp getMemberTradeUpdateDatetime() {
        return memberTradeUpdateDatetime;
    }

    public void setMemberTradeUpdateDatetime(Timestamp memberTradeUpdateDatetime) {
        this.memberTradeUpdateDatetime = memberTradeUpdateDatetime;
    }
}
