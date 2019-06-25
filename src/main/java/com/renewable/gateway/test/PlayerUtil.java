package com.renewable.gateway.test;

/**
 * @Description：
 * @Author: jarry
 */

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayOutputStream;

/**
 * 用于播放声音的工具类
 *
 * @author suren
 * @date 2015年8月23日 下午7:04:22
 */
public class PlayerUtil {
	private SourceDataLine dataLine;

	public void init() throws LineUnavailableException {
		dataLine = AudioSystem.getSourceDataLine(null);
		dataLine.open();
		dataLine.start();
	}

	public void play(ByteArrayOutputStream byteStream) {
		if (byteStream == null) {
			System.err.println("byteStream is null");
			return;
		}

		byte[] data = byteStream.toByteArray();
		byteStream.reset();

		play(data);
	}

	public void play(byte[] data) {
		if (data == null) {
			return;
		}

		play(data, 0, data.length);
	}

	public void play(byte[] data, int off, int length) {
		if (dataLine.isOpen()) {
			dataLine.write(data, off, length);
		}
	}

	public void stop() {
		dataLine.close();
	}
}