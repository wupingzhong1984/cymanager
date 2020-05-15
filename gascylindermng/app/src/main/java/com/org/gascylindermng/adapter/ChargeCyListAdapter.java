package com.org.gascylindermng.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.CylinderInfoBean;

public class ChargeCyListAdapter extends BaseAdapter<CylinderInfoBean> {

    public Context context;
    private AdapterClickListener listener;
    public String pinlessObject;

    public boolean canCheckEdit = false;
    public boolean canDeleteCy = false;

    public interface AdapterClickListener{
        public void deleteClicked(int position);  //自行配置参数  需要传递到activity的值
        public void checkEditClicked(int position);  //自行配置参数  需要传递到activity的值
    }


    public ChargeCyListAdapter(Context context, String pinlessObject, boolean check, boolean deleteCy, AdapterClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.pinlessObject = pinlessObject;
        canCheckEdit = check;
        canDeleteCy = deleteCy;
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        int itemLayoutType = getItemViewType(position);
        ViewHolder viewHolder = null;
        boolean isRecycle = false;
        view = mInflater.inflate(getItemLayoutId(itemLayoutType), null);
        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        final TextView delete = (TextView)viewHolder.get(R.id.cy_list_delete_btn);
        if (canDeleteCy) {
            delete.setVisibility(View.VISIBLE);
            delete.setTag(position);
            delete.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    final int position = (int) view.getTag();

                    if (listener != null) {
                        listener.deleteClicked(position);
                    }
                }
            });
        } else {
            delete.setVisibility(View.GONE);
        }

        final TextView editCheck = (TextView)viewHolder.get(R.id.cy_list_edit_check_btn);
        if (canCheckEdit) {
            editCheck.setVisibility(View.VISIBLE);
            editCheck.setTag(position);
            editCheck.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    final int position = (int) view.getTag();

                    if (listener != null) {
                        listener.checkEditClicked(position);
                    }
                }
            });
        } else {
            editCheck.setVisibility(View.GONE);
        }

        handleItem(itemLayoutType, position, mData.get(position), viewHolder, isRecycle);

        return view;
    }

    @Override
    public void handleItem(int itemViewType, int position, CylinderInfoBean item, ViewHolder holder, boolean isRecycle) {

        TextView number = holder.get(R.id.platform_cy_code);
        if (!TextUtils.isEmpty(item.getSetNumber())){
            number.setText(item.getPlatformCyCode()+" 集格:"+item.getSetNumber());
        } else {
            number.setText(item.getPlatformCyCode());
        }

        TextView code = holder.get(R.id.company_relate_code);
        if (pinlessObject.equals("0")) {
            code.setText(item.getBottleCode());
        } else {
            code.setText(item.getCompanyRelateCode());
        }

        TextView mediumCategory = holder.get(R.id.medium_category);
        mediumCategory.setText(item.getCyMediumName() + " / " + item.getCyCategoryName());

        TextView next = holder.get(R.id.next_regular_date);
        next.setText(item.getNextRegularInspectionDate().substring(0,7));

        TextView expiry = holder.get(R.id.expiry_date);
        expiry.setText(item.getScrapDate().substring(0,7));

    }
}
