package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"COMPANY_ACTIVITY\"")
public class CompanyActivity extends BaseModel {

    @Column(name = "\"COMPANY_BAN\"")
    private String companyBan;

    @Column(name = "\"ACTIVITY_ID\"")
    private String activityId;

    @Column(name = "\"RATE\"")
    private int rate;

    @Column(name = "\"RANK\"")
    private int rank;

    @Column(name = "\"TYPE\"")
    private String type;

    @Column(name = "\"COMPANY_ACTIVITY_UPDATE_DATETIME\"")
    private Timestamp companyActivityUpdateDateTime;

    public static List<CompanyActivity> findByTaxId(String taxId) {
        return getEbeanServer().find(CompanyActivity.class).where()
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

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getCompanyActivityUpdateDateTime() {
        return companyActivityUpdateDateTime;
    }

    public void setCompanyActivityUpdateDateTime(Timestamp companyActivityUpdateDateTime) {
        this.companyActivityUpdateDateTime = companyActivityUpdateDateTime;
    }
}
