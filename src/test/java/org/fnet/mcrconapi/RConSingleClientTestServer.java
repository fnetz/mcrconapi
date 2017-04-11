package org.fnet.mcrconapi;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

final class RConSingleClientTestServer implements Runnable, AutoCloseable {

	private Socket client;
	private ServerSocket server;
	private Thread serverThread;

	public RConSingleClientTestServer() {
		serverThread = new Thread(this);
	}

	@Override
	public void run() {
		try {
			server = new ServerSocket(25575);
			client = server.accept();
			while (server.isBound() && client.isConnected()) {
				Packet packet = Packet.readFrom(client.getInputStream(), true);
				switch (packet.getType()) {
				case AUTH:
					Packet response = new Packet(PacketType.AUTH_RESPONSE, "");
					if (packet.getPayloadAsString().equals(RConClientTest.PASSWORD)) {
						response.setRequestID(packet.getRequestID());
					} else {
						response.setRequestID(-1);
					}
					response.writeTo(client.getOutputStream());
					break;
				case COMMAND:
					if (packet.getPayloadAsString().equals(RConClientTest.SHORT_COMMAND_REQUEST)) {
						Packet crsp = new Packet(PacketType.COMMAND_RESPONSE, RConClientTest.SHORT_COMMAND_RESPONSE);
						crsp.writeTo(client.getOutputStream());
					} else {
						Packet crsp = new Packet(PacketType.COMMAND_RESPONSE, "");
						crsp.writeTo(client.getOutputStream());
					}
					break;
				default:
					System.err.println("INVALID REQUEST TYPE " + packet.getType());
					Packet errrsp = new Packet(PacketType.AUTH_RESPONSE, "");
					errrsp.setRequestID(Packet.REQUEST_ID_AUTH_FAIL);
					errrsp.writeTo(client.getOutputStream());
					break;
				}
			}
		} catch (EOFException e) {
			// Ignore: this means that client closed connection
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		serverThread.start();
	}

	@Override
	public void close() throws Exception {
		if (!client.isClosed())
			client.close();
		if (!server.isClosed())
			server.close();
		serverThread.join();
	}
}