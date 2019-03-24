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

package com.datacruncher.validation.custom;

import com.datacruncher.datastreams.DatastreamDTO;
import com.datacruncher.jpa.entity.SchemaXSDEntity;

import com.datacruncher.utils.generic.CommonUtils;
import com.datacruncher.utils.validation.ComuneAttributes;
import com.datacruncher.utils.validation.MultipleValidation;
import com.datacruncher.validation.KeyConstraint;
import com.datacruncher.validation.ResultStepValidation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComuniItaliani implements MultipleValidation {
    private static final String ADDRESS_FILE = "data/comuni_italiani.txt";
    private static final String delimiter = ";";
    protected static List<ComuneAttributes> comuni = new ArrayList<ComuneAttributes>();
    private static Logger log = Logger.getLogger(ComuniItaliani.class);

	static {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new ClassPathResource(ADDRESS_FILE).getInputStream(),
					"UTF-8"));
			String str;
			while ((str = in.readLine()) != null) {
				String[] fields = StringUtils.splitPreserveAllTokens(str, delimiter);
				// create the list of objects
				if (fields[5].endsWith("x") && fields.length > 7) {
					String[] caps = StringUtils.splitPreserveAllTokens(fields[7], "-");
					int cap = Integer.parseInt(caps[0]);

					for (int i = cap; i <= Integer.parseInt(caps[1]); i++) {
						comuni.add(new ComuneAttributes(fields[0], fields[1], fields[2], fields[3], fields[4], String.format(
								"%" + 5 + "s", Integer.toString(cap)).replace(' ', '0'), fields[6]));
						cap++;
					}
				} else {
					comuni.add(new ComuneAttributes(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]));
				}
			}
			in.close();
		} catch (IOException e) {
			log.error("Error: " + e.getMessage());
		}
	}

	public ResultStepValidation checkValidity(DatastreamDTO datastreamDTO, Object jaxbObject, SchemaXSDEntity schemaXSDEntity)
			throws Exception {
		ResultStepValidation result = new ResultStepValidation();
		ComuneAttributes comstream = new ComuneAttributes();
		Field fld[] = comstream.getClass().getDeclaredFields();
		String keyName;
		String msgresult = null;
		String msgError = "";
		result.setValid(true);
		result.setMessageResult("");
		int i = 0;
		Set<String> keySet;
		Map<String, String> elementValues = null;
		ComuneAttributes attrs = null;
		try {
			while (i < fld.length) {
				fld[i].setAccessible(true);
				keyName = fld[i].getName();
				keySet = CommonUtils.parseSchemaAndGetXPathSetForAnnotation(new ByteArrayInputStream(schemaXSDEntity
						.getSchemaXSD().getBytes()), "@comuniitaliani:" + keyName);
				if (keySet != null && keySet.size() > 0) {
					elementValues = CommonUtils.parseXMLandInvokeDoSomething(new ByteArrayInputStream(datastreamDTO.getOutput()
                            .getBytes()), keySet, jaxbObject);
					msgresult = "";
					for (String keyValue : elementValues.values()) {
						attrs = KeyConstraint.findValueInObjectList(comuni, keyName, keyValue);
						if (attrs == null) {
							msgresult += "Comune Italiano -> " + keyName + " : [" + keyValue + "] wrong. \n";
							result.setValid(false);
						} else {
							// population of the object with only correct values
							// (attention: in case of multiple equals fields,
							// the value is the last found
							fld[i].set(comstream, keyValue);
						}
					}
					msgError += msgresult;
				}
				i++;
			}
            //check the validity of the fields relationship (all fields together)
            if (result.isValid() && !elementValues.isEmpty()) {
                ResultStepValidation res = KeyConstraint.validation(comstream, attrs);
                if (!res.isValid()) {
                    msgError += res.getMessageResult();
                    result.setValid(false);
                }
            }			
			result.setMessageResult(msgError);

		} catch (Exception e) {
			result.setValid(false);
			result.setMessageResult("Validation error in Comuni Italiani. " + e.getMessage());
		}
		return result;
    }
}