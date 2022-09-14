package com.example.zftidemo.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.zftidemo.Flag;
import com.example.zftidemo.MainActivity;
import com.example.zftidemo.R;
import com.example.zftidemo.View.ZoomImageView;
import com.example.zftidemo.dao.ConnectDB;
import com.example.zftidemo.dao.MeasureInformation;
import com.example.zftidemo.dao.ResultOrder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import q.rorbin.badgeview.QBadgeView;

import static android.os.Looper.getMainLooper;

public class HttpService {
    private static Context context;
    private int resLogin = 0;
    private static String ipAddress = "";
    private static String taskId = "";
    private static String queryTaskId = "";
    private static int http_current = 1;
    private static int currentNum = 0;
    private static boolean isFinalCurrent = false;

    public HttpService(Context context){
        this.context = context;
    }

    /**
     * 接收历史界面传过来的taskId
     */
    public void getTaskId(String get_taskId) {
        queryTaskId = get_taskId;
    }

    public List<MeasureInformation> parseRes(String res){
        List<MeasureInformation> resArray = JSONArray.parseArray(res, MeasureInformation.class);
        return resArray;
    }

    /**
     * 查询任务识别详情
     */
    public void taskDetail(TextView[] texts_data, TextView[] texts_result) throws JSONException {
        Map map = postTaskDetailData(ipAddress);

        String code = String.valueOf(map.get("code"));
        String message = String.valueOf(map.get("message"));
//        String task_id = String.valueOf(map.get("task_id"));
        String results = String.valueOf(map.get("results"));
        System.out.println(results);

        if (code.equals("0")&& !results.equals("[]")) {
            List<MeasureInformation> measureInformations = parseRes(results);
            for (MeasureInformation measureInformation : measureInformations){
                if (ResultOrder.resOrder.containsKey(measureInformation.getName())) {
                    Integer index = ResultOrder.resOrder.get(measureInformation.getName());
                    String value = measureInformation.getValue();
                    String result = measureInformation.getStatus();
                    String show = measureInformation.getShow();
                    if (show.equals("1")) {
                        showTextView(texts_data[index], value == null || value.equals("") ? "-" : value);
                        showTextView(texts_result[index], result.equals("0") ? "不合格" : "合格");
                    }
                }
            }
        } else {
            Looper.prepare();
            Toast.makeText(context, "code:" + code + ",message:" + message, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }
    public JSONObject getJsonForTaskList(String token, String current, String size, String start_date, String end_data, String  status) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            obj.put("current",current);
            obj.put("size", size);
            obj.put("start_date", start_date);
            obj.put("end_data", end_data);
            obj.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
    /**
     * 条件分页查询任务列表
     * @param address 请求服务ip
     */
    public Map postTaskListData(String address, String current, String size, String start_date, String end_date, String  status){
        String url = "http://" + address + "/api/task/list";
        String contentType = "application/json";

        HttpTool httpTool = new HttpTool();
        String token = httpTool.getData(context, "token");

        JSONObject jsonObject = getJsonForTaskList(token, current, size, start_date, end_date, status);
        //创建http连接
        HttpURLConnection connection = httpTool.httpConnect(url, contentType);
        // 向服务器上传Json数据
        httpTool.uploadJsonData(connection, jsonObject);
        // 解析数据
        Map map = httpTool.dataAnalyzeWithTaskList(connection);
//        // 数据展示
//        String code = String.valueOf(map.get("code"));
//        String message = String.valueOf(map.get("message"));
//        String task_id = String.valueOf(map.get("task_id"));
//        String status = String.valueOf(map.get("status"));
//        String imageList = String.valueOf(map.get("image_list"));
//        String results = String.valueOf(map.get("results"));
//
//        if (code.equals("0")) {
////                    textView.setText("操作成功！" +
////                            "任务ID：" + task_id + "\n" +
////                            "任务状态：" + status + "\n" +
////                            "图片列表：" + imageList + "\n" +
////                            "结果列表：" + results + "\n");
//            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
//        } else {
//            //textView.setText("code:" + code + ",message:" + message);
//            Toast.makeText(context, "code:" + code + ",message:" + message, Toast.LENGTH_SHORT).show();
//        }

        return map;
    }

    public void isFinalCurrent(Button btn_pre, Button btn_next,String size, String start_date, String end_date, String  status) {
        if (http_current <= 1) {
            // 首页
            btn_pre.post(new Runnable() {
                @Override
                public void run() {
                    btn_pre.setVisibility(View.INVISIBLE);
                }
            });
            btn_next.post(new Runnable() {
                @Override
                public void run() {
                    btn_next.setVisibility(View.VISIBLE);
                }
            });
        } else {
            // 中间
            btn_next.post(new Runnable() {
                @Override
                public void run() {
                    btn_next.setVisibility(View.VISIBLE);
                }
            });
            btn_pre.post(new Runnable() {
                @Override
                public void run() {
                    btn_pre.setVisibility(View.VISIBLE);
                }
            });
        }

        String current = String.valueOf(http_current + 1);
        Map map = postTaskListData(ipAddress, current, size, start_date, end_date, status);

        String records = String.valueOf(map.get("records"));

        System.out.println(records);

        if (records.equals("[]")) {
            // 最后一页
            isFinalCurrent = true;
            btn_next.post(new Runnable() {
                @Override
                public void run() {
                    btn_next.setVisibility(View.INVISIBLE);
                }
            });
//            btn_pre.post(new Runnable() {
//                @Override
//                public void run() {
//                    btn_pre.setVisibility(View.VISIBLE);
//                }
//            });
        }


    }

    /**
     * 条件查询识别任务详情
     */
    public void taskList(int type, TextView tx_pagenum, Button btn_pre, Button btn_next, ListView listView, String current, String size, String start_date, String end_date, String  status) {
        if (type == 0) {
            // 首页
            current = "1";
            http_current = 1;
        } else if (type == -1) {
            //上一页
            http_current--;
            current = String.valueOf(http_current);
        } else if (type == 1) {
            //下一页
            http_current++;
            current = String.valueOf(http_current);
        }

        Map map = postTaskListData(ipAddress, current, size, start_date, end_date, status);

        String code = String.valueOf(map.get("code"));
        String message = String.valueOf(map.get("message"));
        String records = String.valueOf(map.get("records"));

        System.out.println(records);

        if (records.equals("[]")) {

            Looper.prepare();
            Toast.makeText(context, "翻页无效！", Toast.LENGTH_SHORT).show();
            Looper.loop();

        } else {
            String str[] = records.split(",\\{");
            List<String> resultLists = Arrays.asList(str);

            String qualified[] = new String[resultLists.size()];
            String unqualified[] = new String[resultLists.size()];
            String task_id[] = new String[resultLists.size()];
            String create_time[] = new String[resultLists.size()];
            String list_status[] = new String[resultLists.size()];

            //数据解析
            for(int i = 0; i < resultLists.size(); i++){
                String str_list[] = resultLists.get(i).split("\":");
                List<String> resultList = Arrays.asList(str_list);

                unqualified[i] = resultList.get(2).replace("\"", "").replace(",qualified", "");
                qualified[i] = resultList.get(3).replace("\"", "").replace(",update_time", "");
                create_time[i] = resultList.get(6).replace("\"", "").replace(",task_id", "");
                task_id[i] = resultList.get(7).replace(",\"remark", "");
                list_status[i] = resultList.get(9).replace("}", "").replace("]", "");
            }

            if (code.equals("0")) {

                SimpleAdapter simpleAdapter = new SimpleAdapter(context,getData(task_id,list_status,create_time,qualified,unqualified),R.layout.device_view,new String[]{"id", "state", "time", "qualinum",
                        "unqualinum"}, new int[]{R.id.task_id, R.id.task_state,R.id.task_time,R.id.num_qualified,R.id.num_unqualified} );
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(simpleAdapter);
                    }
                });

                String finalCurrent = current;
                tx_pagenum.post(new Runnable() {
                    @Override
                    public void run() {
                        tx_pagenum.setText("第 "+ finalCurrent +" 页");
                    }
                });

//                if (http_current <= 1) {
//                    // 首页
//                    btn_next.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            btn_next.setVisibility(View.VISIBLE);
//                        }
//                    });
//                    btn_pre.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            btn_pre.setVisibility(View.INVISIBLE);
//                        }
//                    });
//                } else {
//                    // 中间
//                    btn_next.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            btn_next.setVisibility(View.VISIBLE);
//                        }
//                    });
//                    btn_pre.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            btn_pre.setVisibility(View.VISIBLE);
//                        }
//                    });
//                }

            } else {
                Looper.prepare();
                Toast.makeText(context, "code:" + code + ",message:" + message, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }


    }



//    /**
//     * 查询条件识别任务详情
//     */
//    public void taskList(ListView listView, String current, String size, String start_date, String end_date, String  status) {
//        //Map map = postTaskListData(ipAddress, current, size, start_date, end_date, status);
//
////        String code = String.valueOf(map.get("code"));
////        String message = String.valueOf(map.get("message"));
////        String records = String.valueOf(map.get("records"));
//
//        SimpleAdapter simpleAdapter = new SimpleAdapter(context,getData(),R.layout.device_view,new String[]{"id", "state", "time", "qualinum",
//                "unqualinum"}, new int[]{R.id.task_id, R.id.task_state,R.id.task_time,R.id.num_qualified,R.id.num_unqualified} );
//        listView.setAdapter(simpleAdapter);
//        //listView.setOnItemClickListener((AdapterView.OnItemClickListener) this);
//
////        if (code.equals("200")) {
//////            textView.post(new Runnable() {
//////                @Override
//////                public void run() {
//////                    textView.setText("操作成功！" +
//////                            "任务ID：" + task_id + "\n" +
//////                            "任务状态：" + status + "\n" +
//////                            "结果列表：" + results + "\n");
//////                }
//////            });
////            //showTextView(textView, records);
////            SimpleAdapter simpleAdapter = new SimpleAdapter(context,getData(),R.layout.device_view,new String[]{"id", "state", "time", "qualinum",
////                    "unqualinum"}, new int[]{R.id.task_id, R.id.task_state,R.id.task_time,R.id.num_qualified,R.id.num_unqualified} );
////            listView.setAdapter(simpleAdapter);
////            listView.setOnItemClickListener((AdapterView.OnItemClickListener) this);
////        } else {
////            Looper.prepare();
////            Toast.makeText(context, "code:" + code + ",message:" + message, Toast.LENGTH_SHORT).show();
////            Looper.loop();
////        }
//    }

    private List<Map<String,Object>> getData(String [] ids,String [] states,String [] times,String [] qualified_num,String [] unqualified_num){
//        int [] ids={1,2,3,4,5,6};
//        String [] states = {"已完成检测", "已完成检测", "已完成检测", "已完成检测", "已完成检测", "已完成检测"};
//        String [] times = {"2022-4-2 11:10:10", "2022-4-2 11:10:10","2022-4-2 11:10:10","2022-4-2 11:10:10","2022-4-2 11:10:10",
//                "2022-4-2 11:10:10"};
//        int [] qualified_num = {1,2,3,4,5,6};
//        int [] unqualified_num = {1,2,3,4,5,6};
        List<Map<String , Object>> list = new ArrayList<>();
        for (int i=0;i< ids.length;i++){
            Map map = new HashMap();
            map.put("id", ids[i]);
            map.put("state",states[i]);
            map.put("time",times[i]);
            map.put("qualinum",qualified_num[i]);
            map.put("unqualinum", unqualified_num[i]);
            list.add(map);
        }
        return list;
    }


    /**
     * 发送数据到textview
     * @param textView
     * @param data
     */
    public void showTextView(TextView textView, String data) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(data);
            }
        });
    }

    /**
     * 开启识别任务
     */
    public void taskDetect() {
        postTaskDetectData(ipAddress);
    }

    /**
     * 查询任务状态
     */
    public void getTaskStatus() {
        postTaskGetStatusData(ipAddress);
    }

    /**
     * 上传照片
     */
    public void uploadImg(Resources resources, ImageView[] imageView,int index,ImageView[] imageViews, Bitmap bitmap_load,Context context){
        String fileAddress[] = new String[6];
        fileAddress[0] = "/storage/emulated/0/DCIM/USBCameraTest/front/1.png";
        fileAddress[1] = "/storage/emulated/0/DCIM/USBCameraTest/front/2.png";
        fileAddress[2] = "/storage/emulated/0/DCIM/USBCameraTest/side/1.png";
        fileAddress[3] = "/storage/emulated/0/DCIM/USBCameraTest/side/2.png";
        fileAddress[4] = "/storage/emulated/0/DCIM/USBCameraTest/back/1.png";
        fileAddress[5] = "/storage/emulated/0/DCIM/USBCameraTest/back/2.png";

        // 上传图片
        // "/storage/emulated/0/app/1.jpg"
        Map map = postTaskUploadImgData(ipAddress, fileAddress[index], String.valueOf(index+1));
//        Map map1 = postTaskUploadImgData(ipAddress, fileAddress[0], "1");
//        Map map2 = postTaskUploadImgData(ipAddress, fileAddress[1], "2");
//        Map map3 = postTaskUploadImgData(ipAddress, fileAddress[2], "3");
//        Map map4 = postTaskUploadImgData(ipAddress, fileAddress[3], "4");
//        Map map5 = postTaskUploadImgData(ipAddress, fileAddress[4], "5");
//        Map map6 = postTaskUploadImgData(ipAddress, fileAddress[5], "6");
//        Flag.setQueryflag(1);
        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Imagewait(resources,imageView[index],context);
            }
        });
