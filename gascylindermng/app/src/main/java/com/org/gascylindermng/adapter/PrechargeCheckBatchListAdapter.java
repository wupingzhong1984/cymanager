package com.org.gascylindermng.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.PrePostchargeCheckBatchBean;
import com.org.gascylindermng.tools.ServiceLogicUtils;

public class PrechargeCheckBatchListAdapter extends BaseAdapter<PrePostchargeCheckBatchBean> {

    public Context context;

    public interface AdapterClickListener{
        public void deleteClicked(int position);  //自行配置参数  需要传递到activity的值
    }

    public PrechargeCheckBatchListAdapter(Context context) {
        super(context);
        this.context = context;

    }

    @Override
    public int getCount() {

        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_pre_charge_batch;
    }

    @Override
    public int getItemLayoutId(int getItemViewType) {
        return getItemViewType;
    }

    @Override
    public void handleItem(int itemViewType, int position, PrePostchargeCheckBatchBean item, ViewHolder holder, boolean isRecycle) {

        TextView startTime = holder.get(R.id.start_time);
        startTime.setText(item.getDate());

        TextView count = holder.get(R.id.cy_count);
        if (!TextUtils.isEmpty(item.getEmpty())) {
            count.setText(item.getCylinderCount() + "  "+(item.getEmpty().equals("1")?"空瓶":"满瓶"));
        } else {
            count.setText(item.getCylinderCount());
        }

        TextView batch = holder.get(R.id.batch);
        batch.setText(item.getBatch());

        TextView remark = holder.get(R.id.remark);
        remark.setText(item.getRemark());

    }
}
