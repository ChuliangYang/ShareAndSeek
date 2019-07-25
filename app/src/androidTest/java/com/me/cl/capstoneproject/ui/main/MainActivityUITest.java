package com.me.cl.capstoneproject.ui.main;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ScrollToAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.LayoutAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.adapter.recyclerview.CommercialCateAdapter;
import com.me.cl.capstoneproject.adapter.recyclerview.CommercialListAdapter;
import com.me.cl.capstoneproject.adapter.recyclerview.FreeListAdapter;
import com.me.cl.capstoneproject.adapter.recyclerview.HelpListAdapter;
import com.me.cl.capstoneproject.bean.CommercialCategory;
import com.me.cl.capstoneproject.bean.CommercialItem;
import com.me.cl.capstoneproject.bean.FreeItem;
import com.me.cl.capstoneproject.bean.HelpItem;
import com.me.cl.capstoneproject.test.IdlingResource.ActivityResumedFocuseIdlingResource;
import com.me.cl.capstoneproject.ui.upload.commercial.CommercialUploadActivity;
import com.me.cl.capstoneproject.ui.upload.free.FreeUploadActivity;
import com.me.cl.capstoneproject.ui.upload.help.HelpUploadActivity;
import com.me.cl.capstoneproject.util.BaseUtils;
import com.me.cl.capstoneproject.widget.FakeView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static com.me.cl.capstoneproject.base.Constant.ListPage.BUNDLE_KEY_CATE;
import static com.me.cl.capstoneproject.base.Constant.MainPage.DATA_KEY_COMMERCIAL_ITEM;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityUITest {

    @Rule
    public IntentsTestRule<MainActivity> mIntentTestRule = new IntentsTestRule<>(MainActivity.class);

    MainActivity activity;
    CountingIdlingResource countingIdlingResource;
    UiDevice mdevice;

    @Before
    public void setUp(){
        mdevice=UiDevice.getInstance(getInstrumentation());
        activity=mIntentTestRule.getActivity();
//        intending(hasAction(android.content.Intent.ACTION_SEND)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
//        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

//    @Test
//    public void pressureTest(){
//        Boolean intentCache=false;
//        mdevice.waitForIdle();
//
//        while (true){
//            if (intentCache) {
//                Intents.release();
//                Intents.init();
//                intentCache=false;
//            }
//            assertCategoryList(0);
//            onView(withId(R.id.vp_commercial_cate)).perform(ViewActions.swipeLeft());
//            mdevice.waitForIdle();
//
//            assertCategoryList(1);
//            onView(withId(R.id.vp_commercial_cate)).perform(ViewActions.swipeRight());
//            mdevice.waitForIdle();
//            intentCache=true;
//        }
//    }

    @Test
    public void mainActivityUITest() throws InterruptedException {

        mdevice.waitForIdle();

        String currentTitleM=activity.spTitle.getAdapter().getItem(0).toString();
        onView(withId(R.id.iv_logo)).check(matches(isDisplayed()));
        onView(withId(R.id.sp_title)).check(matches(allOf(isDisplayed(),withSpinnerText(currentTitleM))));
        onView(withId(R.id.sign)).check(matches(isDisplayed()));

        if (BaseUtils.System.OnlyCheckIfLogin()) {
            assertDrawableEqual(activity.getDrawable(R.drawable.ic_sign_out),activity.sign_menu.getIcon());
        } else {
            assertDrawableEqual(activity.getDrawable(R.drawable.sign_in_icon),activity.sign_menu.getIcon());
        }

        onView(withId(R.id.fab_create)).check(matches(isDisplayed()));
        onView(withId(R.id.adView)).check(matches(isDisplayed()));

        onView(allOf(withText("Commercial"),isDescendantOfA(withId(R.id.tl_main)))).check(matches(allOf(isDisplayed(),isSelected())));
        onView(allOf(withText("Free"),isDescendantOfA(withId(R.id.tl_main)))).check(matches(allOf(isDisplayed(),not(isSelected()))));
        onView(allOf(withText("Help"),isDescendantOfA(withId(R.id.tl_main)))).check(matches(allOf(isDisplayed(),not(isSelected()))));


        assertCategoryList(0);
        onView(withId(R.id.vp_commercial_cate)).perform(ViewActions.swipeLeft());
        mdevice.waitForIdle();

        assertCategoryList(1);
        onView(withId(R.id.vp_commercial_cate)).perform(ViewActions.swipeRight());

        assertCommercialList();
        onView(withId(R.id.sf_commercial)).perform(handleConstraints(ViewActions.swipeDown(),isDisplayingAtLeast(1)));
        while (((SwipeRefreshLayout)(activity.getWindow().getDecorView().findViewById(R.id.sf_commercial))).isRefreshing()){
        }
        mdevice.waitForIdle();

        assertCommercialList();

        for (int i = 1; i <activity.spTitle.getAdapter().getCount() ; i++) {
            onView(withId(R.id.sp_title)).perform(ViewActions.click());
            onData(allOf(is(instanceOf(String.class)),is(activity.spTitle.getAdapter().getItem(i).toString()))).perform(ViewActions.click());
            mdevice.waitForIdle();
            assertCommercialList();
        }

        backToFirstDistrict();

        onView(withId(R.id.vp_main)).perform(ViewActions.swipeLeft());
        mdevice.waitForIdle();
        assertFreeList();

        for (int i = 1; i <activity.spTitle.getAdapter().getCount() ; i++) {
            onView(withId(R.id.sp_title)).perform(ViewActions.click());
            onData(allOf(is(instanceOf(String.class)),is(activity.spTitle.getAdapter().getItem(i).toString()))).perform(ViewActions.click());
            mdevice.waitForIdle();
            assertFreeList();
        }

        backToFirstDistrict();


        onView(withId(R.id.vp_main)).perform(ViewActions.swipeLeft());
        mdevice.waitForIdle();

        assertHelpList();

        for (int i = 1; i <activity.spTitle.getAdapter().getCount() ; i++) {
            onView(withId(R.id.sp_title)).perform(ViewActions.click());
            onData(allOf(is(instanceOf(String.class)),is(activity.spTitle.getAdapter().getItem(i).toString()))).perform(ViewActions.click());
            mdevice.waitForIdle();
            assertHelpList();
        }

        backToFirstDistrict();


        // TODO: 1/30/18 must login your own account manually before following test
        if (BaseUtils.System.OnlyCheckIfLogin()) {
            assertFabCreate("Help", HelpUploadActivity.class);
            onView(withId(R.id.vp_main)).perform(ViewActions.swipeRight());
            mdevice.waitForIdle();

            assertFabCreate("Free", FreeUploadActivity.class);
            onView(withId(R.id.vp_main)).perform(ViewActions.swipeRight());
            mdevice.waitForIdle();

            assertFabCreate("Commercial", CommercialUploadActivity.class);

            try {
                mdevice.findObject(new UiSelector().resourceId("com.me.cl.capstoneproject:id/sign")).click();
                boolean success1=mdevice.findObject(new UiSelector().text("Sign out success")).exists();
                boolean success2=mdevice.findObject(new UiSelector().text("Sign out success")).waitForExists(3000);
                if (!success1&&!success2) {
                    fail("no snack bar");
                }
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void backToFirstDistrict() {
        onView(withId(R.id.sp_title)).perform(ViewActions.click());
        onData(allOf(is(instanceOf(String.class)),is(activity.spTitle.getAdapter().getItem(0).toString()))).perform(ViewActions.click());
        mdevice.waitForIdle();
    }

    private void assertFabCreate(String title,Class target) throws InterruptedException {
        onView(withId(R.id.fab_create)).perform(ViewActions.click());
        intended(allOf(hasExtra("fab_x",activity.viewRoot.getWidth()/2),hasExtra("fab_y",activity.viewRoot.getHeight()/2),hasExtra("radius",activity.fabCreate.getWidth()/2),hasComponent(hasClassName(target.getName()))));

        mdevice.waitForIdle();
        waitTopActivityResumeAndFocus(100L);
        onView(allOf(withText(title),isDescendantOfA(instanceOf(android.support.v7.widget.Toolbar.class)))).check(matches(isDisplayed()));
        pressBack();
        mdevice.findObject(new UiSelector().resourceId("com.me.cl.capstoneproject:id/fab_create")).waitForExists(5000);
        mdevice.waitForIdle();
    }

    private void assertHelpList() throws InterruptedException {
        onView(allOf(withText("Commercial"),isDescendantOfA(withId(R.id.tl_main)))).check(matches(allOf(isDisplayed(),not(isSelected()))));
        onView(allOf(withText("Free"),isDescendantOfA(withId(R.id.tl_main)))).check(matches(allOf(isDisplayed(),not(isSelected()))));
        onView(allOf(withText("Help"),isDescendantOfA(withId(R.id.tl_main)))).check(matches(allOf(isDisplayed(),isSelected())));

        mdevice.waitForIdle();

        RecyclerView rv_list_help=((ViewPager)(activity.getWindow().getDecorView().findViewById(R.id.vp_main))).findViewById(R.id.rv_help);
        if (!(rv_list_help.getAdapter() instanceof HelpListAdapter)) {
            fail("!(rv_list_help.getAdapter() instanceof HelpListAdapter");
        }
        HelpListAdapter helpListAdapter= (HelpListAdapter) rv_list_help.getAdapter();
        List<HelpItem> helpItemList=helpListAdapter.getDataSet();
        for (int i = 0; i < helpItemList.size(); i++) {
            HelpItem helpItem=helpItemList.get(i);
            onView(is(rv_list_help)).perform(RecyclerViewActions.actionOnItemAtPosition(i, new ScrollToAction()));
            onView(allOf(withId(R.id.iv_avatar),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.tv_title),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(allOf(isDisplayed(),withText(helpItem.getTitle()))));
            onView(allOf(withId(R.id.tv_summary),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(allOf(isDisplayed(),withText(helpItem.getSummary()))));
            onView(allOf(withId(R.id.tv_address),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(allOf(isDisplayed(),withText(helpItem.getLocation().getStreet()+"  "+helpItem.getLocation().getCity()))));
            TextView tv=rv_list_help.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.tv_summary);
            if (tv.getLayout().getEllipsisCount(0) > 0) {
                onView(allOf(withId(R.id.iv_expand),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(isDisplayed()));
                onView(allOf(withId(R.id.iv_expand),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).perform(ViewActions.click()).check(LayoutAssertions.noEllipsizedText());
                onView(allOf(withId(R.id.tv_summary),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(withText(helpItem.getSummary())));
                onView(allOf(withId(R.id.iv_expand),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).perform(ViewActions.click());
            } else {
                onView(allOf(withId(R.id.iv_expand),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(not(isDisplayed())));
            }
            onView(allOf(withId(R.id.btn_help),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(isDisplayed()));
            if (helpItem.getPhotoBeanList() == null || helpItem.getPhotoBeanList().size() <= 0) {
                onView(allOf(withId(R.id.rv_photos),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(not(isDisplayed())));
            } else {
                onView(allOf(withId(R.id.rv_photos),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).check(matches(isDisplayed()));
            }
            onView(allOf(withId(R.id.btn_help),isDescendantOfA(allOf(withParent(is(rv_list_help)),withAdapterPosition(i))))).perform(ViewActions.click());

            onView(allOf(hasDescendant(withText("Help")),isRoot())).inRoot(isDialog()).check(matches(isDisplayed()));
            onView(allOf(hasDescendant(withText("Help")),isRoot())).inRoot(isDialog()).check(matches(allOf(hasDescendant(withText("Yes")),hasDescendant(withText("No")),hasDescendant(withText("Do you want to call "+helpItem.getPerson_name()+"?")))));
            onView(withText("No")).inRoot(isDialog()).perform(ViewActions.click());
            onView(allOf(hasDescendant(withText("Yes")),hasDescendant(withText("No")),hasDescendant(withText("Help")))).check(doesNotExist());
        }
    }

    private void assertFreeList() throws InterruptedException {
        onView(allOf(withText("Commercial"),isDescendantOfA(withId(R.id.tl_main)))).check(matches(allOf(isDisplayed(),not(isSelected()))));
        onView(allOf(withText("Free"),isDescendantOfA(withId(R.id.tl_main)))).check(matches(allOf(isDisplayed(),isSelected())));
        onView(allOf(withText("Help"),isDescendantOfA(withId(R.id.tl_main)))).check(matches(allOf(isDisplayed(),not(isSelected()))));

        mdevice.waitForIdle();

        RecyclerView rv_list_free=((ViewPager)(activity.getWindow().getDecorView().findViewById(R.id.vp_main))).getChildAt(1).findViewById(R.id.rv_list);
        if (!(rv_list_free.getAdapter() instanceof FreeListAdapter)) {
            fail("!(rv_list_help.getAdapter() instanceof FreeListAdapter)");
        }
        FreeListAdapter freeListAdapter= (FreeListAdapter) rv_list_free.getAdapter();
        List<FreeItem> freeItemList=freeListAdapter.getDataSet();
        for (int i = 0; i < freeItemList.size(); i++) {
            FreeItem freeItem=freeItemList.get(i);
            onView(is(rv_list_free)).perform(RecyclerViewActions.actionOnItemAtPosition(i, new ScrollToAction()));
            onView(allOf(withId(R.id.iv_avatar),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.tv_title),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(allOf(isDisplayed(),withText(freeItem.getTitle()))));
            onView(allOf(withId(R.id.tv_summary),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(allOf(isDisplayed(),withText(freeItem.getSummary()))));
            onView(allOf(withId(R.id.tv_address),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(allOf(isDisplayed(),withText(freeItem.getLocation().getStreet()+"  "+freeItem.getLocation().getCity()))));
            TextView tv=rv_list_free.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.tv_summary);
            if (tv.getLayout().getEllipsisCount(0) > 0) {
                onView(allOf(withId(R.id.iv_expand),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(isDisplayed()));
                onView(allOf(withId(R.id.iv_expand),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).perform(ViewActions.click()).check(LayoutAssertions.noEllipsizedText());
                onView(allOf(withId(R.id.tv_summary),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(withText(freeItem.getSummary())));
                onView(allOf(withId(R.id.iv_expand),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).perform(ViewActions.click());

            } else {
                onView(allOf(withId(R.id.iv_expand),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(not(isDisplayed())));
            }
            onView(allOf(withId(R.id.btn_contact),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.btn_share),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(isDisplayed()));
            if (freeItem.getPhotoBeanList() == null || freeItem.getPhotoBeanList().size() <= 0) {
                onView(allOf(withId(R.id.rv_photos),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(not(isDisplayed())));
            } else {
                onView(allOf(withId(R.id.rv_photos),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).check(matches(isDisplayed()));
            }
            onView(allOf(withId(R.id.btn_contact),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).perform(ViewActions.click());

            onView(allOf(hasDescendant(withText("Contact")),isRoot())).inRoot(isDialog()).check(matches(isDisplayed()));
            onView(allOf(hasDescendant(withText("Contact")),isRoot())).inRoot(isDialog()).check(matches(allOf(hasDescendant(withText("Yes")),hasDescendant(withText("No")),hasDescendant(withText("Do you want to call "+freeItem.getPerson_name()+"?")))));
            onView(withText("No")).inRoot(isDialog()).perform(ViewActions.click());
            onView(allOf(hasDescendant(withText("Yes")),hasDescendant(withText("No")),hasDescendant(withText("Contact")))).check(doesNotExist());

            onView(allOf(withId(R.id.btn_share),isDescendantOfA(allOf(withParent(is(rv_list_free)),withAdapterPosition(i))))).perform(ViewActions.click());

            assertTrue(mdevice.findObject(new UiSelector().text("Share with")).waitForExists(5000));

            mdevice.pressBack();
            mdevice.waitForIdle();
        }
    }

    private void assertCommercialList() {
        RecyclerView rv_list_commercial=activity.getWindow().getDecorView().findViewById(R.id.rv_commercial_item);
        if (!(rv_list_commercial.getAdapter() instanceof CommercialListAdapter)) {
            fail("!(rv_list_commercial.getAdapter() instanceof CommercialListAdapter)");
        }
        CommercialListAdapter commercialListAdapter= (CommercialListAdapter) rv_list_commercial.getAdapter();
        List<CommercialItem> commercialItemList=commercialListAdapter.getData();
        for (int i = 0; i <commercialItemList.size() ; i++) {
            onView(is(rv_list_commercial)).perform(RecyclerViewActions.actionOnItemAtPosition(i, new ScrollToAction()));
            onView(allOf(withId(R.id.tv_name),isDescendantOfA(allOf(withParent(is(rv_list_commercial)), withAdapterPosition(i))))).check(matches(withText(commercialItemList.get(i).getTitle())));
            onView(allOf(withId(R.id.rb_reviews),isDescendantOfA(allOf(withParent(is(rv_list_commercial)), withAdapterPosition(i))))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.tv_address),isDescendantOfA(allOf(withParent(is(rv_list_commercial)), withAdapterPosition(i))))).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.iv_photo),isDescendantOfA(allOf(withParent(is(rv_list_commercial)), withAdapterPosition(i))))).check(matches(isDisplayed()));
            if (commercialItemList.get(i).getPhotoBeanList() != null && commercialItemList.get(i).getPhotoBeanList().size() > 0) {
                assertTrue(!TextUtils.isEmpty(commercialItemList.get(i).getPhotoBeanList().get(0).getUri()));
            } else {
                assertDrawableEqual(activity.getDrawable(R.drawable.no_image_available),((ImageView)(rv_list_commercial.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.iv_photo))).getDrawable());
            }
            if (commercialItemList.get(i).getAverage_costume()!=0f) {
                onView(allOf(withId(R.id.tv_dollar),isDescendantOfA(allOf(withParent(is(rv_list_commercial)), withAdapterPosition(i))))).check(matches(allOf(isDisplayed(),withText("$ "+String.valueOf((commercialItemList.get(i).getAverage_costume()).intValue())))));
            }
            if (commercialItemList.get(i).getReview_count()!=0f) {
                onView(allOf(withId(R.id.tv_reviews),isDescendantOfA(allOf(withParent(is(rv_list_commercial)), withAdapterPosition(i))))).check(matches(allOf(isDisplayed(),withText(String.valueOf((int) (commercialItemList.get(i).getReview_count())) + (commercialItemList.get(i).getReview_count()==1f?" Review":" Reviews")))));
            }

            TextView summary=(rv_list_commercial.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.tv_summary));
            onView(is(summary)).check(matches(allOf(isDisplayed(),withText(commercialItemList.get(i).getSummary()))));
            onView(is(rv_list_commercial.findViewHolderForAdapterPosition(i).itemView)).perform(ViewActions.click());
            intended(hasExtra(DATA_KEY_COMMERCIAL_ITEM,commercialItemList.get(i)));
            mdevice.findObject(new UiSelector().resourceId("com.me.cl.capstoneproject:id/tv_title")).waitForExists(5000);
            mdevice.waitForIdle();
            onView(allOf(withId(R.id.tv_title),withParent(withId(R.id.cl_base_info)))).check(matches(withText(commercialItemList.get(i).getTitle())));
            pressBack();
            mdevice.findObject(new UiSelector().resourceId("com.me.cl.capstoneproject:id/fab_create")).waitForExists(5000);
            mdevice.waitForIdle();
        }


        NestedScrollView nestedScrollView=activity.getWindow().getDecorView().findViewById((R.id.nsv_root));
        nestedScrollView.scrollTo(0,0);
    }

    private void assertCategoryList(int position) {
        RecyclerView rv_list= (RecyclerView) ((ViewPager)(activity.getWindow().getDecorView().findViewById(R.id.vp_commercial_cate))).getChildAt(position);
        assertNotNull(rv_list);
        if (!(rv_list.getAdapter() instanceof CommercialCateAdapter)) {
            fail("!(rv_list.getAdapter() instanceof CommercialCateAdapter)");
        }
        CommercialCateAdapter commercialCateAdapter= (CommercialCateAdapter) rv_list.getAdapter();
        List<CommercialCategory> commercialCategoryList=commercialCateAdapter.getList();
        for (CommercialCategory commercialCategoryItem: commercialCategoryList) {
            onView(allOf(withText(commercialCategoryItem.getCategory()),isDescendantOfA(is(rv_list)))).check(matches(isDisplayed()));
            switch (commercialCategoryItem.getCategory()){
                case "Restaurants":
                    assertEquals(R.drawable.ic_restaurant,commercialCategoryItem.getImgId());
                    break;
                case "Bars":
                    assertEquals(R.drawable.ic_bars,commercialCategoryItem.getImgId());
                    break;
                case "Hair Salon":
                    assertEquals(R.drawable.ic_salon,commercialCategoryItem.getImgId());
                    break;
                case "Takeout":
                    assertEquals(R.drawable.ic_take_out,commercialCategoryItem.getImgId());
                    break;
                case "Shopping":
                    assertEquals(R.drawable.ic_shopping,commercialCategoryItem.getImgId());
                    break;
                case "Hotel":
                    assertEquals(R.drawable.ic_hotel,commercialCategoryItem.getImgId());
                    break;
                case "Skin Care":
                    assertEquals(R.drawable.ic_skin_care,commercialCategoryItem.getImgId());
                    break;
                case "Coffee":
                    assertEquals(R.drawable.ic_coffe,commercialCategoryItem.getImgId());
                    break;
                case "Drug":
                    assertEquals(R.drawable.ic_drug,commercialCategoryItem.getImgId());
                    break;
                case "Pet":
                    assertEquals(R.drawable.ic_pet,commercialCategoryItem.getImgId());
                    break;
                case "Theater":
                    assertEquals(R.drawable.ic_theater,commercialCategoryItem.getImgId());
                    break;
                case "Other":
                    assertEquals(R.drawable.ic_other,commercialCategoryItem.getImgId());
                    break;
                default:
                    fail("illegal category");
            }
            onView(allOf(withText(commercialCategoryItem.getCategory()),isDescendantOfA(is(rv_list)))).perform(ViewActions.click());

            intended(allOf(hasExtra(BUNDLE_KEY_CATE,commercialCategoryItem.getCategory()),hasExtra("left",activity.fabCreate.getLeft()),hasExtra("top",activity.fabCreate.getTop()),
                    hasExtra("sp_left",activity.spTitle.getLeft()),hasExtra("sp_top",activity.spTitle.getTop()),hasExtra("title",activity.spTitle.getSelectedItem().toString()),
                    hasExtra("titlePosition",activity.spTitle.getSelectedItemPosition())));
            mdevice.findObject(new UiSelector().className(ProgressBar.class)).waitForExists(1000);
            mdevice.findObject(new UiSelector().className(ProgressBar.class)).waitUntilGone(30000);
            mdevice.findObject(new UiSelector().resourceId("com.me.cl.capstoneproject:id/tv_service")).waitForExists(5000);
            mdevice.waitForIdle();
            onView(withId(R.id.tv_service)).check(matches(withText(commercialCategoryItem.getCategory())));
            pressBack();
            mdevice.findObject(new UiSelector().resourceId("com.me.cl.capstoneproject:id/fab_create")).waitForExists(5000);
            mdevice.waitForIdle();
        }

        onView(withId(R.id.indicator)).check(matches(isDisplayed()));
    }

    private void waitTopActivityResumeAndFocus(Long continueFocusTime) {
        countingIdlingResource=new CountingIdlingResource(String.valueOf(System.currentTimeMillis()));
        IdlingRegistry.getInstance().register(countingIdlingResource);
        AppCompatActivity[] activities=new AppCompatActivity[1];
        getActivityInstance(activities);
        onView(is(isAssignableFrom(FakeView.class))).check(doesNotExist());
        IdlingRegistry.getInstance().unregister(countingIdlingResource);
        waitUntilActivityFocusContinue(activities[0],continueFocusTime);
    }

    private void waitUntilActivityFocusContinue(AppCompatActivity activity, Long continueTime) {
        ActivityResumedFocuseIdlingResource activityFocuseIdlingResource1=new ActivityResumedFocuseIdlingResource(activity,continueTime);
        IdlingRegistry.getInstance().register(activityFocuseIdlingResource1);
        onView(is(isAssignableFrom(FakeView.class))).check(doesNotExist());
        IdlingRegistry.getInstance().unregister(activityFocuseIdlingResource1);
    }


    public void getBitmapFromVectorDrawable(Drawable drawable,Bitmap[] bitmap) {
        countingIdlingResource.increment();
        final Drawable[] drawable1 = new Drawable[1];
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable1[0] = (DrawableCompat.wrap(drawable)).mutate();
//            drawable1[0]=drawable;
        } else {
            drawable1[0]=drawable;
        }
        activity.runOnUiThread(() -> {
             bitmap[0] = Bitmap.createBitmap(drawable1[0].getIntrinsicWidth(),
                    drawable1[0].getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap[0]);
            drawable1[0].setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable1[0].draw(canvas);
            countingIdlingResource.decrement();
        });
    }

    public void assertDrawableEqual(Drawable drawable1, Drawable drawable2){
        Bitmap[] bitmap1=new Bitmap[1];
        Bitmap[] bitmap2=new Bitmap[1];
        countingIdlingResource=new CountingIdlingResource(String.valueOf(System.currentTimeMillis()));
        getBitmapFromVectorDrawable(drawable1,bitmap1);
        getBitmapFromVectorDrawable(drawable2,bitmap2);
        Espresso.registerIdlingResources(countingIdlingResource);
        onView(is(isAssignableFrom(FakeView.class))).check(doesNotExist());
        Espresso.unregisterIdlingResources(countingIdlingResource);
        countingIdlingResource=null;
        assertTrue(bitmap1[0].sameAs(bitmap2[0]));
    }

    public Matcher<View> withAdapterPosition(int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (item.getParent() instanceof RecyclerView) {
                    if (item == ((RecyclerView) (item.getParent())).findViewHolderForAdapterPosition(position).itemView) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    public void getActivityInstance(Activity[] topActivity) {
        countingIdlingResource.increment();
        final Activity[] activity = new Activity[1];
        getInstrumentation().runOnMainSync(() -> {
            Activity currentActivity = null;
            Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = (Activity) resumedActivities.iterator().next();
                activity[0] = currentActivity;
            }
            topActivity[0]=activity[0];
            countingIdlingResource.decrement();
        });

    }

    public static ViewAction handleConstraints(final ViewAction action, final Matcher<View> constraints)
    {
        return new ViewAction()
        {
            @Override
            public Matcher<View> getConstraints()
            {
                return constraints;
            }

            @Override
            public String getDescription()
            {
                return action.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view)
            {
                action.perform(uiController, view);
            }
        };
    }
}