//        if(map.get("code").equals("0") ) {
//            Flag.setQueryflag(1);
//        } else {
//            Flag.setQueryflag(0);
//        }

//        for(int i = 0; i < 6; i++) {
//            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(fileAddress[i])); //从本地取图片
//                imageViews[i].setImageBitmap(bitmap); // 显示
//            } catch (FileNotFoundException e) {
//                Toast.makeText(context,"刷新失败！",Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }

//        Imagewait(resources,image1,context);

//        for(int i = 0; i < 6; i++) {
//            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(fileAddress[i])); //从本地取图片
//                Drawable[] array = new Drawable[2];
//                array[0] = new BitmapDrawable(bitmap); // 试验台区图片
//                array[1] = new BitmapDrawable(bitmap_load);  // √
//
//                LayerDrawable la = new LayerDrawable(array);
//
//                la.setLayerInset(1,0,0,600,835);
//                //la.setLayerInset(1,0,0,450,800);
//                ImageView imageView = imageViews[i];
//                imageView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        imageView.setImageDrawable(la);
//                    }
//                });
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }

    }

    /**
     * 创建任务
     */
    public void createTask(Button btn_createtask) {
        postTaskAddData(ipAddress);

        HttpTool httpTool = new HttpTool();
        String runResult = httpTool.getData(context, "runResult");
        String error = httpTool.getData(context, "error");
        String task_id = httpTool.getData(context, "task_id");

        if(runResult.equals("1")) {
            taskId = task_id;
//            btn_createtask.post(new Runnable() {
//                @Override
//                public void run() {
//                    btn_createtask.setEnabled(false);
//                }
//            });

            Looper.prepare();
            Toast.makeText(context, "创建任务成功！", Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else {
            Looper.prepare();
            Toast.makeText(context, "创建任务失败！" + error, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    /**
     * 登录
     */
    public void login(String address) {
        //初始服务器地址
        //String address = "192.168.20.81:9010";
        //address = "192.168.20.122:9010";
        String url = address + ":9010";
        // 传送数据
        postLoginData(url);

        HttpTool httpTool = new HttpTool();
        String runResult = httpTool.getData(context, "runResult");
        String error = httpTool.getData(context, "error");

        if(runResult.equals("1")) {
            httpTool.setData(context, "address", address);
            ipAddress = url;
            Looper.prepare();
            Toast.makeText(context, "登录成功！", Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else {
            Looper.prepare();
            Toast.makeText(context, "登录失败！" + error, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    /**
     * 向服务器发送登录请求
     * 请求参数包括：app_key，app_secret，mac
     * 返回结果map中包括：code，message，device_id，create_time，name，mac，token
     * @param address 请求服务ip
     */
    public void postLoginData(String address) {
        String url = "http://" + address + "/api/device/login"; // 请求url
        String contentType = "application/json"; // 请求方式
        HttpTool httpTool = new HttpTool();
        httpTool.deleteData(context);
        // 将请求参数转为json格式
        JSONObject jsonObject = getJsonForLogin("8442d7fed5604c33a7e7ccbb7117af63", "e10adc3949ba59abbe56e057f20f883e", "3C-6A-A7-DD-61-2F");
        // 创建http连接
        HttpURLConnection connection = httpTool.httpConnect(url, contentType);
        // 向服务器上传Json数据
        httpTool.uploadJsonData(connection, jsonObject);
        // 解析数据
        Map map = httpTool.dataAnalyzeWithLogin(connection);
        // UI结果反馈

        String code = String.valueOf(map.get("code"));
        String message = String.valueOf(map.get("message"));
        String token = String.valueOf(map.get("token"));
        httpTool.setData(context, "error", ""); // 初始化
        if(code.equals("0")) {
            httpTool.setData(context, "token", token); // 将token暂存本地
            httpTool.setData(context, "runResult", "1");
            httpTool.setData(context, "address", address);
        } else {
            httpTool.setData(context, "runResult", "0");
            if(code.equals("null")) {
                httpTool.setData(context, "error", "\n服务器地址错误,请修改后重新连接！");
            } else {
                httpTool.setData(context, "error", "\ncode:" + code + ",message:" + message);
            }
        }
    }

    /**
     * 向服务器发送创建任务请求
     * @param address 请求服务ip
     */
    public void postTaskAddData(String address) {
        String url = "http://" + address + "/api/task/add";
        String contentType = "application/json";
        HttpTool httpTool = new HttpTool();
        // 从本地取出token
        String token = httpTool.getData(context, "token");
        Log.d("***************", token);

        JSONObject jsonObject = getJsonForTaskAdd(token, "test1");
        // 创建http连接
        HttpURLConnection connection = httpTool.httpConnect(url, contentType);
        // 向服务器上传Json数据
        httpTool.uploadJsonData(connection, jsonObject);
        // 解析数据
        Map map = httpTool.dataAnalyzeWithTaskAdd(connection);
        // 数据展示
        String code = String.valueOf(map.get("code"));
        String message = String.valueOf(map.get("message"));
        String task_id = String.valueOf(map.get("task_id"));
//                if (code.equals("0")) {
//                    //textView.setText("创建成功！taskID:" + task_id);
//                    Toast.makeText(context, "创建任务成功！", Toast.LENGTH_SHORT).show();
//                    httpTool.setData(context, "task_id", task_id);
//                    //httpTool.getData(context, "task_id");
//                }

        httpTool.setData(context, "error", ""); // 初始化
        httpTool.setData(context, "task_id", ""); // 初始化
        if(code.equals("0")) {
            httpTool.setData(context, "task_id", task_id);
        } else {
            httpTool.setData(context, "runResult", "0");
            httpTool.setData(context, "error", "\ncode:" + code + ",message:" + message);
        }
    }

    /**
     * 上传照片
     * @param address 请求服务ip
     */
    public Map postTaskUploadImgData(String address, String fileAddress, String type) {
        String url = "http://" + address + "/api/task/uploadImg";
        String contentType = "multipart/form-data";
        File file = new File(fileAddress); // 图片文件 "/storage/emulated/0/app/1.jpg"

        HttpTool httpTool = new HttpTool();
        String token = httpTool.getData(context, "token");
        Log.d("***************", token);
        String taskId = httpTool.getData(context, "task_id");
        Log.d("***************", taskId);

        final Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("task_id", taskId); // 任务id
        params.put("type", type); // 图片类型
        final Map<String, File> files = new HashMap<String, File>();
        files.put("file", file);

        // 创建http连接
        HttpURLConnection connection = httpTool.httpConnect(url, contentType);
        // 向服务器上传Json数据
        httpTool.uploadFormData(connection, params, files);
        // 解析数据
        Map map = httpTool.dataAnalyzeWithTaskUploadImg(connection);
        // 数据展示
        //String code = String.valueOf(map.get("code"));

        return map;
    }

    /**
     * 开启识别任务
     * @param address 请求服务器ip
     */
    public void postTaskDetectData(String address){
        String url = "http://" + address + "/api/task/detect";
        String contentType = "application/json";

        HttpTool httpTool = new HttpTool();
        String token = httpTool.getData(context, "token");
        String taskId = httpTool.getData(context, "task_id");
        int isNeedResult = 1; // 是否需要同步等待结果
        JSONObject jsonObject = getJsonForTaskDetect(token, taskId, isNeedResult);

        //创建http连接
        HttpURLConnection connection = httpTool.httpConnect(url, contentType);
        // 向服务器上传Json数据
        httpTool.uploadJsonData(connection, jsonObject);
        // 解析数据
        Map map = httpTool.dataAnalyzeWithTaskDetect(connection);
        // 数据展示
        String code = String.valueOf(map.get("code"));
        String message = String.valueOf(map.get("message"));
        if (code.equals("0")) {
            Looper.prepare();
            Toast.makeText(context, "识别成功！", Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else {
            Looper.prepare();
            Toast.makeText(context, "code:" + code + "message:" + message, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    /**
     * 查询任务状态
     * @param address 请求服务ip
     */
    public void postTaskGetStatusData(String address) {
        String url = "http://" + address + "/api/task/getStatus";
        String contentType = "application/json";

        HttpTool httpTool = new HttpTool();
        String token = httpTool.getData(context, "token");
        String taskId = httpTool.getData(context, "task_id");
        JSONObject jsonObject = getJsonForTaskGetStatus(token, taskId);

        // 创建http连接
        HttpURLConnection connection = httpTool.httpConnect(url, contentType);
        // 向服务器上传Json数据
        httpTool.uploadJsonData(connection, jsonObject);
        // 解析数据
        Map map = httpTool.dataAnalyzeWithTaskGetStatus(connection);
        // 数据展示
        String status = String.valueOf(map.get("status"));
        switch (status) {
            case "0":
                Looper.prepare();
                Toast.makeText(context, "图片不全", Toast.LENGTH_SHORT).show();
                Looper.loop();
                break;
            case "1":
                Looper.prepare();
                Toast.makeText(context, "图片齐全，待识别！", Toast.LENGTH_SHORT).show();
                Looper.loop();
                break;
            case "2":
                Looper.prepare();
                Toast.makeText(context, "识别中（已触发命令）", Toast.LENGTH_SHORT).show();
                Looper.loop();
                break;
            case "3":
                ConnectDB.updateStaticsResult(ipAddress.split(":")[0], taskId);
                Looper.prepare();
                Toast.makeText(context, "已识别完成！", Toast.LENGTH_SHORT).show();
                Looper.loop();
                break;
            case "4":
                Looper.prepare();
                Toast.makeText(context, "识别失败（识别进程无法访问等等）", Toast.LENGTH_SHORT).show();
                Looper.loop();
        }
    }

    /**
     * 查询任务识别详情
     * @param address 请求服务ip
     */
    public Map postTaskDetailData(String address){
        String url = "http://" + address + "/api/task/detail";
        String contentType = "application/json";

        HttpTool httpTool = new HttpTool();
        String token = httpTool.getData(context, "token");
        //String taskId = httpTool.getData(context, "task_id");
//        String taskId = "146"; // 测试用例
        String post_taskId;
        if (queryTaskId.equals("")) {
            post_taskId = taskId;
        } else {
            post_taskId = queryTaskId;
        }

//        JSONObject jsonObject = getJsonForTaskDetail(token, taskId);
        JSONObject jsonObject = getJsonForTaskDetail(token, post_taskId);
        //创建http连接
        HttpURLConnection connection = httpTool.httpConnect(url, contentType);
        // 向服务器上传Json数据
        httpTool.uploadJsonData(connection, jsonObject);
        // 解析数据
        Map map = httpTool.dataAnalyzeWithTaskDetail(connection);
//        // 数据展示
//        String code = String.valueOf(map.get("code"));
//        String message = String.valueOf(map.get("message"));
//        String task_id = String.valueOf(map.get("task_id"));
//        String status = String.valueOf(map.get("status"));
//        String imageList = String.valueOf(map.get("image_list"));
//        String results = String.valueOf(map.get("results"));
//
//        if (code.equals("0")) {
////                    textView.setText("操作成功！" +
////                            "任务ID：" + task_id + "\n" +
////                            "任务状态：" + status + "\n" +
////                            "图片列表：" + imageList + "\n" +
////                            "结果列表：" + results + "\n");
//            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
//        } else {
//            //textView.setText("code:" + code + ",message:" + message);
//            Toast.makeText(context, "code:" + code + ",message:" + message, Toast.LENGTH_SHORT).show();
//        }

        return map;
    }

    /**
     * 登录请求参数String转json
     * @param key 登录key
     * @param secret 登录密钥
     * @param mac 设备mac地址
     * @return obj json数据
     */
    public JSONObject getJsonForLogin(String key, String secret, String mac) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("app_key", key);
            obj.put("app_secret",secret);
            obj.put("mac", mac);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 创建任务请求参数String转json
     * @param token token
     * @param task_name 任务名称
     * @return obj json数据
     */
    public JSONObject getJsonForTaskAdd(String token, String task_name) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            obj.put("task_name",task_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 开启识别任务请求参数string转json
     * @param token
     * @param taskId
     * @param isNeedResult 是否需要同步等待结果
     * @return
     */
    public JSONObject getJsonForTaskDetect(String token, String taskId, int isNeedResult) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            obj.put("task_id",taskId);
            obj.put("is_need_result",isNeedResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 查询任务状态请求参数string转json
     * @param token
     * @param taskId
     * @return
     */
    public JSONObject getJsonForTaskGetStatus(String token, String taskId) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            obj.put("task_id",taskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 查询识别任务详情参数string转json
     * @param token
     * @param taskId
     * @return
     */
    public JSONObject getJsonForTaskDetail(String token, String taskId) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("token", token);
            obj.put("task_id",taskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
    public void Imagewait(Resources resources, View view,Context context){
        QBadgeView qBadgeView = new QBadgeView(context);
        qBadgeView.post(new Runnable() {
                    @Override
                    public void run() {
                        qBadgeView.setBadgeBackground(resources.getDrawable(R.drawable.checkcircle));
                        qBadgeView.setBadgeText("");
                        qBadgeView.bindTarget(view);
                        qBadgeView.setBadgeGravity(Gravity.END|Gravity.TOP);
                        qBadgeView.setGravityOffset(1,true);
                        qBadgeView.setBadgeTextSize(8, true);
                        qBadgeView.setBadgePadding(15, true);
                        //qBadgeView.hide(true);
                    }
                });
//        qBadgeView.bindTarget(view);


    }

}
