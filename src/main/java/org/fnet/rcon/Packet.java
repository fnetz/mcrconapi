package org.fnet.rcon;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Packet {

	public static final int TYPE_LOGIN = 3;
	public static final int TYPE_AUTH_SUCCESS = 2;
	public static final int TYPE_AUTH_FAIL = -1;
	public static final int TYPE_COMMAND = 2;
	public static final int TYPE_COMMAND_RESPONSE = 0;
	
	private static final Charset PAYLOAD_CHARSET = StandardCharsets.US_ASCII;
	
	private static int packageIdCounter = 0;
	
	private int length;
	private int requestID;
	private int type;
	private byte[] payload;
	
	public Packet(int type, String payload) {
		this.type = type;
		this.requestID = packageIdCounter++;
		this.payload = payload.getBytes(PAYLOAD_CHARSET);
		this.length = Integer.BYTES * 2 + this.payload.length + Byte.BYTES * 2;
	}

	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
		this.length = Integer.BYTES * 2 + payload.length + Byte.BYTES * 2;
	}
	
	public void setPayload(String payload) {
		setPayload(payload.getBytes(PAYLOAD_CHARSET));
	}

	public int getLength() {
		return length;
	}
	
}
