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

import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.utils.generic.I18n;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CreateXMLFromFlatFileFixedPosition implements DaoSet {
	
	Logger log = Logger.getLogger(this.getClass());

	public CreateXMLFromFlatFileFixedPosition() {
	}

	public DatastreamDTO createXMLFromFlatFileFixedPosition (DatastreamDTO dataStreamDTO) {
		String datastream = dataStreamDTO.getInput();
		String fieldValue;
        String fillChar;
		int beginIndex = 0;
		int endIndex = 0;
		int lenght = datastream.length();
		List<SchemaFieldEntity> listSchemaFields = schemaFieldsDao.listSchemaFields(dataStreamDTO.getIdSchema());
		List<String> listValues = new ArrayList<String> ();
		for (int cont = 0 ; cont < listSchemaFields.size() ; cont++) {
            endIndex = beginIndex + Integer.parseInt(listSchemaFields.get(cont).getSize());
            if (endIndex > lenght) {
                dataStreamDTO.setSuccess(false);
                dataStreamDTO.setMessage(I18n.getMessage("error.lenghtNotValid"));
                return dataStreamDTO;
            }
            fieldValue = datastream.substring(beginIndex , endIndex);
            beginIndex = endIndex;
            if(listSchemaFields.get(cont).getIdFieldType()!= FieldType.date){
                fillChar = listSchemaFields.get(cont).getFillChar();
                if (listSchemaFields.get(cont).getIdAlign() == null || listSchemaFields.get(cont).getIdAlign() == 1) {
					fieldValue = StringUtils.stripEnd(fieldValue, fillChar);
				} else {
                    fieldValue = StringUtils.stripStart(fieldValue , fillChar);
                }
            }
            fieldValue = fieldValue.replaceAll("&","&amp;");
			listValues.add(fieldValue);
		}
		if (endIndex != lenght) {
			dataStreamDTO.setSuccess(false);
			dataStreamDTO.setMessage(I18n.getMessage("error.lenghtNotValid"));
		} else {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            stringBuffer.append("<"+Tag.TAG_ROOT+">\n");
			for (int cont = 0 ; cont < listSchemaFields.size() ; cont++) {
				if (!listSchemaFields.get(cont).getNillable() || listValues.get(cont).length() > 0) {
					stringBuffer.append("\t<" + listSchemaFields.get(cont).getName() + ">" + listValues.get(cont) + "</" + listSchemaFields.get(cont).getName() + ">\n");
				}
			}
            stringBuffer.append("</"+ Tag.TAG_ROOT+">\n");
			dataStreamDTO.setSuccess(true);
			dataStreamDTO.setOutput(stringBuffer.toString());
		}

        log.info(dataStreamDTO.getOutput());
		
		return dataStreamDTO;
	}
}