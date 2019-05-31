package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.InclinationRegister;

/**
 * @Description：
 * @Author: jarry
 */

@Deprecated
public interface IInclinationRegisteredService {

    ServerResponse insertRegisteredInfo(InclinationRegister inclinationRegister);
}
