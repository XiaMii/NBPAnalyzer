package com.example.nbpanalyzer.utils;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * Key类型密钥 与 String类型密钥之间自由转换
 */
public class GenKeyFromString {

    /**
     * 根据字符串类型的公钥生成  公钥key
     * @param pubKey   字符串类型的公钥
     * @return 公钥
     */
    public static PublicKey getPubKey(String pubKey) {
        PublicKey publicKey = null;
        try {

            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(pubKey));
            // RSA对称加密算法
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            publicKey = keyFactory.generatePublic(bobPubKeySpec);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    /**
     * 根据String形式的私钥     生成私钥Key
     * @param priKey   字符串类型的私钥
     * @return
     */
    public static PrivateKey getPrivateKey(String priKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec priPKCS8 = null;
        try {
            priPKCS8 = new PKCS8EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(priKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            privateKey = keyf.generatePrivate(priPKCS8);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * 根据Key转换成String类型
     *
     * @param key
     *            密钥Key (公或私钥)
     * @return 密钥Key的String类型
     */
    public static String getStringFromKey(Key key) {
        byte[] keyBytes = key.getEncoded();
        String key_String = (new BASE64Encoder()).encode(keyBytes);
        return key_String;
    }
}


