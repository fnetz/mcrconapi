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
package org.fnet.mcrconapi;

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
public class Packet {

	public static final int REQUEST_ID_AUTH_FAIL = -1;

	private static final Charset PAYLOAD_CHARSET = StandardCharsets.US_ASCII;

	private static int requestIdCounter = 1;

	private int length;
	private int requestID;
	private PacketType type;
	private byte[] payload;

	private Packet() {
	}

	private static ByteBuffer getByteBuffer(InputStream stream, int length) throws IOException {
		byte[] lengthBytes = new byte[length];
		if (stream.read(lengthBytes) == -1)
			throw new EOFException();
		return ByteBuffer.wrap(lengthBytes).order(ByteOrder.LITTLE_ENDIAN);
	}

	/**
	 * Reads a package data from an {@link InputStream}
	 * 
	 * @param dataStream
	 *            the {@link InputStream} to read from
	 * @param clientside
	 *            if the packet is a request
	 * @return the resulting packet
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static Packet readFrom(InputStream dataStream, boolean clientside) throws IOException {
		Packet packet = new Packet();
		packet.length = getByteBuffer(dataStream, 4).getInt();
		if (packet.length < 10)
			throw new MalformedPacketException("Packet length lower than ten (minimum package size)");
		packet.requestID = getByteBuffer(dataStream, 4).getInt();
		packet.type = PacketType.fromID(getByteBuffer(dataStream, 4).getInt(), clientside);
		if (packet.type == null)
			throw new MalformedPacketException("Packet type is none of known packet types");
		int payloadLength = packet.length - (Integer.BYTES * 2 + Byte.BYTES * 2);
		packet.payload = new byte[payloadLength];
		for (int i = 0; i < payloadLength; i++) {
			packet.payload[i] = (byte) dataStream.read();
		}
		if (dataStream.read() != 0)
			throw new MalformedPacketException("Payload terminator byte not zero");
		if (dataStream.read() != 0)
			throw new MalformedPacketException("Packet terminator byte not zero");
		return packet;
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
