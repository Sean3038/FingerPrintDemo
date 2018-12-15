package com.example.ffes.fingerprintdemo.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;

public class FingerPrintModel {

    public static boolean isSupportFingerAuthenticate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }

    public static boolean isHardwareDetected(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fpm = context.getSystemService(FingerprintManager.class);
            if (fpm.isHardwareDetected()) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasEnrolledFingerprints(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fpm = context.getSystemService(FingerprintManager.class);
            if (fpm.hasEnrolledFingerprints()) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void authenticate(Context context, @NonNull final CallBack callBack) {
        if (isSupportFingerAuthenticate()) {
            FingerprintManager fpm = context.getSystemService(FingerprintManager.class);
            CancellationSignal cancellationSignal=new CancellationSignal();
            cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                @Override
                public void onCancel() {
                    callBack.onCancel();
                }
            });
            fpm.authenticate(null, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    callBack.onFail("02", errString.toString());
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    callBack.onFail("02", helpString.toString());
                }

                @Override
                public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    callBack.onSuccess();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    callBack.onFail("02", "找不到指紋資料");
                }
            }, null);
        } else {
            callBack.onFail("02", "不支援指紋辨識");
        }
    }

    public interface CallBack {
        void onSuccess();
        void onFail(String errorCode, String message);
        void onCancel();
    }
}
