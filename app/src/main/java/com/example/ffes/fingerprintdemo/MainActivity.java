package com.example.ffes.fingerprintdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ffes.fingerprintdemo.model.CryptoModel;
import com.example.ffes.fingerprintdemo.model.FingerPrintDialog;
import com.example.ffes.fingerprintdemo.model.MyCrytoStore;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText editText;
    TextView result;

    FingerPrintDialog fragment;
    CryptoModel model;

    boolean isEncrypt = false;

    FingerPrintDialog.FingerPrintCallback callback = new FingerPrintDialog.FingerPrintCallback() {
        @Override
        public void onSuccess() {
            editText.setEnabled(false);
            if (isEncrypt) {
                setdata(model.decrypt(getdata()));
            } else {
                setdata(model.encrypt(getdata()));
            }
            isEncrypt = !isEncrypt;
        }

        @Override
        public void onCancel() {
            fragment.dismiss();
        }

        @Override
        public void onFail(String errorCode, String message) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.data_pane);
        result = findViewById(R.id.result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        model = new CryptoModel(new MyCrytoStore(MainActivity.this.getSharedPreferences(Const.Prefs_NAME, Context.MODE_PRIVATE)));
    }

    private void openDialog() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            Fragment fragment = fm.findFragmentByTag(FingerPrintDialog.class.getSimpleName());
            if (fragment != null) {
                fm.beginTransaction().remove(fragment).commit();
            }
        }
        FingerPrintDialog dialog = FingerPrintDialog.getInstance(callback);
        dialog.show(getFragmentManager(), FingerPrintDialog.class.getSimpleName());
        fragment = dialog;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT);
    }

    private String getdata() {
        if (result.getText().toString().length() > 0) {
            return result.getText().toString();
        }
        return editText.getText().toString();
    }

    private void setdata(String data) {
        result.setText(data);
    }
}
