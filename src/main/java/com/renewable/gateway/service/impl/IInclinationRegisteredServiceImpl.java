package com.renewable.gateway.service.impl;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.InclinationRegisterMapper;
import com.renewable.gateway.pojo.InclinationRegister;
import com.renewable.gateway.service.IInclinationRegisteredService;
import com.renewable.gateway.util.MatlabUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.TreeMap;

/**
 * @Description：
 * @Author: jarry
 */
@Service("iInclinatinRegisteredService")
@Slf4j
public class IInclinationRegisteredServiceImpl implements IInclinationRegisteredService {

    @Autowired
    private InclinationRegisterMapper inclinationRegisterMapper;


    //切记要在控制层控制输入值的问题，以及notNull的问题。

    /**
     * 进行倾斜传感器注册信息的插入或更新（数据库没有对应id就是插入，有的话就是更新  //TODO 目前就是插入，具体可以参考mmall项目.
     *
     * @param inclinationRegister
     * @return
     */
    public ServerResponse insertRegisteredInfo(InclinationRegister inclinationRegister) {

        //为算法二做准备
        double[][] initMeasureArray = new double[4][2];
        initMeasureArray[0][0] = (inclinationRegister.getInitH1() == null ? 0 : inclinationRegister.getInitH1());
        initMeasureArray[1][0] = (inclinationRegister.getInitH2() == null ? 0 : inclinationRegister.getInitH2());
        initMeasureArray[2][0] = (inclinationRegister.getInitH3() == null ? 0 : inclinationRegister.getInitH3());
        initMeasureArray[3][0] = (inclinationRegister.getInitH4() == null ? 0 : inclinationRegister.getInitH4());
        initMeasureArray[0][1] = (inclinationRegister.getInitAngle1() == null ? 0 : inclinationRegister.getInitAngle1());
        initMeasureArray[1][1] = (inclinationRegister.getInitAngle2() == null ? 0 : inclinationRegister.getInitAngle2());
        initMeasureArray[2][1] = (inclinationRegister.getInitAngle3() == null ? 0 : inclinationRegister.getInitAngle3());
        initMeasureArray[3][1] = (inclinationRegister.getInitAngle4() == null ? 0 : inclinationRegister.getInitAngle4());
        double radius = inclinationRegister.getRadius();

        TreeMap<String, Double> initMap = sensorInclinationRegisterAngleInit(initMeasureArray, radius).getData();

        inclinationRegister.setInitTotalAngle(initMap.get("initAngleTotal"));
        inclinationRegister.setInitDirectAngle(initMap.get("initAngleDirect"));
        inclinationRegister.setInitX(initMap.get("initX"));
        inclinationRegister.setInitY(initMap.get("initY"));

        //extend 其他数据的处理

        //这里还需要存储到缓存中   //通过xxrefresh来封装，（如调用缓存发现没有相关缓存，可以直接refresh() //refresh()中可能有多个方面的refresh()函数，如倾斜注册表，配置表等等。
        //话说，本地数据库，真的有必要加入缓存嘛？

        int result = 0;
        if (inclinationRegister.getId() != null) {
            //更新数据
            result = inclinationRegisterMapper.updateByPrimaryKeySelective(inclinationRegister);
        } else {
            //保存数据至数据库。
            result = inclinationRegisterMapper.insertSelective(inclinationRegister);
        }
        if (result == 0) {
            return ServerResponse.createByErrorMessage("数据持久化失败");
        }

        return ServerResponse.createBySuccessMessage("数据持久化成功");
    }

    /**
     * 在填充倾斜传感器初始配置时，就完成对应初始计算工作
     *
     * @param initMeasureArray
     * @param R
     * @return
     */
    private ServerResponse<TreeMap> sensorInclinationRegisterAngleInit(double[][] initMeasureArray, double R) {
//        double[][] initMeasureArray = {{0,315},{0,225},{1.707,90},{0.1,270}};       //之后，改为从数据库的倾斜传感器注册表中获取。    //如果数据库没有数据，要提醒用户设置初始参数。
//        double R = 1;                                                               //同样，之后从数据库的倾斜传感器注册表中获取。

        Object[] initParamObject = null;
        try {
            initParamObject = MatlabUtil.initAngleTotalCalMatlab(initMeasureArray, R);
        } catch (Exception e) {
            ServerResponse.createByErrorMessage("IInclinationServiceImpl/sensorInclinationRegisterAngleInit:calcul of sinfit() Exception!");
        }

        double initAngleTotal = (double) initParamObject[0];
        double initAngleDirect = (double) initParamObject[1];
        double initX = (double) initParamObject[2];
        double initY = (double) initParamObject[3];

        TreeMap<String, Double> initMap = new TreeMap<>();
        initMap.put("initAngleTotal", initAngleTotal);
        initMap.put("initAngleDirect", initAngleDirect);
        initMap.put("initX", initX);
        initMap.put("initY", initY);

        return ServerResponse.createBySuccess(initMap);
    }

    //从guavaCache中获取相关数据的方法     //缓存中的数据结构应该怎么选择呢。毕竟不是redis。或者说通过jackson，实现对象数据序列化。
    public ServerResponse getGuavaCacheData() {

        //1.先确定key值     //key的设定需要一定的规范，最好能做一个专门的封装方法，因为之后肯定还有很多地方需要这种本地缓存。

        //2.通过key，从缓存中获取目标值

        //3.通过jsonUtil将目标值转为对象数据（jackson）

        return null;
    }

    public ServerResponse setGuavaCacheData() {

        //1.先确定key值

        //2.通过jsonUtil将目标对象转为String数据（jackson）

        //3.将前两步实现的数据保存至缓存中

        return null;
    }

    public ServerResponse refreshGuavaCache() {
        //重新缓存
        //1.从数据库获取对应数据
        //2.调用setGuavaCacheData()

        return null;
    }

}
