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

package com.datacruncher.jpa;

public class Update {
	private Object results;
	private boolean success;
	private String message;
	private String extraMessage;
	
	public Object getResults() {
		return results;
	}
	
	public void setResults(Object results) {
		this.results = results;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getExtraMessage() {
		return extraMessage;
	}		
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setExtraMessage(String message) {
		this.extraMessage = message;
	}	
}