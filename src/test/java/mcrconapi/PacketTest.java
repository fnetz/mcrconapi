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
package mcrconapi;

import static org.junit.Assert.*;

import org.fnet.rcon.Packet;
import org.junit.Test;

public class PacketTest {

	/*
	 * Note about packet lengths: Request ID (int) + Type (int) + Payload
	 * (byte[], 10) + 2 null bytes
	 */

	@Test
	public void testGetLength() {
		Packet packet = new Packet(0, "Test");
		assertTrue(packet.getLength() == 14);
	}

	@Test
	public void testGetLengthAfterPayloadChange() {
		Packet packet = new Packet(0, "Test");
		packet.setPayload("Teststring");
		assertTrue(packet.getLength() == 20);
	}

	@Test
	public void testGetPayload() {
		Packet packet = new Packet(0, "Test");
		assertArrayEquals(new byte[] { 84 /* T */, 101 /* e */, 115 /* s */, 116 /* t */ }, packet.getPayload());
	}

}
