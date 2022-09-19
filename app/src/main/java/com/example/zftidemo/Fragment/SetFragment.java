package com.example.zftidemo.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.zftidemo.MainActivity;
import com.example.zftidemo.R;
import com.example.zftidemo.dao.Constant;
import com.example.zftidemo.http.HttpService;
import com.example.zftidemo.http.HttpTool;
import com.example.zftidemo.utils.ImageHandler;
import com.example.zftidemo.utils.Monitor;


public class SetFragment extends Fragment implements View.OnClickListener {
    EditText edit_address;
    Button btn_connect;
    String address;
    Context context;
    ImageView imageView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.setting_layout,container,false);
        edit_address = (EditText) view.findViewById(R.id.edit_address);
        btn_connect = (Button) view.findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(this);
        edit_address.setHorizontallyScrolling(true);
        imageView = (ImageView) view.findViewById(R.id.signal);
        //软键盘单击空白处收回
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getActivity().onTouchEvent(event);
                return false;
            }
        });
        ImageHandler imageHandler = new ImageHandler((ImageView) view.findViewById(R.id.signal));
        (new Monitor(imageHandler)).start();
        edit_address.setInputType(InputType.TYPE_CLASS_NUMBER);
        String digits = "0123456789.";
        edit_address.setKeyListener(DigitsKeyListener.getInstance(digits));
        TextView.OnEditorActionListener enterListen = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEND){
                    address = edit_address.getText().toString();
                    login();
                }
                return false;
            }
        };
        edit_address.setOnEditorActionListener(enterListen);
        context = this.getActivity();

        return view;
    }
    @Override
    public void onClick(View v) {
        address = edit_address.getText().toString();

        login();
    }
    /**
     * 向服务器发送登录请求
     */
    private void login() {
        new Thread() {
            @Override
            public void run() {
                // 向服务器发起登录请求
                HttpService httpService = new HttpService(context);
                httpService.login(address);
            }
        }.start();
    }
}
