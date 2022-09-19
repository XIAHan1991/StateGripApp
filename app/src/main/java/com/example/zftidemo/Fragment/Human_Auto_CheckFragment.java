package com.example.zftidemo.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.zftidemo.R;
import com.example.zftidemo.utils.ImageHandler;
import com.example.zftidemo.utils.Monitor;

public class Human_Auto_CheckFragment extends Fragment {
    FragmentManager mFragmentManager;
    FrameLayout hum_auto_frame;
    private RadioButton rb_human_check, rb_auto_check;
    private Fragment fragmentNow, manualCheckFragment, checkFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.human_auto_check_layout, container, false);
        manualCheckFragment = new ManualCheckFragment();
        checkFragment = new CheckFragment();
        mFragmentManager = getFragmentManager();
        rb_auto_check = (RadioButton)view.findViewById(R.id.bt_auto_check);
        rb_human_check = (RadioButton)view.findViewById(R.id.bt_human_check);
        OnCheckedChanged onCheckedChanged = new OnCheckedChanged();
        rb_human_check.setOnCheckedChangeListener(onCheckedChanged);
        rb_auto_check.setOnCheckedChangeListener(onCheckedChanged);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.hum_auto_framelayout,manualCheckFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentNow = manualCheckFragment;
        ImageHandler imageHandler = new ImageHandler((ImageView) view.findViewById(R.id.signal));
        (new Monitor(imageHandler)).start();
        return view;
    }
    private class OnCheckedChanged implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
            //这里如果不判断isChecked，点击RadioButton时此函数会执行两次，一次是某个按钮的状态变为false，一次是点击的按钮状态变为ture
            FragmentTransaction fragmentTransaction1 = mFragmentManager.beginTransaction();
            if (isChecked){
                switch (buttonView.getId()){
                    case R.id.bt_human_check:
                        if (manualCheckFragment.isAdded()){
                            fragmentTransaction1.hide(fragmentNow).show(manualCheckFragment);
                        }else {
                            fragmentTransaction1.hide(fragmentNow).add(R.id.hum_auto_framelayout,manualCheckFragment);
                            fragmentTransaction1.addToBackStack(null);
                        }
                        fragmentNow = manualCheckFragment;
                        fragmentTransaction1.commit();
                        break;
                    case R.id.bt_auto_check:
                        if (checkFragment.isAdded()){
                            fragmentTransaction1.hide(fragmentNow).show(checkFragment);
                        }else {
                            fragmentTransaction1.hide(fragmentNow).add(R.id.hum_auto_framelayout,checkFragment);
                            fragmentTransaction1.addToBackStack(null);
                        }
                        fragmentNow = checkFragment;
                        fragmentTransaction1.commit();
                        break;
                    default:
                        break;
                }
            }
        }
    }


}
