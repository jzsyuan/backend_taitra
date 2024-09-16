package models;

import io.ebean.Expr;
import io.ebean.Expression;
import io.ebean.ExpressionList;
import io.ebean.annotation.View;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@View(name = "\"EXHIBITION_LIST_GROUP\"")
public class ExhibitionListGroup extends BaseModel {
    @Id
    @Column(name = "\"EXHIBITION_ID\"")
    private String exhibitionId;

    @Column(name = "\"EXHIBITION_GROUP\"")
    private String exhibitionGroup;

    @Column(name = "\"EXHIBITION_COVER\"")
    private String exhibitionCover;

    @Column(name = "\"EXHIBITION_TITLE\"")
    private String exhibitionTitle;

    @Column(name = "\"EXHIBITION_YEAR\"")
    private Integer exhibitionYear;

    @Column(name = "\"EXHIBITION_START_DATE\"")
    private Date exhibitionSdate;

    @Column(name = "\"EXHIBITION_END_DATE\"")
    private Date exhibitionEdate;

    @Column(name = "\"EXHIBITION_TAG\"")
    private String exhibitionTags;  //jsonb

    @Column(name = "\"EXHIBITION_INDUSTRY\"")
    private String exhibitionIndustries;    //jsonb

    @Column(name = "\"EXHIBITION_PRODUCT\"")
    private String exhibitionProducts;  //jsonb

    @Column(name = "\"EXHIBITION_COUNTRY\"")
    private String exhibitionCountry;

    @Column(name = "\"EXHIBITION_CITY\"")
    private String exhibitionCity;

    @Column(name = "\"EXHIBITION_VENUE\"")
    private String exhibitionVenue;

    @Column(name = "\"EXHIBITION_SUMMARY\"")
    private String exhibitionSummary;

    @Column(name = "\"EXHIBITION_FREQUENCY\"")
    private String exhibitionFrequency;

    @Column(name = "\"EXHIBITION_ORGANIZER\"")
    private String exhibitionOrganizer;

    @Column(name = "\"EXHIBITION_ORGANIZER_WEBSITE\"")
    private String exhibitionOrganizerWebsite;

    @Column(name = "\"EXHIBITION_ELIGIBILITY\"")
    private String exhibitionEligibility;

    @Column(name = "\"EXHIBITION_WEBSITE\"")
    private String exhibitionWebsite;

    @Column(name = "\"EXHIBITION_EXHIBITOR_COUNT\"")
    private Integer exhibitionExhibitors;

    @Column(name = "\"EXHIBITION_VISTOR_COUNT\"")
    private Integer exhibitionVisitors;

    @Column(name = "\"EXHIBITION_SPACE\"")
    private Integer exhibitionSpace;    //m^2

    @Column(name = "\"EXHIBITION_UPDATE_DATETIME\"")
    private Timestamp exhibitionUpdateDateTime;

    @Transient
    private Integer rank;

    public ExhibitionListGroup(ExhibitionList exhibitionList) {
        this.exhibitionId = exhibitionList.getExhibitionId();
        this.exhibitionGroup = exhibitionList.getExhibitionGroup();
        this.exhibitionCover = exhibitionList.getExhibitionCover();
        this.exhibitionTitle = exhibitionList.getExhibitionTitle();
        this.exhibitionYear = exhibitionList.getExhibitionYear();
        this.exhibitionSdate = exhibitionList.getExhibitionSdate();
        this.exhibitionEdate = exhibitionList.getExhibitionEdate();
        this.exhibitionTags = exhibitionList.getExhibitionTags();
        this.exhibitionIndustries = exhibitionList.getExhibitionIndustries();
        this.exhibitionProducts = exhibitionList.getExhibitionProducts();
        this.exhibitionCountry = exhibitionList.getExhibitionCountry();
        this.exhibitionCity = exhibitionList.getExhibitionCity();
        this.exhibitionVenue = exhibitionList.getExhibitionVenue();
        this.exhibitionSummary = exhibitionList.getExhibitionSummary();
        this.exhibitionFrequency = exhibitionList.getExhibitionFrequency();
        this.exhibitionOrganizer = exhibitionList.getExhibitionOrganizer();
        this.exhibitionOrganizerWebsite = exhibitionList.getExhibitionOrganizerWebsite();
        this.exhibitionEligibility = exhibitionList.getExhibitionEligibility();
        this.exhibitionWebsite = exhibitionList.getExhibitionWebsite();
        this.exhibitionExhibitors = exhibitionExhibitors;
        this.exhibitionVisitors = exhibitionVisitors;
        this.exhibitionSpace = exhibitionList.getExhibitionSpace();
        this.exhibitionUpdateDateTime =exhibitionList.getExhibitionUpdateDateTime();
    }

