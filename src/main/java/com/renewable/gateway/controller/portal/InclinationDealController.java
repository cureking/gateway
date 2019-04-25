package com.renewable.gateway.controller.portal;

import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.pojo.InclinationDealed;
import com.renewable.gateway.service.IInclinationDealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description：
 * @Author: jarry
 */
@Controller
//RequestMapping注解，将其下（class Controller）的请求全部统一到/sensor/这一命名空间中。
@RequestMapping("/inclination_deal/")
public class InclinationDealController {
    @Autowired
    private IInclinationDealService iInclinationDealService;


    //test
    //根据传感器id，pageHelper参数获取对应监控数据  //采用pageHelper技术
    @RequestMapping(value = "data_2_db.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse data2DB() {
        InclinationDealed inclinationDealed = new InclinationDealed();
        inclinationDealed.setAngleX(10.0);
        inclinationDealed.setAngleY(10.0);
        inclinationDealed.setTemperature(10.0);
        return iInclinationDealService.inclinationData2DB(inclinationDealed);
    }

}
