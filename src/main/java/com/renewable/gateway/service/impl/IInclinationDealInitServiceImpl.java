package com.renewable.gateway.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.InclinationDealedInitMapper;
import com.renewable.gateway.pojo.InclinationDealedInit;
import com.renewable.gateway.service.IInclinationDealInitService;
import com.renewable.gateway.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iInclinationDealInitServiceImpl")
public class IInclinationDealInitServiceImpl implements IInclinationDealInitService {


    @Autowired
    private InclinationDealedInitMapper inclinationDealedInitMapper;


    @Override
    public ServerResponse<PageInfo> getDataList(int pageNum, int pageSize, int sensor_identifier) {

        //pagehelper 使用逻辑:  第一步，startPage--start；第二步，填充自己的sql查询逻辑；第三步，pageHelper--收尾
        //第一步：startPage--start
        PageHelper.startPage(pageNum, pageSize);

        //第二步：填充自己的sql查询逻辑
        List<InclinationDealedInit> inclinationDealedInitList = inclinationDealedInitMapper.selectList(sensor_identifier);
        //todo_finished 数据全部为空
        if (inclinationDealedInitList == null) {
            return ServerResponse.createByErrorMessage("no data conform your requirement!");
        }

        List<InclinationDealedInit> inclinationVoList = Lists.newArrayList();
        for (InclinationDealedInit inclinationItem : inclinationDealedInitList) {
            InclinationDealedInit inclinationDealedTotalVo = assembleInclinationDealedInitVo(inclinationItem);
            inclinationVoList.add(inclinationDealedTotalVo);
        }

        //第三步：pageHelper--收尾
        PageInfo pageResult = new PageInfo(inclinationDealedInitList);
        pageResult.setList(inclinationVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    //日后有需要的话，可以将这里改为对应VO。
    private InclinationDealedInit assembleInclinationDealedInitVo(InclinationDealedInit inclinationDealedTotal){
        return inclinationDealedTotal;
    }


    public ServerResponse<List<Object>> getDataListByTime(String startTime, String endTime, int sensor_identifier) {
        List<InclinationDealedInit> inclinationList = inclinationDealedInitMapper.selectListByTime(DateTimeUtil.strToDate(startTime), DateTimeUtil.strToDate(endTime), sensor_identifier);
        List<Object> inclinationVoObjectList = Lists.newArrayList();
        for (InclinationDealedInit inclinationItem : inclinationList) {
            InclinationDealedInit inclinationVo = assembleInclinationDealedInitVo(inclinationItem);
            Object InclinationVoObject = (Object) inclinationVo;
            inclinationVoObjectList.add(InclinationVoObject);

        }
        return ServerResponse.createBySuccess(inclinationVoObjectList);
    }
}
