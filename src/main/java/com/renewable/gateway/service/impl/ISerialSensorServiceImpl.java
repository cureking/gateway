package com.renewable.gateway.service.impl;

import com.google.common.collect.Lists;
import com.renewable.gateway.common.GuavaCache;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.dao.SerialSensorMapper;
import com.renewable.gateway.exception.serial.*;
import com.renewable.gateway.extend.serial.SerialPool;
import com.renewable.gateway.extend.serial.sensor.InclinationDeal526T;
import com.renewable.gateway.extend.serial.sensor.InclinationDeal826T;
import com.renewable.gateway.pojo.SerialSensor;
import com.renewable.gateway.rabbitmq.producer.SerialSensorProducer;
import com.renewable.gateway.service.ISerialSensorService;
import com.renewable.gateway.util.SerialPortUtil;
import com.sun.org.apache.regexp.internal.RE;
import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.CacheConstant.TERMINAL_ID;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
@Service("iSerialSensorServiceImpl")
public class ISerialSensorServiceImpl implements ISerialSensorService {

	@Autowired
	private SerialSensorMapper serialSensorMapper;

	@Autowired
	private SerialSensorProducer serialSensorProducer;

	@Autowired
	private SerialPool serialPool;

	@Override
	public ServerResponse insert(SerialSensor serialSensor) {
		// 1.数据校验（部分情况下还有权限校验）
		if (serialSensor == null || serialSensor.getPort() == null) {
			return ServerResponse.createByErrorMessage("the serialSensor is null or the port of the serialSensor is null !");
		}

		// 2.serialSensorAssemble
		SerialSensor insertSerialSensor = serialSensor; // 这里简化一下

		// 3.插入数据
		int countRow = serialSensorMapper.insertSelective(insertSerialSensor);
		if (countRow == 0) {
			return ServerResponse.createByErrorMessage("the serialSensor insert fail !");
		}

		// 4.返回成功响应
		return ServerResponse.createBySuccessMessage("the serialSensor insert success .");
	}

	@Override
	public ServerResponse insertBatch(List<SerialSensor> serialSensorList) {
		// 1.数据校验（部分情况下还有权限校验）
		if (serialSensorList == null || serialSensorList.size() == 0) {
			return ServerResponse.createByErrorMessage("the serialSensorList is null or the size of the serialSensorList is zero !");
		}

		// 2.serialSensorAssemble
		List<SerialSensor> insertSerialSensorList = serialSensorList;// 这里简化一下
		if (insertSerialSensorList == null || insertSerialSensorList.size() == 0) {
			return ServerResponse.createByErrorMessage("serialList assemble fail ! the serialList is :" + insertSerialSensorList.toString());
		}

		// 3.插入数据
		int countRow = serialSensorMapper.insertBatch(insertSerialSensorList);
		if (countRow == 0) {
			return ServerResponse.createByErrorMessage("the serialSensor insert fail !");
		}

		// 4.返回成功响应
		return ServerResponse.createBySuccessMessage("the serialSensor insert success .");
	}

	@Override
	public ServerResponse update(SerialSensor serialSensor) {
		// 1.数据校验（部分情况下还有权限校验）
		if (serialSensor == null || serialSensor.getPort() == null) {
			return ServerResponse.createByErrorMessage("the serialSensor is null or the port of the serialSensor is null !");
		}

		// 2.serialSensorAssemble
		SerialSensor insertSerialSensor = serialSensor; // 这里简化一下

		// 3.插入数据
		int countRow = serialSensorMapper.updateByPrimaryKeySelective(insertSerialSensor);
		if (countRow == 0) {
			return ServerResponse.createByErrorMessage("the serialSensor update fail !");
		}

		// 4.返回成功响应
		return ServerResponse.createBySuccessMessage("the serialSensor update success .");
	}

