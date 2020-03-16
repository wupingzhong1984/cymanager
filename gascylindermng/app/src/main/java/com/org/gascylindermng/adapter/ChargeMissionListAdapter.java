package com.org.gascylindermng.adapter;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.ServiceLogicUtils;

public class ChargeMissionListAdapter extends BaseAdapter<ChargeMissionBean> {

    public Context context;
    private AdapterClickListener listener;

    public interface AdapterClickListener{
        public void deleteClicked(int position);  //自行配置参数  需要传递到activity的值
    }

    public ChargeMissionListAdapter(Context context, AdapterClickListener listener) {
        super(context);
        this.context = context;

        this.listener = listener;
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        int itemLayoutType = getItemViewType(position);
        ViewHolder viewHolder = null;
        boolean isRecycle = false;
        view = mInflater.inflate(getItemLayoutId(itemLayoutType), null);
        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        final TextView delete = (TextView)viewHolder.get(R.id.mission_list_delete_btn);
        delete.setTag(position);
        delete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                final int position = (int)view.getTag();

                if(listener!= null) {
                    listener.deleteClicked(position);
                }
            }
        });

        handleItem(itemLayoutType, position, mData.get(position), viewHolder, isRecycle);

        return view;
    }

            @Override
    public void handleItem(int itemViewType, int position, ChargeMissionBean item, ViewHolder holder, boolean isRecycle) {

        TextView startTime = holder.get(R.id.start_time);
        startTime.setText(item.getBeginDate());

        TextView medium = holder.get(R.id.medium);
        medium.setText(item.getMediemName() + "   ("+ ServiceLogicUtils.getPurenessByKey(item.getPureness()).getText()+")");

        TextView cyCount = holder.get(R.id.cy_count);
        cyCount.setText(item.getCylinderCount());

        TextView batch = holder.get(R.id.product_batch);
        batch.setText(item.getProductionBatch());

        TextView state = holder.get(R.id.charge_state);
        state.setText(item.getStatus().equals("1")?"充装中...":"已完成"); //1 未完成 2已完成
        state.setTextColor(context.getResources().getColor(item.getStatus().equals("1")?R.color.red:R.color.myblue));
    }
}
