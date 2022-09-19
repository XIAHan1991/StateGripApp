package com.example.zftidemo.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.zftidemo.R;
import com.example.zftidemo.utils.ImageHandler;
import com.example.zftidemo.utils.Monitor;
import com.serenegiant.usb.USBMonitor;

import static java.lang.Thread.sleep;

public class CameraTitle extends Fragment {
    FragmentManager mFragmentManager;
    private Fragment fragment;
    public CameraFragment cameraFragment;
    private USBMonitor mUSBMonitor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_title, container, false);
        fragment = new CameraFragment();
        mFragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.camera_frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        cameraFragment=(CameraFragment) fragment;
        mUSBMonitor = new USBMonitor(getContext(), cameraFragment.mOnDeviceConnectListener);
        cameraFragment.setmUSBMonitor(mUSBMonitor);
        ImageHandler imageHandler = new ImageHandler((ImageView) view.findViewById(R.id.signal));
        (new Monitor(imageHandler)).start();
        return view;
    }
        @Override
    public void onStart() {
        super.onStart();
        mUSBMonitor.register();

        if (cameraFragment.mUVCCameraViewSecond != null)
            cameraFragment.mUVCCameraViewSecond.onResume();
        if (cameraFragment.mUVCCameraViewFirst != null)
            cameraFragment.mUVCCameraViewFirst.onResume();
    }
        @Override
    public void onResume(){
        super.onResume();
            //cameraFragment.ConnectUSB();
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //cameraFragment.ConnectUSB1();
    }

    @Override
    public void onStop() {
        cameraFragment.mHandlerFirst.close();
        if (cameraFragment.mUVCCameraViewFirst != null)
            cameraFragment.mUVCCameraViewFirst.onPause();
        cameraFragment.mCaptureButtonFirst.setVisibility(View.INVISIBLE);

        cameraFragment.mHandlerSecond.close();
        if (cameraFragment.mUVCCameraViewSecond != null)
            cameraFragment.mUVCCameraViewSecond.onPause();
        mUSBMonitor.unregister();//usb管理器解绑
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (cameraFragment.mHandlerFirst != null) {
            cameraFragment.mHandlerFirst = null;
        }

        if (cameraFragment.mHandlerSecond != null) {
            cameraFragment.mHandlerSecond = null;
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        cameraFragment.mUVCCameraViewFirst = null;
        cameraFragment.mCaptureButtonFirst = null;

        cameraFragment.mUVCCameraViewSecond = null;



        super.onDestroy();
    }


}
