package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"COMPANY_EXHIBITION\"")
public class CompanyExhibition extends BaseModel {

    @Column(name = "\"COMPANY_BAN\"")
    private String companyBan;

    @Column(name = "\"EXHIBITION_ID\"")
    private String exhibitionId;

    @Column(name = "\"RATE\"")
    private String rate;

    @Column(name = "\"RANK\"")
    private Integer rank;

    @Column(name = "\"TYPE\"")
    private String type;

    @Column(name = "\"COMPANY_EXHIBITION_UPDATE_DATETIME\"")
    private Timestamp memberExhibitionUpdateDateTime;

    public static List<CompanyExhibition> findByTaxId(String taxId) {
        return getEbeanServer().find(CompanyExhibition.class).where()
                .eq("\"COMPANY_BAN\"", taxId)
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

    public String getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(String exhibitionId) {
        this.exhibitionId = exhibitionId;
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

    public Timestamp getMemberExhibitionUpdateDateTime() {
        return memberExhibitionUpdateDateTime;
    }

    public void setMemberExhibitionUpdateDateTime(Timestamp memberExhibitionUpdateDateTime) {
        this.memberExhibitionUpdateDateTime = memberExhibitionUpdateDateTime;
    }
}
