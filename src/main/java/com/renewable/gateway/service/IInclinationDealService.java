package com.renewable.gateway.service;

import com.github.pagehelper.PageInfo;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.InclinationDealed;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
public interface IInclinationDealService {

    //对外
    ServerResponse<PageInfo> getInclinationDataList(int pageNum, int pageSize);

    ServerResponse<List<Object>> getInclinationDataListTime(String startTime, String endTime);

    //对内
    ServerResponse inclinationData2DB(InclinationDealed inclinationDealed);

}
