package com.example.bunny.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author someone
 */
public class DESUtils {

    public static String encrypt3DES(String key, String data) throws UtilException {
        byte[] byteKey = BytesUtil.hexString2Bytes(key);
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DESede");
        // �ɠ密
        try {
            if ((data.length() / 2) % 8 != 0) {
                data = data.concat(new String(new char[((8 - (data.length() / 2 % 8)) * 2 + data.length()) - data.length()]));
            }
            Cipher c = Cipher.getInstance("DESede/ECB/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, deskey);
            return BytesUtil.bytes2HexString(c.doFinal(BytesUtil.hexString2Bytes(data)));
        } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | InvalidKeyException
            | IllegalBlockSizeException
            | BadPaddingException e) {
            throw new UtilException(e);
        }
    }

    public static String encrypt3DES(String key, byte[] data) throws UtilException {
        byte[] byteKey = BytesUtil.hexString2Bytes(key);
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DESede");
        // �ɠ密
        try {
            Cipher c = Cipher.getInstance("DESede/ECB/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, deskey);
            return BytesUtil.bytes2HexString(c.doFinal(data));
        } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | InvalidKeyException
            | IllegalBlockSizeException
            | BadPaddingException e) {
            throw new UtilException(e);
        }
    }

    public static String decrypt3DES(String key, String data) throws UtilException {
        byte[] byteKey = BytesUtil.hexString2Bytes(key);
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DESede");
        // 解密
        try {
            Cipher c = Cipher.getInstance("DESede/ECB/NoPadding");
            c.init(Cipher.DECRYPT_MODE, deskey);
            return BytesUtil.bytes2HexString(c.doFinal(BytesUtil.hexString2Bytes(data)));
        } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | InvalidKeyException
            | IllegalBlockSizeException
            | BadPaddingException e) {
            throw new UtilException(e);
        }
    }

    /**
     * ���DES�ɠ密
     *
     * @param byteKey the key material of the secret key. The contents of
     *                the array are copied to protect against subsequent modification.
     * @param data    the input buffer
     * @return the new buffer with the result
     */
    public static byte[] des(byte[] byteKey, byte[] data) throws UtilException {
        //生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DES");
        try {
            Cipher c = Cipher.getInstance("DES/ECB/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, deskey);
            return c.doFinal(data);
        } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | InvalidKeyException
            | IllegalBlockSizeException
            | BadPaddingException e) {
            throw new UtilException(e);
        }
    }

    public static String des(String key, String dataString) throws UtilException {
        byte[] byteKey = BytesUtil.hexString2Bytes(key);
        byte[] data = BytesUtil.hexString2Bytes(dataString);
        //生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DES");
        //�ɠ密
        try {
            Cipher c = Cipher.getInstance("DES/ECB/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, deskey);
            return BytesUtil.bytes2HexString(c.doFinal(data));
        } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | InvalidKeyException
            | IllegalBlockSizeException
            | BadPaddingException e) {
            throw new UtilException(e);
        }
    }

    /**
     * ���DES解密
     *
     * @param key        the key material of the secret key.
     * @param dataString the input string data
     * @return the new string data with the result
     */
    public static String undes(String key, String dataString) throws UtilException {
        byte[] byteKey = BytesUtil.hexString2Bytes(key);
        byte[] data = BytesUtil.hexString2Bytes(dataString);
        //生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DES");
        //�ɠ密
        try {
            Cipher c = Cipher.getInstance("DES/ECB/NoPadding");
            c.init(Cipher.DECRYPT_MODE, deskey);
            return BytesUtil.bytes2HexString(c.doFinal(data));
        } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | InvalidKeyException
            | IllegalBlockSizeException
            | BadPaddingException e) {
            throw new UtilException(e);
        }
    }

    public static byte[] undes(byte[] byteKey, byte[] data) throws UtilException {
        //生成密钥
        SecretKey deskey = new SecretKeySpec(byteKey, "DES");
        //�ɠ密
        try {
            Cipher c = Cipher.getInstance("DES/ECB/NoPadding");
            c.init(Cipher.DECRYPT_MODE, deskey);
            return c.doFinal(data);
        } catch (NoSuchAlgorithmException
            | NoSuchPaddingException
            | InvalidKeyException
            | IllegalBlockSizeException
            | BadPaddingException e) {
            throw new UtilException(e);
        }
    }

    /**
     * 计算SHA-1
     *
     * @param data the array of bytes.
     * @return the array of bytes which calculate SHA-1
     */
    public static byte[] calcSHA1(byte[] data) throws UtilException {
        MessageDigest sha1Digest;
        try {
            sha1Digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new UtilException(e);
        }
        sha1Digest.update(data);
        return sha1Digest.digest();
    }

    /**
     * ���DES解密
     *
     * @param byteKey the key material of the secret key. The contents of
     *                the array are copied to protect against subsequent modification.
     * @param data    the input buffer
     * @param desType 0表示单DES�?表示3DES
     * @return the new buffer with the result
     */
    public static byte[] undes(byte[] byteKey, byte[] data, int desType) throws UtilException {
        if (0 == desType) {
            //单DES解密
            return undes(byteKey, data);
        } else {
            String undesStr;
            try {
                undesStr = decrypt3DES(new String(byteKey, "UTF-8"), new String(data, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new UtilException(e);
            }
            return undesStr == null ? null : undesStr.getBytes();
        }
    }

    /**
     * ���DES�ɠ密
     *
     * @param byteKey the key material of the secret key. The contents of
     *                the array are copied to protect against subsequent modification.
     * @param data    the input buffer
     * @param desType 0表示单DES�?表示3DES
     * @return the new buffer with the result
     */
    public static byte[] des(byte[] byteKey, byte[] data, int desType) throws UtilException {
        if (0 == desType) {
            //单DES解密
            return des(byteKey, data);
        } else {
            String desStr;
            try {
                desStr = encrypt3DES(new String(byteKey, "UTF-8"), data);
            } catch (UnsupportedEncodingException e) {
                throw new UtilException(e);
            }
            return desStr == null ? null : desStr.getBytes();
        }
    }


    /**
     * 字符串加密
     *
     * @param data  字符串
     * @param key 密钥
     * @return 加密结果字符串
     */
    public static String encryptString(String data, String key) throws UtilException {
        if(data == null || data.length() <= 0) {
            return data;
        }
        data = String.format("%04d%s", data.length(), data);
        int p = (data.length() % 8) != 0 ? 1 : 0;
        int lengthPadded = (data.length() / 8 + p) * 8;
        data = StringUtil.addRightPadding(data, '0', lengthPadded);
        byte[] encryptBytes = DESUtils.des(BytesUtil.toBytes(key), BytesUtil.toBytes(data));
        return BytesUtil.byteArray2HexString(encryptBytes);
    }

    /**
     * 字符串解密
     *
     * @param data  字符串
     * @param key 密钥
     * @return 解密结果字符串
     */
    public static String unencryptString(String data, String key) throws UtilException {
        if(data == null || data.length() <= 0) {
            return data;
        }
        byte[] unencryptBytes = DESUtils.undes(BytesUtil.toBytes(key), BytesUtil.hexString2Bytes(data));
        String outData = BytesUtil.fromBytes(unencryptBytes);
        int unencryptDataLen = Integer.parseInt(outData.substring(0, 4));
        return  outData.substring(4, 4 + unencryptDataLen);
    }
}
