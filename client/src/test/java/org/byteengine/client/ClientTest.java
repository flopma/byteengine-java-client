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
package org.byteengine.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Consts;
import org.junit.Before;
import org.junit.Test;

import com.byteengine.client.Client;
import com.byteengine.client.exception.BECmdException;
import com.byteengine.client.exception.ByteEngineException;

/**
 * @author issa
 *
 */
public class ClientTest {
	
	Client client;
	String token;
	String db = "javaclient";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println("Setting up test");
		
		client = new Client("50.116.39.84", 8500, false);
		
		System.out.println(String.format("Server alive ? [%b]", client.isServerAlive()));
		
		token = client.login("flopma", "mXGquoP5ZN");
	}

	/**
	 * Test method for {@link com.byteengine.client.Client#Exec(java.lang.String)}.
	 * @throws IOException 
	 * @throws BECmdException 
	 */
	@Test
	public void testExec() throws BECmdException, IOException {
		System.out.println(client.exec(token, "user.whoami; @" + db + ".info /;"));
	}

	/**
	 * Test method for {@link com.byteengine.client.Client#read(java.lang.String, java.lang.String, java.lang.String)}.
	 * @throws ByteEngineException 
	 * @throws IOException 
	 */
	@Test
	public void testRead() throws IOException, ByteEngineException {
		File file = client.read(token, db, "/issatest");
		InputStream in = new FileInputStream(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte buf[] = new byte[8192];
		int r = -1;
		while ((r = in.read(buf)) > -1) {
			out.write(buf, 0, r);
		}
		System.out.println(out.toString());
		in.close();
		out.close();
	}

	/**
	 * Test method for {@link com.byteengine.client.Client#write(java.lang.String, java.lang.String, java.lang.String, java.io.InputStream)}.
	 * @throws IOException 
	 * @throws ByteEngineException 
	 */
	@Test
	public void testWrite() throws ByteEngineException, IOException {
		//client.exec(token, "@javaclient.newfile /issatest {}");
		InputStream in = new ByteArrayInputStream("this is a test content".getBytes(Consts.UTF_8));
		client.write(token, db, "/issatest", in);
	}

}
