package models;

import io.ebean.ExpressionList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "\"NEWS_LIST\"")
public class NewsList extends BaseModel {

    @Column(name = "\"NEWS_ID\"")
    private String newsId;

    @Column(name = "\"NEWS_FROM\"")
    private String newsFrom;

    @Column(name = "\"NEWS_SOURCE\"")
    private String newsSource;

    @Column(name = "\"NEWS_URL\"")
    private String newsUrl;

    @Column(name = "\"NEWS_TITLE\"")
    private String newsTitle;

    @Column(name = "\"NEWS_COVER\"")
    private String newsCover;

    @Column(name = "\"NEWS_DATETIME\"")
    private Timestamp newsDatetime;

    @Column(name = "\"NEWS_CONTENT\"")
    private String newsContent;

    @Column(name = "\"NEWS_UPDATE_DATETIME\"")
    private Timestamp newsUpdateDatetime;

    public static List<NewsList> findBy(String keyword, Date startTime, Date endTime, int pageIndex, int pageSize) {
        ExpressionList<NewsList> expressionList = getEbeanServer().createQuery(NewsList.class).where();
        if (keyword != null && !keyword.isEmpty()) {
            expressionList.like("\"NEWS_TITLE\"", "%" + keyword + "%");
        }
        if (startTime != null) {
            expressionList.ge("\"NEWS_DATETIME\"", startTime);
        }
        if (endTime != null) {
            expressionList.le("\"NEWS_DATETIME\"", endTime);
        }

        expressionList.orderBy("\"NEWS_DATETIME\" DESC");
        expressionList.setFirstRow(pageIndex);
        expressionList.setMaxRows(pageSize);
        return expressionList.findList();
    }

    public static int findCount(String keyword, Date startTime, Date endTime) {
        ExpressionList<NewsList> expressionList = getEbeanServer().createQuery(NewsList.class).where();
        if (keyword != null && !keyword.isEmpty()) {
            expressionList.like("\"NEWS_TITLE\"", "%" + keyword + "%");
        }
        if (startTime != null) {
            expressionList.ge("\"NEWS_DATETIME\"", startTime);
        }
        if (endTime != null) {
            expressionList.le("\"NEWS_DATETIME\"", endTime);
        }

        return expressionList.findCount();
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsFrom() {
        return newsFrom;
    }

    public void setNewsFrom(String newsFrom) {
        this.newsFrom = newsFrom;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsCover() {
        return newsCover;
    }

    public void setNewsCover(String newsCover) {
        this.newsCover = newsCover;
    }

    public Timestamp getNewsDatetime() {
        return newsDatetime;
    }

    public void setNewsDatetime(Timestamp newsDatetime) {
        this.newsDatetime = newsDatetime;
    }

    public String getNewsContent() {
        return newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public Timestamp getNewsUpdateDatetime() {
        return newsUpdateDatetime;
    }

    public void setNewsUpdateDatetime(Timestamp newsUpdateDatetime) {
        this.newsUpdateDatetime = newsUpdateDatetime;
    }
}