	@Override
	public ServerResponse updateByUser(SerialSensor serialSensor) {
		// 1.数据校验
		if (serialSensor == null){
			return ServerResponse.createByErrorMessage("the serialSensor si null !");
		}

		// 2.数据插入	// 由于下面的原因，放弃插入，由消费者流程执行插入操作
		// 3.数据发往中控室	// 其实只需要把数据发往中控室即可，中控室在更新数据后，会将新的数据返回，从而更细终端配置
		List<SerialSensor> serialSensorList = Lists.newArrayList();
		serialSensorList.add(serialSensor);

		ServerResponse sendResponse = this.sendSerialSensor2MQ(serialSensorList);
		if (sendResponse.isFail()){
			return sendResponse;
		}

		return ServerResponse.createBySuccessMessage("serial has send to centcontrol .");
	}

//    public ServerResponse updateBatch(List<SerialSensor> serialSensorList){
//        // 1.数据校验（部分情况下还有权限校验）
//        if (serialSensorList == null || serialSensorList.size() == 0){
//            return ServerResponse.createByErrorMessage("the serialSensorList is null or the size of the serialSensorList is zero !");
//        }
//
//        // 2.serialSensorAssemble
//        List<SerialSensor> insertSerialSensorList = serialSensorList;// 这里简化一下
//        if (insertSerialSensorList == null || insertSerialSensorList.size() == 0){
//            return ServerResponse.createByErrorMessage("serialList assemble fail ! the serialList is :"+ insertSerialSensorList.toString());
//        }
//
//        // 3.插入数据
//        int countRow = serialSensorMapper.insertBatch(insertSerialSensorList);
//        if (countRow == 0){
//            return ServerResponse.createByErrorMessage("the serialSensor insert fail !");
//        }
//
//        // 4.返回成功响应
//        return ServerResponse.createBySuccessMessage("the serialSensor insert success .");
//    }

	@Override
	public ServerResponse refresh() {

		// 1.获取串口通信列表serialSensorList
		List<String> portNameList = SerialPortUtil.findPort();
		if (portNameList == null || portNameList.size() == 0) {
			return ServerResponse.createByErrorMessage("the portNameList is null or the size of the portNameList is zero !");
		}

		// 2.组装serialSensorList中的serialSensor（除了port外，其他设置为默认值即可，另外不设置ID）
		List<SerialSensor> serialSensorList = this.serialSensorListGeneratorByPort(portNameList);

		// 3-.将已经注册的传感器从列表中删除   // 从数据库校验
		List<SerialSensor> uploadSerialSensorList = this.serialSensorFilterByExistInDB(serialSensorList);
		if (uploadSerialSensorList == null || uploadSerialSensorList.size() == 0) {
			return ServerResponse.createBySuccessMessage("there is no new serialSensor !");
		}

		// 3.（修正）信息发往中控室
		ServerResponse sendResponse = this.sendSerialSensor2MQ(uploadSerialSensorList);
		if (sendResponse.isFail()){
			return sendResponse;
		}

		// 3.保存serialSensorList至缓存  // 后来这个模型改了，必须将注册信息发送至中控室，由中控室分陪全局ID与其他注册信息，如sensorRegister.etc
		// 4.保存serialSensorList至数据库
		// 5.发送serialSensorList至中控室
		return ServerResponse.createBySuccessMessage("the serialSensor config has uploaded to centControl .");
	}

	@Override
	public ServerResponse sendSerialSensor2MQ(List<SerialSensor> serialSensorList) {
		if (serialSensorList == null || serialSensorList.size() == 0){
			return ServerResponse.createByErrorMessage("the serialSensorList is null !");
		}
		try {
			serialSensorProducer.sendSerialSensor(serialSensorList);
		} catch (IOException e) {
			log.error("IOException:{}", e);
			return ServerResponse.createByErrorMessage("IOException:" + e.toString());
		} catch (TimeoutException e) {
			log.error("TimeoutException:{}", e);
			return ServerResponse.createByErrorMessage("TimeoutException:" + e.toString());
		} catch (InterruptedException e) {
			log.error("InterruptedException:{}", e);
			return ServerResponse.createByErrorMessage("InterruptedException:" + e.toString());
		}
		return ServerResponse.createBySuccessMessage("serial has sended .");
	}

	@Override
	public ServerResponse receiveSerialSensorFromMQ(SerialSensor serialSensor) {
		// 1.校验数据
		if (serialSensor == null) {
			return ServerResponse.createByErrorMessage("the serialSensor is null !");
		}

		// 2.数据组装
		SerialSensor insertSerialSensor = serialSensor;

		// 3.保存serialSensor至数据库
		// 有则更新，无则插入
		ServerResponse insertOrUpdateSerialSensorResponse = this.insertOrUpdateSerialSensor(insertSerialSensor);
		if (insertOrUpdateSerialSensorResponse.isFail()) {
			return insertOrUpdateSerialSensorResponse;
		}

		return ServerResponse.createBySuccessMessage("the serialSensor has inserted .");
	}

	@Override
	public ServerResponse taskLoadFromSerialSensor() {
		//TODO 现在先从数据库中获取吧。之后再建立缓存。（毕竟缓存需要建立数据一致）

		ServerResponse<List<SerialSensor>> loadSerialSensorListResponse = this.list();
		if (loadSerialSensorListResponse == null){
			return ServerResponse.createByErrorMessage("the loadSerialSensorListResponse is null !");
		}
		List<SerialSensor> serialSensorList = loadSerialSensorListResponse.getData();

		// 在想，下列的数据校验应该交给单元自己处理，会比较好
		if (serialSensorList == null || serialSensorList.size() == 0){
			return ServerResponse.createByErrorMessage("the serialSensorList is null or its size is zero !");
		}

		ServerResponse loadSerialSensorDataFromResponse = this.loadFromSerialSensorByList(serialSensorList);
		if (loadSerialSensorDataFromResponse.isFail()){
			return loadSerialSensorDataFromResponse;
		}

		return ServerResponse.createBySuccessMessage("load serialSensor success .");
	}



