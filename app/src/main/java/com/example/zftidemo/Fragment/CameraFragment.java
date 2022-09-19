package com.example.zftidemo.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.zftidemo.R;
import com.example.zftidemo.dao.Constant;
import com.example.zftidemo.utils.ImageRotation;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.usbcameracommon.UvcCameraDataCallBack;
import com.serenegiant.widget.CameraViewInterface;

import java.util.List;
import java.util.TreeMap;

import static com.serenegiant.utils.UIThreadHelper.runOnUiThread;
import static java.lang.Thread.sleep;


public class CameraFragment extends Fragment implements CameraDialog.CameraDialogParent{
    Button btn_front, btn_side, btn_behind,  btn_photo, camera_connect, camera_convert;
    private static final boolean DEBUG = true;
    private static final String TAG = "MainActivity";
    private boolean isPrepared;
    private static final float[] BANDWIDTH_FACTORS = {0.5f, 0.5f};

    // for accessing USB and USB camera
    private String  pathcamera;
    private String  image_save_path;
    private String local = "front";
    private Integer rotation = 0;
    // image save reverse
    //private boolean save_reverse = true;
    public void setmUSBMonitor(USBMonitor mUSBMonitor) {
        this.mUSBMonitor = mUSBMonitor;
    }
    public USBMonitor mUSBMonitor;
    public static UVCCameraHandler mHandlerFirst;
    public CameraViewInterface mUVCCameraViewFirst;
    public Button mCaptureButtonFirst;

    private String connect_order = "first";
    public static UVCCameraHandler mHandlerSecond;
    public CameraViewInterface mUVCCameraViewSecond;
    public String image_name0, image_name1;
    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment fragment;
    //Button btn_start;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.camera_layout,container,false);
        //btn_start = (Button) view.findViewById(R.id.btn_start);
        btn_front = (Button) view.findViewById(R.id.btn_front);
        btn_side = (Button) view.findViewById(R.id.btn_side);
        btn_behind = (Button) view.findViewById(R.id.btn_behind);
        btn_photo = (Button) view.findViewById(R.id.btn_photo);
        btn_front.setOnClickListener(mOnClickListener);
        btn_side.setOnClickListener(mOnClickListener);
        btn_behind.setOnClickListener(mOnClickListener);
        //btn_pre.setOnClickListener(mOnClickListener);
        btn_photo.setOnClickListener(mOnClickListener);

        camera_connect = (Button) view.findViewById(R.id.btn_connect_camera);
        camera_convert = (Button) view.findViewById(R.id.btn_convert);


        camera_connect.setOnClickListener(mOnClickListener);
        camera_convert.setOnClickListener(mOnClickListener);
        camera_convert.setVisibility(View.INVISIBLE);
        pathcamera = this.getResources().getString(R.string.camera_path);
        image_save_path = this.getResources().getString(R.string.image_path);
        // view.findViewById(R.id.RelativeLayout1).setOnClickListener(mOnClickListener);
        rotation = Integer.parseInt(this.getResources().getString(R.string.rotation));
        image_name0 = Constant.save_reverse ? "2.png" : "1.png";
        image_name1 = Constant.save_reverse ? "1.png" : "2.png";
//        mUSBMonitor = new USBMonitor(getContext(), mOnDeviceConnectListener);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        camera_convert.setEnabled(false);
        btn_photo.setEnabled(false);
        resultFirstCamera(view);
        resultSecondCamera(view);
        return view;
    }

    UvcCameraDataCallBack firstDataCallBack = new UvcCameraDataCallBack() {
        @Override
        public void getData(byte[] data) {
             // if (DEBUG) Log.v(TAG, "数据回调:" + data.length);
        }
    };

    private void resultFirstCamera(View view) {
        mUVCCameraViewFirst = (CameraViewInterface) view.findViewById(R.id.camera_view_first);
        //设置显示宽高
        //mUVCCameraViewFirst.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float) UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        mCaptureButtonFirst = (Button)view.findViewById(R.id.btn_photo);
        mCaptureButtonFirst.setOnClickListener(mOnClickListener);
        mCaptureButtonFirst.setVisibility(View.INVISIBLE);
        mHandlerFirst = UVCCameraHandler.createHandler(getActivity(), mUVCCameraViewFirst
                , UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT
                , BANDWIDTH_FACTORS[0],firstDataCallBack);
    }
    private void resultSecondCamera(View view) {
        mUVCCameraViewSecond = (CameraViewInterface)view.findViewById(R.id.camera_view_second);
        mHandlerSecond = UVCCameraHandler.createHandler(getActivity(), mUVCCameraViewSecond
                , UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT
                , BANDWIDTH_FACTORS[1]);
    }

    public final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
