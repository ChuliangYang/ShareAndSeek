package com.me.cl.capstoneproject.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.base.Constant;
import com.me.cl.capstoneproject.event.ForeGroundEvent;
import com.me.cl.capstoneproject.event.LoginEvent;
import com.me.cl.capstoneproject.event.TitleChangeEvent;
import com.me.cl.capstoneproject.ui.main.mvp.MainInteractor;
import com.me.cl.capstoneproject.ui.main.mvp.MainView;
import com.me.cl.capstoneproject.ui.upload.commercial.CommercialUploadActivity;
import com.me.cl.capstoneproject.util.BaseUtils;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import static android.app.Activity.RESULT_OK;
import static com.me.cl.capstoneproject.base.Constant.MainPage.REQUEST_CODE_COMMERCIAL_CREATE_SIGN_IN;
import static com.me.cl.capstoneproject.base.Constant.MainPage.STATE_KEY_CURRENT_TAG;
import static com.me.cl.capstoneproject.base.Constant.MainPage.STATE_KEY_CURRENT_TAG_TITLE;
import static com.me.cl.capstoneproject.base.Constant.MainPage.STATE_KEY_CURRENT_TAG_TITLE_POSITION;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by CL on 1/26/18.
 */
@PrepareForTest(MainPresenterImpl.class)
public class MainPresenterImplTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    MainView mainView;
    @Mock
    MainInteractor mainInteractor;
    @InjectMocks
    MainPresenterImpl mainPresenterImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mainPresenterImpl.setMainView(mainView);
    }

    @Test
    public void testInit() throws Exception {
        MainPresenterImpl spy= PowerMockito.spy(mainPresenterImpl);
        doNothing().when(spy).configDistrictsSpinner();
        doNothing().when(spy).configViewPagerWithTab();

        spy.init();
        verify(spy).configDistrictsSpinner();
        verify(spy).configViewPagerWithTab();
    }

    @Test
    public void testConfigDistrictsSpinner() throws Exception {
        when(mainInteractor.getAllDistricts()).thenReturn(new String[]{"city1","city2"});

        mainPresenterImpl.configDistrictsSpinner();

        verify(mainView).popSpinnerWithDistricts(mainInteractor.getAllDistricts());
    }

    @Test
    public void testConfigViewPagerWithTab() throws Exception {
        when(mainInteractor.getAllTags()).thenReturn(new String[]{"tag1","tag2"});

        mainPresenterImpl.configViewPagerWithTab();

        verify(mainView).initViewPagerWithTabLayout(mainInteractor.getAllTags());
        verify(mainInteractor).saveCurrentState(STATE_KEY_CURRENT_TAG, 0);
        verify(mainInteractor).saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE, "New York");
        verify(mainInteractor).saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION, 0);

    }

    @Test
    public void testOnViewPageSelected() throws Exception {
        int position=0;
        String title="New York";
        int titlePosition=0;
        EventBus eventBus=PowerMockito.spy(EventBus.getDefault());
        mainPresenterImpl.eventBus=eventBus;
        ArgumentCaptor<ForeGroundEvent.MainPageTAG> argument = ArgumentCaptor.forClass(ForeGroundEvent.MainPageTAG.class);
        doNothing().when(eventBus).postSticky((argument.capture()));

        mainPresenterImpl.onViewPageSelected(position, title, titlePosition);

        verify(mainInteractor).saveCurrentState(STATE_KEY_CURRENT_TAG, position);
        verify(mainInteractor).saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE, title);
        verify(mainInteractor).saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION, titlePosition);
        assertEquals(argument.getValue().CurrentTitlePosition,titlePosition);
        assertEquals(argument.getValue().CurrentTitle,title);
        assertEquals(argument.getValue().CurrentTagPosition,position);
    }

    @Test
    @PrepareForTest({BaseUtils.System.class})
    public void testOnFabCreateClick() throws Exception {
        View rootview=mock(View.class);
        View view=mock(View.class);
        Activity context=mock(Activity.class);
        when(mainInteractor.getCurrentState(STATE_KEY_CURRENT_TAG)).thenReturn(0);
        when(rootview.getWidth()).thenReturn(1000);
        when(rootview.getHeight()).thenReturn(2000);
        when(view.getWidth()).thenReturn(100);

        PowerMockito.mockStatic(BaseUtils.System.class);
        Mockito.when(BaseUtils.System.loginCheck(context,REQUEST_CODE_COMMERCIAL_CREATE_SIGN_IN)).thenReturn(true);
        mainPresenterImpl.onFabCreateClick(context, view, rootview);
        verify(mainView).startUploadActivity(eq(CommercialUploadActivity.class),eq(500),eq(1000),eq(50),anyInt());

    }

    @Test
    public void testOnStart() throws Exception {
        when(mainInteractor.getCurrentState(STATE_KEY_CURRENT_TAG)).thenReturn(0);
        when(mainInteractor.getCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION)).thenReturn(0);
        when(mainInteractor.getCurrentState(STATE_KEY_CURRENT_TAG_TITLE)).thenReturn("new york");
        EventBus eventBus=mock(EventBus.class);
        ArgumentCaptor<ForeGroundEvent.MainPageTAG> argument = ArgumentCaptor.forClass(ForeGroundEvent.MainPageTAG.class);

        mainPresenterImpl.eventBus=eventBus;
        mainPresenterImpl.onStart();

        verify(eventBus).postSticky(argument.capture());
        assertEquals(argument.getValue().CurrentTagPosition,0);
        assertEquals(argument.getValue().CurrentTitle,"new york");
        assertEquals(argument.getValue().CurrentTitlePosition,0);
    }

    @Test
    public void testOnTitleChange() throws Exception {
        EventBus eventBus=mock(EventBus.class);
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);

        mainPresenterImpl.eventBus=eventBus;
        mainPresenterImpl.onTitleChange("title", 0, Constant.TITLE_CHANGE_FROM_MAIN);

        verify(eventBus,times(2)).postSticky(argument.capture());
        assertEquals(2,argument.getAllValues().size());
        assertTrue(argument.getAllValues().get(0) instanceof TitleChangeEvent.FromMainActivity);
        assertTrue(argument.getAllValues().get(1) instanceof TitleChangeEvent.ToMainPageTags);
        assertEquals(((TitleChangeEvent.FromMainActivity)(argument.getAllValues().get(0))).NewPosition,0);
        assertEquals(((TitleChangeEvent.FromMainActivity)(argument.getAllValues().get(0))).NewTitle,"title");
        assertEquals(((TitleChangeEvent.ToMainPageTags)(argument.getAllValues().get(1))).CurrentPosition,0);
        assertEquals(((TitleChangeEvent.ToMainPageTags)(argument.getAllValues().get(1))).CurrentTitle,"title");
        verify(mainInteractor).saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE, "title");
        verify(mainInteractor).saveCurrentState(STATE_KEY_CURRENT_TAG_TITLE_POSITION, 0);
    }

    @Test
    @PrepareForTest({IdpResponse.class})
    public void testHandleActivityResult() throws Exception {
        Intent intent=mock(Intent.class);
        IdpResponse idpResponse=mock(IdpResponse.class);
        EventBus eventBus=mock(EventBus.class);
        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        PowerMockito.mockStatic(IdpResponse.class);
        Mockito.when(IdpResponse.fromResultIntent(intent)).thenReturn(idpResponse);

        mainPresenterImpl.eventBus=eventBus;
        mainPresenterImpl.handleActivityResult(REQUEST_CODE_COMMERCIAL_CREATE_SIGN_IN, RESULT_OK, intent, null);

        verify(mainView).performFabClick();
        verify(eventBus).postSticky(argument.capture());
        assertTrue(argument.getValue() instanceof LoginEvent.LogIn);
    }

    @Test
    @PrepareForTest({BaseUtils.System.class,AuthUI.class})
    public void testOnOptionsItemSelected() throws Exception {
        FragmentActivity activity=mock(FragmentActivity.class);
        Task<Void> task=mock(Task.class);
        MenuItem menuItem=mock(MenuItem.class);
        AuthUI authUI=mock(AuthUI.class);
        EventBus eventBus=mock(EventBus.class);
        ArgumentCaptor<OnCompleteListener> argument = ArgumentCaptor.forClass(OnCompleteListener.class);
        ArgumentCaptor<LoginEvent.LogOut> argument1 = ArgumentCaptor.forClass(LoginEvent.LogOut.class);
        when(menuItem.getItemId()).thenReturn(R.id.sign);
        PowerMockito.mockStatic(BaseUtils.System.class);
        Mockito.when(BaseUtils.System.OnlyCheckIfLogin()).thenReturn(true);
        PowerMockito.mockStatic(AuthUI.class);
        Mockito.when(AuthUI.getInstance()).thenReturn(authUI);
        when(authUI.signOut(activity)).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(activity.getString(R.string.sign_out_success)).thenReturn("test");

        mainPresenterImpl.eventBus=eventBus;
        boolean result = mainPresenterImpl.onOptionsItemSelected(menuItem, activity);

        verify(task).addOnCompleteListener(argument.capture());
        argument.getValue().onComplete(task);
        verify(eventBus).postSticky(argument1.capture());
        verify(mainView).showSnackBar("test");
        assertTrue(argument1.getValue() instanceof LoginEvent.LogOut);
        assertTrue(result);
    }

}
