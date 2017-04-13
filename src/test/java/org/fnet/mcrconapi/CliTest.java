package org.fnet.mcrconapi;

import java.util.Map;

import org.fnet.mcrconapi.cli.CommandLineMain;
import org.junit.Test;

import static org.junit.Assert.*;

public class CliTest {

	@Test
	public void testParseArguments() {
		final String cmdstring = "42", loginstring = "testpassword";
		
		Map<String, String> args = CommandLineMain
				.parseArguments(new String[] { "--help", "--login", loginstring, "-cmd", cmdstring });
		
		assertTrue(args.containsKey("help"));
		assertTrue(args.containsKey("c"));
		assertTrue(args.containsKey("m"));
		assertTrue(args.containsKey("d"));
		assertTrue(args.containsKey("login"));

		assertEquals(cmdstring, args.get("d"));
		assertEquals(loginstring, args.get("login"));
	}

}
