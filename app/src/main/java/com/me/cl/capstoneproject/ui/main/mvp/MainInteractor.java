package com.me.cl.capstoneproject.ui.main.mvp;

/**
 * Created by CL on 11/1/17.
 */

public interface MainInteractor {
    String[] getAllDistricts();
    String[] getAllTags();
    void saveCurrentState(String key, Object value);
    Object getCurrentState(String key);
}
