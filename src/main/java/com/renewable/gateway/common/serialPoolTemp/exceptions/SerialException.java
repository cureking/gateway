package com.renewable.gateway.common.serialPoolTemp.exceptions;

/**
 * @Description：
 * @Author: jarry
 */
public class SerialException extends RuntimeException {
	private static final long serialVersionUID = -2946266495682282677L;

	public SerialException(String message) {
		super(message);
	}

	public SerialException(Throwable e) {
		super(e);
	}

	public SerialException(String message, Throwable cause) {
		super(message, cause);
	}
}
