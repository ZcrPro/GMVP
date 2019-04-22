package com.zhihuianxin.secure;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Vincent on 2016/11/1.
 */

public class SecureLocal {

    public static byte[] signMessage(byte[] data) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }
        return hash;
    }
}
