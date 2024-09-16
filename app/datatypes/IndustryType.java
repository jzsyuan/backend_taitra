package datatypes;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum IndustryType {
    //("全部", "電子/通訊及周邊設備", "機械", "汽機車/零配件/交通運輸", "農漁產品/食品", "醫療/保健/藥品/美容生技", "五金手工具/建材/家具/安全器材", "化學/橡塑膠/儀器/光學/紙/包裝", "禮品/文具/文創/家用品/百貨", "醫療服務/文創/連鎖/資訊服務", "紡織/服飾配件/鞋類/袋類/珠寶", "自行車/運動用品", "親子婦幼及銀髮用品");
    NONE("none", "")
    ;

    private final String code;
    private final String description;

    IndustryType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private static final Map<String, IndustryType> lookup = new HashMap<String, IndustryType>();

    static {
        for (IndustryType suit : EnumSet.allOf(IndustryType.class)) {
            lookup.put(suit.getCode(), suit);
        }
    }

    public static IndustryType fromOrdinal(String ordinal) {
        return (ordinal == null) ? NONE : lookup.get(ordinal);
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
