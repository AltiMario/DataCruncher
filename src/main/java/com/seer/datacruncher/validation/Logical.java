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

package com.seer.datacruncher.validation;

import com.seer.datacruncher.constants.StreamStatus;
import com.seer.datacruncher.datastreams.DatastreamDTO;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ChecksTypeEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.jpa.entity.SchemaXSDEntity;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.language.LanguagesList;
import com.seer.datacruncher.utils.validation.MultipleValidation;
import com.seer.datacruncher.utils.validation.SingleValidation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

public class Logical implements DaoSet {

    private final Logger log = Logger.getLogger(this.getClass());
    public static Map<String,Set<String>> mapExtraCheck = new HashMap<String, Set<String>>();

    private boolean isWarningField(SchemaFieldEntity fieldEntity) {
        return fieldEntity.getErrorType() == StreamStatus.Warning.getCode();
    }

    /**
     * Cool method that invoked when schemaField validation fails. Used for
     * values adding to ResultStepValidation result.
     *
     * @param fieldEntity
     * @param retValue
     * @param errMsg
     *            - for failed schemaField
     */
    private void setErrorOrWarn(SchemaFieldEntity fieldEntity, ResultStepValidation retValue, String errMsg) {
        boolean isErrorExists = !retValue.isValid && !retValue.isWarning;
        retValue.setValid(false);
        retValue.addFailedNodePath(fieldEntity.getOrigPath("\\") + "$$" + errMsg);
        retValue.setWarning(isWarningField(fieldEntity) && !isErrorExists);
    }

    private void appendStrToResult(ResultStepValidation retValue, String str) {
        retValue.setMessageResult(retValue.getMessageResult() + str + "\n");
    }

    /**
     * Gets 'Warning: ' or 'Error: '
     *
     * @param fieldEntity
     * @return
     */
    private String getPrefix(SchemaFieldEntity fieldEntity) {
        return (isWarningField(fieldEntity) ? I18n.getMessage("success.validationWarning") : I18n.getMessage("error.error"))
                .concat(": ");
    }

