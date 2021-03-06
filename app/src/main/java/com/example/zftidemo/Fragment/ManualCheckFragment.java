package com.example.zftidemo.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.zftidemo.MainActivity;
import com.example.zftidemo.R;

import com.example.zftidemo.View.ZoomImageView;
import com.example.zftidemo.dao.ConnectDB;
import com.example.zftidemo.http.HttpTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static java.lang.Thread.sleep;

public class ManualCheckFragment extends Fragment implements View.OnClickListener {
    String fileAddress[] = new String[]{"/storage/emulated/0/DCIM/USBCameraTest/front/1.png", "/storage/emulated/0/DCIM/USBCameraTest/front/2.png",
            "/storage/emulated/0/DCIM/USBCameraTest/side/1.png","/storage/emulated/0/DCIM/USBCameraTest/side/2.png",
            "/storage/emulated/0/DCIM/USBCameraTest/back/1.png","/storage/emulated/0/DCIM/USBCameraTest/back/2.png"};

    private int[] arrayPicture =  new int[]{R.drawable.check,R.drawable.mes,R.drawable.set};
    private ImageSwitcher imageSwitcher;
    private int index;
    private float touchDownX;
    private float touchUpX;
    private RadioGroup rg, rg_bt, rg_dl;
    private RadioButton rb_take_photo,rb_human_check;
    private Button btn_confirm;
    private Context context;
    private ImageView pre_pho,next_pho;
    ZoomImageView zoomImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.manualcheck, container, false);
        rg = (RadioGroup)view.findViewById(R.id.rg_p_h);
        rg_bt = (RadioGroup)view.findViewById(R.id.rg_bt);
        rg_dl = (RadioGroup)view.findViewById(R.id.rg_dl);
        pre_pho = (ImageView)view.findViewById(R.id.pre_pho);
        next_pho = (ImageView)view.findViewById(R.id.next_pho);
        zoomImageView = (ZoomImageView)view.findViewById(R.id.preview_pho);
        btn_confirm = (Button)view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(this);
        zoomImageView.setImageURI(Uri.parse(fileAddress[0]));
        zoomImageView.ReChangeScale();
//        zoomImageView.setImageURI(null);
        pre_pho.setOnClickListener(this);
        next_pho.setOnClickListener(this);
        context = this.getActivity();


        return view;
    }
    /*private class OnCheckedChanged implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
            //?????????????????????isChecked?????????RadioButton??????????????????????????????????????????????????????????????????false???????????????????????????????????????ture
            if (isChecked){
                switch (buttonView.getId()){
                    case R.id.bt_take_picture:
                        getActivity().getSupportFragmentManager().beginTransaction().
                                replace(R.id.fl_photo,new CameraFragment())
                                .commit();
                        break;
                    case R.id.bt_by_human:
                        //Toast.makeText(getContext(),"tp",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }
    }*/
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pre_pho:
                System.out.println("=============================================================");
                index = index == 0 ? fileAddress.length - 1 : index -1 ;
                zoomImageView.setImageURI(Uri.parse(fileAddress[index]));
                zoomImageView.ReChangeScale();
                break;
            case R.id.next_pho:
                System.out.println("=============================================================");
                index = index == fileAddress.length-1 ? 0: index + 1;
                zoomImageView.setImageURI(Uri.parse(fileAddress[index]));
                zoomImageView.ReChangeScale();
                break;
            case R.id.btn_confirm:
                int rd_bt1=3,rd_bt2=3;
                for (int i=0;i<2;i++){
                    RadioButton radioButton = (RadioButton) rg_dl.getChildAt(i);
                    RadioButton radioButton1 = (RadioButton) rg_bt.getChildAt(i);
                    if (radioButton1.isChecked()){
                        if (radioButton1.getText().equals("??????")){
                            rd_bt1 = 1;
                        }else {
                            rd_bt1 = 0;
                        }
                    }
                    if (radioButton.isChecked()){
                        if (radioButton1.getText().equals("??????")){
                            rd_bt2 = 1;
                        }else {
                            rd_bt2 = 0;
                        }
                    }
                }
                if (rd_bt1==3||rd_bt2==3){
                    Toast.makeText(getContext(),"?????????????????????",Toast.LENGTH_SHORT).show();
                }else {
//            Toast.makeText(getContext(),"??????"+Integer.toString(rd_bt1)+"??????"+Integer.toString(rd_bt2),Toast.LENGTH_SHORT).show();
                    final MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setFragmentSkipInterface(new MainActivity.FragmentSkipInterface() {
                        @Override
                        public void gotoFragment(ViewPager viewPager) {
                            /** ????????????????????????????????? */
                            viewPager.setCurrentItem(2);
                        }
                    });
                    mainActivity.skipToFragment();
                }

                String taskId =  ConnectDB.getData(context, "task_id");
                if (taskId.equals("")) {
                    Toast.makeText(context, "??????????????????", Toast.LENGTH_SHORT).show();
                } else {
                    dbInsert(Integer.parseInt(taskId), rd_bt1, rd_bt2);
                }
                break;
        }


//        Toast.makeText(context, taskId, Toast.LENGTH_SHORT).show();


//        int finalRd_bt = rd_bt1;
//        int finalRd_bt1 = rd_bt2;
//        new Thread() {
//            @Override
//            public void run() {
//                boolean b1 = ConnectDB.insert2ApTaskResult(Integer.parseInt(taskId), "????????????", finalRd_bt);
//                boolean b2 = ConnectDB.insert2ApTaskResult(Integer.parseInt(taskId), "??????????????????", finalRd_bt1);
//                if(b1 && b2) {
//                    System.out.println("????????????");
//                    Looper.prepare();
//                    Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
//                }
//            }
//        }.start();
    }

    /**
     * ????????????
     */
    private void dbInsert(int task_id, int status_16, int status_17) {
        new Thread() {
            @Override
            public void run() {
                HttpTool httpTool = new HttpTool();
                String ipAddress = httpTool.getData(context, "address");

                boolean b1 = ConnectDB.insert2ApTaskResult(ipAddress, task_id, "????????????", status_16);
                boolean b2 = ConnectDB.insert2ApTaskResult(ipAddress, task_id, "??????????????????", status_17);
                if(b1 && b2) {
                    System.out.println("????????????");
                    Looper.prepare();
                    Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }.start();
    }
//    @Override
//    public void onResume(){
//        super.onResume();
//        System.out.print("aaaaaaaaaaaa");
////        try {
////            sleep(100);
////            next_pho.performClick();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//
//    }
//        @Override
//    public void onStart() {
//        super.onStart();
//        zoomImageView.setImageURI(null);
//    }

}
