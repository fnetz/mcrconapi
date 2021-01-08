/*
 * Copyright (c) 2021 Felix Solcher
 * Licensed under the terms of the MIT license.
 */
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
