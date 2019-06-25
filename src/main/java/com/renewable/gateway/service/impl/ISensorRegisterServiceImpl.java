package com.renewable.gateway.service.impl;

import com.google.common.collect.Lists;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.SensorRegisterMapper;
import com.renewable.gateway.pojo.SensorRegister;
import com.renewable.gateway.rabbitmq.producer.SensorRegisterProducer;
import com.renewable.gateway.service.ISensorRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
@Service("iSensorRegisterServiceImpl")
public class ISensorRegisterServiceImpl implements ISensorRegisterService {

	@Autowired
	private SensorRegisterMapper sensorRegisterMapper;

	@Autowired
	private SensorRegisterProducer sensorRegisterProducer;

	@Override
	public ServerResponse insert(SensorRegister sensorRegister) {
		// 1.数据校验（部分情况下还有权限校验）
		if (sensorRegister == null) {
			return ServerResponse.createByErrorMessage("the sensorRegister is null !");
		}

		// 2.serialSensorAssemble
		SensorRegister insertSensorRegister = sensorRegister; // 这里简化一下

		// 3.插入数据
		int countRow = sensorRegisterMapper.insertSelective(insertSensorRegister);
		if (countRow == 0) {
			return ServerResponse.createByErrorMessage("the sensorRegister insert fail !");
		}

		// 4.返回成功响应
		return ServerResponse.createBySuccessMessage("the sensorRegister insert success .");
	}

	public ServerResponse insertOrUpdateBy(SensorRegister sensorRegister) {
		// 数据校验
		if (sensorRegister == null) {
			return ServerResponse.createByErrorMessage("sensorRegister is null !");
		}

		// 2.sensorRegisterAssemble
		SensorRegister insertOrUpdateSensorRegister = sensorRegister;

		// 3.插入/更新数据    // 貌似由于数据库版本问题不支ON DUPLICATE KEY UPDATE （之后细究）
		        int countRow = sensorRegisterMapper.insertOrUpdate(sensorRegister);
        if (countRow == 0){
            return ServerResponse.createByErrorMessage("the sensorRegister insert/update fail !");
        }

        // 已经对上述代码的mapper进行了更新，尚未测试
//		SensorRegister existSensorRegister = sensorRegisterMapper.selectByPrimaryKey(sensorRegister.getId());
//		if (existSensorRegister == null) {
//			int countRow = sensorRegisterMapper.insertSelective(sensorRegister);
//			if (countRow == 0) {
//				return ServerResponse.createByErrorMessage("the sensorRegister insert fail !");
//			}
//		} else {
//			// 说明数据原来就存在，那么就不需要更改create_time()
//			sensorRegister.setCreateTime(null);
//			int countRow = sensorRegisterMapper.updateByPrimaryKeySelective(sensorRegister);
//			if (countRow == 0) {
//				return ServerResponse.createByErrorMessage("the sensorRegister update fail !");
//			}
//		}

		// 4.返回成功响应
		return ServerResponse.createBySuccessMessage("the sensorRegister insert/update success .");
	}

	@Override
	public ServerResponse receiveSensorRegisterFromMQ(SensorRegister sensorRegister) {
		// 1.校验数据
		if (sensorRegister == null) {
			return ServerResponse.createByErrorMessage("the sensorRegister is null !");
		}

		// 2.数据组装
		SensorRegister insertSensorRegister = sensorRegister;

		// 3.保存serialSensor至数据库
		ServerResponse insertSerialSensorResponse = this.insertOrUpdateBy(insertSensorRegister);
		if (insertSerialSensorResponse.isFail()) {
			return insertSerialSensorResponse;
		}

		return ServerResponse.createBySuccessMessage("the sensorRegister has inserted .");
	}

