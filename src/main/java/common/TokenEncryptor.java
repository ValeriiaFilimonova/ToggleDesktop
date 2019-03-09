package common;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class TokenEncryptor {
    private static StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

    static {
        encryptor.setPassword("mew mew");
    }

    public static String encrypt(String string) {
        return encryptor.encrypt(string);
    }

    public static String decrypt(String string) {
        return encryptor.decrypt(string);
    }
}
