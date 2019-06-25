package com.renewable.gateway.service;

import com.github.pagehelper.PageInfo;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.pojo.Inclination;

import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
public interface IInclinationService {

	//对外
	ServerResponse<PageInfo> listInclinationData(int pageNum, int pageSize);

	ServerResponse<List<Object>> listInclinationDataByTime(String startTime, String endTime);


	//对内
	void sendDataInclination(String port, String baudrate, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum);

	ServerResponse receiveData(String port, int baudrate, byte[] originBuffer);

	//数据处理模块的调用 目前还可以用来进行一些测试
	ServerResponse inclinationData2DB(Inclination inclination);

	//设置风机初始倾角测量的计算(算法一）
	double[] initTotalAngleCal(double[][] initMeasureArray, double R);

	//同上（Matlab版）
//    Object[] initAngleTotalCalMatlab(double[][] initMeasureArray, double R);

	//设置计算包含初始值的总倾斜合倾角（算法三） //算法二原始版在OtherUtil
	double[] calInitAngleTotal(double angleX, double angleY, double X, double Y, double angleInit, InclinationConst.InclinationInstallModeEnum installModeEnum);

//	// 定时任务-获取数据（由功能指向传感器类型）
//	ServerResponse taskLoadInclinationFromSensor();
}
