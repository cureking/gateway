package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.InitializationInclination;
import org.apache.ibatis.annotations.Param;

public interface InitializationInclinationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(InitializationInclination record);

    int insertSelective(InitializationInclination record);

    InitializationInclination selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(InitializationInclination record);

    int updateByPrimaryKey(InitializationInclination record);

    // custom
    InitializationInclination selectByTerminalIdAndSensorRegisterId(@Param("terminalId") int terminalId, @Param("sensorRegisterId") int sensorRegisterId);
}