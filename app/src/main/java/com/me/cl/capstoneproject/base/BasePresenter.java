package com.me.cl.capstoneproject.base;

/**
 * Created by CL on 11/3/17.
 */

public interface BasePresenter<V> {
    void manage(V view);
    void destory();
    void init();
}
