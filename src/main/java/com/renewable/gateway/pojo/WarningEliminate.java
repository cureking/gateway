package com.renewable.gateway.pojo;

import java.util.Date;

public class WarningEliminate {
	private Integer id;

	private Integer warningId;

	private Integer userId;

	private Integer eliminateWay;

	private String mark;

	private Integer terminalId;

	private Integer sensorType;

	private Date createTime;

	private Date updateTime;

	public WarningEliminate(Integer id, Integer warningId, Integer userId, Integer eliminateWay, String mark, Integer terminalId, Integer sensorType, Date createTime, Date updateTime) {
		this.id = id;
		this.warningId = warningId;
		this.userId = userId;
		this.eliminateWay = eliminateWay;
		this.mark = mark;
		this.terminalId = terminalId;
		this.sensorType = sensorType;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public WarningEliminate() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getWarningId() {
		return warningId;
	}

	public void setWarningId(Integer warningId) {
		this.warningId = warningId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getEliminateWay() {
		return eliminateWay;
	}

	public void setEliminateWay(Integer eliminateWay) {
		this.eliminateWay = eliminateWay;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark == null ? null : mark.trim();
	}

	public Integer getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(Integer terminalId) {
		this.terminalId = terminalId;
	}

	public Integer getSensorType() {
		return sensorType;
	}

	public void setSensorType(Integer sensorType) {
		this.sensorType = sensorType;
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