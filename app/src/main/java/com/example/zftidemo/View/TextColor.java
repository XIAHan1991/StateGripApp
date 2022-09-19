package com.example.zftidemo.View;

import android.graphics.Color;

public class TextColor {
    public int TextColor(String text){
        if (text.equals("合格")){
            return Color.parseColor("#000000");
        }else if (text.equals("不合格")){
            return Color.parseColor("#FF0000");
        }else return Color.parseColor("#000000");
    }
}
