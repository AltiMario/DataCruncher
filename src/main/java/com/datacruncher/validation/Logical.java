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

package com.datacruncher.validation;

import com.datacruncher.constants.StreamStatus;
import com.datacruncher.datastreams.DatastreamDTO;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.*;
import com.datacruncher.utils.generic.CommonUtils;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.utils.language.LanguagesList;
import com.datacruncher.utils.validation.MultipleValidation;
import com.datacruncher.utils.validation.SingleValidation;
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
    public static Map<String, Set<String>> mapExtraCheck = new HashMap<String, Set<String>>();

    private boolean isWarningField(SchemaFieldEntity fieldEntity) {
        return fieldEntity.getErrorType() == StreamStatus.Warning.getCode();
    }

    /**
     * Cool method that invoked when schemaField validation fails. Used for
     * values adding to ResultStepValidation result.
     *
     * @param fieldEntity
     * @param retValue
     * @param errMsg      - for failed schemaField
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

            List<CustomErrorEntity> customErrors = (List<CustomErrorEntity>) customErrorsDao.read(idSchema).getResults();

            keys = mapExtraCheck.keySet();
            Map<String, List<String>> mapMultiClass = new HashMap<String, List<String>>();

            for (String fieldPath : keys) {
                try {
                    boolean isValid = false;
                    Set<String> setClass = mapExtraCheck.get(fieldPath);
                    SchemaFieldEntity fieldEntity = schemaFieldsDao.getFieldByPath(fieldPath, idSchema, "/");

//                    String suggestions = "";
                    StringBuilder errorMessageBuilder = new StringBuilder();
                    String elementValue = CommonUtils.parseXMLandInvokeDoSomething(new ByteArrayInputStream(datastreamDTO
                            .getOutput().getBytes()), fieldPath, jaxbObject);
                    if (elementValue == null) {
                        isValid = true;
                    } else {
                        if (setClass != null && setClass.size() > 0) {
                            for (String checkInfo : setClass) {
                                ValidationCheckInfo validationCheckInfo = ValidationCheckInfo.parse(checkInfo);
                                String checkType = validationCheckInfo.getCheckType();
                                String extraCheckType = validationCheckInfo.getExtraCheckType();
                                String className = validationCheckInfo.getClassName();

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
                                        errorMessageBuilder.append(suggestion);
                                    } else {
                                        isValid = true;
                                        break;
                                    }
                                } else if (checkType.contains("singleValidation")) {
                                    if (!extraCheckType.equalsIgnoreCase("Custom code")
                                            && !className.contains("com.datacruncher.validation."))
                                        className = "com.datacruncher.validation." + className;

                                    if (extraCheckType.equalsIgnoreCase("Custom Code")) {
                                        validationObj = getCustomCodeInstance(
                                                checkType.equalsIgnoreCase("singleValidation"), className);
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
                                            addFieldError(fieldEntity, errorMessageBuilder, customErrors, new Object() {
                                                @Override
                                                public String toString() {
                                                    return getPrefix(fieldEntity) + localRetValue.getMessageResult() + "\n";
                                                }
                                            });
                                        } else {
                                            isValid = true;
                                            break;
                                        }
                                    } else {
                                        final String realClassName = className;
                                        addFieldError(fieldEntity, errorMessageBuilder, customErrors, new Object() {
                                            @Override
                                            public String toString() {
                                                return getPrefix(fieldEntity)
                                                        + MessageFormat.format(I18n.getMessage("error.validationClass"), realClassName)
                                                        + "\n";
                                            }
                                        });
                                    }
                                } else if (checkType.contains("multipleValidation")) {
                                    if (!className.contains("com.datacruncher.validation."))
                                        className = "com.datacruncher.validation." + className;
                                    if (mapMultiClass.size() > 0 && mapMultiClass.containsKey(className)) {
                                        List<String> info = mapMultiClass.get(className);

                                        if (info.get(0) != null && info.get(0).equals("true")) {
                                            isValid = true;
                                            break;
                                        } else {
                                            if (info.get(1) != null) {
                                                addFieldError(fieldEntity, errorMessageBuilder, customErrors, info.get(1));
                                            }
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
                                                addFieldError(fieldEntity, errorMessageBuilder, customErrors, new Object() {
                                                    @Override
                                                    public String toString() {
                                                        String msg = localRetValue.getMessageResult();
                                                        String postfix = msg.endsWith("\n") ? msg.substring(0, msg.length() - 2) : msg;
                                                        return getPrefix(fieldEntity) + postfix + "\n";
                                                    }
                                                });
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
                                            final String realClassName = className;
                                            addFieldError(fieldEntity, errorMessageBuilder, customErrors, new Object() {
                                                @Override
                                                public String toString() {
                                                    return getPrefix(fieldEntity)
                                                            + MessageFormat.format(I18n.getMessage("error.validationClass"), realClassName)
                                                            + "\n";
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }

                        if (!isValid) {
                            // TODO Use CompositeResultStepValidation
                            String suggestions = errorMessageBuilder.toString();
                            setErrorOrWarn(fieldEntity, retValue, suggestions);
                            if (!suggestions.equals("")) {
                                appendStrToResult(retValue, StringUtils.removeEnd(suggestions, "\n"));
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

            if (retValue.getMessageResult() != null && retValue.getMessageResult().equals("")) {
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

    /**
     * @param field
     * @param messageBuilder
     * @param customErrors
     * @param messageFormatter Object that overrides toString() method to format error message
     */
    private void addFieldError(SchemaFieldEntity field, StringBuilder messageBuilder, List<CustomErrorEntity> customErrors, Object messageFormatter) {
        boolean messageAdded = false;
        if (field != null && field.hasCustomError()) {
            final Optional<CustomErrorEntity> customErrorEntity = customErrors.stream()
                    .filter(e -> e.getId() == field.getIdCustomError())
                    .findFirst();
            customErrorEntity.ifPresent(c -> messageBuilder.append(c.getDescription()));
            messageAdded = customErrorEntity.isPresent();
        }
        if (!messageAdded) {
            messageBuilder.append(messageFormatter.toString());
        }
    }

    public ResultStepValidation performSingleCheck(ChecksTypeEntity checkType, String elementValue) {
        ResultStepValidation result = null;
        try {
            Object validationObject;
            if (checkType.isCustomCode()) {
                validationObject = getCustomCodeInstance(checkType.isSingleValidation(), checkType.getRealClassName());
            } else {
                Class<?> validationClass;
                validationClass = Class.forName(checkType.getRealClassName());
                try {
                    Method factoryMethod = validationClass.getDeclaredMethod("getInstance");
                    validationObject = factoryMethod.invoke(null, (Object[]) null);
                } catch (Exception exception) {
                    validationObject = validationClass.newInstance();
                }
            }
            if (validationObject != null && validationObject instanceof SingleValidation) {
                SingleValidation singleValidation = (SingleValidation) validationObject;
                result = singleValidation.checkValidity(elementValue);
            }
        } catch (Exception ex) {
            log.error("Logical Validation - Exception", ex);
            result = new ResultStepValidation();
            result.setValid(false);
            appendStrToResult(result, I18n.getMessage("error.system"));
        }
        return result;
    }

    private Object getCustomCodeInstance(boolean singleValidation, String className) {
        try {
            ReadList list = checksTypeDao.findCustomCodeByName(className);
            if (list == null || CollectionUtils.isEmpty(list.getResults())) {
                return null;
            }
            ChecksTypeEntity checksTypeEntity = (ChecksTypeEntity) list.getResults().get(0);
            String sourceCode = checksTypeEntity.getValue();
            if (StringUtils.isEmpty(sourceCode)) {
                return null;
            }
            Class<?> implementedClass;
            String classFullName;
            if (singleValidation) {
                implementedClass = SingleValidation.class;
                classFullName = "SingleValidation";
            } else {
                implementedClass = MultipleValidation.class;
                classFullName = "MultipleValidation";
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