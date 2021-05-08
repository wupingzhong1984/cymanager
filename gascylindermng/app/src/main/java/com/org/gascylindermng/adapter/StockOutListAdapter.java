package com.org.gascylindermng.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.StockOutListBean;

public class StockOutListAdapter extends BaseAdapter<StockOutListBean> {

    public Context context;

    public StockOutListAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getCount() {

        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_stockout;
    }

    @Override
    public int getItemLayoutId(int getItemViewType) {
        return getItemViewType;
    }

    @Override
    public void handleItem(int itemViewType, int position, StockOutListBean item, ViewHolder holder, boolean isRecycle) {

        TextView startTime = holder.get(R.id.start_time);
        startTime.setText(item.getTime());

        TextView count = holder.get(R.id.cy_count);
        count.setText(item.getCylinderCount());

        TextView shipnumber = holder.get(R.id.shipnumber);
        shipnumber.setText(item.getShipNumber());

        TextView remark = holder.get(R.id.remark);
        remark.setText(item.getRemark());

    }

}
