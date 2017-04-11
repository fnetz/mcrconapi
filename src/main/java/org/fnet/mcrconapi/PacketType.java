package org.fnet.mcrconapi;

public enum PacketType {
	AUTH(3, true), AUTH_RESPONSE(2, false), COMMAND(2, true), COMMAND_RESPONSE(0, false);
	
	private final int id;
	private final boolean clientside;
	
	private PacketType(int id, boolean clientside) {
		this.id = id;
		this.clientside = clientside;
	}

	public int getId() {
		return id;
	}

	public boolean isClientside() {
		return clientside;
	}
	
	public static PacketType fromID(int id, boolean clientside) {
		for (PacketType type : values()) {
			if (type.getId() == id && type.clientside == clientside)
				return type;
		}
		return null;
	}
}