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

import com.seer.datacruncher.constants.DateTimeType;
import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.datastreams.XSDentities.*;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.*;
import org.jsoup.Jsoup;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.seer.datacruncher.datastreams.XSDentities.Annotation;
import com.seer.datacruncher.datastreams.XSDentities.Appinfo;
import com.seer.datacruncher.jpa.entity.MacroEntity;


public class CreateXSDJAXB implements DaoSet {

    public CreateXSDJAXB() {
    }

    protected ObjectFactory objectFactory = new ObjectFactory();
    private Schema schemaXSD;
    private int idStreamType;

    public String createRoot (long idSchema) {
        schemaXSD = objectFactory.createSchema();
        SchemaEntity schemaEntity = schemasDao.find(idSchema);
        idStreamType = schemaEntity.getIdStreamType();
        Annotation annotation = objectFactory.createAnnotation();

        schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(annotation);
        addMacroToDocument(annotation, idSchema);
        
        TopLevelElement schemaNameElement = objectFactory.createTopLevelElement();
		schemaNameElement.setName(Tag.TAG_SCHEMA_NAME);
		schemaNameElement.setFixed(schemaEntity.getName());
        schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(schemaNameElement);
        
        TopLevelElement dataStreamTypeElement = objectFactory.createTopLevelElement();
        dataStreamTypeElement.setName(Tag.TAG_DATA_STREAM_TYPE);
        dataStreamTypeElement.setFixed(String.valueOf(schemaEntity.getIdStreamType()));
        schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(dataStreamTypeElement);
        
        if(schemaEntity.getIdStreamType() == StreamType.flatFileDelimited) {
        	 TopLevelElement delimiterElement = objectFactory.createTopLevelElement();
        	 delimiterElement.setName(Tag.TAG_DELIMITER_CHAR);
        	 delimiterElement.setFixed(schemaEntity.getDelimiter());
             schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(delimiterElement);
        }
        
        if(schemaEntity.getStartDate() != null) {        	
        	TopLevelElement validityStartDateElement = objectFactory.createTopLevelElement();
        	validityStartDateElement.setName(Tag.TAG_VALIDITY_START_DATE);
        	validityStartDateElement.setFixed(new SimpleDateFormat("yyyy-MM-dd").format(schemaEntity.getStartDate()));
        	schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(validityStartDateElement);        	
        } else {        	
        	TopLevelElement validityStartDateElement = objectFactory.createTopLevelElement();
        	validityStartDateElement.setName(Tag.TAG_VALIDITY_START_DATE);
        	validityStartDateElement.setFixed("");
        	schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(validityStartDateElement);        	
        }
        
        if(schemaEntity.getEndDate() != null) {
        	TopLevelElement validityEndDateElement = objectFactory.createTopLevelElement();
        	validityEndDateElement.setName(Tag.TAG_VALIDITY_END_DATE);
        	validityEndDateElement.setFixed(new SimpleDateFormat("yyyy-MM-dd").format(schemaEntity.getEndDate()));
        	schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(validityEndDateElement);
        } else {
        	TopLevelElement validityEndDateElement = objectFactory.createTopLevelElement();
        	validityEndDateElement.setName(Tag.TAG_VALIDITY_END_DATE);
        	validityEndDateElement.setFixed("");
        	schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(validityEndDateElement);
        }
        
        if(schemaEntity.getDescription().trim().length() > 0) {
        	TopLevelElement descriptionElement = objectFactory.createTopLevelElement();
        	descriptionElement.setName(Tag.TAG_DESCRIPTION);
        	descriptionElement.setFixed(schemaEntity.getDescription());
        	schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(descriptionElement);
        }
        
        if (idStreamType == StreamType.XML || idStreamType == StreamType.XMLEXI) {
            SchemaFieldEntity root;
            root = schemaFieldsDao.root(idSchema);
            TopLevelElement topLevelElement = objectFactory.createTopLevelElement();
            topLevelElement.setName(root.getName());

            String description = root.getDescription();
            if (description != null && !description.equals("")) {
                Documentation documentation = objectFactory.createDocumentation();
                documentation.getContent().add(Jsoup.parse(description).text());
                annotation.getAppinfoOrDocumentation().add(documentation);
            }
            LocalComplexType localComplexType = objectFactory.createLocalComplexType();
            if (root.getIdFieldType() == FieldType.all) {
                All all = objectFactory.createAll();

                createChild (0 , root.getIdSchemaField() , all.getParticle());
                localComplexType.setAll(all);
            } else if (root.getIdFieldType() == FieldType.choice) {
                ExplicitGroup explicitGroup = objectFactory.createExplicitGroup();

                createChild (0 , root.getIdSchemaField() , explicitGroup.getParticle());
                localComplexType.setChoice(explicitGroup);
            } else if (root.getIdFieldType() == FieldType.sequence) {

                ExplicitGroup explicitGroup = objectFactory.createExplicitGroup();

                createChild (0 , root.getIdSchemaField() , explicitGroup.getParticle());
                localComplexType.setSequence(explicitGroup);

            }
            ArrayList<SchemaFieldEntity> listAttr;
            listAttr =(ArrayList<SchemaFieldEntity>) schemaFieldsDao.listAttrChild(root.getIdSchemaField());
            if (listAttr.size()>0)
                createAttributes(listAttr,localComplexType.getAttributeOrAttributeGroup());
            topLevelElement.setComplexType(localComplexType);
            schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(topLevelElement);
        } else if (idStreamType == StreamType.flatFileFixedPosition || idStreamType == StreamType.flatFileDelimited ||
                idStreamType == StreamType.EXCEL) {
            TopLevelElement topLevelElement = objectFactory.createTopLevelElement();
            topLevelElement.setName(Tag.TAG_ROOT);
            LocalComplexType localComplexType = objectFactory.createLocalComplexType();
            ExplicitGroup sequence = objectFactory.createExplicitGroup();
            createChild (idSchema , 0 , sequence.getParticle());
            localComplexType.setSequence(sequence);
            topLevelElement.setComplexType(localComplexType);
            schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(topLevelElement);
        }else if (idStreamType == StreamType.JSON) {
            TopLevelElement topLevelElement = objectFactory.createTopLevelElement();
            topLevelElement.setName(Tag.TAG_ROOT);
            LocalComplexType localComplexType = objectFactory.createLocalComplexType();

            All all = objectFactory.createAll();

            createChild (idSchema, -1 , all.getParticle());

            localComplexType.setAll(all);

            topLevelElement.setComplexType(localComplexType);
            schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(topLevelElement);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            JAXBContext context = JAXBContext.newInstance( "com.seer.datacruncher.datastreams.XSDentities" );
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT , Boolean.TRUE);
            marshaller.marshal(schemaXSD, byteArrayOutputStream);
        } catch (JAXBException exception) {
            Logger.getLogger(CreateXSDJAXB.class.getName()).log(Level.SEVERE , null , exception);
            return null;
        }
        return byteArrayOutputStream.toString();
    }

    private boolean createRestriction(SchemaFieldEntity field ,Restriction restriction,TopLevelSimpleType topLevelSimpleType, String localSympleTypeName){
        boolean restricted =false;
        Union unionAllowValues = null;
        
        if (field.getIdFieldType() == FieldType.alphanumeric) {

            restriction.setBase(new QName("xs:string"));
            Integer minLength = field.getMinLength();
            if (minLength != null) {
                NumFacet minFacet = objectFactory.createNumFacet();
                minFacet.setValue(minLength.toString());
                JAXBElement<?> minLenght = objectFactory.createMinLength(minFacet);
                restriction.getFacets().add(minLenght);
                restricted = true;
            }
            Integer maxLength = field.getMaxLength();
            if (maxLength != null) {
                NumFacet maxFacet = objectFactory.createNumFacet();
                maxFacet.setValue(maxLength.toString());
                JAXBElement<?> maxLenght = objectFactory.createMaxLength(maxFacet);
                restriction.getFacets().add(maxLenght);
                restricted = true;
            }
            NoFixedFacet enumeration;
            JAXBElement<?> enumElement;
            ArrayList<AlphanumericFieldValuesEntity> listAlphanumericFieldValues;
            listAlphanumericFieldValues = (ArrayList<AlphanumericFieldValuesEntity>) alphaFieldDao.listAlphanumericFieldValues(field.getIdSchemaField()) ;
            for (AlphanumericFieldValuesEntity listAlphanumericFieldValue : listAlphanumericFieldValues) {
                enumeration = objectFactory.createNoFixedFacet();
                enumeration.setValue(listAlphanumericFieldValue.getValue());
                enumElement = objectFactory.createEnumeration(enumeration);
                restriction.getFacets().add(enumElement);
                restricted = true;
            }
            List<ChecksTypeEntity> list  = checksTypeDao.getChecksTypeBySchemaFiledId(field.getIdSchemaField());
            if (list != null && list.size()>0) {
                int i = 0;
                while (i < list.size()) {
                    ChecksTypeEntity checksTypeEntity = list.get(i);
                    if (checksTypeEntity != null && checksTypeEntity.isRegExp()) {
                        String regExp = checksTypeEntity.getValue();
                        if (regExp != null) {
                            Pattern pattern = objectFactory.createPattern();
                            pattern.setValue(regExp);
                            restriction.getFacets().add(pattern);
                            restricted = true;
                        }
                    }
                    i++;
                }
            }
        } else if (field.getIdFieldType() == FieldType.numeric) {
            Integer fractionDigit = field.getFractionDigits();
            int idNumericType= field.getIdNumericType();
            String minInclusive = null;
            String maxInclusive = null;
            if(idNumericType == 1){
                restriction.setBase(new QName("xs:integer"));
                if (field.getMinInclusive()!= null) {
                	minInclusive = calcErrorToleratedValue(field.getMinInclusive(), field.getErrorToleranceValue(), true);                    
                }
                if (field.getMaxInclusive()!= null) {
                	maxInclusive =  calcErrorToleratedValue(field.getMaxInclusive(), field.getErrorToleranceValue(), false);
                }
            }else{
                restriction.setBase(new QName("xs:decimal"));
                if (field.getMinInclusive()!= null) {
                	minInclusive = calcErrorToleratedValue(field.getMinInclusive(), field.getErrorToleranceValue(), true);
                }
                if (field.getMaxInclusive()!= null) {
                	maxInclusive =  calcErrorToleratedValue(field.getMaxInclusive(), field.getErrorToleranceValue(), false);
                }
            }

            if (fractionDigit != null) {
                NumFacet numFacet = objectFactory.createNumFacet();
                numFacet.setValue(fractionDigit.toString());
                JAXBElement<?> element = objectFactory.createFractionDigits(numFacet);
                restriction.getFacets().add(element);
                restricted = true;

            }

            if (minInclusive != null) {
                NumFacet numFacet = objectFactory.createNumFacet();
                numFacet.setValue(minInclusive);
                JAXBElement<?> element = objectFactory.createMinInclusive(numFacet);
                restriction.getFacets().add(element);
                restricted = true;
            }

            
            if (maxInclusive != null) {
                NumFacet numFacet = objectFactory.createNumFacet();
                numFacet.setValue(maxInclusive);
                JAXBElement<?> element = objectFactory.createMaxInclusive(numFacet);
                restriction.getFacets().add(element);
                restricted = true;
            }
            NoFixedFacet enumeration;
            JAXBElement<?> enumElem;
            ArrayList<NumericFieldValuesEntity> listNumericFieldValues = (ArrayList<NumericFieldValuesEntity>) numericFieldDao.listNumericFieldValues(field.getIdSchemaField());
			if (listNumericFieldValues != null && listNumericFieldValues.size() > 0) {				
				if(field.getErrorToleranceValue() > 0) { 
					unionAllowValues = objectFactory.createUnion();
                    for (NumericFieldValuesEntity listNumericFieldValue : listNumericFieldValues) {

                        LocalSimpleType localSimpleType = new LocalSimpleType();
                        Restriction allowValueRestriction = new Restriction();
                        allowValueRestriction.setBase(new QName("xs:decimal"));
                        NumFacet numFacet = objectFactory.createNumFacet();
                        numFacet.setValue(calcErrorToleratedValue(Double.parseDouble(listNumericFieldValue.getValue()),
                                field.getErrorToleranceValue(), true));
                        JAXBElement<?> element = objectFactory.createMinInclusive(numFacet);
                        allowValueRestriction.getFacets().add(element);

                        numFacet = objectFactory.createNumFacet();
                        numFacet.setValue(calcErrorToleratedValue(Double.parseDouble(listNumericFieldValue.getValue()),
                                field.getErrorToleranceValue(), false));
                        element = objectFactory.createMaxInclusive(numFacet);
                        allowValueRestriction.getFacets().add(element);

                        localSimpleType.setRestriction(allowValueRestriction);
                        unionAllowValues.getSimpleType().add(localSimpleType);

                        topLevelSimpleType.setUnion(unionAllowValues);
                        restricted = true;
                    }
				} else {
                    for (NumericFieldValuesEntity listNumericFieldValue : listNumericFieldValues) {
                        enumeration = objectFactory.createNoFixedFacet();
                        enumeration.setValue(listNumericFieldValue.getValue());
                        enumElem = objectFactory.createEnumeration(enumeration);
                        restriction.getFacets().add(enumElem);
                        restricted = true;
                    }
				}
			}

        } else if (field.getIdFieldType() == FieldType.date) {
            String regDateType = "";
            String regTimeType = "";
            String delimiter;
            Boolean isDate = true;
            Boolean isTime = true;

            if (field.getIdTimeType() !=  null){
                switch (field.getIdTimeType()) {
                    case DateTimeType.dblpnthhmmss:  // hh:mm:ss
                        regTimeType  = "(([2][1-3]|[01][0-9])[:]([0-5][0-9])[:]([0-5][0-9]))";
                        break;
                    case DateTimeType.dothhmmss:  // hh.mm.ss
                        regTimeType  = "(([2][1-3]|[01][0-9])[.]([0-5][0-9])[.]([0-5][0-9]))";
                        break;
                    case DateTimeType.dblpnthmmss:  // h:mm:ss
                        regTimeType  = "(([0-9]|[2][1-3]|[1][0-9])[:]([0-5][0-9])[:]([0-5][0-9]))";
                        break;
                    case DateTimeType.dothmmss:  // h.mm.ss
                        regTimeType  = "(([0-9]|[2][1-3]|[1][0-9])[.]([0-5][0-9])[.]([0-5][0-9]))";
                        break;
                    case DateTimeType.dblpnthhmm:  // hh:mm
                        regTimeType  = "(([2][1-3]|[01][0-9])[:]([0-5][0-9]))";
                        break;
                    case DateTimeType.dothhmm:  // hh.mm
                        regTimeType  = "(([2][1-3]|[01][0-9])[.]([0-5][0-9]))";
                        break;
                    case DateTimeType.dblpntZhhmmss:  // hh:mm:ss AM/PM
                        regTimeType  = "(([0][0-9]|[1][012])[:]([0-5][0-9])[:]([0-5][0-9])(\\s[AP]M))";
                        break;
                    case DateTimeType.dotZhhmmss:  // hh.mm.ss AM/PM
                        regTimeType  = "(([0][0-9]|[1][012])[.]([0-5][0-9])[.]([0-5][0-9])(\\s[AP]M))";
                        break;
                }
            } else {
                isTime = false;
            }
            if (field.getIdDateType()!= null) {
                if (field.getIdDateType() == 1 || field.getIdDateType() == 4 || field.getIdDateType() == 7){
                    delimiter = "[/]";
                } else if (field.getIdDateType() == 2 || field.getIdDateType() == 5 || field.getIdDateType() == 8){
                    delimiter = "[-]";
                } else if (field.getIdDateType() == 3 || field.getIdDateType() == 6 || field.getIdDateType() == 9){
                    delimiter = "[.]";
                } else {
                    delimiter = "";
                }

                switch (field.getIdDateType()) {
                    case DateTimeType.slashDDMMYYYY :  // dd/MM/yyyy
                    case DateTimeType.signDDMMYYYY :  // dd-MM-yyyy
                    case DateTimeType.dotDDMMYYYY:  // dd.MM.yyyy
                    case DateTimeType.DDMMYYYY:  // ddMMyyyy
                        regDateType  = "(((0[1-9]|[12]\\d|3[01])" + delimiter + "(0[13578]|1[02])" + delimiter + "(\\d{4}))|((0[1-9]|[12]\\d|30)" + delimiter + "(0[13456789]|1[012])" + delimiter + "(\\d{4}))|((0[1-9]|1\\d|2[0-8])" + delimiter + "02" + delimiter + "(\\d{4}))|((29)" + delimiter + "(02)" + delimiter + "(((\\d{2})00)|((\\d{2})[0][48])|((\\d{2})[2468][048])|((\\d{2})[13579][26]))))";
                        break;
                    case DateTimeType.slashDDMMYY:  // dd/MM/yy
                    case DateTimeType.signDDMMYY:  // dd-MM-yy
                    case DateTimeType.dotDDMMYY:  // dd.MM.yy
                    case DateTimeType.DDMMYY:  // ddMMyy
                        regDateType  = "(((0[1-9]|[12]\\d|3[01])" + delimiter + "(0[13578]|1[02])" + delimiter + "(\\d{2}))|((0[1-9]|[12]\\d|30)" + delimiter + "(0[13456789]|1[012])" + delimiter + "(\\d{2}))|((0[1-9]|1\\d|2[0-8])" + delimiter + "02" + delimiter + "(\\d{2}))|((29)" + delimiter + "(02)" + delimiter + "((00)|([0][48])|([2468][048])|([13579][26]))))";
                        break;
                    case DateTimeType.slashYYYYMMDD:  // yyyy/MM/dd
                    case DateTimeType.signYYYYMMDD:  // yyyy-MM-dd
                    case DateTimeType.dotYYYYMMDD:  // yyyy.MM.dd
                    case DateTimeType.YYYYMMDD:  // yyyyMMdd
                        regDateType  = "(((\\d{4})" + delimiter + "(0[13578]|1[02])" + delimiter + "(0[1-9]|[12]\\d|3[01]))|((\\d{4})" + delimiter + "(0[13456789]|1[012])" + delimiter + "(0[1-9]|[12]\\d|30))|((\\d{4})" + delimiter + "02" + delimiter + "(0[1-9]|1\\d|2[0-8]))|((((\\d{2})00)|((\\d{2})[0][48])|((\\d{2})[2468][048])|((\\d{2})[13579][26]))(" + delimiter + "02" + delimiter + "29)))";
                        break;
                }
            } else {
                isDate = false;
            }

            restriction.setBase(new QName("xs:string"));
            Pattern pattern = objectFactory.createPattern();
            if (isTime && isDate) {
                pattern.setValue("(" + regDateType + "(\\s)" + regTimeType +")");
            } else {
                if (isDate) {
                    pattern.setValue(regDateType);
                } else if (isTime) {
                    pattern.setValue(regTimeType);
                }
            }
            restriction.getFacets().add(pattern);
            restricted = true;
        }
        if (restricted){
            topLevelSimpleType.setName(localSympleTypeName);
            if(unionAllowValues == null) {
            	topLevelSimpleType.setRestriction(restriction);
            }
            schemaXSD.getSimpleTypeOrComplexTypeOrGroup().add(topLevelSimpleType);
        }
        return restricted;

    }
    private void createFlatFileFixedAttributes(List<Annotated> attributes, SchemaFieldEntity field ){
        Attribute attribute;
        attribute = objectFactory.createAttribute();
        attribute.setName(Tag.TAG_SIZE);
        attribute.setType(new QName("xs:string"));
        attribute.setFixed(field.getSize());
        attributes.add(attribute);
        if(field.getIdAlign() != null){
            attribute = objectFactory.createAttribute();
            attribute.setName(Tag.TAG_ALIGN);
            attribute.setType(new QName("xs:string"));
            attribute.setFixed(field.getIdAlign().toString());
            attributes.add(attribute);
        }
        if(field.getFillChar() != null){
            attribute = objectFactory.createAttribute();
            attribute.setName(Tag.TAG_FILL);
            attribute.setType(new QName("xs:string"));
            attribute.setFixed(field.getFillChar());
            attributes.add(attribute);
        }
    }
    private void createAttributes(ArrayList<SchemaFieldEntity> listAttr, List<Annotated> attributes){
        Attribute attribute;
        for (SchemaFieldEntity aListAttr : listAttr) {
            attribute = objectFactory.createAttribute();
            attribute.setName(aListAttr.getName());
            String localSympleTypeName = aListAttr.getName() + "Type" + aListAttr.getIdSchemaField();
            if (!aListAttr.getNillable()) {
                attribute.setUse("required");
            }
            boolean annotated = false;
            Annotation annotation = objectFactory.createAnnotation();
            Restriction restriction = objectFactory.createRestriction();
            TopLevelSimpleType topLevelSimpleType = objectFactory.createTopLevelSimpleType();
            String description = aListAttr.getDescription();
            if (description != null && !description.equals("")) {
                Documentation documentation = objectFactory.createDocumentation();
                documentation.getContent().add(Jsoup.parse(description).text());
                annotation.getAppinfoOrDocumentation().add(documentation);
                annotated = true;
                attribute.setAnnotation(annotation);
            }

            boolean restricted;

            if (aListAttr.getIdFieldType() == FieldType.alphanumeric) {
                List<ChecksTypeEntity> list = checksTypeDao.getChecksTypeBySchemaFiledId(aListAttr.getIdSchemaField());
                if (list != null && list.size() > 0) {

                    int i = 0;
                    while (i < list.size()) {
                        ChecksTypeEntity checksTypeEntity = list.get(i);
                        if (checksTypeEntity.getTokenRule() != null) {
                            Appinfo appinfo = objectFactory.createAppinfo();
                            appinfo.getContent().add(
                                    checksTypeEntity.getTokenRule());
                            if (annotated)
                                annotation = attribute.getAnnotation();
                            annotation.getAppinfoOrDocumentation().add(appinfo);
                            attribute.setAnnotation(annotation);
                        }
                        i++;
                    }
                }
                restricted = createRestriction(aListAttr, restriction, topLevelSimpleType, localSympleTypeName);
                if (restricted) {
                    attribute.setType(new QName(localSympleTypeName));
                } else {
                    attribute.setType(new QName("xs:string"));
                }
            } else if (aListAttr.getIdFieldType() == FieldType.numeric) {

                restricted = createRestriction(aListAttr, restriction, topLevelSimpleType, localSympleTypeName);
                if (restricted) {
                    attribute.setType(new QName(localSympleTypeName));
                } else {
                    int idNumericType = aListAttr.getIdNumericType();
                    if (idNumericType == 1) {
                        attribute.setType(new QName("xs:integer"));
                    } else {
                        attribute.setType(new QName("xs:decimal"));
                    }

                }

            } else if (aListAttr.getIdFieldType() == FieldType.date) {
                if (aListAttr.getIdDateTimeType() == DateTimeType.xsdDate) {
                    attribute.setType(new QName("xs:date"));
                } else if (aListAttr.getIdDateTimeType() == DateTimeType.xsdTime) {
                    attribute.setType(new QName("xs:time"));
                } else if (aListAttr.getIdDateTimeType() == DateTimeType.xsdDateTime) {
                    attribute.setType(new QName("xs:dateTime"));
                } else if (aListAttr.getIdDateTimeType() == DateTimeType.unixTimestamp) {
                    attribute.setType(new QName("xs:string"));
                    Appinfo appinfo = objectFactory.createAppinfo();
                    appinfo.getContent().add("@unixDate");
                    if (annotated)
                        annotation = attribute.getAnnotation();
                    annotation.getAppinfoOrDocumentation().add(appinfo);
                    attribute.setAnnotation(annotation);
                } else {
                    Appinfo appInfo = objectFactory.createAppinfo();
                    String tDateTime = aListAttr.getIdDateTimeType() + "";
                    String tDate = aListAttr.getIdDateType() != null ? aListAttr.getIdDateType() + "" : "0";
                    String tTime = aListAttr.getIdTimeType() != null ? aListAttr.getIdTimeType() + "" : "0";

                    appInfo.getContent().add("@jvDate:" + tDateTime + "-" + tDate + "-" + tTime);

                    if (annotated)
                        annotation = attribute.getAnnotation();
                    annotation.getAppinfoOrDocumentation().add(appInfo);
                    attribute.setAnnotation(annotation);
                    restricted = createRestriction(aListAttr, restriction, topLevelSimpleType, localSympleTypeName);
                    attribute.setType(new QName(localSympleTypeName));
                }
            }
            attributes.add(attribute);
        }

    }
    protected void addMacroToDocument(Annotation annotation, long idSchema) {
        @SuppressWarnings("rawtypes")
        List macros = macrosDao.read(idSchema).getResults();
        if (macros.size() > 0) {
            Appinfo appInfo = objectFactory.createAppinfo();
            for (Object o : macros) {
                MacroEntity ent = (MacroEntity) o;
                if (ent.getIsActive() == 1) {
                    appInfo.getContent().add("@RuleCheck:" + ent.getName() + ";\n");
                }
            }
            annotation.getAppinfoOrDocumentation().add(appInfo);
        }
    }

    private void createChild (long idSchema , long idParent, List<Object> particle) {
        ArrayList<SchemaFieldEntity> listChild;
        if (idParent == -1) { //Json schema type
            listChild =(ArrayList<SchemaFieldEntity>) schemaFieldsDao.listElemChild(idSchema, 0);
        }else{
            if (idParent != 0) {//xml schema type
                listChild =(ArrayList<SchemaFieldEntity>) schemaFieldsDao.listElemChild(idParent);
            } else {   //not xml schema type
                listChild =(ArrayList<SchemaFieldEntity>) schemaFieldsDao.listSchemaFields(idSchema);
            }
        }
        for (SchemaFieldEntity aListChild : listChild) {
            boolean restricted;
            boolean annotated = false;
            TopLevelElement topLevelElement = objectFactory.createTopLevelElement();
            Annotation annotation = objectFactory.createAnnotation();
            ArrayList<SchemaFieldEntity> listAttr = (ArrayList<SchemaFieldEntity>) schemaFieldsDao.listAttrChild(aListChild.getIdSchemaField());
            String description = aListChild.getDescription();

            topLevelElement.setName(aListChild.getName());
            if (aListChild.getMaxOccurs() == 0) {
                topLevelElement.setMaxOccurs("unbounded");
            } else if (aListChild.getMaxOccurs() > 1) {
                topLevelElement.setMaxOccurs(aListChild.getMaxOccurs().toString());
            }


            if (description != null && !description.equals("")) {
                Documentation documentation = objectFactory.createDocumentation();
                documentation.getContent().add(Jsoup.parse(description).text());
                annotation.getAppinfoOrDocumentation().add(documentation);
                annotated = true;
                topLevelElement.setAnnotation(annotation);
            }

            if (aListChild.getIdFieldType() > 3) {


                TopLevelSimpleType topSympleType = objectFactory.createTopLevelSimpleType();

                LocalComplexType localComplexType = objectFactory.createLocalComplexType();
                SimpleContent simpleContent = objectFactory.createSimpleContent();
                SimpleExtensionType extension = objectFactory.createSimpleExtensionType();

                Restriction restriction = objectFactory.createRestriction();
                String localSympleTypeName = aListChild.getName() + "Type" + aListChild.getIdSchemaField();
                QName qName = new QName(localSympleTypeName);

                if (aListChild.getNillable())
                    topLevelElement.setMinOccurs(BigInteger.ZERO);


                if (aListChild.getIdFieldType() == FieldType.alphanumeric) {
                    List<ChecksTypeEntity> list = checksTypeDao.getChecksTypeBySchemaFiledId(aListChild.getIdSchemaField());
                    if (list != null && list.size() > 0) {

                        int i = 0;
                        while (i < list.size()) {
                            ChecksTypeEntity checksTypeEntity = list.get(i);
                            if (checksTypeEntity.getTokenRule() != null) {
                                Appinfo appinfo = objectFactory.createAppinfo();
                                appinfo.getContent().add(
                                        checksTypeEntity.getTokenRule());
                                if (annotated)
                                    annotation = topLevelElement.getAnnotation();
                                annotation.getAppinfoOrDocumentation().add(appinfo);
                                topLevelElement.setAnnotation(annotation);
                            }
                            i++;
                        }
                    }

                    restricted = createRestriction(aListChild, restriction, topSympleType, localSympleTypeName);

                    if (!restricted)
                        qName = new QName("xs:string");

                } else if (aListChild.getIdFieldType() == FieldType.numeric) {
                    if (aListChild.getIsForecastable()) {
                        Appinfo appInfo = objectFactory.createAppinfo();
                        appInfo.getContent().add(
                                "@forecasting:" + aListChild.getForecastAccuracy() + "-"
                                        + aListChild.getForecastSpeed());
                        annotation.getAppinfoOrDocumentation().add(appInfo);
                        topLevelElement.setAnnotation(annotation);
                    }

                    restricted = createRestriction(aListChild, restriction, topSympleType, localSympleTypeName);
                    if (!restricted) {
                        int idNumericType = aListChild.getIdNumericType();
                        if (idNumericType == 1) {
                            qName = new QName("xs:integer");
                        } else {
                            qName = new QName("xs:decimal");
                        }
                    }


                } else if (aListChild.getIdFieldType() == FieldType.date) {
                    if (aListChild.getIdDateTimeType() == DateTimeType.unixTimestamp) {
                        Appinfo appInfo = objectFactory.createAppinfo();
                        appInfo.getContent().add("@unixDate");
                        if (annotated)
                            annotation = topLevelElement.getAnnotation();
                        annotation.getAppinfoOrDocumentation().add(appInfo);
                        topLevelElement.setAnnotation(annotation);
                        qName = new QName("xs:string");
                    } else {
                        if (aListChild.getIdDateTimeType() == DateTimeType.xsdDate) {
                            qName = new QName("xs:date");
                        } else if (aListChild.getIdDateTimeType() == DateTimeType.xsdTime) {
                            qName = new QName("xs:time");
                        } else if (aListChild.getIdDateTimeType() == DateTimeType.xsdDateTime) {
                            qName = new QName("xs:dateTime");
                        } else {
                            Appinfo appInfo = objectFactory.createAppinfo();
                            String tDateTime = aListChild.getIdDateTimeType() + "";
                            String tDate = aListChild.getIdDateType() != null ? aListChild.getIdDateType() + "" : "0";
                            String tTime = aListChild.getIdTimeType() != null ? aListChild.getIdTimeType() + "" : "0";

                            appInfo.getContent().add("@jvDate:" + tDateTime + "-" + tDate + "-" + tTime);

                            if (annotated)
                                annotation = topLevelElement.getAnnotation();
                            annotation.getAppinfoOrDocumentation().add(appInfo);
                            topLevelElement.setAnnotation(annotation);
                            restricted = createRestriction(aListChild, restriction, topSympleType, localSympleTypeName);
                        }
                    }
                }
                if (idStreamType == StreamType.flatFileFixedPosition) {
                    extension.setBase(qName);
                    createFlatFileFixedAttributes(extension.getAttributeOrAttributeGroup(), aListChild);
                    simpleContent.setExtension(extension);
                    localComplexType.setSimpleContent(simpleContent);
                    topLevelElement.setComplexType(localComplexType);
                } else if (listAttr.size() > 0) {
                    extension.setBase(qName);
                    createAttributes(listAttr, extension.getAttributeOrAttributeGroup());
                    simpleContent.setExtension(extension);
                    localComplexType.setSimpleContent(simpleContent);
                    topLevelElement.setComplexType(localComplexType);
                } else {
                    topLevelElement.setType(qName);
                }


                JAXBElement<?> element = objectFactory.createElement(topLevelElement);
                particle.add(element);

            } else {

                LocalComplexType localComplexType = objectFactory.createLocalComplexType();
                if (aListChild.getIdFieldType() == FieldType.all) {
                    All all = objectFactory.createAll();

                    createChild(0, aListChild.getIdSchemaField(), all.getParticle());
                    localComplexType.setAll(all);
                } else if (aListChild.getIdFieldType() == FieldType.choice) {
                    ExplicitGroup explicitGroup = objectFactory.createExplicitGroup();

                    createChild(0, aListChild.getIdSchemaField(), explicitGroup.getParticle());
                    localComplexType.setChoice(explicitGroup);
                } else if (aListChild.getIdFieldType() == FieldType.sequence) {
                    ExplicitGroup explicitGroup = objectFactory.createExplicitGroup();

                    createChild(0, aListChild.getIdSchemaField(), explicitGroup.getParticle());
                    localComplexType.setSequence(explicitGroup);
                }


                if (listAttr.size() > 0)
                    createAttributes(listAttr, localComplexType.getAttributeOrAttributeGroup());

                topLevelElement.setComplexType(localComplexType);

                JAXBElement<?> element = objectFactory.createElement(topLevelElement);
                particle.add(element);
            }
        }
    }
    private String calcErrorToleratedValue(Double value, int errorToleranceValue, boolean isStartingValue) {
    	String errorToleratedValue;
    	long result;
        if(errorToleranceValue > 0) {
        	if(isStartingValue)
        		value = value - ((errorToleranceValue * value ) / 100);
        	else
        		value = value + ((errorToleranceValue * value ) / 100);
        	
        	result = Math.round(value);
        	errorToleratedValue = Long.toString(result);
        } else {
        	int temp = (int)value.doubleValue();
            double d1 = value - temp;
            
            if(d1 == 0.0f) {
            	errorToleratedValue = Long.toString(temp);
           	} else {
           		errorToleratedValue = value.toString();
            }
        }   

    	return errorToleratedValue;
    }
}