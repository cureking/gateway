package com.renewable.gateway.bo;

import lombok.*;

/**
 * @Description：
 * @Author: jarry
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OriginDataAddtion<T> {

    String port;
    int addres;
    String sensor;
    int baudrate;

    T data;
}
