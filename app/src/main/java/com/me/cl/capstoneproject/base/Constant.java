package com.me.cl.capstoneproject.base;

/**
 * Created by CL on 11/6/17.
 */

public class Constant {
    public final static String MAP_LAST_KEY="lastKey";
    public final static int NOT_FOUND=-11;
    public final static  int TITLE_CHANGE_FROM_MAIN=0;
    public final static  int TITLE_CHANGE_FROM_LIST=1;

    public static class SharedPreference{
        public static String SHARED_PREFERENCES_NAME = "SP";
        public static String KEY_CATOGERY = "KEY_CATOGERY";
        public static String KEY_COMMERCIAL_NAME = "KEY_COMMERCIAL_NAME";
        public static String KEY_FREE_NAME = "KEY_FREE_NAME";
        public static String KEY_HELP_NAME = "KEY_HELP_NAME";
        public static String KEY_COMMERCIAL_REVIEWS = "KEY_COMMERCIAL_REVIEWS";
        public static final String CATOGERY_COMMERCIAL = "CATOGERY_COMMERCIAL";
        public static String CATOGERY_FREE = "CATOGERY_FREE";
        public static String CATOGERY_HELP = "CATOGERY_HELP";
        public static String KEY_CATOGERY_HELP = "KEY_CATOGERY_HELP";
    }


    public static class NetWork{
        public final static String BASE_URL="https://us-central1-cnsy-a98d2.cloudfunctions.net";
    }
    public static class Dialog{
        public final static String PLEASE_WAIT="Please wait";
        public final static String FETCHING="Fetching";
        public final static String FETCHING_FAILED="Fetching failed,please try again!";
        public final static String FETCHING_DISTANCE_FAILED="can't fetch services sorted by distance";
        public final static String SORRY="Sorry";
        public final static String OK="OK";
    }

    public static  class MainPage{
        public final static int EACH_PAGE_COUNT=8;
        public final static int COLUM_COUNT=4;
        public final static int LIST_PAGE_SIZE=30;
        public final static int TAG_KEY_SINGLE_LINE=12;
        public final static int REQUEST_CODE_PERMISSION_CHECK_FREE =1;
        public final static int REQUEST_CODE_PERMISSION_CHECK_HELP=2;
        public static final int REQUEST_CODE_COMMERCIAL_CREATE_SIGN_IN = 111;
        public static final int REQUEST_CODE_FREE_CREATE_SIGN_IN = 112;
        public static final int REQUEST_CODE_HELP_CREATE_SIGN_IN = 113;
        public static final int REQUEST_CODE_COMMENT_PUBLISH_SIGN_IN = 114;
        public static final int REQUEST_CODE_SIGN_IN_MENU = 115;
        public static final int REQUEST_CODE_CREATE_COMMERCIAL = 201;
        public static final int REQUEST_CODE_CREATE_FREE = 202;
        public static final int REQUEST_CODE_CREATE_HELP = 203;
        public static final int REQUEST_CODE_DETAIL = 301;



