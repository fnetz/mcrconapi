/*
 * Copyright (c) 2021 Felix Solcher
 * Licensed under the terms of the MIT license.
 */
package org.fnet.mcrconapi;

import java.io.IOException;

/**
 * Thrown if a packet is malformed, e.g. wrong length or unknown type
 */
public class MalformedPacketException extends IOException {

	private static final long serialVersionUID = -6409356862198025733L;

	/**
	 * Constructs a new MalformedPacketException with a specific message
	 * 
	 * @param message
	 *            the message to show
	 */
	public MalformedPacketException(String message) {
		super(message);
	}

}
