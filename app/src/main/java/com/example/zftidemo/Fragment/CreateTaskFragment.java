package com.example.zftidemo.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.zftidemo.MainActivity;
import com.example.zftidemo.R;
import com.example.zftidemo.dao.ConnectDB;
import com.example.zftidemo.dao.Constant;
import com.example.zftidemo.http.HttpService;
import com.example.zftidemo.utils.ImageHandler;
import com.example.zftidemo.utils.Monitor;

public class CreateTaskFragment extends Fragment implements View.OnClickListener {
    Button btn_createtask;
    Context context;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.createtask,container,false);
        btn_createtask = (Button) view.findViewById(R.id.btn_createtask);
        btn_createtask.setOnClickListener(this);

        context = this.getActivity();
        ImageHandler imageHandler = new ImageHandler((ImageView) view.findViewById(R.id.signal));
        (new Monitor(imageHandler)).start();
        return view;
    }
    @Override
    public void onClick(View v) {
        //Toast.makeText(context, "创建任务成功！", Toast.LENGTH_SHORT).show();
        createTask();
        final MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setFragmentSkipInterface(new MainActivity.FragmentSkipInterface() {
            @Override
            public void gotoFragment(ViewPager viewPager) {
                /** 跳转到第二个页面的逻辑 */
                viewPager.setCurrentItem(1);
            }
        });
        mainActivity.skipToFragment();
    }

    private void createTask() {
        new Thread() {
            @Override
            public void run() {
                // 向服务器发起创建任务请求
                ConnectDB.setIsInsert(true);
                HttpService httpService = new HttpService(context);
                httpService.createTask(btn_createtask);
            }
        }.start();
    }
}
