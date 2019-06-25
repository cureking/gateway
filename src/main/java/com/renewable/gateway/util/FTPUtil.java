package com.renewable.gateway.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class FTPUtil {

	private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
	private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
	private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

	public FTPUtil(String ip, int port, String user, String pwd) {
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.pwd = pwd;
	}

	public static boolean uploadFile(List<File> fileList) throws IOException {
		FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass);
		log.info("开始连接FTP服务器");
		//这里的异常直接抛出，置于业务层级处理。并不是所有的异常都要在越底层处理越好
		boolean result = ftpUtil.uploadFile("img", fileList);
		log.info("结束上传，上传结果：{}", result);

		return result;
	}

	private boolean uploadFile(String remotePath, List<File> fileList) throws IOException {
		boolean uploaded = true;
		FileInputStream fis = null;
		//连接FTP服务器
		if (connectServer(this.ip, this.port, this.user, this.pwd)) {
			try {
				ftpClient.changeWorkingDirectory(remotePath);
				//设置ftp连接配置
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftpClient.enterLocalPassiveMode();

				//上传文件
				for (File fileItem : fileList) {
					fis = new FileInputStream(fileItem);
					ftpClient.storeFile(fileItem.getName(), fis);
				}
			} catch (IOException e) {
				log.error("上传文件异常（切换工作目录异常）", e);
				uploaded = false;
				e.printStackTrace();
			} finally {
				//无论如何都要关闭流和ftp连接
				//这里的异常，我们直接抛出，不在此处处理
				fis.close();
				ftpClient.disconnect();
			}
		}
		return uploaded;
	}

	private boolean connectServer(String ip, int port, String user, String pwd) {
		ftpClient = new FTPClient();
		boolean isSuccess = false;
		try {
			ftpClient.connect(ip);
			isSuccess = ftpClient.login(user, pwd);
		} catch (IOException e) {
			log.error("FTP服务器连接异常", e);
		}
		return isSuccess;
	}

	private String ip;
	private int port;
	private String user;
	private String pwd;
	private FTPClient ftpClient;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
}
