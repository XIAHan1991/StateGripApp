package com.example.zftidemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.core.content.ContextCompat;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogDatePickerSelect {
    private Context context;
    private OnDateSelectCallBack callBack;
    private AlertDialog dialog;
    DatePicker start_Date,end_Date;
    TextView tx_reset,tx_confirm;
    ImageView im_close;


    public DialogDatePickerSelect(Context context, OnDateSelectCallBack dateSelectCallBack){
        this.context = context;
        this.callBack = dateSelectCallBack;
    }
    public void showDatePickView(){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        start_Date = view.findViewById(R.id.early_date);
        end_Date = view.findViewById(R.id.later_date);
        tx_reset = view.findViewById(R.id.tx_reset);
        tx_confirm = view.findViewById(R.id.tx_confirm);
        im_close=view.findViewById(R.id.im_close);
        im_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tx_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //留给glc发挥的地方
                // 嗯，好的
                dialog.dismiss();
                callBack.onReset();



            }
        });
        tx_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar start_cal = Calendar.getInstance();
                Calendar end_cal = Calendar.getInstance();
                start_cal.set(start_Date.getYear(), start_Date.getMonth(),start_Date.getDayOfMonth());
                end_cal.set(end_Date.getYear(), end_Date.getMonth(),end_Date.getDayOfMonth());

                callBack.onDateSelected(simpleDateFormat.format(start_cal.getTime())
                        ,simpleDateFormat.format(end_cal.getTime()));

                dialog.dismiss();

            }
        });
        dialog = builder.create();
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 设置时间选择器分割线颜色
     * @param
     *//*
    private void setDatePickerDividerColor(DatePicker datePicker){
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount();i++){
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for(Field pf : pickerFields){
                if(pf.getName().equals("mSelectionDivider")){
                    pf.setAccessible(true);
                    try {
                        pf.set(picker,new ColorDrawable(ContextCompat.getColor(context,R.color.blue_500)));
                    }catch (IllegalAccessException e){
                        e.printStackTrace();
                    }catch (Resources.NotFoundException e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }*/
    public interface OnDateSelectCallBack{
        void onDateSelected(String StartDate, String EndDate);
        void onReset();
    }
}
