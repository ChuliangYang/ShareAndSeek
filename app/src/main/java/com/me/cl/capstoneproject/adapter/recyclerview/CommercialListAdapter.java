package com.me.cl.capstoneproject.adapter.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.bean.CommercialItem;
import com.me.cl.capstoneproject.ui.detail.CommercialDetailActivity;
import com.me.cl.capstoneproject.widget.RatioImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.me.cl.capstoneproject.base.Constant.ListPage.REQUEST_CODE_DETAIL;
import static com.me.cl.capstoneproject.base.Constant.MainPage.DATA_KEY_COMMERCIAL_ITEM;

/**
 * Created by CL on 11/7/17.
 */

public class CommercialListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<CommercialItem> commercialItems;
    private  RequestOptions requestOptions;

    public CommercialListAdapter(Context context, ArrayList<CommercialItem> commercialItems) {
        this.context = context;
        this.commercialItems = commercialItems;
        requestOptions=new RequestOptions().fitCenter();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommercialViewHolder(LayoutInflater.from(context).inflate(R.layout.item_commertial, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommercialViewHolder) {
            CommercialViewHolder commercialViewHolder= (CommercialViewHolder) holder;
            CommercialItem commercialItem=commercialItems.get(position);

            if (commercialItems.get(position).getPhotoBeanList() != null && commercialItem.getPhotoBeanList().size() > 0) {
                commercialViewHolder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(context).load(commercialItems.get(position).getPhotoBeanList().get(0).getUri()).into(commercialViewHolder.ivPhoto);
            } else {
                commercialViewHolder.ivPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
//                Glide.with(context).load(context.getDrawable(R.drawable.no_image_available)).into(commercialViewHolder.ivPhoto);
                commercialViewHolder.ivPhoto.setImageResource(R.drawable.no_image_available);
            }
            if (commercialItem.getAverage_costume() != 0f) {
                commercialViewHolder.tvDollar.setVisibility(View.VISIBLE);
                commercialViewHolder.tvDollar.setText(context.getString(R.string.monetary_unit)+" "+ String.valueOf((commercialItem.getAverage_costume()).intValue()));
            } else {
                commercialViewHolder.tvDollar.setVisibility(View.GONE);
            }
            commercialViewHolder.tvName.setText(commercialItem.getTitle());
            commercialViewHolder.tvSummary.setText(commercialItem.getSummary());
//            if (commercialItem.getReview_count()!=null) {
            if (commercialItem.getReview_count() != 0f) {
                commercialViewHolder.tvReviews.setText(String.valueOf((int) (commercialItem.getReview_count())) + (commercialItem.getReview_count() == 1f ? " "+context.getString(R.string.review) : " "+context.getString(R.string.reviews)));
            } else {
                commercialViewHolder.tvReviews.setText("");
            }
//            }
            commercialViewHolder.tvAddress.setText(String.format("%s %s",commercialItem.getLocation().getStreet(),commercialItem.getLocation().getCity()));
//            if (commercialItem.getRating()!=null) {
            commercialViewHolder.rbReviews.setRating(commercialItem.getRating());
//            }

        }
    }


    @Override
    public int getItemCount() {
        return commercialItems != null ? commercialItems.size() : 0;
    }

    public void setData(ArrayList<CommercialItem> commercialItems){
        this.commercialItems=commercialItems;
//        notifyItemRangeChanged();
//        notifyItemRangeChanged(0,commercialItems.size());
//        notifyItemran
//        notifyDataSetChanged();
    }
    public ArrayList<CommercialItem> getData(){
        return commercialItems;
    }

    public class CommercialViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_photo)
        RatioImageView ivPhoto;
        @BindView(R.id.fl_photo)
        FrameLayout fl_photo;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.rb_reviews)
        RatingBar rbReviews;
        @BindView(R.id.tv_reviews)
        TextView tvReviews;
        @BindView(R.id.tv_dollar)
        TextView tvDollar;
        @BindView(R.id.tv_address)
        TextView tvAddress;
        @BindView(R.id.tv_summary)
        TextView tvSummary;
        @BindView(R.id.cl_backgroud)
        ConstraintLayout clBackgroud;

        public CommercialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(v -> {
                Intent intent=new Intent(context, CommercialDetailActivity.class);
                intent.putExtra(DATA_KEY_COMMERCIAL_ITEM,commercialItems.get(getAdapterPosition()));
//                context.startActivity(intent);
                ActivityOptionsCompat transitionActivityOptions=null;
                Pair statusBar = new Pair<>(itemView.getRootView().findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
                itemView.getRootView().findViewById(android.R.id.statusBarBackground).setTransitionName(Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
                Pair navigationBar = new Pair<>(itemView.getRootView().findViewById(android.R.id.navigationBarBackground),Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                if (commercialItems.get(getAdapterPosition()).getPhotoBeanList() == null || commercialItems.get(getAdapterPosition()).getPhotoBeanList().size() == 0) {
                    Pair second = new Pair<>(fl_photo, ViewCompat.getTransitionName(fl_photo));
                    fl_photo.setTransitionName(ViewCompat.getTransitionName(fl_photo));
                    if (navigationBar.first == null) {
                        transitionActivityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,second,statusBar);
                    } else {
                        itemView.getRootView().findViewById(android.R.id.navigationBarBackground).setTransitionName(Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                        transitionActivityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,second,statusBar,navigationBar);
                    }
                } else {
                    Pair first = new Pair<>(ivPhoto, ViewCompat.getTransitionName(ivPhoto));
                    Pair second = new Pair<>(fl_photo, ViewCompat.getTransitionName(fl_photo));
                    ivPhoto.setTransitionName(ViewCompat.getTransitionName(ivPhoto));
                    fl_photo.setTransitionName(ViewCompat.getTransitionName(fl_photo));
                    if (navigationBar.first == null) {
                        transitionActivityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, first,second,statusBar);
                    } else {
                        itemView.getRootView().findViewById(android.R.id.navigationBarBackground).setTransitionName(Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                        transitionActivityOptions =
                                ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, first,second,statusBar,navigationBar);
                    }

                }
                ActivityCompat.startActivityForResult((Activity) context,
                        intent, REQUEST_CODE_DETAIL,transitionActivityOptions.toBundle());
            });

        }
    }
}
