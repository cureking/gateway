package com.renewable.gateway.pojo;

import java.util.Date;

public class SerialSensor {
    private Short id;

    private Integer sensorRegisterId;

    private Integer terminalId;

    private String port;

    private String address;

    private Integer baudrate;

    private Boolean model;

    private Boolean zero;

    private String mark;

    private Date createTime;

    private Date updateTime;

    public SerialSensor(Short id, Integer sensorRegisterId, Integer terminalId, String port, String address, Integer baudrate, Boolean model, Boolean zero, String mark, Date createTime, Date updateTime) {
        this.id = id;
        this.sensorRegisterId = sensorRegisterId;
        this.terminalId = terminalId;
        this.port = port;
        this.address = address;
        this.baudrate = baudrate;
        this.model = model;
        this.zero = zero;
        this.mark = mark;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public SerialSensor() {
        super();
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Integer getSensorRegisterId() {
        return sensorRegisterId;
    }

    public void setSensorRegisterId(Integer sensorRegisterId) {
        this.sensorRegisterId = sensorRegisterId;
    }

    public Integer getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Integer terminalId) {
        this.terminalId = terminalId;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port == null ? null : port.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public Integer getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(Integer baudrate) {
        this.baudrate = baudrate;
    }

    public Boolean getModel() {
        return model;
    }

    public void setModel(Boolean model) {
        this.model = model;
    }

    public Boolean getZero() {
        return zero;
    }

    public void setZero(Boolean zero) {
        this.zero = zero;
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