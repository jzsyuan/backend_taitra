package utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ethanlin on 25/07/2017.
 */
public class StringUtils {
    public static String toString(String[] sourceArray) {
        String result = "";
        int index = 0;
        for (String parameter : sourceArray) {
            if (parameter == null || parameter.trim().length() == 0) {
                continue;
            }
            if (index != 0) {
                result += ",";
            }
            result += parameter;
            index++;
        }
        return result;
    }

    public static boolean containNullOrEmpty(String... parameters) {
        for (String parameter : parameters) {
            if (parameter == null || parameter.trim().length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidTaiwanCellPhone(String cellPhone) {
        String regex = "^09[0-9]{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cellPhone);
        return matcher.matches();
    }


    public static String yyyyMMddHHmmssToDashSeparated(String source, boolean withTime) {
        return source.substring(0, 4) + "-" + source.substring(4, 6) + "-" + source.substring(6, 8);
    }

    private static final String UTF8_BOM = "\uFEFF";

    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public static String hideString(String string) {
        String result = "";
        for (int i = 0; i < string.length(); i += 2) {
            result += string.charAt(i) + "*";
            //result += string.substring(i,i) + "*";
        }
        if (result.length() > string.length()) {
            result = result.substring(0, string.length());
        }
        return result;
    }

    public static String checkNull(String string) {
        return string == null ? "" : string;
    }

    public static String getRandomKey(int length, int type) {
        char[] alphabetaTable = type == 0 ? "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray() : "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int index;

        for (int i = 0; i < length; i++) {
            index = random.nextInt(alphabetaTable.length);
            sb.append(alphabetaTable[index]);
        }
        return sb.toString();
    }

    public static String firstEnglishToUpper(String str) {
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    public static String paddingRight(String str, int paddingWidth, char paddingChar) {
        int paddingLength = paddingWidth - str.length();
        StringBuilder sbPadding = new StringBuilder();

        if (paddingLength > 0) {
            for (int i = 0; i < paddingLength; i++) {
                sbPadding.append(paddingChar);
            }

            return str + sbPadding.toString();
        } else {
            return str;
        }
    }

    public static String paddingLeft(String str, int paddingWidth, char paddingChar) {
        int paddingLength = paddingWidth - str.length();
        StringBuilder sbPadding = new StringBuilder();

        if (paddingLength > 0) {
            for (int i = 0; i < paddingLength; i++) {
                sbPadding.append(paddingChar);
            }

            return sbPadding.toString() + str;
        } else {
            return str;
        }
    }

    public static String serialNumGenerator(int length){
        Random rand = new Random(System.currentTimeMillis());
        String serialNum = "";
        int count = 0;
        char[] encodeChars = new char[]{
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q',
                'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
                'Z', '0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9'};
        while(count < length)
        {
            int randNum = rand.nextInt(encodeChars.length);
            char randChar = encodeChars[randNum];
            serialNum = serialNum + randChar;
            count++;
        }
        return serialNum.toUpperCase();
    }
}
