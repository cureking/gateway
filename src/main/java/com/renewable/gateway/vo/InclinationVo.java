package com.renewable.gateway.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @Description：
 * @Author: jarry
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InclinationVo {
    private Long id;

    private Double angleX;

    private Double angleY;

    private Double temperature;

    private Date createTime;

}
