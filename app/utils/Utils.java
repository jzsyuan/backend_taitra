package utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;
import play.mvc.Http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static Object getApplicationConfig(Config configuration, String key) {
        for (Map.Entry<String, ConfigValue> entry : configuration.entrySet()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue().unwrapped();
            }
        }
        return null;
    }

    public static boolean containNullOrEmpty(String... parameters) {
        for (String parameter : parameters) {
            if (parameter == null || parameter.trim().length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static boolean isUrl(String pInput) {
        if (pInput == null) {
            return false;
        }
        String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
                + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"
                + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"
                + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"
                + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"
                + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"
                + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"
                + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(pInput);
        return matcher.matches();
    }

    public static boolean checkBanNumber(String banNumber) {
        String regex = "^[0-9]{8}$";
        return (banNumber != null && Pattern.matches(regex, banNumber));
    }

    //Get random exhibition image link
    public static String getRandomExhibitionImageLink() {
        String baseLink = "https://ezchoice-staging.s3-ap-southeast-1.amazonaws.com/uploads/taitra/exhibition/";
        String link = "";
        List<String> exbImgList = Arrays.asList("exbImage1", "exbImage2", "exbImage3", "exbImage4", "exbImage5", "exbImage6", "exbImage7", "exbImage8", "exbImage9", "exbImage10",
                "exbImage11", "exbImage12", "exbImage13", "exbImage14", "exbImage15", "exbImage16");
        Random rand = new Random();
        int n = rand.nextInt(exbImgList.size());
        link = baseLink + exbImgList.get(n) + ".jpg";

        return link;
    }

    //Get random activity image link
    public static String getRandomActivityImageLink() {
        String baseLink = "https://ezchoice-staging.s3-ap-southeast-1.amazonaws.com/uploads/taitra/activity/";
        String link = "";
        List<String> actImgList = Arrays.asList("actImage1.png", "actImage2.png", "actImage3.png", "actImage4.png", "actImage5.png", "actImage6.png", "actImage7.png", "actImage8.jpg", "actImage9.jpg", "actImage10.jpg",
                "actImage11.jpg", "actImage12.jpg", "actImage13.jpg", "actImage14.jpg", "actImage15.jpg", "actImage16.jpg", "actImage17.jpg", "actImage18.jpg", "actImage19.jpg", "actImage20.jpg",
                "actImage21.jpg", "actImage22.jpg", "actImage23.png", "actImage24.png", "actImage25.png", "actImage26.png", "actImage27.png", "actImage28.png", "actImage29.png", "actImage30.png",
                "actImage31.png", "actImage32.png", "actImage33.png", "actImage34.png", "actImage35.png", "actImage36.png", "actImage37.png", "actImage38.png", "actImage39.png", "actImage40.png",
                "actImage41.png");
        Random rand = new Random();
        int n = rand.nextInt(actImgList.size());
        link = baseLink + actImgList.get(n);

        return link;
    }

}
