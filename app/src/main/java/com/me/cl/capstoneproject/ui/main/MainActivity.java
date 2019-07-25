package com.me.cl.capstoneproject.ui.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.me.cl.capstoneproject.MyApplication;
import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.adapter.viewpager.MainPagerAdapter;
import com.me.cl.capstoneproject.anim.ZoomOutPageTransformer;
import com.me.cl.capstoneproject.base.BaseActivity;
import com.me.cl.capstoneproject.base.Constant;
import com.me.cl.capstoneproject.event.AnimEvent;
import com.me.cl.capstoneproject.event.LoginEvent;
import com.me.cl.capstoneproject.event.PermissionCheckEvent;
import com.me.cl.capstoneproject.event.StartEvent;
import com.me.cl.capstoneproject.event.TitleChangeEvent;
import com.me.cl.capstoneproject.screenwidget.WidgetService;
import com.me.cl.capstoneproject.ui.main.di.DaggerMainActivityComponent;
import com.me.cl.capstoneproject.ui.main.di.MainActivityModule;
import com.me.cl.capstoneproject.ui.main.mvp.MainPresenter;
import com.me.cl.capstoneproject.ui.main.mvp.MainView;
import com.me.cl.capstoneproject.util.BaseUtils;
import com.me.cl.capstoneproject.util.BitmapUtil;
import com.me.cl.capstoneproject.widget.CustomSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_FAB_CENTER;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_FAB_CENTER_X;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_FAB_CENTER_Y;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_FAB_X;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_FAB_Y;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_LEFT;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_RADIUS;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_SP_LEFT;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_SP_TITLE;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_SP_TITLE_POSITION;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_SP_TOP;
import static com.me.cl.capstoneproject.base.Constant.MainPage.BUNDLE_KEY_TOP;
import static com.me.cl.capstoneproject.base.Constant.MainPage.REQUEST_CODE_PERMISSION_CHECK_FREE;
import static com.me.cl.capstoneproject.base.Constant.MainPage.REQUEST_CODE_PERMISSION_CHECK_HELP;
import static com.me.cl.capstoneproject.base.Constant.MainPage.TAG_DOWN;
import static com.me.cl.capstoneproject.base.Constant.MainPage.TAG_UP;

public class MainActivity extends BaseActivity implements MainView {

    @Inject
    MainPresenter mainPresenter;

