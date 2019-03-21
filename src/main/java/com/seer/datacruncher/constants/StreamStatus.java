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
package com.seer.datacruncher.constants;


public enum StreamStatus {
	Invalid("0"), Valid("1"), Warning("2") ;
    String dbCode;
    StreamStatus(String dbCode) {
        this.dbCode = dbCode;
    }
    public String getDbCode() {
        return dbCode;
    }
    public int getCode() {
    	return Integer.parseInt(dbCode);
    }
    public static StreamStatus getStatus(String dbCode) {
    	StreamStatus[] statusList = StreamStatus.values();
        for (StreamStatus status : statusList) {
            if (dbCode.equals(status.getDbCode())) {
                return status;
            }
        }
        return null;
    }
}
