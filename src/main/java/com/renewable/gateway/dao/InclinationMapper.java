package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.Inclination;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface InclinationMapper {
	int deleteByPrimaryKey(Long id);

	int insert(Inclination record);

	int insertSelective(Inclination record);

	Inclination selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(Inclination record);

	int updateByPrimaryKey(Inclination record);

	//custom    pageHelper
	List<Inclination> selectList();

	List<Inclination> selectListByTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	Inclination selectNextByPrimaryKey(Long id);

	Inclination selectNewByPrimaryKey();

	Inclination selectPeakByIdArea(@Param("startId") long startId, @Param("endId") long endId);

	Inclination selcetPeakByTimeArea(@Param("sensorId") int sensorId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

	long selectLastIdbyTime(Date lastTime);
}