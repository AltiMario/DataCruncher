/*
 * Copyright (c) 2019  Altimari Mario
 * All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.seer.datacruncher.load;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Simple Load Runner to perform Load Testing on DataCruncher
 * @author Hassan Saghir
 *
 */
public class LoadRunner {
	
	private static Properties properties;

	public static void main(String[] args){
		new LoadRunner().startLoadTesting();
	}
	
	/**
	 * Function To Start Load Testing
	 */
	public void startLoadTesting(){
		properties = new Properties();
		InputStream in = LoadRunner.class.getClassLoader().getResourceAsStream("load.properties");

		try {
			properties.load(in);
		} catch (Exception e) {
			System.out.println("load.properties not found in classpath");
			e.printStackTrace();
			return;
		}

		String url = properties.getProperty("url");
		DefaultHttpClient client = null;
		try {
			for(int i=1; i<=Integer.valueOf(properties.getProperty("iterations")); i++){
				for(int j=1; j<=Integer.valueOf(properties.getProperty("no_of_parallel_users")); j++){
					
					System.out.println("--- Iteration: "+ i +" | Request: "+j +" ---");
					client = createClient();
					
					/** LOGIN **/
					HttpPost httpPostLogin = new HttpPost(url+"/login.json");
					System.out.println("Requesting: "+httpPostLogin.getURI());
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					nvps.add(new BasicNameValuePair("userName", properties.getProperty("user_name")));
					nvps.add(new BasicNameValuePair("password", properties.getProperty("password")));
					httpPostLogin.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
					HttpResponse response = client.execute(httpPostLogin);
					printResponse(response);
					
					/** APPLICATION **/
					// Read
					if((properties.getProperty("application")+"").indexOf("read")!=-1){
						postRequest(client,"applicationsRead.json");
					}
					
					/** DATABASES **/
					// Read
					if((properties.getProperty("database")+"").indexOf("read")!=-1){
						postRequest(client,"databasesRead.json");
					}
					
					/** SCHEMAS **/
					// Read
					if((properties.getProperty("schema")+"").indexOf("read")!=-1){
						postRequest(client,"schemasRead.json");
					}
					
					/** TASKS (SCHEDULERS) **/
					// Read
					if((properties.getProperty("task")+"").indexOf("read")!=-1){
						postRequest(client,"tasksRead.json");
					}
					
					/** USERS **/
					// Read
					if((properties.getProperty("user")+"").indexOf("read")!=-1){
						postRequest(client,"usersRead.json");
					}
					
					/** REPORTS **/
					// Read
					if((properties.getProperty("report")+"").indexOf("read")!=-1){
						postRequest(client,"realTimeGraph.json");
					}
				}
				Thread.sleep(Integer.valueOf(properties.getProperty("delay_in_secs"))*1000);
			}

		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}finally{
			if(null != client)
				client.getConnectionManager().shutdown();
		}
	}

	private static HttpResponse postRequest(HttpClient client, String action){
		HttpResponse response=null;
		try {
				HttpPost httpPostDatabase = new HttpPost(properties.getProperty("url")+"/"+action);
				System.out.println("Requesting: "+httpPostDatabase.getURI());
				response = client.execute(httpPostDatabase);
				printResponse(response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Return HttpClient
	 * @return
	 */
	public static DefaultHttpClient createClient() {
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
		cm.setMaxTotal(100);
		DefaultHttpClient client = new DefaultHttpClient(cm);
		return client;
	}
	
	/**
	 * Print Response
	 * @param response
	 */
	public static void printResponse(HttpResponse response){
		if (response != null && response.getEntity() != null) {
			InputStream instream = null;
		     try {
		    	 instream = response.getEntity().getContent();
		         BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
		         System.out.println("Response=> Code:"+response.getStatusLine()+" | content:"+ reader.readLine()+"\n");

		     } catch (IOException ex) {
		    	 ex.printStackTrace();
		     } catch (RuntimeException ex) {
		         throw ex;
		     } finally {
		    	 if(null != instream){
					try {
						instream.close();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
		    	 }
		     }
		 }
	}
}
