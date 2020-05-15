package com.org.gascylindermng.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;

import java.util.ArrayList;

public class ChargeCheckAdapter extends BaseAdapter<CheckItemBean> {

    public boolean checkOK;
    public String nextAreaId;
    public String remark;

    private ArrayList<ProcessNextAreaBean> nextAreaList;
    private Context context;

    private String cyNumber;
    private String setNumber;

    public ChargeCheckAdapter(Context context,
                              ArrayList<CheckItemBean> items,
                              ArrayList<ProcessNextAreaBean> nextAreaList,
                              String cyNum,
                              String setNum,
                              boolean check,
                              String nextAreaId,
                              String remark) {
        super(context);
        this.context = context;
        this.mData.addAll(items);
        this.nextAreaList = nextAreaList;
        this.cyNumber = cyNum;
        this.setNumber = setNum;
        checkOK = check;
        this.nextAreaId = nextAreaId;
        this.remark = remark;
    }

    @Override
    public int getCount() {
        return mData.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return R.layout.item_charge_check_head;
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

            if (nextAreaList != null && nextAreaList.size() > 0) {

                MySimpleSpinnerAdapter spinnerAdapter = new MySimpleSpinnerAdapter(context);
                ArrayList<String> adapterData = new ArrayList<String>();
                for (ProcessNextAreaBean p : nextAreaList) {
                    adapterData.add(p.getAreaName());
                }
                spinnerAdapter.addData(adapterData);
                Spinner spinner = (Spinner)viewHolder.get(R.id.next_area_spinner);
                spinner.setAdapter(spinnerAdapter);
                for (int i = 0; i<nextAreaList.size();i++) {
                    if (nextAreaList.get(i).getAreaId().equals(nextAreaId)) {
                        spinner.setSelection(i, true);
                    }
                }
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
            if (!TextUtils.isEmpty(remark)) {
                remarkET.setText(remark);
            }
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

            TextView cyOrSet = (TextView)holder.get(R.id.check_cy_or_set);
            if (!TextUtils.isEmpty(cyNumber)) {
                cyOrSet.setText("气瓶编号："+cyNumber);
            } else {
                cyOrSet.setText("集格编号："+setNumber);
            }

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


    public ArrayList<ProcessNextAreaBean> getNextAreaList() {
        return nextAreaList;
    }

    public void setNextAreaList(ArrayList<ProcessNextAreaBean> nextAreaList) {
        this.nextAreaList = nextAreaList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getCyNumber() {
        return cyNumber;
    }

    public void setCyNumber(String cyNumber) {
        this.cyNumber = cyNumber;
    }

    public String getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }


}
