package com.me.cl.capstoneproject.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

//支持GridLayoutManager的footview加载更多,并且自动管理footview的显示与隐藏

public class AutoLoadMoreRecyclerView extends RecyclerView {
    private Boolean isLoading = false;
    private OnLoadMoreListener onLoadMoreListener;
    private Boolean isScroll = false;
    private int beforeLoadAmount = -1;
    private int scrollFromBottomViewTop = 0;
    private Boolean startCount = false;
    private GridLayoutManager gridLayoutManager;
    private Boolean showBottomView = true;
    private boolean canLoadMore = true;
    private int scorllY = 0;
    public final static int SUPPORT_FOOT_VIEW_TYPE=-2;

    public AutoLoadMoreRecyclerView(Context context) {
        super(context);
    }

    public AutoLoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (canLoadMore) {
            canLoadMore();
        }
    }

    public AutoLoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        if (getLayoutManager() != null && getLayoutManager() instanceof GridLayoutManager) {
            ((GridLayoutManager) getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (getAdapter().getItemViewType(position)) {
                        case SUPPORT_FOOT_VIEW_TYPE:
                            if (canLoadMore) {
                                return 2;
                            } else {
                                return 0;
                            }

                        default:return 1;
                 }
                }
            });
        }
        super.onAttachedToWindow();
    }

    private void canLoadMore() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != SCROLL_STATE_IDLE) {
                    isScroll = true;
                    showBottomView = true;
                } else {
                    isScroll = false;
                }


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scorllY += dy;
                Log.v("onScrolled:  scrolly ", String.valueOf(scorllY));
                gridLayoutManager = (GridLayoutManager) getLayoutManager();
                if (!isScroll) {
                    hideBottomView();
                    Log.v("recyclerview", "---------------------------------------------------控件没有滑动---------------------------------------");

                }
                if (beforeLoadAmount < getAdapter().getItemCount() && beforeLoadAmount != -1) {
                    isLoading = false;
                    beforeLoadAmount = -1;
                    hideBottomView();
                }
                if (showBottomView) {
                    if (gridLayoutManager != null && getAdapter() != null && gridLayoutManager.findLastCompletelyVisibleItemPosition() >= (getAdapter().getItemCount() - 2)) {
                        if (getBottomView() != null) {
                            getBottomView().setVisibility(View.VISIBLE);
                            Log.v("recyclerview", "---------------------------------------------------显示footview---------------------------------------");
                            showBottomView = false;
                        }
                    }
                }
                if (isBottomViewVisible() && isScroll) {//recyclerview初始化会在用户未滑动的情况下自动调用几次onScrolled（），而且此时由于没有数据，底部view算是可见，加入isScroll来准确判断用户操作引发的滑动，避免因为这种BUG引发的误判
                    Log.v("recyclerview", "------------------------------------footview可见---------------------------------------");
                    if (startCount) {
                        scrollFromBottomViewTop += dy;
                        Log.v("scrollFromBottomViewTop", String.valueOf(scrollFromBottomViewTop));
                    }
                    if (!isLoading && (scrollFromBottomViewTop >= (getBottomView().getHeight() * 2 / 3))) {
                        Log.v("recyclerview", "---------------------------------------------------footview超过一半可见，开始刷新-----------------------------------------");
                        Log.v("recyclerview", "BottomView的高度为" + getBottomView().getHeight());
                        onLoadMoreListener.onLoadMore();
                        isLoading = true;
                        beforeLoadAmount = getAdapter().getItemCount();
                        startCount = false;
                        scrollFromBottomViewTop = 0;
                    }
                    startCount = true;
                }
            }
        });
    }

    private Boolean isBottomViewVisible() {
        gridLayoutManager = (GridLayoutManager) getLayoutManager();
        return gridLayoutManager.findLastVisibleItemPosition() != NO_POSITION && gridLayoutManager.findLastVisibleItemPosition() == (getAdapter().getItemCount() - 1);

    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private View getBottomView() {
        if ((getLayoutManager()).findViewByPosition(getAdapter().getItemCount() - 1) != null) {
            return (getLayoutManager()).findViewByPosition(getAdapter().getItemCount() - 1);
        } else {
            return null;
        }

    }

    public void hideBottomView() {
        if (gridLayoutManager != null && getAdapter() != null && gridLayoutManager.findLastCompletelyVisibleItemPosition() >= (getAdapter().getItemCount() - 2)) {
            if (getBottomView() != null) {
                getBottomView().setVisibility(View.GONE);
            }
            showBottomView = false;
        }

    }

    public void shouldLoadMore(boolean shouldLoadMore) {
        this.canLoadMore = shouldLoadMore;
        if (canLoadMore) {
            canLoadMore();
        } else {
            removeOnScrollListener(null);
        }
    }

    public int getScorllY() {
        return scorllY;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
