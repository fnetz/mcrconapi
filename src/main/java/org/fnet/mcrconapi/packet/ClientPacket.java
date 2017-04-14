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
package org.fnet.mcrconapi.packet;

import java.io.IOException;
import java.io.InputStream;

import org.fnet.mcrconapi.MalformedPacketException;

/**
 * A RCON packet sent by the client.
 */
public class ClientPacket extends Packet {

	public ClientPacket(PacketType type, String payload) {
		super(type, payload);
	}

	public ClientPacket(InputStream stream) throws IOException {
		super(stream);
	}

	@Override
	protected void readFrom(InputStream stream) throws IOException {
		length = getByteBuffer(stream, 4).getInt();
		if (length < 10)
			throw new MalformedPacketException("Packet length lower than ten (minimum package size)");
		requestID = getByteBuffer(stream, 4).getInt();
		type = PacketType.fromID(getByteBuffer(stream, 4).getInt(), true);
		if (type == null)
			throw new MalformedPacketException("Packet type is none of known packet types");
		int payloadLength = length - (Integer.BYTES * 2 + Byte.BYTES * 2);
		payload = new byte[payloadLength];
		for (int i = 0; i < payloadLength; i++) {
			payload[i] = (byte) stream.read();
		}
		if (stream.read() != 0)
			throw new MalformedPacketException("Payload terminator byte not zero");
		if (stream.read() != 0)
			throw new MalformedPacketException("Packet terminator byte not zero");
	}

}
