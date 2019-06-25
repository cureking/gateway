package com.renewable.gateway.common.serialPoolTemp.exceptions;

/**
 * @Description：
 * @Author: jarry
 */
public class SerialConnectionException extends SerialException {
	private static final long serialVersionUID = 3878126572474819403L;

	public SerialConnectionException(String message) {
		super(message);
	}

	public SerialConnectionException(Throwable cause) {
		super(cause);
	}

	public SerialConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
}
