/*
 * $Id$
 * 
 *  Copyright (C) 2015 Issa Gorissen
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.byteengine.client.exception;

public class ByteEngineException extends Exception {
	private static final long serialVersionUID = -5305677012675197070L;

	public ByteEngineException() {
		super();
	}

	public ByteEngineException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ByteEngineException(String message, Throwable cause) {
		super(message, cause);
	}

	public ByteEngineException(String message) {
		super(message);
	}

	public ByteEngineException(Throwable cause) {
		super(cause);
	}
	
}
