package org.fnet.mcrconapi;

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = -7310707583704316944L;
	private ErrorType type;

	public enum ErrorType {
		WRONG_PASSWORD, ALREADY_AUTHENTICATED
	}

	public AuthenticationException(String message, ErrorType type) {
		super(message);
		this.type = type;
	}

	public ErrorType getType() {
		return type;
	}

}
