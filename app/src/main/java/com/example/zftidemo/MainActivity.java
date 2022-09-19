package com.example.zftidemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zftidemo.Fragment.Cam_Man_Fragment;
import com.example.zftidemo.Fragment.CameraFragment;
import com.example.zftidemo.Fragment.CameraTitle;
import com.example.zftidemo.Fragment.CheckFragment;
import com.example.zftidemo.Fragment.CreateTaskFragment;
import com.example.zftidemo.Fragment.Human_Auto_CheckFragment;
import com.example.zftidemo.Fragment.ManualCheckFragment;
import com.example.zftidemo.Fragment.MesFragment;
import com.example.zftidemo.Fragment.QueryallFragment;
import com.example.zftidemo.Fragment.SetFragment;
import com.example.zftidemo.View.NoSwipeViewPager;
import com.example.zftidemo.dao.Constant;
import com.example.zftidemo.http.HttpService;
import com.example.zftidemo.http.HttpTool;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;

import static com.serenegiant.utils.UIThreadHelper.runOnUiThread;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener,CameraDialog.CameraDialogParent{
    private USBMonitor mUSBMonitor;
    private static final int TAB_SIZE = 5;
    private NoSwipeViewPager viewPager;
    private Fragment[] tabFragments = {new CreateTaskFragment(),new CameraTitle(),
            new Human_Auto_CheckFragment(), new MesFragment(), new SetFragment()};
    private int[] tabs_id = {R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4, R.id.tab5};
    private LinearLayout[] tabs = new LinearLayout[TAB_SIZE];
    private int[] icons_id = {R.id.task_icon, R.id.camera_icon, R.id.check_icon,
            R.id.message_icon,R.id.setting_icon};
    private ImageView[] icons = new ImageView[TAB_SIZE];
    private int[] text_id = {R.id.task_text, R.id.camera_text, R.id.check_text,
            R.id.message_text, R.id.setting_text};
    private TextView[] texts = new TextView[TAB_SIZE];
    /*private int[] icons_normal = {R.drawable.task_foreground, R.drawable.ic_camera_foreground, R.drawable.ic_check_foreground,
            R.drawable.ic_message_foreground, R.drawable.ic_setting_foreground};*/
    private int[] icons_normal = {R.drawable.task, R.drawable.camera, R.drawable.check,
            R.drawable.mes, R.drawable.set};
    private int[] icons_press ={R.drawable.task_avtive, R.drawable.camera_active,R.drawable.check_avtive,
    R.drawable.mes_avtive, R.drawable.set_avtive};
    private int connect_id = R.drawable.connected;
    private Context context; // 上下文
    EditText edit_address;
    HttpTool httpTool = null;
    CameraFragment fragment;
    public  ManualCheckFragment manualCheckFragment;

    //页面跳转
    private FragmentSkipInterface mFragmentSkipInterface;
    public void setFragmentSkipInterface(FragmentSkipInterface fragmentSkipInterface) {
        mFragmentSkipInterface = fragmentSkipInterface;
    }

    /** Fragment跳转 */
    public void skipToFragment(){
        if(mFragmentSkipInterface != null){
            mFragmentSkipInterface.gotoFragment(viewPager);
        }
    }
    public interface FragmentSkipInterface {
        /** ViewPager中子Fragment之间跳转的实现方法 */
        void gotoFragment(ViewPager viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        httpTool = new HttpTool();
        context = MainActivity.this;
        //fragment = (CameraFragment) getSupportFragmentManager().getFragments().get(1);
//        fragment=(CameraFragment) tabFragments[1];
//        mUSBMonitor = new USBMonitor(this, fragment.mOnDeviceConnectListener);
//        fragment.setmUSBMonitor(mUSBMonitor);
        // 向服务器发起登录请求
        login();
        //monitor();
    }

    private void initView() {
        viewPager = (NoSwipeViewPager) findViewById(R.id.vp_container);
        for(int i = 0; i < TAB_SIZE; i++){
            tabs[i] = (LinearLayout)findViewById(tabs_id[i]);
            icons[i] = (ImageView)findViewById(icons_id[i]);
            texts[i] = (TextView)findViewById(text_id[i]);
        }
        viewPager.setAdapter(new MainTabFragmentAdapter(getSupportFragmentManager(),tabFragments));
        viewPager.addOnPageChangeListener(this);
        for(int i = 0; i < TAB_SIZE; i++){
            tabs[i].setOnClickListener(this);
        }
        icons[0].setImageResource(icons_press[0]);
        texts[0].setTextColor(ContextCompat.getColor(this,R.color.blue_500));
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        edit_address = (EditText)findViewById(R.id.edit_address);
    }

    /**
     * 向服务器发送登录请求
     */
    private void login() {
        new Thread() {
            @Override
            public void run() {
                String address = getResources().getString(R.string.ip);
                HttpService httpService = new HttpService(context);
                httpService.login(address);
            }
        }.start();
    }

    /**
     *
     * @param isTabClick  是否点击
     * @param index     点击项或滚动项
     */
    //页面切换逻辑
    private void resetTab(boolean isTabClick, int index) {
        for(int i = 0; i < TAB_SIZE; i++){
            if(index == i){
                icons[i].setImageResource(icons_press[i]);
                texts[i].setTextColor(ContextCompat.getColor(this,R.color.blue_500));
                if(isTabClick && viewPager.getCurrentItem() % TAB_SIZE != index){
                    viewPager.setCurrentItem(index, false);
                }
            } else{
                icons[i].setImageResource(icons_normal[i]);
                texts[i].setTextColor(ContextCompat.getColor(this,R.color.black));
            }
        }
    }
    //底部菜单栏单击事件
        @Override
        public void onClick(View v) {
            int id = v.getId();
            for(int i = 0; i < TAB_SIZE; i++){
//                if(id==tabs_id[1]){
//                    fragment.ConnectUSB();
//                    fragment.ConnectUSB1();
//                }
                if (id == tabs_id[i]){
                    resetTab(true,i);
                    if (i != 1){
                        CameraFragment cameraFragment =  ((CameraTitle)tabFragments[1]).cameraFragment;
                        cameraFragment.DisConnect();
                        cameraFragment.checkConnectStatus();
                    }
                    break;
                }
            }
        }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        resetTab(false,position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    // 点击空白区域 自动隐藏软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mUSBMonitor.register();//考虑写一个InitUsb函数在UsbCameraClass中，在此处调用(内容为连接usb摄像头的代码)
//        if (fragment.mUVCCameraViewSecond != null)
//            fragment.mUVCCameraViewSecond.onResume();
//        if (fragment.mUVCCameraViewFirst != null)
//            fragment.mUVCCameraViewFirst.onResume();
//    }
//    @Override
//    public void onStop() {
//        fragment.mHandlerFirst.close();
//        if (fragment.mUVCCameraViewFirst != null)
//            fragment.mUVCCameraViewFirst.onPause();
//        fragment.mCaptureButtonFirst.setVisibility(View.INVISIBLE);
//
//        fragment.mHandlerSecond.close();
//        if (fragment.mUVCCameraViewSecond != null)
//            fragment.mUVCCameraViewSecond.onPause();
//        mUSBMonitor.unregister();//usb管理器解绑
//        super.onStop();
//    }
//
//    @Override
//    public void onDestroy() {
//        if (fragment.mHandlerFirst != null) {
//            fragment.mHandlerFirst = null;
//        }
//
//        if (fragment.mHandlerSecond != null) {
//            fragment.mHandlerSecond = null;
//        }
//        if (mUSBMonitor != null) {
//            mUSBMonitor.destroy();
//            mUSBMonitor = null;
//        }
//        fragment.mUVCCameraViewFirst = null;
//        fragment.mCaptureButtonFirst = null;
//
//        fragment.mUVCCameraViewSecond = null;
//
//
//
//        super.onDestroy();
//    }
    @Override
    public USBMonitor getUSBMonitor() {
        return mUSBMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {

    }

}
