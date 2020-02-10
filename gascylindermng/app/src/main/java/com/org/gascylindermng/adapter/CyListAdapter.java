package com.org.gascylindermng.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.CylinderInfoBean;

public class CyListAdapter extends BaseAdapter<CylinderInfoBean> {

    public Context context;
    public String pinlessObject;

    public CyListAdapter(Context context, String pinlessObject) {
        super(context);
        this.context = context;
        this.pinlessObject = pinlessObject;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_cylinder;
    }

    @Override
    public int getItemLayoutId(int getItemViewType) {
        return getItemViewType;
    }

    @Override
    public void handleItem(int itemViewType, int position, CylinderInfoBean item, ViewHolder holder, boolean isRecycle) {

        TextView number = holder.get(R.id.platform_cy_code);
        number.setText(item.getPlatformCyCode());

        TextView code = holder.get(R.id.company_relate_code);
        if (pinlessObject.equals("0")) {
            code.setText(item.getBottleCode());
        } else {
            code.setText(item.getCompanyRelateCode());
        }

        TextView medium = holder.get(R.id.medium);
        medium.setText(item.getCyMediumName());

        TextView category = holder.get(R.id.cy_category);
        category.setText(item.getCyCategoryName());

        TextView next = holder.get(R.id.next_regular_date);
        next.setText(item.getNextRegularInspectionDate().substring(0,7));

        TextView expiry = holder.get(R.id.expiry_date);
        expiry.setText(item.getScrapDate().substring(0,7));

    }
}
