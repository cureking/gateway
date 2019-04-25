package com.renewable.gateway.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.renewable.gateway.common.Const;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.sensor.InclinationConst;
import com.renewable.gateway.dao.InclinationMapper;


import com.renewable.gateway.dao.InclinationRegisterMapper;
import com.renewable.gateway.dao.SensorRegisterMapper;
import com.renewable.gateway.pojo.Inclination;
import com.renewable.gateway.pojo.InclinationDealed;
import com.renewable.gateway.pojo.InclinationRegister;
import com.renewable.gateway.pojo.SensorRegister;
import com.renewable.gateway.serial.sensor.InclinationDeal526T;
import com.renewable.gateway.service.IInclinationDealService;
import com.renewable.gateway.service.IInclinationService;
import com.renewable.gateway.service.IRegisteredInfoService;
import com.renewable.gateway.util.DateTimeUtil;
import com.renewable.gateway.util.MatlabUtil;
import com.renewable.gateway.util.OtherUtil;
import com.renewable.gateway.vo.InclinationVo;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sinfit.Calcul;

import java.math.BigDecimal;
import java.util.*;

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
    private IInclinationDealService iInclinationDealService;

    @Autowired
    private IRegisteredInfoService iRegisteredInfoService;

    @Autowired
    private InclinationRegisterMapper inclinationRegisterMapper;

    @Autowired
    private SensorRegisterMapper sensorRegisterMapper;

//    @Autowired
//    private Calcul calcul;


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
        port = OtherUtil.getSubStrByCharAfter(port,'/');

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
        System.out.println("port:"+port+" baudrate:"+baudrate);
        SensorRegister sensorRegister = sensorRegisterMapper.selectByPortAndAddress(port,"01");

        if (sensorRegister == null){
            return ServerResponse.createByErrorMessage("sensorID:"+sensorRegister.getId());
        }
        System.out.println("inclinationRegisterMapper:"+inclinationRegisterMapper);
        InclinationRegister inclinationRegister = inclinationRegisterMapper.selectBySensorKey(sensorRegister.getId());
        if (inclinationRegister == null){
            System.out.println("inclinationRegister 获取失败");
        }

        double R = inclinationRegister.getRadius();
        double[][] initMeasuerArray = OtherUtil.assembleMatlabArray(
                inclinationRegister.getInitH1(),inclinationRegister.getInitH2(),inclinationRegister.getInitH3(),inclinationRegister.getInitH4(),
                inclinationRegister.getInitAngle1(),inclinationRegister.getInitAngle2(),inclinationRegister.getInitAngle3(),inclinationRegister.getInitAngle4());


        //开始算法三的计算          //InclinationConst.InclinationInstallModeEnum.FOUR
        double[] resultTDInitArray = this.calInitAngleTotal(inclination.getAngleX(),inclination.getAngleY(),inclinationRegister.getInitX(),inclinationRegister.getInitY(),inclinationRegister.getInitTotalAngle(), InclinationConst.InclinationInstallModeEnum.codeOf(1));


        //放入计算后，包含初始角度的合倾角，极其对应的方位角
        inclination.setAngleInitTotal(resultTDInitArray[0]);
        inclination.setDirectAngleInit(resultTDInitArray[1]);
        //日后需要，这里可以扩展加入方向角  //方向角的计算可以查看ipad上概念化画板/Renewable/未命名8（包含方位角的计算）
        //添加该数据对应的sensorID
        inclination.setSensorId(inclinationRegister.getSensorId());


        //数据处理由sql完成
        ServerResponse response = inclinationData2DB(inclination);
        return response;
    }

    private double[] calAngleTotal(double angleX, double angleY) {
        double[] angleTotal = null;

        if (angleX == 0 && angleY == 0) {    //这样的条件看起来较为更为清晰，即计算公式无法计算X=Y=0的情况。
            //可以不做处理
        } else {
            angleTotal = OtherUtil.calAngleTotal(angleX, angleY,0,1);
        }
        return angleTotal;
    }

    //计算包含初始角度的合倾角
