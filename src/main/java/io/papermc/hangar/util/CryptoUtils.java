package io.papermc.hangar.util;

import org.postgresql.shaded.com.ongres.scram.common.util.Preconditions;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {

    private CryptoUtils() { }

    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    public static byte[] hmac(String algo, byte[] secret, byte[] data) {
        Preconditions.checkArgument(secret.length != 0, "empty secret");
        Preconditions.checkArgument(data.length != 0, "nothing to hash!");
        try {
            Mac hmac = Mac.getInstance(algo);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algo);
            hmac.init(keySpec);
            return hmac.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String hmacSha256(String secret, byte[] data) {
        return bytesToHex(hmac("HmacSHA256", secret.getBytes(StandardCharsets.UTF_8), data));
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int j = 0;
        while (j < bytes.length) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];

            j++;
        }
        return new String(hexChars);
    }

    public static String md5ToHex(byte[] bytes) {
        try {
            return bytesToHex(MessageDigest.getInstance("MD5").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
