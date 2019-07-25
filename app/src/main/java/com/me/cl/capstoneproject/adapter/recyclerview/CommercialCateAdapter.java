package com.me.cl.capstoneproject.adapter.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.me.cl.capstoneproject.R;
import com.me.cl.capstoneproject.bean.CommercialCategory;
import com.me.cl.capstoneproject.event.StartEvent;
import com.me.cl.capstoneproject.ui.list.CommercialListActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.me.cl.capstoneproject.base.Constant.ListPage.BUNDLE_KEY_CATE;

/**
 * Created by CL on 11/5/17.
 */

public class CommercialCateAdapter extends RecyclerView.Adapter {
    private List<CommercialCategory> commercialCategories;
    private Context context;

    public CommercialCateAdapter(Context context,List<CommercialCategory> commercialCategories) {
        this.commercialCategories=commercialCategories;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommercialCateHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commercial_category, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommercialCateHolder) {
            CommercialCateHolder commercialCateHolder= (CommercialCateHolder) holder;
            commercialCateHolder.tvCato.setText(commercialCategories.get(position).getCategory());
            commercialCateHolder.tvCato.setCompoundDrawablesWithIntrinsicBounds(null,context.getResources().getDrawable(commercialCategories.get(position).getImgId()),null,null);
        }

    }

    @Override
    public int getItemCount() {
        return commercialCategories.size();
    }

    public class CommercialCateHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_cato)
        TextView tvCato;

        public CommercialCateHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvCato.setWidth(context.getResources().getDisplayMetrics().widthPixels/4);
            tvCato.setHeight(context.getResources().getDisplayMetrics().widthPixels/4*4/5);
            itemView.setOnClickListener(v -> {
                Intent intent=new Intent(context, CommercialListActivity.class);
                intent.putExtra(BUNDLE_KEY_CATE,commercialCategories.get(getAdapterPosition()).getCategory());
                EventBus.getDefault().postSticky(new StartEvent.Activity(intent, StartEvent.Activity.activity.MainActivity, true));
//                context.startActivity(intent);
            });
        }
    }

    @VisibleForTesting
    public List<CommercialCategory> getList(){
        return commercialCategories;
    }

}