package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.WarningEliminate;

public interface WarningEliminateMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(WarningEliminate record);

	int insertSelective(WarningEliminate record);

	WarningEliminate selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(WarningEliminate record);

	int updateByPrimaryKey(WarningEliminate record);
}