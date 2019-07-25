package com.me.cl.capstoneproject.ui.main;

import android.content.Context;

import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.di.ActivityQL;
import com.me.cl.capstoneproject.ui.main.mvp.MainInteractor;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by CL on 11/1/17.
 */

public class MainInteractorImpl implements MainInteractor {

    private Context context;

    private HashMap<String,Object> state=new HashMap();

    @Inject
    public MainInteractorImpl(@ActivityQL Context context) {
        this.context=context;
    }

    @Override
    public String[] getAllDistricts() {
        return context.getResources().getStringArray(R.array.district_list);
    }

    @Override
    public String[] getAllTags() {
        return context.getResources().getStringArray(R.array.main_tags);
    }

    public void saveCurrentState(String key, Object value){
        state.put(key,value);
    }

    public Object getCurrentState(String key){
        if (state.containsKey(key)) {
            return state.get(key);
        }
        return null;
    }

    public HashMap<String, Object> getState() {
        return state;
    }
}
