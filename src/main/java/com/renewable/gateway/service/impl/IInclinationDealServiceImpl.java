package com.renewable.gateway.service.impl;

import com.github.pagehelper.PageInfo;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.InclinationDealedMapper;
import com.renewable.gateway.pojo.InclinationDealed;
import com.renewable.gateway.service.IInclinationDealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iInclinationDealedServiceImpl")
public class IInclinationDealServiceImpl implements IInclinationDealService {

    @Autowired
    private InclinationDealedMapper inclinationDealedMapper;


    @Override
    public ServerResponse<PageInfo> getInclinationDataList(int pageNum, int pageSize) {
        return null;
    }

    @Override
    public ServerResponse<List<Object>> getInclinationDataListTime(String startTime, String endTime) {
        return null;
    }

    @Override
    public ServerResponse inclinationData2DB(InclinationDealed inclinationDealed) {
        System.out.println("angle_X:" + inclinationDealed.getAngleX() + "  angle_Y:" + inclinationDealed.getAngleY() + "  temperature:" + inclinationDealed.getTemperature());

        int resutlt = inclinationDealedMapper.insertSelective(inclinationDealed);
        if (resutlt == 0) {
            return ServerResponse.createByErrorMessage("insert inclinationDealData failure!");
        }
        return ServerResponse.createBySuccessMessage("insert inclinationDealData success");
    }
}