	@Override
	public ServerResponse loadFromSerialSensorByList(List<SerialSensor> serialSensorList) {
		// 1.数据校验
		if (serialSensorList == null || serialSensorList.size() == 0){
			return ServerResponse.createByErrorMessage("the serialSensorList is null or its size is zero !");
		}

		// 2.对列表中每个数据进行loadFromSerialSensorByItem方法
		for (SerialSensor serialSensor : serialSensorList) {
			ServerResponse loadFromSerialSensorByItemServerResponse = this.loadFromSerialSensorByItem(serialSensor);
			if (loadFromSerialSensorByItemServerResponse.isFail()){
				log.error("error:{}", loadFromSerialSensorByItemServerResponse);
				continue;
			}
		}

		// 3.返回成功响应
		return ServerResponse.createBySuccessMessage("loaded data by serial sensor list .");
	}

	private ServerResponse loadFromSerialSensorByItem(SerialSensor serialSensor){
		// 1.数据校验
		if (serialSensor == null){
			return ServerResponse.createBySuccessMessage("the serial sensor is null !");
		}

		// 2.调用服务，请求数据
		ServerResponse response = this.commandAssemble(serialSensor);
		if (response.isFail()){
			return response;
		}

		// 4.向传感器发送命令
		byte[] originArray = (byte[])response.getData();
		ServerResponse sendResponse = this.sendCommand(serialSensor, originArray);

		if (sendResponse.isFail()){
			return sendResponse;
		}
		// 3.返回成功响应
		return ServerResponse.createBySuccess("command has sended to hardware .");
	}

	/**
	 * 组装serial对应的命令，需要判断采用什么命令协议，这里可以先硬编码526T	// 最佳办法修改数据表结构，MD，用数据长度作为标识，不早说。。。
	 * @param serialSensor
	 * @return
	 */
	private ServerResponse<byte[]> commandAssemble(SerialSensor serialSensor){
		String address = serialSensor.getAddress();
		String dataLength = serialSensor.getMark();
		if (dataLength == null || dataLength == "Init" || dataLength.contains("T")){
			return ServerResponse.createByErrorMessage("该传感器尚未配置成功 !");
		}
		Integer dataArrayLength = Integer.parseInt(dataLength);
		if (InclinationConst.InclinationSerialTypeEnum.T826.getCode() == dataArrayLength){
			return InclinationDeal826T.command2Origin(Integer.parseInt(address), InclinationConst.InclinationSensor1Enum.READALL);
		}
		if (InclinationConst.InclinationSerialTypeEnum.T526.getCode() == dataArrayLength){
			return InclinationDeal526T.command2Origin(Integer.parseInt(address), InclinationConst.InclinationSensor1Enum.READALL);
		}
		return ServerResponse.createByErrorMessage("传感器配置错误 ! the dataLength:"+dataLength);
	}

	private ServerResponse sendCommand(SerialSensor serialSensor, byte[] originArray){
			String port = serialSensor.getPort();

			ServerResponse response = serialPool.sendData(port, originArray);
//		 	ServerResponse response = this.sendCommand2SerialSensor(serialSensor, originArray);

			return response;
	}

	private ServerResponse sendCommand2SerialSensor(SerialSensor serialSensor, byte[] originArray){
		String port = serialSensor.getPort();
		Integer baudarte = serialSensor.getBaudrate();
		ArrayList<String> test = SerialPortUtil.findPort();

		try {
			SerialPort serialPort = SerialPortUtil.openPort(port, baudarte);
			try {
				SerialPortUtil.sendToPort(serialPort,originArray);
			} catch (SendDataToSerialPortFailure sendDataToSerialPortFailure) {
				sendDataToSerialPortFailure.printStackTrace();
			} catch (SerialPortOutputStreamCloseFailure serialPortOutputStreamCloseFailure) {
				serialPortOutputStreamCloseFailure.printStackTrace();
			}
		} catch (SerialPortParameterFailure serialPortParameterFailure) {
			serialPortParameterFailure.printStackTrace();
		} catch (NotASerialPort notASerialPort) {
			notASerialPort.printStackTrace();
		} catch (NoSuchPort noSuchPort) {
			noSuchPort.printStackTrace();
		} catch (PortInUse portInUse) {
			portInUse.printStackTrace();
		}
		return ServerResponse.createBySuccessMessage("has sended to hardware .");
	}

