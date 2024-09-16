package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"COMPANY_TRADE\"")
public class CompanyTrade extends BaseModel {
    @Column(name = "\"COMPANY_BAN\"")
    private String companyBan;

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

    @Column(name = "\"COMPANY_TRADE_UPDATE_DATETIME\"")
    private Timestamp companyTradeUpdateDatetime;

    public static List<CompanyTrade> findByCompanyBanAndType(String companyBan, String type) {
        return getEbeanServer().find(CompanyTrade.class).where()
                .eq("\"COMPANY_BAN\"", companyBan)
                .eq("\"TYPE\"", type)
                .isNotNull("\"RANK\"")
                .orderBy("\"RANK\" ASC")
                .findList();
    }

    public String getCompanyBan() {
        return companyBan;
    }

    public void setCompanyBan(String companyBan) {
        this.companyBan = companyBan;
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

    public Timestamp getCompanyTradeUpdateDatetime() {
        return companyTradeUpdateDatetime;
    }

    public void setCompanyTradeUpdateDatetime(Timestamp companyTradeUpdateDatetime) {
        this.companyTradeUpdateDatetime = companyTradeUpdateDatetime;
    }
}
