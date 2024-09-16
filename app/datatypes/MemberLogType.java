package datatypes;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MemberLogType {
    NONE("none", "", "none"),

    EXHIBITION_FAVORITE_ADD_ID("exb_fav_add_id", "展會新增收藏", "EXHIBITION_FAVORITE_ADD_ID"),
    EXHIBITION_FAVORITE_DEL_ID("exb_fav_del_id", "展會移除收藏", "EXHIBITION_FAVORITE_DEL_ID"),
    EXHIBITION_FAVORITE_CLK_ID("exb_fav_clk_id", "展會點擊收藏", "EXHIBITION_FAVORITE_CLK_ID"),
    EXHIBITION_RECOMMEND_ADD_ID("exb_rec_add_id", "展會推薦收藏", "EXHIBITION_RECOMMEND_ADD_ID"),
    EXHIBITION_RECOMMEND_DEL_ID("exb_rec_del_id", "展會推薦移除", "EXHIBITION_RECOMMEND_DEL_ID"),
    EXHIBITION_RECOMMEND_CLK_ID("exb_rec_clk_id", "展會推薦點擊", "EXHIBITION_RECOMMEND_CLK_ID"),
    EXHIBITION_CLICK_ID("exb_clk_id", "展會點擊", "EXHIBITION_CLICK_ID"),
    EXHIBITION_SEARCH_TEXT("exb_sch_txt", "展會搜尋", "EXHIBITION_SEARCH_TEXT"),

    ACTIVITY_FAVORITE_ADD_ID("act_fav_add_id", "活動新增收藏", "ACTIVITY_FAVORITE_ADD_ID"),
    ACTIVITY_FAVORITE_DEL_ID("act_fav_del_id", "活動移除收藏", "ACTIVITY_FAVORITE_DEL_ID"),
    ACTIVITY_FAVORITE_CLK_ID("act_fav_clk_id", "活動點擊收藏", "ACTIVITY_FAVORITE_CLK_ID"),
    ACTIVITY_RECOMMEND_ADD_ID("act_rec_add_id", "活動推薦收藏", "ACTIVITY_RECOMMEND_ADD_ID"),
    ACTIVITY_RECOMMEND_DEL_ID("act_rec_del_id", "活動推薦移除", "ACTIVITY_RECOMMEND_DEL_ID"),
    ACTIVITY_RECOMMEND_CLK_ID("act_rec_clk_id", "活動推薦點擊", "ACTIVITY_RECOMMEND_CLK_ID"),
    ACTIVITY_CLICK_ID("act_clk_id", "活動點擊", "ACTIVITY_CLICK_ID"),
    ACTIVITY_SEARCH_TEXT("act_sch_txt", "活動搜尋", "ACTIVITY_SEARCH_TEXT"),

    TRADE_FAVORITE_ADD_ID("trd_fav_add_id", "經貿數據新增收藏", "TRADE_FAVORITE_ADD_ID"),
    TRADE_FAVORITE_DEL_ID("trd_fav_del_id", "經貿數據移除收藏", "TRADE_FAVORITE_DEL_ID"),
    TRADE_FAVORITE_CLK_ID("trd_fav_clk_id", "經貿數據點擊收藏", "TRADE_FAVORITE_CLK_ID"),
    TRADE_RECOMMEND_ADD_ID("trd_rec_add_id", "經貿數據推薦收藏", "TRADE_RECOMMEND_ADD_ID"),
    TRADE_RECOMMEND_DEL_ID("trd_rec_del_id", "經貿數據推薦移除", "TRADE_RECOMMEND_DEL_ID"),
    TRADE_RECOMMEND_CLK_ID("trd_rec_clk_id", "經貿數據推薦點擊", "TRADE_RECOMMEND_CLK_ID"),
    TRADE_CLICK_ID("trd_clk_id", "經貿數據點擊", "TRADE_CLICK_ID"),
    TRADE_SEARCH_TEXT("trd_sch_txt", "經貿數據搜尋", "TRADE_SEARCH_TEXT"),

    TRADE_RECOMMEND_DEL_CNTYHSC6("trd_rec_del_cntyhsc6", "經貿數據移除推薦CNTY_HS6", "TRADE_REC_DEL_CNTYHSC6"),
    TRADE_RECOMMEND_DEL_CNTYHSC4("trd_rec_del_cntyhsc4", "經貿數據移除推薦CNTY_HS4", "TRADE_REC_DEL_CNTYHSC4"),
    TRADE_RECOMMEND_DEL_HSC6("trd_rec_del_hsc6", "經貿數據移除推薦HS6", "TRADE_REC_DEL_HSC6"),

    PROFILE_INTEREST_COUNTRY_ADD_ID("profile_interest_country_add_id", "新增感興趣的國家", "PROFILE_INTEREST_COUNTRY_ADD_ID"),
    PROFILE_INTEREST_COUNTRY_DEL_ID("profile_interest_country_del_id", "移除感興趣的國家", "PROFILE_INTEREST_COUNTRY_DEL_ID"),
    PROFILE_INTEREST_INDUSTRY_ADD_ID("profile_interest_industry_add_id", "新增感興趣的產業", "PROFILE_INTEREST_INDUSTRY_ADD_ID"),
    PROFILE_INTEREST_INDUSTRY_DEL_ID("profile_interest_industry_del_id", "移除感興趣的產業", "PROFILE_INTEREST_INDUSTRY_DEL_ID"),

    AI_ACTIVITY_CLICK_MORE("ai_act_clk_mor", "", "AI_ACTIVITY_CLICK_MORE"),
    AI_EXHIBITION_CLICK_MORE("ai_exb_clk_mor", "", "AI_EXHIBITION_CLICK_MORE"),
    AI_TWS_CLICK_ID("ai_tws_clk_id", "", "AI_TWS_CLICK_ID"),
    AI_NEWS_CLICK_ID("ai_nws_clk_id", "", "AI_NEWS_CLICK_ID"),
    AI_NEWS_CLICK_MORE("ai_nws_clk_mor", "", "AI_NEWS_CLICK_MORE"),
    AI_TRADE_CLICK_MORE("ai_trd_clk_mor", "", "AI_TRADE_CLICK_MORE"),
    EXHIBITION_FAVORITE_SHARE_ID("exb_fav_shr_id", "", "EXHIBITION_FAVORITE_SHARE_ID"),

    EXHIBITION_FAVORITE_WEB_ID("exb_fav_web_id", "", "EXHIBITION_FAVORITE_WEB_ID"),
    EXHIBITION_RECOMMEND_WEB_ID("exb_rec_web_id", "", "EXHIBITION_RECOMMEND_WEB_ID"),
    EXHIBITION_WEB_ID("exb_web_id", "", "EXHIBITION_WEB_ID"),
    EXHIBITION_RECOMMEND_SHARE_ID("exb_rec_shr_id", "", "EXHIBITION_RECOMMEND_SHARE_ID"),
    EXHIBITION_SHARE_ID("exb_shr_id", "", "EXHIBITION_SHARE_ID"),
    EXHIBITION_FAVORITE_REGISTER_ID("exb_fav_reg_id", "", "EXHIBITION_FAVORITE_REGISTER_ID"),
    EXHIBITION_RECOMMEND_REGISTER_ID("exb_rec_reg_id", "", "EXHIBITION_RECOMMEND_REGISTER_ID"),
    EXHIBITION_REGISTER_ID("exb_reg_id", "", "EXHIBITION_REGISTER_ID"),
    EXHIBITION_LEAVE_ID("exb_lve_id", "", "EXHIBITION_LEAVE_ID"),

    ACTIVITY_FAVORITE_SHARE_ID("act_fav_shr_id", "", "ACTIVITY_FAVORITE_SHARE_ID"),
    ACTIVITY_FAVORITE_WEB_ID("act_fav_web_id", "", "ACTIVITY_FAVORITE_WEB_ID"),
    ACTIVITY_RECOMMEND_SHARE_ID("act_rec_shr_id", "", "ACTIVITY_RECOMMEND_SHARE_ID"),
    ACTIVITY_RECOMMEND_WEB_ID("act_rec_web_id", "", "ACTIVITY_RECOMMEND_WEB_ID"),
    ACTIVITY_ADD_ID("act_add_id", "", "ACTIVITY_ADD_ID"),
    ACTIVITY_SHARE_ID("act_shr_id", "", "ACTIVITY_SHARE_ID"),
    ACTIVITY_WEB_ID("act_web_id", "", "ACTIVITY_WEB_ID"),

    NEWS_CLICK_ID("nws_clk_id", "", "NEWS_CLICK_ID"),
    NEWS_SHARE_ID("nws_shr_id", "", "NEWS_SHARE_ID"),
    TNEWS_CLICK_ID("tws_clk_id", "", "TNEWS_CLICK_ID"),
    TNEWS_SHARE_ID("tws_shr_id", "", "TNEWS_SHARE_ID"),
    TNEWS_LEAVE_ID("tws_lve_id", "", "TNEWS_LEAVE_ID"),

    TRADE_CLICK_TAB_CNTYHSC6("trd_clk_tab_cntyhsc6", "", "TRADE_CLICK_TAB_CNTYHSC6"),
    TRADE_FAVORITE_CLICK_CNTYHSC6("trd_fav_clk_cntyhsc6", "", "TRADE_FAVORITE_CLICK_CNTYHSC6"),
    TRADE_RECOMMEND_CLICK_CNTYHSC6("trd_rec_clk_cntyhsc6", "", "TRADE_RECOMMEND_CLICK_CNTYHSC6"),
    TRADE_SEARCH_CNTYHSC6("trd_sch_cntyhsc6", "", "TRADE_SEARCH_CNTYHSC6"),
    TRADE_FAVORITE_SHARE_CNTYHSC6("trd_fav_shr_cntyhsc6", "", "TRADE_FAVORITE_SHARE_CNTYHSC6"),
    TRADE_RECOMMEND_SHARE_CNTYHSC6("trd_rec_shr_cntyhsc6", "", "TRADE_RECOMMEND_SHARE_CNTYHSC6"),
    TRADE_SHARE_CNTYHSC6("trd_shr_cntyhsc6", "", "TRADE_SHARE_CNTYHSC6"),
    TRADE_FAVORITE_DEL_CNTYHSC6("trd_fav_del_cntyhsc6", "", "TRADE_FAVORITE_DEL_CNTYHSC6"),
    TRADE_RECOMMEND_ADD_CNTYHSC6("trd_rec_add_cntyhsc6", "", "TRADE_RECOMMEND_ADD_CNTYHSC6"),
    TRADE_ADD_CNTYHSC6("trd_add_cntyhsc6", "", "TRADE_ADD_CNTYHSC6"),
    TRADE_CLICK_SECTION1_CNTYHSC6("trd_clk_sec1_cntyhsc6", "", "TRADE_CLICK_SECTION1_CNTYHSC6"),
    TRADE_CLICK_SECTION2_CNTYHSC6("trd_clk_sec2_cntyhsc6", "", "TRADE_CLICK_SECTION2_CNTYHSC6"),
    TRADE_CLICK_SECTION3_CNTYHSC6("trd_clk_sec3_cntyhsc6", "", "TRADE_CLICK_SECTION3_CNTYHSC6"),

    TRADE_CLICK_TAB_CNTYHSC4("trd_clk_tab_cntyhsc4", "", "TRADE_CLICK_TAB_CNTYHSC4"),
    TRADE_FAVORITE_CLICK_CNTYHSC4("trd_fav_clk_cntyhsc4", "", "TRADE_FAVORITE_CLICK_CNTYHSC4"),
    TRADE_RECOMMEND_CLICK_CNTYHSC4("trd_rec_clk_cntyhsc4", "", "TRADE_RECOMMEND_CLICK_CNTYHSC4"),
    TRADE_SEARCH_CNTY_CNTYHSC4("trd_sch_cntyhsc4", "", "TRADE_SEARCH_CNTY_CNTYHSC4"),
    TRADE_FAVORITE_SHARE_CNTYHSC4("trd_fav_shr_cntyhsc4", "", "TRADE_FAVORITE_SHARE_CNTYHSC4"),
    TRADE_REC_SHARE_CNTYHSC4("trd_rec_shr_cntyhsc4", "", "TRADE_REC_SHARE_CNTYHSC4"),
    TRADE_SHARE_CNTYHSC4("trd_shr_cntyhsc4", "", "TRADE_SHARE_CNTYHSC4"),
    TRADE_FAVORITE_DEL_CNTYHSC4("trd_fav_del_cntyhsc4", "", "TRADE_FAVORITE_DEL_CNTYHSC4"),
    TRADE_RECOMMEND_ADD_CNTYHSC4("trd_rec_add_cntyhsc4", "", "TRADE_RECOMMEND_ADD_CNTYHSC4"),
    TRADE_ADD_CNTYHSC4("trd_add_cntyhsc4", "", "TRADE_ADD_CNTYHSC4"),
    TRADE_CLICK_SECTION1_CNTYHSC4("trd_clk_sec1_cntyhsc4", "", "TRADE_CLICK_SECTION1_CNTYHSC4"),
    TRADE_CLICK_SECTION2_CNTYHSC4("trd_clk_sec2_cntyhsc4", "", "TRADE_CLICK_SECTION2_CNTYHSC4"),
    TRADE_CLICK_SECTION3_CNTYHSC4("trd_clk_sec3_cntyhsc4", "", "TRADE_CLICK_SECTION3_CNTYHSC4"),

    TRADE_CLICK_TAB_HSC6("trd_clk_tab_hsc6", "", "TRADE_CLICK_TAB_HSC6"),
    TRADE_FAVORITE_CLICK_HSC6("trd_fav_clk_hsc6", "", "TRADE_FAVORITE_CLICK_HSC6"),
    TRADE_RECOMMEND_CLICK_HSC6("trd_rec_clk_hsc6", "", "TRADE_RECOMMEND_CLICK_HSC6"),
    TRADE_SEARCH_HSC6("trd_sch_hsc6", "", "TRADE_SEARCH_HSC6"),
    TRADE_FAVORITE_SHARE_HSC6("trd_fav_shr_hsc6", "", "TRADE_FAVORITE_SHARE_HSC6"),
    TRADE_REC_SHARE_HSC6("trd_rec_shr_hsc6", "", "TRADE_REC_SHARE_HSC6"),
    TRADE_SHARE_HSC6("trd_shr_hsc6", "", "TRADE_SHARE_HSC6"),
    TRADE_FAVORITE_DEL_HSC6("trd_fav_del_hsc6", "", "TRADE_FAVORITE_DEL_HSC6"),
    TRADE_RECOMMEND_ADD_HSC6("trd_rec_add_hsc6", "", "TRADE_RECOMMEND_ADD_HSC6"),
    TRADE_ADD_HSC6("trd_add_hsc6", "", "TRADE_ADD_HSC6"),
    TRADE_CLICK_SECTION1_HSC6("trd_clk_sec1_hsc6", "", "TRADE_CLICK_SECTION1_HSC6"),
    TRADE_CLICK_SECTION2_HSC6("trd_clk_sec2_hsc6", "", "TRADE_CLICK_SECTION2_HSC6"),
    TRADE_CLICK_SECTION3_HSC6("trd_clk_sec3_hsc6", "", "TRADE_CLICK_SECTION3_HSC6"),
    ;

    private final String code;
    private final String description;
    private final String action;

    MemberLogType(String code, String description, String action) {
        this.code = code;
        this.description = description;
        this.action = action;
    }

    private static final Map<String, MemberLogType> lookup = new HashMap<String, MemberLogType>();

    static {
        for (MemberLogType suit : EnumSet.allOf(MemberLogType.class)) {
            lookup.put(suit.getCode(), suit);
        }
    }

    public static MemberLogType fromOrdinal(String ordinal) {
        return (ordinal == null) ? NONE : lookup.get(ordinal);
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public String getAction() { return action; }
}
