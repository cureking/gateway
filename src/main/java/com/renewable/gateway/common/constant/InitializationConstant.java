package com.renewable.gateway.common.constant;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class InitializationConstant {

	// 初始配置数据的状态类型
	public enum InitializationStatusEnum {

		/**
		 * 终端服务器自动注册时，产生的初始化数据条目，不具有具体测量值等，故无法使用计算
		 */
		INITIALIZATION(0, "该初始配置处于初始化状态，无法使用"),
		/**
		 * 部署人员输入相关初始数据后，才可以进行计算
		 */
		USE(1, "正常使用状态"),
		NEED_COMPLETE(2, "该初始配置的配置信息未补全，无法使用");



		private Integer code;
		private String value;

		InitializationStatusEnum(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public int getCode() {
			return code;
		}

		public static InitializationStatusEnum codeOf(int code) {
			for (InitializationStatusEnum initializationStatusEnum : values()) {
				if (initializationStatusEnum.getCode() == code) {
					return initializationStatusEnum;
				}
			}
			log.warn("状态没有找到对应的枚举", code);
			throw new RuntimeException("没有找到对应的状态枚举");    //这里由于是在java程序内，无页面响应。故将相关信息导入到日志中。这里抛出的异常由调用方处理
		}
	}

}
