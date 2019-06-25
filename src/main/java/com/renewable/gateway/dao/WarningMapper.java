package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.Warning;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarningMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(Warning record);

	int insertSelective(Warning record);

	Warning selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Warning record);

	int updateByPrimaryKey(Warning record);

	// custom
	Long selectLastOringinId();

	int insertBatch(@Param("warningList") List<Warning> warningList);
}