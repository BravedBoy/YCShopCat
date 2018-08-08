package com.ycbjie.ycshopcat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ycbjie.ycshopcat.bean.ShopBean;

import org.yczbj.ycrefreshviewlib.adapter.RecyclerArrayAdapter;
import org.yczbj.ycrefreshviewlib.viewHolder.BaseViewHolder;


public class ShopMoreAdapter extends RecyclerArrayAdapter<ShopBean> {


    public ShopMoreAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(parent);
    }


    public class MyViewHolder extends BaseViewHolder<ShopBean> {

        private final ImageView iv_image;

        MyViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_more_shop);
            iv_image = getView(R.id.iv_image);
        }

        @Override
        public void setData(final ShopBean beans){
            iv_image.setBackgroundResource(beans.getImage());
        }
    }

}
