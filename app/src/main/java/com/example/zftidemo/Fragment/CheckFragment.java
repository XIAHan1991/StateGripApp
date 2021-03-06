package com.example.zftidemo.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import q.rorbin.badgeview.QBadgeView;

import com.example.zftidemo.Getback;
import com.example.zftidemo.MainActivity;
import com.example.zftidemo.R;
import com.example.zftidemo.View.ZoomImageView;
import com.example.zftidemo.http.HttpService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static android.os.Looper.getMainLooper;
import static java.lang.Thread.sleep;

public class CheckFragment extends Fragment implements View.OnClickListener{
    Button btn_refresh,btn_upload,btn_check;
    Context context;
    Bitmap bitmap_load;
//    ZoomImageView imageView1,imageView2;
    public static ImageView[] imageView= new ImageView[6];
    public static ImageView[] imageViews = new ImageView[6];
    public static QBadgeView[] qBadgeView = new QBadgeView[6];
    public int[] imageView_id = {R.id.f_image1,R.id.f_image2,R.id.p_image1,R.id.p_image2,R.id.b_image1,R.id.b_image2};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.check_layout,container,false);
        btn_refresh = (Button) view.findViewById(R.id.btn_refresh);
        btn_upload = (Button) view.findViewById(R.id.btn_upload);
        btn_check = (Button) view.findViewById(R.id.btn_check);
        bitmap_load = ((BitmapDrawable)getResources().getDrawable(
                R.drawable.checkcircle)).getBitmap();
        for(int i = 0; i < 6; i++) {
            imageViews[i] = (ImageView)view.findViewById(imageView_id[i]);
        }

//            imageView[i] = (ZoomImageView)view.findViewById(imageView_id[i]);
            imageView[0] = (ImageView)view.findViewById(R.id.f_image1);
            imageView[1] = (ImageView)view.findViewById(R.id.f_image2);
            imageView[2] = (ImageView)view.findViewById(R.id.p_image1);
            imageView[3] = (ImageView)view.findViewById(R.id.p_image2);
            imageView[4] = (ImageView)view.findViewById(R.id.b_image1);
            imageView[5] = (ImageView)view.findViewById(R.id.b_image2);


//        imageView1 = (ZoomImageView)view.findViewById(R.id.f_image1);
//        imageView2 = (ZoomImageView)view.findViewById(R.id.f_image2);


        btn_refresh.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        btn_check.setOnClickListener(this);


        context = this.getActivity();

        showImg();
        initImagewait();  //???????????????????????????

        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refresh:
                //Toast.makeText(getContext(),"?????????",Toast.LENGTH_SHORT).show();
//                Imagewait(getResources(),imageView1);
                showImg();
                break;
            case R.id.btn_upload:
                //Toast.makeText(getContext(),"?????????",Toast.LENGTH_SHORT).show();

                uploadImg();

                break;
            case R.id.btn_check:
                //Toast.makeText(getContext(),"????????????????????????",Toast.LENGTH_SHORT).show();
                check();
                final MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setFragmentSkipInterface(new MainActivity.FragmentSkipInterface() {
                    @Override
                    public void gotoFragment(ViewPager viewPager) {
                        /** ????????????????????????????????? */
                        viewPager.setCurrentItem(3);
                    }
                });
                mainActivity.skipToFragment();
                break;
        }
    }





    /**
     * ??????????????????
     */
    private void detect() {
        new Thread() {
            @Override
            public void run() {
                // ??????????????????????????????????????????
                HttpService httpService = new HttpService(context);
                httpService.taskDetect();
            }
        }.start();
    }

    /**
     * ??????????????????
     */
    private void check() {
        new Thread() {
            @Override
            public void run() {
                detect();
                // ??????????????????????????????????????????
                HttpService httpService = new HttpService(context);
                httpService.getTaskStatus();
            }
        }.start();
    }

    /**
     * ????????????
     */
    private void uploadImg() {
        for(int index=0;index<6;index++){

            //????????????????????????
            upload(index, new Getback() {
                @Override
                public void onCountCallBack(int index) {
                    //???????????????????????????
//                    Imagewait(getResources(),imageView[index]);
                        qBadgeView[index].setVisibility(View.VISIBLE);


//                if(Flag.getQueryflag()==1){
//                    Imagewait(getResources(),imageView[index]);
//                }
//                    Flag.setQueryflag(0);
                }
            });
//            upload(index);
//            if(Flag.getQueryflag()==1){
//                Imagewait(getResources(),imageView[index]);
//            }
//            Flag.setQueryflag(0);
        }

    }
    private void upload(int index, final Getback getback){

        new Thread() {
            @Override
            public void run() {
                try {
                    // ????????????????????????????????????
                    //String fileAddress1 = "/storage/emulated/0/app";
                    HttpService httpService = new HttpService(context);

                    httpService.uploadImg(getResources(),imageView,index,imageViews,bitmap_load,context);
                }catch (Exception ex){

                }finally {
//?????????????????????????????????????????????????????????
                    Handler handler = new Handler(getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (getback != null) {
                                getback.onCountCallBack(index);
                            }
                        }
                    });


                }
            }
        }.start();

    }
    /**
     * ????????????
     */
    public void showImg() {
        String fileAddress[] = new String[6];
        fileAddress[0] = "/storage/emulated/0/DCIM/USBCameraTest/front/1.png";
        fileAddress[1] = "/storage/emulated/0/DCIM/USBCameraTest/front/2.png";
        fileAddress[2] = "/storage/emulated/0/DCIM/USBCameraTest/side/1.png";
        fileAddress[3] = "/storage/emulated/0/DCIM/USBCameraTest/side/2.png";
        fileAddress[4] = "/storage/emulated/0/DCIM/USBCameraTest/back/1.png";
        fileAddress[5] = "/storage/emulated/0/DCIM/USBCameraTest/back/2.png";

        for(int i = 0; i < 6; i++) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(fileAddress[i])); //??????????????????
                imageViews[i].setImageBitmap(bitmap); // ??????

                if (qBadgeView[i] != null) {
                    qBadgeView[i].setVisibility(View.INVISIBLE);  //????????????
                }

            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(),"???????????????",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void Imagewait(Resources resources, View view){
        QBadgeView qBadgeView = new QBadgeView(context);
        qBadgeView.hide(true);
        qBadgeView.setBadgeBackground(resources.getDrawable(R.drawable.checkcircle));
        qBadgeView.setBadgeText("");
        qBadgeView.bindTarget(view);
        qBadgeView.setBadgeGravity(Gravity.END|Gravity.TOP);
        qBadgeView.setGravityOffset(1,true);
        qBadgeView.setBadgeTextSize(8, true);
        qBadgeView.setBadgePadding(15, true);


    }

    public void initImagewait() {
        for(int i = 0; i < 6;i++) {
            qBadgeView[i] = new QBadgeView(context);
            qBadgeView[i].hide(true);
            qBadgeView[i].setBadgeBackground(getResources().getDrawable(R.drawable.checkcircle));
            qBadgeView[i].setBadgeText("");
            qBadgeView[i].bindTarget(imageView[i]);
            qBadgeView[i].setBadgeGravity(Gravity.END|Gravity.TOP);
            qBadgeView[i].setGravityOffset(1,true);
            qBadgeView[i].setBadgeTextSize(8, true);
            qBadgeView[i].setBadgePadding(15, true);
            qBadgeView[i].setVisibility(View.INVISIBLE);
        }
    }

}
