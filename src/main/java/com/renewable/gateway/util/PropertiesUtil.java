package com.renewable.gateway.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @Description：
 * @Author: jarry
 * @Date: 12/25/2018 15:53
 */
@Slf4j
public class PropertiesUtil {

	private static Properties props;

	//要在tomcat启动时，读取到其中的配置  //7-3
	static {
		String filename = "gateway.properties";
		props = new Properties();
		try {
			props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(filename), "UTF-8"));
		} catch (Exception e) {
			log.error("配置文件读取异常", e);
		}

	}


	public static String getProperty(String key) {
		//trim()消去空格，避免空格误事
		String value = props.getProperty(key.trim());
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return value.trim();
	}

	//进行方法重载，便于业务的不同需求
	public static String getProperty(String key, String defaultValue) {
		//trim()消去空格，避免空格误事
		String value = props.getProperty(key.trim());
		if (StringUtils.isBlank(value)) {
			value = defaultValue;
		}
		return value.trim();
	}


//    // 执行顺序： 静态代码块 > 普通代码块 > 构造代码块
//    //静态代码块。在类加载时执行，且只执行一次。常用于初始化静态变量
//    static{
//
//    }
//    //普通代码块
//    {
//
//    }
//    //构造代码块
//    public PropertiesUtil(){
//
//    }
}
