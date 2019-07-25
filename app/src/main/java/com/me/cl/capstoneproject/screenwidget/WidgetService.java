package com.me.cl.capstoneproject.screenwidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.base.Constant;
import com.me.cl.capstoneproject.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Date;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class WidgetService extends IntentService {

    public WidgetService() {
        super("WidgetService");
    }

    @Override
    protected void onHandleIntent(Intent mintent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, AppWidget.class));

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.app_widget);
            Intent intent = new Intent(getApplicationContext(), WidgetListProviderService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.fromParts(getString(R.string.content), String.valueOf(appWidgetId + new Date().getTime()), null));

            String cate=(String)(SharedPreferencesHelper.getValueByKey(getApplicationContext(),Constant.SharedPreference.SHARED_PREFERENCES_NAME,Constant.SharedPreference.KEY_CATOGERY,""));
            intent.putExtra(Constant.SharedPreference.KEY_CATOGERY,cate);
            if (cate.equals(Constant.SharedPreference.CATOGERY_COMMERCIAL)) {
                intent.putStringArrayListExtra(Constant.SharedPreference.KEY_COMMERCIAL_NAME, (ArrayList<String>) SharedPreferencesHelper.getObject(getApplicationContext(),Constant.SharedPreference.SHARED_PREFERENCES_NAME,Constant.SharedPreference.KEY_COMMERCIAL_NAME));
                intent.putStringArrayListExtra(Constant.SharedPreference.KEY_COMMERCIAL_REVIEWS, (ArrayList<String>) SharedPreferencesHelper.getObject(getApplicationContext(),Constant.SharedPreference.SHARED_PREFERENCES_NAME,Constant.SharedPreference.KEY_COMMERCIAL_REVIEWS));
                views.setTextViewText(R.id.tv_widget_service,getString(R.string.commercial) );
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_FREE)) {
                intent.putStringArrayListExtra(Constant.SharedPreference.KEY_FREE_NAME, (ArrayList<String>) SharedPreferencesHelper.getObject(getApplicationContext(),Constant.SharedPreference.SHARED_PREFERENCES_NAME,Constant.SharedPreference.KEY_FREE_NAME));
                views.setTextViewText(R.id.tv_widget_service,getString(R.string.free) );
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_HELP)) {
                intent.putStringArrayListExtra(Constant.SharedPreference.KEY_HELP_NAME, (ArrayList<String>) SharedPreferencesHelper.getObject(getApplicationContext(),Constant.SharedPreference.SHARED_PREFERENCES_NAME,Constant.SharedPreference.KEY_HELP_NAME));
                views.setTextViewText(R.id.tv_widget_service,getString(R.string.help) );
            }

            views.setRemoteAdapter(R.id.lv_widget_list,intent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_widget_list);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}
