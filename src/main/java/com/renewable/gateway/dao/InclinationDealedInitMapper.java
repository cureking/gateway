package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.InclinationDealedInit;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface InclinationDealedInitMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InclinationDealedInit record);

    int insertSelective(InclinationDealedInit record);

    InclinationDealedInit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InclinationDealedInit record);

    int updateByPrimaryKey(InclinationDealedInit record);

    //custom
    List<InclinationDealedInit> selectList(int sensor_identifier);

    List<InclinationDealedInit> selectListByVersionAndLimit(@Param("version")String version, @Param("limit") int limit);

    List<InclinationDealedInit> selectListByTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("sensor_identifier") int sensor_identifier);

    int updateBatch(@Param(value = "inclinationDealedInitList")List<InclinationDealedInit> inclinationDealedInitList);
}