package com.org.gascylindermng.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.ChargeMissionBean;

public class ChargeMissionListAdapter extends BaseAdapter<ChargeMissionBean> {

    public Context context;

    public ChargeMissionListAdapter(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getCount() {

        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_charge_mission;
    }

    @Override
    public int getItemLayoutId(int getItemViewType) {
        return getItemViewType;
    }

    @Override
    public void handleItem(int itemViewType, int position, ChargeMissionBean item, ViewHolder holder, boolean isRecycle) {

        TextView startTime = holder.get(R.id.start_time);
        startTime.setText(item.getBeginDate());

        TextView medium = holder.get(R.id.medium);
        medium.setText(item.getMediemName());

        TextView cyCount = holder.get(R.id.cy_count);
        cyCount.setText(item.getCylinderCount());

        TextView batch = holder.get(R.id.product_batch);
        batch.setText(item.getProductionBatch());

        TextView state = holder.get(R.id.charge_state);
        state.setText(item.getStatus().equals("1")?"充装中":"已完成"); //1 未完成 2已完成
    }
}