    public static ExhibitionListGroup findExhibitionGroupById(String exhibitionId) {
        return getEbeanServer().createQuery(ExhibitionListGroup.class).where().eq("\"EXHIBITION_ID\"", exhibitionId).findOne();
    }

    public static List<ExhibitionListGroup> findExhibitionGroupAll() {
        ExpressionList<ExhibitionListGroup> expressionList = getEbeanServer().createQuery(ExhibitionListGroup.class).where();
        return expressionList.findList();
    }

    public static List<ExhibitionListGroup> findExhibitionGroup(String keyword, int year, String nation, Date now, List<String> exhibitionIds, List<String> notExhibitionIds, boolean isDescending, int pageIndex, int pageSize) {
        ExpressionList<ExhibitionListGroup> expressionList = getEbeanServer().createQuery(ExhibitionListGroup.class).where();
        if (keyword != null && !keyword.isEmpty()) {
            //expressionList.like("\"EXHIBITION_TITLE\"", "%" + keyword + "%");
            expressionList.or(Expr.like("\"EXHIBITION_TITLE\"", "%" + keyword + "%"), Expr.raw("\"EXHIBITION_TAG\" ??| array['" + keyword + "']"));
        }
        if (year != 0) {
            expressionList.eq("\"EXHIBITION_YEAR\"", year);
        }
        if (nation != null && !nation.isEmpty()) {
            //expressionList.eq("\"EXHIBITION_COUNTRY\"", nation);
            expressionList.and().raw("\"EXHIBITION_TAG\" ??| array['" + nation + "']").endAnd();
        }
        if (exhibitionIds != null) {
            expressionList.in("\"EXHIBITION_ID\"", exhibitionIds);
        }
        if (notExhibitionIds != null) {
            expressionList.notIn("\"EXHIBITION_ID\"", notExhibitionIds);
        }
        if (now != null) {
            if (isDescending) {
                expressionList.lt("\"EXHIBITION_END_DATE\"", now);
            } else {
                expressionList.ge("\"EXHIBITION_END_DATE\"", now);
            }
        }
        /*
        expressionList.isNotNull("\"EXHIBITION_TITLE\"");
        expressionList.isNotNull("\"EXHIBITION_START_DATE\"");
        expressionList.isNotNull("\"EXHIBITION_END_DATE\"");
        expressionList.isNotNull("\"EXHIBITION_COUNTRY\"");
        */
        if (exhibitionIds == null) {
            if (isDescending) {
                expressionList.orderBy("\"EXHIBITION_END_DATE\" DESC");
            } else {
                expressionList.orderBy().asc("\"EXHIBITION_END_DATE\"");
            }
        }

        if (pageIndex >= 0) {
            expressionList.setFirstRow(pageIndex);
        }
        expressionList.setMaxRows(pageSize);
        return expressionList.findList();
    }

