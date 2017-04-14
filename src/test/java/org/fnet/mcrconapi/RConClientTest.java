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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class RConClientTest {

	static final String PASSWORD = "rbfH3x";
	static final String SHORT_COMMAND_RESPONSE = "shortresponse";
	static final String SHORT_COMMAND_REQUEST = "shortrequest";
	private static RConTestServer rConTestServer;
	private static int rconPort;
	
	@Rule
	public Timeout globalTimeout = new Timeout(2, TimeUnit.SECONDS);

	@BeforeClass
	public static void setUp() throws Exception {
		rConTestServer = new RConTestServer();
		rConTestServer.start();
		rconPort = rConTestServer.getPort();
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		rConTestServer.close();
		rConTestServer = null;
	}

	@Test
	public void testAuthenticate() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1", rconPort)) {
			client.authenticate(PASSWORD);
			assertTrue(client.isAuthenticated());
		}
	}

	@Test
	public void testAuthenticateInConstructor() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1", rconPort, PASSWORD)) {
			assertTrue(client.isAuthenticated());
		}
	}

	@Test(expected = AuthenticationException.class)
	public void testAuthenticateThrowsExceptionOnWrongPassword() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1", rconPort)) {
			client.authenticate("wrongPassword");
		}
	}

	@Test
	public void testSendCommand() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1", rconPort)) {
			client.authenticate(PASSWORD);
			assertEquals(SHORT_COMMAND_RESPONSE, client.sendCommand(SHORT_COMMAND_REQUEST));
		}
	}

	@Test(expected = InvalidPacketException.class)
	public void testSendCommandThrowsExceptionOnUnknownCommand() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1", rconPort)) {
			client.authenticate(PASSWORD);
			client.sendCommand("unknownCommand");
		}
	}

}
