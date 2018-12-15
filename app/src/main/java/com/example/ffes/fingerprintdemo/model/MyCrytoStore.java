package com.example.ffes.fingerprintdemo.model;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.KeyStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class MyCrytoStore implements CryptoModel.CryptoStore {

    private static String KEY_IV = "ivkey";
    private static String KEYSTORE_PROVIDER = "AndroidKeyStore";


    SharedPreferences sp;

    public MyCrytoStore(SharedPreferences sp) {
        this.sp = sp;
    }

    @Override
    public void saveIV(byte[] iv) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_IV, Base64.encodeToString(iv, Base64.DEFAULT));
        editor.commit();
    }

    @Override
    public byte[] getIV() {
        String str = sp.getString(KEY_IV, null);
        return Base64.decode(str, Base64.DEFAULT);
    }

    @Override
    public SecretKey getKey(String alias, String algorithm, String blockMode, String paddingMode) {
        try {
            if (!checkKey(alias)) {
                generateKey(alias, algorithm, blockMode, paddingMode);
            }
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);
            return (SecretKey) keyStore.getKey(alias, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey(String alias, String algorithm, String blockMode, String paddingMode) {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator
                    .getInstance(algorithm, KEYSTORE_PROVIDER);


            keyGenerator.init(
                    new KeyGenParameterSpec
                            .Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(paddingMode)
                            .setBlockModes(blockMode)
                            .setRandomizedEncryptionRequired(true)
                            .build());

            keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkKey(String alias) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER);
            keyStore.load(null);
            return keyStore.containsAlias(alias);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
