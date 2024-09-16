package models;

import io.ebean.Expr;
import io.ebean.ExpressionList;
import play.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="\"ACTIVITY_LIST\"")
public class ActivityList extends BaseModel {
    @Id
    @Column(name = "\"ACTIVITY_ID\"")
    private String activityId;

    @Column(name = "\"ACTIVITY_NO\"")
    private String activityNo;

    @Column(name = "\"ACTIVITY_URL\"")
    private String activityUrl;

    @Column(name = "\"ACTIVITY_STATUS\"")
    private String activityStatus;

    @Column(name = "\"ACTIVITY_TITLE_ZH\"")
    private String activityTitleTc;

    @Column(name = "\"ACTIVITY_TITLE_EN\"")
    private String activityTitleEn;

    @Column(name = "\"ACTIVITY_COUNTRY\"")
    private String activityCountry;

    @Column(name = "\"ACTIVITY_AREA\"")
    private String activityArea;    //jsonb

    @Column(name = "\"ACTIVITY_LOCATION\"")
    private String activityLocation;

    @Column(name = "\"ACTIVITY_YEAR\"")
    private Integer activityYear;

    @Column(name = "\"ACTIVITY_ONLINE_START_DATETIME\"")
    private Timestamp activityOnlineStart;

    @Column(name = "\"ACTIVITY_ONLINE_END_DATETIME\"")
    private Timestamp activityOnlineEnd;

    @Column(name = "\"ACTIVITY_EVENT_START_DATETIME\"")
    private Timestamp activityEventStart;

    @Column(name = "\"ACTIVITY_EVENT_END_DATETIME\"")
    private Timestamp activityEventEnd;

    @Column(name = "\"ACTIVITY_ENROLL_START_DATETIME\"")
    private Timestamp activityEnrollStart;

    @Column(name = "\"ACTIVITY_ENROLL_END_DATETIME\"")
    private Timestamp activityEnrollEnd;

    @Column(name = "\"ACTIVITY_TAG\"")
    private String activityTags; //jsonb

    @Column(name = "\"ACTIVITY_CATEGORY\"")
    private String activityCategory;    //jsonb

    @Column(name = "\"ACTIVITY_INDUSTRY\"")
    private String activityIndustries;  //jsonb

    @Column(name = "\"ACTIVITY_ORGANIZER\"")
    private String activityOrganizer;

    @Column(name = "\"ACTIVITY_CONTENT\"")
    private String activityContent;

    @Column(name = "\"ACTIVITY_COVER\"")
    private String activityCover;

    @Column(name = "\"ACTIVITY_FEE\"")
    private String activityFee;

    @Column(name = "\"ACTIVITY_CONTACT_PERSON\"")
    private String activityContactPerson;

    @Column(name = "\"ACTIVITY_CONTACT_TEL\"")
    private String activityContactTel;

    @Column(name = "\"ACTIVITY_CONTACT_FAX\"")
    private String activityContactFax;

    @Column(name = "\"ACTIVITY_CONTACT_EMAIL\"")
    private String activityContactEmail;

    @Column(name = "\"ACTIVITY_UPDATE_DATETIME\"")
    private Timestamp activityUpdateDateTime;

    @Transient
    private Integer rank;

    public static ActivityList findActivityListById(String activityId) {
       return getEbeanServer().createQuery(ActivityList.class).where().eq("\"ACTIVITY_ID\"", activityId).findOne();
    }

    public static List<ActivityList> findExhibitionAll() {
        ExpressionList<ActivityList> expressionList = getEbeanServer().createQuery(ActivityList.class).where();
        return expressionList.findList();
    }

