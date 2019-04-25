package com.renewable.gateway.dao;

import com.renewable.gateway.pojo.InclinationDealed;

public interface InclinationDealedMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InclinationDealed record);

    int insertSelective(InclinationDealed record);

    InclinationDealed selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InclinationDealed record);

    int updateByPrimaryKey(InclinationDealed record);
}