package com.me.cl.capstoneproject.ui.list.mvp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcelable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.me.cl.capstoneproject.bean.CommercialItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CL on 11/29/17.
 */

public interface CommercialListView {
    void showList(List<CommercialItem> list,boolean animate,Boolean restore,Parcelable[] States);
    void showAlertDialog(String title, String content, String button, MaterialDialog.SingleButtonCallback singleButtonCallback, DialogInterface.OnCancelListener onCancelListener, Context context);
    void controlIndeterminateProgress(boolean show,String title,String content,DialogInterface.OnCancelListener onCancelListener,Context context,Boolean swipe);
    void restoreLastSortType(int sortType);
    void showSnackBar(String info);
    String getSpinnerTitle();
    ArrayList<CommercialItem> getListModel();
    Parcelable getRvState();
    Parcelable getSpinnerTitleState();
    Parcelable getSpinnerSortState();
    void restoreSpinner(Parcelable[] states);
    Integer getSpinnerTitlePosition();
    void setTitleSpinnerInitEnable(Boolean titleSpinnerListnerEnable);
}
