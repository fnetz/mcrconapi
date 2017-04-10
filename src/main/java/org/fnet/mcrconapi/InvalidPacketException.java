package org.fnet.mcrconapi;

import java.io.IOException;

public class InvalidPacketException extends IOException {

	private static final long serialVersionUID = 7359011692721587596L;
	private Packet invalidPacket;

	public InvalidPacketException(String message, Packet invalidPacket) {
		super(message);
		this.invalidPacket = invalidPacket;
	}

	public Packet getInvalidPacket() {
		return invalidPacket;
	}

}