    public static List<ExhibitionListGroup> findByExhibitionIds(List<String> exhibitionIds, int pageIndex, int pageSize) {
        ExpressionList<ExhibitionListGroup> expressionList = getEbeanServer().createQuery(ExhibitionListGroup.class).where();
        if (!exhibitionIds.isEmpty()) {
            expressionList.in("\"EXHIBITION_ID\"", exhibitionIds);
        } else {
            return new ArrayList<>();
        }
        expressionList.setFirstRow(pageIndex);
        expressionList.setMaxRows(pageSize);
        return expressionList.findList();
    }

    public static List<ExhibitionListGroup> findByGroup(String groupId) {
        return getEbeanServer().createQuery(ExhibitionListGroup.class).where()
                .eq("\"EXHIBITION_GROUP\"", groupId)
                .orderBy("\"EXHIBITION_START_DATE\" DESC")
                .findList();
    }

    public static int findExhibitionGroupCount(String keyword, int year, String nation, Date now, List<String> exhibitionIds, List<String> notExhibitionIds, boolean isDescending) {
        ExpressionList<ExhibitionListGroup> expressionList = getEbeanServer().createQuery(ExhibitionListGroup.class).where();
        if (keyword != null && !keyword.isEmpty()) {
            //expressionList.like("\"EXHIBITION_TITLE\"", "%" + keyword + "%");
            expressionList.or(Expr.like("\"EXHIBITION_TITLE\"", "%" + keyword + "%"), Expr.raw("\"EXHIBITION_TAG\" ??| array['" + keyword + "']"));
        }
        if (year != 0) {
            expressionList.eq("\"EXHIBITION_YEAR\"", year);
        }
        if (nation != null && !nation.isEmpty()) {
            //expressionList.eq("\"EXHIBITION_COUNTRY\"", nation);
            expressionList.and().raw("\"EXHIBITION_TAG\" ??| array['" + nation + "']").endAnd();
        }
        if (exhibitionIds != null) {
            expressionList.in("\"EXHIBITION_ID\"", exhibitionIds);
        }
        if (notExhibitionIds != null) {
            expressionList.notIn("\"EXHIBITION_ID\"", notExhibitionIds);
        }
        if (now != null) {
            if (isDescending) {
                expressionList.lt("\"EXHIBITION_END_DATE\"", now);
            } else {
                expressionList.ge("\"EXHIBITION_END_DATE\"", now);
            }
        }
        /*
        expressionList.isNotNull("\"EXHIBITION_TITLE\"");
        expressionList.isNotNull("\"EXHIBITION_START_DATE\"");
        expressionList.isNotNull("\"EXHIBITION_END_DATE\"");
        expressionList.isNotNull("\"EXHIBITION_COUNTRY\"");
        */
        return expressionList.findCount();
    }

    public static int findByExhibitionIdsCount(List<String> exhibitionIds) {
        ExpressionList<ExhibitionListGroup> expressionList = getEbeanServer().createQuery(ExhibitionListGroup.class).where();
        if (!exhibitionIds.isEmpty()) {
            expressionList.in("\"EXHIBITION_ID\"", exhibitionIds);
        } else {
            return 0;
        }
        return expressionList.findCount();
    }

