package com.renewable.gateway.service.impl;

import com.google.common.collect.Lists;
import com.renewable.gateway.common.GuavaCache;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.constant.InitializationConstant;
import com.renewable.gateway.dao.InitializationInclinationMapper;
import com.renewable.gateway.pojo.InitializationInclination;
import com.renewable.gateway.rabbitmq.producer.InitializationProducer;
import com.renewable.gateway.service.IInitializationInclinationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.CacheConstant.TERMINAL_ID;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
@Service("iInitializationInclinationServiceImpl")
public class IInitializationInclinationServiceImpl implements IInitializationInclinationService {

	@Autowired
	private InitializationInclinationMapper initializationInclinationMapper;

	@Autowired
	private InitializationProducer initializationProducer;

	@Override
	public ServerResponse insert(InitializationInclination initializationInclination) {
		// 1.数据校验（部分情况下还有权限校验）
		if (initializationInclination == null) {
			return ServerResponse.createByErrorMessage("the initializationInclination is null !");
		}

		// 2.initializationInclinationAssemble
		InitializationInclination insertSensorRegister = initializationInclination; // 这里简化一下

		// 3.插入数据
		int countRow = initializationInclinationMapper.insertSelective(insertSensorRegister);
		if (countRow == 0) {
			return ServerResponse.createByErrorMessage("the initializationInclination insert fail !");
		}

		// 4.返回成功响应
		return ServerResponse.createBySuccessMessage("the initializationInclination insert success .");
	}

	@Override
	public ServerResponse update(InitializationInclination initializationInclination) {
		if (initializationInclination == null){
			return ServerResponse.createByErrorMessage("the initializationInclination is null !");
		}
		if (initializationInclination.getId() == null){
			return ServerResponse.createByErrorMessage("the id of initializationInclination is null !");
		}

		InitializationInclination initializationInclinationUpdate = this.InitializationInclinationAssemble(initializationInclination);

		int countRow = initializationInclinationMapper.updateByPrimaryKeySelective(initializationInclinationUpdate);
		if (countRow == 0){
			return ServerResponse.createByErrorMessage("initialization update fail !");
		}
		return ServerResponse.createBySuccessMessage("initialization update success .");
	}

	@Override
	public ServerResponse updateEnterprise(InitializationInclination initializationInclination) {
		// 更新这里日后可以添加事务管控，防止中控服务器与终端服务器数据不一致
		// 1.数据校验
		if (initializationInclination == null){
			return ServerResponse.createByErrorMessage("the initializationInclination is null !");
		}
		if (initializationInclination.getId() == null){
			return ServerResponse.createByErrorMessage("the id of initializationInclination is null !");
		}

		// 2.数据assemble
		InitializationInclination initializationInclinationUpdate = initializationInclination;

		// 3.发送消息至中控服务器MQ，以更新中控服务器数据		// 先发送远程的，因为中控服务器可能发送失败，那么就不可以完成本地操作，那样会造成数据不一致。	// 之后可以建立MQ生产者重发机制
		ServerResponse remoteUpdateResponse = this.sendInitializationInclination2MQ(initializationInclinationUpdate);

		// 4.更新本地数据
		ServerResponse localUpdateResponse = this.update(initializationInclinationUpdate);
		if (localUpdateResponse.isFail()){
			return localUpdateResponse;
		}

		// 5.返回成功响应
		return ServerResponse.createBySuccessMessage("initializationInclination has updated .");
	}


	@Override
	public ServerResponse getInitializationInclinationBySensorRegisterId(Integer sensorRegisterId) {
		if (sensorRegisterId == null){
			return ServerResponse.createByErrorMessage("the sensorRegister id is null !");
		}
		int terminalId = Integer.parseInt(GuavaCache.getKey(TERMINAL_ID));

		InitializationInclination initializationInclination = initializationInclinationMapper.selectByTerminalIdAndSensorRegisterId(terminalId, sensorRegisterId);
		if (initializationInclination == null) {
			return ServerResponse.createByErrorMessage("can't find the initializationInclination with the sensorRegisterId: " + sensorRegisterId + " and terminalId: " + terminalId);
		}

		return ServerResponse.createBySuccess(initializationInclination);
	}

	@Override
	public ServerResponse listInitializationInclination() {
		// 这里以后需要的话，可以做权限验证
		List<InitializationInclination> initializationInclinationList = null;
		initializationInclinationList = initializationInclinationMapper.listInitializationInclination();
		if (initializationInclinationList == null || initializationInclinationList.size() == 0){
			return ServerResponse.createByErrorMessage("the initializationInclination list is null !");
		}
		return ServerResponse.createBySuccess(initializationInclinationList);
	}

	@Override
	public ServerResponse receiveInitializationInclinationFromMQ(InitializationInclination initializationInclination) {
		// 1.校验数据
		if (initializationInclination == null) {
			return ServerResponse.createByErrorMessage("the initializationInclination is null !");
		}

		// 2.数据组装
		InitializationInclination insertInitializationInclination = initializationInclination;

		// 3.保存serialSensor至数据库
		// 有则更新，无则插入
		ServerResponse insertInitializationInclinationResponse = this.insertOrUpdateInitializationInclination(insertInitializationInclination);
		if (insertInitializationInclinationResponse.isFail()) {
			return insertInitializationInclinationResponse;
		}

		return ServerResponse.createBySuccessMessage("the initializationInclination has inserted .");
	}

