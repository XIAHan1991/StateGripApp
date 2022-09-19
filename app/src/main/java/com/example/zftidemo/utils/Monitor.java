package com.example.zftidemo.utils;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.example.zftidemo.MainActivity;
import com.example.zftidemo.R;
import com.example.zftidemo.dao.Constant;

public class Monitor extends Thread{
    private ImageHandler imageHandler;
    public Monitor(ImageHandler imageHandler){
        this.imageHandler = imageHandler;
    }
    @Override
    public void run(){
        try{
            while(!Constant.is_connected){
                Thread.sleep(10);
            }
            Message msg = new Message();
            this.imageHandler.sendMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
