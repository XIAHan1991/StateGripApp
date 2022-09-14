package com.example.zftidemo.dao;

import java.util.HashMap;
import java.util.Map;

public class ResultOrder {
    public static Map<String, Integer> resOrder = new HashMap<>();
    static {
        resOrder.put("JP柜离地高度(标准≥1.8m)", 0);
        resOrder.put("配变距地高度(标准≥3.4m)", 1);
        resOrder.put("槽钢与避雷器横担间距(标准1.8m±5cm)", 2);
        resOrder.put("避雷器横担与熔丝横担间距(标准1.0m±5cm)", 3);
        resOrder.put("熔丝横担与第二引线横担间距(标准1.8m±5cm)", 4);
        resOrder.put("第二引线横担与第一引线横担间距(标准1.8m±5cm)", 5);
        resOrder.put("第一引线横担与线路横担间距(标准1.8m±5cm)", 6);
        resOrder.put("熔断器本体轴线与地面垂线夹角(标准15°~30°)", 7);
        resOrder.put("变压器水平倾斜(标准≤跟开距离1%)", 8);
        resOrder.put("警示牌与变压器挂设高度一致", 9);
        resOrder.put("相位牌与变压器高压侧方向一致", 10);
        resOrder.put("杆号牌离地高度(标准3m)", 11);
        resOrder.put("抱箍配置", 12);
        resOrder.put("电杆埋地深度", 13);
        resOrder.put("接地引下线配置", 14);
        resOrder.put("扁铁配置", 15);
        resOrder.put("低压电缆配置", 16);
        resOrder.put("避雷器配置", 17);
        resOrder.put("接地环和熔丝上桩头间距", 18);
        resOrder.put("引下线与导线连接处由2个异形并购夹相连", 19);
        resOrder.put("导线引下线与熔丝具接头由黄绿红进行相位标注", 20);

    }
}
