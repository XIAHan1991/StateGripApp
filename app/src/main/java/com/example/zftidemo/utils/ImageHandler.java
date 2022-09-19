package com.example.zftidemo.utils;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.example.zftidemo.R;
import com.example.zftidemo.dao.Constant;

public class ImageHandler extends Handler {

    private ImageView imageView;
    public ImageHandler(ImageView imageView){
        this.imageView = imageView;
    }
    @Override
    public void handleMessage(Message msg){
        try{
            super.handleMessage(msg);
            imageView.setImageResource(R.drawable.connected);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
