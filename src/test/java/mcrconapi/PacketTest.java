package mcrconapi;

import static org.junit.Assert.*;

import org.fnet.rcon.Packet;
import org.junit.Test;

public class PacketTest {

	@Test
	public void testGetLength() {
		Packet packet = new Packet(0, "Test");
		assertTrue(packet
				.getLength() == 14 /*
									 * Request ID (int) + Type (int) + Payload
									 * (byte[], 4) + 2 null bytes
									 */);
	}

	@Test
	public void testGetLengthAfterPayloadChange() {
		Packet packet = new Packet(0, "Test");
		packet.setPayload("Teststring");
		assertTrue(packet
				.getLength() == 20 /*
									 * Request ID (int) + Type (int) + Payload
									 * (byte[], 10) + 2 null bytes
									 */);
	}

	@Test
	public void testGetPayload() {
		Packet packet = new Packet(0, "Test");
		assertArrayEquals(new byte[] { 84 /* T */, 101 /* e */, 115 /* s */, 116 /* t */ }, packet.getPayload());
	}

}
