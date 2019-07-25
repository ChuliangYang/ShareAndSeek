package com.me.cl.capstoneproject.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by CL on 1/15/18.
 */

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    public CustomSwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child!=null&&!(child instanceof ImageView)) {
                    child.measure(0,0);
                    if(child.getMeasuredHeight()==0){
                        setMeasuredDimension(getMeasuredWidth(),getMeasuredHeight());
                    }else{
                        setMeasuredDimension(getMeasuredWidth(),child.getMeasuredHeight());
                    }
                }
            }
    }

}
