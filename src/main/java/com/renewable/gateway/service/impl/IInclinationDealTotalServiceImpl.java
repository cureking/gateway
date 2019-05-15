package com.renewable.gateway.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.renewable.gateway.common.Const;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.InclinationDealedTotalMapper;
import com.renewable.gateway.pojo.InclinationDealedTotal;
import com.renewable.gateway.service.IInclinationDealTotalService;
import com.renewable.gateway.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iInclinationDealTotalServiceImpl")
public class IInclinationDealTotalServiceImpl implements IInclinationDealTotalService {

    @Autowired
    private InclinationDealedTotalMapper inclinationDealedTotalMapper;


    @Override
    public ServerResponse<PageInfo> getDataList(int pageNum, int pageSize, int sensor_identifier) {

        //pagehelper 使用逻辑:  第一步，startPage--start；第二步，填充自己的sql查询逻辑；第三步，pageHelper--收尾
        //第一步：startPage--start
        PageHelper.startPage(pageNum, pageSize);

        //第二步：填充自己的sql查询逻辑
        List<InclinationDealedTotal> inclinationDealedTotalList = inclinationDealedTotalMapper.selectList(sensor_identifier);
        //todo_finished 数据全部为空
        if (inclinationDealedTotalList == null) {
            return ServerResponse.createByErrorMessage("no data conform your requirement!");
        }

        List<InclinationDealedTotal> inclinationVoList = Lists.newArrayList();
        for (InclinationDealedTotal inclinationItem : inclinationDealedTotalList) {
            InclinationDealedTotal inclinationDealedTotalVo = assembleInclinationDealedTotalVo(inclinationItem);
            inclinationVoList.add(inclinationDealedTotalVo);
        }

        //第三步：pageHelper--收尾
        PageInfo pageResult = new PageInfo(inclinationDealedTotalList);
        pageResult.setList(inclinationVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    //日后有需要的话，可以将这里改为对应VO。
    private InclinationDealedTotal assembleInclinationDealedTotalVo(InclinationDealedTotal inclinationDealedTotal){
        return inclinationDealedTotal;
    }


    public ServerResponse<List<Object>> getDataListByTime(String startTime, String endTime, int sensor_identifier) {
        List<InclinationDealedTotal> inclinationList = inclinationDealedTotalMapper.selectListByTime(DateTimeUtil.strToDate(startTime), DateTimeUtil.strToDate(endTime), sensor_identifier);
        List<Object> inclinationVoObjectList = Lists.newArrayList();
        for (InclinationDealedTotal inclinationItem : inclinationList) {
            InclinationDealedTotal inclinationVo = assembleInclinationDealedTotalVo(inclinationItem);
            Object InclinationVoObject = (Object) inclinationVo;
            inclinationVoObjectList.add(InclinationVoObject);

        }
        return ServerResponse.createBySuccess(inclinationVoObjectList);
    }

    /**
     * 进行数据的上传
     * @return
     */
    @Override
    public ServerResponse uploadDataList() {
        List<InclinationDealedTotal> inclinationDealedTotalList = inclinationDealedTotalMapper.selectListByVersion("Cleaned");  //这里以后要集成的Const文件中，另外相关数据字段，应该改为数字（节省带宽，降低出错可能性（写代码））


        return null;
    }

}
