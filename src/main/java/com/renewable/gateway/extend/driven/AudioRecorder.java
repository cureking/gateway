package com.renewable.gateway.extend.driven;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @Description：
 * @Author: jarry
 */
public class AudioRecorder extends JFrame {

	private static final long serialVersionUID = 1L;

	AudioFormat audioFormat;
	TargetDataLine targetDataLine;

	final JButton captureBtn = new JButton("Capture");
	final JButton stopBtn = new JButton("Stop");

	final JPanel btnPanel = new JPanel();
	final ButtonGroup btnGroup = new ButtonGroup();
	final JRadioButton aifcBtn = new JRadioButton("AIFC");
	final JRadioButton aiffBtn = new JRadioButton("AIFF");
	final JRadioButton auBtn = new JRadioButton("AU", true);
	final JRadioButton sndBtn = new JRadioButton("SND");
	final JRadioButton waveBtn = new JRadioButton("WAVE");

	public static void main(String args[]) {
		new AudioRecorder();
	}// end main

	/**
	 * Java的SWING页面建立，服务器不需要
	 */
	public AudioRecorder() {
		captureBtn.setEnabled(true);
		stopBtn.setEnabled(false);

		captureBtn.addActionListener(new ActionListener() {
										 @Override
										 public void actionPerformed(ActionEvent e) {
											 captureBtn.setEnabled(false);
											 stopBtn.setEnabled(true);
											 captureAudio();
										 }
									 }
		);

		stopBtn.addActionListener(new ActionListener() {
									  @Override
									  public void actionPerformed(ActionEvent e) {
										  captureBtn.setEnabled(true);
										  stopBtn.setEnabled(false);
										  targetDataLine.stop();
										  targetDataLine.close();
									  }
								  }
		);

		getContentPane().add(captureBtn);
		getContentPane().add(stopBtn);

		btnGroup.add(aifcBtn);
		btnGroup.add(aiffBtn);
		btnGroup.add(auBtn);
		btnGroup.add(sndBtn);
		btnGroup.add(waveBtn);

		btnPanel.add(aifcBtn);
		btnPanel.add(aiffBtn);
		btnPanel.add(auBtn);
		btnPanel.add(sndBtn);
		btnPanel.add(waveBtn);

		getContentPane().add(btnPanel);

		getContentPane().setLayout(new FlowLayout());
//		setTitle("Copyright 2003, R.G.Baldwin");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(300, 120);
		setVisible(true);
	}

	/**
	 * 开始声音抓取（新建了一个DataLine）
	 */
	private void captureAudio() {
		try {
			audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);

			new CaptureThread().start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * 对过去声音的格式进行设置（设定频率，比特位等）
	 * @return
	 */
	private AudioFormat getAudioFormat() {
		float sampleRate = 8000.0F;
		// 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;
		// 8,16
		int channels = 1;
		// 1,2
		boolean signed = true;
		// true,false
		boolean bigEndian = false;
		// true,false
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

	/**
	 * 新开一个线程，用于抓取声音数据，并在其中设置了数据保存的格式与路径
	 */
	class CaptureThread extends Thread {
		@Override
		public void run() {
			AudioFileFormat.Type fileType = null;
			File audioFile = null;

			if (aifcBtn.isSelected()) {
				fileType = AudioFileFormat.Type.AIFC;
				audioFile = new File("junk.aifc");
			} else if (aiffBtn.isSelected()) {
				fileType = AudioFileFormat.Type.AIFF;
				audioFile = new File("junk.aif");
			} else if (auBtn.isSelected()) {
				fileType = AudioFileFormat.Type.AU;
				audioFile = new File("junk.au");
			} else if (sndBtn.isSelected()) {
				fileType = AudioFileFormat.Type.SND;
				audioFile = new File("junk.snd");
			} else if (waveBtn.isSelected()) {
				fileType = AudioFileFormat.Type.WAVE;
				audioFile = new File("junk.wav");
			}

			try {
				targetDataLine.open(audioFormat);
				targetDataLine.start();
				AudioSystem.write(new AudioInputStream(targetDataLine), fileType, audioFile);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}

