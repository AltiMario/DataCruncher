/*
 * Copyright (c) 2015  www.see-r.com
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

package com.seer.datacruncher.services.mashape;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.client.exceptions.MashapeClientException;
import com.mashape.client.http.HttpClient;
import com.mashape.client.http.HttpMethod;
import com.mashape.client.http.callback.MashapeCallback;

public class TokenMashape {

	private String publicKey;
	private String privateKey;

	public TokenMashape(String publicKey, String privateKey) {
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public JSONObject validateDataStream(String idSchema, String dataStream) throws MashapeClientException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idSchema", idSchema);
		parameters.put("dataStream", dataStream);
		return (JSONObject) HttpClient.doRequest(HttpMethod.POST, "http://hH15CWJDPI7PQyJ9gbVmlkEbP.proxy.mashape.com/mashape/validateDataStream.json", parameters, true, publicKey, privateKey, true);
	}

	public Thread validateDataStream(String idSchema, String dataStream, MashapeCallback mashapeCallback) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idSchema", idSchema);
		parameters.put("dataStream", dataStream);
		return HttpClient.doRequest(HttpMethod.POST, "http://hH15CWJDPI7PQyJ9gbVmlkEbP.proxy.mashape.com/mashape/validateDataStream.json", parameters, true, publicKey, privateKey, true, mashapeCallback);
	}

}