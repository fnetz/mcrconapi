/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 fnetworks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
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
