package com.renewable.gateway.service;

import com.github.pagehelper.PageInfo;
import com.renewable.gateway.common.ServerResponse;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
@Deprecated
public interface ISensorDataService {


    //对外
    @Deprecated
    ServerResponse<PageInfo> getDataList(int pageNum, int pageSize, int sensor_identifier);

    @Deprecated
    ServerResponse<List<Object>> getDataListByTime(String startTime, String endTime, int sensor_id);

    //简单试行，先完成倾斜传感器数的问答接收
    @Deprecated
    void sendData(int sensor_id, String port, String address, String baudrate);


    //数据清洗
    @Deprecated
    ServerResponse cleanDataTasks();


}
