package com.me.cl.capstoneproject.anim;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Interpolator;

import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

/**
 * Created by CL on 1/10/18.
 */

public class ScaleFadeInAnimator extends ScaleInAnimator {
    public ScaleFadeInAnimator() {
        super();
    }

    public ScaleFadeInAnimator(Interpolator interpolator) {
        super(interpolator);
    }


    @Override protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .scaleX(0)
                .scaleY(0)
                .alpha(0)
                .setDuration(getRemoveDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start();
    }

    @Override protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 0);
        ViewCompat.setScaleY(holder.itemView, 0);

        ViewCompat.setAlpha(holder.itemView, 0);
    }

    @Override protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .scaleX(1)
                .scaleY(1)
                .setDuration(getAddDuration())
                .setInterpolator(mInterpolator)
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start();
        ViewCompat.animate(holder.itemView).alpha(1).setDuration(getAddDuration()*5/4)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder)+getAddDuration()/4).start();

    }
}
