package com.renewable.gateway.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.renewable.gateway.common.GuavaCache;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.constant.InclinationConstant;
import com.renewable.gateway.dao.InclinationDealedInitMapper;
import com.renewable.gateway.pojo.Inclination;
import com.renewable.gateway.pojo.InclinationDealedInit;
import com.renewable.gateway.rabbitmq.pojo.InclinationInit;
import com.renewable.gateway.rabbitmq.producer.InclinationProducer;
import com.renewable.gateway.service.IInclinationDealInitService;
import com.renewable.gateway.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.CacheConstant.TERMINAL_ID;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
@Service("iInclinationDealInitServiceImpl")
public class IInclinationDealInitServiceImpl implements IInclinationDealInitService {


    @Autowired
    private InclinationDealedInitMapper inclinationDealedInitMapper;

    @Autowired
    private InclinationProducer inclinationProducer;

    @Autowired
    private ITerminalServiceImpl iTerminalService;


    @Override
    public ServerResponse<PageInfo> getDataList(int pageNum, int pageSize, int sensor_identifier) {

        //pagehelper 使用逻辑:  第一步，startPage--start；第二步，填充自己的sql查询逻辑；第三步，pageHelper--收尾
        //第一步：startPage--start
        PageHelper.startPage(pageNum, pageSize);

        //第二步：填充自己的sql查询逻辑
        List<InclinationDealedInit> inclinationDealedInitList = inclinationDealedInitMapper.selectList(sensor_identifier);
        //todo_finished 数据全部为空
        if (inclinationDealedInitList == null) {
            return ServerResponse.createByErrorMessage("no data conform your requirement!");
        }

        List<InclinationDealedInit> inclinationVoList = Lists.newArrayList();
        for (InclinationDealedInit inclinationItem : inclinationDealedInitList) {
            InclinationDealedInit inclinationDealedTotalVo = assembleInclinationDealedInitVo(inclinationItem);
            inclinationVoList.add(inclinationDealedTotalVo);
        }

        //第三步：pageHelper--收尾
        PageInfo pageResult = new PageInfo(inclinationDealedInitList);
        pageResult.setList(inclinationVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    //日后有需要的话，可以将这里改为对应VO。
    private InclinationDealedInit assembleInclinationDealedInitVo(InclinationDealedInit inclinationDealedTotal) {
        return inclinationDealedTotal;
    }


    public ServerResponse<List<Object>> getDataListByTime(String startTime, String endTime, int sensor_identifier) {
        List<InclinationDealedInit> inclinationList = inclinationDealedInitMapper.selectListByTime(DateTimeUtil.strToDate(startTime), DateTimeUtil.strToDate(endTime), sensor_identifier);
        List<Object> inclinationVoObjectList = Lists.newArrayList();
        for (InclinationDealedInit inclinationItem : inclinationList) {
            InclinationDealedInit inclinationVo = assembleInclinationDealedInitVo(inclinationItem);
            Object InclinationVoObject = (Object) inclinationVo;
            inclinationVoObjectList.add(InclinationVoObject);

        }
        return ServerResponse.createBySuccess(inclinationVoObjectList);
    }

    @Override
    public ServerResponse uploadDataList() {
        List<InclinationDealedInit> inclinationDealedInitList = inclinationDealedInitMapper.selectListByVersionAndLimit(InclinationConstant.VERSION_CLEANED,50);  //这里以后要集成的Const文件中，另外相关数据字段，应该改为数字（节省带宽，降低出错可能性（写代码））

        if (inclinationDealedInitList == null) {
            return ServerResponse.createByErrorMessage("can't get targeted data from db");
        }

        List<InclinationInit> inclinationInitList = this.inclinationDealedTotalList2InclinationTotalList(inclinationDealedInitList).getData();         //这种转换放在该服务层，还是MQ的调用层，我觉得应该放在这里，但是InclinationTotal又该放在哪里呢？想想，还是将转换放在放在这里，pojo放在Vo或者BO，又或者rabbitmq下。

        // 正确的做法，这里需要进行事务级的控制，确保数据在这里不会因为MQ发送失败，造成数据丢失（RabbitMQ也有自己消息的事务控制，可以了解）
        try {
            inclinationProducer.sendInclinationInit(inclinationInitList);
        } catch (IOException e) {
            log.info("IOException:" + e);
            return ServerResponse.createByErrorMessage("Inclination data try send to MQ but fail !");
        } catch (TimeoutException e) {
            log.info("TimeoutException:" + e);
            return ServerResponse.createByErrorMessage("Inclination data try send to MQ but fail !");
        } catch (InterruptedException e) {
            log.info("InterruptedException:" + e);
            return ServerResponse.createByErrorMessage("Inclination data try send to MQ but fail !");
        }

        // 当上述操作没有出现问题，这里可以将之前的那些数据在数据库的状态修改
        List<Long> idList = this.listIdFromInclinationList(inclinationDealedInitList);
        int countRow = inclinationDealedInitMapper.updateVersionByIdAndVersion(idList,InclinationConstant.VERSION_UPLOADED);
        return ServerResponse.createBySuccessMessage("Inclination data sended to MQ !");
    }

    private ServerResponse<List<InclinationInit>> inclinationDealedTotalList2InclinationTotalList(List<InclinationDealedInit> inclinationDealedInitList) {
        if (inclinationDealedInitList == null) {
            return null;
        }

        List<InclinationInit> inclinationInitList = Lists.newArrayList();
        for (InclinationDealedInit inclinationDealedInitItem : inclinationDealedInitList) {
            InclinationInit inclinationInit = InclinationTotalAssemble(inclinationDealedInitItem);
            inclinationInitList.add(inclinationInit);
        }

        return ServerResponse.createBySuccess(inclinationInitList);
    }

    private InclinationInit InclinationTotalAssemble(InclinationDealedInit inclinationDealedInit) {
        InclinationInit inclinationInit = new InclinationInit();

//        inclinationTotal.setId(inclinationDealedTotal.getId());       // ID不需要传入，由数据库自动递增生成。如果需要在终端服务器找到对应清洗后的数据，可以通过origin_id。其也是唯一标识的，可以作为关键键。
        inclinationInit.setSensorId(inclinationDealedInit.getSensorId());
        inclinationInit.setOriginId(inclinationDealedInit.getOriginId());
        inclinationInit.setAngleX(inclinationDealedInit.getAngleX());
        inclinationInit.setAngleY(inclinationDealedInit.getAngleY());
        inclinationInit.setAngleTotal(inclinationDealedInit.getAngleTotal());
        inclinationInit.setDirectAngle(inclinationDealedInit.getDirectAngle());
        inclinationInit.setAngleInitTotal(inclinationDealedInit.getAngleInitTotal());
        inclinationInit.setDirectAngleInit(inclinationDealedInit.getDirectAngleInit());
        inclinationInit.setTemperature(inclinationDealedInit.getTemperature());
        inclinationInit.setVersion(inclinationDealedInit.getVersion());
        inclinationInit.setCreateTime(inclinationDealedInit.getCreateTime());

//        inclinationInit.setTerminalId(1);      // 建立配置，模块时，这里需要将终端地址改为配置中得ID
        String idStr = GuavaCache.getKey(TERMINAL_ID);
        if (idStr == null) {
            // 更新配置
            iTerminalService.refreshConfigFromCent();
            idStr = GuavaCache.getKey(TERMINAL_ID); // 由于网络传输等的耗时，此时的缓存只会是原始配置，所以一方面可以选择等待（但是离线时间可能较久）。另一方方面，就先传输数据，只能说前几个数据可能会存在问题
        }
        inclinationDealedInit.setId(Long.parseLong(idStr));

        return inclinationInit;
    }

    private List<Long> listIdFromInclinationList(List<InclinationDealedInit> inclinationDealedInitList){
        if (inclinationDealedInitList == null){
            return null;
        }

        List<Long> idList = Lists.newArrayList();
        for (InclinationDealedInit inclinationDealedInit : inclinationDealedInitList) {
            long id = inclinationDealedInit.getId();
            idList.add(id);
        }
        return idList;
    }

}
