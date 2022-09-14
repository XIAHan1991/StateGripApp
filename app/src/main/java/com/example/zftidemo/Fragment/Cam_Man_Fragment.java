package com.example.zftidemo.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.zftidemo.R;
import com.serenegiant.usb.USBMonitor;

import static java.lang.Thread.sleep;


public class Cam_Man_Fragment extends Fragment implements View.OnClickListener{
    private RadioGroup rg;
    private RadioButton rb_take_photo,rb_human_check;
    Fragment CamFragment,ManFragment,FragmentNow;
    private FragmentManager mFragmentManager;
    private FrameLayout frameLayout;
    private USBMonitor mUSBMonitor;
    CameraFragment cameraFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.cam_man_layout, container, false);
        rb_take_photo = (RadioButton)view.findViewById(R.id.bt_take_picture);
        rb_human_check = (RadioButton)view.findViewById(R.id.bt_by_human);
        OnCheckedChanged onCheckedChanged = new OnCheckedChanged();
        rb_human_check.setOnCheckedChangeListener(onCheckedChanged);
        rb_take_photo.setOnCheckedChangeListener(onCheckedChanged);
        frameLayout = (FrameLayout)view.findViewById(R.id.cam_man_frame);
        CamFragment = new CameraFragment();
        ManFragment = new ManualCheckFragment();
        mFragmentManager = getActivity().getSupportFragmentManager();
        initDefaultFragment();
//        cameraFragment=(CameraFragment) CamFragment;
//        mUSBMonitor = new USBMonitor(getContext(), cameraFragment.mOnDeviceConnectListener);
//        cameraFragment.setmUSBMonitor(mUSBMonitor);
        return view;
    }

    private void initDefaultFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.cam_man_frame,CamFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        FragmentNow = CamFragment;
    }
    private class OnCheckedChanged implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
            //这里如果不判断isChecked，点击RadioButton时此函数会执行两次，一次是某个按钮的状态变为false，一次是点击的按钮状态变为ture
            FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
            if (isChecked){
                switch (buttonView.getId()){
                    case R.id.bt_take_picture:
                        if (CamFragment.isAdded()){
                            fragmentTransaction1.hide(FragmentNow).show(CamFragment);
                        }else {
                            fragmentTransaction1.hide(FragmentNow).add(R.id.cam_man_frame,CamFragment);
                            fragmentTransaction1.addToBackStack(null);
                        }
                        FragmentNow = CamFragment;
                        fragmentTransaction1.commit();
                        break;
                    case R.id.bt_by_human:
                        if (ManFragment.isAdded()){
                            fragmentTransaction1.hide(FragmentNow).show(ManFragment);
                        }else {
                            fragmentTransaction1.hide(FragmentNow).add(R.id.cam_man_frame,ManFragment);
                            fragmentTransaction1.addToBackStack(null);
                        }
                        FragmentNow = ManFragment;
                        fragmentTransaction1.commit();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        
    }



}
