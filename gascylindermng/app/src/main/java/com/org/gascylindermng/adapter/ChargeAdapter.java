package com.org.gascylindermng.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.org.gascylindermng.R;
import com.org.gascylindermng.activity.ChargeActivity;
import com.org.gascylindermng.base.BaseAdapter;
import com.org.gascylindermng.bean.ChargeMissionBean;
import com.org.gascylindermng.bean.CheckItemBean;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.ProcessBean;
import com.org.gascylindermng.bean.ProcessNextAreaBean;
import com.org.gascylindermng.tools.CommonTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChargeAdapter extends BaseAdapter<CheckItemBean> {

    public Context context;
    public ChargeActivity parentActivity;

    private ChargeMissionBean missionInfo;
    public ChargeMissionBean getMissionInfo() {
        return this.missionInfo;
    }
    public void setMissionInfo(ChargeMissionBean missionInfo) {
        this.missionInfo = missionInfo;
    }

    private ArrayList<CylinderInfoBean> newMissionCyList; //new mission cy
    private ArrayList<ProcessNextAreaBean> nextAreaList;

    private String nextAreaId;
    private String remark;
    private Date beginTime;
    private Date endTime;
    private String productBatch;

//    private ViewHolder headHolder;
//    private ViewHolder boomHolder;

    public ChargeAdapter(Context context, ChargeActivity activity) {
        super(context);
        this.context = context;
        this.remark = "";
        this.parentActivity = activity;
        this.newMissionCyList = new ArrayList<CylinderInfoBean>();
        this.nextAreaList = new ArrayList<ProcessNextAreaBean>();
    }

    @Override
    public int getCount() {
        return mData.size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return R.layout.item_charge_head;
        } else if(position == mData.size()+1) {
            return R.layout.item_charge_bottom;
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
         //   this.headHolder = viewHolder;

            TextView checkTitle = viewHolder.get(R.id.cy_count);
            checkTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //parentActivity.showCyListActivity();
                }
            });

            Button startBtn = viewHolder.get(R.id.charge_start);
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (missionInfo != null) {

                        LinearLayout llname = (LinearLayout) parentActivity.getLayoutInflater()
                                .inflate(R.layout.view_common_dialog, null);
                        final TextView textView = (TextView) llname.findViewById(R.id.message);
                        textView.setText("任务已开始，无法修改开始时间。");

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final AlertDialog dialog = builder.setView(llname).create();
                        dialog.show();
                        final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        beginTime = new Date();
                        notifyDataSetChanged();
                    }
                }
            });

            Button endBtn = viewHolder.get(R.id.charge_end);
            endBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (missionInfo == null) {
                        LinearLayout llname = (LinearLayout) parentActivity.getLayoutInflater()
                                .inflate(R.layout.view_common_dialog, null);
                        final TextView textView = (TextView) llname.findViewById(R.id.message);
                        textView.setText("请先创建任务再设定结束时间。");

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final AlertDialog dialog = builder.setView(llname).create();
                        dialog.show();
                        final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        endTime = new Date();
                        notifyDataSetChanged();
                    }
                }
            });

            EditText batchET = viewHolder.get(R.id.edittext_product_batch);
            batchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(missionInfo == null) {
                        productBatch = s.toString().trim();
                    } else {
                        missionInfo.setProductionBatch(s.toString().trim());
                    }
    //                notifyDataSetChanged();
                }
            });

            handleItem(itemLayoutType, position, null, viewHolder, isRecycle);
        } else if (position == mData.size()+1) {

   //         this.boomHolder = viewHolder;
//            if (nextAreaList.size() > 0) {
//
//                MySimpleSpinnerAdapter spinnerAdapter = new MySimpleSpinnerAdapter(context);
//                ArrayList<String> adapterData = new ArrayList<String>();
//                for (ProcessNextAreaBean p : nextAreaList) {
//                    adapterData.add(p.getAreaName());
//                }
//                spinnerAdapter.addData(adapterData);
//                Spinner spinner = (Spinner)viewHolder.get(R.id.next_area_spinner);
//                spinner.setAdapter(spinnerAdapter);
//                spinner.setSelection(0, true);
//                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                        ProcessNextAreaBean pB = nextAreaList.get(position);
//                        nextAreaId = pB.getAreaId();
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//
//                    }
//                });
//
//            }

//            EditText remarkET = (EditText)viewHolder.get(R.id.edittext_charge_remark);
//            remarkET.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                    if(missionInfo == null) {
//                        remark = s.toString().trim();
//                    } else {
//                        missionInfo.setRemark(s.toString().trim());
//                    }
//  //                  notifyDataSetChanged();
//                }
//            });

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
            TextView checkTitle = holder.get(R.id.cy_count);
            int count = 0;
            if (missionInfo == null) {
                count = newMissionCyList.size();

                if(beginTime != null) {

                    TextView textView = holder.get(R.id.start_time);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    textView.setText(sdf.format(beginTime));
                }
                if (endTime != null) {

                    TextView textView = holder.get(R.id.end_time);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    textView.setText(sdf.format(endTime));
                }

                if (productBatch != null) {
                    TextView textView = holder.get(R.id.edittext_product_batch);
                    textView.setText(productBatch);
                }

            } else {
                count = Integer.valueOf(missionInfo.getCylinderCount());

                TextView textView = holder.get(R.id.start_time);
                textView.setText(missionInfo.getBeginDate().substring("yyyy-MM-dd".length()+1,missionInfo.getBeginDate().length()));
                if (endTime != null) {

                    TextView textView2 = holder.get(R.id.end_time);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    textView2.setText(sdf.format(endTime));
                }
                if (!TextUtils.isEmpty(missionInfo.getProductionBatch())) {
                    TextView textView3 = holder.get(R.id.edittext_product_batch);
                    textView3.setText(missionInfo.getProductionBatch());
                }

            }
            checkTitle.setText("本次充装气瓶数："+count+" 个");

        } else if(position == mData.size()+1) {
//            TextView textView = holder.get(R.id.edittext_charge_remark);
//            if (missionInfo == null) {
//                textView.setText(TextUtils.isEmpty(getRemark())?"":getRemark());
//            } else {
//                textView.setText(TextUtils.isEmpty(missionInfo.getRemark())?"":missionInfo.getRemark());
//            }

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

    public ArrayList<CylinderInfoBean> getNewMissionCyList() {
        return newMissionCyList;
    }

    public void setNewMissionCyList(ArrayList<CylinderInfoBean> newMissionCyList) {
        this.newMissionCyList = newMissionCyList;
    }

    public ArrayList<ProcessNextAreaBean> getNextAreaList() {
        return nextAreaList;
    }

    public void setNextAreaList(ArrayList<ProcessNextAreaBean> nextAreaList) {
        this.nextAreaList = nextAreaList;
    }

    public String getNextAreaId() {
        return nextAreaId;
    }

    public void setNextAreaId(String nextAreaId) {
        this.nextAreaId = nextAreaId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getProductBatch() {
        return productBatch;
    }

    public void setProductBatch(String productBatch) {
        this.productBatch = productBatch;
    }
}
