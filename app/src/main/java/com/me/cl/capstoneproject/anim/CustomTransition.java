//package com.me.cl.capstoneproject;
//
//import android.animation.Animator;
//import android.animation.ValueAnimator;
//import android.content.Context;
//import android.graphics.Rect;
//import android.os.Build;
//import android.support.annotation.RequiresApi;
//import android.transition.ChangeBounds;
//import android.transition.Transition;
//import android.transition.TransitionListenerAdapter;
//import android.transition.TransitionValues;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.ViewGroup;
//
//
///**
// * Created by CL on 1/19/18.
// */
//
//public class CustomTransition extends ChangeBounds {
//    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
//    private static final String PROPNAME_PARENT = "android:changeBounds:parent";
//
//
//    public CustomTransition() {
//        super();
//    }
//
//    public CustomTransition(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
////    @Override
////    public void captureStartValues(TransitionValues transitionValues) {
////        super.captureStartValues(transitionValues);
////    }
////
////    @Override
////    public void captureEndValues(TransitionValues transitionValues) {
////        super.captureEndValues(transitionValues);
////    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
//
//
////        Map<String, Object> startParentVals = startValues.values;
////        Map<String, Object> endParentVals = endValues.values;
////        ViewGroup startParent = (ViewGroup) startParentVals.get(PROPNAME_PARENT);
////        ViewGroup endParent = (ViewGroup) endParentVals.get(PROPNAME_PARENT);
////        if (startParent == null || endParent == null) {
////            return null;
////        }
//        final View view = endValues.view;
////        final View view = startValues.view;
//        Rect startBounds = (Rect) startValues.values.get(PROPNAME_BOUNDS);
//        Rect endBounds = (Rect) endValues.values.get(PROPNAME_BOUNDS);
//        final int startLeft = startBounds.left;
//        final int endLeft = endBounds.left;
//        final int startTop = startBounds.top;
//        final int endTop = endBounds.top;
//        final int startRight = startBounds.right;
//        final int endRight = endBounds.right;
//        final int startBottom = startBounds.bottom;
//        final int endBottom = endBounds.bottom;
//        final int startWidth = startRight - startLeft;
//        final int startHeight = startBottom - startTop;
//        final int endWidth = endRight - endLeft;
//        final int endHeight = endBottom - endTop;
//        ValueAnimator anim=null;
////        Reflect.on(view).call("setLeftTopRightBottom", startLeft, startTop, startRight, startBottom);
////        anim= ObjectAnimator.ofFloat(0,-500f);
////        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
////            @Override
////            public void onAnimationUpdate(ValueAnimator animation) {
////                view.setTranslationY((Float) animation.getAnimatedValue());
////            }
////
////        });
////        view.setVisibility(View.GONE);
//
//        ValueAnimator valueAnimator=ValueAnimator.ofInt(0,1);
//                valueAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                view.animate().yBy(-400).rotation(360).setDuration(1000);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
////                Reflect.on(view).call("setLeftTopRightBottom", endLeft, endTop, endRight, endBottom);
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        anim=valueAnimator;
//
////        final ViewGroup parent = (ViewGroup) view.getParent();
////        Reflect.on(parent).call("suppressLayout", true);
//
////        parent.suppressLayout(true);
//        TransitionListener transitionListener = new TransitionListenerAdapter() {
//            boolean mCanceled = false;
//
//            @Override
//            public void onTransitionCancel(Transition transition) {
////                Reflect.on(parent).call("suppressLayout", false);
//
////                parent.suppressLayout(false);
//                mCanceled = true;
//            }
//
//            @Override
//            public void onTransitionEnd(Transition transition) {
//                if (!mCanceled) {
////                    Reflect.on(parent).call("suppressLayout", false);
////                Reflect.on(view).call("setLeftTopRightBottom", endLeft, endTop, endRight, endBottom);
//
////                    parent.suppressLayout(false);
//                }
//                transition.removeListener(this);
//            }
//
//            @Override
//            public void onTransitionPause(Transition transition) {
////                Reflect.on(parent).call("suppressLayout", false);
//
////                parent.suppressLayout(false);
//            }
//
//            @Override
//            public void onTransitionResume(Transition transition) {
////                Reflect.on(parent).call("suppressLayout", true);
//
////                parent.suppressLayout(true);
//            }
//        };
//        addListener(transitionListener);
//        return  null;
//    }
//
//    @Override
//    public long getDuration() {
//        return 1;
//    }
//}
