package com.renewable.gateway.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SensorRegister {
    private Integer id;

    private String nickName;

    private String port;

    private String address;

    private Integer type;

    private Integer model;

    private Integer zero;

    private Integer baudrate;

    private Byte cleanType;

    private String cleanKey;

    private Long cleanInterval;

    private Long cleanLastId;

    private String remake;

    private Date createTime;

    private Date updateTime;

}