package com.renewable.gateway.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.dao.InclinationDealedTotalMapper;
import com.renewable.gateway.pojo.InclinationDealedTotal;
import com.renewable.gateway.rabbitmq.producer.InclinationProducer;
import com.renewable.gateway.rabbitmq.pojo.InclinationTotal;
import com.renewable.gateway.service.IInclinationDealTotalService;
import com.renewable.gateway.util.DateTimeUtil;
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
@Service("iInclinationDealTotalServiceImpl")
public class IInclinationDealTotalServiceImpl implements IInclinationDealTotalService {

    @Autowired
    private InclinationDealedTotalMapper inclinationDealedTotalMapper;

    @Autowired
    private InclinationProducer inclinationProducer;


    @Override
    public ServerResponse<PageInfo> getDataList(int pageNum, int pageSize, int sensor_identifier) {

        //pagehelper 使用逻辑:  第一步，startPage--start；第二步，填充自己的sql查询逻辑；第三步，pageHelper--收尾
        //第一步：startPage--start
        PageHelper.startPage(pageNum, pageSize);

        //第二步：填充自己的sql查询逻辑
        List<InclinationDealedTotal> inclinationDealedTotalList = inclinationDealedTotalMapper.selectList(sensor_identifier);
        //todo_finished 数据全部为空
        if (inclinationDealedTotalList == null) {
            return ServerResponse.createByErrorMessage("no data conform your requirement!");
        }

        List<InclinationDealedTotal> inclinationVoList = Lists.newArrayList();
        for (InclinationDealedTotal inclinationItem : inclinationDealedTotalList) {
            InclinationDealedTotal inclinationDealedTotalVo = assembleInclinationDealedTotalVo(inclinationItem);
            inclinationVoList.add(inclinationDealedTotalVo);
        }

        //第三步：pageHelper--收尾
        PageInfo pageResult = new PageInfo(inclinationDealedTotalList);
        pageResult.setList(inclinationVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    //日后有需要的话，可以将这里改为对应VO。
    private InclinationDealedTotal assembleInclinationDealedTotalVo(InclinationDealedTotal inclinationDealedTotal){
        return inclinationDealedTotal;
    }


    public ServerResponse<List<Object>> getDataListByTime(String startTime, String endTime, int sensor_identifier) {
        List<InclinationDealedTotal> inclinationList = inclinationDealedTotalMapper.selectListByTime(DateTimeUtil.strToDate(startTime), DateTimeUtil.strToDate(endTime), sensor_identifier);
        List<Object> inclinationVoObjectList = Lists.newArrayList();
        for (InclinationDealedTotal inclinationItem : inclinationList) {
            InclinationDealedTotal inclinationVo = assembleInclinationDealedTotalVo(inclinationItem);
            Object InclinationVoObject = (Object) inclinationVo;
            inclinationVoObjectList.add(InclinationVoObject);

        }
        return ServerResponse.createBySuccess(inclinationVoObjectList);
    }

    /**
     * 进行数据的上传
     * @return
     */
    @Override
    public ServerResponse uploadDataList() {
        List<InclinationDealedTotal> inclinationDealedTotalList = inclinationDealedTotalMapper.selectListByVersion("Cleaned");  //这里以后要集成的Const文件中，另外相关数据字段，应该改为数字（节省带宽，降低出错可能性（写代码））
        if (inclinationDealedTotalList == null){
            return ServerResponse.createByErrorMessage("can't get targeted data from db");
        }

        // 之后如果实在无法通过一个SQL语句实现（不打算使用存储过程，因为之后终端服务器的部署是很多的。  那就在这里执行相关update语句，根据获取的数据的主键ID，来更新数据表相关记录的状态

        List<InclinationTotal> inclinationTotalList = this.inclinationDealedTotalList2InclinationTotalList(inclinationDealedTotalList).getData();         //这种转换放在该服务层，还是MQ的调用层，我觉得应该放在这里，但是InclinationTotal又该放在哪里呢？想想，还是将转换放在放在这里，pojo放在Vo或者BO，又或者rabbitmq下。

        // 正确的做法，这里需要进行事务级的控制，确保数据在这里不会因为MQ发送失败，造成数据丢失（RabbitMQ也有自己消息的事务控制，可以了解）
        try {
            inclinationProducer.sendInclinationTotal(inclinationTotalList);
        } catch (IOException e) {
            log.info("IOException:"+e);
            return ServerResponse.createByErrorMessage("Inclination data try send to MQ but fail !");
        } catch (TimeoutException e) {
            log.info("TimeoutException:"+e);
            return ServerResponse.createByErrorMessage("Inclination data try send to MQ but fail !");
        } catch (InterruptedException e) {
            log.info("InterruptedException:"+e);
            return ServerResponse.createByErrorMessage("Inclination data try send to MQ but fail !");
        }

        return ServerResponse.createBySuccessMessage("Inclination data sended to MQ !");
    }

    private ServerResponse<List<InclinationTotal>> inclinationDealedTotalList2InclinationTotalList(List<InclinationDealedTotal> inclinationDealedTotalList){
        if (inclinationDealedTotalList == null){
            return null;
        }

        List<InclinationTotal> inclinationTotalList = Lists.newArrayList();
        for (InclinationDealedTotal inclinationDealedTotalItem : inclinationDealedTotalList) {
            InclinationTotal inclinationTotal = InclinationTotalAssemble(inclinationDealedTotalItem);
            inclinationTotalList.add(inclinationTotal);
        }

        return ServerResponse.createBySuccess(inclinationTotalList);
    }

    private InclinationTotal InclinationTotalAssemble(InclinationDealedTotal inclinationDealedTotal){
        InclinationTotal inclinationTotal = new InclinationTotal();

//        inclinationTotal.setId(inclinationDealedTotal.getId());       // ID不需要传入，由数据库自动递增生成。如果需要在终端服务器找到对应清洗后的数据，可以通过origin_id。其也是唯一标识的，可以作为关键键。
        inclinationTotal.setSensorId(inclinationDealedTotal.getSensorId());
        inclinationTotal.setOriginId(inclinationDealedTotal.getOriginId());
        inclinationTotal.setAngleX(inclinationDealedTotal.getAngleX());
        inclinationTotal.setAngleY(inclinationDealedTotal.getAngleY());
        inclinationTotal.setAngleTotal(inclinationDealedTotal.getAngleTotal());
        inclinationTotal.setDirectAngle(inclinationDealedTotal.getDirectAngle());
        inclinationTotal.setAngleInitTotal(inclinationDealedTotal.getAngleInitTotal());
        inclinationTotal.setDirectAngleInit(inclinationDealedTotal.getDirectAngleInit());
        inclinationTotal.setTemperature(inclinationDealedTotal.getTemperature());
        inclinationTotal.setVersion(inclinationDealedTotal.getVersion());
        inclinationTotal.setCreateTime(inclinationDealedTotal.getCreateTime());

        inclinationTotal.setTerminalId(1);      //建立配置，模块时，这里需要将终端地址改为配置中得ID    // 配置会存在数据库与缓存两个部分，但由于空间上存在三个分布，而时间上分布不明确，故需要注意一致性问题。基于业务特点，建议最终一致性，或用户一致性。



        return  inclinationTotal;
    }

}
