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
    Button btn_front, btn_side, btn_behind,  btn_photo;
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
    public Surface mFirstPreviewSurface;

    public UVCCameraHandler mHandlerSecond;
    public CameraViewInterface mUVCCameraViewSecond;
    public Surface mSecondPreviewSurface;
    private RadioGroup rg;
    private RadioButton rb_take_photo,rb_human_check;

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
        //btn_pre = (Button) view.findViewById(R.id.btn_ccc);
        btn_photo = (Button) view.findViewById(R.id.btn_photo);
        //btn_next = (Button) view.findViewById(R.id.btn_next);
        //btn_start.setOnClickListener(this);
        btn_front.setOnClickListener(mOnClickListener);
        btn_side.setOnClickListener(mOnClickListener);
        btn_behind.setOnClickListener(mOnClickListener);
        //btn_pre.setOnClickListener(mOnClickListener);
        btn_photo.setOnClickListener(mOnClickListener);
        /*rg = (RadioGroup)view.findViewById(R.id.rg_p_h);
        rb_take_photo = (RadioButton)view.findViewById(R.id.bt_take_picture);
        rb_human_check = (RadioButton)view.findViewById(R.id.bt_by_human);
        OnCheckedChanged onCheckedChanged = new OnCheckedChanged();
        rb_human_check.setOnCheckedChangeListener(onCheckedChanged);
        rb_take_photo.setOnCheckedChangeListener(onCheckedChanged);*/
        //btn_next.setOnClickListener(mOnClickListener);
        view.findViewById(R.id.RelativeLayout1).setOnClickListener(mOnClickListener);

//        mUSBMonitor = new USBMonitor(getContext(), mOnDeviceConnectListener);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        resultFirstCamera(view);
        resultSecondCamera(view);
        return view;
    }
    /* private class OnCheckedChanged implements CompoundButton.OnCheckedChangeListener{
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
             //这里如果不判断isChecked，点击RadioButton时此函数会执行两次，一次是某个按钮的状态变为false，一次是点击的按钮状态变为ture
             if (isChecked){
                 switch (buttonView.getId()){
                     case R.id.bt_take_picture:
                         break;
                     case R.id.bt_by_human:
                         //Toast.makeText(getContext(),"tp",Toast.LENGTH_SHORT).show();
                         getActivity().getSupportFragmentManager().beginTransaction().
                                 replace(R.id.fl_man,new ManualCheckFragment())
                                 .commit();
                         break;
                     default:
                         break;
                 }
             }
         }
     }*/
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
        ((UVCCameraTextureView) mUVCCameraViewFirst).setOnClickListener(mOnClickListener);
        mCaptureButtonFirst = (Button)view.findViewById(R.id.btn_photo);
        mCaptureButtonFirst.setOnClickListener(mOnClickListener);
        mCaptureButtonFirst.setVisibility(View.INVISIBLE);
        mHandlerFirst = UVCCameraHandler.createHandler(getActivity(), mUVCCameraViewFirst
                , UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT
                , BANDWIDTH_FACTORS[0],firstDataCallBack);
    }
    private void resultSecondCamera(View view) {
        mUVCCameraViewSecond = (CameraViewInterface)view.findViewById(R.id.camera_view_second);
        //mUVCCameraViewSecond.setAspectRatio(UVCCamera.DEFAULT_PREVIEW_WIDTH / (float) UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        ((UVCCameraTextureView) mUVCCameraViewSecond).setOnClickListener(mOnClickListener);
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
//                if (DEBUG) Log.v(TAG, "onConnect:" + device);
                if (!mHandlerFirst.isOpened()) {
                    mHandlerFirst.open(ctrlBlock);
                    final SurfaceTexture st = mUVCCameraViewFirst.getSurfaceTexture();
                    mHandlerFirst.startPreview(new Surface(st));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCaptureButtonFirst.setVisibility(View.VISIBLE);
                        }
                    },0);
                }else if (!mHandlerSecond.isOpened()) {
                    mHandlerSecond.open(ctrlBlock);
                    final SurfaceTexture st = mUVCCameraViewSecond.getSurfaceTexture();
                    mHandlerSecond.startPreview(new Surface(st));
                }
            }catch (Exception e){
                Log.v(TAG, "摄像头连接失败");
            }

        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
