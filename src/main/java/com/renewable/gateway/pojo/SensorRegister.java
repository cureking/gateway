package com.renewable.gateway.pojo;

import java.util.Date;

public class SensorRegister {
	private Integer id;

	private Integer terminalId;

	private Integer sensorId;

	private String nickname;

	private Byte status;

	private Date createTime;

	private Date updateTime;

	public SensorRegister(Integer id, Integer terminalId, Integer sensorId, String nickname, Byte status, Date createTime, Date updateTime) {
		this.id = id;
		this.terminalId = terminalId;
		this.sensorId = sensorId;
		this.nickname = nickname;
		this.status = status;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public SensorRegister() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(Integer terminalId) {
		this.terminalId = terminalId;
	}

	public Integer getSensorId() {
		return sensorId;
	}

	public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname == null ? null : nickname.trim();
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}