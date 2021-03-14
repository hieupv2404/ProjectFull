//package com.nuce.group3.utils;
//
//import org.apache.commons.codec.digest.DigestUtils;
//
//public class PasswordUtils {
//    private static PasswordUtils instance;
//
//    private String randomHash;
//
//    private PasswordUtils() {
//    }
//
//    public static PasswordUtils getInstance() {
//        if (instance == null)
//            instance = new PasswordUtils();
//        return instance;
//    }
//
//    public void setRandomHash(String randomHash) {
//        this.randomHash = randomHash;
//    }
//
//    public String getHash(String input) {
//        return DigestUtils.md5Hex((input + randomHash).toUpperCase());
//    }
//}
