package org.fnet.mcrconapi.cli;

import java.io.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.fnet.mcrconapi.AuthenticationException;
import org.fnet.mcrconapi.RConClient;

public class CommandLineMain {

	public static void main(String[] args) {
		Map<String, String> arguments = parseArguments(args);
		if (arguments.containsKey("help") || arguments.containsKey("h")) {
			printVersion();
			printUsage();
			return;
		} else if (arguments.containsKey("version") || arguments.containsKey("-v")) {
			printVersion();
			return;
		}
		String host = arguments.containsKey("host") ? arguments.get("host") : arguments.get("a");
		boolean interactive = !arguments.containsKey("noninteractive") && !arguments.containsKey("n");
		String password = arguments.containsKey("login") ? arguments.get("login") : arguments.get("l");
		String command = arguments.containsKey("command") ? arguments.get("command") : arguments.get("c");

		try (Scanner sc = new Scanner(System.in)) {
			if (host == null) {
				if (interactive) {
					System.out.println("Enter host address: ");
					host = sc.nextLine();
				} else {
					System.err.println("Need host address");
					System.exit(1);
				}
			}
			if (password == null) {
				if (interactive) {
					System.out.println("Enter password: ");
					Console console = System.console();
					if (console != null) {
						password = new String(console.readPassword());
					} else {
						password = sc.nextLine();
					}
				} else {
					System.err.println("Need password");
					System.exit(1);
				}
			}
			if (command == null) {
				if (interactive) {
					System.out.println("Enter command to send: ");
					command = sc.nextLine();
				} else {
					System.err.println("Need command");
					System.exit(1);
				}
			}
		}

		RConClient client;
		try {
			client = new RConClient(host);
		} catch (IOException e) {
			System.err.println("An exception occured while connecting to the server: ");
			e.printStackTrace(System.err);
			System.exit(1);
			return;
		}
		try {
			client.authenticate(password);
		} catch (IOException | AuthenticationException e) {
			System.err.println("An exception occured while authenticating: ");
			e.printStackTrace(System.err);
			try {
				client.close();
			} catch (IOException ex) {
				System.err.println("Additionaly, an exception occured while closing the client: ");
				ex.printStackTrace(System.err);
				System.exit(1);
				return;
			}
			System.exit(1);
			return;
		}
		try {
			System.out.println(client.sendCommand(command));
		} catch (AuthenticationException | IOException e) {
			System.err.println("An exception occured while sending command: ");
			e.printStackTrace(System.err);
			try {
				client.close();
			} catch (IOException ex) {
				System.err.println("Additionaly, an exception occured while closing the client: ");
				ex.printStackTrace(System.err);
				System.exit(1);
				return;
			}
			System.exit(1);
			return;
		}
		try {
			client.close();
		} catch (IOException e) {
			System.err.println("An exception occured while closing the client: ");
			e.printStackTrace(System.err);
			System.exit(1);
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
		System.out.println("    --command       | -c <command> : "
				+ "Command that should be sent to the server (noninteractive mode)");
	}

}
