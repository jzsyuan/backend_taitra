package utils;

import play.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * Created by ethanlin on 18/11/2017.
 */
public class AESEncrypt
{
    public static String byte2str(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 2);
        int i = 0;
        while (i < paramArrayOfByte.length)
        {
            if ((paramArrayOfByte[i] & 0xFF) < 16) {
                localStringBuffer.append("0");
            }
            localStringBuffer.append(Long.toString(paramArrayOfByte[i] & 0xFF, 16));
            localStringBuffer.append(" ");
            i += 1;
        }
        return localStringBuffer.toString();
    }


    public static byte[] encryptAES(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    {
        try
        {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(paramArrayOfByte1);
            SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfByte2, "AES");
            Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            localCipher.init(ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            paramArrayOfByte1 = localCipher.doFinal(paramArrayOfByte3);
            return paramArrayOfByte1;
        }
        catch (Exception e) {}
        return null;
    }

    public static byte[] encryptSHA384(byte[] paramArrayOfByte)
    {
        return hashTemplate(paramArrayOfByte, "SHA-384");
    }

    public static String encryptSHA384ToString(String paramString)
    {
        return encryptSHA384ToString(paramString.getBytes());
    }

    public static String encryptSHA384ToString(byte[] paramArrayOfByte)
    {
        return byte2str(encryptSHA384(paramArrayOfByte));
    }

    private static byte[] hashTemplate(byte[] paramArrayOfByte, String paramString)
    {
        if ((paramArrayOfByte == null) || (paramArrayOfByte.length <= 0)) {
            return null;
        }
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance(paramString);
            messageDigest.update(paramArrayOfByte);
            paramArrayOfByte = messageDigest.digest();
            return paramArrayOfByte;
        }
        catch (NoSuchAlgorithmException e)
        {
            Logger.error("", e);
        }
        return null;
    }

    public static byte[] str2byte(String paramString)
    {
        byte[] arrayOfByte = new byte[paramString.length() / 2];
        int i = 0;
        int k;
        for (int j = 0; i < paramString.length() + 2; j = k)
        {
            k = j;
            if (i != 0)
            {
                arrayOfByte[j] = ((byte)Integer.parseInt(paramString.charAt(i - 2) + "" + paramString.charAt(i - 1), 16));
                k = j + 1;
            }
            i += 2;
        }
        return arrayOfByte;
    }
}