//            if (DEBUG) Log.v(TAG, "onAttach:" + device);
//            Toast.makeText(getActivity(), "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            //设备连接成功
            try {
                if (connect_order.equals("first") && !mHandlerFirst.isOpened() && device.equals(mDeviceListAdapter.getItem(Constant.camera_reverse ? 1 : 0))){
                    mHandlerFirst.open(ctrlBlock);
                    final SurfaceTexture st1 = mUVCCameraViewFirst.getSurfaceTexture();
                    mHandlerFirst.startPreview(new Surface(st1));
                }else if (connect_order.equals("second") && !mHandlerSecond.isOpened() && device.equals(mDeviceListAdapter.getItem(Constant.camera_reverse ? 0 : 1))){
                    mHandlerSecond.open(ctrlBlock);
                    final SurfaceTexture st2 = mUVCCameraViewSecond.getSurfaceTexture();
                    mHandlerSecond.startPreview(new Surface(st2));
                }
            }catch (Exception e){
                Log.v(TAG, "摄像头连接失败");
            }finally {
                camera_connect.setEnabled(true);
            }

        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
//            if (DEBUG) Log.v(TAG, "onDisconnect:" + device);
//            if ((mHandlerFirst != null) && !mHandlerFirst.isEqual(device)) {
//                queueEvent(new Runnable() {
//                    @Override
//                    public void run() {
//                        mHandlerFirst.close();
//                        if (mFirstPreviewSurface != null) {
//                            mFirstPreviewSurface.release();
//                            mFirstPreviewSurface = null;
//                        }
//                        setCameraButton();
//                    }
//                });
//            } else if ((mHandlerSecond != null) && !mHandlerSecond.isEqual(device)) {
//                queueEvent(new Runnable() {
//                    @Override
//                    public void run() {
//                        mHandlerSecond.close();
//                        if (mSecondPreviewSurface != null) {
//                            mSecondPreviewSurface.release();
//                            mSecondPreviewSurface = null;
//                        }
//                        setCameraButton();
//                    }
//                });
//            }
        }

        @Override
        public void onDettach(final UsbDevice device) {
//            if (DEBUG) Log.v(TAG, "onDettach:" + device);
//            Toast.makeText(getActivity(), "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(final UsbDevice device) {
            if (DEBUG) Log.v(TAG, "onCancel:"+ device);
        }
    };
    private CameraDialog.DeviceListAdapter mDeviceListAdapter;
    @Override
    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCameraButton();
                }
            }, 0);
        }
    }
    public void setCameraButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((mHandlerFirst != null) && !mHandlerFirst.isOpened() && (mCaptureButtonFirst != null)) {
                    mCaptureButtonFirst.setVisibility(View.INVISIBLE);
                }
            }
        }, 0);
    }
    //单击”拍照“后调用相机
    //相机调用代码待定
    public final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            /*case R.id.btn_start:
                //创建task的函数
                break;*/
                case R.id.btn_front:
                    //拍摄物体正面
                    local = "front";
                    break;
                case R.id.btn_side:
                    //拍摄物体侧面
                    local = "side";
                    break;
                case R.id.btn_behind:
                    //拍摄物体后面
                    local = "back";
                    break;
                case R.id.btn_connect_camera:
                    if (camera_connect.getText().toString().equals("连接")){
                        try {
                            camera_connect.setEnabled(false);
                            ConnectUSB0();
                            Thread.sleep(200);
                            ConnectUSB1();
                            Thread.sleep(200);
                            if(mHandlerFirst.isOpened() && mHandlerSecond.isOpened()) {
                                mCaptureButtonFirst.setVisibility(View.VISIBLE);
                                camera_connect.setText("断开");
                                camera_convert.setVisibility(View.VISIBLE);
                                camera_convert.setEnabled(true);
                                mCaptureButtonFirst.setEnabled(true);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            camera_connect.setEnabled(true);
                        }
                    }else if(camera_connect.getText().toString().equals("断开")){
                        try{
                            camera_connect.setEnabled(false);
                            DisConnect();
                            checkConnectStatus();

                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            camera_connect.setEnabled(true);
                        }
                    }

                    break;
                case R.id.btn_convert:
                    Constant.camera_reverse = !Constant.camera_reverse;
                    camera_convert.setEnabled(false);
                    camera_connect.setEnabled(false);
                    mCaptureButtonFirst.setEnabled(false);
                    try {
                        DisConnect();
                        ConnectUSB0();
                        ConnectUSB1();
                        camera_convert.setEnabled(true);
                        mCaptureButtonFirst.setEnabled(true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        camera_connect.setEnabled(true);
                    }
                    break;
                case R.id.btn_photo:
                    //拍摄照片
                    if (mHandlerFirst!= null) {
                        if (mHandlerFirst.isOpened()) {
                            if (checkPermissionWriteExternalStorage()) {
                                mHandlerFirst.captureStill(pathcamera+"/"+local+"/"+image_name0);//()中写入保存文件夹
                                Thread rotationImage0 = new ImageRotation(pathcamera+"/"+local+"/"+image_name0, image_save_path+"/"+local+"/"+image_name0, rotation);
                                rotationImage0.start();
                                Toast.makeText(getContext(),"图片拍照完成", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(),"checkPermissionWriteExternalStorage():false", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if (mHandlerSecond != null) {
                        if (mHandlerSecond.isOpened()) {
                            if (checkPermissionWriteExternalStorage()) {
                                mHandlerSecond.captureStill(pathcamera+"/"+local+"/"+image_name1);
                                Thread rotationImage1 = new ImageRotation(pathcamera+"/"+local+"/"+image_name1, image_save_path+"/"+local+"/"+image_name1, rotation);
                                rotationImage1.start();
                                // Toast.makeText(getContext(),"图片二拍照完成", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
            }
        }
    };
    protected boolean checkPermissionWriteExternalStorage(){
        PackageManager pm = getContext().getPackageManager();
        int check = pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", getContext().getPackageName());
        if (check == 0){
            return true;
        }
        else
            return false;
    }
    public void ConnectUSB0(){
        try{
            if (mHandlerFirst != null) {
                if (!mHandlerFirst.isOpened()) {
                    if (mDeviceListAdapter == null){
                        List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(getActivity(), com.serenegiant.uvccamera.R.xml.device_filter);
                        mDeviceListAdapter = new CameraDialog.DeviceListAdapter(getActivity(), mUSBMonitor.getDeviceList(filter.get(0)));
                    }
                    connect_order = "first";
                    UsbDevice device0 = mDeviceListAdapter.getItem(Constant.camera_reverse ? 1 : 0);
                    mUSBMonitor.requestPermission(device0);
//                CameraDialog.showDialog(getActivity());
                }
            }
            Thread.sleep(100);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void ConnectUSB1(){
        try {
            if (mHandlerSecond != null) {
                if (mHandlerSecond != null && !mHandlerSecond.isOpened()) {
                    if (mDeviceListAdapter == null){
                        List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(getActivity(), com.serenegiant.uvccamera.R.xml.device_filter);
                        mDeviceListAdapter = new CameraDialog.DeviceListAdapter(getActivity(), mUSBMonitor.getDeviceList(filter.get(0)));
                    }
                    connect_order = "second";
                    UsbDevice device1 = mDeviceListAdapter.getItem(Constant.camera_reverse ? 0 : 1);
                    mUSBMonitor.requestPermission(device1);//获取设备信息，并检查打开此设备的权限
                }
            }
            Thread.sleep(100);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void checkConnectStatus(){
        try {
            Thread.sleep(100);
            if (!mHandlerFirst.isOpened() || !mHandlerSecond.isOpened()) {
                mCaptureButtonFirst.setVisibility(View.INVISIBLE);
                camera_connect.setText("连接");
                camera_convert.setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void DisConnect(){
        try{

            if (mHandlerFirst.isOpened()) {
                mHandlerFirst.close();
            }
            if (mHandlerSecond.isOpened()){
                mHandlerSecond.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
