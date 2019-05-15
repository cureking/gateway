package com.renewable.gateway.service;

import com.github.pagehelper.PageInfo;
import com.renewable.gateway.common.ServerResponse;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
public interface IInclinationDealTotalService {

    //对外
    ServerResponse<PageInfo> getDataList(int pageNum, int pageSize, int sensor_identifier);

    ServerResponse<List<Object>> getDataListByTime(String startTime, String endTime, int sensor_identifier);


    //对内    定时任务调用
    ServerResponse uploadDataList();
}
