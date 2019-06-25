package com.renewable.gateway.service.impl;

import com.google.common.collect.Lists;
import com.renewable.gateway.common.GuavaCache;
import com.renewable.gateway.common.ServerResponse;
import com.renewable.gateway.common.constant.WarningConstant;
import com.renewable.gateway.dao.WarningMapper;
import com.renewable.gateway.pojo.InclinationDealedInit;
import com.renewable.gateway.pojo.Warning;
import com.renewable.gateway.rabbitmq.producer.WarningProducer;
import com.renewable.gateway.service.IInclinationDealInitService;
import com.renewable.gateway.service.IInitializationInclinationService;
import com.renewable.gateway.service.IWarningService;
import com.renewable.gateway.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.renewable.gateway.common.constant.CacheConstant.TERMINAL_ID;
import static com.renewable.gateway.common.constant.WarningConstant.*;
import static com.renewable.gateway.common.constant.WarningConstant.SensorTypeEnum.Inclination_HCA526T;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
@Service("iWarningServiceImpl")
public class IWarningServiceImpl implements IWarningService {

	@Autowired
	private IInclinationDealInitService iInclinationDealInitService;

	@Autowired
	private IInitializationInclinationService iInitializationInclinationService;

	@Autowired
	private WarningProducer warningProducer;

	@Autowired
	private WarningMapper warningMapper;

	@Override
	public ServerResponse insertWarning(Warning warning) {
		if (warning == null) {
			return ServerResponse.createByErrorMessage("there is no warning !");
		}
		int countRow = warningMapper.insertSelective(warning);
		if (countRow == 0) {
			return ServerResponse.createByErrorMessage("warning insert fail !");
		}
		return ServerResponse.createBySuccessMessage("warniing insert success !");
	}

	@Override
	public ServerResponse insertWarningList(List<Warning> warningList) {
		if (warningList == null || warningList.size() == 0) {
			return ServerResponse.createByErrorMessage("there is no warning !");
		}

		int countRow = warningMapper.insertBatch(warningList);
		if (countRow == 0) {
			return ServerResponse.createByErrorMessage("warnings insert fail !");
		}
		return ServerResponse.createBySuccessMessage("warniings insert success !");
	}

	@Override
	public ServerResponse stateCheck() {
		// 倾斜传感器数据监测
		ServerResponse inclinationInitCheckResponse = this.inclinationInitCheck();
		if (inclinationInitCheckResponse.isFail()) {
			return ServerResponse.createByErrorMessage("inclination check fail !");
		}

		return ServerResponse.createBySuccessMessage("state check success !");
	}

