package com.u8.server.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/5/11/011.
 */
public class SignUtils {

    public static String md5(String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffers = md.digest(name.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < buffers.length; i++) {
                String s = Integer.toHexString(0xff & buffers[i]);
                if (s.length() == 1) {
                    sb.append("0" + s);
                }
                if (s.length() != 1) {
                    sb.append(s);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
