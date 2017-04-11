package org.fnet.mcrconapi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class RConClientTest {

	static final String PASSWORD = "rbfH3x";
	static final String SHORT_COMMAND_RESPONSE = "shortresponse";
	static final String SHORT_COMMAND_REQUEST = "shortrequest";
	private RConSingleClientTestServer rConTestServer;
	
	@Rule
	public Timeout globalTimeout = new Timeout(2, TimeUnit.SECONDS);

	@Before
	public void setUp() throws Exception {
		rConTestServer = new RConSingleClientTestServer();
		rConTestServer.start();
	}
	
	@After
	public void tearDown() throws Exception {
		rConTestServer.close();
		rConTestServer = null;
	}

	@Test
	public void testAuthenticate() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1")) {
			client.authenticate(PASSWORD);
			assertTrue(client.isAuthenticated());
		}
	}

	@Test
	public void testAuthenticateInConstructor() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1", PASSWORD)) {
			assertTrue(client.isAuthenticated());
		}
	}

	@Test(expected = AuthenticationException.class)
	public void testAuthenticateThrowsExceptionOnWrongPassword() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1")) {
			client.authenticate("wrongPassword");
		}
	}

	@Test
	public void testSendCommand() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1")) {
			client.authenticate(PASSWORD);
			assertEquals(SHORT_COMMAND_RESPONSE, client.sendCommand(SHORT_COMMAND_REQUEST));
		}
	}

	@Test(expected = InvalidPacketException.class, timeout = 0)
	public void testSendCommandThrowsExceptionOnUnknownCommand() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1")) {
			client.authenticate(PASSWORD);
			client.sendCommand("unknownCommand");
		}
	}

}
