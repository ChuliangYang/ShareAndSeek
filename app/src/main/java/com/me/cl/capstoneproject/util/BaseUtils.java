package com.me.cl.capstoneproject.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

/**
 * Created by CL on 11/8/17.
 */

public class BaseUtils {
    public static String checkStringEmpty(Object s){
        if (s instanceof String) {
            if (!TextUtils.isEmpty((String) s)) {
                return (String) s;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static class Dialog{
        public static MaterialDialog showAlertDialog(String title, String content, String button, MaterialDialog.SingleButtonCallback singleButtonCallback, Context context){
            return new MaterialDialog.Builder(context)
                    .title(title)
                    .content(content)
                    .positiveText(button)
                    .onPositive(singleButtonCallback)
                    .show();
        }

        public static MaterialDialog controlIndeterminateProgress(boolean show,String title,String content,MaterialDialog indeterminateProgress,Context context){
            if (show) {
                if (indeterminateProgress == null) {
                    indeterminateProgress = new MaterialDialog.Builder(context).title(title).content(content)
                            .progress(true, 0).build();
                    indeterminateProgress.show();
                } else {
                    indeterminateProgress.show();
                }
                return  indeterminateProgress;
            } else {
                if (indeterminateProgress!=null&&indeterminateProgress.isShowing()) {
                    indeterminateProgress.dismiss();
                }
            }

            return indeterminateProgress;
        }
    }

    public static class System{
        public static boolean runtimePermissionCheck(Activity activity, String permission, int requestCode){
            if (activity==null||!(activity instanceof Activity)) {
                return false;
            }

            if (ContextCompat.checkSelfPermission(activity,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(activity,
                        new String[]{permission},
                        requestCode);


                return false;
            }

            return true;
        }

        public static boolean loginCheck(Activity activity,int requestCode){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                return true;
            } else {
                activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .build(),
                        requestCode);
                return false;
            }
        }

        public static boolean OnlyCheckIfLogin(){
            FirebaseAuth auth = FirebaseAuth.getInstance();
            return auth.getCurrentUser() != null;
        }
    }


}
