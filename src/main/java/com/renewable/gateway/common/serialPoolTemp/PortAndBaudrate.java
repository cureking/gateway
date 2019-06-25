package com.renewable.gateway.common.serialPoolTemp;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
@AllArgsConstructor
@Getter
@EqualsAndHashCode
//类使用@ToString注解，Lombok会生成一个toString()方法，默认情况下，会输出所有属性（会按照属性定义顺序），用逗号来分割。
@ToString
//  refer to redis.clients.jedis.HostAndPort
public class PortAndBaudrate implements Serializable {
	private static final long serialVersionUID = -519876229978427751L;


	private String port;
	private int baudrate;


	public static String[] extractParts(String from) {
		int idx = from.lastIndexOf(",");
		String host = idx != -1 ? from.substring(0, idx) : from;
		String port = idx != -1 ? from.substring(idx + 1) : "";
		return new String[]{host, port};
	}


	public static PortAndBaudrate parseString(String from) {

		try {
			String[] parts = extractParts(from);
			String port = parts[0];
			int baudrate = Integer.valueOf(parts[1]);
			return new PortAndBaudrate(port, baudrate);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(ex);
		}
	}


}