	@Override
	public ServerResponse getSensorRegisterById(Integer sensorRegisterId) {
		if (sensorRegisterId == null){
			return ServerResponse.createByErrorMessage("the id of sensorRegister is null !");
		}

		SensorRegister sensorRegister = null;
		sensorRegister = sensorRegisterMapper.selectByPrimaryKey(sensorRegisterId);
		if (sensorRegister == null){
			return ServerResponse.createByErrorMessage("the sensor is not exist with the id: "+sensorRegisterId);
		}

		return ServerResponse.createBySuccess(sensorRegister);
	}

	@Override
	public ServerResponse listSensorRegister() {
		List<SensorRegister> sensorRegisterList = null;
		sensorRegisterList = sensorRegisterMapper.listSensorRegister();
		if (sensorRegisterList == null || sensorRegisterList.size() == 0){
			return ServerResponse.createByErrorMessage("the list of sensorRegister is null !");
		}
		return ServerResponse.createBySuccess(sensorRegisterList);
	}

	@Override
	public ServerResponse update(SensorRegister sensorRegister) {
		if (sensorRegister == null){
			return ServerResponse.createByErrorMessage("the sensorRegister is null !");
		}
		if (sensorRegister.getId() == null){
			return ServerResponse.createByErrorMessage("the id of the sensorRegister is null !");
		}

		SensorRegister sensorRegisterUpdate = sensorRegister;
		int countRow = sensorRegisterMapper.updateByPrimaryKeySelective(sensorRegisterUpdate);
		if (countRow == 0){
			return ServerResponse.createByErrorMessage("the sensorRegister update fail !");
		}

		return ServerResponse.createBySuccessMessage("the sensorRegister update success .");
	}

	@Override
	public ServerResponse updateEnterprise(SensorRegister sensorRegister) {
		// 更新这里日后可以添加事务管控，防止中控服务器与终端服务器数据不一致
		// 1.数据校验
		if (sensorRegister == null){
			return ServerResponse.createByErrorMessage("the sensorRegister is null !");
		}
		if (sensorRegister.getId() == null){
			return ServerResponse.createByErrorMessage("the id of sensorRegister is null !");
		}

		// 2.数据assemble
		SensorRegister sensorRegisterUpdate = sensorRegister;

		// 3.发送消息至中控服务器MQ，以更新中控服务器数据		// 先发送远程的，因为中控服务器可能发送失败，那么就不可以完成本地操作，那样会造成数据不一致。	// 之后可以建立MQ生产者重发机制
		ServerResponse remoteUpdateResponse = this.sendSensorRegister2MQ(sensorRegisterUpdate);

		// 4.更新本地数据
		ServerResponse localUpdateResponse = this.update(sensorRegisterUpdate);
		if (localUpdateResponse.isFail()){
			return localUpdateResponse;
		}

		// 5.返回成功响应
		return ServerResponse.createBySuccessMessage("sensorRegister has updated .");
	}

	private ServerResponse sendSensorRegister2MQ(List<SensorRegister> sensorRegisterList){
		// 1.数据校验
		if (sensorRegisterList == null || sensorRegisterList.size() == 0){
			return ServerResponse.createByErrorMessage("the sensorRegister list is null or its size is zero !");
		}

		// 2.数据Assemble简化处理
		List<SensorRegister> sensorRegisterUploadeList = sensorRegisterList;

		// 3.发送数据
		try {
			sensorRegisterProducer.sendSensorRegister(sensorRegisterUploadeList);
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

		return ServerResponse.createBySuccessMessage("the sensorRegisterUploadeList has sended to MQ .");
	}

	private ServerResponse sendSensorRegister2MQ(SensorRegister sensorRegister){
		// 1.数据校验
		if (sensorRegister == null){
			return ServerResponse.createByErrorMessage("the sensorRegister is null !");
		}

		// 2.数据assemble简化
		List<SensorRegister> sensorRegisterList = Lists.newArrayList();
		sensorRegisterList.add(sensorRegister);

		return this.sendSensorRegister2MQ(sensorRegisterList);
	}
}