	/*
	 * 目测其中很多部分都可以进行抽象，甚至愿意花时间的话，可以利用泛型等进行整个抽象。
	 */
	@Override
	public ServerResponse inclinationInitCheck() {

		// 1.从inclinationDealInit对应服务中获取符合条件的异常数据列表
		Long lastOringinId = warningMapper.selectLastOringinId();
		if (lastOringinId == null) {
			lastOringinId = 0L;     // 如果没有目标数据，那么这里就设置一个后面根本不起作用的数据（其实这就有一定耦合性了，之后修正吧。因为这涉及基本数据类型与封装数据类型的使用规范，以前没有注意啊。唉）
		}
		System.out.println("duration: " + INCLINATIONINIT_DURATION);

		// 1+.获取阈值	// TODO 原本应该是针对不同传感器，按照不同的阈值进行判断。但是原先的设计是共同阈值，所以这里就先按照对应init表中第一条的数据来获取。之后再拆分
		Integer sensorReigsterId = null;
		ServerResponse inclinationInitLimitResponse = iInitializationInclinationService.getInitLimitBySelectiveSensorRegisterId(sensorReigsterId);
		if (inclinationInitLimitResponse.isFail()){
			return inclinationInitLimitResponse;
		}

		double inclinationInitLimit = 	(double)inclinationInitLimitResponse.getData();
		ServerResponse checkResponse = iInclinationDealInitService.listCheckedData(inclinationInitLimit, INCLINATIONINIT_DURATION, lastOringinId, INCLINATIONINIT_COUNT_LIMIT);
		if (checkResponse.isFail()) {
			return ServerResponse.createByErrorMessage("list inclination check fail !");
		}
		List<InclinationDealedInit> inclinationDealedInitList = (List<InclinationDealedInit>) checkResponse.getData();   // 这里有点奇怪，按说这里是不需要进行强转的。因为实际被调用方法那里的泛型已经赋类型了啊。之后再看看。

		// 2-.如果异常数据列表没有数据，直接跳出
		if (inclinationDealedInitList.size() == 0) {
			return ServerResponse.createBySuccessMessage("there is no inclinationInit checked !");
		}

		// 2.判断缓存中是否有相关数据，如果有，就增加至列表中（切记要加载列表的开头，否则后续无法将该列表作为整体分析）。没有就跳过
		String firstInclinationInitCheckStr = GuavaCache.getKey(INCLINATION_LAST_CHECK_KEY);
		if (firstInclinationInitCheckStr != null) {
			InclinationDealedInit firstInclinationInitCheck = JsonUtil.string2Obj(firstInclinationInitCheckStr, InclinationDealedInit.class);
			inclinationDealedInitList = this.addElement2ListStart(firstInclinationInitCheck, inclinationDealedInitList);
		}

		// 3.第三版修正：对check数据列表进行警报检查（判断ckeck数据与对应sensorId的缓存数据是否在一定实现内（如果，对应缓存不存在，就将该Check数据直接按照prefix+sensorId的方式保存到内存中）。然后将更新缓存）。
//        // 2+.将数据列表中的最后一条数据，写入缓存
//        InclinationDealedInit inclinationDealedInitCheck2Cache = inclinationDealedInitList.get(inclinationDealedInitList.size()-1);
//        String inclinationDealedInitCheck2CacheStr = JsonUtil.obj2String(inclinationDealedInitCheck2Cache);
//        GuavaCache.setKey(INCLINATION_LAST_CHECK_KEY,inclinationDealedInitCheck2CacheStr);
//
//        // 3.对异常数据列表进行警报检查，如果有警报产生条件（目前是连续两个数据异常），就将产生的警报置入警报列表
//        // 突然在想我们貌似并不需要整个InclinationDealedInit放入数组。不过我有点懒得改了。。。之后再优化吧
//        List<Warning> incliantionInitWarningList = this.proofInclinationDealedInitCheck2Warning(inclinationDealedInitList);
//        if (incliantionInitWarningList == null){
//            return ServerResponse.createBySuccessMessage("there is no inclinationInit warning !");
//        }
		long durationLimit = INCLINATIONINIT_DURATION_BY_TWO_CHECK;
		List<Warning> incliantionInitWarningList = this.proofInclinationDealedInitCheck2Warning(inclinationDealedInitList, durationLimit);
		if (incliantionInitWarningList == null) {
			return ServerResponse.createBySuccessMessage("there is no inclinationInit warning !");
		}

		// 4.如果警报列表不为空，即对数据列表进行持久化，上传等操作
		ServerResponse insertResponse = this.insertWarningList(incliantionInitWarningList);
		if (insertResponse.isFail()) {
			return insertResponse;
		}
		// 4.2上传服务调用
		try {
			warningProducer.sendWarning(incliantionInitWarningList);
		} catch (IOException e) {
			log.info("IOException:" + e);
			return ServerResponse.createByErrorMessage("inclinationInit warning try send to MQ but fail !");
		} catch (TimeoutException e) {
			log.info("TimeoutException:" + e);
			return ServerResponse.createByErrorMessage("inclinationInit warning try send to MQ but fail !");
		} catch (InterruptedException e) {
			log.info("InterruptedException:" + e);
			return ServerResponse.createByErrorMessage("inclinationInit warning try send to MQ but fail !");
		}

		return ServerResponse.createBySuccessMessage("inclinationInit warning has dealed(persistent,uploaded.etc) !");
	}

	/**
	 * 将一个元素添加到对应列表的首位，其他元素依次后延。    // 其实这里可以通过不定项参数/可变参数扩展为多个元素添加，但是貌似阿里的工作手册并不推荐这么做，容易产生错误。而且目前也不需要，就不做了
	 *
	 * @param addElement
	 * @param originList
	 * @param <T>
	 * @return
	 */
	private <T> List<T> addElement2ListStart(T addElement, List<T> originList) {
		List<T> resultList = Lists.newArrayList();
		resultList.add(addElement);

		for (T item : originList) {
			resultList.add(item);
		}

		return resultList;
	}

	private <T> List<Warning> proofCheckData2Warning(Class<T> clazz, List<T> checkDataList) {
		return null;    // 泛型无法调用具体方法，即使每个调用的T都有getId()。（不过想想也对，否则很容易出现unCheck的异常了）
	}

