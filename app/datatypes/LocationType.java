package datatypes;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum LocationType {
    NONE("none", ""),                   //無
    ALL("location0", ""),               //全部
    TAIPEI("location1", "Taipei"),            //台北
    TAOYUAN("location2", "Taoyuan"),           //桃園
    HSINCHU("location3", "Hsinchu"),           //新竹
    TAICHUNG("location4", "Taichung"),          //台中
    TAINAN("location5", "Tainan"),            //台南
    KAOHSIUNG("location6", "Kaohsiung"),         //高雄
    TAIWAN("location7", "Taiwan"),            //台灣
    ASIA("location8", "Asia"),              //亞洲
    MIDDLE_EAST("location9", "middle_east"),       //中東
    EUROPE("location10", "europe"),           //歐洲
    AFRICA("location11", "africa"),           //非洲
    NORTH_AMERICA("location12", "north_america"),    //北美洲
    MIDDLE_AMERICA("location13", "middle_america"),   //中美洲
    SOUTH_AMERICA("location14", "south_america"),    //南美洲
    OCEANIA("location15", "oceania"),          //大洋洲
    ;

    private final String code;
    private final String description;

    LocationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private static final Map<String, LocationType> lookup = new HashMap<String, LocationType>();

    static {
        for (LocationType suit : EnumSet.allOf(LocationType.class)) {
            lookup.put(suit.getCode(), suit);
        }
    }

    public static LocationType fromOrdinal(String ordinal) {
        return (ordinal == null) ? NONE : lookup.get(ordinal);
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
