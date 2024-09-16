package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "\"COMPANY_LIST\"")
public class CompanyList extends BaseModel {
    @Id
    @Column(name = "\"COMPANY_BAN\"")
    private String companyBan;

    @Column(name = "\"COMPANY_STATUS\"")
    private String companyStatus;

    @Column(name = "\"COMPANY_TITLE\"")
    private String companyTitle;

    @Column(name = "\"COMPANY_ADDRESS\"")
    private String companyAddress;

    @Column(name = "\"COMPANY_ACTIVITY\"")
    private String companyActivity;   //jsonb

    @Column(name = "\"COMPANY_INDUSTRY\"")
    private String companyIndustry;   //jsonb

    @Column(name = "\"COMPANY_PRODUCT\"")
    private String companyProduct; //jsonb

    @Column(name = "\"COMPANY_WEBSITE\"")
    private String companyWebsite;

    @Column(name = "\"COMPANY_CAPITAL\"")
    private String companyCapital;

    @Column(name = "\"COMPANY_OWNER\"")
    private String companyOwner;

    @Column(name = "\"COMPANY_ESTABLISH_DATE\"")
    private Date companyEstablishDate;

    @Column(name = "\"COMPANY_UPDATE_DATETIME\"")
    private Timestamp companyUpdateDateTime;

    public static CompanyList findByCompanyBan(String companyBan) {
        return getEbeanServer().find(CompanyList.class).where().eq("\"COMPANY_BAN\"", companyBan).findOne();
    }

    public String getCompanyBan() {
        return companyBan;
    }

    public void setCompanyBan(String companyBan) {
        this.companyBan = companyBan;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public String getCompanyTitle() {
        return companyTitle;
    }

    public void setCompanyTitle(String companyTitle) {
        this.companyTitle = companyTitle;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyActivity() {
        return companyActivity;
    }

    public void setCompanyActivity(String companyActivity) {
        this.companyActivity = companyActivity;
    }

    public String getCompanyIndustry() {
        return companyIndustry;
    }

    public void setCompanyIndustry(String companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    public String getCompanyProduct() {
        return companyProduct;
    }

    public void setCompanyProduct(String companyProduct) {
        this.companyProduct = companyProduct;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyCapital() {
        return companyCapital;
    }

    public void setCompanyCapital(String companyCapital) {
        this.companyCapital = companyCapital;
    }

    public String getCompanyOwner() {
        return companyOwner;
    }

    public void setCompanyOwner(String companyOwner) {
        this.companyOwner = companyOwner;
    }

    public Date getCompanyEstablishDate() {
        return companyEstablishDate;
    }

    public void setCompanyEstablishDate(Date companyEstablishDate) {
        this.companyEstablishDate = companyEstablishDate;
    }

    public Timestamp getCompanyUpdateDateTime() {
        return companyUpdateDateTime;
    }

    public void setCompanyUpdateDateTime(Timestamp companyUpdateDateTime) {
        this.companyUpdateDateTime = companyUpdateDateTime;
    }
}