    public static List<ActivityList> findActivityList(String keyword, String type, String industry, String location, Date startTime, Date endTime, Boolean taitra, Boolean showAll, String openStatus, Date now, List<String> activityIds, List<String> notActivityIds, boolean isDescending, int pageIndex, int pageSize) {
        Date current = new Date();
        ExpressionList<ActivityList> expressionList = getEbeanServer().createQuery(ActivityList.class).where();
        if (keyword != null && !keyword.isEmpty()) {
            //expressionList.like("\"ACTIVITY_TITLE_ZH\"", "%" + keyword + "%");
            expressionList.or(Expr.like("\"ACTIVITY_TITLE_ZH\"", "%" + keyword + "%"), Expr.raw("\"ACTIVITY_TAG\" ??| array['" + keyword + "']"));
            //        .or()
            //        .raw("\"ACTIVITY_TAG\" ??| array['" + keyword + "']")
            //        .endOr();
        }
        if (type != null && !type.isEmpty()) {
            expressionList.and().raw("\"ACTIVITY_CATEGORY\" ??| array['" + type + "']").endAnd();
        }
        if (industry != null && !industry.isEmpty()) {
            expressionList.and().raw("\"ACTIVITY_INDUSTRY\" ??| array['" + industry + "']").endAnd();
        }
        if (location != null && !location.isEmpty()) {
            expressionList.and().raw("\"ACTIVITY_COUNTRY\" ??| array['" + location + "']").endAnd();
        }
        if (startTime != null) {
            expressionList.ge("\"ACTIVITY_EVENT_START_DATETIME\"", startTime);
        }
        if (endTime != null) {
            expressionList.le("\"ACTIVITY_EVENT_END_DATETIME\"", endTime);
        }
        if (taitra != null) {
            if (taitra) {
                expressionList.eq("\"ACTIVITY_ORGANIZER\"", "貿協");
            } else {
                expressionList.eq("\"ACTIVITY_ORGANIZER\"", "非貿協");
            }
        }
        if (showAll != null) {

        }
        //全部:null、0:我要報名 1:報名截止 2:尚未開放
        if (openStatus != null && !openStatus.isEmpty()) {
            if (openStatus.equals("0")) {
                expressionList.betweenProperties("\"ACTIVITY_ENROLL_START_DATETIME\"", "\"ACTIVITY_ENROLL_END_DATETIME\"", current);
            } else if (openStatus.equals("1")) {
                expressionList.le("\"ACTIVITY_ENROLL_END_DATETIME\"", current);
            } else if (openStatus.equals("2")) {
                expressionList.ge("\"ACTIVITY_ENROLL_START_DATETIME\"", current);
            }
        }
        if (activityIds != null) {
            expressionList.in("\"ACTIVITY_ID\"", activityIds);
        }
        if (notActivityIds != null) {
            expressionList.notIn("\"ACTIVITY_ID\"", notActivityIds);
        }
        if (now != null) {
            if (isDescending) {
                expressionList.lt("\"ACTIVITY_EVENT_END_DATETIME\"", now);
            } else {
                expressionList.ge("\"ACTIVITY_EVENT_END_DATETIME\"", now);
            }
        }
        if (activityIds == null) {
            if (isDescending) {
                expressionList.orderBy("\"ACTIVITY_EVENT_END_DATETIME\" DESC");
            } else {
                expressionList.orderBy("\"ACTIVITY_EVENT_END_DATETIME\" ASC");
            }
        }

        if (pageIndex >= 0) {
            expressionList.setFirstRow(pageIndex);
        }
        expressionList.setMaxRows(pageSize);
        return expressionList.findList();
    }

    public static List<ActivityList> findByActivityIds(List<String> activityIds, int pageIndex, int pageSize) {
        ExpressionList<ActivityList> expressionList = getEbeanServer().createQuery(ActivityList.class).where();
        if (!activityIds.isEmpty()) {
            expressionList.in("\"ACTIVITY_ID\"", activityIds);
        } else {
            return new ArrayList<>();
        }

        expressionList.setFirstRow(pageIndex);
        expressionList.setMaxRows(pageSize);
        return expressionList.findList();
    }