	@Override
	public ServerResponse getSerialSensorByPortAndAddress(String port, String address) {
		int terminalId = Integer.parseInt(GuavaCache.getKey(TERMINAL_ID));

		SerialSensor serialSensor = serialSensorMapper.selectByTerminalIdAndPortAndAddress(terminalId, port, address);
		if (serialSensor == null) {
			return ServerResponse.createByErrorMessage("can't find the serialSensor with the terminalId: " + terminalId + " and port: " + port + " and address: " + address);
		}

		return ServerResponse.createBySuccess(serialSensor);
	}

	@Override
	public ServerResponse list() {
		// 1.校验
		// 2.获取数据
		List<SerialSensor> serialSensorList = null;
		serialSensorList = serialSensorMapper.listSeriaLSensor();
		if (serialSensorList == null){
			return ServerResponse.createByErrorMessage("can't find serialSensor !");
		}
		return ServerResponse.createBySuccess(serialSensorList);
	}


	private SerialSensor serialSensorGeneratorByPort(String port) {
		if (port == null) {
			return null;
		}

		SerialSensor resultSerialSensor = new SerialSensor();

		resultSerialSensor.setSensorRegisterId(65565);  // 这里设置一个无效的确定值，确保之后可以更新它
		System.out.println(GuavaCache.getKey(TERMINAL_ID));
		resultSerialSensor.setTerminalId(Integer.parseInt(GuavaCache.getKey(TERMINAL_ID)));
		resultSerialSensor.setPort(port);
		resultSerialSensor.setAddress("01");        // 这里设置一个默认值（该默认值应当设置在常量表中）.这个量除非硬件做出让步，否则是无法实现自动注册的
		resultSerialSensor.setBaudrate(9600);       // 同上
		resultSerialSensor.setModel(false);
		resultSerialSensor.setZero(false);
		resultSerialSensor.setMark("Init");     // 初始状态，还没有完全设定值，不进行相关数据采集？
		resultSerialSensor.setCreateTime(new Date());
		resultSerialSensor.setUpdateTime(new Date());

		return resultSerialSensor;
	}

	private List<SerialSensor> serialSensorListGeneratorByPort(List<String> portList) {
		List<SerialSensor> resultSerialSensorList = Lists.newArrayList();

		for (String item : portList) {
			SerialSensor resultSerialSensor = this.serialSensorGeneratorByPort(item);
			if (resultSerialSensor == null) {
				continue;
			}
			resultSerialSensorList.add(resultSerialSensor);
		}

		return resultSerialSensorList;
	}

	private List<SerialSensor> serialSensorFilterByExistInDB(List<SerialSensor> serialSensorList) {
		List<SerialSensor> resultSerialSensorList = Lists.newArrayList();
		for (SerialSensor serialSensor : serialSensorList) {
			ServerResponse checkValidResponse = this.checkValidByPort(serialSensor);
			if (checkValidResponse.isFail()) {
				// 不存在符合条件的数据，将该数据放入新增列表中
				resultSerialSensorList.add(serialSensor);
			}
		}
		return resultSerialSensorList;
	}

	private ServerResponse checkValidByPort(SerialSensor serialSensor) {

		int terminalId = serialSensor.getTerminalId();
		List<SerialSensor> resultSerialSensorList = serialSensorMapper.selectByTerminalIdAndPort(terminalId, serialSensor.getPort());

		if (resultSerialSensorList.size() == 1) {
			return ServerResponse.createBySuccessMessage("the serialSensor meeting the condition exist");
		}
		if (resultSerialSensorList.size() > 1) {
			// 这里日后可能出现异常，就是查询多个结果出来。也就是一个port有多个address，但是这个异常出现了，也就是提醒该写新的语句了。    // 话说硬件难道就不可以有个标准的注册规范嘛
			return ServerResponse.createBySuccessMessage("the serialSensor meeting the condition exist (more than one)!");
		}
		// 无法获得符合条件的数据
		return ServerResponse.createByErrorMessage("no serial meet the condition !");
	}

	private ServerResponse insertOrUpdateSerialSensor(SerialSensor serialSensor){
		if (serialSensor == null){
			return ServerResponse.createByErrorMessage("the serialSensor is null !");
		}

		SerialSensor serialSensorInsertOrUpdate = serialSensor;

		int countRow = serialSensorMapper.insertOrUpdate(serialSensorInsertOrUpdate);
		if (countRow == 0){
			return ServerResponse.createByErrorMessage("serialSensor insert or update fail !");
		}

		return ServerResponse.createBySuccessMessage("serialSensor insert or update success .");
	}
}
