package org.fnet.mcrconapi;

import java.io.IOException;

public class InvalidPacketException extends IOException {

	private static final long serialVersionUID = 7359011692721587596L;

	public InvalidPacketException(String message) {
		super(message);
	}

}