	@Override
	public ServerResponse getInitLimitBySelectiveSensorRegisterId(Integer sensorRegisterId) {
		// 1.数据校验	// 允许sensorRegisterId为null，那就获取第一个数据（但第一个数据的目标字段可能为空）
//		if (sensorRegisterId == null){
//			return ServerResponse.createByErrorMessage("sensorRegisterId is null !");
//		}

		// 2.查询目标数据
		Double initLimit = initializationInclinationMapper.getInitLimit(sensorRegisterId);
		if (initLimit == null){
			return ServerResponse.createByErrorMessage("the initLimit is null with the sensorRegisterId: "+sensorRegisterId);
		}

		// 3.返回正确响应
		return ServerResponse.createBySuccess(initLimit);
	}

	/**
	 * 根据InitializationInclination其他字段，判断status应该如何设置
	 */
	private InitializationInclination InitializationInclinationAssemble(InitializationInclination initializationInclination){
		InitializationInclination initializationInclinationAssemble = new InitializationInclination();
		initializationInclinationAssemble.setId(initializationInclination.getId());
		initializationInclinationAssemble.setSensorRegisterId(initializationInclination.getSensorRegisterId());
		initializationInclinationAssemble.setTerminalId(initializationInclination.getTerminalId());

		initializationInclinationAssemble.setRadius(initializationInclination.getRadius());
		initializationInclinationAssemble.setInitH1(initializationInclination.getInitH1());
		initializationInclinationAssemble.setInitAngle1(initializationInclination.getInitAngle1());
		initializationInclinationAssemble.setInitH2(initializationInclination.getInitH2());
		initializationInclinationAssemble.setInitAngle2(initializationInclination.getInitAngle2());
		initializationInclinationAssemble.setInitH3(initializationInclination.getInitH3());
		initializationInclinationAssemble.setInitAngle3(initializationInclination.getInitAngle3());
		initializationInclinationAssemble.setInitH4(initializationInclination.getInitH4());
		initializationInclinationAssemble.setInitAngle4(initializationInclination.getInitAngle4());

		initializationInclinationAssemble.setInitTotalAngle(initializationInclination.getInitTotalAngle());
		initializationInclinationAssemble.setInitDirectAngle(initializationInclination.getInitDirectAngle());
		initializationInclinationAssemble.setTotalAngleLimit(initializationInclination.getTotalAngleLimit());
		initializationInclinationAssemble.setTotalInitAngleLimit(initializationInclination.getTotalInitAngleLimit());
		initializationInclinationAssemble.setInitX(initializationInclination.getInitX());
		initializationInclinationAssemble.setInitY(initializationInclination.getInitY());

		initializationInclinationAssemble.setStatus(InitializationConstant.InitializationStatusEnum.USE.getCode());
		if (initializationInclination.getRadius() == null ||
				initializationInclination.getInitH1() == null || initializationInclination.getInitAngle1() == null || initializationInclination.getInitH2() == null || initializationInclination.getInitAngle2() == null ||
				initializationInclination.getInitH3() == null || initializationInclination.getInitAngle3() == null || initializationInclination.getInitH4() == null || initializationInclination.getInitAngle4() == null ||
				initializationInclination.getInitTotalAngle() == null || initializationInclination.getInitDirectAngle() == null || initializationInclination.getTotalAngleLimit() == null || initializationInclination.getTotalInitAngleLimit() == null ||
				initializationInclination.getInitX() == null || initializationInclination.getInitY() == null){
			initializationInclinationAssemble.setStatus(InitializationConstant.InitializationStatusEnum.NEED_COMPLETE.getCode());
		}

		initializationInclinationAssemble.setCreateTime(new Date());
		initializationInclinationAssemble.setUpdateTime(new Date());

		return initializationInclinationAssemble;
	}

	private ServerResponse insertOrUpdateInitializationInclination(InitializationInclination initializationInclination){
		if (initializationInclination == null){
			return ServerResponse.createByErrorMessage("the initializationInclination is null !");
		}

		InitializationInclination initializationInclinationInsertOrUpdate = initializationInclination;

		int countRow = initializationInclinationMapper.insertOrUpdateByKey(initializationInclinationInsertOrUpdate);
		if (countRow == 0){
			return ServerResponse.createByErrorMessage("the initializationInclination insert or update fail !");
		}

		return ServerResponse.createBySuccessMessage("the initializationInclination insert or update success .");
	}

	private ServerResponse sendInitializationInclination2MQ(List<InitializationInclination> initializationInclinationList){
		// 1.数据校验
		if (initializationInclinationList == null || initializationInclinationList.size() == 0){
			return ServerResponse.createByErrorMessage("the initializationInclination list is null or its size is zero !");
		}

		// 2.数据Assemble简化处理
		List<InitializationInclination> initializationInclinationUploadeList = initializationInclinationList;

		// 3.发送数据
		try {
			initializationProducer.sendInitializationInclination(initializationInclinationUploadeList);
		} catch (IOException e) {
			log.error("IOException:{}",e);
			return ServerResponse.createByErrorMessage("IOException: "+e.toString());
		} catch (TimeoutException e) {
			log.error("TimeoutException:{}",e);
			return ServerResponse.createByErrorMessage("IOException: "+e.toString());
		} catch (InterruptedException e) {
			log.error("InterruptedException:{}",e);
			return ServerResponse.createByErrorMessage("IOException: "+e.toString());
		}

		return ServerResponse.createBySuccessMessage("the initializationInclination has sended to MQ .");
	}

	private ServerResponse sendInitializationInclination2MQ(InitializationInclination initializationInclination){
		// 1.数据校验
		if (initializationInclination == null){
			return ServerResponse.createByErrorMessage("the initializationInclination is null !");
		}

		// 2.数据assemble简化
		List<InitializationInclination> initializationInclinationList = Lists.newArrayList();
		initializationInclinationList.add(initializationInclination);

		return this.sendInitializationInclination2MQ(initializationInclinationList);
	}

}
