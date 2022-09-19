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
    private String img_path;
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
        img_path = this.getResources().getString(R.string.image_path);

        context = this.getActivity();

        showImg();
        initImagewait();  //初始化右上角的对勾

        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refresh:
                //Toast.makeText(getContext(),"刷新咯",Toast.LENGTH_SHORT).show();
//                Imagewait(getResources(),imageView1);
                showImg();
                break;
            case R.id.btn_upload:
                //Toast.makeText(getContext(),"上传鸭",Toast.LENGTH_SHORT).show();

                uploadImg();

                break;
            case R.id.btn_check:
                //Toast.makeText(getContext(),"人工智能算法出动",Toast.LENGTH_SHORT).show();
                check();
                final MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setFragmentSkipInterface(new MainActivity.FragmentSkipInterface() {
                    @Override
                    public void gotoFragment(ViewPager viewPager) {
                        /** 跳转到第二个页面的逻辑 */
                        viewPager.setCurrentItem(3);
                    }
                });
                mainActivity.skipToFragment();
                break;
        }
    }





    /**
     * 开启识别任务
     */
    private void detect() {
        new Thread() {
            @Override
            public void run() {
                // 向服务器发起开启识别任务请求
                HttpService httpService = new HttpService(context);
                httpService.taskDetect();
            }
        }.start();
    }

    /**
     * 查询任务状态
     */
    private void check() {
        new Thread() {
            @Override
            public void run() {
                detect();
                // 向服务器发起查询任务状态请求
                HttpService httpService = new HttpService(context);
                httpService.getTaskStatus();
            }
        }.start();
    }

    /**
     * 上传照片
     */
    private void uploadImg() {
        for(int index=0;index<6;index++){

            //带参带上回调函数
            upload(index, new Getback() {
                @Override
                public void onCountCallBack(int index) {
                    //回调的结果可会获取
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
                    // 向服务器发起上传照片请求
                    //String fileAddress1 = "/storage/emulated/0/app";
                    HttpService httpService = new HttpService(context);

                    httpService.uploadImg(getResources(),imageView,index,imageViews,bitmap_load,context);
                }catch (Exception ex){

                }finally {
//线程结束调用回调函数，带回你想要的东西
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
     * 显示图片
     */
    public void showImg() {
        String fileAddress[] = new String[]{"front/1.png", "front/2.png",
                "side/1.png","side/2.png", "back/1.png","back/2.png"};

        for(int i = 0; i < 6; i++) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(img_path+"/"+fileAddress[i])); //从本地取图片
                imageViews[i].setImageBitmap(bitmap); // 显示

                if (qBadgeView[i] != null) {
                    qBadgeView[i].setVisibility(View.INVISIBLE);  //隐藏对勾
                }

            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(),"刷新失败！",Toast.LENGTH_SHORT).show();
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
