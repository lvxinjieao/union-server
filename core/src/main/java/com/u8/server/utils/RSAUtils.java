package com.u8.server.utils;


import com.u8.server.log.Log;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 公钥加密》私钥解密
 * 私钥加密》公钥解密
 */
public class RSAUtils {

    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM_SHA = "SHA1WithRSA";
    public static final String SIGNATURE_SHA_512 = "SHA512WithRSA";
    public static final String SIGNATURE_SHA256WithRSA = "SHA256WithRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";


    /**
     * 使用公钥判断签名是否与数据匹配
     *
     * @param data       待签名数据
     * @param sign       签名值
     * @param public_key 支付公钥
     */
    public static boolean verify(String data, String sign, String public_key) throws Exception{
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decode = Base64.getDecoder().decode(public_key);
            Log.i("verify decode="+decode);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decode);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            java.security.Signature signature = java.security.Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
            signature.initVerify(publicKey);
            signature.update(data.getBytes("utf-8"));
            boolean verify = signature.verify(Base64.getDecoder().decode(sign));
            return verify;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 使用公钥判断签名是否与数据匹配
     *
     * @param data       待签名数据
     * @param sign       签名值
     * @param public_key 支付公钥
     */
    public static boolean verify(String data, String sign, String public_key, String charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decode = Base64.getDecoder().decode(public_key);
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decode));
            java.security.Signature signature = java.security.Signature.getInstance(SIGNATURE_ALGORITHM_MD5);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(charset));
            boolean verify = signature.verify(Base64.getDecoder().decode(sign));
            return verify;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 使用公钥判断签名是否与数据匹配
     *
     * @param data       待签名数据
     * @param sign       签名值
     * @param public_key 支付公钥
     * @param charset 编码
     * @param Signature 签名方式
     */
    public static boolean verify(String data, String sign, String public_key, String charset,String Signature) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decode = Base64.getDecoder().decode(public_key);
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(decode));
            java.security.Signature signature = java.security.Signature.getInstance(Signature);
            signature.initVerify(publicKey);
            signature.update(data.getBytes(charset));
            boolean verify = signature.verify(Base64.getDecoder().decode(sign));
            return verify;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 使用私钥对数据进行加密签名
     *
     * @param data 待签名数据
     * @param data 商户私钥
     */
    public static String sign(String data, String private_key) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(private_key)));
            Signature signet = Signature.getInstance("MD5withRSA");
            signet.initSign(privateKey);
            signet.update(data.getBytes("utf-8"));
            byte[] signed = signet.sign();
            byte[] str = Base64.getEncoder().encode(signed);
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 生成公钥和私钥
     */
    public static Map<String, Object> generateKeys() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 取得私钥
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return new String(Base64.getEncoder().encode(key.getEncoded()));
    }


    /**
     * 取得公钥
     */
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return new String(Base64.getEncoder().encode(key.getEncoded()));
    }


    /**
     * 使用公钥对数据进行加密签名
     *
     * @param str       字符串
     * @param publicKey 公钥
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] encode = Base64.getEncoder().encode(cipher.doFinal(str.getBytes("UTF-8")));
        return new String(encode);
    }


    /**
     * 使用私钥对数据进行解密签名
     *
     * @param str        字符串
     * @param privateKey 私钥
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////


    public static void main(String[] args) throws Exception {
//        Map<String, Object> keys = generateKeys();
//        String publickey = getPublicKey(keys);
//        String privateKey = getPrivateKey(keys);
//        System.out.println("公钥：" + pubKey);
//        System.out.println("私钥：" + priKey);


        String data = "tranNo=[2332047888886005760]&orderNo=[335583147812978688]&amount=[1.0]&transState=[S]";
        System.out.println("原始数据： " + data);



        String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBDDEtoNnCZDGoDD/JEE5MuUR35JqqS2fRKq1vT7hVxJBP1hU7XM9FdaTswLv14TeYcUnAZbM09h6bqi9q5RSeHCVjK4PqfXqpOFDQEamwt8Jb0L32qfvisUDRJ2RysZIe0wQ5V/cfqHkIf04m3gHkILbm+Ve4chf2ZhjCDQkyRwIDAQAB";
        System.out.println("公钥： " + publickey);

        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIEMMS2g2cJkMagMP8kQTky5RHfkmqpLZ9EqrW9PuFXEkE/WFTtcz0V1pOzAu/XhN5hxScBlszT2HpuqL2rlFJ4cJWMrg+p9eqk4UNARqbC3wlvQvfap++KxQNEnZHKxkh7TBDlX9x+oeQh/TibeAeQgtub5V7hyF/ZmGMINCTJHAgMBAAECgYBVynYzQFkka5m7f5E0KRv008IZ9qeACStWmgm9E4GXu1q5SLjKwzOkmMZxAtyyZOyh8sa2GqZd0jWdBTIK3YqrcirbBiqQgg5Uty2nQ3kbWGAa99zJGPtehE+oxWFZzK+q7S4jZ6SHw9KoBgsWSLoof/zPE1HY6/ZmCbu0VBy3WQJBANGHah92hEMV0eaEMZJSfd7s5qpN3lEp/cVqpIK6BykGukfsbjUX7QiopjmWWrJ6dKQPLQ7h70E0fmkHFJsG120CQQCdqzo1eQt0wK+jPyD3qWs7ztbqrt2n1ASv9jneiP3u2yD3pZVW2VlX4vnBqP0fNHyDOPHYo7KPChuIde9TP9wDAkEAs+1LDlWh4lHce81NHE/GUyawNdLEdIJQr7SSLMg+2dYzJQw83076d5MLmywoGlfZzgBSOxN9d0ryupIf+bh4SQJAenb9B3u1gkIIKmxmVveo6xOFq1OXpzvvSB2gZVFSq6xYtwJONN8tni4WgG2Z7hr+e45Hi1Xa42+eYyf3dFotsQJBAILt3jebGxXvi1GvS+e0mM7dC4N2UBldYOcv1upkAWMIBvpW5NtZmRLpj2eQs+TxyRL32KSduynCTxig9JPovRY=";
        System.out.println("私钥： " + privateKey);

//        String encrypt = encrypt(data, pubKey);
//        System.out.println("公钥加密数据：" + encrypt);
//
//        String decrypt = decrypt(encrypt, priKey);
//        System.out.println("私钥解密数据：" + decrypt);

//        eUjurHlfpbWxGRnCIWjAvLcrC84cSRsgl3zJcLcRvuMjG15VEf9RNW3/EvieVaoqNdQ2MFzRem0NmrtfFxMQmizcR7boK1Mre/erIkMoaph4YcVCsg s6LysvAWhQ BOESiHv4pSSSvodtgZ4BoZzHnx5nxMGnZ08ZJilWQVpz4=
//        eUjurHlfpbWxGRnCIWjAvLcrC84cSRsgl3zJcLcRvuMjG15VEf9RNW3/EvieVaoqNdQ2MFzRem0NmrtfFxMQmizcR7boK1Mre/erIkMoaph4YcVCsg+s6LysvAWhQ+BOESiHv4pSSSvodtgZ4BoZzHnx5nxMGnZ08ZJilWQVpz4=

        String sign = "eUjurHlfpbWxGRnCIWjAvLcrC84cSRsgl3zJcLcRvuMjG15VEf9RNW3/EvieVaoqNdQ2MFzRem0NmrtfFxMQmizcR7boK1Mre/erIkMoaph4YcVCsg+s6LysvAWhQ+BOESiHv4pSSSvodtgZ4BoZzHnx5nxMGnZ08ZJilWQVpz4=";
        System.out.println("签名: " + sign);

        String s = sign(data, privateKey);
        System.out.println("签名: " + s);

        System.out.println("匹配: " + s.equals(sign));

        System.out.println("签名验证结果: " + verify(data, sign, publickey));

    }

}