    @BindView(R.id.fab_create)
    FloatingActionButton fabCreate;
    @BindView(R.id.sp_title)
    Spinner spTitle;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.tl_main)
    TabLayout tlMain;
    @BindView(R.id.adView)
    AdView adView;
    @BindView(R.id.vp_main)
    ViewPager vpMain;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.view_root)
    ConstraintLayout viewRoot;
    @BindView(R.id.ll_top)
    LinearLayout llTop;

    MenuItem sign_menu;

    int fabCenterX;
    int fabCenterY;
    boolean configReenterTransition=false;
    boolean fabCenter=false;

    public int TITLE_CHANGE_SOURCE = Constant.TITLE_CHANGE_FROM_MAIN;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        DaggerMainActivityComponent.builder()
                .applicationComponent(((MyApplication) getApplication()).getApplicationComponent())
                .mainActivityModule(new MainActivityModule(this)).build()
                .inject(this);
        setSupportActionBar(tbMain);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (savedInstanceState!=null) {
            if (savedInstanceState.getBoolean(Constant.MainPage.BUNDLE_KEY_CONFIG_REENTER)) {
                configReenterTransition=true;
                configReenterTransition();
            }
            if (savedInstanceState.getBoolean(BUNDLE_KEY_FAB_CENTER)) {
                fabCenterX=savedInstanceState.getInt(BUNDLE_KEY_FAB_CENTER_X);
                fabCenterY=savedInstanceState.getInt(BUNDLE_KEY_FAB_CENTER_Y);
                fabCenter=true;
                fabCreate.post(() -> {
                    fabCreate.setX(fabCenterX);
                    fabCreate.setY(fabCenterY);
                });

            }

        }
        displayAdd();
        addListener();
        mainPresenter.manage(this);
        mainPresenter.init();

    }

    public void showAlertDialog(String title, String content, String button, MaterialDialog.SingleButtonCallback singleButtonCallback) {
        super.showAlertDialog(title, content, button, singleButtonCallback, null);
    }

    public void controlIndeterminateProgress(boolean show, String title, String content) {
        super.controlIndeterminateProgress(show, title, content, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mainPresenter.onStart();
        ivLogo.setAlpha(0f);
        ivLogo.setScaleX(0.8f);
        ivLogo.animate()
                .alpha(1f)
                .scaleX(1f)
                .setStartDelay(500)
                .setDuration(1000)
                .setInterpolator(new FastOutSlowInInterpolator());
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        getApplicationContext().startService(new Intent(getApplicationContext(), WidgetService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adView.destroy();
    }

    private void addListener() {
        fabCreate.setOnClickListener(v -> {
            mainPresenter.onFabCreateClick(this, v, viewRoot);
        });
        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mainPresenter.onViewPageSelected(position, spTitle.getSelectedItem().toString(), spTitle.getSelectedItemPosition());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        spTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainPresenter.onTitleChange(spTitle.getSelectedItem().toString(), position, TITLE_CHANGE_SOURCE);
                TITLE_CHANGE_SOURCE = Constant.TITLE_CHANGE_FROM_MAIN;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (spTitle instanceof CustomSpinner) {
            ((CustomSpinner) spTitle).setOnDropDownVisibleChangeListener(new CustomSpinner.onDropDownVisibleChangeListener() {
                @Override
                public void onPopUp(View v) {
                    ((ImageButton) (spTitle.findViewById(R.id.btn_arrow))).setImageDrawable(getDrawable(R.drawable.vector_arrow_down_to_up));
                    ((Animatable) (((ImageButton) (spTitle.findViewById(R.id.btn_arrow))).getDrawable())).start();
                    spTitle.findViewById(R.id.btn_arrow).setTag(TAG_UP);
                }

                @Override
                public void onDismiss(View v) {
                    ((ImageButton) (spTitle.findViewById(R.id.btn_arrow))).setImageDrawable(getDrawable(R.drawable.vector_arrow_up_to_down));
                    ((Animatable) (((ImageButton) (spTitle.findViewById(R.id.btn_arrow))).getDrawable())).start();
                    spTitle.findViewById(R.id.btn_arrow).setTag(TAG_DOWN);
                }
            });
        }
        spTitle.setSelection(0);//trigger item event manually


    }

    private void displayAdd() {
//        MobileAds.initialize(this, getString(R.string.ad_test_app_id));
        MobileAds.initialize(this, getString(R.string.ad_app_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        sign_menu = menu.findItem(R.id.sign);
        if (BaseUtils.System.OnlyCheckIfLogin()) {
            sign_menu.setIcon(R.drawable.ic_sign_out);
        } else {
            sign_menu.setIcon(R.drawable.sign_in_icon);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mainPresenter.onOptionsItemSelected(item, this)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mainPresenter.handleActivityResult(requestCode, resultCode, data, this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onTitleChange(TitleChangeEvent.FromCommercialListActivity event) {
        if (event != null) {
            spTitle.setSelection(event.NewPosition);
            TITLE_CHANGE_SOURCE = Constant.TITLE_CHANGE_FROM_LIST;
//            mainPresenter.onTitleChange(spTitle.getSelectedItem().toString(), event.NewPosition,Constant.TITLE_CHANGE_FROM_LIST);
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLoginChange(LoginEvent.base event) {
        if (event instanceof LoginEvent.Failed && !TextUtils.isEmpty(((LoginEvent.Failed) event).info)) {
            showSnackBar(((LoginEvent.Failed) event).info);
        }
        if (event instanceof LoginEvent.LogIn) {
            ((Animatable) (sign_menu.setIcon(R.drawable.sign_in_anim).getIcon())).start();
        }
        if (event instanceof LoginEvent.LogOut) {
            sign_menu.setIcon(R.drawable.vector_sign_out);
            if(sign_menu.getIcon() instanceof Animatable){
                ((Animatable) (sign_menu.getIcon())).start();
            }
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAnimEvent(AnimEvent event) {
        if (event.id == fabCreate.getId()) {
            if (!event.reverse) {
                fabCenterX=viewRoot.getWidth() / 2 - fabCreate.getWidth() / 2;
                fabCenterY=viewRoot.getHeight() / 2 - fabCreate.getHeight() / 2;
                fabCreate.animate().x(fabCenterX).y(fabCenterY).setDuration(event.duration).setInterpolator(new PathInterpolator(0.4f, 0.4f, 0.8f, 1f));
                EventBus.getDefault().removeStickyEvent(event);
                fabCenter=true;
            } else {
                fabCreate.animate().x(fabCreate.getLeft()).y(fabCreate.getTop()).setDuration(event.duration);
                EventBus.getDefault().removeStickyEvent(event);
                fabCenter=false;
            }

        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onStartEvent(StartEvent.Activity event) {
        if (event.target == StartEvent.Activity.activity.MainActivity) {
            Intent intent = event.intent;
            if (event.withSharedAnim) {
                BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(llTop), intent);
                BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(spTitle), intent);
                intent.putExtra(BUNDLE_KEY_LEFT, fabCreate.getLeft());
                intent.putExtra(BUNDLE_KEY_TOP, fabCreate.getTop());
                intent.putExtra(BUNDLE_KEY_SP_LEFT, spTitle.getLeft());
                intent.putExtra(BUNDLE_KEY_SP_TOP, spTitle.getTop());
                intent.putExtra(BUNDLE_KEY_SP_TITLE, spTitle.getSelectedItem().toString());
                intent.putExtra(BUNDLE_KEY_SP_TITLE_POSITION, spTitle.getSelectedItemPosition());
                Pair first = new Pair<>(vpMain, ViewCompat.getTransitionName(vpMain));
                Pair third = new Pair<>(llTop, ViewCompat.getTransitionName(llTop));
                Pair forth = new Pair<>(spTitle, ViewCompat.getTransitionName(spTitle));
                Pair statusBar = new Pair<>(findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
                Pair navigationBar = new Pair<>(findViewById(android.R.id.navigationBarBackground),Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                if (navigationBar.first == null) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this,first, third, forth,statusBar).toBundle());

                } else {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this,first, third, forth,statusBar,navigationBar).toBundle());

                }
                configReenterTransition();
                configReenterTransition=true;
            } else {
                startActivity(intent);
            }
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    private void configReenterTransition() {
        getWindow().getReenterTransition().setStartDelay(800);
        getWindow().setAllowReturnTransitionOverlap(false);
        getWindow().getReenterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                    ((ViewGroup) (((ViewGroup) (vpMain.getChildAt(0))).getChildAt(0))).getChildAt(0).setTranslationX(vpMain.getMeasuredWidth());
                    ((ViewGroup) (((ViewGroup) (vpMain.getChildAt(0))).getChildAt(0))).getChildAt(1).setTranslationX(vpMain.getMeasuredWidth());
                    tlMain.getChildAt(0).setTranslationX(tbMain.getMeasuredWidth());
                    ((ViewGroup) (((ViewGroup) (vpMain.getChildAt(0))).getChildAt(0))).getChildAt(0).setAlpha(0);
                    ((ViewGroup) (((ViewGroup) (vpMain.getChildAt(0))).getChildAt(0))).getChildAt(1).setAlpha(0);
                    tlMain.getChildAt(0).setAlpha(0);


                    tlMain.getChildAt(0).animate().x(0).setDuration(500).setInterpolator(new OvershootInterpolator());
                    ((ViewGroup) (((ViewGroup) (vpMain.getChildAt(0))).getChildAt(0))).getChildAt(0).animate().x(0).setStartDelay(200).setDuration(500).setInterpolator(new OvershootInterpolator());
                    ((ViewGroup) (((ViewGroup) (vpMain.getChildAt(0))).getChildAt(0))).getChildAt(1).animate().x(0).setStartDelay(400).setDuration(500).setInterpolator(new FastOutSlowInInterpolator());
                    tlMain.getChildAt(0).animate().alpha(1).setDuration(500).setInterpolator(new FastOutSlowInInterpolator());
                    ((ViewGroup) (((ViewGroup) (vpMain.getChildAt(0))).getChildAt(0))).getChildAt(0).animate().alpha(1).setStartDelay(300).setDuration(500).setInterpolator(new FastOutSlowInInterpolator());
                    ((ViewGroup) (((ViewGroup) (vpMain.getChildAt(0))).getChildAt(0))).getChildAt(1).animate().alpha(1).setStartDelay(500).setDuration(500).setInterpolator(new FastOutSlowInInterpolator());
                    YoYo.with(Techniques.ZoomInRight).duration(1000).playOn(tbMain.getChildAt(tbMain.getChildCount()-1));
                    ivLogo.setAlpha(0f);
                    ivLogo.setScaleX(0.8f);
                    ivLogo.animate()
                            .alpha(1f)
                            .scaleX(1f)
                            .setStartDelay(200)
                            .setDuration(1000)
                            .setInterpolator(new FastOutSlowInInterpolator());
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                getWindow().getReenterTransition().removeListener(this);
                getWindow().getReenterTransition().setStartDelay(0);
                getWindow().setAllowReturnTransitionOverlap(true);
                configReenterTransition=false;

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CHECK_FREE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().postSticky(new PermissionCheckEvent.PhoneCall.MainActivityToFreeListFragment(EventBus.getDefault().removeStickyEvent(PermissionCheckEvent.PhoneCall.FreeListAdapterToActivity.class).number));
                } else {


                }
                break;
            case REQUEST_CODE_PERMISSION_CHECK_HELP:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().postSticky(new PermissionCheckEvent.PhoneCall.MainActivityToHelpListFragment(EventBus.getDefault().removeStickyEvent(PermissionCheckEvent.PhoneCall.HelpListAdapterToActivity.class).number));
                } else {


                }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fabCenterX>0&&fabCenterY>0) {
            int middle=fabCenterX;
            fabCenterX=fabCenterY;
            fabCenterY=middle;
        }

        outState.putInt(BUNDLE_KEY_FAB_CENTER_X,fabCenterX);
        outState.putInt(BUNDLE_KEY_FAB_CENTER_Y,fabCenterY);
        outState.putBoolean(Constant.MainPage.BUNDLE_KEY_CONFIG_REENTER,configReenterTransition);
        outState.putBoolean(BUNDLE_KEY_FAB_CENTER,fabCenter);
    }



    //------------------------------------------------------------View_interface----------------------------------------------------------------------

    @Override
    public void popSpinnerWithDistricts(String[] districts) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_area, R.id.item_title, districts);
        adapter.setDropDownViewResource(R.layout.spinner_drop);
        spTitle.setAdapter(adapter);
    }

    @Override
    public void initViewPagerWithTabLayout(String[] titles) {
        vpMain.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), titles));
        tlMain.setupWithViewPager(vpMain);
        vpMain.setCurrentItem(0);
        vpMain.setOffscreenPageLimit(3);
        vpMain.setPageTransformer(true, new ZoomOutPageTransformer());

        View[] tabs = new View[3];
        tlMain.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                tlMain.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int j = 0; j < tlMain.getTabCount(); j++) {
                    TabLayout.Tab tab = tlMain.getTabAt(j);
                    try {
                        Class aClass = TabLayout.Tab.class;
                        Field field = aClass.getDeclaredField("mView");
                        field.setAccessible(true);
                        ViewGroup viewGroup = (ViewGroup) field.get(tab);
                        for (int i = 0; i < viewGroup.getChildCount(); i++) {
                            if (viewGroup.getChildAt(i) instanceof TextView) {
                                tabs[j] = viewGroup.getChildAt(i);
                                if (j != 0) {
                                    viewGroup.getChildAt(i).setScaleX(0.7f);
                                    viewGroup.getChildAt(i).setScaleY(0.7f);
                                }
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        tlMain.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (tabs[position] != null) {
                    tabs[position].animate().scaleX(1f).scaleY(1f);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (tabs[position] != null) {
                    tabs[position].animate().scaleX(0.7f).scaleY(0.7f);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void showSnackBar(String info) {
        Snackbar.make(viewRoot, info, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void performFabClick() {
        fabCreate.performClick();
    }

    @Override
    public void startUploadActivity(Class target, int x, int y, int radius,int request) {
        Intent intent=new Intent(this,target);
        intent.putExtra(BUNDLE_KEY_FAB_X,x);
        intent.putExtra(BUNDLE_KEY_FAB_Y,y);
        intent.putExtra(BUNDLE_KEY_RADIUS,radius);
        startActivityForResult(intent,request);
    }


//------------------------------------------------------------View_interface----------------------------------------------------------------------

}




