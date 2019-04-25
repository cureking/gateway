package com.renewable.gateway.service;

import com.github.pagehelper.PageInfo;
import com.renewable.gateway.common.ServerResponse;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
public interface ISensorDataService {


    //对外
    ServerResponse<PageInfo> getDataList(int pageNum, int pageSize, int sensor_identifier);

    ServerResponse<List<Object>> getDataListByTime(String startTime, String endTime, int sensor_id);


    //对内部底层开放
    //数据真的适合作为参数传入嘛。在设计理念上存在一定不合理，并且将数据暴露到了更高一层。
//    ServerResponse sendData(Object data,int sensor_id);


    //简单试行，先完成倾斜传感器数的问答接收
    void sendData(int sensor_id, String port, String address, String baudrate);


    //数据清洗
    ServerResponse cleanDataTasks();


}
