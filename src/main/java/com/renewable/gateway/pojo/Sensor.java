package com.renewable.gateway.pojo;

import java.util.Date;

public class Sensor {
	private Integer id;

	private Byte functionType;

	private String modelType;

	private String manufacturer;

	private Byte state;

	private String mark;

	private Date createTime;

	private Date updateTime;

	public Sensor(Integer id, Byte functionType, String modelType, String manufacturer, Byte state, String mark, Date createTime, Date updateTime) {
		this.id = id;
		this.functionType = functionType;
		this.modelType = modelType;
		this.manufacturer = manufacturer;
		this.state = state;
		this.mark = mark;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}

	public Sensor() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Byte getFunctionType() {
		return functionType;
	}

	public void setFunctionType(Byte functionType) {
		this.functionType = functionType;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType == null ? null : modelType.trim();
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer == null ? null : manufacturer.trim();
	}

	public Byte getState() {
		return state;
	}

	public void setState(Byte state) {
		this.state = state;
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