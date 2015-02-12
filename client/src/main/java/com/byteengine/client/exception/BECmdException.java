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

/**
 * @author issa
 *
 */
public class BECmdException extends ByteEngineException {
	private static final long serialVersionUID = 876050900897979412L;

	public BECmdException() {
		super();
	}

	public BECmdException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BECmdException(String message, Throwable cause) {
		super(message, cause);
	}

	public BECmdException(String message) {
		super(message);
	}

	public BECmdException(Throwable cause) {
		super(cause);
	}

}