    public String getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(String exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public String getExhibitionGroup() {
        return exhibitionGroup;
    }

    public void setExhibitionGroup(String exhibitionGroup) {
        this.exhibitionGroup = exhibitionGroup;
    }

    public String getExhibitionCover() {
        return exhibitionCover;
    }

    public void setExhibitionCover(String exhibitionCover) {
        this.exhibitionCover = exhibitionCover;
    }

    public String getExhibitionTitle() {
        return exhibitionTitle;
    }

    public void setExhibitionTitle(String exhibitionTitle) {
        this.exhibitionTitle = exhibitionTitle;
    }

    public Integer getExhibitionYear() {
        return exhibitionYear;
    }

    public void setExhibitionYear(int exhibitionYear) {
        this.exhibitionYear = exhibitionYear;
    }

    public Date getExhibitionSdate() {
        return exhibitionSdate;
    }

    public void setExhibitionSdate(Date exhibitionSdate) {
        this.exhibitionSdate = exhibitionSdate;
    }

    public Date getExhibitionEdate() {
        return exhibitionEdate;
    }

    public void setExhibitionEdate(Date exhibitionEdate) {
        this.exhibitionEdate = exhibitionEdate;
    }

    public String getExhibitionTags() {
        return exhibitionTags;
    }

    public void setExhibitionTags(String exhibitionTags) {
        this.exhibitionTags = exhibitionTags;
    }

    public String getExhibitionIndustries() {
        return exhibitionIndustries;
    }

    public void setExhibitionIndustries(String exhibitionIndustries) {
        this.exhibitionIndustries = exhibitionIndustries;
    }

    public String getExhibitionProducts() {
        return exhibitionProducts;
    }

    public void setExhibitionProducts(String exhibitionProducts) {
        this.exhibitionProducts = exhibitionProducts;
    }

    public String getExhibitionCountry() {
        return exhibitionCountry;
    }

    public void setExhibitionCountry(String exhibitionCountry) {
        this.exhibitionCountry = exhibitionCountry;
    }

    public String getExhibitionCity() {
        return exhibitionCity;
    }

    public void setExhibitionCity(String exhibitionCity) {
        this.exhibitionCity = exhibitionCity;
    }

    public String getExhibitionVenue() {
        return exhibitionVenue;
    }

    public void setExhibitionVenue(String exhibitionVenue) {
        this.exhibitionVenue = exhibitionVenue;
    }

    public String getExhibitionSummary() {
        return exhibitionSummary;
    }

    public void setExhibitionSummary(String exhibitionSummary) {
        this.exhibitionSummary = exhibitionSummary;
    }

    public String getExhibitionFrequency() {
        return exhibitionFrequency;
    }

    public void setExhibitionFrequency(String exhibitionFrequency) {
        this.exhibitionFrequency = exhibitionFrequency;
    }

    public String getExhibitionOrganizer() {
        return exhibitionOrganizer;
    }

    public void setExhibitionOrganizer(String exhibitionOrganizer) {
        this.exhibitionOrganizer = exhibitionOrganizer;
    }

    public String getExhibitionEligibility() {
        return exhibitionEligibility;
    }

    public void setExhibitionEligibility(String exhibitionEligibility) {
        this.exhibitionEligibility = exhibitionEligibility;
    }

    public String getExhibitionWebsite() {
        return exhibitionWebsite;
    }

    public void setExhibitionWebsite(String exhibitionWebsite) {
        this.exhibitionWebsite = exhibitionWebsite;
    }

    public Integer getExhibitionExhibitors() {
        return exhibitionExhibitors;
    }

    public void setExhibitionExhibitors(Integer exhibitionExhibitors) {
        this.exhibitionExhibitors = exhibitionExhibitors;
    }

    public Integer getExhibitionVisitors() {
        return exhibitionVisitors;
    }

    public void setExhibitionVisitors(Integer exhibitionVisitors) {
        this.exhibitionVisitors = exhibitionVisitors;
    }

    public Integer getExhibitionSpace() {
        return exhibitionSpace;
    }

    public void setExhibitionSpace(Integer exhibitionSpace) {
        this.exhibitionSpace = exhibitionSpace;
    }

    public Timestamp getExhibitionUpdateDateTime() {
        return exhibitionUpdateDateTime;
    }

    public void setExhibitionUpdateDateTime(Timestamp exhibitionUpdateDateTime) {
        this.exhibitionUpdateDateTime = exhibitionUpdateDateTime;
    }

    public String getExhibitionOrganizerWebsite() {
        return exhibitionOrganizerWebsite;
    }

    public void setExhibitionOrganizerWebsite(String exhibitionOrganizerWebsite) {
        this.exhibitionOrganizerWebsite = exhibitionOrganizerWebsite;
    }

    public void setExhibitionYear(Integer exhibitionYear) {
        this.exhibitionYear = exhibitionYear;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
