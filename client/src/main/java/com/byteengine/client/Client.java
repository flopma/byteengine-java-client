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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.byteengine.client.exception.BECmdException;
import com.byteengine.client.exception.BELoginException;
import com.byteengine.client.exception.ByteEngineException;

/**
 * Default client for Byte Engine
 * 
 * @author issa
 *
 */
public class Client implements IClient {
	
	static private final Logger LOG = LoggerFactory.getLogger(Client.class);
	
	RequestConfig proxyConfig = null;
	CloseableHttpClient httpClient = null;
	String serverBaseUrl;
	
	public Client(String host, int port, boolean ssl) {
		httpClient = HttpClients.createDefault();
		
		serverBaseUrl = String.format("%s://%s:%d",
				ssl ? "https" : "http",
				host,
				port);
	}

	public void setProxySettings(String proxyHost, int proxyPort, String proxyUsername, String proxyPass) {
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		
		proxyConfig = RequestConfig.custom().setProxy(proxy).build();
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		AuthScope authScope = new AuthScope(proxyHost, proxyPort);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(proxyUsername, proxyPass);
		credsProvider.setCredentials(authScope, creds);
		
		httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
	}
	
	
	public String exec(String token, String cmd) throws BECmdException, IOException {
		HttpPost post = new HttpPost(serverBaseUrl + "/bfs/query");
		setProxy(post);
		
		CloseableHttpResponse response = null;
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("sending a command to server : %s", cmd));
			}
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", token));
			params.add(new BasicNameValuePair("query", cmd));
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			
			response = httpClient.execute(post);
			
			String resp = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			JSONObject json = new JSONObject(resp);
			String status = json.getString("status");
			
			if ("ok".equalsIgnoreCase(status)) {
				return resp;
			} else {
				throw new BECmdException(json.getString("msg"));
			}
		} finally {
			try {
				response.close();
			} catch (IOException ioe) {}
		}
	}

	public boolean isServerAlive() {
		HttpGet get = new HttpGet(serverBaseUrl + "/");
		setProxy(get);
		
		CloseableHttpResponse response = null;
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("checking if server alive, trying url %s", get.getURI().toString()));
			}
			
			response = httpClient.execute(get);
			EntityUtils.consume(response.getEntity());
			return (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);
			
		} catch (IOException e) {
			return false;
		} finally {
			try {
				response.close();
			} catch (IOException e) {}
		}
	}

	public String login(String userid, String password) throws BELoginException, IOException {
		HttpPost post = new HttpPost(serverBaseUrl + "/bfs/token");
		setProxy(post);
		
		CloseableHttpResponse response = null;
		try {
			if (LOG.isTraceEnabled()) {
				LOG.debug(String.format("trying to acquire a token for user [%s]", userid));
			}
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", userid));
			params.add(new BasicNameValuePair("password",  password));
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			
			response = httpClient.execute(post);
			
			String resp = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			JSONObject json = new JSONObject(resp);
			String status = json.getString("status");
			
			if ("ok".equalsIgnoreCase(status)) {
				return json.getString("data");
			} else {
				throw new BELoginException(json.getString("msg"));
			}
		} finally {
			try {
				response.close();
			} catch (IOException ioe) {}
		}
	}

	public File read(String token, String db, String remoteFile) throws IOException, ByteEngineException {
		HttpPost post = new HttpPost(serverBaseUrl + "/bfs/readbytes");
		setProxy(post);
		
		CloseableHttpResponse response = null;
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("downloading file from db %s with path %s", db, remoteFile));
			}
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", token));
			params.add(new BasicNameValuePair("database", db));
			params.add(new BasicNameValuePair("path", remoteFile));
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			
			response = httpClient.execute(post);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				File tmpFile = File.createTempFile(remoteFile, null);
				tmpFile.deleteOnExit();
				InputStream in = null;
				OutputStream out = null;
				try {
					out = new BufferedOutputStream(new FileOutputStream(tmpFile));
					in = response.getEntity().getContent();
					byte buf[] = new byte[8192];
					int r = -1;
					while ((r = in.read(buf)) > -1) {
						out.write(buf, 0, r);
					}
					
					return tmpFile;
				} finally {
					try { in.close(); } catch (IOException ioe) {}
					try { out.close(); } catch (IOException ioe) {}
				}
				
			} else {
				JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
				throw new ByteEngineException(json.getString("msg"));
			}
		} finally {
			try { response.close(); } catch (IOException ioe) {}
		}
	}

	public void write(String token, String db, String remoteFile,
			InputStream data) throws ByteEngineException, IOException {
		
		String uploadTicket = getUploadTicket(token, db, remoteFile);
		
		CloseableHttpResponse response = null;
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("uploading file with ticket %s", uploadTicket));
			}
			
			HttpPost post = new HttpPost(String.format("%s/bfs/writebytes/%s", serverBaseUrl, uploadTicket));
			setProxy(post);
			
			HttpEntity entity = MultipartEntityBuilder.create()
				.addBinaryBody("file", data)
				.build();
			post.setEntity(entity);
			
			response = httpClient.execute(post);
			
			String resp = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			JSONObject json = new JSONObject(resp);
			String status = json.getString("status");
			
			if (!"ok".equalsIgnoreCase(status)) {
				throw new ByteEngineException(json.getString("msg"));
			}
		} finally {
			try { data.close(); } catch(IOException ioe) {}
			try { response.close(); } catch (IOException ioe) {}
		}
	}

	
	private String getUploadTicket(String token, String db, String path) throws ByteEngineException, IOException {
		HttpPost post = new HttpPost(serverBaseUrl + "/bfs/uploadticket");
		setProxy(post);
		
		CloseableHttpResponse response = null;
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("obtaining a ticket before uploading file %s in db %s", path, db));
			}
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("token", token));
			params.add(new BasicNameValuePair("database", db));
			params.add(new BasicNameValuePair("path", path));
			post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
			
			response = httpClient.execute(post);
			
			String resp = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
			JSONObject json = new JSONObject(resp);
			String status = json.getString("status");

			if ("ok".equalsIgnoreCase(status)) {
				return json.getString("data");
			} else {
				throw new ByteEngineException(json.getString("msg"));
			}
		} finally {
			try {
				response.close();
			} catch (IOException ioe) {}
		}
	}
	
	private void setProxy(HttpRequestBase request) {
		if (proxyConfig != null) {
			request.setConfig(proxyConfig);
		}
	}
}
