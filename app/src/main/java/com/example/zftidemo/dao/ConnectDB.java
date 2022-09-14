package com.example.zftidemo.dao;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.zftidemo.http.HttpService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConnectDB {
    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBUSER = "root";
    private static final String DBPASSWORD = "123456";
    private static final String FILE_NAME = "zfti"; //文件名
    private static boolean isInsert = false;
    private static int insertCount = 1;

    public static void setIsInsert(boolean isInsert1) {
        isInsert = isInsert1;
    }

    /*
     * 将时间转换为时间戳
     */
    public static String getNowTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * 使用sharedPreferences将数据从本地xml中取出
     * @param context 上下文
     * @param key   键
     */
    public static String getData(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getString(key, "");
    }
    public static boolean updateStaticsResult(String ipAddress, String task_id){
        Connection c = null;

        String dbURL = "jdbc:mysql://" + ipAddress + ":3306/artifact?useSSL=false";

        try {
            Class.forName(DBDRIVER);
            System.out.println("驱动加载成功===============");
        } catch (Exception e) {
            System.out.println("驱动加载失败================");
        }

        try {
            c = DriverManager.getConnection(dbURL, DBUSER, DBPASSWORD);

            if (c != null ) {
                System.out.println( c +"连接成功===========");
                String sql;
                sql = "UPDATE ap_task \n" +
                        "SET qualified = ( SELECT count(*) FROM `ap_task_result` t1 WHERE task_id = " +task_id+
                        " AND `status` = 1 AND `show` = 1 ), unqualified = ( SELECT count(*) FROM `ap_task_result` t1 WHERE task_id = " +task_id +
                        " AND `status` = 0 AND `show` = 1 ) \n" +
                        "WHERE\n" + " task_id = "+task_id;
                PreparedStatement pst = c.prepareStatement(sql);
                pst.execute();
                pst.close();
                c.close();
                return true;
            }
        } catch (SQLException e) {
            System.out.println( e.toString() +"连接失败===========");
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 插入数据
     * @param task_id
     * @param name
     * @param status
     * @return
     */
    public static boolean insert2ApTaskResult(String ipAddress, int task_id, String name, int status) {
        Connection c = null;

        String dbURL = "jdbc:mysql://" + ipAddress + ":3306/artifact?useSSL=false";

        try {
            Class.forName(DBDRIVER);
            System.out.println("驱动加载成功===============");
        } catch (Exception e) {
            System.out.println("驱动加载失败================");
        }

        try {
            c = DriverManager.getConnection(dbURL, DBUSER, DBPASSWORD);

            if (c != null ) {
                System.out.println( c +"连接成功===========");
                String sql;

                if (isInsert) {
                    sql = "INSERT INTO ap_task_result (task_id, name, status, `show`, create_time) VALUES (" + task_id + ", '" + name + "', " + status + ", 1, '" + getNowTime() + "')";
                } else {
                    sql = "UPDATE ap_task_result set status=" + status + " where task_id=" + task_id + " and name='" + name + "' and `show`=1";
                }

                if (insertCount == 2) {
                    insertCount = 1;
                    isInsert = false;
                } else {
                    insertCount++;
                }
                PreparedStatement pst = c.prepareStatement(sql);

                pst.execute();
                pst.close();
                c.close();
                return true;
            }
        } catch (SQLException e) {
            System.out.println( e.toString() +"连接失败===========");
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     */
    public static int getDBCount(String ipAddress) {
        Connection c = null;
        PreparedStatement pst = null;
        int count = 0;
        String dbURL = "jdbc:mysql://" + ipAddress + ":3306/artifact?useSSL=false";

        try {
            Class.forName(DBDRIVER);
            System.out.println("驱动加载成功===============");
        } catch (Exception e) {
            System.out.println("驱动加载失败================");
        }

        try {
            c = DriverManager.getConnection(dbURL, DBUSER, DBPASSWORD);

            if (c != null ) {
                System.out.println( c +"连接成功===========");
                String sql = "SELECT count(*) from ap_task;";

                pst = c.prepareStatement(sql);
                ResultSet rs = pst.executeQuery();// 返回结果集

                while (rs.next()) {
                    count = rs.getInt(1);
                }

                pst.close();
                c.close();
                return count;
            }
        } catch (SQLException e) {
            System.out.println( e.toString() +"连接失败===========");
            e.printStackTrace();
        } finally {
            try{
                pst.close();
                c.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return count;
    }
}
