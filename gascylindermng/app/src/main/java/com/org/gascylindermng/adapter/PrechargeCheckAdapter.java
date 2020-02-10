package com.org.gascylindermng.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.activity.PrechargeCheckActivity;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.ProcessBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;
import com.org.gascylindermng.presenter.UserPresenter;

import java.util.ArrayList;

public class PrechargeCheckAdapter extends BaseAdapter<CheckItemBean> {

    public CylinderInfoBean cyInfo;

    public ArrayList<ProcessNextAreaBean> nextAreaList;

    public Context context;
    public boolean checkOK = true;
    public String nextAreaId;
    public String remark;

    private UserPresenter userPresenter;

    public PrechargeCheckAdapter(Context context) {
        super(context);
        this.context = context;
        this.remark = "";
        this.userPresenter = new UserPresenter(null);
        this.nextAreaList = new ArrayList<ProcessNextAreaBean>();
    }

    @Override
    public int getCount() {

        if (cyInfo == null) {
            return 1;
        } else {
            return mData.size()+2;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (cyInfo == null) {
                return R.layout.item_start_scanner;
            } else {
                return R.layout.item_cylinder_baseinfo;
            }
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

            if (nextAreaList.size() > 0) {

                MySimpleSpinnerAdapter spinnerAdapter = new MySimpleSpinnerAdapter(context);
                ArrayList<String> adapterData = new ArrayList<String>();
                for (ProcessNextAreaBean p : nextAreaList) {
                    adapterData.add(p.getAreaName());
                }
                spinnerAdapter.addData(adapterData);
                Spinner spinner = (Spinner)viewHolder.get(R.id.next_process_spinner);
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
            if (cyInfo == null) {

            } else {
                TextView cylinderPlatformCode = (TextView)holder.get(R.id.platform_cy_code);
                cylinderPlatformCode.setText(cyInfo.getPlatformCyCode());
                TextView companyRelateCode = (TextView)holder.get(R.id.company_relate_code);
                if (userPresenter.querComapny().getPinlessObject().equals("0")) {
                    companyRelateCode.setText(cyInfo.getBottleCode());
                } else {
                    companyRelateCode.setText(cyInfo.getCompanyRelateCode());
                }

                TextView medium = (TextView)holder.get(R.id.medium);
                medium.setText(cyInfo.getCyMediumName());
                TextView category = (TextView)holder.get(R.id.cy_category);
                category.setText(cyInfo.getCyCategoryName());
                TextView regularDate = (TextView)holder.get(R.id.next_regular_date);
                regularDate.setText(cyInfo.getNextRegularInspectionDate().substring(0,7));
                TextView expiryDate = (TextView)holder.get(R.id.expiry_date);
                expiryDate.setText(cyInfo.getScrapDate().substring(0,7));
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

}
