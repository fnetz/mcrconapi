# mcrconapi
[![Build Status](https://travis-ci.org/fnetworks/mcrconapi.svg?branch=master)](https://travis-ci.org/fnetworks/mcrconapi)

A simple lightweight java 8 API to execute commands on minecraft servers remotely via the RCON protocol (see below).

Tested against minecraft spigot 1.7.10 server - SUCCESS

Current version: v1.1.0

## CLI Usage
    java -jar mcrconapi-1.1.0.jar
Parameters:

Long form | Short form | Arguments | Description
------------ | ------------- | --------- | -----------
--host | -a | address | Specify the host address
--login | -l | password | Login at the server with the given password
--help | -h | - | Show a help message
--version | -v | - | Prints version information
--noninteractive | -n | - | Non-Interactive mode (exit instead of asking for missing information and commands, default is interactive mode)
--command | -c | command | Command that should be sent to the server (noninteractive mode)

An example call would be `java -jar mcrconapi-1.1.0.jar --host 127.0.0.1 -l supersecret --command "say Hello World" -n`.

Parameters can also be bundled, like `-nc "say Hello World"` for setting mode to non-interactive and specifying command at the same time. This works only for the short forms, and only the last parameter can have a value.

## API Usage
1. Instantiate RConClient using one of the four constructors
2. If you used one of the non-password constructors, call authenticate(password) to login.
3. Use sendCommand(command) to execute a command and get its output

## Contribute
1. Clone the project using the link github provides
2. Generate the project files for your IDE.

To generate these files run the following commands from the project root (where the pom.xml is):

    mvn eclipse:eclipse
    
for Eclipse IDE or

    mvn idea:idea

for IntelliJ IDEA.

Now you should be able to import this project into your IDE.

## The RCON protocol
### Packet infrastructure
Field name | Field type | Description
---------- | ---------- | -----------
Length | int (4 bytes) | Length of _remainder_ of packet
Request ID | int (4 bytes) | Client-generated ID
Type | int (4 bytes) | See packet types below
Payload | byte[] | ASCII encoded text (see packet types below for possible content)
2-byte terminator | byte, byte | Two null bytes

### Packet types
Type | Name | Description
---- | ---- | --------
3 | Login | Outgoing payload: password. If the server returns a packet with the same request ID, auth was successful (note: packet type is 2, not 3). If you get a request ID of -1, auth failed (wrong password).
2 | Command | Outgoing payload should be the command to run, e.g. `time set 0`
2 | Login Response | See login packet
0 | Command Response | Incoming payload is the output of the command, though many commands return nothing, and there's no way of detecting unknown commands. The output of the command may be split over multiple packets, each containing 4096 bytes (less for the last packet). Each packet contains part of the payload (and the two-byte padding). The last packet sent is the end of the output.

The default port for RCON is 25575

For further information on RCON see [http://wiki.vg/RCON]()
