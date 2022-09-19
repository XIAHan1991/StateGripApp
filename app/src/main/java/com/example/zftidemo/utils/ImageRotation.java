package com.example.zftidemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageRotation extends Thread{
    private String inputImage;
    private String outputImage;
    private Integer rotation;
    private Bitmap input, output;
    public ImageRotation(String inputImage, String outputImage, Integer rotation){
        this.inputImage = inputImage;
        this.outputImage = outputImage;
        this.rotation = rotation;
    }
    public void run(){
        try {
            File file = new File(this.outputImage);
            if (file.exists() && file.isFile()){
                file.delete();
            }
            Thread.sleep(1000);
            input = BitmapFactory.decodeFile(this.inputImage);
            Thread.sleep(1000);
            Matrix matrix = new Matrix();
            matrix.setRotate(this.rotation);
            output = Bitmap.createBitmap(input, 0, 0, input.getWidth(), input.getHeight(), matrix, true);
            OutputStream outputStream = new FileOutputStream(file);
            output.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Thread.sleep(1000);
            outputStream.flush();
            outputStream.close();
            file = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
