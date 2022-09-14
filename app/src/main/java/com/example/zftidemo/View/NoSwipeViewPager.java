package com.example.zftidemo.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import java.util.jar.Attributes;

//禁止侧滑的ViewPager
public class NoSwipeViewPager extends ViewPager {
    private boolean canSwiper = false;
    public NoSwipeViewPager(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }
    public void setCanSwiper(boolean canSwiper){
        this.canSwiper = canSwiper;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev){
        return canSwiper && super.onTouchEvent(ev);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        return canSwiper && super.onInterceptTouchEvent(ev);
    }
}
