package com.renewable.gateway.pojo;

import java.util.Date;

public class Warning {
	private Integer id;

	private Integer terminalId;

	private Integer sensorRegisterId;

	private Long originId;

	private Integer level;

	private Integer status;

	private Integer sensorType;

	private String mark;

	private Date createTime;

	private Date updateTime;

	public Warning(Integer id, Integer terminalId, Integer sensorRegisterId, Long originId, Integer level, Integer status, Integer sensorType, String mark, Date createTime, Date updateTime) {
		this.id = id;
		this.terminalId = terminalId;
		this.sensorRegisterId = sensorRegisterId;
		this.originId = originId;
		this.level = level;
		this.status = status;
		this.sensorType = sensorType;
		this.mark = mark;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Warning() {
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

	public Integer getSensorRegisterId() {
		return sensorRegisterId;
	}

	public void setSensorRegisterId(Integer sensorRegisterId) {
		this.sensorRegisterId = sensorRegisterId;
	}

	public Long getOriginId() {
		return originId;
	}

	public void setOriginId(Long originId) {
		this.originId = originId;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSensorType() {
		return sensorType;
	}

	public void setSensorType(Integer sensorType) {
		this.sensorType = sensorType;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark == null ? null : mark.trim();
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