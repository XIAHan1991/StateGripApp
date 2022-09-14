package com.example.zftidemo.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.zftidemo.MainActivity;
import com.example.zftidemo.R;
import com.example.zftidemo.View.TextColor;
import com.example.zftidemo.http.HttpService;

import org.json.JSONException;

import static java.lang.Thread.sleep;

public class MesFragment extends Fragment implements View.OnClickListener {
    Button btn_query;
    Button im_back;
    Context context;
    View view;
    TextView textView;
    TextColor textColor = new TextColor();
    private static final int TAB_SIZE = 21;
    public TextView[] texts_data = new TextView[TAB_SIZE];
    public TextView[] texts_result = new TextView[TAB_SIZE];
    public int[] text_id_data = {R.id.data_1, R.id.data_2, R.id.data_3, R.id.data_4, R.id.data_5,
            R.id.data_6, R.id.data_7, R.id.data_8, R.id.data_9, R.id.data_10, R.id.data_11, R.id.data_12, R.id.data_13,
            R.id.data_14, R.id.data_15, R.id.data_16, R.id.data_17, R.id.data_18, R.id.data_19, R.id.data_20, R.id.data_21};
    public int[] text_id_result = {R.id.result_1, R.id.result_2, R.id.result_3, R.id.result_4, R.id.result_5,
            R.id.result_6, R.id.result_7, R.id.result_8, R.id.result_9, R.id.result_10, R.id.result_11, R.id.result_12, R.id.result_13,
            R.id.result_14, R.id.result_15, R.id.result_16, R.id.result_17, R.id.result_18, R.id.result_19, R.id.result_20, R.id.result_21};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.mes_layout,container,false);
        btn_query = (Button) view.findViewById(R.id.btn_query);
        btn_query.setOnClickListener(this);
        /*btn_his= (Button) view.findViewById(R.id.btn_his);
        btn_his.setOnClickListener(this);*/
        im_back = (Button) view.findViewById(R.id.back);
        im_back.setOnClickListener(this);
        //textView = (TextView) view.findViewById(R.id.data_1);
//        Spinner spinner = (Spinner) view.findViewById(R.id.sp_1);
//// 建立数据源
//        String[] mItems = {"合格", "不合格"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, mItems);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        for(int i = 0; i < TAB_SIZE; i++){
            texts_data[i] = (TextView) view.findViewById(text_id_data[i]);
            texts_result[i] = (TextView) view.findViewById(text_id_result[i]);
            texts_result[i].setTextColor(textColor.TextColor(texts_result[i].getText().toString()));
        }

        context = this.getActivity();

        return view;
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(getContext(),"hello",Toast.LENGTH_SHORT).show();
        switch (v.getId()){
            case R.id.back:
                Toast.makeText(getContext(),"页面跳转",Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.demo1,new QueryallFragment())
                        .commit();
                break;
            case R.id.btn_query:
                query();
                break;
        }
    }

    /**
     * 向服务器发起查询任务详情请求
     */
    private void query() {
        new Thread() {
            @Override
            public void run() {
                HttpService httpService = new HttpService(context);
                try {
                        httpService.taskDetail(texts_data, texts_result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }
}
