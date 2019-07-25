package com.me.cl.capstoneproject.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by CL on 11/3/17.
 */

public class BaseActivity extends AppCompatActivity {

    MaterialDialog IndeterminateProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public  void showAlertDialog(String title, String content, String button, MaterialDialog.SingleButtonCallback singleButtonCallback,DialogInterface.OnCancelListener onCancelListener){
        new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .positiveText(button)
                .canceledOnTouchOutside(false)
                .cancelListener(onCancelListener)
                .onPositive(singleButtonCallback)
                .show();
    }

    public  void controlIndeterminateProgress(boolean show,String title,String content,DialogInterface.OnCancelListener onCancelListener){
        if (show) {
            if (IndeterminateProgress == null) {
                IndeterminateProgress = new MaterialDialog.Builder(this).title(title).content(content)
                        .progress(true, 0).canceledOnTouchOutside(false).cancelListener(onCancelListener).show();
            } else {
                IndeterminateProgress.show();
            }

        } else {
            if (IndeterminateProgress.isShowing()) {
                IndeterminateProgress.dismiss();
            }
        }
    }
}