    public ResultStepValidation logicalValidation(DatastreamDTO datastreamDTO, Object jaxbObject) {
        long idSchema = datastreamDTO.getIdSchema();
        Set<String> keys;
        ResultStepValidation retValue = new ResultStepValidation();
        retValue.setValid(true);
        retValue.setMessageResult("");
        SchemaXSDEntity schemaXSDEntity = schemasXSDDao.read(idSchema);
        Class<?> validationClass;
        Object validationObj;
        Method factoryMethod;

        try {
            if (mapExtraCheck.keySet().size() == 0
                    || !mapExtraCheck.keySet().iterator().next().contains(String.valueOf(datastreamDTO.getIdSchema()))) {
                mapExtraCheck = schemaFieldsDao.getMapExtraCheck(idSchema);
            }

            keys = mapExtraCheck.keySet();
            Map<String,List<String>> mapMultiClass= new HashMap<String,List<String>>();

            for (String fieldPath : keys) {
                try {
                    boolean isValid= false;
					Set<String> setClass = mapExtraCheck.get(fieldPath);
					SchemaFieldEntity fieldEntity = schemaFieldsDao.getFieldByPath(fieldPath, idSchema, "/");

					String suggestions = "";
					String elementValue = CommonUtils.parseXMLandInvokeDoSomething(new ByteArrayInputStream(datastreamDTO
                            .getOutput().getBytes()), fieldPath, jaxbObject);
					if (elementValue == null) {
						isValid = true;
					} else {

						if (setClass != null && setClass.size() > 0) {
							for (String checkInfo : setClass) {
								String checkType = checkInfo.substring(checkInfo.indexOf(":") + 1, checkInfo.lastIndexOf("_"));
								String extraCheckType = checkInfo.substring(checkInfo.indexOf("_") + 1,
										checkInfo.lastIndexOf("-"));
								String className = checkInfo.substring(checkInfo.lastIndexOf("-") + 1);

								if (checkType.contains("@spellcheck")) {

									long idCheckType = Long.parseLong(className);
									ChecksTypeEntity checksTypeEntity = checksTypeDao.find(idCheckType);

									if (checksTypeEntity == null) {
										continue;
									}
									String description = checksTypeEntity.getName();
									Locale locale = Locale.ENGLISH;
									for (LanguagesList langs : LanguagesList.values()) {
										if (description.endsWith(langs.toString())) {
											locale = langs.getLocale();
											break;
										}
									}
									SpellChecker spellChecker = new SpellChecker(locale.getLanguage());

									if (!spellChecker.exist(elementValue)) {
										String suggestion = MessageFormat.format(I18n.getMessage("message.spellCheckError"),
												getPrefix(fieldEntity), elementValue,
												Arrays.toString(spellChecker.getSuggestions(elementValue))).concat("\n");
										suggestions += suggestion;
									} else {
										isValid = true;
										break;
									}
								} else if (checkType.contains("singleValidation")) {
									if (!extraCheckType.equalsIgnoreCase("Custom code")
											&& !className.contains("com.seer.datacruncher.validation."))
										className = "com.seer.datacruncher.validation." + className;

									if (extraCheckType.equalsIgnoreCase("Custom Code")) {
										validationObj = getCustomCodeInstance(checkType, className);
									} else {
										validationClass = Class.forName(className);
										try {
											factoryMethod = validationClass.getDeclaredMethod("getInstance");
											validationObj = factoryMethod.invoke(null, (Object[]) null);
										} catch (Exception exception) {
											validationObj = validationClass.newInstance();
										}
									}
									if (validationObj != null && validationObj instanceof SingleValidation) {
										SingleValidation singleValidation = (SingleValidation) validationObj;
										ResultStepValidation localRetValue = singleValidation.checkValidity(elementValue);
										if (localRetValue != null && !localRetValue.isValid()) {
											suggestions += getPrefix(fieldEntity) + localRetValue.getMessageResult() + "\n";
										} else {
											isValid = true;
											break;
										}
									} else {
										suggestions += getPrefix(fieldEntity)
												+ MessageFormat.format(I18n.getMessage("error.validationClass"), className)
												+ "\n";
									}

								} else if (checkType.contains("multipleValidation")) {
									if (!className.contains("com.seer.datacruncher.validation."))
										className = "com.seer.datacruncher.validation." + className;
									if (mapMultiClass.size() > 0 && mapMultiClass.containsKey(className)) {
										List<String> info = mapMultiClass.get(className);

										if (info.get(0) != null && info.get(0).equals("true")) {
											isValid = true;
											break;
										} else {
											if (info.get(1) != null)
												suggestions += info.get(1);
										}
									} else {
										validationClass = Class.forName(className);
										validationObj = validationClass.newInstance();
										if (validationObj != null && validationObj instanceof MultipleValidation) {
											List<String> info;
											MultipleValidation multipleValidation = (MultipleValidation) validationObj;
											ResultStepValidation localRetValue = multipleValidation.checkValidity(datastreamDTO,
													jaxbObject, schemaXSDEntity);
											if (!localRetValue.isValid()) {
												String msg = localRetValue.getMessageResult();
												String postfix = msg.endsWith("\n") ? msg.substring(0, msg.length() - 2) : msg;
												msg = getPrefix(fieldEntity) + postfix + "\n";
												suggestions += msg;
												info = new ArrayList<String>();
												info.add(0, "false");
												info.add(1, "");
												mapMultiClass.put(className, info);
											} else {
												info = new ArrayList<String>();
												info.add(0, "true");
												info.add(1, "");
												mapMultiClass.put(className, info);
												isValid = true;
												break;
											}
										} else {
											suggestions += getPrefix(fieldEntity)
													+ MessageFormat.format(I18n.getMessage("error.validationClass"), className)
													+ "\n";
										}
									}

								}
							}
						}

						if (!isValid) {
							setErrorOrWarn(fieldEntity, retValue, suggestions);
							if (!suggestions.equals("")) {
								if (suggestions.endsWith("\n"))
									suggestions = suggestions.substring(0, suggestions.length() - 1);
								appendStrToResult(retValue, suggestions);
							}
						}

                    }

                } catch (Exception exception) {
                    log.error("Logical Validation - Exception : " + exception);
                    retValue.setValid(false);
                    appendStrToResult(retValue, I18n.getMessage("error.system"));
                    return retValue;
                }
            }

            if (retValue.getMessageResult() != null && retValue.getMessageResult().equals("")){
                retValue.setMessageResult(I18n.getMessage("success.validationOK"));
            }
        } catch (Exception exception) {
            retValue.setValid(false);
            appendStrToResult(retValue, I18n.getMessage("error.system"));
            log.error("Logical Validation - Exception : " + exception);
            return retValue;
        }
        return retValue;
    }

    private Object getCustomCodeInstance(String checkType, String className){
        try {
            ReadList list = checksTypeDao.findCustomCodeByName(className);
            if (list == null || CollectionUtils.isEmpty(list.getResults())) {
                return null;
            }
            ChecksTypeEntity checksTypeEntity = (ChecksTypeEntity)list.getResults().get(0);
            String sourceCode = checksTypeEntity.getValue();
            if(StringUtils.isEmpty(sourceCode)){
                return null;
            }
            Class<?> implementedClass;
            String classFullName;
            if(checkType.equalsIgnoreCase("singleValidation")){
                implementedClass = SingleValidation.class;
                classFullName = "com.seer.datacruncher.utils.validation.SingleValidation";
            }else{
                implementedClass = MultipleValidation.class;
                classFullName = "com.seer.datacruncher.utils.validation.MultipleValidation";
            }
            return CommonUtils.getClassInstance(className, classFullName, implementedClass, sourceCode);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}