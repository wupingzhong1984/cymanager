package com.org.gascylindermng.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.activity.CaptureActivity;
import com.org.gascylindermng.R;
import com.org.gascylindermng.api.HttpResponseResult;
import com.org.gascylindermng.base.BaseActivity;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.SetBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.CommonTools;
import com.org.gascylindermng.tools.PermissionPageUtils;
import com.org.gascylindermng.tools.ServiceLogicUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_OTHER_SCAN_LIST;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN_SET_LIST;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN_CY_LIST;
import static com.google.zxing.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST;

public class StockOutActivity extends BaseActivity implements ApiCallback {

    @BindView(R.id.title_name)
    TextView titleName;
    @BindView(R.id.cy_count)
    TextView cyCountTV;
    @BindView(R.id.edittext_trans_order)
    EditText edittextTransOrder;
    @BindView(R.id.edittext_remark)
    EditText edittextRemark;

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    private int REQUEST_CODE_2 = 0x02;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    private UserPresenter userPresenter;

    private int scanCount = 0;
    private ArrayList<CylinderInfoBean> newCyList; //new mission cy
    private ArrayList<SetBean> newSetList;
    private ArrayList<CylinderInfoBean> newAllCyList; //new mission cy

    @Override
    protected void initLayout(Bundle savedInstanceState) {
        setContentView(R.layout.activity_stockout);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        titleName.setText("出库");
        userPresenter = new UserPresenter(this);

        this.newCyList = new ArrayList<CylinderInfoBean>();
        this.newSetList = new ArrayList<SetBean>();
        this.newAllCyList = new ArrayList<CylinderInfoBean>();

    }

