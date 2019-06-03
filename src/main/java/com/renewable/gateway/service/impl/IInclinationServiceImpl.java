package com.renewable.gateway.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.dao.InclinationMapper;
import com.renewable.gateway.dao.SensorRegisterMapper;
import com.renewable.gateway.pojo.*;
import com.renewable.gateway.serial.sensor.InclinationDeal526T;
import com.renewable.gateway.service.IInclinationService;
import com.renewable.gateway.service.IInitializationInclinationService;
import com.renewable.gateway.service.ISerialSensorService;
import com.renewable.gateway.util.DateTimeUtil;
import com.renewable.gateway.util.OtherUtil;
import com.renewable.gateway.vo.InclinationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

//import com.renewable.gateway.serialTemp.sensor.IInclinationDeal;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iInclinationServiceImpl")
public class IInclinationServiceImpl implements IInclinationService {

    @Autowired
    private InclinationMapper inclinationMapper;

    @Autowired
    private ISerialSensorService iSerialSensorService;

    @Autowired
    private IInitializationInclinationService iInitializationInclinationService;

//    @Autowired
//    private SensorRegisterMapper sensorRegisterMapper;

    public ServerResponse<PageInfo> getInclinationDataList(int pageNum, int pageSize) {
        //pagehelper 使用逻辑:  第一步，startPage--start；第二步，填充自己的sql查询逻辑；第三步，pageHelper--收尾
        //第一步：startPage--start
        PageHelper.startPage(pageNum, pageSize);

        //第二步：填充自己的sql查询逻辑
        List<Inclination> inclinationList = inclinationMapper.selectList();
        //todo_finished 数据全部为空
        if (inclinationList == null) {
            return ServerResponse.createByErrorMessage("no data conform your requirement!");
        }

        List<InclinationVo> inclinationVoList = Lists.newArrayList();
        for (Inclination inclinationItem : inclinationList) {
            InclinationVo inclinationVo = assumbleInclinationListVo(inclinationItem);
            inclinationVoList.add(inclinationVo);
        }

        //第三步：pageHelper--收尾
        PageInfo pageResult = new PageInfo(inclinationList);
        pageResult.setList(inclinationVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<List<Object>> getInclinationDataListTime(String startTime, String endTime) {
        List<Inclination> inclinationList = inclinationMapper.selectListByTime(DateTimeUtil.strToDate(startTime), DateTimeUtil.strToDate(endTime));
        List<Object> inclinationVoObjectList = Lists.newArrayList();
        for (Inclination inclinationItem : inclinationList) {
            InclinationVo inclinationVo = assumbleInclinationListVo(inclinationItem);
            Object InclinationVoObject = (Object) inclinationVo;
            inclinationVoObjectList.add(InclinationVoObject);

        }
        return ServerResponse.createBySuccess(inclinationVoObjectList);
    }

    @Override
    public void sendDataInclination(String port, String baudrate, InclinationConst.InclinationSensor1Enum inclinationSensor1Enum) {
        System.out.println("IInclinationSerciveImpl/sendDataInclination");
        //todo 其实底层并没有采用baudrate，因为这需要动态管理，当时还没有设备注册表，故采用统一的波特率
//        iInclinationDeal.sendData("COM1",9600,01, InclinationConst.InclinationSensor1Enum.READALL);

    }

    /**
     * 用于将serialListener接收到的数据转为标准格式，并计算相关综合值，最后写入数据库。
     *
     * @param port
     * @param baudrate
     * @param originBuffer
     * @return
     */
    @Override
    public ServerResponse receiveData(String port, int baudrate, byte[] originBuffer) {
        //要对获得的name（即port进行一定处理.）
        port = OtherUtil.getSubStrByCharAfter(port, '/');

        ServerResponse<Inclination> serverResponse = InclinationDeal526T.origin2Object(originBuffer);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("data transfer error:" + Arrays.toString(originBuffer));
        }

        Inclination inclination = serverResponse.getData();
        //放入计算后，不含初始角度的合倾角与对应方位角
        double[] resultTDArray = calAngleTotal(inclination.getAngleX(), inclination.getAngleY());
        inclination.setAngleTotal(resultTDArray[0]);
        inclination.setDirectAngle(resultTDArray[1]);

        //获取算法三所需参数（调用算法二matlab版）   //todo 其实这里应该是根据port与address的，但是 获取的数据还没有address。。当然上一层的数据是有的，只是没有传下来，之后有时间弄一下。
        System.out.println("port:" + port + " baudrate:" + baudrate);
        StringBuilder address = new StringBuilder("01");
        ServerResponse serialSensorResponse = iSerialSensorService.getSerialSensorByPortAndAddress(port,address.toString());
        if (serialSensorResponse.isFail()){
            return ServerResponse.createByErrorMessage("can't get the serialSensor with port: "+port+ " address: "+address.toString());
        }
        SerialSensor serialSensor = (SerialSensor)serialSensorResponse.getData();
//        SensorRegister sensorRegister = sensorRegisterMapper.selectByPortAndAddress(port, "01");

        if (serialSensor == null) {
            return ServerResponse.createByErrorMessage("can't get the serialSensor with port: "+port+ " address: "+address.toString());
        }
        int sensorRegisterIdInInitializationInclination = serialSensor.getSensorRegisterId();
        ServerResponse initializationInclinationResponse = iInitializationInclinationService.getInitializationInclinationBySensorRegisterId(sensorRegisterIdInInitializationInclination);
        if (initializationInclinationResponse.isFail()){
            return ServerResponse.createByErrorMessage("can't get the initializationInclinationResponse with sensorRegisterId: "+sensorRegisterIdInInitializationInclination);
        }
        InitializationInclination initializationInclination = (InitializationInclination)initializationInclinationResponse.getData();

        double R = initializationInclination.getRadius();
        double[][] initMeasuerArray = OtherUtil.assembleMatlabArray(
                initializationInclination.getInitH1(), initializationInclination.getInitH2(), initializationInclination.getInitH3(), initializationInclination.getInitH4(),
                initializationInclination.getInitAngle1(), initializationInclination.getInitAngle2(), initializationInclination.getInitAngle3(), initializationInclination.getInitAngle4());


        //开始算法三的计算          //InclinationConst.InclinationInstallModeEnum.FOUR
        double[] resultTDInitArray = this.calInitAngleTotal(inclination.getAngleX(), inclination.getAngleY(), initializationInclination.getInitX(), initializationInclination.getInitY(), initializationInclination.getInitTotalAngle(), InclinationConst.InclinationInstallModeEnum.codeOf(1));


        //放入计算后，包含初始角度的合倾角，极其对应的方位角
        inclination.setAngleInitTotal(resultTDInitArray[0]);
        inclination.setDirectAngleInit(resultTDInitArray[1]);
        //日后需要，这里可以扩展加入方向角  //方向角的计算可以查看ipad上概念化画板/Renewable/未命名8（包含方位角的计算）
        //添加该数据对应的sensorID
        inclination.setSensorId(initializationInclination.getSensorRegisterId());
        inclination.setVersion("NoClean");


        //数据处理由sql完成
        ServerResponse response = inclinationData2DB(inclination);
        return response;
    }

    private double[] calAngleTotal(double angleX, double angleY) {
        double[] angleTotal = null;

        if (angleX == 0 && angleY == 0) {    //这样的条件看起来较为更为清晰，即计算公式无法计算X=Y=0的情况。
            //可以不做处理
        } else {
            angleTotal = OtherUtil.calAngleTotal(angleX, angleY, 0, 1);
        }
        return angleTotal;
    }

    @Override
    public ServerResponse inclinationData2DB(Inclination inclination) {
        System.out.println("angle_X:" + inclination.getAngleX() + "  angle_Y:" + inclination.getAngleY() + "  temperature:" + inclination.getTemperature());

        inclination.setCreateTime(new Date());

        int resutlt = inclinationMapper.insertSelective(inclination);
        if (resutlt == 0) {
            return ServerResponse.createByErrorMessage("insert inclinationData failure!");
        }
        return ServerResponse.createBySuccessMessage("insert inclinationData success");
    }


    private InclinationVo assumbleInclinationListVo(Inclination inclination) {
        InclinationVo inclinationVo = new InclinationVo();
        inclinationVo.setId(inclination.getId());
        inclinationVo.setAngleX(inclination.getAngleX());
        inclinationVo.setAngleY(inclination.getAngleY());
        inclinationVo.setTemperature(inclination.getTemperature());
        inclinationVo.setCreateTime(inclination.getCreateTime());

        //还可以添加一些公共部分，如url中的domain

        return inclinationVo;
    }

    private InclinationDealedTotal inclinationDealAssemble(Inclination inclination) {
        InclinationDealedTotal inclinationDealedTotal = new InclinationDealedTotal();
        inclinationDealedTotal.setOriginId(inclination.getId());
        inclinationDealedTotal.setSensorId(inclination.getSensorId());
        inclinationDealedTotal.setAngleX(inclination.getAngleX());
        inclinationDealedTotal.setAngleY(inclination.getAngleY());
        inclinationDealedTotal.setAngleTotal(inclination.getAngleTotal());
        inclinationDealedTotal.setTemperature(inclination.getTemperature());
        return inclinationDealedTotal;
    }

    /**
     * 有关风机初始倾角测量的计算
     *
     * @param initMeasureArray //  double[][] initMeasureArray  double p1h,double p1angel,double p2h,double p2angel,double p3h,double p3angel,double p4h,double p4angel
     * @param R
     * @return 返回数组的第一个元素表示合倾角，第二个元素表示其方位角。
     */
    public double[] initTotalAngleCal(double[][] initMeasureArray, double R) {
        double initTotalAngle = 0;
        double initTotalDirectAngle = 0;
        double[] singlePlaneResultArray;

        //这里先硬编码，之后进行抽象，重构。
        double[][] singlePlaneArray1 = {initMeasureArray[1], initMeasureArray[2], initMeasureArray[3]};
        singlePlaneResultArray = initPlaneAngle(singlePlaneArray1, R);
        if (initTotalAngle < singlePlaneResultArray[0]) {
            initTotalAngle = singlePlaneResultArray[0];
            initTotalDirectAngle = singlePlaneResultArray[1];
        }
//        System.out.println(Arrays.toString(singlePlaneResultArray));
        double[][] singlePlaneArray2 = {initMeasureArray[0], initMeasureArray[2], initMeasureArray[3]};
        singlePlaneResultArray = initPlaneAngle(singlePlaneArray2, R);
        if (initTotalAngle < singlePlaneResultArray[0]) {
            initTotalAngle = singlePlaneResultArray[0];
            initTotalDirectAngle = singlePlaneResultArray[1];
        }
//        System.out.println(Arrays.toString(singlePlaneResultArray));
        double[][] singlePlaneArray3 = {initMeasureArray[0], initMeasureArray[1], initMeasureArray[3]};
        singlePlaneResultArray = initPlaneAngle(singlePlaneArray3, R);
        if (initTotalAngle < singlePlaneResultArray[0]) {
            initTotalAngle = singlePlaneResultArray[0];
            initTotalDirectAngle = singlePlaneResultArray[1];
        }
//        System.out.println(Arrays.toString(singlePlaneResultArray));
        double[][] singlePlaneArray4 = {initMeasureArray[0], initMeasureArray[1], initMeasureArray[2]};
        singlePlaneResultArray = initPlaneAngle(singlePlaneArray4, R);
        if (initTotalAngle < singlePlaneResultArray[0]) {
            initTotalAngle = singlePlaneResultArray[0];
            initTotalDirectAngle = singlePlaneResultArray[1];
        }
//        System.out.println(Arrays.toString(singlePlaneResultArray));

        double[] totalAngleArray = {initTotalAngle, initTotalDirectAngle};
        return totalAngleArray;
    }


    //    计算三个点组成的平面的初始倾角
//    返回数组的第一个元素表示合倾角，第二个元素表示其方位角。
//    double p1h,double p1angel,double p2h,double p2angel,double p3h,double p3angel         singlePlaneResultArray[high,angle]
    private double[] initPlaneAngle(double[][] singlePlaneArray, double R) {
//        for (double[] doubles : singlePlaneArray) {
//            System.out.println(Arrays.toString(doubles));
//        }
        //要将三个点按高程从小到大排序
        singlePlaneArray = OtherUtil.bubbleSort(singlePlaneArray);  //默认是按0位排序的 //这里采用的是冒泡排序，之后可以改进
//        for (double[] doubles : singlePlaneArray) {
//            System.out.println(Arrays.toString(doubles));
//        }

        //提取出需要的参数  //其实可以直接写，但为了后来者可以看懂，还是分开写吧。
        double p1h = singlePlaneArray[0][0];
        double p1angel = singlePlaneArray[0][1];
        double p2h = singlePlaneArray[1][0];
        double p2angel = singlePlaneArray[1][1];
        double p3h = singlePlaneArray[2][0];
        double p3angel = singlePlaneArray[2][1];

        //将角度转为弧度制。
//        System.out.println("angle:"+p1angel);
        p1angel = OtherUtil.angle2radian(p1angel);
//        System.out.println("radian:"+p1angel);
        p2angel = OtherUtil.angle2radian(p2angel);
        p3angel = OtherUtil.angle2radian(p3angel);

        double F = Math.sin(p2angel - p3angel)
                + Math.sin(p3angel - p1angel)
                + Math.sin(p1angel - p2angel);
        double Fx = (Math.sin(p3angel) - Math.sin(p2angel)) * p1h +
                (Math.sin(p1angel) - Math.sin(p3angel)) * p2h +
                (Math.sin(p2angel) - Math.sin(p1angel)) * p3h;

        double Fy = (Math.cos(p3angel) - Math.cos(p2angel)) * p1h +
                (Math.cos(p1angel) - Math.cos(p3angel)) * p2h +
                (Math.cos(p2angel) - Math.cos(p1angel)) * p3h;
//        System.out.println("Fy start:");
//        System.out.println((Math.cos(p3angel)-Math.cos(p2angel))*p1h);
//        System.out.println((Math.cos(p1angel)-Math.cos(p3angel))*p2h);
//        System.out.println((Math.cos(p2angel)-Math.cos(p1angel))*p3h);
//        System.out.println("Fy end!");
        double X = Fx / (F * R);
        double Y = Fy / (F * R);

        double totalAngle = Math.acos(1 / Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2) + 1));      //合倾角计算
        double totalDirectAngle;
        if (Y > 0) {
            totalDirectAngle = Math.acos(-X / (Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2))));
        } else if (Y < 0) {
            totalDirectAngle = 2 * Math.PI - Math.acos(-X / (Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2))));
        } else {
            //Y == 0的情况下
            totalDirectAngle = Math.PI;   //TODO 需要修改，这里是随便写的初始值。
        }
        //方位角范围是[0,360)的
        totalDirectAngle = (totalDirectAngle == Math.PI * 2 ? 0 : totalDirectAngle);
//
//        System.out.println("pre-p1:"+p1angel+" pre-p2:"+p2angel+" pre-p3:"+p3angel);
//        System.out.println("F:"+F+" Fx:"+Fx+" Fy:"+Fy+" X:"+X+" Y:"+Y);
//        System.out.println("totalAngle:"+OtherUtil.radian2angle(totalAngle)+" totlaDirectAngle:"+OtherUtil.radian2angle(totalDirectAngle));

        double[] totalAngleArray = {OtherUtil.radian2angle(totalAngle), OtherUtil.radian2angle(totalDirectAngle)};
        return totalAngleArray;
    }


    public double[] calInitAngleTotal(double angleX, double angleY, double X, double Y, double angleInitTotal, InclinationConst.InclinationInstallModeEnum installModeEnum) {
        return OtherUtil.calInitAngleTotal(angleX, angleY, X, Y, angleInitTotal, installModeEnum);

    }

}