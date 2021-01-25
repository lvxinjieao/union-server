package com.u8.server.sdk.samsung.api;
 

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class EncryptUtil
{
	public static String Hex2String(byte[] data)
	{
	    StringBuilder hex = new StringBuilder(data.length * 2);
	    for (byte b : data) {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }
	    return hex.toString();
	}
	
	public static String md5(String string) {
	    byte[] hash;
	    try {
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Huh, MD5 should be supported?", e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);
	    }
	    
	    return Hex2String(hash);
	}
	
	/** 
     * Base64解码 
     * @param key 
     * @return 
     */  
    public static byte[] decryptBASE64(String key){  
        return Base64_Android.decode(key, Base64_Android.NO_WRAP);  
    }  
      
    /** 
     * Base64编码 
     * @param sign 
     * @return 
     */  
    public static String encryptBASE64(byte[] key){  
        return Base64_Android.encodeToString(key, Base64_Android.NO_WRAP);  
    }
}