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

/*
Gli attributi presenti sono equivalenti (e devono restare tali) alla mappatura su DB e sul front-end
*/
package com.datacruncher.constants;

import java.util.HashMap;
import java.util.Map;

public final class GenericType {
    public static final int simpleContent = 4;
    public static final int complexContent = 5;
    public static final int integer = 1;
    public static final int decimal = 2;

    public static final int okEvent = 0;
    public static final int koEvent = 1;
    public static final int warnEvent = 2;

    public static final int DownloadTypeConn = 1;
    public static final int uploadTypeConn = 2;

    public static final int maxEmailStream = 50;


    public static Map<Integer,String> getStatusType() {
        Map<Integer,String> mapStandlib = new HashMap<Integer,String>();
        mapStandlib.put(GenericType.okEvent, StreamStatus.getStatus(String.valueOf(GenericType.okEvent)).name());
        mapStandlib.put(GenericType.koEvent, StreamStatus.getStatus(String.valueOf(GenericType.koEvent)).name());
        mapStandlib.put(GenericType.warnEvent, StreamStatus.getStatus(String.valueOf(GenericType.warnEvent)).name());
        return mapStandlib;
    }

}