//            if (DEBUG) Log.v(TAG, "onDisconnect:" + device);
            if ((mHandlerFirst != null) && !mHandlerFirst.isEqual(device)) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mHandlerFirst.close();
                        if (mFirstPreviewSurface != null) {
                            mFirstPreviewSurface.release();
                            mFirstPreviewSurface = null;
                        }
                        setCameraButton();
                    }
                });
            } else if ((mHandlerSecond != null) && !mHandlerSecond.isEqual(device)) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mHandlerSecond.close();
                        if (mSecondPreviewSurface != null) {
                            mSecondPreviewSurface.release();
                            mSecondPreviewSurface = null;
                        }
                        setCameraButton();
                    }
                });
            }
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
                case R.id.camera_view_first:


                    ConnectUSB();
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
                case R.id.camera_view_second:
                    //下一步
                    ConnectUSB1();
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
    public void ConnectUSB(){
        if (mHandlerFirst != null) {
            if (!mHandlerFirst.isOpened()) {
                List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(getActivity
                        (), com.serenegiant.uvccamera.R.xml.device_filter);
                mDeviceListAdapter = new CameraDialog.DeviceListAdapter(getActivity(), mUSBMonitor.getDeviceList(filter.get(0)));
                UsbDevice device0 = mDeviceListAdapter.getItem(0);
                mUSBMonitor.requestPermission(device0);//获取设备信息，并检查打开此设备的权限
                setCameraButton();
//                CameraDialog.showDialog(getActivity());
            } else {
                mHandlerFirst.close();
                setCameraButton();
            }
        }
    }
    public void ConnectUSB1(){
        if (mHandlerSecond != null) {
            if (!mHandlerSecond.isOpened()) {
                List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(getActivity(), com.serenegiant.uvccamera.R.xml.device_filter);
                mDeviceListAdapter = new CameraDialog.DeviceListAdapter(getActivity(), mUSBMonitor.getDeviceList(filter.get(0)));
                UsbDevice device1 = mDeviceListAdapter.getItem(1);
                mUSBMonitor.requestPermission(device1);//获取设备信息，并检查打开此设备的权限
                setCameraButton();
            } else {
                mHandlerSecond.close();
                setCameraButton();
            }
        }
    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        mUSBMonitor.register();
//
//        if (mUVCCameraViewSecond != null)
//            mUVCCameraViewSecond.onResume();
//        if (mUVCCameraViewFirst != null)
//            mUVCCameraViewFirst.onResume();
//    }
////        @Override
////    public void onResume(){
////        super.onResume();
////            ConnectUSB();
////            try {
////                sleep(100);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////            ConnectUSB1();
////    }
//
//    @Override
//    public void onStop() {
//        mHandlerFirst.close();
//        if (mUVCCameraViewFirst != null)
//            mUVCCameraViewFirst.onPause();
//        mCaptureButtonFirst.setVisibility(View.INVISIBLE);
//
//        mHandlerSecond.close();
//        if (mUVCCameraViewSecond != null)
//            mUVCCameraViewSecond.onPause();
//        mUSBMonitor.unregister();//usb管理器解绑
//        super.onStop();
//    }
//
//    @Override
//    public void onDestroy() {
//        if (mHandlerFirst != null) {
//            mHandlerFirst = null;
//        }
//
//        if (mHandlerSecond != null) {
//            mHandlerSecond = null;
//        }
//        if (mUSBMonitor != null) {
//            mUSBMonitor.destroy();
//            mUSBMonitor = null;
//        }
//        mUVCCameraViewFirst = null;
//        mCaptureButtonFirst = null;
//
//        mUVCCameraViewSecond = null;
//
//
//
//        super.onDestroy();
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        ConnectUSB();
//        try {
//            sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        ConnectUSB1();
//
//    }

//    @Override
//    public void lazyLoad() {
//        if(!isPrepared || !isVisible) {
//            return;
//        }
//         ConnectUSB();
//            try {
//                sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//          ConnectUSB1();
//    }
//    @UiThread
//    protected void onLazyload(){
//        ConnectUSB();
//        try {
//            sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        ConnectUSB1();
//    }

}
