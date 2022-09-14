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
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatCallback;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.zftidemo.MainActivity;
import com.example.zftidemo.R;
import com.example.zftidemo.View.LazyFragment;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.usbcameracommon.UvcCameraDataCallBack;
import com.serenegiant.widget.CameraViewInterface;
import com.serenegiant.widget.UVCCameraTextureView;

import java.util.List;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.usbcameracommon.UvcCameraDataCallBack;
import com.serenegiant.widget.CameraViewInterface;
import com.serenegiant.widget.UVCCameraTextureView;
import static com.serenegiant.utils.ThreadPool.queueEvent;
import static com.serenegiant.utils.UIThreadHelper.runOnUiThread;
import static java.lang.Thread.sleep;


public class CameraFragment extends Fragment implements CameraDialog.CameraDialogParent{
    Button btn_front, btn_side, btn_behind,  btn_photo, camera_connect, camera_convert;
    private static final boolean DEBUG = true;
    private static final String TAG = "MainActivity";
    private boolean isPrepared;
    private static final float[] BANDWIDTH_FACTORS = {0.5f, 0.5f};

    // for accessing USB and USB camera
    private String  pathcamera="/storage/emulated/0/DCIM/USBCameraTest/front/1.png";
    private String  pathcamera2="/storage/emulated/0/DCIM/USBCameraTest/front/2.png";

    public void setmUSBMonitor(USBMonitor mUSBMonitor) {
        this.mUSBMonitor = mUSBMonitor;
    }
    public USBMonitor mUSBMonitor;
    public UVCCameraHandler mHandlerFirst;
    public CameraViewInterface mUVCCameraViewFirst;
    public Button mCaptureButtonFirst;
    private static boolean reverse = false;
    private String connect_order = "first";
    public UVCCameraHandler mHandlerSecond;
    public CameraViewInterface mUVCCameraViewSecond;

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
        // view.findViewById(R.id.RelativeLayout1).setOnClickListener(mOnClickListener);

//        mUSBMonitor = new USBMonitor(getContext(), mOnDeviceConnectListener);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        resultFirstCamera(view);
        resultSecondCamera(view);
        return view;
    }

    UvcCameraDataCallBack firstDataCallBack = new UvcCameraDataCallBack() {
        @Override
        public void getData(byte[] data) {
            if (DEBUG) Log.v(TAG, "数据回调:" + data.length);
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
                if (connect_order.equals("first") && !mHandlerFirst.isOpened() && device.equals(mDeviceListAdapter.getItem(reverse ? 1 : 0))){
                    mHandlerFirst.open(ctrlBlock);
                    final SurfaceTexture st1 = mUVCCameraViewFirst.getSurfaceTexture();
                    mHandlerFirst.startPreview(new Surface(st1));
                }else if (connect_order.equals("second") && !mHandlerSecond.isOpened() && device.equals(mDeviceListAdapter.getItem(reverse ? 0 : 1))){
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
                    pathcamera="/storage/emulated/0/DCIM/USBCameraTest/front/1.png";
                    pathcamera2="/storage/emulated/0/DCIM/USBCameraTest/front/2.png";
                    break;
                case R.id.btn_side:
                    //拍摄物体侧面
                    pathcamera="/storage/emulated/0/DCIM/USBCameraTest/side/1.png";
                    pathcamera2="/storage/emulated/0/DCIM/USBCameraTest/side/2.png";
                    break;
                case R.id.btn_behind:
                    //拍摄物体后面
                    pathcamera="/storage/emulated/0/DCIM/USBCameraTest/back/1.png";
                    pathcamera2="/storage/emulated/0/DCIM/USBCameraTest/back/2.png";
                    break;
                case R.id.btn_connect_camera:
                    if (camera_connect.getText().toString().equals("连接")){
                        try {
                            camera_connect.setEnabled(false);
                            ConnectUSB0();
                            Thread.sleep(500);
                            ConnectUSB1();
                            Thread.sleep(500);
                            if(mHandlerFirst.isOpened() && mHandlerSecond.isOpened()) {
                                mCaptureButtonFirst.setVisibility(View.VISIBLE);
                                camera_connect.setText("断开");
                                camera_convert.setVisibility(View.VISIBLE);
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
                            Thread.sleep(200);
                            if(!mHandlerFirst.isOpened() || !mHandlerSecond.isOpened()) {
                                mCaptureButtonFirst.setVisibility(View.INVISIBLE);
                                camera_connect.setText("连接");
                                camera_convert.setVisibility(View.INVISIBLE);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            camera_connect.setEnabled(true);
                        }
                    }

                    break;
                case R.id.btn_convert:
                    reverse = !reverse;
                    camera_convert.setEnabled(false);
                    try {
                        DisConnect();
                        ConnectUSB0();
                        Thread.sleep(500);
                        ConnectUSB1();
                        Thread.sleep(500);
                        camera_convert.setEnabled(true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case R.id.btn_photo:
                    //拍摄照片
                    if (mHandlerFirst!= null) {
                        if (mHandlerFirst.isOpened()) {
                            if (checkPermissionWriteExternalStorage()) {
                                mHandlerFirst.captureStill(pathcamera);//()中写入保存文件夹
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
                                mHandlerSecond.captureStill(pathcamera2);
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
        if (mHandlerFirst != null) {
            if (!mHandlerFirst.isOpened()) {
                if (mDeviceListAdapter == null){
                    List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(getActivity(), com.serenegiant.uvccamera.R.xml.device_filter);
                    mDeviceListAdapter = new CameraDialog.DeviceListAdapter(getActivity(), mUSBMonitor.getDeviceList(filter.get(0)));
                }
                connect_order = "first";
                UsbDevice device0 = mDeviceListAdapter.getItem(reverse ? 1 : 0);
                mUSBMonitor.requestPermission(device0);
//                CameraDialog.showDialog(getActivity());
            }
        }
    }
    public void ConnectUSB1(){
        if (mHandlerSecond != null) {
            if (mHandlerSecond != null && !mHandlerSecond.isOpened()) {
                if (mDeviceListAdapter == null){
                    List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(getActivity(), com.serenegiant.uvccamera.R.xml.device_filter);
                    mDeviceListAdapter = new CameraDialog.DeviceListAdapter(getActivity(), mUSBMonitor.getDeviceList(filter.get(0)));
                }
                connect_order = "second";
                UsbDevice device1 = mDeviceListAdapter.getItem(reverse ? 0 : 1);
                mUSBMonitor.requestPermission(device1);//获取设备信息，并检查打开此设备的权限
            }
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
