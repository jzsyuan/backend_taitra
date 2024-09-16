package utils;

import javax.persistence.Column;
import java.lang.reflect.Field;

public class EbeanUtils {
    public static String getAllFieldsOfRawSql(Class object, String prefix, boolean shouldSkipIfNoAnnotation) {
        return getAllFieldsOfRawSql(object, prefix, shouldSkipIfNoAnnotation, false);
    }

    public static String getAllFieldsOfRawSql(Class object, String prefix, boolean shouldSkipIfNoAnnotation, boolean isAnyValue) {
        StringBuilder fields = new StringBuilder();
        int index = 0;
        for (Field field : object.getDeclaredFields()) {
            if (field.getName().startsWith("_")) {
                continue;
            }

            Column annotation = field.getAnnotation(Column.class);
            if (annotation==null || annotation.name().length()==0) {
                if (shouldSkipIfNoAnnotation) {
                    continue;
                }
                if (index!=0) {
                    fields.append(",");
                }
                String name;
                if (isAnyValue) {
                    name = "ANY_VALUE(" + prefix + camelToUnderline(field.getName()) + ")";
                } else {
                    name = prefix + camelToUnderline(field.getName());
                }

                fields.append(name);
            } else {
                if (index!=0) {
                    fields.append(",");
                }
                fields.append(prefix + annotation.name());
            }
            index++;
        }
        fields.append(" ");
        return fields.toString();
    }

    public static String camelToUnderline(String source) {
        if (source == null) {
            return null;
        }
        String target = "";
        for (int i=0; i<source.length(); i++) {
            char ch = source.charAt(i);
            if (ch >= 'A' && ch <= 'Z') {
                if (i!=0) {
                    target += "_";
                }

            }
            target += source.substring(i, i+1).toLowerCase();
        }
        return target;
    }
}
