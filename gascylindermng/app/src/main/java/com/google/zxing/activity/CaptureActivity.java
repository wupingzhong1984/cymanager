package com.google.zxing.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.decoding.RGBLuminanceSource;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.view.ViewfinderView;
import com.org.gascylindermng.R;
import com.org.gascylindermng.bean.CylinderInfoBean;
import com.org.gascylindermng.bean.SetBean;
import com.org.gascylindermng.callback.ApiCallback;
import com.org.gascylindermng.presenter.UserPresenter;
import com.org.gascylindermng.tools.CommonTools;
import com.org.gascylindermng.tools.PermissionPageUtils;
import com.org.gascylindermng.tools.ServiceLogicUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends AppCompatActivity implements Callback, ApiCallback {

    private static final int REQUEST_CODE_SCAN_GALLERY = 100;
    @BindView(R.id.scan_result_count)
    TextView scanResultCount;
    @BindView(R.id.last_msg)
    TextView lastMsg;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private ImageView back;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    public InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private String photo_path;
    private Bitmap scanBitmap;
    public static final int RESULT_CODE_QR_SCAN = 0xA1; //ok
    public static final String INTENT_EXTRA_KEY_QR_SCAN_LIST = "qr_scan_result";
    public static final String INTENT_EXTRA_KEY_QR_SCAN_SET_LIST = "qr_scan_result_set_list";
    public static final String INTENT_EXTRA_KEY_QR_SCAN_CY_LIST = "qr_scan_result_cy_list";
    public static final String INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST = "qr_scan_result_all_cy_list";

    private boolean multiScan = false;
    private boolean isChargeScan = false;
    private ArrayList<String> resultList;
    private ArrayList<SetBean> setList;
    private ArrayList<CylinderInfoBean> cyInfoList;
    private ArrayList<CylinderInfoBean> allCyInfoList;

    private String lastScanSetId;
    private String lastScanCyNum;

    private UserPresenter userPresenter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userPresenter = new UserPresenter(this);

        setContentView(R.layout.activity_scanner);
        ButterKnife.bind(this);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_content);
        back = (ImageView) findViewById(R.id.back_img);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        this.resultList = new ArrayList<String>();
        this.setList = new ArrayList<SetBean>();
        this.cyInfoList = new ArrayList<CylinderInfoBean>();
        this.allCyInfoList = new ArrayList<CylinderInfoBean>();

        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
        bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
        bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
        bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

        String mode = (String) getIntent().getSerializableExtra("mode");
        if (!TextUtils.isEmpty(mode) && mode.equals(ServiceLogicUtils.scan_multi)) {
            multiScan = true;
        } else {
            multiScan = false;
        }

        String isCharge = (String) getIntent().getSerializableExtra("isChargeScan");
        if (!TextUtils.isEmpty(isCharge) && isCharge.equals("1")) {
            isChargeScan = true;
        } else {
            isChargeScan = false;
        }

        scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN_GALLERY:
                    //获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor.moveToFirst()) {
                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    }
                    cursor.close();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            if (result != null) {
//                                Message m = handler.obtainMessage();
//                                m.what = R.id.decode_succeeded;
//                                m.obj = result.getText();
//                                handler.sendMessage(m);
                                Intent resultIntent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString(INTENT_EXTRA_KEY_QR_SCAN_LIST, result.getText());
//                                Logger.d("saomiao",result.getText());
//                                bundle.putParcelable("bitmap",result.get);
                                resultIntent.putExtras(bundle);
                                CaptureActivity.this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                            } else {
                                Message m = handler.obtainMessage();
                                m.what = R.id.decode_failed;
                                m.obj = "Scan failed!";
                                handler.sendMessage(m);
                            }
                        }
                    }).start();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.scanner_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        //quit the scan view
