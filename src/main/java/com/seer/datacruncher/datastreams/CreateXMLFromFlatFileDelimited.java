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

package com.seer.datacruncher.datastreams;

import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.generic.I18n;
import org.apache.log4j.Logger;

import java.util.List;

public class CreateXMLFromFlatFileDelimited implements DaoSet {
	Logger log = Logger.getLogger(this.getClass());

	public CreateXMLFromFlatFileDelimited() {
	}

	public DatastreamDTO createXMLFromFlatFileDelimited(DatastreamDTO dataStreamDTO) {
		String delimiter = schemasDao.find(dataStreamDTO.getIdSchema()).getDelimiter();
        char chrDelim = schemasDao.find(dataStreamDTO.getIdSchema()).getChrDelimiter().charAt(0);
		String[] fields = CommonUtils.fieldSplit(dataStreamDTO.getInput(),delimiter,chrDelim);
		List<SchemaFieldEntity> listSchemaFields = schemaFieldsDao.listSchemaFields(dataStreamDTO.getIdSchema());
        if (fields.length != listSchemaFields.size()) {
			dataStreamDTO.setSuccess(false);
			dataStreamDTO.setMessage(I18n.getMessage("error.numberFieldsNoMatch"));
		} else {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            stringBuffer.append("<"+ Tag.TAG_ROOT+">\n");
            String fieldValue;
            String startchar;
            String endchar;
			for (int cont = 0; cont < listSchemaFields.size(); cont++) {
                fieldValue= (fields[cont]).trim();
                if(fields[cont].length() > 0 && fieldValue.startsWith(chrDelim+"") && fieldValue.endsWith(chrDelim+"") ){
                    fieldValue=fieldValue.substring(1,fieldValue.length()-1);
                }
                if(fields[cont].length() > 0)
                    fieldValue = fieldValue.replaceAll("&","&amp;");
                if (!listSchemaFields.get(cont).getNillable() || fieldValue.length() > 0) {
					stringBuffer.append(
                            "\t<" + listSchemaFields.get(cont).getName() + ">" +
                                    fieldValue
                            + "</"+ listSchemaFields.get(cont).getName() + ">\n");
				}
			}
			stringBuffer.append("</"+Tag.TAG_ROOT+">\n");
			dataStreamDTO.setSuccess(true);
			dataStreamDTO.setOutput(stringBuffer.toString());
		}
		return dataStreamDTO;
	}
}