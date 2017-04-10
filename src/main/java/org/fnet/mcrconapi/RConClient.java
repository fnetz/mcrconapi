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

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.fnet.mcrconapi.AuthenticationException.ErrorType;

public class RConClient implements Closeable {

	public static final int DEFAULT_RCON_PORT = 25575;

	private Socket socket;
	private OutputStream outputStream;
	private InputStream inputStream;

	private boolean authenticated;

	/**
	 * Creates a connection to the rcon server and tries to authenticate using
	 * the given password
	 * 
	 * @param address
	 *            the minecraft server address
	 * @param port
	 *            the rcon port
	 * @param password
	 *            the login password
	 * @throws IOException
	 *             if the socket fails to connect or one of the streams fail to
	 *             read/write
	 * @throws AuthenticationException
	 *             if the client fails to authenticate at the server
	 */
	public RConClient(String address, int port, String password) throws IOException, AuthenticationException {
		createConnection(address, port);
		authenticate(password);
	}

	/**
	 * Creates a connection to the rcon server using the default RCON port
	 * (25575) and tries to authenticate using the given password
	 * 
	 * @param address
	 *            the minecraft server address
	 * @param password
	 *            the rcon port
	 * @throws IOException
	 *             if the socket fails to connect or one of the streams fails to
	 *             read/write
	 * @throws AuthenticationException
	 *             if the client fails to authenticate at the server
	 */
	public RConClient(String address, String password) throws IOException, AuthenticationException {
		this(address, DEFAULT_RCON_PORT, password);
	}

	/**
	 * Creates a connection to the rcon server
	 * 
	 * @param address
	 *            the minecraft server address
	 * @param port
	 *            the rcon port
	 * @throws IOException
	 *             if the socket fails to connect or one of the streams fails to
	 *             read/write
	 */
	public RConClient(String address, int port) throws IOException {
		createConnection(address, port);
	}

	/**
	 * Creates a connection to the rcon server using the default RCON port
	 * (25575)
	 * 
	 * @param address
	 *            the minecraft server address
	 * @throws IOException
	 *             if the socket fails to connect or one of the streams fails to
	 *             read/write
	 */
	public RConClient(String address) throws IOException {
		this(address, DEFAULT_RCON_PORT);
	}

	private void createConnection(String host, int port) throws IOException {
		socket = new Socket(host, port);
		outputStream = new DataOutputStream(socket.getOutputStream());
		inputStream = new DataInputStream(socket.getInputStream());
	}

	/**
	 * 
	 * @param password
	 *            the password
	 * @throws IOException
	 *             if one of the streams fails to read/write
	 * @throws AuthenticationException
	 *             if the client is already authenticated or
	 */
	public void authenticate(String password) throws IOException, AuthenticationException {
		if (authenticated)
			throw new AuthenticationException("Already authenticated", ErrorType.ALREADY_AUTHENTICATED);
		Packet loginPacket = new Packet(Packet.TYPE_LOGIN, password);
		loginPacket.writeTo(outputStream);
		Packet loginResponse = Packet.readFrom(inputStream);
		if (loginResponse.getType() != Packet.TYPE_AUTH_RESPONSE)
			throw new InvalidPacketException(
					"Packet type should be TYPE_AUTH_RESPONSE (" + Packet.TYPE_AUTH_RESPONSE + ")", loginResponse);
		if (loginResponse.getRequestID() == loginPacket.getRequestID())
			authenticated = true;
		else if (loginResponse.getRequestID() == Packet.REQUEST_ID_AUTH_FAIL)
			throw new AuthenticationException("Failed to authenticate at server"
					+ socket.getInetAddress().getHostAddress() + ", port " + socket.getPort(),
					ErrorType.WRONG_PASSWORD);
	}

	/**
	 * @return if the client is authenticated
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	public String sendCommand(String command) throws AuthenticationException, IOException {
		if (!authenticated)
			throw new AuthenticationException("Not yet authenticated", ErrorType.NOT_AUTHENTICATED);
		Packet commandPacket = new Packet(Packet.TYPE_COMMAND, command);
		commandPacket.writeTo(outputStream);
		StringBuilder builder = new StringBuilder();
		Packet lastPacket;
		while ((lastPacket = Packet.readFrom(inputStream)).getLength() == 4096) {
			if (lastPacket.getType() != Packet.TYPE_COMMAND_RESPONSE)
				throw new InvalidPacketException("Received packet of invalid type " + lastPacket.getType(), lastPacket);
			builder.append(lastPacket.getPayloadAsString());
		}
		if (lastPacket.getLength() == 10)
			throw new InvalidPacketException("Packet payload empty (this could mean an invalid command)", lastPacket);
		else
			builder.append(lastPacket.getPayloadAsString());
		return builder.toString();
	}

	/**
	 * Closes the connection to the server
	 */
	@Override
	public void close() throws IOException {
		inputStream.close();
		outputStream.close();
		socket.close();
	}

}
