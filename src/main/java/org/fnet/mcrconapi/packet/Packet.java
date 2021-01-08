/*
 * Copyright (c) 2021 Felix Solcher
 * Licensed under the terms of the MIT license.
 */
package org.fnet.mcrconapi.packet;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A RCON packet, either sent by the server or the client.
 */
public abstract class Packet {

	public static final int REQUEST_ID_AUTH_FAIL = -1;

	private static final Charset PAYLOAD_CHARSET = StandardCharsets.US_ASCII;

	private static int requestIdCounter = 1;

	protected int length;
	protected int requestID;
	protected PacketType type;
	protected byte[] payload;

	protected static ByteBuffer getByteBuffer(InputStream stream, int length) throws IOException {
		byte[] lengthBytes = new byte[length];
		if (stream.read(lengthBytes) == -1)
			throw new EOFException();
		return ByteBuffer.wrap(lengthBytes).order(ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Generates a new packet with a type and a payload given. The length and
	 * requestID are automatically generated where the requestID is a value that
	 * is incremented on every package generation and the length is calculated.
	 * 
	 * @param type
	 *            the packet type
	 * @param payload
	 *            the payload
	 * @see Packet#setType(PacketType)
	 * @see Packet#getRequestID()
	 */
	public Packet(PacketType type, String payload) {
		this.type = type;
		this.requestID = requestIdCounter++;
		this.payload = payload.getBytes(PAYLOAD_CHARSET);
		this.length = Integer.BYTES * 2 + this.payload.length + Byte.BYTES * 2;
	}

	/**
	 * Reads a package from an {@link InputStream}
	 * 
	 * @param stream
	 *            the {@link InputStream} to read from
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public Packet(InputStream stream) throws IOException {
		readFrom(stream);
	}

	protected abstract void readFrom(InputStream stream) throws IOException;

	/**
	 * If not changed with {@link Packet#setRequestID(int)}, this returns a
	 * client-generated id (auto-increment) or, if this {@link Packet} is a
	 * response to an authorisation request, then it's the original request id
	 * or {@code -1} if the auth failed
	 * 
	 * @return the client-generated request ID or the previously set ID
	 * @see Packet#setRequestID(int)
	 * @see Packet#setType(PacketType)
	 */
	public int getRequestID() {
		return requestID;
	}

	/**
	 * Sets the {@link Packet}'s request ID
	 * 
	 * @param requestID
	 *            the request id
	 */
	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	/**
	 * Returns the Packet's type as an enum.
	 * 
	 * @return the {@link Packet}'s type
	 * @see Packet#setType(PacketType)
	 * @see <a href="http://wiki.vg/RCON">http://wiki.vg/RCON</a>
	 */
	public PacketType getType() {
		return type;
	}

	/**
	 * 
	 * 
	 * @param type
	 *            the {@link Packet}'s type
	 * @see <a href="http://wiki.vg/RCON">http://wiki.vg/RCON</a>
	 */
	public void setType(PacketType type) {
		this.type = type;
	}

	/**
	 * Gets the raw payload that is an US_ASCII encoded string
	 * 
	 * @return the payload as {@code byte[]}
	 */
	public byte[] getPayload() {
		return payload;
	}

	/**
	 * Gets the payload as string
	 * 
	 * @return the payload as {@link String}
	 */
	public String getPayloadAsString() {
		return new String(payload, PAYLOAD_CHARSET);
	}

	/**
	 * Set the payload {@code String} directly as {@code byte[]} encoded in
	 * {@code US_ASCII}
	 * 
	 * @param payload
	 *            the payload, encoded in ASCII
	 */
	public void setPayload(byte[] payload) {
		this.payload = payload;
		this.length = Integer.BYTES * 2 + payload.length + Byte.BYTES * 2;
	}

	/**
	 * Set the payload {@link String}
	 * 
	 * @param payload
	 *            the payload {@link String}
	 */
	public void setPayload(String payload) {
		setPayload(payload.getBytes(PAYLOAD_CHARSET));
	}

	/**
	 * Get the calculated {@link Packet} length
	 * 
	 * @return the {@link Packet} length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Writes the data of the packet in a raw format to the given
	 * {@link OutputStream}
	 * 
	 * @see <a href="http://wiki.vg/RCON">http://wiki.vg/RCON</a>
	 * @param outputStream
	 *            the {@link DataOutputStream} to write the {@link Packet} to
	 * @throws IOException
	 *             if an I/O error occurs at the DataOutputStream
	 */
	public void writeTo(OutputStream outputStream) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(this.length + 4).order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(length);
		buffer.putInt(requestID);
		buffer.putInt(type.getId());
		buffer.put(payload);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		outputStream.write(buffer.array());
	}

	@Override
	public String toString() {
		return String.format("Packet[length=\"%s\",requestId=\"%s\",type=\"%s\" (id: %s), payload=\"%s\"]", length,
				requestID, type, type.getId(), getPayloadAsString());
	}

}
