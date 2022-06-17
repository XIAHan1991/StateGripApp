package com.example.zftidemo.View;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

public abstract class LazyFragment extends Fragment {
    protected boolean isVisible;
    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    private  boolean isLazyloaded;
    private  boolean isPrepared;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        isPrepared=true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        if(getUserVisibleHint()) {
//            isVisible = true;
//            onVisible();
//        } else {
//            isVisible = false;
//            onInvisible();
//        }
        lazyLoad();
    }

    protected void onVisible(){
        lazyLoad();
    }

    protected void lazyLoad(){
        if (getUserVisibleHint()&&isPrepared&&!isLazyloaded){
            onLazyload();
            isLazyloaded=true;
        }
    };
    @UiThread
    protected void onLazyload(){}
}