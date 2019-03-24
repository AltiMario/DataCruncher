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
import com.datacruncher.utils.validation.CodiceFiscaleAttributes;
import com.datacruncher.utils.validation.ComuneAttributes;
import com.datacruncher.utils.validation.MultipleValidation;
import com.datacruncher.utils.validation.SingleValidation;
import com.datacruncher.validation.KeyConstraint;
import com.datacruncher.validation.ResultStepValidation;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodiceFiscale implements MultipleValidation,SingleValidation {
	
	private static Logger log = Logger.getLogger(CodiceFiscale.class);
	private String regex = "^[A-Za-z]{6}[0-9]{2}[A-Za-z][0-9]{2}[A-Za-z][0-9]{3}[A-Za-z]$";
    private Pattern pattern = Pattern.compile(regex);
    private String invalidCause = "";
    private int dispari[] = {1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 2, 4, 18, 20, 11, 3, 6, 8, 12, 14, 16, 10, 22, 25, 24, 23};
    private static final Pattern VOCAL_LIST  = Pattern.compile("[AEIOU]");
    private static final String  VOCAL       = "vocal";
    private static final String  CONSONANT   = "consonant";
    private static final String  CHARACTER_X = "X";
    private static final String  MALE        = "M";
    private static final String  FEMALE      = "F";
    private static final String  CODFISC      = "codiceFiscale";
    
    private static char[] months_list = {'A', 'B', 'C', 'D', 'E', 'H','L', 'M', 'P', 'R', 'S', 'T' };
    private static int[][] EVEN_ODD_CHAR_CODES={{0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25},
    											{1,0,5,7,9,13,15,17,19,21,1,0,5,7,9,13,15,17,19,21,2,4,18,20,11,3,6,8,12,14,16,10,22,25,24,23}};
       
    /**
     * the method returns all the vowels or consonants from a string supplied as input
     * 
     * @param value
     * @param typeValue
     * @return
     */
    private static String getAttribute(String value, String typeValue) {
    	
    	String resultValue = new String();
		int i = 0;
		char[] valChar = value.toCharArray();
		
		for (i = 0; i < valChar.length; i++) {
			if (typeValue.equals(VOCAL) && VOCAL_LIST.matcher(String.valueOf(valChar[i])).matches()) {
				resultValue += valChar[i];
			}
			else if (typeValue.equals(CONSONANT) && !VOCAL_LIST.matcher(String.valueOf(valChar[i])).matches()) {
				resultValue += valChar[i];
			}
		}
		return resultValue;
	}
    
   /**
    * the method returns the triple corresponding to the surname for the tax code
    * @param name
    * @return
    */
    private static StringBuilder getNameAttribute(String name){
    	
    	StringBuilder tripleNameAttribute = new StringBuilder(getAttribute(name, CONSONANT));
    	      
		if (tripleNameAttribute.length() >= 4) {
			tripleNameAttribute = tripleNameAttribute.delete(1, 2);
		}

		tripleNameAttribute.append(getAttribute(name, VOCAL));

		if (tripleNameAttribute.length() > 3) {
			tripleNameAttribute = tripleNameAttribute.replace(0, tripleNameAttribute.length(), tripleNameAttribute.substring(0, 3));
		}

		for (int i = tripleNameAttribute.length(); i < 3; i++) {
			tripleNameAttribute.append(CHARACTER_X);
		}

		return tripleNameAttribute;
       	
    }
    /**
     * the method returns the triple corresponding to the surname for the tax code
     * @param surname
     * @return
     */
    private static StringBuilder getSurnameAttribute(String surname){
    	
    	StringBuilder tripleSurnameAttribute = new StringBuilder();

    	tripleSurnameAttribute.append(getAttribute(surname, CONSONANT)
				+ getAttribute(surname, VOCAL));

		if (tripleSurnameAttribute.length() > 3) {
			tripleSurnameAttribute = new StringBuilder(tripleSurnameAttribute.substring(0, 3));
		}

		for (int i = tripleSurnameAttribute.length(); i < 3; i++) {
			tripleSurnameAttribute.append(CHARACTER_X);
		}
		
		return tripleSurnameAttribute;
       	
    }
      private static char getCodMonth(int month){
    	return months_list[month];
    }
    
    /**
     * The method returns the 5 characters corresponding to the sex and date of birth
     * @param birthDate
     * @param sex
     * @return
     */
    private static StringBuilder getBirthAndSexAttribute(String birthDate, String sex){
    	
    	StringBuilder birthAndSexAttribute = new StringBuilder();
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    	Date date = new Date();
    	GregorianCalendar cal = new GregorianCalendar();
    	try{
    		date = df.parse(birthDate);
    	}catch(ParseException exception){
    		 log.error("Error: " + exception.getMessage());
    	}
    	cal.setTime(date);
    	
		Integer day   = cal.get(GregorianCalendar.DAY_OF_MONTH);
		Integer month = cal.get(GregorianCalendar.MONTH);
		Integer year  = cal.get(GregorianCalendar.YEAR);
		birthAndSexAttribute.append(year.toString().substring(2, 4));
		birthAndSexAttribute.append(getCodMonth(month));

		if (sex.equals(MALE)) {
			birthAndSexAttribute.append(String.format("%02d", day));
		} else {
			day += 40;
			birthAndSexAttribute.append((day).toString());
		}
		return birthAndSexAttribute;
       	
    }
    
    /**
     * The method looks for the part of the tax code corresponding to the 
     * municipality and the province of birth of 
     * the subject in the list of the municipalities surveyed in the application
     * 
     * @param birthPlace
     * @param birthPlaceProvince
     * @return
     */
	private static String getBirthPlaceAttribute(String birthPlace, String birthPlaceProvince) {
		String codFisco = "";
		ComuneAttributes listaComuni = KeyConstraint.findValueInObjectList(ComuniItaliani.comuni, "comune", birthPlace);
		if (listaComuni != null && listaComuni.getProvincia().toUpperCase().equals(birthPlaceProvince)) {
			codFisco = listaComuni.getCodfisco();
		}
		return codFisco;
	}
    
    /**
     * The method returns the control character of the tax code
     * @param codFisc
     * @return
     */
    private static Character getControlCharacterAttribute(String codFisc){
    	
    	Integer sumValue = new Integer(0);

		for (int i = 0; i < codFisc.length(); i++) {
			
			int k = Character.getNumericValue(codFisc.charAt(i));
			if (i % 2 == 0) {
				sumValue += EVEN_ODD_CHAR_CODES[1][k];
			} else {
				sumValue +=  EVEN_ODD_CHAR_CODES[0][k];
			}

		}
		
		return Character.toUpperCase(Character.forDigit(((sumValue % 26)+10), 35));
    }

    /**
     * The method applies the tax code
     * @param datastreamDTO
     * @param jaxbObject
     * @param schemaXSDEntity
     * @return
     * @throws Exception
     */
    public  ResultStepValidation checkValidity(DatastreamDTO datastreamDTO, Object jaxbObject, SchemaXSDEntity schemaXSDEntity) throws Exception {
        ResultStepValidation result = new ResultStepValidation();
        CodiceFiscaleAttributes codiceFiscaleComplete = new CodiceFiscaleAttributes();
        Field fld[] = codiceFiscaleComplete.getClass().getDeclaredFields();
        String keyName;
        String msgresult ;
        String msgError = "";
        result.setValid(true);
        result.setMessageResult("");
        int i = 0;
        Set<String> keySet;
        Map<String, String> elementValues= null;
        try{
            //check the validity field by field of all elements (ex: if is a valid nome or cognome, without relationship)
            while (i < fld.length) {
                fld[i].setAccessible(true);
                keyName = fld[i].getName();
                keySet = CommonUtils.parseSchemaAndGetXPathSetForAnnotation(new ByteArrayInputStream(schemaXSDEntity.getSchemaXSD().getBytes()), "@codicefiscale:" + keyName);
                if (keySet != null && keySet.size() > 0) {
                    elementValues = CommonUtils.parseXMLandInvokeDoSomething(new ByteArrayInputStream(datastreamDTO.getOutput().getBytes()), keySet, jaxbObject);
                    msgresult = "";
                    for (String keyValue : elementValues.values()) {
                       if (keyValue == null || keyValue.equals("")){
                            msgresult += "Codice Fiscale -> " + keyName + " : [" + keyValue + "] wrong. \n";
                            result.setValid(false);
                        }
                        else {
                            fld[i].set(codiceFiscaleComplete, keyValue.toUpperCase());
                        }
                    }
                    msgError += msgresult;
                }
                i++;
            }

            //check the validity of the fields relationship (all fields together)
            if (result.isValid() && !elementValues.isEmpty()) {
                msgError = parameterVerify(codiceFiscaleComplete);
                if (msgError.equals(""))
                    result.setValid(true);
                else result.setValid(false);
            }
            result.setMessageResult(msgError);
        }catch(Exception e)  {
            result.setValid(false);
            result.setMessageResult("Validation error in Codice fiscale. " +e.getMessage());
        }
        return result;
    }
    /**
     * the method check tax identifier parameter
     * @param codiceFiscaleComplete
     * @return
     */
    private String parameterVerify(CodiceFiscaleAttributes codiceFiscaleComplete){
    	Field fld[] = codiceFiscaleComplete.getClass().getDeclaredFields();
    	boolean parameterNull = false;
    	boolean allParameterNull = true;
    	int i=0;
    	String keyName = "";
    	String result = "";
    	if (codiceFiscaleComplete.getCodiceFiscale() == null
    			|| codiceFiscaleComplete.getCodiceFiscale().equals("")){
    		
    		result += "Codice Fiscale -> codiceFiscale :  missing. This parameter is mandatory. \n";
    		return result;
    	}
    	while (i < fld.length) {
    		 fld[i].setAccessible(true);
             keyName = fld[i].getName();
             try{
            	 String parameterName = (String)fld[i].get(codiceFiscaleComplete);
            	 if (parameterName == null || parameterName.equals("")){
                	 result += "Codice Fiscale -> " + keyName + " :  missing. \n";
                	 parameterNull = true;
                 }
            	 else {
            		 if (!keyName.equals(CODFISC))
            				 allParameterNull = false;
            	 
            	 }
                 i++;
             }
             catch(IllegalAccessException illegalAccessException){
            	 log.error("Error: " + illegalAccessException.getMessage());
             }
             
    	 }
    	 if (allParameterNull){
    		 if (isValid(codiceFiscaleComplete.getCodiceFiscale()))
    			result = "";
    		 else
    			 result =  "Codice Fiscale: [" + codiceFiscaleComplete.getCodiceFiscale() + "] wrong. "+ invalidCause;
    		 return result;
    	 }  	 
		 if (!parameterNull) {
			    result += checkValidityFields(codiceFiscaleComplete);
			    if (result.equals("")){
		    		String cf =  getSurnameAttribute(codiceFiscaleComplete.getCognome()).toString() 
					+ getNameAttribute(codiceFiscaleComplete.getNome()).toString() 
						+ getBirthAndSexAttribute(codiceFiscaleComplete.getDataNascita(), codiceFiscaleComplete.getSesso()).toString()
							+getBirthPlaceAttribute(codiceFiscaleComplete.getComuneNascita(),codiceFiscaleComplete.getProvinciaNascita());
					cf += getControlCharacterAttribute(cf).toString();
					if (cf.equals(codiceFiscaleComplete.getCodiceFiscale()) 
							&& isValid(codiceFiscaleComplete.getCodiceFiscale()))
						return result;	
					else result += "Codice Fiscale: [" + codiceFiscaleComplete.getCodiceFiscale() + "] wrong. \n";
			    }
		 } else result += "Cannot validate Codice Fiscale, missing parameters";
    	return result;
    	
    }
    public String checkValidityFields(CodiceFiscaleAttributes codiceFiscaleComplete){
    	String result = "";
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    	
    	if (codiceFiscaleComplete.getSesso().length() !=1 || (!codiceFiscaleComplete.getSesso().equals(MALE)
    			&& !codiceFiscaleComplete.getSesso().equals(FEMALE)))
    		result += "Codice Fiscale -> sesso malformat (M/F). \n";
    	try{
    		df.parse(codiceFiscaleComplete.getDataNascita());
    		
    	}catch(ParseException exception){
    		result += "Codice Fiscale -> data di Nascita malformat (dd/MM/yyyy). \n";
    	}
    	if (!KeyConstraint.isFound(ComuniItaliani.comuni, "comune", codiceFiscaleComplete.getComuneNascita())){
    		result += "Codice Fiscale -> Comune di Nascita wrong. \n";
    	}
    	if (!KeyConstraint.isFound(ComuniItaliani.comuni, "provincia", codiceFiscaleComplete.getProvinciaNascita())){
    		result += "Codice Fiscale -> Provincia di Nascita wrong. \n";
    	}
    	if (codiceFiscaleComplete.getProvinciaNascita().length()!=2)
    		result += "Codice Fiscale -> Provincia di Nascita malformat (PP). \n";
    	return result;
    	
    }
    
    private boolean isValid(String string) {
        String cf;
        int i, s = 0, c;
        try {
            if (string == null || string.length() == 0) {
                invalidCause = "Empty or NULL value";
                return false;
            }
            if ( string.length() > 16) {
                invalidCause = "Too long (expected 16, got "+string.length()+")";
                return false;
            }
            if ( string.length() < 16) {
                invalidCause = "Too short (expected 16, got "+string.length()+")";
                return false;
            }
            Matcher m = pattern.matcher(string);
            if (!m.matches()) {
                invalidCause = "Regexp not matches";
                return false;
            }
            cf = string.toUpperCase();
            for (i = 1; i <= 13; i += 2) {
                c = cf.charAt(i);
                if (c >= '0' && c <= '9') {
                    s = s + c - '0';
                } else {
                    s = s + c - 'A';
                }
            }
            for (i = 0; i <= 14; i += 2) {
                c = cf.charAt(i);
                if (c >= '0' && c <= '9') {
                    c = c - '0' + 'A';
                }
                s = s + dispari[c - 'A'];
            }
        } catch (Exception exception) {
            log.error("Codice Fiscale Exception : " + exception);
            invalidCause = exception.getMessage();
            return false;
        }
        return s % 26 + 'A' == cf.charAt(15);
    }
    public ResultStepValidation checkValidity(String cod_fisc){
        ResultStepValidation result = new ResultStepValidation();
        try{
            if(isValid(cod_fisc)){
                result.setValid(true);
            }else{
                result.setValid(false);
                result.setMessageResult("Codice fiscale : [" + cod_fisc + "] wrong.\n"+ invalidCause);
            }
        }catch(Exception e)  {
            result.setValid(false);
            result.setMessageResult("Codice fiscale: [" + cod_fisc + "] wrong. " +e.getMessage());
        }
        return result;
    }
}