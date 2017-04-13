package org.fnet.mcrconapi.cli;

import java.util.HashMap;
import java.util.Map;

import org.fnet.mcrconapi.RConClient;

public class CommandLineMain {

	public static void main(String[] args) {
		args = new String[] { "-h" };
		Map<String, String> arguments = parseArguments(args);
		if (arguments.containsKey("help") || arguments.containsKey("h")) {
			printVersion();
			printUsage();
			return;
		} else if (arguments.containsKey("version") || arguments.containsKey("-v")) {
			printVersion();
			return;
		}
	}

	public static Map<String, String> parseArguments(String[] args) {
		final Map<String, String> map = new HashMap<>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("--")) {
				if (args.length > i + 1 && !args[i + 1].startsWith("--")) {
					map.put(args[i].substring(2), args[i + 1]);
				} else {
					map.put(args[i].substring(2), null);
				}
			} else if (args[i].startsWith("-")) {
				if (args.length > i + 1 && !args[i + 1].startsWith("-")) {
					String keyname = args[i].substring(1);
					if (keyname.length() > 1) {
						for (int j = 0; j < keyname.length() - 1; j++) {
							map.put(keyname.substring(j, j + 1), null);
						}
						map.put(keyname.substring(keyname.length() - 1, keyname.length()), args[i + 1]);
					} else {
						map.put(keyname, args[i + 1]);
					}
				} else {
					String keyname = args[i].substring(1);
					if (keyname.length() > 1) {
						for (int j = 0; j < keyname.length(); j++) {
							map.put(keyname.substring(j, j + 1), null);
						}
						map.put(keyname.substring(keyname.length() - 1, keyname.length()), null);
					} else {
						map.put(keyname, null);
					}
				}
			}
		}
		return map;
	}

	public static void printVersion() {
		System.out.println("MCRCONAPI v" + RConClient.API_VERSION);
		System.out.println("Copyright (c) 2017 fnetworks");
	}

	public static void printUsage() {
		System.out.println("Parameters: ");
		System.out.println("    --host           | -a <address>  : Specify the host address");
		System.out.println("    --login          | -l <password> : Login at the server with the given password");
		System.out.println("    --help           | -h            : Show this help message");
		System.out.println("    --version        | -v            : Prints version information");
		System.out.println("    --noninteractive | -n            : "
				+ "Non-Interactive mode (exit instead of asking for missing information and commands, "
				+ "default is interactive mode)");
		System.out.println("    --commands       | -c <commands> : "
				+ "Comma separated list of commands that should be sent to the server");
	}

}
