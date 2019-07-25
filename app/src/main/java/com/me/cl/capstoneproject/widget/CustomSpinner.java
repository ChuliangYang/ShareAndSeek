package com.me.cl.capstoneproject.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListPopupWindow;
import android.widget.Spinner;

import java.lang.reflect.Field;

/**
 * Created by CL on 1/3/18.
 */

public class CustomSpinner extends Spinner {
    onDropDownVisibleChangeListener onDropDownVisibleChangeListener;

    public CustomSpinner(Context context) {
        super(context);
    }

    public CustomSpinner(Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
    }


    @Override
    public boolean performClick() {
        boolean handled=super.performClick();
        if(onDropDownVisibleChangeListener!=null){
            try {
                Class  aClass = Spinner.class;
                Field field = aClass.getDeclaredField("mPopup");//私有变量t的引用名称
                field.setAccessible(true);//一定要设置成可访问

                ((ListPopupWindow)field.get(this)).getListView().addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                            onDropDownVisibleChangeListener.onPopUp(v);
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                            onDropDownVisibleChangeListener.onDismiss(v);
                        removeOnAttachStateChangeListener(this);
                    }
                });
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }


        return handled;
    }

    public interface onDropDownVisibleChangeListener{
         void onPopUp(View v);
         void onDismiss(View v);
    }

    public CustomSpinner.onDropDownVisibleChangeListener getOnDropDownVisibleChangeListener() {
        return onDropDownVisibleChangeListener;
    }

    public void setOnDropDownVisibleChangeListener(CustomSpinner.onDropDownVisibleChangeListener onDropDownVisibleChangeListener) {
        this.onDropDownVisibleChangeListener = onDropDownVisibleChangeListener;
    }

}