    @OnClick({R.id.back_img, R.id.start_scanner, R.id.submit, R.id.cy_count})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.start_scanner:
                if (CommonTools.isCameraCanUse()) {
                    Intent intent = new Intent(StockOutActivity.this, CaptureActivity.class);
                    intent.putExtra("mode", ServiceLogicUtils.scan_multi);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    showToast("请打开此应用的摄像头权限！");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            PermissionPageUtils u = new PermissionPageUtils(getBaseContext());
                            u.jumpPermissionPage();
                        }
                    }, 1000);
                }
                break;
            case R.id.submit:
                hideSoftInput();

                if (newAllCyList.size() == 0) {
                    showToast("请先扫描检测对象气瓶二维码");
                    return;
                }

                if (TextUtils.isEmpty(edittextTransOrder.getText().toString())) {
                    showToast("请输入发货单号");
                    return;
                }

                LinearLayout llname = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.view_operation_dialog, null);
                final TextView textView = (TextView) llname.findViewById(R.id.message);
                textView.setText("是否确认信息正确并提交？");

                final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
                btnConfirm.setText("确认");
                final TextView btnCancel = (TextView) llname.findViewById(R.id.dialog_btn_cancel);

                AlertDialog.Builder builder = new AlertDialog.Builder(StockOutActivity.this);
                final AlertDialog dialog = builder.setView(llname).create();
                dialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<String> cyIdList = new ArrayList<String>();
                        for (CylinderInfoBean c : newAllCyList) {
                            cyIdList.add(c.getCyId());
                        }

                        userPresenter.submitStockOutRecord(
                                cyIdList,
                                edittextTransOrder.getText().toString(),
                                edittextRemark.getText().toString());
                        dialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.cy_count:
                if(newAllCyList.size()==0)return;
                Intent intent = new Intent(StockOutActivity.this, ChargeCyListActivity.class);
                intent.putExtra("CyBeanlist", newAllCyList);
                intent.putExtra("canDeleteCy", "1");
                startActivityForResult(intent,REQUEST_CODE_2);
                break;
        }
    }

    @Override
    public <T> void successful(String api, T success) {

        if (api.equals("submitStockOutRecord")) {
            LinearLayout llname = (LinearLayout) this.getLayoutInflater()
                    .inflate(R.layout.view_common_dialog, null);
            final TextView textView = (TextView) llname.findViewById(R.id.message);
            textView.setText("提交成功。");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.setView(llname).create();
            dialog.show();
            final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    closeAndRefreshBatchList();
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    closeAndRefreshBatchList();
                }
            }, 1500);
        }
    }

    @Override
    public <T> void failure(String api, T failure) {

        if (failure instanceof String && ((String)failure).equals("needUpdate")) {

            super.updateApp();
            return;
        }
        if (api.equals("submitStockOutRecord")) {
            showToast("提交失败，" + (String) failure + "，请重新尝试提交。");
        } else {
            showToast("接口报错，" + (String) failure);
        }
    }

    private void closeAndRefreshBatchList() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("needRefresh", "1");
        resultIntent.putExtras(bundle);
        this.setResult(0xA1, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调

        Bundle bundle = data.getExtras();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> result = bundle.getStringArrayList(INTENT_EXTRA_KEY_OTHER_SCAN_LIST);
                if (result != null && result.size() > 0) {
                    scanCount += result.size();
                }

                ArrayList<SetBean> setList = (ArrayList<SetBean>) bundle.getSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST);
                if (setList != null && setList.size() > 0) {
                    getNewSetList().addAll(setList);
                }

                ArrayList<CylinderInfoBean> cyList = (ArrayList<CylinderInfoBean>) bundle.getSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST);
                if (cyList != null && cyList.size() > 0) {
                    getNewCyList().addAll(cyList);
                }

                ArrayList<CylinderInfoBean> allCyList = (ArrayList<CylinderInfoBean>) bundle.getSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST);
                if (allCyList != null && allCyList.size() > 0) {
                    getNewAllCyList().addAll(allCyList);
                }
                cyCountTV.setText("扫描：" + scanCount + "，散瓶：" + getNewCyList().size() + "，集格：" + getNewSetList().size() + "，总气瓶数：" + getNewAllCyList().size()+"  >");
            } else {
                //showToast("扫描失败");
            }
        } else if (requestCode == REQUEST_CODE_2) {

            ArrayList<CylinderInfoBean> result = (ArrayList<CylinderInfoBean>)bundle.getSerializable("CyBeanlist");
            newAllCyList.clear();
            if (result != null && result.size() > 0) {
                newAllCyList.addAll(result);
            }
            if (newAllCyList.size() > 0) {

                for (int i = newSetList.size()-1; i > -1; i--) {
                    boolean hasSet = false;
                    for (CylinderInfoBean c : newAllCyList) {
                        if (!TextUtils.isEmpty(c.getSetId()) && c.getSetId().equals(newSetList.get(i).getSetId())) {
                            hasSet = true;
                            break;
                        }
                    }
                    if (!hasSet) {
                        newSetList.remove(i);
                    }
                }

                for (int i = newCyList.size()-1; i > -1; i--) {

                    boolean hasCy = false;
                    for (CylinderInfoBean c : newAllCyList) {
                        if (c.getCyId().equals(newCyList.get(i).getCyId())) {
                            hasCy = true;
                            break;
                        }
                    }
                    if (!hasCy) {
                        newCyList.remove(i);
                    }
                }

                scanCount = newSetList.size() + newCyList.size();

            } else {
                newCyList.clear();
                newSetList.clear();
                scanCount = 0;
            }
            cyCountTV.setText("扫描：" + scanCount + "，散瓶：" + getNewCyList().size() + "，集格：" + getNewSetList().size() + "，总气瓶数：" + getNewAllCyList().size()+"  >");
        }
    }

    public ArrayList<CylinderInfoBean> getNewCyList() {
        return newCyList;
    }

    public void setNewCyList(ArrayList<CylinderInfoBean> newCyList) {
        this.newCyList = newCyList;
    }

    public ArrayList<SetBean> getNewSetList() {
        return newSetList;
    }

    public void setNewSetList(ArrayList<SetBean> newSetList) {
        this.newSetList = newSetList;
    }

    public ArrayList<CylinderInfoBean> getNewAllCyList() {
        return newAllCyList;
    }

    public void setNewAllCyList(ArrayList<CylinderInfoBean> newAllCyList) {
        this.newAllCyList = newAllCyList;
    }
}
