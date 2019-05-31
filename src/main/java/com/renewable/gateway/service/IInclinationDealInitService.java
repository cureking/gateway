package com.renewable.gateway.service;

import com.github.pagehelper.PageInfo;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.InclinationDealedInit;

import java.time.Duration;
import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
public interface IInclinationDealInitService {

    //对外
    ServerResponse<PageInfo> getDataList(int pageNum, int pageSize, int sensor_identifier);

    ServerResponse<List<Object>> getDataListByTime(String startTime, String endTime, int sensor_identifier);

    //对内    定时任务调用
    ServerResponse uploadDataList();

    // 对内   用于警报服务获取异常数据
    ServerResponse<List<InclinationDealedInit>> listCheckedData(double limit, long duration, long lastOriginId, int countLimit);
}