        public final static String  DATA_KEY_COMMERCIAL_ITEM="commercial_item_data";
        public final static String  CATE_RESTAURANTS="Restaurants";
        public final static String  CATE_BARS="Bars";
        public final static String  CATE_HAIR_SALON="Hair Salon";
        public final static String  CATE_TAKEOUT="Takeout";
        public final static String  CATE_SHOPPING="Shopping";
        public final static String  CATE_NIGHTLIFE="Nightlife";
        public final static String  CATE_HOTEL="Hotel";
        public final static String  CATE_SKIN_CARE="Skin Care";
        public final static String  CATE_OTHER="Other";
        public final static String CACHE_KEY_CURRENT_TITLE_POSITION ="CACHE_KEY_CURRENT_TITLE_POSITION";
        public final static String  CACHE_KEY_CURRENT_TITLE="CACHE_KEY_CURRENT_TITLE";
        public final static String  STATE_KEY_CURRENT_TAG="STATE_KEY_CURRENT_TAG";
        public final static String  STATE_KEY_CURRENT_TAG_TITLE="STATE_KEY_CURRENT_TAG_TITLE";
        public final static String  STATE_KEY_CURRENT_TAG_TITLE_POSITION="STATE_KEY_CURRENT_TAG_TITLE_POSITION";
        public final static String STATE_KEY_SMART_REFRESH_WHEN_FRONT ="STATE_KEY_SMART_REFRESH_WHEN_FRONT";
        public final static String STATE_KEY_FORCE_REFRESH_WHEN_FRONT ="STATE_KEY_FORCE_REFRESH_WHEN_FRONT";
        public final static String  STATE_KEY_FORE_GROUND="STATE_KEY_FORE_GROUND";
        public final static String  BUNDLE_KEY_CONFIG_REENTER="configReenterTransition";
        public final static String  BUNDLE_KEY_FAB_CENTER_X ="fabCenterX";
        public final static String  BUNDLE_KEY_FAB_CENTER_Y ="fabCenterY";
        public final static String BUNDLE_KEY_FAB_CENTER ="fabCenter";
        public final static String  BUNDLE_KEY_LEFT="left";
        public final static String  BUNDLE_KEY_TOP="top";
        public final static String  BUNDLE_KEY_SP_LEFT="sp_left";
        public final static String  BUNDLE_KEY_SP_TOP="sp_top";
        public final static String  BUNDLE_KEY_SP_TITLE="title";
        public final static String  BUNDLE_KEY_SP_TITLE_POSITION="titlePosition";
        public final static String  BUNDLE_KEY_FAB_X="fab_x";
        public final static String  BUNDLE_KEY_FAB_Y="fab_y";
        public final static String  BUNDLE_KEY_RADIUS="radius";
        public final static String BUNDLE_KEY_LIST ="List";
        public final static String BUNDLE_KEY_FORCE_REFRESH ="force_refresh";
        public final static String BUNDLE_KEY_SMART_REFRESH ="smart_refresh";
        public final static String  BUNDLE_KEY_RVSTATE="rvState";
        public final static String  BUNDLE_KEY_SAVE_STATE="save_state";
        public final static String  BUNDLE_KEY_CURRENT_TITLE_POSITION="title_position";
        public final static String  BUNDLE_KEY_CURRENT_TITLE="title";
        public final static String  TAG_UP="up";
        public final static String  TAG_DOWN="down";
        public final static String  NEW_YORK="New York";

    }

    public static class UploadPage{
        public final static int PICK_UP_PHOTO_REQUEST_CODE=1;
        public final static int REQUEST_CODE_PREVIEW=2;
        public final static int REQUEST_CODE_ACCESS_FINE_LOCATION=32;


        public final static String MAP_KEY_AVERAGE="average";
        public final static String MAP_KEY_CATEGORY="Category";
        public final static String MAP_KEY_PHONE="Phone";
        public final static String MAP_KEY_WECHAT="wechat";
        public final static String MAP_KEY_RATE="rate";
        public final static String MAP_KEY_SUMMARY="summary";
        public final static String MAP_KEY_TITLE="Title";
        public final static String MAP_KEY_PHOTOS="Photos";
        public final static String MAP_KEY_STREET="Street";
        public final static String MAP_KEY_CITY="city";
        public final static String MAP_KEY_STATE="state";
        public final static String MAP_KEY_ZIP="zip";
        public final static String MAP_KEY_END="end";
        public final static String MAP_KEY_LATITUDE="MAP_KEY_LATITUDE";
        public final static String MAP_KEY_LONGTITUDE="MAP_KEY_LONGTITUDE";
        public final static String MAP_KEY_LOCATION_LISTENER="MAP_KEY_LOCATION_LISTENER";
        public final static String MAP_KEY_GOOGLE_LOCATION_CLIENT="MAP_KEY_GOOGLE_LOCATION_CLIENT";

