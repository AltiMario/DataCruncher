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

package com.seer.datacruncher.utils.schema;

import com.seer.datacruncher.utils.generic.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JaxbGenerationResults {
	
	private List<Map<String, String>> jaxbGenerationResults = new ArrayList<Map<String,String>>();
	
	private boolean isGenerationSucc = true;
	
	/**
	 * Adds successful result.
	 * 
	 * @param msg - message (example: generation step info)
	 */
	public void addSuccessfulResult(String msg) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("success", "true");
		map.put("msg", msg);
		jaxbGenerationResults.add(map);
	}
	
	/**
	 * Adds failure result.
	 * 
	 * @param msg - message (example: generation step info)
	 * @param e - failure exception
	 */
	public void addFailureResult(String msg, Exception e) {
		addFailureResult(msg, CommonUtils.getExceptionAsString(e));
		e.printStackTrace();		
	}	
	
	/**
	 * Adds failure result.
	 * 
	 * @param msg - message (example: generation step info)
	 * @param stackTrace - stackTrace to save
	 */
	public void addFailureResult(String msg, String stackTrace) {
		isGenerationSucc = false;
		Map<String, String> map = new HashMap<String, String>();
		map.put("success", "false");
		map.put("stackTrace", stackTrace);
		map.put("msg", msg);
		jaxbGenerationResults.add(map);
	}		
	
	/**
	 * Checks if generation successful.
	 * 
	 * @return boolean
	 */
	public boolean isGenerationSuccessful() {
		return isGenerationSucc;
	}
	
	/**
	 * Returns generation results.
	 *  
	 * @return generation results
	 */
	public List<Map<String, String>> getGenerationResults() {
		return jaxbGenerationResults;
	}
	
}
