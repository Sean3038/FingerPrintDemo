package com.example.ffes.fingerprintdemo.model;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffes.fingerprintdemo.R;

public class FingerPrintDialog extends DialogFragment {

    private static long REFRESHDELAY = 3 * 1000;

    TextView mMessage;
    Button cancel;
    ImageView icon;
    Context context;

    FingerPrintCallback callback;

    FingerPrintModel.CallBack modelCallback = new FingerPrintModel.CallBack() {
        @Override
        public void onSuccess() {
            showSuccess();
        }

        @Override
        public void onFail(String errorCode, String message) {
            showErrorMessage(errorCode, message);
        }

        @Override
        public void onCancel() {
            close();
        }
    };

    Handler handler = new Handler(Looper.getMainLooper());
    Runnable refresh = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };
    Runnable close = new Runnable() {
        @Override
        public void run() {
            close();
        }
    };

    public static FingerPrintDialog getInstance(FingerPrintCallback callback) {
        FingerPrintDialog fragment = new FingerPrintDialog();
        fragment.setCallback(callback);
        return fragment;
    }

    private void setCallback(FingerPrintCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_finger_print, container, false);
        mMessage = view.findViewById(R.id.message);
        cancel = view.findViewById(R.id.cancel);
        icon = view.findViewById(R.id.verify_icon);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onCancel();
                }
                dismiss();
            }
        });

        checkFingerPrintSupport();
        return view;
    }

    public void showErrorMessage(String errorCode, String message) {
        handler.postDelayed(refresh, REFRESHDELAY);
        if (isAdded()) {
            mMessage.setText(message);
            icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_error));
            if (callback != null) {
                callback.onFail(errorCode, message);
            }
        }
    }

    public void showNoSupport() {
        if (isAdded()) {
            mMessage.setText("不支援指紋辨識");
            icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_error));
            if (callback != null) {
                callback.onFail("02", "不支援指紋辨識");
            }
        }
    }

    public void showSuccess() {
        handler.postDelayed(close, 1 * 1000);
        if (isAdded()) {
            mMessage.setText("辨識成功");
            icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_check_confirm));
            if (callback != null) {
                callback.onSuccess();
            }
        }
    }

    private void refresh() {
        if (isAdded()) {
            mMessage.setText("將手指置於感應器上");
            icon.setImageDrawable(getResources().getDrawable(R.drawable.drawable_finger_print));
        }
    }

    private void close() {
        dismiss();
    }


    private void checkFingerPrintSupport() {
        if (!FingerPrintModel.isSupportFingerAuthenticate()) {
            showNoSupport();
        } else if (!FingerPrintModel.isHardwareDetected(context)) {
            showErrorMessage("02", "無法連接辨識裝置");
        } else if (!FingerPrintModel.hasEnrolledFingerprints(context)) {
            showErrorMessage("02", "請至裝置設定指紋");
        } else {
            FingerPrintModel.authenticate(context, modelCallback);
        }
    }

    public interface FingerPrintCallback {
        void onSuccess();

        void onCancel();

        void onFail(String errorCode, String message);
    }
}
