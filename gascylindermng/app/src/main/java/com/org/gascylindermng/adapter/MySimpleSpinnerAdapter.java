package com.org.gascylindermng.adapter;

import android.content.Context;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseAdapter;

public class MySimpleSpinnerAdapter extends BaseAdapter<String> {

    public MySimpleSpinnerAdapter(Context context) {

        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.simple_spinner_item;
    }

    @Override
    public int getItemLayoutId(int getItemViewType) {
        return getItemViewType;
    }

    @Override
    public void handleItem(int itemViewType, int position, final String item, ViewHolder holder, boolean isRecycle){

        TextView titleTV =  (TextView)holder.get(R.id.title);
        titleTV.setText(item);
    }
}