        public final static String BUNDLE_KEY_PHOTO_LIST="BUNDLE_KEY_PHOTO_LIST";


        public final static int FLAG_TITLE_TEXT_FIELD=1;
        public final static int FLAG_AVERAGE_TEXT_FIELD=2;
        public final static int FLAG_ADDRESS_TEXT_FIELD=3;
        public final static int FLAG_ZIP_TEXT_FIELD=4;
        public final static int FLAG_PHONE_TEXT_FIELD=5;
        public final static int FLAG_WECHAT_TEXT_FIELD=6;
        public final static int FLAG_SUMMARY_TEXT_FIELD=7;
        public final static int FLAG_TYPE_FIELD=8;
        public final static int FLAG_CITY_FIELD=9;
        public final static int FLAG_END_TIME=10;

        public final static String VAILD_PHONE_PATTERN="^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";

        public final static int GOOGLE_CLIENT_CONNECTED=1;
        public final static int GOOGLE_CLIENT_CONNECTION_SUSPENDED=2;
        public final static int GOOGLE_CLIENT_CONNECTION_FAILED=3;
    }

    public static class DetailPage{
        public final static String BUNDLE_KEY_COMMERCIAL_ITEM="CommercialItem";
        public final static String BUNDLE_KEY_PHOTO_LIST="BUNDLE_KEY_PHOTO_LIST";
        public final static String BUNDLE_KEY_COMMERCIAL_ID="BUNDLE_KEY_COMMERCIAL_ID";
        public final static String BUNDLE_KEY_CURRENT_COLOR="currentColor";
        public final static String BUNDLE_KEY_VIBRANT_COLOR="vibrantColor";
        public final static String BUNDLE_KEY_TEXT_COLOR="textColor";
        public final static String BUNDLE_KEY_APP_BAR_STATE="appbarState";
        public final static String BUNDLE_KEY_AUTO_CHANGE="autoChangeBanner";
        public final static String BUNDLE_KEY_HAS_PHOTO="hasPhoto";
        public final static String BUNDLE_KEY_CACHE_PHONE="cachePhone";
        public final static String INT_KEY_CURRENT_POSITION="INT_KEY_CURRENT_POSITION";
        public final static String MAP_KEY_COMMENT_DIALOG_FRAGMENT="CommentDialogFragment";
        public final static int PICK_UP_PHOTO_REQUEST_CODE=1;
        public final static int REQUEST_CODE_PERMISSION_CHECK_PHONE_CALL =1;
    }

    public static class ListPage{
        public final static int SORT_LIST_BY_RATE=0;
        public final static int SORT_LIST_BY_COSTUME=1;
        public final static int SORT_LIST_BY_DISTANCE=2;
        public final static int SORT_LIST_BY_DEFAULT=3;
        public final static int SORT_LIST_FROM_CACHE=100;
        public final static String BUNDLE_KEY_CATE="BUNDLE_KEY_CATE";
        public final static String TRANSITION_DISTRICT="district";
        public final static String TRANSITION_TOOLBAR="toolbar";
        public final static String CACHE_KEY_SORT_TYPE="CACHE_KEY_SORT_TYPE";
        public final static String CACHE_KEY_PEDING_DISTANCE_SORT="CACHE_KEY_PEDING_DISTANCE_SORT";
        public final static String RX_KEY_COMMERCIAL_LIST="configCommercialListFirstPage";
        public static final int REQUEST_CODE_DETAIL = 301;
    }

    public static class Database{
        public static final String BASE_LIST_LOCATION="commercial/base_list";
        public static final String FREE_LIST_LOCATION="free/list";
        public static final String HELP_LIST_LOCATION="help/list";
        public static final String REVIEW_LIST_LOCATION="commercial/reviews";
        public static final String BASE_LIST_ITEM_AVERAGE_COSTUME="average_costume";
        public static final String BASE_LIST_ITEM_RATE="rate";
    }

}
