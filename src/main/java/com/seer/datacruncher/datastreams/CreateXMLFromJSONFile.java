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

package com.seer.datacruncher.datastreams;

import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.jpa.dao.DaoSet;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.XML;

public class CreateXMLFromJSONFile implements DaoSet {

    Logger log = Logger.getLogger(this.getClass());

    public DatastreamDTO createXml(DatastreamDTO dataStreamDTO) {
        try {
            JSONObject jsonObject = new JSONObject(dataStreamDTO.getInput());
            String xmlString = XML.toString(jsonObject);            
            
            // Add root tag around the xml message
    		String xmlMessage = "<"+ Tag.TAG_ROOT+">" + xmlString + "</"+Tag.TAG_ROOT+">";
    		
            dataStreamDTO.setOutput(xmlMessage);            
            dataStreamDTO.setSuccess(true);
            
        } catch (Exception e) {        	
            dataStreamDTO.setSuccess(false);
            dataStreamDTO.setMessage("Json Error");
            log.error("Json error", e);
        }
        return dataStreamDTO;
    }

}