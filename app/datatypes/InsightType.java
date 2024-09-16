package datatypes;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum InsightType {
    NONE("none", "", ""),

    ACTIVITY_FAVORITE_ADD_ID("act_fav_add_id", "新增活動列表", "ACTIVITY_FAVORITE_ADD_ID"),
    ACTIVITY_FAVORITE_DEL_ID("act_fav_del_id", "移除活動列表", "ACTIVITY_FAVORITE_DEL_ID"),
    ACTIVITY_RECOMMEND_DEL_ID("act_rec_del_id", "移除活動推薦", "ACTIVITY_RECOMMEND_DEL_ID"),
    EXHIBITION_FAVORITE_ADD_ID("exb_fav_add_id", "新增展會列表", "EXHIBITION_FAVORITE_ADD_ID"),
    EXHIBITION_FAVORITE_DEL_ID("exb_fav_del_id", "移除展會列表", "EXHIBITION_FAVORITE_DEL_ID"),
    EXHIBITION_RECOMMEND_DEL_ID("exb_rec_del_id", "移除展會推薦", "EXHIBITION_RECOMMEND_DEL_ID"),

    ACTIVITY_BLOCK_ID("act_blk_id", "遮蔽活動列表", "ACTIVITY_BLOCK_ID"),
    EXHIBITION_BLOCK_ID("exb_blk_id", "遮蔽展會列表", "EXHIBITION_BLOCK_ID"),

    TRADE_FAVORITE_ADD_CNTY_HS6("trd_fav_add_cntyhs6", "新增追蹤目標市場", "TRADE_FAVORITE_ADD_CNTYHS6"),
    TRADE_FAVORITE_DEL_CNTY_HS6("trd_fav_del_cntyhs6", "移除追蹤目標市場", "TRADE_FAVORITE_DEL_CNTYHS6"),
    TRADE_FAVORITE_ADD_CNTY_HS4("trd_fav_add_cntyhs4", "新增深耕目標市場", "TRADE_FAVORITE_ADD_CNTYHS4"),
    TRADE_FAVORITE_DEL_CNTY_HS4("trd_fav_del_cntyhs4", "移除深耕目標市場", "TRADE_FAVORITE_DEL_CNTYHS4"),
    TRADE_FAVORITE_ADD_HS6("trd_fav_add_hs6", "新增尋找潛力市場", "TRADE_FAVORITE_ADD_HS6"),
    TRADE_FAVORITE_DEL_HS6("trd_fav_del_hs6", "移除尋找潛力市場", "TRADE_FAVORITE_DEL_HS6"),

    TRADE_RECOMMEND_DEL_CNTYHSC6("trd_rec_del_cntyhs6", "經貿數據移除推薦CNTY_HS6", "TRADE_REC_DEL_CNTYHS6"),
    TRADE_RECOMMEND_DEL_CNTYHSC4("trd_rec_del_cntyhs4", "經貿數據移除推薦CNTY_HS4", "TRADE_REC_DEL_CNTYHS4"),
    TRADE_RECOMMEND_DEL_HSC6("trd_rec_del_hs6", "經貿數據移除推薦HS6", "TRADE_REC_DEL_HS6"),
    ;

    private final String code;
    private final String description;
    private final String action;

    InsightType(String code, String description, String action) {
        this.code = code;
        this.description = description;
        this.action = action;
    }

    private static final Map<String, InsightType> lookup = new HashMap<String, InsightType>();

    static {
        for (InsightType suit : EnumSet.allOf(InsightType.class)) {
            lookup.put(suit.getCode(), suit);
        }
    }

    public static InsightType fromOrdinal(String ordinal) {
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
