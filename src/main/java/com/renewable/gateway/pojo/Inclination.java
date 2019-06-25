package com.renewable.gateway.pojo;

import java.util.Date;

public class Inclination {
	private Long id;

	private Integer sensorId;

	private Double angleX;

	private Double angleY;

	private Double angleTotal;

	private Double directAngle;

	private Double angleInitTotal;

	private Double directAngleInit;

	private Double temperature;

	private Date createTime;

	private String version;

	public Inclination(Long id, Integer sensorId, Double angleX, Double angleY, Double angleTotal, Double directAngle, Double angleInitTotal, Double directAngleInit, Double temperature, Date createTime, String version) {
		this.id = id;
		this.sensorId = sensorId;
		this.angleX = angleX;
		this.angleY = angleY;
		this.angleTotal = angleTotal;
		this.directAngle = directAngle;
		this.angleInitTotal = angleInitTotal;
		this.directAngleInit = directAngleInit;
		this.temperature = temperature;
		this.createTime = createTime;
		this.version = version;
	}

	public Inclination() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSensorId() {
		return sensorId;
	}

	public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}

	public Double getAngleX() {
		return angleX;
	}

	public void setAngleX(Double angleX) {
		this.angleX = angleX;
	}

	public Double getAngleY() {
		return angleY;
	}

	public void setAngleY(Double angleY) {
		this.angleY = angleY;
	}

	public Double getAngleTotal() {
		return angleTotal;
	}

	public void setAngleTotal(Double angleTotal) {
		this.angleTotal = angleTotal;
	}

	public Double getDirectAngle() {
		return directAngle;
	}

	public void setDirectAngle(Double directAngle) {
		this.directAngle = directAngle;
	}

	public Double getAngleInitTotal() {
		return angleInitTotal;
	}

	public void setAngleInitTotal(Double angleInitTotal) {
		this.angleInitTotal = angleInitTotal;
	}

	public Double getDirectAngleInit() {
		return directAngleInit;
	}

	public void setDirectAngleInit(Double directAngleInit) {
		this.directAngleInit = directAngleInit;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version == null ? null : version.trim();
	}
}