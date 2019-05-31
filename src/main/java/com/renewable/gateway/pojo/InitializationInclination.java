package com.renewable.gateway.pojo;

import java.util.Date;

public class InitializationInclination {
    private Integer id;

    private Integer sensorRegisterId;

    private Integer terminalId;

    private Double radius;

    private Double initH1;

    private Double initAngle1;

    private Double initH2;

    private Double initAngle2;

    private Double initH3;

    private Double initAngle3;

    private Double initH4;

    private Double initAngle4;

    private Double initTotalAngle;

    private Double initDirectAngle;

    private Double totalAngleLimit;

    private Double totalInitAngleLimit;

    private Double initX;

    private Double initY;

    private Date createTime;

    private Date updateTime;

    public InitializationInclination(Integer id, Integer sensorRegisterId, Integer terminalId, Double radius, Double initH1, Double initAngle1, Double initH2, Double initAngle2, Double initH3, Double initAngle3, Double initH4, Double initAngle4, Double initTotalAngle, Double initDirectAngle, Double totalAngleLimit, Double totalInitAngleLimit, Double initX, Double initY, Date createTime, Date updateTime) {
        this.id = id;
        this.sensorRegisterId = sensorRegisterId;
        this.terminalId = terminalId;
        this.radius = radius;
        this.initH1 = initH1;
        this.initAngle1 = initAngle1;
        this.initH2 = initH2;
        this.initAngle2 = initAngle2;
        this.initH3 = initH3;
        this.initAngle3 = initAngle3;
        this.initH4 = initH4;
        this.initAngle4 = initAngle4;
        this.initTotalAngle = initTotalAngle;
        this.initDirectAngle = initDirectAngle;
        this.totalAngleLimit = totalAngleLimit;
        this.totalInitAngleLimit = totalInitAngleLimit;
        this.initX = initX;
        this.initY = initY;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public InitializationInclination() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Double getInitH1() {
        return initH1;
    }

    public void setInitH1(Double initH1) {
        this.initH1 = initH1;
    }

    public Double getInitAngle1() {
        return initAngle1;
    }

    public void setInitAngle1(Double initAngle1) {
        this.initAngle1 = initAngle1;
    }

    public Double getInitH2() {
        return initH2;
    }

    public void setInitH2(Double initH2) {
        this.initH2 = initH2;
    }

    public Double getInitAngle2() {
        return initAngle2;
    }

    public void setInitAngle2(Double initAngle2) {
        this.initAngle2 = initAngle2;
    }

    public Double getInitH3() {
        return initH3;
    }

    public void setInitH3(Double initH3) {
        this.initH3 = initH3;
    }

    public Double getInitAngle3() {
        return initAngle3;
    }

    public void setInitAngle3(Double initAngle3) {
        this.initAngle3 = initAngle3;
    }

    public Double getInitH4() {
        return initH4;
    }

    public void setInitH4(Double initH4) {
        this.initH4 = initH4;
    }

    public Double getInitAngle4() {
        return initAngle4;
    }

    public void setInitAngle4(Double initAngle4) {
        this.initAngle4 = initAngle4;
    }

    public Double getInitTotalAngle() {
        return initTotalAngle;
    }

    public void setInitTotalAngle(Double initTotalAngle) {
        this.initTotalAngle = initTotalAngle;
    }

    public Double getInitDirectAngle() {
        return initDirectAngle;
    }

    public void setInitDirectAngle(Double initDirectAngle) {
        this.initDirectAngle = initDirectAngle;
    }

    public Double getTotalAngleLimit() {
        return totalAngleLimit;
    }

    public void setTotalAngleLimit(Double totalAngleLimit) {
        this.totalAngleLimit = totalAngleLimit;
    }

    public Double getTotalInitAngleLimit() {
        return totalInitAngleLimit;
    }

    public void setTotalInitAngleLimit(Double totalInitAngleLimit) {
        this.totalInitAngleLimit = totalInitAngleLimit;
    }

    public Double getInitX() {
        return initX;
    }

    public void setInitX(Double initX) {
        this.initX = initX;
    }

    public Double getInitY() {
        return initY;
    }

    public void setInitY(Double initY) {
        this.initY = initY;
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