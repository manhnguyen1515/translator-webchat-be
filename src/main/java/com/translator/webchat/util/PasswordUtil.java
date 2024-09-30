package com.translator.webchat.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class PasswordUtil {

    private static String hashAlgorithm; // Static field to hold the hashing algorithm
    private static String salt;

    @Value("${instance.hash.password}") // Injecting the value from application.properties
    private String hashAlgorithmValue;
    @Value("${password.salt}") // Injecting the salt value from application.properties
    private String saltValue;

    @PostConstruct
    private void init() {
        PasswordUtil.hashAlgorithm = this.hashAlgorithmValue;
        PasswordUtil.salt = this.saltValue;
        System.out.println("Hash Algorithm: " + hashAlgorithm); // Print out the value for verification
        System.out.println("Salt: " + salt); // Print out the salt value for verification
    }

    public static String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public static byte[] getSalt() {
        //System.out.println(Base64.getDecoder().decode(salt)); // Print out the salt value for verification
        return Base64.getDecoder().decode(salt);

    }

//    public static byte[] generateSalt() throws NoSuchAlgorithmException {
//        SecureRandom random = SecureRandom.getInstanceStrong();
//        byte[] salt = new byte[16];
//        random.nextBytes(salt);
//        return salt;
//    }

    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(getHashAlgorithm());
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    public static HashedPassword hashAndSaltPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = getSalt();
        String hashedPassword = hashPassword(password, salt);
        return new HashedPassword(hashedPassword, Base64.getEncoder().encodeToString(salt));
    }

    public static boolean verifyPassword(String originalPassword, String storedHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = getSalt();
        String newHash = hashPassword(originalPassword, salt);
        return newHash.equals(storedHash);
    }

    public static boolean isPasswordMatch(String oldPassword, String newPassword) {
        return oldPassword.equals(newPassword);
    }

//    public static void main(String[] args) {
//        try {
//            String password = "mySecurePassword";
//            HashedPassword hashedPassword = hashAndSaltPassword(password);
//
//            System.out.println("Salt: " + hashedPassword.getSalt());
//            System.out.println("Hashed Password: " + hashedPassword.getHashedPassword());
//
//            // Verify password
//            boolean isPasswordCorrect = verifyPassword("mySecurePassword", hashedPassword.getHashedPassword(), hashedPassword.getSalt());
//            System.out.println("Password verification: " + isPasswordCorrect);
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//    }
}
