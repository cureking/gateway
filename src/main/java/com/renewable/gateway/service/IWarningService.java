package com.renewable.gateway.service;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.Warning;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
public interface IWarningService {

	ServerResponse insertWarning(Warning warning);

	ServerResponse insertWarningList(List<Warning> warningList);

	ServerResponse stateCheck();

	ServerResponse inclinationInitCheck();
}
