package org.fnet.mcrconapi;

import java.io.IOException;

public class TempTestForRealServer {

	public static void main(String[] args) throws IOException, AuthenticationException {
		try (RConClient client = new RConClient("127.0.0.1", "testPassword")) {
			client.sendCommand("say hello world");
		}
	}
	
}
