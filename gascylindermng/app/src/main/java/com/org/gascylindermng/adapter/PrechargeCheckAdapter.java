package com.org.gascylindermng.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.activity.PrechargeCheckActivity;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.ProcessBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;

import java.util.ArrayList;

public class PrechargeCheckAdapter extends BaseAdapter<CheckItemBean> {

    public int scanCount;
    public int setCount;
    public int cyCount;
    public int allCyCount;

    public ArrayList<ProcessNextAreaBean> nextAreaList;

    public Context context;
    public boolean checkOK = true;
    public boolean isEmptyCy = true;
    public String nextAreaId;
    public String remark;

    private boolean canEmpty;

    public PrechargeCheckAdapter(Context context,ArrayList<CheckItemBean> items,boolean canEmpty) {
        super(context);
        this.context = context;
        this.remark = "";
        this.nextAreaList = new ArrayList<ProcessNextAreaBean>();
        this.mData.addAll(items);
        this.canEmpty = canEmpty;
    }

    @Override
    public int getCount() {
        return mData.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return R.layout.item_pre_pro_charge_header;
        } else if(position == mData.size()+1) {
            return R.layout.item_prechargecheck_bottom;
        } else {
            return R.layout.item_check;
        }
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

        if (position == 0) {
            handleItem(itemLayoutType, position, null, viewHolder, isRecycle);
        } else if (position == mData.size()+1) {
            final ImageView cb = (ImageView)viewHolder.get(R.id.check_result_checkbox);
            cb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    if (checkOK == true) {
                        cb.setImageResource(R.mipmap.item_check_off);
                        checkOK = false;
                    } else {
                        cb.setImageResource(R.mipmap.item_check_on);
                        checkOK = true;
                    }
                }
            });

            final ImageView cyEmpty = (ImageView)viewHolder.get(R.id.empty_cy);
            final ImageView cyFull = (ImageView)viewHolder.get(R.id.full_cy);
            final LinearLayout emOrFull = (LinearLayout)viewHolder.get(R.id.empty_or_full);
            cyEmpty.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    isEmptyCy = true;
                    cyEmpty.setImageResource(R.mipmap.item_check_on);
                    cyFull.setImageResource(R.mipmap.item_check_off);
                }
            });
            cyFull.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    isEmptyCy = false;
                    cyEmpty.setImageResource(R.mipmap.item_check_off);
                    cyFull.setImageResource(R.mipmap.item_check_on);
                }
            });

            if (!canEmpty) {
                emOrFull.setVisibility(View.GONE);
            }

            if (nextAreaList.size() > 0) {

                MySimpleSpinnerAdapter spinnerAdapter = new MySimpleSpinnerAdapter(context);
                ArrayList<String> adapterData = new ArrayList<String>();
                for (ProcessNextAreaBean p : nextAreaList) {
                    adapterData.add(p.getAreaName());
                }
                spinnerAdapter.addData(adapterData);
                Spinner spinner = (Spinner)viewHolder.get(R.id.next_area_spinner);
                spinner.setAdapter(spinnerAdapter);
                spinner.setSelection(0, true);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        ProcessNextAreaBean pB = nextAreaList.get(position);
                        nextAreaId = pB.getAreaId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            EditText remarkET = (EditText)viewHolder.get(R.id.edittext_remark);
            remarkET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    remark = s.toString().trim();
                }
            });

            handleItem(itemLayoutType, position, null, viewHolder, isRecycle);
        } else {
            final ImageView cb = (ImageView)viewHolder.get(R.id.check_checkbox);
            cb.setTag(position-1);
            cb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    int index = (int)view.getTag();
                    CheckItemBean ci = mData.get(index);
                    if (ci.isState() == true) {
                        cb.setImageResource(R.mipmap.item_check_off);
                        ci.setState(false);
                    } else {
                        cb.setImageResource(R.mipmap.item_check_on);
                        ci.setState(true);
                    }
                }
            });

            handleItem(itemLayoutType, position, mData.get(position-1), viewHolder, isRecycle);
        }
        return view;
    }

    @Override
    public void handleItem(int itemViewType, int position, CheckItemBean item, ViewHolder holder, boolean isRecycle) {

        if (position == 0) {

            TextView scanResultCount = (TextView)holder.get(R.id.scan_result_count);
            scanResultCount.setText("扫描："+scanCount+" 散瓶："+cyCount+" 集格："+setCount+" 总气瓶数："+allCyCount+"  >");

        } else if(position == mData.size()+1) {
            ImageView cb = holder.get(R.id.check_result_checkbox);
            if (checkOK == true) {
                cb.setImageResource(R.mipmap.item_check_on);
            } else {
                cb.setImageResource(R.mipmap.item_check_off);
            }
        } else {
            TextView checkTitle = holder.get(R.id.check_title);
            checkTitle.setText(item.getTitle());

            ImageView cb = holder.get(R.id.check_checkbox);
            if (item.isState() == true) {
                cb.setImageResource(R.mipmap.item_check_on);
            } else {
                cb.setImageResource(R.mipmap.item_check_off);
            }
        }

    }

}