//    private double calAngleInitTotal(double angleX,double angleY){
//
//    }

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

    @Override
    public ServerResponse cleanDataTask(SensorRegister sensorRegister) {
        //TODO 之后会考虑将一些几乎不改动的信息放入本地缓存中，以降低不必要的时间浪费。

        //1.获取数据清洗的开始数据inclination
        Inclination startInclination = inclinationMapper.selectNextByPrimaryKey(sensorRegister.getCleanLastId());
//        System.out.println("IInclinationServiceImpl/cleanDataTask:startInclination:" + startInclination.toString());
        //
        Inclination currentInclination = inclinationMapper.selectNewByPrimaryKey();
//        System.out.println("IInclinationServiceImpl/cleanDataTask:currentInclination:" + currentInclination.toString());

        //2.判断现在到上一次数据清洗的时间间隔是否足够数据清洗周期长度
        long timeDuration = currentInclination.getCreateTime().getTime() - startInclination.getCreateTime().getTime();
//        System.out.println("IInclinationServiceImpl/cleanDataTask:timeDuration:" + timeDuration);
        if (timeDuration < sensorRegister.getCleanInterval()) {
            return ServerResponse.createByErrorMessage("时间间隔尚未达到设定值，等待下一次清洗");
        }

        //3.进行数据清洗
        return cleanDataAssort(sensorRegister, startInclination, currentInclination);
    }

    /**
     * 对数据清洗类型进行分类
     *
     * @param sensorRegister
     * @return
     */
    private ServerResponse cleanDataAssort(SensorRegister sensorRegister, Inclination startInclination, Inclination currentInclination) {
        if (sensorRegister.getCleanType() == Const.DataCleanType.NoAction.getCode()) {
            return ServerResponse.createBySuccessMessage("该传感器的数据处理类型为" + Const.DataCleanType.NoAction.getValue());
        } else if (sensorRegister.getCleanType() == Const.DataCleanType.PeakAction.getCode()) {
            cleanDatabyPeak(sensorRegister, startInclination, currentInclination);
        } else if (sensorRegister.getCleanType() == Const.DataCleanType.TimeAreaAction.getCode()) {

        }
        return null;
    }

    /**
     * 数据清洗中峰值清洗法
     *
     * @param sensorRegister
     * @param startInclination
     * @param currentInclination
     * @return
     */
    private ServerResponse cleanDatabyPeak(SensorRegister sensorRegister, Inclination startInclination, Inclination currentInclination) {
        //todo 2019.04.04   目前的打算是暂时新建表，之后再设定Spring多数据源。

        //1.获得有效结束 endInclination     之前已经处理了不足一个周期的，接下来就是判断有多少个周期
        long startTime = startInclination.getCreateTime().getTime();
        long currentTime = currentInclination.getCreateTime().getTime();
        long duration = currentTime - startTime;
        long interval = sensorRegister.getCleanInterval();
        long periodCound = duration / interval;

        //2.每个周期要产生一个inclinationDeal
        for (int i = 0; i < periodCound; i++) {
            System.out.println("IInclinationServiceImpl/cleanDatabyPeak: startTime:"+startTime+"  currentTime"+currentTime+"  duration:"+duration+" interval:"+interval+"  periodCound"+periodCound+"  i:"+i);
            //a.计算每个周期的开始时间与结束时间
            long cycleStartTime = startTime + interval * i;
            long cycleEndTime = startTime + interval * (i + 1);
            //b.获取该周期内的峰值inclination
//            System.out.println(sensorRegister.getId());
//            System.out.println(inclinationMapper.selectPeakByIdArea(2200,2300));
            Inclination peakInclination = inclinationMapper.selcetPeakByTimeArea(sensorRegister.getId(), new Date(cycleStartTime), new Date(cycleEndTime));
            if (peakInclination == null) {
//                return ServerResponse.createByErrorMessage("获取峰值失败:" + sensorRegister.toString());
                continue;
            }
            System.out.println("IInclinationServiceImpl/cleanDatabyPeak: startTime:"+ cycleStartTime+"  endTime:"+cycleEndTime+"  start2String"+ new Date(cycleStartTime)+"  peakInclinationId:"+peakInclination.getId());
            //c.对峰值进行封装 日后便于扩展
            InclinationDealed inclinationDealed = new InclinationDealed();
            inclinationDealed = inclinationDealAssemble(peakInclination);
            //d.保存峰值
            iInclinationDealService.inclinationData2DB(inclinationDealed);
        }
        //3.保存最新清洗的inclination_id   //不一定是当前的
        long lastTime = startTime + interval * periodCound;
        long lastId = inclinationMapper.selectLastIdbyTime(new Date(lastTime));
        sensorRegister.setCleanLastId(lastTime);
        return iRegisteredInfoService.updateSensor(sensorRegister);
    }

    private InclinationDealed inclinationDealAssemble(Inclination inclination) {
        InclinationDealed inclinationDealed = new InclinationDealed();
        inclinationDealed.setOriginId(inclination.getId());
        inclinationDealed.setSensorId(inclination.getSensorId());
        inclinationDealed.setAngleX(inclination.getAngleX());
        inclinationDealed.setAngleY(inclination.getAngleY());
        inclinationDealed.setAngleTotal(inclination.getAngleTotal());
        inclinationDealed.setTemperature(inclination.getTemperature());
        return inclinationDealed;
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



    public double[] calInitAngleTotal(double angleX, double angleY,double X,double Y, double angleInitTotal, InclinationConst.InclinationInstallModeEnum installModeEnum) {
        return OtherUtil.calInitAngleTotal(angleX, angleY, X,Y,angleInitTotal, installModeEnum);

    }

}