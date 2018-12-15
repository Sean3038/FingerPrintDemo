package com.example.ffes.fingerprintdemo.model;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoModel {

    private static String KEYSTORE_ALIAS = "MyKey";
    private static String ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    private static String PADDING_MODE = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static String TRANSFORMATION =
            ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING_MODE;

    private Cipher cipher;
    private CryptoStore store;

    public CryptoModel(CryptoStore store) {
        this.store = store;
    }

    private void prepareEncrypt() {
        try {
            SecretKey key = store.getKey(KEYSTORE_ALIAS, ALGORITHM, BLOCK_MODE, PADDING_MODE);
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            store.saveIV(cipher.getIV());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareDecrypt() {
        try {
            SecretKey key = store.getKey(KEYSTORE_ALIAS, ALGORITHM, BLOCK_MODE, PADDING_MODE);
            cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(store.getIV()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String doEncrypt(byte[] data) {
        String str = null;
        try {
            byte[] result = cipher.doFinal(data);
            str = Base64.encodeToString(result, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private byte[] doDecrypt(String data) {
        byte[] result = null;
        try {
            result = cipher.doFinal(Base64.decode(data, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String encrypt(String data) {
        prepareEncrypt();
        return doEncrypt(data.getBytes());
    }

    public String decrypt(String data) {
        prepareDecrypt();
        return new String(doDecrypt(data));
    }

    interface CryptoStore {
        void saveIV(byte[] iv);

        byte[] getIV();

        SecretKey getKey(String alias, String algorithm, String blockMode, String paddingMode);
    }
}
