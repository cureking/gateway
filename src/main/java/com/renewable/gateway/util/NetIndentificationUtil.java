package com.renewable.gateway.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @Description：
 * @Author: jarry
 */
public class NetIndentificationUtil {

	public static String getLocalIP() {
		String localIP = "";
		InetAddress addr = null;
		try {
			addr = (InetAddress) InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//获取本机IP
		localIP = addr.getHostAddress().toString();
		return localIP;
	}

	public static String getLocalMac() {
		InetAddress addr = null;
		try {
			addr = (InetAddress) InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//获取网卡，获取地址
		byte[] macArray = new byte[0];
		try {
			macArray = NetworkInterface.getByInetAddress(addr).getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		StringBuffer macString = new StringBuffer("");
		for (int i = 0; i < macArray.length; i++) {
			if (i != 0) {
				macString.append("-");
			}
			//字节转换为整数
			int temp = macArray[i] & 0xff;
			String str = Integer.toHexString(temp);
			if (str.length() == 1) {
				macString.append("0" + str);
			} else {
				macString.append(str);
			}
		}
		return macString.toString().toUpperCase();
	}

	public static void main(String[] args) {
		System.out.println(getLocalIP());
		System.out.println(getLocalMac());
	}
}
