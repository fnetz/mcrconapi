/*
 * Copyright (c) 2021 Felix Solcher
 * Licensed under the terms of the MIT license.
 */
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

		assertTrue("Arguments don't contain key 'help'", args.containsKey("help"));
		assertTrue("Arguments don't contain key 'c'", args.containsKey("c"));
		assertTrue("Arguments don't contain key 'm'", args.containsKey("m"));
		assertTrue("Arguments don't contain key 'd'", args.containsKey("d"));
		assertTrue("Arguments don't contain key 'login'", args.containsKey("login"));

		assertFalse("Arguments contain key '" + loginstring + "' which should be a value", args.containsKey(loginstring));
		assertTrue("Arguments don't contain value " + loginstring, args.containsValue(loginstring));
		assertFalse("Arguments contain key '" + cmdstring + "' which should be a value", args.containsKey(cmdstring));
		assertTrue("Arguments don't contain value " + cmdstring, args.containsValue(cmdstring));

		assertEquals("Key 'd' is not mapped to " + cmdstring, cmdstring, args.get("d"));
		assertEquals("Key 'login' is not mapped to " + loginstring, loginstring, args.get("login"));
	}

	@Test
	public void testParseArguments2() {
		Map<String, String> args = CommandLineMain.parseArguments(new String[] { "--show", "b", "--test", "-xyz" });

		assertTrue("Arguments don't contain key 'show'", args.containsKey("show"));
		assertTrue("Arguments don't contain key 'test'", args.containsKey("test"));
		assertTrue("Arguments don't contain key 'x'", args.containsKey("x"));
		assertTrue("Arguments don't contain key 'y'", args.containsKey("y"));
		assertTrue("Arguments don't contain key 'z'", args.containsKey("z"));

		assertFalse("Arguments contain key 'b' which should be a value", args.containsKey("b"));
		assertTrue("Arguments don't contain value 'b'", args.containsValue("b"));
		
		assertEquals("Key 'show' is not mapped to 'b'", "b", args.get("show"));
	}

}