	private List<Warning> proofInclinationDealedInitCheck2Warning(List<InclinationDealedInit> inclinationDealedInitList, long durationLimit) {
		List<Warning> warningList = Lists.newArrayList();

		for (int i = 0; i < inclinationDealedInitList.size(); i++) {
			// 修正判断条件：由原来判断是否ID连续，转为判断（传感器ID一致&&时间间隔duration足够小）
			ServerResponse proofInclinationInitCheckResponse = this.proofInclinationInitCheckFromCache(inclinationDealedInitList.get(i), durationLimit);
			if (proofInclinationInitCheckResponse.isSuccess()) {
				warningList.add((Warning) proofInclinationInitCheckResponse.getData());
			}
//            if (inclinationDealedInitList.get(i).getId() == inclinationDealedInitList.get(i+1).getId()){
//                Warning inclinationInitWarning = this.warningAssembleByInclinationInit(inclinationDealedInitList.get(i+1));     // 两条数据确定警报，以第二条数据为记录核心
//                warningList.add(inclinationInitWarning);
//            }
		}
		return warningList;
	}

	private ServerResponse<Warning> proofInclinationInitCheckFromCache(InclinationDealedInit inclinationDealedInit, long duraionLimit) {
		// 1.生成cacheKey，获取原有cacheValue
		StringBuilder cacheKey = new StringBuilder(INCLINATION_LAST_CHECK_PREFIX);
		cacheKey.append(inclinationDealedInit.getSensorId().toString());
		String cacheValueStr = GuavaCache.getKey(cacheKey.toString());

		// 2.更新cacheKey对应value（没有，就是新建）
		String jsonStr = JsonUtil.obj2StringPretty(inclinationDealedInit);
		GuavaCache.setKey(cacheKey.toString(), jsonStr);

		// 3.如果存在原有相关传感器的check数据，从而进一步判断其时间与现有时间的差距，从而决定是否上传警报
		if (cacheValueStr != null) {
			InclinationDealedInit firstInclinationInit = JsonUtil.string2Obj(cacheValueStr, InclinationDealedInit.class);
			ServerResponse durationProofResponse = this.proofInclinationInitsDurationInLimit(duraionLimit, firstInclinationInit, inclinationDealedInit);
			if (durationProofResponse.isSuccess()) {
				Warning incliantionInitWarning = this.warningAssembleByInclinationInit(inclinationDealedInit);
				return ServerResponse.createBySuccess(incliantionInitWarning);
			}
			// 超出时限，也当作没有警报
		}
		return ServerResponse.createByErrorMessage("No warning");
	}

	private ServerResponse proofInclinationInitsDurationInLimit(long durationLimit, InclinationDealedInit... inclinationDealedInits) {   // 这里就用可变参数。之后改为固定吧，毕竟阿里推荐不采用可变参数
		long duration = Math.abs(inclinationDealedInits[0].getCreateTime().getTime() - inclinationDealedInits[1].getCreateTime().getTime());  // 逻辑清晰，就不拆了
		if (duration < durationLimit) {
			log.info("duration:{}", new Date(duration));
			return ServerResponse.createBySuccessMessage("the duration beyond the limit of duration !");
		}
		return ServerResponse.createByErrorMessage("the duration in the limit of duration !");
	}

	private Warning warningAssembleByInclinationInit(InclinationDealedInit inclinationDealedInit) {
		Warning inclinationWarning = new Warning();
		inclinationWarning.setTerminalId(Integer.parseInt(GuavaCache.getKey(TERMINAL_ID)));

		inclinationWarning.setSensorRegisterId(inclinationDealedInit.getSensorId());   //TODO 2019.05.30   明天要完成终端服务器数据表的重构，原先的数据表在接入中控室后确实表现不佳。尤其原来的需求确实了解不足。

		inclinationWarning.setOriginId(inclinationDealedInit.getOriginId());
		inclinationWarning.setLevel(WarningConstant.WarningLevelEnum.YellowAlert.getCode());
		// status暂时只作为保留段

		inclinationWarning.setSensorType(Inclination_HCA526T.getCode());    // 将这一类配置信息也写入静态配置文件中去。    //TODO 也打算建立一个全局的Sensor数据库表，来根据对应信息来获取传感器类型（便于动态增删改查）。
		inclinationWarning.setCreateTime(new Date());
		inclinationWarning.setUpdateTime(new Date());

		return inclinationWarning;
	}
}
