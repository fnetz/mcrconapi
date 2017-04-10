package org.fnet.mcrconapi;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RConClientTest {

	private static final String PASSWORD = "rbfH3x";
	private ServerSocket socket;
	private Thread listenerThread;
	private Socket client;
	private static final String SHORT_COMMAND_RESPONSE = "shortresponse";
	private static final String SHORT_COMMAND_REQUEST = "shortrequest";

	@Before
	public void setUp() throws Exception {
		socket = new ServerSocket(25575);
		listenerThread = new Thread(() -> {
			try {
				client = socket.accept();
				try (DataInputStream input = new DataInputStream(client.getInputStream());
						DataOutputStream output = new DataOutputStream(client.getOutputStream())) {
					while (client.isConnected()) {
						Packet packet = Packet.readFrom(input);
						switch (packet.getType()) {
						case 3:
							Packet response = new Packet(2, "");
							if (packet.getPayloadAsString().equals(PASSWORD)) {
								response.setRequestID(packet.getRequestID());
							} else {
								response.setRequestID(-1);
							}
							response.writeTo(output);
							break;
						case 2:
							if (packet.getPayloadAsString().equals(SHORT_COMMAND_REQUEST)) {
								Packet crsp = new Packet(0, SHORT_COMMAND_RESPONSE);
								crsp.writeTo(output);
							} else {
								Packet crsp = new Packet(0, "");
								crsp.writeTo(output);
							}
							break;
						}
					}
				} catch (EOFException | SocketException e) {
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		listenerThread.start();
	}

	@After
	public void tearDown() throws Exception {
		client.close();
		socket.close();
		listenerThread.join();
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

	@Test(expected = InvalidPacketException.class)
	public void testSendCommandThrowsExceptionOnUnknownCommand() throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1")) {
			client.authenticate(PASSWORD);
			client.sendCommand("unknownCommand");
		}
	}

}
