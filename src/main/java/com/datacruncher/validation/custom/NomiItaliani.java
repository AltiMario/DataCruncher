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
import com.datacruncher.utils.validation.MultipleValidation;
import com.datacruncher.validation.ResultStepValidation;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NomiItaliani implements MultipleValidation {
    private static final String MALE_NAMES_FILE = "dictionary/names_males_it.txt";
    private static final String FEMALE_NAMES_FILE = "dictionary/names_females_it.txt";
    private static final String SURNAMES_FILE = "dictionary/surnames_it.txt";
    private static Logger log = Logger.getLogger(NomiItaliani.class);
    private static final List<String> listMaleNames = new ArrayList<String>();
    private static final List<String> listFemaleNames = new ArrayList<String>();
    private static final List<String> listSurnames = new ArrayList<String>();
    private static final String flds[] = {"nomemaschile", "nomefemminile", "cognome"};
    private static final List fldsData[] = {listMaleNames, listFemaleNames, listSurnames};
    
    static {
    	//parse italian male names
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new ClassPathResource(MALE_NAMES_FILE).getInputStream(), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {                
                listMaleNames.add(str.toLowerCase());
            }
            in.close();
        } catch (IOException e) {
            log.error("Error: " + e.getMessage());
        }
        
        //parse italian female names
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new ClassPathResource(FEMALE_NAMES_FILE).getInputStream(), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {                
                listFemaleNames.add(str.toLowerCase());
            }
            in.close();
        } catch (IOException e) {
            log.error("Error: " + e.getMessage());
        }
        
        //parse italian surnames
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new ClassPathResource(SURNAMES_FILE).getInputStream(), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {                
                listSurnames.add(str.toLowerCase());
            }
            in.close();
        } catch (IOException e) {
            log.error("Error: " + e.getMessage());
        }
    }
    public  ResultStepValidation checkValidity(DatastreamDTO datastreamDTO, Object jaxbObject, SchemaXSDEntity schemaXSDEntity) throws Exception {
        ResultStepValidation result = new ResultStepValidation();
        String keyName;
        String msgresult;
        String msgError = "";
        result.setValid(true);
        result.setMessageResult("");
        
        Set<String> keySet;
        Map<String, String> elementValues;
        
        int i = 0;
        try{
        	while (i < flds.length) {
        		keyName = flds[i];
        		keySet = CommonUtils.parseSchemaAndGetXPathSetForAnnotation(new ByteArrayInputStream(schemaXSDEntity.getSchemaXSD().getBytes()), "@italian:" + keyName);
        		        		
        		if (keySet != null && keySet.size() > 0) {
        			elementValues = CommonUtils.parseXMLandInvokeDoSomething(new ByteArrayInputStream(datastreamDTO.getOutput().getBytes()), keySet, jaxbObject);
        			msgresult = "";
        			List listData = fldsData[i];
        			for (String keyValue : elementValues.values()) {
        				if(!listData.contains(keyValue.toLowerCase())) {
        					msgresult += keyName + " : [" + keyValue + "] wrong. \n";
        					result.setValid(false);
        				}
        			}
        			msgError += msgresult;
        		}
        		i++;
        	}

        	result.setMessageResult(msgError);

        }catch(Exception e)  {
        	result.setValid(false);
        	result.setMessageResult("Validation error in Nome. " +e.getMessage());
        }
        return result;
    }
}