//		cancelScanButton.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				CaptureActivity.this.finish();
//			}
//		});
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();

        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(CaptureActivity.this, "扫描失败!", Toast.LENGTH_SHORT).show();

            LinearLayout llname = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.view_scan_success_dialog, null);
            final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
            img.setImageResource(R.mipmap.scan_fail);
            final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
            text.setText("扫描失败！");
            AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
            final AlertDialog dialog = builder.setView(llname).create();
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 1200);

        } else {

            System.out.println("sssssssssssssssss scan 0 = " + resultString);
            ArrayList<String> cySetIdList = new ArrayList<>();
            ArrayList<String> cyPlatformNumberList = new ArrayList<>();
            ServiceLogicUtils.getCylinderPlatformNumberOrSetIdFromScanResult(resultString, cySetIdList, cyPlatformNumberList);
            if (cySetIdList.size() > 0) {
                this.lastScanSetId = cySetIdList.get(0);
                this.lastScanCyNum = null;
                for (SetBean history:getSetList()) {

                    if (this.lastScanSetId.equals(history.getSetId())) {
                        LinearLayout llname = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.view_scan_success_dialog, null);
                        final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                        img.setImageResource(R.mipmap.scan_success);
                        final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                        text.setText("重复集格！");
                        AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                        final AlertDialog dialog = builder.setView(llname).create();
                        dialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 1500);
                        this.lastScanSetId = null;

                        if (multiScan && handler != null) {

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (handler != null) {
                                            handler.restartPreviewAndDecode(); // 实现多次扫描
                                        }
                                        System.out.println("do...");
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        System.out.println("exception...");
                                    }
                                }
                            }, 2000);

                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CaptureActivity.this.finish();
                                }
                            }, 2000);
                        }
                        return;
                    }

                }

                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行下载操作
                        try {
                            userPresenter.getSetWithCylinderListBySetId(lastScanSetId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else if (cyPlatformNumberList.size() > 0) {
                this.lastScanCyNum = cyPlatformNumberList.get(0);
                this.lastScanSetId = null;
                for (CylinderInfoBean history:getCyInfoList()) {

                    if (this.lastScanCyNum.equals(history.getPlatformCyCode())) {
                        LinearLayout llname = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.view_scan_success_dialog, null);
                        final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                        img.setImageResource(R.mipmap.scan_success);
                        final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                        text.setText("重复散瓶！");
                        AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                        final AlertDialog dialog = builder.setView(llname).create();
                        dialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 1500);
                        this.lastScanCyNum = null;

                        if (multiScan && handler != null) {

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (handler != null) {
                                            handler.restartPreviewAndDecode(); // 实现多次扫描
                                        }
                                        System.out.println("do...");
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        System.out.println("exception...");
                                    }
                                }
                            }, 2000);

                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    CaptureActivity.this.finish();
                                }
                            }, 2000);
                        }
                        return;
                    }

                }

                new Thread() {
                    @Override
                    public void run() {
                        //在子线程中进行下载操作
                        try {
                            userPresenter.getCylinderInfoByPlatformCyNumber(lastScanCyNum);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else {
                this.lastScanSetId = null;
                this.lastScanCyNum = null;

                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                getResultList().add(resultString);
                bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
                bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());

                resultIntent.putExtras(bundle);
                this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());

                LinearLayout llname = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.view_scan_success_dialog, null);
                final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                img.setImageResource(R.mipmap.scan_success);
                final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                text.setText("非气瓶/集格二维码！");
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                final AlertDialog dialog = builder.setView(llname).create();
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 1500);

                if (multiScan && handler != null) {

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (handler != null) {
                                    handler.restartPreviewAndDecode(); // 实现多次扫描
                                }
                                System.out.println("do...");
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                System.out.println("exception...");
                            }
                        }
                    }, 2000);

                } else {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CaptureActivity.this.finish();
                        }
                    }, 2000);
                }
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    public void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public <T> void successful(String api, T success) {

        if (api.equals("getSetWithCylinderListBySetId")) {

            if (success != null && success instanceof SetBean) {

                SetBean set = (SetBean) success;

                if (set.getCylinderList() == null || set.getCylinderList().size() == 0) {
                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    getResultList().add(getLastScanSetId());
                    bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
                    setLastScanSetId(null);
                    resultIntent.putExtras(bundle);
                    this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                    scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());

                    LinearLayout llname = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.view_scan_success_dialog, null);
                    final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                    img.setImageResource(R.mipmap.scan_success);
                    final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                    text.setText("该集格未绑定气瓶，无法使用！");
                    AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                    final AlertDialog dialog = builder.setView(llname).create();
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2000);
                } else if (isChargeScan && CommonTools.lessFourHourBetweenNowAndDate(set.getCylinderList().get(0).getLastFillTime())) {
                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    getResultList().add(getLastScanSetId());
                    bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
                    setLastScanSetId(null);
                    resultIntent.putExtras(bundle);
                    this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                    scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());

                    LinearLayout llname = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.view_scan_success_dialog, null);
                    final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                    img.setImageResource(R.mipmap.scan_success);
                    final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                    text.setText("该集格4小时内已充装。");
                    AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                    final AlertDialog dialog = builder.setView(llname).create();
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2000);
                } else if (isChargeScan && getAllCyInfoList().size()>0 && !getAllCyInfoList().get(0).getCyMediumId().equals(set.getCylinderList().get(0).getCyMediumId())) {

                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    getResultList().add(getLastScanCyNum());
                    bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
                    setLastScanCyNum(null);
                    resultIntent.putExtras(bundle);
                    this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                    scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());

                    LinearLayout llname = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.view_scan_success_dialog, null);
                    final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                    img.setImageResource(R.mipmap.scan_success);
                    final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                    text.setText("该集格气瓶介质id："+set.getCylinderList().get(0).getCyMediumId()+"与任务介质id："+getAllCyInfoList().get(0).getCyMediumId()+"不同，无法添加。");
                    AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                    final AlertDialog dialog = builder.setView(llname).create();
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2000);

                } else {

                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    getResultList().add(getLastScanSetId());
                    getSetList().add(set);
                    for (CylinderInfoBean c : set.getCylinderList()) {
                        getAllCyInfoList().add(c);
                    }
                    bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST, getCyInfoList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
                    setLastScanSetId(null);
                    resultIntent.putExtras(bundle);
                    this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                    scanResultCount.setText("扫描：" + getResultList().size() + " 散瓶：" + getCyInfoList().size() + " 集格：" + getSetList().size() + " 总气瓶数：" + getAllCyInfoList().size());

                    LinearLayout llname = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.view_scan_success_dialog, null);
                    final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                    img.setImageResource(R.mipmap.scan_success);
                    final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                    text.setText("集格扫描成功！集格号：" + set.getSetNumber() + " 绑定气瓶数：" + set.getCylinderList().size());
                    lastMsg.setText(text.getText().toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                    final AlertDialog dialog = builder.setView(llname).create();
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2000);
                }
            } else {

                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                getResultList().add(getLastScanSetId());
                bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
                bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
                setLastScanSetId(null);
                resultIntent.putExtras(bundle);
                this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());

                LinearLayout llname = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.view_scan_success_dialog, null);
                final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                img.setImageResource(R.mipmap.scan_success);
                final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                text.setText("未知集格！");
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                final AlertDialog dialog = builder.setView(llname).create();
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 2000);
            }

            if (multiScan && handler != null) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (handler != null) {
                                handler.restartPreviewAndDecode(); // 实现多次扫描
                            }
                            System.out.println("do...");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.out.println("exception...");
                        }
                    }
                }, 2000);

            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CaptureActivity.this.finish();
                    }
                }, 2000);
            }

        } else if (api.equals("getCylinderInfoByPlatformCyNumber")) {

            if (success != null && success instanceof CylinderInfoBean) {

                CylinderInfoBean cy = (CylinderInfoBean) success;

                if (isChargeScan && CommonTools.lessFourHourBetweenNowAndDate(cy.getLastFillTime())) {

                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    getResultList().add(getLastScanCyNum());
                    bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
                    setLastScanCyNum(null);
                    resultIntent.putExtras(bundle);
                    this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                    scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());

                    LinearLayout llname = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.view_scan_success_dialog, null);
                    final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                    img.setImageResource(R.mipmap.scan_success);
                    final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                    text.setText("该气瓶4小时内已充装。");
                    AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                    final AlertDialog dialog = builder.setView(llname).create();
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2000);

                } else if (isChargeScan && getAllCyInfoList().size()>0 && !getAllCyInfoList().get(0).getCyMediumId().equals(cy.getCyMediumId())) {

                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    getResultList().add(getLastScanCyNum());
                    bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
                    bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
                    setLastScanCyNum(null);
                    resultIntent.putExtras(bundle);
                    this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                    scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());

                    LinearLayout llname = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.view_scan_success_dialog, null);
                    final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                    img.setImageResource(R.mipmap.scan_success);
                    final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                    text.setText("该气瓶介质id："+cy.getCyMediumId()+"与任务介质id："+getAllCyInfoList().get(0).getCyMediumId()+"不同，无法添加。");
                    AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                    final AlertDialog dialog = builder.setView(llname).create();
                    dialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2000);

                } else{

                        Intent resultIntent = new Intent();
                        Bundle bundle = new Bundle();
                        getResultList().add(getLastScanCyNum());
                        getCyInfoList().add(cy);
                        getAllCyInfoList().add(cy);
                        bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                        bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                        bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST, getCyInfoList());
                        bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
                        setLastScanCyNum(null);
                        resultIntent.putExtras(bundle);
                        this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                        scanResultCount.setText("扫描：" + getResultList().size() + "，散瓶：" + getCyInfoList().size() + "，集格：" + getSetList().size() + "，总气瓶数：" + getAllCyInfoList().size());

                        LinearLayout llname = (LinearLayout) getLayoutInflater()
                                .inflate(R.layout.view_scan_success_dialog, null);
                        final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                        img.setImageResource(R.mipmap.scan_success);
                        final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                        text.setText("气瓶扫描成功！标签号：" + cy.getPlatformCyCode() +
                                " 介质：" + cy.getCyMediumName() +
                                " 过期时间：" + cy.getNextRegularInspectionDate().substring(0, 7));
                        lastMsg.setText(text.getText().toString());
                        AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                        final AlertDialog dialog = builder.setView(llname).create();
                        dialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 2000);
                    
                }
            } else {

                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                getResultList().add(getLastScanCyNum());
                bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
                bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
                bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
                bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
                setLastScanCyNum(null);
                resultIntent.putExtras(bundle);
                this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());

                LinearLayout llname = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.view_scan_success_dialog, null);
                final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
                img.setImageResource(R.mipmap.scan_success);
                final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
                text.setText("未知气瓶！请绑定气瓶信息");
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                final AlertDialog dialog = builder.setView(llname).create();
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 2000);
            }

            if (multiScan && handler != null) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (handler != null) {
                                handler.restartPreviewAndDecode(); // 实现多次扫描
                            }
                            System.out.println("do...");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.out.println("exception...");
                        }
                    }
                }, 2000);

            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CaptureActivity.this.finish();
                    }
                }, 2000);
            }
        }
    }

    @Override
    public <T> void failure(String api, T failure) {
        if (failure instanceof String && ((String) failure).equals("needUpdate")) {

            updateApp();
            return;
        }

        if(api.equals("getSetWithCylinderListBySetId") || api.equals("getCylinderInfoByPlatformCyNumber")) {

            LinearLayout llname = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.view_scan_success_dialog, null);
            final ImageView img = (ImageView) llname.findViewById(R.id.scan_dialog_icon);
            img.setImageResource(R.mipmap.scan_success);
            final TextView text = (TextView) llname.findViewById(R.id.scan_dialog_text);
            text.setText("未知气瓶/集格！");
            AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
            final AlertDialog dialog = builder.setView(llname).create();
            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 2000);

            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            if (getLastScanCyNum() != null) {
                getResultList().add(getLastScanCyNum());
            } else {
                getResultList().add(getLastScanSetId());
            }
            bundle.putStringArrayList(INTENT_EXTRA_KEY_QR_SCAN_LIST, getResultList());
            bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_SET_LIST, getSetList());
            bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_CY_LIST,getCyInfoList());
            bundle.putSerializable(INTENT_EXTRA_KEY_QR_SCAN_ALL_CY_LIST, getAllCyInfoList());
            resultIntent.putExtras(bundle);
            this.setResult(RESULT_CODE_QR_SCAN, resultIntent);
            setLastScanCyNum(null);
            setLastScanSetId(null);

            scanResultCount.setText("扫描："+getResultList().size()+" 散瓶："+getCyInfoList().size()+" 集格："+getSetList().size()+" 总气瓶数："+getAllCyInfoList().size());

            if (multiScan && handler != null) {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (handler != null) {
                                handler.restartPreviewAndDecode(); // 实现多次扫描
                            }
                            System.out.println("do...");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.out.println("exception...");
                        }
                    }
                }, 2000);

            } else {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CaptureActivity.this.finish();
                    }
                }, 2000);
            }
        }
    }

    public ArrayList<String> getResultList() {
        return resultList;
    }

    public void setResultList(ArrayList<String> resultList) {
        this.resultList = resultList;
    }

    public ArrayList<SetBean> getSetList() {
        return setList;
    }

    public void setSetList(ArrayList<SetBean> setList) {
        this.setList = setList;
    }

    public ArrayList<CylinderInfoBean> getCyInfoList() {
        return cyInfoList;
    }

    public void setCyInfoList(ArrayList<CylinderInfoBean> cyInfoList) {
        this.cyInfoList = cyInfoList;
    }

    public ArrayList<CylinderInfoBean> getAllCyInfoList() {
        return allCyInfoList;
    }

    public void setAllCyInfoList(ArrayList<CylinderInfoBean> allCyInfoList) {
        this.allCyInfoList = allCyInfoList;
    }

    public String getLastScanSetId() {
        return lastScanSetId;
    }

    public void setLastScanSetId(String lastScanSetId) {
        this.lastScanSetId = lastScanSetId;
    }

    public String getLastScanCyNum() {
        return lastScanCyNum;
    }

    public void setLastScanCyNum(String lastScanCyNum) {
        this.lastScanCyNum = lastScanCyNum;
    }

    public void updateApp() {

        int permission = ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // 2、提示  3、禁止
        if (permission != PackageManager.PERMISSION_GRANTED) {
            showToast("请打开此应用的存储权限！");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PermissionPageUtils u = new PermissionPageUtils(getBaseContext());
                    u.jumpPermissionPage();
                }
            }, 1000);

        } else {
            LinearLayout llname = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.view_common_dialog, null);
            final TextView textView = (TextView) llname.findViewById(R.id.message);
            textView.setText("新版本已上线，需要下载安装后才可继续使用。");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.setView(llname).create();
            dialog.show();

            final TextView btnConfirm = (TextView) llname.findViewById(R.id.dialog_btn_confirm);
            btnConfirm.setText("开始下载");
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downLoadApk("http://www.ffqs.cn/cymanager.apk");
                }
            });
        }


    }

    private void downLoadApk(final String downloadUrl) {
        //进度条
        final ProgressDialog pd;
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(downloadUrl, pd);
                    //安装APK
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                }
            }
        }.start();

    }

    private static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "cymanager.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    private void installApk(File file) {

        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    this, "com.org.gascylindermng", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean hasInstallPermission = this.getPackageManager().canRequestPackageInstalls();
                if (!hasInstallPermission) {
                    startInstallPermissionSettingActivity();
                }
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        try {
            this.startActivity(intent);
        } catch (Exception e) {
            Log.d("error:", e.toString());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() { //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }
}