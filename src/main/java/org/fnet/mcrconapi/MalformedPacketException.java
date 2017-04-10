package org.fnet.mcrconapi;

import java.io.IOException;

public class MalformedPacketException extends IOException {

	private static final long serialVersionUID = -6409356862198025733L;
	
	public MalformedPacketException(String message) {
		super(message);
	}

}
