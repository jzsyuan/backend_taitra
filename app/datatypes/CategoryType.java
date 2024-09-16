package datatypes;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum CategoryType {
    NONE("none", ""),
    ALL("category0", ""),
    DOMESTIC("category1", "國內展覽"),
    FOREIGN("category2", "國外展覽"),
    PURCHASE("category3", "採購洽談"),
    CONFERENCE("category4", "研討會/說明會"),
    EXPAND("category5", "貿訪/拓銷團"),
    TRAINING("category6", "培訓課程"),
    OTHER("category7", "其他"),
    ;

    private final String code;
    private final String description;

    CategoryType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private static final Map<String, CategoryType> lookup = new HashMap<String, CategoryType>();

    static {
        for (CategoryType suit : EnumSet.allOf(CategoryType.class)) {
            lookup.put(suit.getCode(), suit);
        }
    }

    public static CategoryType fromOrdinal(String ordinal) {
        return (ordinal == null) ? NONE : lookup.get(ordinal);
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
