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
package com.seer.datacruncher.constants;

public enum Roles {
	ADMINISTRATOR(1), APPLICAITON_MANAGER(2), OPERATOR(3), DISPATCHER(4),USER(5);
	int dbCode;
	Roles(int dbCode) {
        this.dbCode = dbCode;
    }
    public int getDbCode() {
        return dbCode;
    }
    public static Roles getStatus(int dbCode) {
    	Roles[] statusList = Roles.values();
        for (Roles status : statusList) {
			if (dbCode == status.getDbCode()) {
                return status;
            }
        }
        return null;
    }
}