    public static int findActivityCount(String keyword, String type, String industry, String location, Date startTime, Date endTime, Boolean taitra, Boolean showAll, String openStatus, Date now, List<String> activityIds, List<String> notActivityIds, boolean isDescending) {
        Date current = new Date();
        ExpressionList<ActivityList> expressionList = getEbeanServer().createQuery(ActivityList.class).where();
        if (keyword != null && !keyword.isEmpty()) {
            //expressionList.like("\"ACTIVITY_TITLE_ZH\"", "%" + keyword + "%");
            expressionList.or(Expr.like("\"ACTIVITY_TITLE_ZH\"", "%" + keyword + "%"), Expr.raw("\"ACTIVITY_TAG\" ??| array['" + keyword + "']"));
            //        .or()
            //        .raw("\"ACTIVITY_TAG\" ??| array['" + keyword + "']")
            //        .endOr();
        }
        if (type != null && !type.isEmpty()) {
            expressionList.and().raw("\"ACTIVITY_CATEGORY\" ??| array['" + type + "']").endAnd();
        }
        if (industry != null && !industry.isEmpty()) {
            expressionList.and().raw("\"ACTIVITY_INDUSTRY\" ??| array['" + industry + "']").endAnd();
        }
        if (location != null && !location.isEmpty()) {
            expressionList.and().raw("\"ACTIVITY_COUNTRY\" ??| array['" + location + "']").endAnd();
        }
        if (startTime != null) {
            expressionList.ge("\"ACTIVITY_EVENT_START_DATETIME\"", startTime);
        }
        if (endTime != null) {
            expressionList.le("\"ACTIVITY_EVENT_END_DATETIME\"", endTime);
        }
        if (taitra != null) {
            if (taitra) {
                expressionList.eq("\"ACTIVITY_ORGANIZER\"", "貿協");
            } else {
                expressionList.eq("\"ACTIVITY_ORGANIZER\"", "非貿協");
            }
        }
        if (showAll != null) {

        }
        if (openStatus != null && !openStatus.isEmpty()) {
            if (openStatus.equals("0")) {
                expressionList.betweenProperties("\"ACTIVITY_ENROLL_START_DATETIME\"", "\"ACTIVITY_ENROLL_END_DATETIME\"", current);
            } else if (openStatus.equals("1")) {
                expressionList.le("\"ACTIVITY_ENROLL_END_DATETIME\"", current);
            } else if (openStatus.equals("2")) {
                expressionList.ge("\"ACTIVITY_ENROLL_START_DATETIME\"", current);
            }
        }
        if (activityIds != null) {
            expressionList.in("\"ACTIVITY_ID\"", activityIds);
        }
        if (notActivityIds != null) {
            expressionList.notIn("\"ACTIVITY_ID\"", notActivityIds);
        }
        if (now != null) {
            if (isDescending) {
                expressionList.lt("\"ACTIVITY_EVENT_END_DATETIME\"", now);
            } else {
                expressionList.ge("\"ACTIVITY_EVENT_END_DATETIME\"", now);
            }
        }

        return expressionList.findCount();
    }

