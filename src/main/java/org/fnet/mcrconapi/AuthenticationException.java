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

/**
 * Thrown if an authentication exception occurs. This could be when the password
 * is wrong, or you are already or not authenticated when trying to send a
 * command.
 * 
 * @see ErrorType
 */
public class AuthenticationException extends Exception {

	private static final long serialVersionUID = -7310707583704316944L;
	private final ErrorType type;

	/**
	 * The type of the authentication error
	 */
	public enum ErrorType {
		/**
		 * Wrong password: The password you entered and the password that was
		 * defined on the server don't match.
		 */
		WRONG_PASSWORD,
		/**
		 * Already authenticated: You already logged on to the remote server.
		 */
		ALREADY_AUTHENTICATED,
		/**
		 * Not authenticated: RCON requires you to provide a password to log in
		 */
		NOT_AUTHENTICATED
	}

	/**
	 * Constructs a new AuthenticationException with given message and error
	 * type.
	 * 
	 * @param message
	 *            the message to show
	 * @param type
	 *            the authentication error type
	 * @see ErrorType
	 */
	public AuthenticationException(String message, ErrorType type) {
		super(message);
		this.type = type;
	}

	/**
	 * Returns the error type of the exception
	 * 
	 * @return The error type
	 * @see ErrorType
	 */
	public ErrorType getType() {
		return type;
	}

}
