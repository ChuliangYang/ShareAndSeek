package com.me.cl.capstoneproject.ui.main;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;

/**
 * Created by CL on 1/27/18.
 */
@RunWith(RobolectricTestRunner.class)
public class MainInteractorImplTest {
    MainInteractorImpl mainInteractor;
    Context context;

    @Before
    public void setUp(){
        //输出日志
//        ShadowLog.stream = System.out;
        context= RuntimeEnvironment.application;
        mainInteractor=new MainInteractorImpl(context);
    }

    @Test
    public void testGetAllDistricts() throws Exception {
        assertThat(Arrays.asList(mainInteractor.getAllDistricts()),hasItems("New York","Manhattan","Brooklyn","Queens","Bronx","Staten Island"));
    }

    @Test
    public void testGetAllTags() throws Exception {
        assertThat(Arrays.asList(mainInteractor.getAllTags()),hasItems("Help","Free","Commercial"));
    }

    @Test
    public void testSaveCurrentState() throws Exception{
        String key="key";
        Object value=new Object();
        mainInteractor.saveCurrentState(key,value);
        assertThat(mainInteractor.getState(),hasEntry(key,value));
        mainInteractor.getState().clear();


        String key1="key1";
        Object value1=null;
        mainInteractor.saveCurrentState(key1,value1);
        assertThat(mainInteractor.getState(),hasEntry(key1,value1));
        mainInteractor.getState().clear();


        String key2=null;
        Object value2="value2";
        mainInteractor.saveCurrentState(key2,value2);
        assertThat(mainInteractor.getState(),hasEntry(key2,value2));
        mainInteractor.getState().clear();


        String key3=null;
        Object value3=null;
        mainInteractor.saveCurrentState(key3,value3);
        assertThat(mainInteractor.getState(),hasEntry(key3,value3));

    }

    @Test
    public void testGetCurrentState() throws Exception {
        String key="key";
        Object value=new Object();
        mainInteractor.saveCurrentState(key,value);
        assertThat(mainInteractor.getCurrentState(key),equalTo(value));
        mainInteractor.getState().clear();

        String key1="key1";
        Object value1=null;
        mainInteractor.saveCurrentState(key1,value1);
        assertThat(mainInteractor.getCurrentState(key1),equalTo(value1));
        mainInteractor.getState().clear();


        String key2=null;
        Object value2="value2";
        mainInteractor.saveCurrentState(key2,value2);
        assertThat(mainInteractor.getCurrentState(key2),equalTo(value2));
        mainInteractor.getState().clear();


        String key3=null;
        Object value3=null;
        mainInteractor.saveCurrentState(key3,value3);
        assertThat(mainInteractor.getCurrentState(key3),equalTo(value3));
    }

}
