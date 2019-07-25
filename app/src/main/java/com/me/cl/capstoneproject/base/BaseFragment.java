package com.me.cl.capstoneproject.base;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by CL on 12/18/17.
 */

public class BaseFragment extends Fragment {
    MaterialDialog IndeterminateProgress;

    public  void showAlertDialog(String title, String content, String button, MaterialDialog.SingleButtonCallback singleButtonCallback,DialogInterface.OnCancelListener onCancelListener){
        new MaterialDialog.Builder(getContext())
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
                IndeterminateProgress = new MaterialDialog.Builder(getContext()).title(title).content(content)
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