    public static int findByActivityIdsCount(List<String> activityIds) {
        ExpressionList<ActivityList> expressionList = getEbeanServer().createQuery(ActivityList.class).where();
        if (!activityIds.isEmpty()) {
            expressionList.in("\"ACTIVITY_ID\"", activityIds);
        } else {
            return 0;
        }

        return expressionList.findCount();
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityNo() {
        return activityNo;
    }

    public void setActivityNo(String activityNo) {
        this.activityNo = activityNo;
    }

    public String getActivityUrl() {
        return activityUrl;
    }

    public void setActivityUrl(String activityUrl) {
        this.activityUrl = activityUrl;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getActivityTitleTc() {
        return activityTitleTc;
    }

    public void setActivityTitleTc(String activityTitleTc) {
        this.activityTitleTc = activityTitleTc;
    }

    public String getActivityTitleEn() {
        return activityTitleEn;
    }

    public void setActivityTitleEn(String activityTitleEn) {
        this.activityTitleEn = activityTitleEn;
    }

    public String getActivityCountry() {
        return activityCountry;
    }

    public void setActivityCountry(String activityCountry) {
        this.activityCountry = activityCountry;
    }

    public String getActivityArea() {
        return activityArea;
    }

    public void setActivityArea(String activityArea) {
        this.activityArea = activityArea;
    }

    public String getActivityLocation() {
        return activityLocation;
    }

    public void setActivityLocation(String activityLocation) {
        this.activityLocation = activityLocation;
    }

    public Integer getActivityYear() {
        return activityYear;
    }

    public void setActivityYear(Integer activityYear) {
        this.activityYear = activityYear;
    }

    public Timestamp getActivityOnlineStart() {
        return activityOnlineStart;
    }

    public void setActivityOnlineStart(Timestamp activityOnlineStart) {
        this.activityOnlineStart = activityOnlineStart;
    }

    public Timestamp getActivityOnlineEnd() {
        return activityOnlineEnd;
    }

    public void setActivityOnlineEnd(Timestamp activityOnlineEnd) {
        this.activityOnlineEnd = activityOnlineEnd;
    }

    public Timestamp getActivityEventStart() {
        return activityEventStart;
    }

    public void setActivityEventStart(Timestamp activityEventStart) {
        this.activityEventStart = activityEventStart;
    }

    public Timestamp getActivityEventEnd() {
        return activityEventEnd;
    }

    public void setActivityEventEnd(Timestamp activityEventEnd) {
        this.activityEventEnd = activityEventEnd;
    }

    public Timestamp getActivityEnrollStart() {
        return activityEnrollStart;
    }

    public void setActivityEnrollStart(Timestamp activityEnrollStart) {
        this.activityEnrollStart = activityEnrollStart;
    }

    public Timestamp getActivityEnrollEnd() {
        return activityEnrollEnd;
    }

    public void setActivityEnrollEnd(Timestamp activityEnrollEnd) {
        this.activityEnrollEnd = activityEnrollEnd;
    }

    public String getActivityTags() {
        return activityTags;
    }

    public void setActivityTags(String activityTags) {
        this.activityTags = activityTags;
    }

    public String getActivityCategory() {
        return activityCategory;
    }

    public void setActivityCategory(String activityCategory) {
        this.activityCategory = activityCategory;
    }

    public String getActivityIndustries() {
        return activityIndustries;
    }

    public void setActivityIndustries(String activityIndustries) {
        this.activityIndustries = activityIndustries;
    }

    public String getActivityOrganizer() {
        return activityOrganizer;
    }

    public void setActivityOrganizer(String activityOrganizer) {
        this.activityOrganizer = activityOrganizer;
    }

    public String getActivityContent() {
        return activityContent;
    }

    public void setActivityContent(String activityContent) {
        this.activityContent = activityContent;
    }

    public String getActivityCover() {
        return activityCover;
    }

    public void setActivityCover(String activityCover) {
        this.activityCover = activityCover;
    }

    public String getActivityFee() {
        return activityFee;
    }

    public void setActivityFee(String activityFee) {
        this.activityFee = activityFee;
    }

    public String getActivityContactPerson() {
        return activityContactPerson;
    }

    public void setActivityContactPerson(String activityContactPerson) {
        this.activityContactPerson = activityContactPerson;
    }

    public String getActivityContactTel() {
        return activityContactTel;
    }

    public void setActivityContactTel(String activityContactTel) {
        this.activityContactTel = activityContactTel;
    }

    public String getActivityContactFax() {
        return activityContactFax;
    }

    public void setActivityContactFax(String activityContactFax) {
        this.activityContactFax = activityContactFax;
    }

    public String getActivityContactEmail() {
        return activityContactEmail;
    }

    public void setActivityContactEmail(String activityContactEmail) {
        this.activityContactEmail = activityContactEmail;
    }

    public Timestamp getActivityUpdateDateTime() {
        return activityUpdateDateTime;
    }

    public void setActivityUpdateDateTime(Timestamp activityUpdateDateTime) {
        this.activityUpdateDateTime = activityUpdateDateTime;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
