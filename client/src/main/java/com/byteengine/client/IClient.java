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
package com.byteengine.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.byteengine.client.exception.BECmdException;
import com.byteengine.client.exception.BELoginException;
import com.byteengine.client.exception.BETokenException;
import com.byteengine.client.exception.ByteEngineException;

/**
 * Interface defining functions needed to interact with a Byte Engine server
 * 
 * @author issa
 *
 */
public interface IClient {
	/**
	 * Sends a command to execute on the Byte Engine server
	 * 
	 * @param token token for authentication purposes
	 * @param cmd The command to execute on the remote server
	 * @return The result of the execution
	 * @throws Exception
	 */
	String exec(String token, String cmd) throws BECmdException, BETokenException, IOException;
	
	/**
	 * @return <code>true</code> if the server is reachable, otherwise <code>false</code>
	 */
	boolean isServerAlive();
	
	/**
	 * Login with the ByteEngine server
	 * 
	 * @param userid
	 * @param password
	 * @return a token if the login is successful, otherwise <code>null</code>
	 */
	String login(String userid, String password) throws BELoginException, IOException;
	
	/**
	 * Reads a file from the Byte Engine instance
	 * 
	 * @param token token for authentication purposes
	 * @param db database where the remote file resides
	 * @param remoteFile path of the remote file
	 * @return File create via File.createTempFile()
	 */
	File read(String token, String db, String remoteFile) throws ByteEngineException, IOException;
	
	/**
	 * Writes a file on the Byte Engine instance
	 * 
	 * @param token token for authentication purposes
	 * @param db database where the remote file should be stored
	 * @param remoteFile path of the remote file
	 * @param data stream containing the data to store - will be close when this functions has finished
	 */
	void write(String token, String db, String remoteFile, InputStream data) throws ByteEngineException, IOException;
}
