package com.example.zftidemo.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpTool<T>  {
//
//    public static final int MODE_GET = 101;
//    public static final int MODE_POST = 102;
    private static final String FILE_NAME = "zfti"; //文件名
//
//    private int message_what;
//    private int current_mode;
//    private int type;
//
//    private JSONObject obj;
//    private String str_url;
//    private String data = "";
//    private HashMap<String, String> param = new HashMap<String, String>();
//    private String token;
//    private String contentType;
//    private Context context;

    public HttpTool() {

    }
    /**
     * 解析查询识别任务详情请求应答数据
     * @param connection
     * @return Map 数据集
     */
    public Map dataAnalyzeWithTaskList(HttpURLConnection connection) {
        Map<String, Object> map = new HashMap<>();//创建map对象
        int responseCode = 0;

        try {
            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                String bufferString = buffer.readLine();
                Log.d("lookInoutLine", bufferString);

                JSONObject jsonObject = new JSONObject(bufferString);
                String code = jsonObject.optString("code");
                String message = jsonObject.optString("message");
                String data = jsonObject.optString("data");
                map.put("code", code);
                map.put("message", message);

                //String转JSONObject
                JSONObject result = new JSONObject(data);
                if(result != null) {
                    String records = String.valueOf(result.get("records"));
                    map.put("records", records);
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }
    /**
     * @param MODL         选择POST或GET方式，HttpTool.MODE_GET / HttpTool.MODE_POST
     * @param url          发送http请求的url，包括ip后的router
     * @param message_what 使用Handler时，分辨消息的message.what
     */
//    HttpTool(int MODL, String url, int message_what, Handler handler, JSONObject jsonObject, int doType, Context context, String contentType) {
//        current_mode = MODL;
//        str_url = url;
//        this.message_what = message_what;
//        this.handler = handler;
//        this.type = doType;
//        this.context = context;
//        this.obj = jsonObject;
//        this.contentType = contentType;
//
//        //Log.d("T", String.valueOf(result));
//    }

//    /**
//     * 存储token
//     * @param context
//     * @param token
//     */
//    public void put(Context context, String token){
//        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("token", token);
//        editor.apply();
//    }
//
//    /**
//     * 取出token
//     * @param context
//     * @@return token
//     */
//    public String get(Context context){
//        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        return sharedPreferences.getString("token", "");
//    }

    /**
     * @param MODE 设置的新模式 HttpTool.MODE_GET / HttpTool.MODE_POST
     * @return 是否成功设置
     */
//    public boolean setMODE(int MODE){
//        if((MODE!=MODE_GET)||(MODE!=MODE_POST)){
//            return false;
//        }else {
//            current_mode = MODE;
//            return true;
//        }
//    }

    /**
     * 清空要发送的数据
     */
//    public void clearData() {
//        data = "";
//        param.clear();
//    }

    /**
     * @param key   要发送数据的key值
     * @param value 要发送数据的value值
     */
//    public void addData(String key, String value) {
//        if (current_mode == MODE_GET) {
//            addGETData(key, value);
//        } else {
//            addPOSTData(key, value);
//        }
//    }

//    private void addGETData(String key, String value) {
//        if (!data.equals("")) {
//            data = data + "&";
//        } else {
//            data = "?";
//        }
//        data = data + key + "=" + value;
//    }

//    private void addPOSTData(String key, String value) {
//        param.put(key, value);
//    }


//    private String getDataString(HashMap<String, String> params) throws Exception {
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            if (first)
//                first = false;
//            else
//                result.append("&");
//            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//        }
//        return result.toString();
//    }

//    private void sendMessage() {
//        Looper myLooper, mainLooper;
//        myLooper = Looper.myLooper();
//        mainLooper = Looper.getMainLooper();    //获得自己的main的looper
//        String obj;
//        if (myLooper == null) {
//            mNoLooperThreadHandler = new MainActivity.MyHandler(mainLooper);
//            obj = "NoLooperThread has no looper andhandleMessage function executed in main thread!";
//        } else {
//            mNoLooperThreadHandler = new MainActivity.MyHandler(myLooper);
//            obj = "This is from NoLooperThread self andhandleMessage function executed in NoLooperThread!";
//        }
//        mNoLooperThreadHandler.removeMessages(message_what);    //清空消息队列
//
//        Message m = mNoLooperThreadHandler.obtainMessage(message_what, 1, 1, obj);    //生成消息对象
//        mNoLooperThreadHandler.sendMessage(m);   //发送消息
//    }

//    public void sendMessage(Object obj) {
//
//        Looper.prepare();
//
//        handler.removeMessages(message_what);    //清空消息队列
//        Message message = handler.obtainMessage(message_what, 1, 1, obj);
//        handler.sendMessage(message);
////        Looper.loop();
//    }





    /**
     * 使用sharedPreferences将数据存入本地xml文件中
     *
     * @param context 上下文
//     * @param key   键
//     * @param value 值
     */
    public static void setData(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString(key, value);
        editor.commit();//提交修改
    }
//    public static void setData(Context context, Map<String, String> map) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            editor.putString(entry.getKey(), entry.getValue());
//        }
//        editor.commit();//提交修改
//    }

    /**
     * 使用sharedPreferences将数据从本地xml中取出
     *
     * @param context 上下文
     * @param key   键
     */
    public static String getData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString(key, "");
    }

    /**
     * 清空本地xml数据
     * @param context
     */
    public static void deleteData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


    /**
     * 建立Http连接
     * @param str_url 请求url
     * @param contentType 请求方式
     * @return connection HttpURLConnection对象
     */
    public HttpURLConnection httpConnect(String str_url, String contentType) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(str_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000); // 连接超时
            connection.setRequestMethod("POST"); // 请求方式
            connection.setInstanceFollowRedirects(false); // 重定向
            connection.setReadTimeout(5000); // 读取超时
            connection.setDoOutput(true); // 允许文件输出流
            connection.setDoInput(true);  //允许文件输入流
            connection.setUseCaches(false); // 不允许使用缓存
            connection.setRequestProperty("Content-Type", contentType); // 传送内容类
            connection.setRequestProperty("connection", "keep-alive"); //保持连接
            connection.setRequestProperty("charset", "UTF-8"); // 编码格式
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 上传json数据
     * @param connection HttpURLConnection对象
     * @param jsonObject 数据
     */
    public void uploadJsonData(HttpURLConnection connection, JSONObject jsonObject) {
        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(connection.getOutputStream());
            wr.write(jsonObject.toString().getBytes("UTF-8"));//这样可以处理中文乱码问题
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传form表单
     *
     * @param params text content
     * @param files pictures
     * @return String result of Service response
     * @throws IOException
     */
    public void uploadFormData(HttpURLConnection connection, Map<String, String> params, Map<String, File> files) {
        String BOUNDARY = UUID.randomUUID().toString(); // 分界线
        String PREFIX ="--"; // 前缀
        String LINEND = "\r\n"; // 换行
        String MULTIPART_FROM_DATA ="multipart/form-data"; // 传送内容格式
        String CHARSET ="UTF-8";
        try {
            connection.setReadTimeout(10* 1000);// 缓存的最长时间
            connection.setDoInput(true);// 允许输入
            connection.setDoOutput(true);// 允许输出
            connection.setUseCaches(false);// 不允许使用缓存
            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection","keep-alive");
            connection.setRequestProperty("Charsert","UTF-8");
            connection.setRequestProperty("Content-Type", MULTIPART_FROM_DATA +";boundary=" + BOUNDARY);

            // 发送非图片参数
            StringBuilder sb =new StringBuilder();
            for(Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\""+ LINEND);
                sb.append("Content-Type: text/plain; charset="+ CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit"+ LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }
            DataOutputStream outStream =new DataOutputStream(connection.getOutputStream());
            outStream.write(sb.toString().getBytes());
            // 发送文件数据
            if(files != null)
                for(Map.Entry<String, File> file : files.entrySet()) {
                    StringBuilder sb1 =new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                            + file.getValue().getName() +"\"" + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset="+ CHARSET + LINEND);
                    sb1.append(LINEND);
                    outStream.write(sb1.toString().getBytes());
                    InputStream is =new FileInputStream(file.getValue());
                    byte[] buffer =new byte[1024];
                    int len = 0;
                    while((len = is.read(buffer)) != -1) {
                        outStream.write(buffer,0, len);
                    }
                    is.close();
                    outStream.write(LINEND.getBytes());
                }
            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            outStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析登录请求应答数据
     * @param connection
     * @return Map 数据集
     */
    public Map dataAnalyzeWithLogin(HttpURLConnection connection) {
        Map<String, String> map = new HashMap<String, String>();//创建map对象
        int responseCode = 0;
        try {
            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String bufferString = buffer.readLine();
                Log.d("lookInoutLine", bufferString); // 输出查看返回数据

                JSONObject jsonObject = new JSONObject(bufferString);
                String code = jsonObject.optString("code");
                String message = jsonObject.optString("message");
                String data = jsonObject.optString("data");

                //String转JSONObject
                JSONObject result = new JSONObject(data);
                String device_id = String.valueOf(result.get("device_id"));
                String create_time = String.valueOf(result.get("create_time"));
                String name = String.valueOf(result.get("name"));
                String mac = String.valueOf(result.get("mac"));
                String token = String.valueOf(result.get("token"));

                map.put("code", code);
                map.put("message", message);
                map.put("device_id", device_id);
                map.put("create_time", create_time);
                map.put("name", name);
                map.put("mac", mac);
                map.put("token", token);
            }
            connection.disconnect(); // 断开连接
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map; // 返回结果
    }

    /**
     * 解析创建任务请求应答数据
     * @param connection
     * @return Map 数据集
     */
    public Map dataAnalyzeWithTaskAdd(HttpURLConnection connection) {
        Map<String, String> map = new HashMap<String, String>();//创建map对象
        int responseCode = 0;
        try {
            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String bufferString = buffer.readLine();
                Log.d("lookInoutLine", bufferString);

                JSONObject jsonObject = new JSONObject(bufferString);
                String code = jsonObject.optString("code");
                String message = jsonObject.optString("message");
                String data = jsonObject.optString("data");

                //String转JSONObject
                JSONObject result = new JSONObject(data);
                String task_id = String.valueOf(result.get("task_id"));

                map.put("code", code);
                map.put("message", message);
                map.put("task_id", task_id);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解析上传图片请求应答数据
     * @param connection
     * @return Map 数据集
     */
    public Map dataAnalyzeWithTaskUploadImg(HttpURLConnection connection) {
        Map<String, String> map = new HashMap<String, String>();//创建map对象
        int responseCode = 0;
        try {
            responseCode = connection.getResponseCode();
            System.out.println("responseCode:" + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String bufferString = buffer.readLine();
                Log.d("lookInoutLine", bufferString);

                JSONObject jsonObject = new JSONObject(bufferString);
                String code = jsonObject.optString("code");
                String message = jsonObject.optString("message");

                map.put("code", code);
                map.put("message", message);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解析开启识别请求应答数据
     * @param connection
     * @return Map 数据集
     */
    public Map dataAnalyzeWithTaskDetect(HttpURLConnection connection) {
        Map<String, String> map = new HashMap<String, String>();//创建map对象
        int responseCode = 0;
        try {
            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                String bufferString = buffer.readLine();
                Log.d("lookInoutLine", bufferString);

                JSONObject jsonObject = new JSONObject(bufferString);
                String code = jsonObject.optString("code");
                String message = jsonObject.optString("message");
                String data = jsonObject.optString("data");
                map.put("code", code);
                map.put("message", message);

                //String转JSONObject
                JSONObject result = new JSONObject(data);
                if(result != null) {
                    String taskId = String.valueOf(result.get("task_id"));
                    String taskName = String.valueOf(result.get("task_name"));
                    String status = String.valueOf(result.get("status"));
                    String results = String.valueOf(result.get("results"));

                    map.put("task_id", taskId);
                    map.put("task_name", taskName);
                    map.put("status", status);
                    map.put("results", results);
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解析查询任务状态请求应答数据
     * @param connection
     * @return Map 数据集
     */
    public Map dataAnalyzeWithTaskGetStatus(HttpURLConnection connection) {
        Map<String, String> map = new HashMap<String, String>();//创建map对象
        int responseCode = 0;

        try {
            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                String bufferString = buffer.readLine();
                Log.d("lookInoutLine", bufferString);

                JSONObject jsonObject = new JSONObject(bufferString);
                String code = jsonObject.optString("code");
                String message = jsonObject.optString("message");
                String data = jsonObject.optString("data");
                map.put("code", code);
                map.put("message", message);

                //String转JSONObject
                JSONObject result = new JSONObject(data);
                if(result != null) {
                    String taskId = String.valueOf(result.get("task_id"));
                    String status = String.valueOf(result.get("status"));
                    map.put("task_id", taskId);
                    map.put("status", status);
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 解析查询识别任务详情请求应答数据
     * @param connection
     * @return Map 数据集
     */
    public Map dataAnalyzeWithTaskDetail(HttpURLConnection connection) {
        Map<String, Object> map = new HashMap<>();//创建map对象
        int responseCode = 0;

        try {
            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                String bufferString = buffer.readLine();
                Log.d("lookInoutLine", bufferString);

                JSONObject jsonObject = new JSONObject(bufferString);
                String code = jsonObject.optString("code");
                String message = jsonObject.optString("message");
                String data = jsonObject.optString("data");
                map.put("code", code);
                map.put("message", message);

                //String转JSONObject
                JSONObject result = new JSONObject(data);
                if(result != null) {
                    String taskId = String.valueOf(result.get("task_id"));
                    String taskName = String.valueOf(result.get("task_name"));
                    String status = String.valueOf(result.get("status"));
                    String imageList = String.valueOf(result.get("image_list"));
                    String results = String.valueOf(result.get("results"));

                    map.put("task_id", taskId);
                    map.put("task_name", taskName);
                    map.put("status", status);
                    map.put("image_list", imageList);
                    map.put("results", results);
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     *
     * @param url Service net address
     * @param params text content
     * @param files pictures
     * @return String result of Service response
     * @throws IOException
     */
//    public void post(String url, Map<String, String> params, Map<String, File> files) {
//        String BOUNDARY = java.util.UUID.randomUUID().toString();
//        String PREFIX ="--", LINEND = "\r\n";
//        String MULTIPART_FROM_DATA ="multipart/form-data";
//        String CHARSET ="UTF-8";
//        try {
//            URL uri = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
//            conn.setReadTimeout(10* 1000);// 缓存的最长时间
//            conn.setDoInput(true);// 允许输入
//            conn.setDoOutput(true);// 允许输出
//            conn.setUseCaches(false);// 不允许使用缓存
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("connection","keep-alive");
//            conn.setRequestProperty("Charsert","UTF-8");
//            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA +";boundary=" + BOUNDARY);
//
//            // 首先组拼文本类型的参数
//            StringBuilder sb =new StringBuilder();
//            for(Map.Entry<String, String> entry : params.entrySet()) {
//                sb.append(PREFIX);
//                sb.append(BOUNDARY);
//                sb.append(LINEND);
//                sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\""+ LINEND);
//                sb.append("Content-Type: text/plain; charset="+ CHARSET + LINEND);
//                sb.append("Content-Transfer-Encoding: 8bit"+ LINEND);
//                sb.append(LINEND);
//                sb.append(entry.getValue());
//                sb.append(LINEND);
//            }
//            DataOutputStream outStream =new DataOutputStream(conn.getOutputStream());
//            outStream.write(sb.toString().getBytes());
//            // 发送文件数据
//            if(files != null)
//                for(Map.Entry<String, File> file : files.entrySet()) {
//                    StringBuilder sb1 =new StringBuilder();
//                    sb1.append(PREFIX);
//                    sb1.append(BOUNDARY);
//                    sb1.append(LINEND);
//                    sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
//                            + file.getValue().getName() +"\"" + LINEND);
//                    sb1.append("Content-Type: application/octet-stream; charset="+ CHARSET + LINEND);
//                    sb1.append(LINEND);
//                    outStream.write(sb1.toString().getBytes());
//                    InputStream is =new FileInputStream(file.getValue());
//                    byte[] buffer =new byte[1024];
//                    int len = 0;
//                    while((len = is.read(buffer)) != -1) {
//                        outStream.write(buffer,0, len);
//                    }
//                    is.close();
//                    outStream.write(LINEND.getBytes());
//                }
//            // 请求结束标志
//            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
//            outStream.write(end_data);
//            outStream.flush();
//            // 得到响应码
//            int responseCode = conn.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//
//                InputStreamReader in = new InputStreamReader(conn.getInputStream());
//                BufferedReader buffer = new BufferedReader(in);
//                String inputLine = null;
//                String bufferString = buffer.readLine();
//                Log.d("lookInoutLine", bufferString);
//
//                JSONObject jsonObject = new JSONObject(bufferString);
//                String code = jsonObject.optString("code");
//                //String message = jsonObject.optString("message");
//                //String data = jsonObject.optString("data");
//
//            }
//            outStream.close();
//            conn.disconnect();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
}