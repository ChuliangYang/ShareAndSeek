package com.me.cl.capstoneproject.screenwidget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.base.Constant;

import java.util.List;

/**
 * Created by CL on 2/1/18.
 */

public class WidgetListProviderService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListFactory(getApplicationContext(), intent);
    }

    class WidgetListFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context context;
        private Intent intent;
        private List<String> commercialName;
        private List<String> commercialReviews;
        private List<String> freeName;
        private List<String> helpName;
        private String cate;

        public WidgetListFactory(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
            cate=intent.getStringExtra(Constant.SharedPreference.KEY_CATOGERY);
            if (cate.equals(Constant.SharedPreference.CATOGERY_COMMERCIAL)) {
                commercialName=intent.getStringArrayListExtra(Constant.SharedPreference.KEY_COMMERCIAL_NAME);
                commercialReviews=intent.getStringArrayListExtra(Constant.SharedPreference.KEY_COMMERCIAL_REVIEWS);
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_HELP)) {
                helpName=intent.getStringArrayListExtra(Constant.SharedPreference.KEY_HELP_NAME);
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_FREE)) {
                freeName=intent.getStringArrayListExtra(Constant.SharedPreference.KEY_FREE_NAME);
            }
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (cate.equals(Constant.SharedPreference.CATOGERY_COMMERCIAL)) {
                commercialName=intent.getStringArrayListExtra(Constant.SharedPreference.KEY_COMMERCIAL_NAME);
                commercialReviews=intent.getStringArrayListExtra(Constant.SharedPreference.KEY_COMMERCIAL_REVIEWS);
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_HELP)) {
                helpName=intent.getStringArrayListExtra(Constant.SharedPreference.KEY_HELP_NAME);
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_FREE)) {
                freeName=intent.getStringArrayListExtra(Constant.SharedPreference.KEY_FREE_NAME);
            }
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            if (cate.equals(Constant.SharedPreference.CATOGERY_COMMERCIAL)) {
                if (commercialName==null) {
                    return 0;
                }
                return commercialName.size();
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_HELP)) {
                return helpName.size();
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_FREE)) {
                return freeName.size();
            }
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.item_widget);
            if (cate.equals(Constant.SharedPreference.CATOGERY_COMMERCIAL)) {
                rv.setTextViewText(R.id.tv_widget_service_name, commercialName.get(i));
                String review="";
                if (commercialReviews.get(i).equals("0")) {

                } else {
                    rv.setTextViewText(R.id.tv_widget_reviews, commercialReviews.get(i)+" "+getString(R.string.review));
                }
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_HELP)) {
                rv.setTextViewText(R.id.tv_widget_service_name, helpName.get(i));
            } else if (cate.equals(Constant.SharedPreference.CATOGERY_FREE)) {
                rv.setTextViewText(R.id.tv_widget_service_name, freeName.get(i));
            }
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
