/*
 * Copyright (c) 2021 Felix Solcher
 * Licensed under the terms of the MIT license.
 */
package org.fnet.mcrconapi;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fnet.mcrconapi.packet.ClientPacket;
import org.fnet.mcrconapi.packet.Packet;
import org.fnet.mcrconapi.packet.PacketType;
import org.fnet.mcrconapi.packet.ServerPacket;
import org.junit.Test;

public class PacketTest {

	/*
	 * Note about packet lengths: Request ID (int) + Type (int) + Payload
	 * (byte[], 10) + 2 null bytes
	 */

	@Test
	public void testGetLength() {
		Packet packet = new ClientPacket(PacketType.AUTH, "Test");
		assertEquals(14, packet.getLength());
	}

	@Test
	public void testGetLengthAfterPayloadChange() {
		Packet packet = new ClientPacket(PacketType.AUTH, "Test");
		packet.setPayload("Teststring");
		assertEquals(20, packet.getLength());
	}

	@Test
	public void testGetPayload() {
		Packet packet = new ClientPacket(PacketType.AUTH, "Test");
		assertArrayEquals(new byte[] { 84 /* T */, 101 /* e */, 115 /* s */, 116 /* t */ }, packet.getPayload());
	}

	@Test
	public void testWriteTo() throws IOException {
		Packet packet = new ClientPacket(PacketType.AUTH, "Test");
		packet.setRequestID(1);
		try (ByteArrayOutputStream arrayStream = new ByteArrayOutputStream(packet.getLength())) {
			packet.writeTo(arrayStream);
			assertArrayEquals(new byte[] { 14, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 84, 101, 115, 116, 0, 0 },
					arrayStream.toByteArray());
		}
	}

	@Test
	public void testWriteToNoPayload() throws IOException {
		Packet packet = new ClientPacket(PacketType.AUTH, "");
		// Set the request id so we don't have to get the current requestId
		// counter value with reflections
		packet.setRequestID(1);
		try (ByteArrayOutputStream arrayStream = new ByteArrayOutputStream(packet.getLength());
				DataOutputStream dataStream = new DataOutputStream(arrayStream)) {
			packet.writeTo(dataStream);
			assertArrayEquals(new byte[] { 10, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 0, 0 },
					arrayStream.toByteArray());
		}
	}

	@Test
	public void testAsciiEncoding() {
		Packet packet = new ClientPacket(PacketType.AUTH, "üa");
		assertNotEquals('ü', packet.getPayloadAsString().charAt(0));
		assertEquals('a', packet.getPayloadAsString().charAt(1));
	}

	@Test
	public void testReadFrom() throws IOException {
		byte[] packetData = new byte[] { 14, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 84, 101, 115, 116, 0, 0 };
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(packetData);
				DataInputStream dataStream = new DataInputStream(inputStream)) {
			Packet packet = new ServerPacket(dataStream);
			assertEquals(14, packet.getLength());
			assertEquals(1, packet.getRequestID());
			assertEquals(PacketType.COMMAND_RESPONSE, packet.getType());
			assertEquals("Test", packet.getPayloadAsString());
		}
	}

	@Test(expected = MalformedPacketException.class)
	public void testReadFromThrowsExceptionOnWrongType() throws IOException {
		byte[] packetData = new byte[] { 14, 0, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 84, 101, 115, 116, 0, 0 };
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(packetData);
				DataInputStream dataStream = new DataInputStream(inputStream)) {
			new ServerPacket(dataStream);
		}
	}
	
	@Test(expected = MalformedPacketException.class)
	public void testReadFromThrowsExceptionIfLengthTooShort() throws IOException {
		byte[] packetData = new byte[] { 13, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 84, 101, 115, 116, 0, 0 };
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(packetData);
				DataInputStream dataStream = new DataInputStream(inputStream)) {
			new ServerPacket(dataStream);
		}
	}

	@Test(expected = MalformedPacketException.class)
	public void testReadFromThrowsExceptionIfLengthTooShort2() throws IOException {
		byte[] packetData = new byte[] { 9, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 84, 101, 115, 116, 0, 0 };
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(packetData);
				DataInputStream dataStream = new DataInputStream(inputStream)) {
			new ServerPacket(dataStream);
		}
	}
	
	@Test(expected = IOException.class)
	public void testReadFromThrowsExceptionIfLengthTooLong() throws IOException {
		byte[] packetData = new byte[] { 15, 0, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 84, 101, 115, 116, 0, 0 };
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(packetData);
				DataInputStream dataStream = new DataInputStream(inputStream)) {
			new ServerPacket(dataStream);
		}
	}

}
