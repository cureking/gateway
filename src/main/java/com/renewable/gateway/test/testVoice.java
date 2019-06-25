package com.renewable.gateway.test;

import java.io.IOException;

/**
 * @Description：
 * @Author: jarry
 */
public class testVoice {
	public static void main(String[] args) throws IOException {
		PlayerUtil playerUtil = new PlayerUtil();

		RecordUtil recordUtil = new RecordUtil();
		recordUtil.start();
		byte[] voiceByte = recordUtil.getData();
		System.out.println(voiceByte);
		recordUtil.close();
		recordUtil.finish();
	}
}
