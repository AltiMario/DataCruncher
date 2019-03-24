/*
 * DataCruncher
 * Copyright (c) Mario Altimari. All rights reserved.
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
 *
 */
package com.datacruncher.constants;

public enum Alerts {
	NEVER(1,"Never"), 
	WARNING_ONE_A_DAY(2,"Warning_one_a_day"), 
	ERRORS_ONE_A_DAY(3,"Errors_one_a_day"), 
	ALL_ONE_A_DAY(4,"All_one_a_day"), 
	WARNINGS_EVERY_TIME(5,"Warnings_every_time"), 
	ERRORS_EVERY_TIME(6,"Errors_every_time"), 
	ALL_EVERY_TIME(7,"All_every_time"), 
	
	;

	int dbCode;
	String dbName;
	Alerts(int dbCode, String dbName) {
		this.dbCode = dbCode;
		this.dbName = dbName;
	}

	public int getDbCode() {
		return dbCode;
	}
	public String getDbName(){
		return dbName;
	}

	public static Alerts getAlert(int dbCode) {
		Alerts[] alertsList = Alerts.values();
		for (Alerts alert : alertsList) {
			if (dbCode == alert.getDbCode()) {
				return alert;
			}
		}
		return null;
	}
    public static Alerts getAlert(String dbName) {
        Alerts[] alertsList = Alerts.values();
        for (Alerts alert : alertsList) {
            if (dbName == alert.getDbName()) {
                return alert;
            }
        }
        return null;
    }
}
