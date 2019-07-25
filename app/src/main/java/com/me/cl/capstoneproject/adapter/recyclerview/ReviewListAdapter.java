package com.me.cl.capstoneproject.adapter.recyclerview;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.bean.ReviewBean;
import com.me.cl.capstoneproject.bean.ReviewBeanWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by CL on 11/24/17.
 */

public class ReviewListAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<ReviewBeanWrapper> reviewBeanList;
    private  RequestOptions requestOptions;

    public ReviewListAdapter(Context context, List<ReviewBeanWrapper> reviewBeanList) {
        this.context = context;
        this.reviewBeanList = reviewBeanList;
        requestOptions=new RequestOptions().error(R.drawable.ic_head_avater).centerCrop();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReviewsViewHolder) {
            ReviewBean reviewBean = reviewBeanList.get(position).getReviewBean();
            ReviewsViewHolder viewHolder = (ReviewsViewHolder) holder;
            viewHolder.rvAlbum.setHasFixedSize(true);
            viewHolder.rvAlbum.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            viewHolder.rvAlbum.setAdapter(new ReviewAlbumAdapter(context, reviewBean.getPhotos()));
            if (reviewBean.getPhotos() ==null|| reviewBean.getPhotos().size()==0) {
                viewHolder.rvAlbum.setVisibility(View.GONE);
            } else {
                viewHolder.rvAlbum.setVisibility(View.VISIBLE);
            }
            viewHolder.tvTitle.setText(reviewBean.getName());
            viewHolder.tvSummary.setSingleLine();
            viewHolder.ivExpand.setImageResource(R.drawable.ic_arrow_down_accent);
            viewHolder.rbRating.setRating(Float.parseFloat(reviewBean.getRate()));
            viewHolder.tvDate.setText(reviewBean.getDate());
            if (!TextUtils.isEmpty(reviewBean.getContent())) {
                viewHolder.clSummary.setVisibility(View.VISIBLE);
                viewHolder.tvSummary.setText(reviewBean.getContent());
            } else {
                viewHolder.clSummary.setVisibility(View.GONE);
            }
            Glide.with(context).setDefaultRequestOptions(requestOptions).load(reviewBean.getAvatar()).into(viewHolder.ivAvatar);

            if (reviewBeanList.get(position).getExpandState() == 0) {
                viewHolder.tvSummary.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        viewHolder.tvSummary.getViewTreeObserver().removeOnPreDrawListener(this);
                        Layout l=viewHolder.tvSummary.getLayout();
                        if (l != null) {
                            int lines = l.getLineCount();
                            if (lines > 0) {
                                if (l.getEllipsisCount(lines - 1) > 0) {
                                    viewHolder.ivExpand.setVisibility(View.VISIBLE);
                                    reviewBeanList.get(position).setExpandState(2);
                                } else {
                                    viewHolder.ivExpand.setVisibility(View.GONE);
                                    reviewBeanList.get(position).setExpandState(1);
                                }
                            } else {
                                viewHolder.ivExpand.setVisibility(View.GONE);
                                reviewBeanList.get(position).setExpandState(1);
                            }
                        } else {
                            viewHolder.ivExpand.setVisibility(View.GONE);
                            reviewBeanList.get(position).setExpandState(1);

                        }

                        return true;
                    }
                });
            } else {
                switch (reviewBeanList.get(position).getExpandState()){
                    case 1:
                        viewHolder.ivExpand.setVisibility(View.GONE);
                        break;
                    case 2:
                        viewHolder.ivExpand.setVisibility(View.VISIBLE);
                        viewHolder.tvSummary.setSingleLine(true);
                        break;
                    case 3:
                        viewHolder.ivExpand.setVisibility(View.VISIBLE);
                        viewHolder.tvSummary.setSingleLine(false);
                        break;
                }
            }

            ((ReviewsViewHolder) holder).ivExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reviewBeanList.get(position).getExpand() != null) {
                        reviewBeanList.get(position).setExpand(!reviewBeanList.get(position).getExpand());
                    } else {
                        reviewBeanList.get(position).setExpand(false);
                    }


                    TransitionSet transitionSet=new TransitionSet();
                    transitionSet.addTransition(new AutoTransition());
                    transitionSet.setDuration(300);
                    transitionSet.addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {
                            if (reviewBeanList.get(position).getExpand()) {
                                //变到下
                                viewHolder.ivExpand.setImageDrawable(context.getDrawable(R.drawable.vector_arrow_up_to_down_accent));
                                ((Animatable)(viewHolder.ivExpand.getDrawable())).start();
                            } else {
                                //变到上
                                viewHolder.ivExpand.setImageDrawable(context.getDrawable(R.drawable.vector_arrow_down_to_up_accent));
                                ((Animatable)(viewHolder.ivExpand.getDrawable())).start();
                            }
                        }

                        @Override
                        public void onTransitionEnd(Transition transition) {
                            transitionSet.removeListener(this);
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
                    TransitionManager.beginDelayedTransition(((ReviewsViewHolder) holder).clReview,transitionSet);
                    viewHolder.tvSummary.setSingleLine(reviewBeanList.get(position).getExpand());
                    if (reviewBeanList.get(position).getExpand()) {
                        reviewBeanList.get(position).setExpandState(2);
                    } else {
                        reviewBeanList.get(position).setExpandState(3);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return reviewBeanList != null ? reviewBeanList.size() : 0;
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.rb_rating)
        RatingBar rbRating;
        @BindView(R.id.tv_summary)
        TextView tvSummary;
        @BindView(R.id.cl_summary)
        ConstraintLayout clSummary;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.rv_album)
        RecyclerView rvAlbum;
        @BindView(R.id.iv_expand)
        ImageView ivExpand;
        @BindView(R.id.cl_review)
        ConstraintLayout clReview;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addNewItems(List<ReviewBeanWrapper> reviewBeans) {
        if (reviewBeanList!=null) {
            reviewBeanList.clear();
        }
        reviewBeanList.addAll(reviewBeans);
        notifyDataSetChanged();
    }
}
