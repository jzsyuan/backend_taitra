package utils;

import org.apache.xerces.impl.dv.util.Base64;
import play.Logger;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Formatter;


/**
 * Created with IntelliJ IDEA.
 * User: jiunhonglin
 * Date: 13/8/12
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class Crypto {
    public static String md5(String input) {
        String hexString = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            hexString = (new HexBinaryAdapter()).marshal(md.digest(input.getBytes()));
        } catch (Exception e) {

        }
        return hexString.toLowerCase();
    }

    public static String sha256(String input) {
        String hexString = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hexString = (new HexBinaryAdapter()).marshal(md.digest(input.getBytes()));
        } catch (Exception e) {

        }
        return hexString.toLowerCase();
    }

    public static String base64Decode(String input) {
        byte[] result = Base64.decode(input);

        if (result == null) {
            return null;
        }

        return new String(result);
    }

    public static String base64Encode(String input) {
        return Base64.encode(input.getBytes());
    }

    private static Key generateKey(String encryptKey) throws Exception {
        Key key = new SecretKeySpec(encryptKey.getBytes(), "AES");
        return key;
    }

    private static byte[] hexStringToByteArray(String s) {
        s = s.toUpperCase();
        int len = s.length();
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            data[i] = (byte) Character.digit(s.charAt(i), 16);
        }
        return data;
    }

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    public static String calculateRFC2104HMAC(String data, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            return Base64.encode(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            Logger.error("calculateRFC2104HMAC error", e);
        }
        return "";
    }

    public static String calculateHmacSha1Base64(String data, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            String result = Base64.encode(rawHmac);

            return result;
        } catch (GeneralSecurityException e) {
            //LOG.warn("Unexpected error while creating hash: " + e.getMessage(),	e);
            throw new IllegalArgumentException();
        }
    }

    public static String calculateHmacSha256Base64(String data, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            String result = Base64.encode(rawHmac);

            return result;
        } catch (GeneralSecurityException e) {
            //LOG.warn("Unexpected error while creating hash: " + e.getMessage(),	e);
            throw new IllegalArgumentException();
        }
    }
}
