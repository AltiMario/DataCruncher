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
import com.seer.datacruncher.constants.GenericType;
import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.datastreams.XSDentities.*;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ChecksTypeEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldCheckTypesEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.jpa.entity.SchemaXSDEntity;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XSDFieldImporter implements DaoSet {

    private long schemaId;

    private Schema schemaModel;
    private List<OpenAttrs> topSimpleTypeOrComplexType;
    private Map<String, Map<String, Integer>> typeMap;
    private List<String> fieldImporterResults;


    public static class XsdSchemaFieldInfo {

        private Map<SchemaFieldEntity, List<SchemaFieldEntity>> fields;
        private Map<SchemaFieldEntity, List<String>> fieldEnumeration;
        private List<String> fieldInfoResults;
        private long idSchema;

        public List<String> getFieldInfoResults() {
            return fieldInfoResults;
        }

        public void setFieldInfoResults(List<String> fieldInfoResults) {
            this.fieldInfoResults = fieldInfoResults;
        }

        public XsdSchemaFieldInfo(long idSchema) {
            fields = new IdentityHashMap<SchemaFieldEntity, List<SchemaFieldEntity>>();
            fieldEnumeration = new IdentityHashMap<SchemaFieldEntity, List<String>>();
            fieldInfoResults = new ArrayList<String>();
            this.idSchema = idSchema;
        }

        void addField(SchemaFieldEntity field, SchemaFieldEntity parent) {
            List<SchemaFieldEntity> list = fields.get(parent);
            if (list == null) {
                fields.put(parent, list = new ArrayList<SchemaFieldEntity>());
            }

            list.add(field);
        }
        void updField(SchemaFieldEntity parent) {
            List<SchemaFieldEntity> list = fields.get(parent);
            if (list == null) {
                fields.put(parent, list = new ArrayList<SchemaFieldEntity>());
            } else{
                fields.remove(parent);
                fields.put(parent, list);
            }
        }

        void addFieldEnumeration(SchemaFieldEntity field, String value) {
            List<String> list = fieldEnumeration.get(field);
            if (list == null) {
                fieldEnumeration.put(field, list = new ArrayList<String>());
            }

            list.add(value);
        }

        public List<SchemaFieldEntity> getTopLevelFields() {
            // return all top level elements, Typically will be one
            return fields.get(null);
        }

        public List<SchemaFieldEntity> getChildren(
                SchemaFieldEntity schemaFieldEntity) {
            return fields.get(schemaFieldEntity);
        }

        public List<String> getFieldEnumeration(
                SchemaFieldEntity schemaFieldEntity) {
            return fieldEnumeration.get(schemaFieldEntity);
        }

        public long getIdSchema() {
            return idSchema;
        }

    }

    public XSDFieldImporter(SchemaXSDEntity schemaEntity) {
        this(schemaEntity, defaultTypeMap());

    }

    private static Map<String, Map<String, Integer>> defaultTypeMap() {
        Map<String, Map<String, Integer>> defaultMap = new HashMap<String, Map<String, Integer>>();
        // Full list is here
        // http://www.w3.org/TR/xmlschema-2/#primitive-vs-derived
        Map<String, Integer> xmlnsTypeMap = new HashMap<String, Integer>();

        xmlnsTypeMap.put("string", FieldType.alphanumeric);
        xmlnsTypeMap.put("anyURI", FieldType.alphanumeric);
        xmlnsTypeMap.put("normalizedString", FieldType.alphanumeric);
        xmlnsTypeMap.put("token", FieldType.alphanumeric);

        xmlnsTypeMap.put("date", FieldType.date);
        xmlnsTypeMap.put("dateTime", FieldType.date);
        xmlnsTypeMap.put("time", FieldType.date);
        xmlnsTypeMap.put("duration", FieldType.date);

        xmlnsTypeMap.put("float", FieldType.numeric);
        xmlnsTypeMap.put("double", FieldType.numeric);
        xmlnsTypeMap.put("decimal", FieldType.numeric);
        xmlnsTypeMap.put("byte", FieldType.numeric);
        xmlnsTypeMap.put("short", FieldType.numeric);
        xmlnsTypeMap.put("int", FieldType.numeric);
        xmlnsTypeMap.put("nonPositiveInteger", FieldType.numeric);
        xmlnsTypeMap.put("nonNegativeInteger", FieldType.numeric);
        xmlnsTypeMap.put("positiveInteger", FieldType.numeric);
        xmlnsTypeMap.put("integer", FieldType.numeric);
        xmlnsTypeMap.put("long", FieldType.numeric);

        defaultMap.put(XMLConstants.W3C_XML_SCHEMA_NS_URI, xmlnsTypeMap);
        return defaultMap;
    }

    public XSDFieldImporter(SchemaXSDEntity schemaEntity,
                            Map<String, Map<String, Integer>> typeMap) {
        schemaId = schemaEntity.getIdSchemaXSD();
        schemaModel = loadSchema(schemaEntity.getSchemaXSD());
        fieldImporterResults = new ArrayList<String>();

        if (schemaId <= 0) {
            throw new IllegalStateException("invalid schema id");
        }
        if (schemaModel == null) {
            throw new IllegalStateException(
                    "invalid schema. Could not able to parse schema");
        }

        this.typeMap = typeMap;
    }

    private Schema loadSchema(String xsd) {
        Schema schema = null;
        try {
            JAXBContext context = JAXBContext
                    .newInstance("com.seer.datacruncher.datastreams.XSDentities");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            schema = (Schema) unmarshaller.unmarshal(new StringReader(xsd));

        } catch (JAXBException exception) {
            Logger.getLogger(CreateXSDJAXB.class.getName()).log(Level.SEVERE,
                    null, exception);
        }
        return schema;
    }

    public XsdSchemaFieldInfo getSchemaFieldInfo() {

        List<OpenAttrs> elementList = schemaModel
                .getSimpleTypeOrComplexTypeOrGroup();

        Element root = findTopLevelElement(elementList);
        List<OpenAttrs> includeList = schemaModel.getIncludeOrImportOrRedefine();
        root.setAnnotation(findAnnotationTopLevelElement(includeList));
        XsdSchemaFieldInfo info = new XsdSchemaFieldInfo(schemaId);

        topSimpleTypeOrComplexType = findTopSimpleTypeOrComplexType(elementList) ;

        processChildOfComplexType(root, null, info, 1);

        info.setFieldInfoResults(fieldImporterResults);
        return info;

    }

    private void processChildOfComplexType(Element element,
                                           SchemaFieldEntity parentField, 
                                           XsdSchemaFieldInfo info,
                                           int elementOrder) {

        ComplexType type = element.getComplexType();

        assert type != null : "Element is not of ComplexType";
        int idFieldType = determineIdFieldTypeFrom(type);
        if (idFieldType == 0 ) {
            processEmptyComplexType(element, parentField,info,elementOrder);
        }else if (idFieldType <= 3 ) {
            processOrderIndicators(element, parentField,info,elementOrder,idFieldType);
        }else{
            SimpleContent simpleContent = type.getSimpleContent();
            ComplexContent complexContent = type.getComplexContent();
            if (simpleContent  != null){
                processSimpleContent(element, parentField, info,elementOrder,simpleContent);
            } else if (complexContent != null) {
                processComplexContent(element, parentField, info,elementOrder,complexContent);
            }
        }
    }
    private void processEmptyComplexType(Element element,
                                        SchemaFieldEntity parentField,
                                        XsdSchemaFieldInfo info,
                                        int elementOrder
                                       ) {

        ComplexType type = element.getComplexType();
        String name = element.getName();
        String desc = getDescriptionFrom(element);
        String maxOccurs = element.getMaxOccurs();
        int idFieldType = 1;
        SchemaFieldEntity field = createEntityField(name, desc, idFieldType,
                elementOrder, maxOccurs, false);

        info.addField(field, parentField);
        if(type.getAttributeOrAttributeGroup() != null && !type.getAttributeOrAttributeGroup().isEmpty()) {
            List<Annotated> list = type.getAttributeOrAttributeGroup();
            Iterator<Annotated> listIterator = list.iterator();
            List<Object> attributes = new ArrayList<Object>();
            while(listIterator.hasNext()) {
                Annotated annotatedObject = listIterator.next();
                if(annotatedObject instanceof Attribute) {
                    attributes.add(annotatedObject);
                } else if(annotatedObject instanceof AttributeGroup) {

                }
            }
            if(!attributes.isEmpty())
                processAttributes(field,info, attributes);
        }
    }
    private void processOrderIndicators(Element element,
                                        SchemaFieldEntity parentField,
                                        XsdSchemaFieldInfo info,
                                        int elementOrder,
                                        int idFieldType) {

        ComplexType type = element.getComplexType();
        String name = element.getName();
        String desc = getDescriptionFrom(element);
        String maxOccurs = element.getMaxOccurs();
        SchemaFieldEntity field = createEntityField(name, desc, idFieldType,
                elementOrder, maxOccurs, false);

        info.addField(field, parentField);
        if (idFieldType == FieldType.all) {
            All all = type.getAll();
            processParticles(field, info, all.getParticle());
        } else if (idFieldType == FieldType.choice) {
            ExplicitGroup group = type.getChoice();
            processParticles(field, info, group.getParticle());
        } else if (idFieldType == FieldType.sequence) {
            ExplicitGroup group = type.getSequence();
            processParticles(field, info, group.getParticle());
        }
        if(type.getAttributeOrAttributeGroup() != null && !type.getAttributeOrAttributeGroup().isEmpty()) {
            List<Annotated> list = type.getAttributeOrAttributeGroup();
            Iterator<Annotated> listIterator = list.iterator();
            List<Object> attributes = new ArrayList<Object>();
            while(listIterator.hasNext()) {
                Annotated annotatedObject = listIterator.next();
                if(annotatedObject instanceof Attribute) {
                    attributes.add(annotatedObject);
                } else if(annotatedObject instanceof AttributeGroup) {

                }
            }
            if(!attributes.isEmpty())
                processAttributes(field,info, attributes);
        }
    }
    private void processSimpleContent(Element element,
                                      SchemaFieldEntity parentField,
                                      XsdSchemaFieldInfo info,
                                      int elementOrder,
                                      SimpleContent simpleContent ) {
        boolean isBaseType = false;
        String baseLocalPart = null;
        SimpleExtensionType extension = simpleContent.getExtension();
        String name = element.getName();
        String desc = getDescriptionFrom(element);
        String maxOccurs = element.getMaxOccurs();
        QName baseTypeName = new QName("http://www.w3.org/2001/XMLSchema","string","xs");
        TopLevelSimpleType topType;
        List<Annotated> list = null;
        if (extension  != null) {
            baseTypeName = extension.getBase();
            list =  extension.getAttributeOrAttributeGroup();
        }else{
            SimpleRestrictionType restriction = simpleContent.getRestriction();
            if (restriction != null) {
                baseTypeName = restriction.getBase();
                list =  restriction.getAttributeOrAttributeGroup();
            }
        }
        int idFieldType = findIdFieldTypeByBaseName(baseTypeName);
        if(idFieldType == 0){
            topType = findTopLevelSimpleTypeByName(baseTypeName.getLocalPart());
            if (topType != null){
                Restriction res = topType.getRestriction();
                idFieldType = findIdFieldTypeByTypeName(res.getBase());
                LocalSimpleType locType =new LocalSimpleType();
                locType.setRestriction(res);
                element.setSimpleType(locType);
            }else{
                idFieldType = FieldType.alphanumeric;
                element.setSimpleType(createLocalsimpleType(baseTypeName));
            }
        }else{
            isBaseType = true;
            baseLocalPart = baseTypeName.getLocalPart();
        }

        SchemaFieldEntity entityField = createEntityField(name, desc,
                idFieldType, elementOrder, maxOccurs, false);
        boolean isNillable = false;
        Restriction restriction = null;
        if(isBaseType){
            isNillable = true;
        }else{
            restriction = element.getSimpleType().getRestriction();
            if (element.isNillable() || element.getMinOccurs().intValue() == 0)
                isNillable = true;
        }
        populateFieldEntity (entityField,
                restriction,
                baseLocalPart,
                element.getAnnotation(),
                isNillable,
                parentField,
                info);
        
        if(list != null && !list.isEmpty()) {
            Iterator<Annotated> listIterator = list.iterator();
            List<Object> attributes = new ArrayList<Object>();
            while(listIterator.hasNext()) {
                Annotated annotatedObject = listIterator.next();
                if(annotatedObject instanceof Attribute) {
                    attributes.add(annotatedObject);
                } else if(annotatedObject instanceof AttributeGroup) {

                }
            }
            if(!attributes.isEmpty())
                processAttributes(entityField,info, attributes);
        }
    }
    private void processAttributes(SchemaFieldEntity parentField,
                                   XsdSchemaFieldInfo info, List<Object> particles) {
        int index = 1;
        for (Object object : particles) {
            Attribute particle = (Attribute) unwarpParticle(object);
            processComplexOrSimpleAttribute(particle, parentField, info, index++);
        }
    }
    private void processComplexOrSimpleAttribute(Attribute attribute,
                                                 SchemaFieldEntity parentField, XsdSchemaFieldInfo info,
                                                 int elementOrder) {

        SimpleType simpleType =  attribute.getSimpleType();
        QName type =  attribute.getType();
        if (simpleType != null) {
            processSimpleAttributeOrElement( attribute, parentField, info, elementOrder);
        } else{
            if (type == null) {
                attribute.setType(new QName("http://www.w3.org/2001/XMLSchema","string","xs"));
                fieldImporterResults.add("type information not found for attribute " + attribute.getName() +". Set default type (string)");
            }
            processAttributeUsingBaseType(attribute, parentField, info, elementOrder);

        }
    }
    private boolean isCustomType(QName baseTypeName) {
        String uri = baseTypeName.getNamespaceURI();
        String name = baseTypeName.getLocalPart();
        Map<String, Integer> map = typeMap.get(uri);
        boolean isCustom = false;
        if (map == null || map.get(name) == null) {
            isCustom = true;
        }
        return isCustom;
    }

    private void processAttributeUsingBaseType(Attribute attribute,
                                               SchemaFieldEntity parentField, XsdSchemaFieldInfo info,
                                               int elementOrder) {
        QName type = attribute.getType();
        if (isSimpleType(type)) {
            if(isCustomType(type)){
                TopLevelSimpleType topType = findTopLevelSimpleTypeByName(type.getLocalPart());
                if (topType != null){
                    Restriction ret = topType.getRestriction();
                    LocalSimpleType locType =new LocalSimpleType();
                    locType.setRestriction(ret);
                    attribute.setSimpleType(locType);
                }else{
                    type = new QName("http://www.w3.org/2001/XMLSchema","string","xs");
                    fieldImporterResults.add("type information not found for attribute: " + attribute.getName());
                    attribute.setSimpleType(createLocalsimpleType(type));
                }
            }else{
                attribute.setSimpleType(createLocalsimpleType(type));
            }



            processSimpleAttributeOrElement(attribute, parentField, info, elementOrder);
        }

    }
    private void populateFieldEntity (SchemaFieldEntity entityField,
                                      Restriction restriction,
                                      String baseLocalPart,
                                      Annotation annotation,
                                      boolean isNillable,
                                      SchemaFieldEntity parentField,
                                      XsdSchemaFieldInfo info){
        List<String> appListInfo = null;
        Set<SchemaFieldCheckTypesEntity> schemaFieldCheckTypeSet;
        SchemaFieldCheckTypesEntity schemaFieldCheckTypesEntity;

        if (baseLocalPart!= null){
            if (entityField.getIdFieldType() == FieldType.date) {
                entityField.setIdDateFmtType(determineIdDateTypeFromFieldType(baseLocalPart));
            }else if (entityField.getIdFieldType() == FieldType.numeric) {
                entityField.setIdNumericType(determineIdNumberTypeFromFieldType(baseLocalPart));
            }
        }else{
            if (entityField.getIdFieldType() == FieldType.date) {
                entityField.setIdDateFmtType(determineIdDateTypeFromFieldType(restriction.getBase().getLocalPart()));
            }else if (entityField.getIdFieldType() == FieldType.numeric) {
                entityField.setIdNumericType(determineIdNumberTypeFromFieldType(restriction.getBase().getLocalPart()));
            }
        }
        schemaFieldsDao.destroySchemFieldCheckTypes(entityField.getIdSchemaField());

        if (annotation != null) {
            appListInfo = getAppInfo(annotation);

        }
        if (appListInfo != null && appListInfo.size() >= 0) {
            schemaFieldCheckTypeSet = new HashSet<SchemaFieldCheckTypesEntity>();
            for (String appInfo : appListInfo) {
                if (appInfo.startsWith("@forecasting:")) {
                    String arr[] = appInfo.split(":")[1].split("-");
                    entityField.setForecastAccuracy(Integer.parseInt(arr[0]));
                    entityField.setForecastSpeed(Integer.parseInt(arr[1]));
                }else if(appInfo.startsWith("@unixDate")) {
                    entityField.setIdFieldType(6);
                    entityField.setIdDateFmtType(DateTimeType.unixTimestamp);
                }else if (appInfo.startsWith("@jvDate:")) {
                    String arr[] = appInfo.split(":")[1].split("-");
                    if (arr.length == 3){
                        entityField.setIdFieldType(6);
                        if(!arr[0].equals("0")){
                            entityField.setIdDateFmtType(Integer.parseInt(arr[0]));
                        }
                        if(!arr[1].equals("0")){
                            entityField.setIdDateType(Integer.parseInt(arr[1]));
                        }
                        if(!arr[2].equals("0")){
                            entityField.setIdTimeType(Integer.parseInt(arr[2]));
                        }
                    }
                }else{
                    ChecksTypeEntity checksTypeEntity = checksTypeDao
                            .getChecksTypeByDescr(appInfo);
                    if (checksTypeEntity != null
                            && checksTypeEntity.getIdCheckType() > 0) {
                        schemaFieldCheckTypesEntity = new SchemaFieldCheckTypesEntity();
                        schemaFieldCheckTypesEntity.setIdCheckType(checksTypeEntity.getIdCheckType());
                        schemaFieldCheckTypesEntity.setSchemaFieldEntity(entityField);
                        schemaFieldCheckTypeSet.add(schemaFieldCheckTypesEntity);
                    }
                }
            }
            entityField.setSchemaFieldCheckTypeSet(schemaFieldCheckTypeSet);
        }
        if(isNillable){
            entityField.setNillable(true);
        } else {
            entityField.setNillable(false);
        }
        // set default values
        entityField.setIdAlign(1);
        entityField.setFillChar(" ");
        if (restriction!= null)
        populateFieldFromRestriction(entityField, restriction, info);

        info.addField(entityField, parentField);
    }

    private int findIdFieldTypeByBaseName(QName baseTypeName) {
        String uri = baseTypeName.getNamespaceURI();
        String name = baseTypeName.getLocalPart();
        Map<String, Integer> map = typeMap.get(uri);
        Integer type;
        if (map == null || (type = map.get(name)) == null) {
            type = 0;
        }

        return type;
    }
    private void processComplexContent(Element element,
                                       SchemaFieldEntity parentField,
                                       XsdSchemaFieldInfo info,
                                       int elementOrder,
                                       ComplexContent complexContent ) {
        // TODO find a way to implement me
        fieldImporterResults.add("complex type " + element.getName() + " reference not yet supported");
        /*throw new UnsupportedOperationException(
                "complex type reference not yet supported");   */
    }


    private void processParticles(SchemaFieldEntity parentField,
                                  XsdSchemaFieldInfo info, List<Object> particles) {
        int index = 1;
        for (Object object : particles) {
            Element particle = (Element)unwarpParticle(object);
            processComplexOrSimpleElement(particle, parentField, info, index++);
        }
    }

    private void processComplexOrSimpleElement(Element element,
                                               SchemaFieldEntity parentField, XsdSchemaFieldInfo info,
                                               int elementOrder) {
        SimpleType simpleType = element.getSimpleType();
        ComplexType complexType = element.getComplexType();
        QName type = element.getType();
        if (simpleType != null) {
            processSimpleAttributeOrElement(element, parentField, info, elementOrder);
        } else if (complexType != null) {
            processChildOfComplexType(element, parentField, info, elementOrder);
        } else if (type != null) {
            processElementUsingBaseType(element, parentField, info,
                    elementOrder);
        } else {
            element.setType(new QName("http://www.w3.org/2001/XMLSchema","string","xs"));
            fieldImporterResults.add("type information not found for element: " + element.getName());
            processElementUsingBaseType(element, parentField, info, elementOrder);
            /*throw new IllegalStateException(
                    "type information not found for element " + element.getName());*/
        }

    }

    private void processElementUsingBaseType(Element element,
                                             SchemaFieldEntity parentField, XsdSchemaFieldInfo info,
                                             int elementOrder) {
        QName type = element.getType();
        if (isSimpleType(type)) {
            TopLevelSimpleType topSimpleType = findTopLevelSimpleTypeByName(type.getLocalPart());
            TopLevelComplexType topComplexType = findTopLevelComplexTypeByName(type.getLocalPart());
            
            if (topSimpleType != null){
                Restriction ret = topSimpleType.getRestriction();
                LocalSimpleType locType =new LocalSimpleType();
                locType.setRestriction(ret);
                element.setSimpleType(locType);
                processSimpleAttributeOrElement(element, parentField, info, elementOrder);
            }else if (topComplexType != null){
                element.setComplexType(createLocalComplexType(topComplexType ));
                processChildOfComplexType(element, parentField,info,elementOrder);
            }else{
                element.setSimpleType(createLocalsimpleType(type));
                processSimpleAttributeOrElement(element, parentField, info, elementOrder);
            }
        } else {
            element.setComplexType(findComplexTypeByName(type));

            processComplexOrSimpleElement(element, parentField, info,
                    elementOrder);
        }
    }
    private LocalComplexType createLocalComplexType(TopLevelComplexType topComplexType ) {
        LocalComplexType localComplexType =   new LocalComplexType();
        localComplexType.setAbstract(topComplexType.isAbstract());
        localComplexType.setSimpleContent(topComplexType.getSimpleContent());
        localComplexType.setComplexContent(topComplexType.getComplexContent());
        localComplexType.setGroup(topComplexType.getGroup());
        localComplexType.setAll(topComplexType.getAll());
        localComplexType.setChoice(topComplexType.getChoice());
        localComplexType.setSequence(topComplexType.getSequence());
        localComplexType.setAnyAttribute(topComplexType.getAnyAttribute());
        localComplexType.setMixed(topComplexType.isMixed());
        List<Annotated> attributes = topComplexType.getAttributeOrAttributeGroup();
        List<String> _final = topComplexType.getFinal();
        List<String> block= topComplexType.getBlock();
        if (attributes.size()>0){
            for (int i = attributes.size() - 1; i >= 0; i--)
                localComplexType.getAttributeOrAttributeGroup().add(attributes.get(i));
        }
        if (_final.size()>0){
            for (int i = _final.size() - 1; i >= 0; i--)
                localComplexType.getFinal().add(_final.get(i));
        }
        if (block.size()>0){
            for (int i = block.size() - 1; i >= 0; i--)
                localComplexType.getBlock().add(block.get(i));
        }
        return localComplexType;
    }
    private LocalComplexType findComplexTypeByName(QName type) {
        // TODO find a way to implement me
        fieldImporterResults.add("local complex type reference not yet supported");
        return new LocalComplexType();
        /*throw new UnsupportedOperationException(
                "local complex type reference not yet supported");*/
    }

    private boolean isSimpleType(QName type) {
        return XMLConstants.W3C_XML_SCHEMA_NS_URI.endsWith(type.getNamespaceURI());
    }

    private LocalSimpleType createLocalsimpleType(final QName type) {
        return new LocalSimpleType() {
            @Override
            public Restriction getRestriction() {
                Restriction ret = new Restriction();
                ret.setBase(type);
                return ret;
            }
        };
    }
    private TopLevelSimpleType findTopLevelSimpleTypeByName(String name){
        TopLevelSimpleType type = null;
        for (OpenAttrs openAttrs : topSimpleTypeOrComplexType) {
            if (openAttrs instanceof TopLevelSimpleType && name.equals(((TopLevelSimpleType) openAttrs).getName()) ){
                type = ((TopLevelSimpleType) openAttrs);
                break;
            }
        }
        return  type;
    }
    private TopLevelComplexType findTopLevelComplexTypeByName(String name){
        TopLevelComplexType type = null;
        for (OpenAttrs openAttrs : topSimpleTypeOrComplexType) {
            if (openAttrs instanceof TopLevelComplexType && name.equals(((TopLevelComplexType) openAttrs).getName()) ){
                type = ((TopLevelComplexType) openAttrs);
                break;
            }
        }
        return  type;
    }
    private void processSimpleAttributeOrElement(Annotated annotated,
                                                 SchemaFieldEntity parentField, XsdSchemaFieldInfo info,
                                                 int elementOrder) {
        if (annotated instanceof Element){
            Element element = (Element) annotated;
            processSimpleElement(element, parentField,info,elementOrder);
        }else if (annotated instanceof Attribute){
            Attribute attribute = (Attribute) annotated;
            processSimpleAttribute(attribute, parentField,info,elementOrder);
        }

    }
    private void processSimpleAttribute(Attribute attribute,
                                        SchemaFieldEntity parentField, XsdSchemaFieldInfo info,
                                        int elementOrder) {
        
        SimpleType type = attribute.getSimpleType();
        String name = attribute.getName();
        String desc = getDescriptionFrom(attribute);
        int idFieldType = determineIdFieldTypeFrom(type);
        SchemaFieldEntity entityField = createEntityField(name, desc,
                idFieldType, elementOrder, null, true);
        boolean isNillable = true;
        entityField.setNillable(true);
        if ((attribute.getUse()).equals("required"))
            isNillable = false;
        
        
        
                
        if (entityField.getName().equals(Tag.TAG_SIZE)){
            entityField.setSize(attribute.getFixed());
            info.addField(entityField, parentField);
        }else  if (entityField.getName().equals(Tag.TAG_ALIGN)) {
            entityField.setIdAlign(Integer.parseInt(attribute.getFixed()));
            info.addField(entityField, parentField);
        }else if (entityField.getName().equals(Tag.TAG_FILL)){
            entityField.setFillChar(attribute.getFixed());
            info.addField(entityField, parentField);
        }else {
            populateFieldEntity (entityField,
                    type.getRestriction(),
                    null,
                    attribute.getAnnotation(),
                    isNillable,
                    parentField,
                    info);
        }
               
        
        
        
        

    }
    private void processSimpleElement(Element element,
                                      SchemaFieldEntity parentField, XsdSchemaFieldInfo info,
                                      int elementOrder) {

        SimpleType type = element.getSimpleType();
        String name = element.getName();
        String desc = getDescriptionFrom(element);
        String maxOccurs = element.getMaxOccurs();
        int idFieldType = determineIdFieldTypeFrom(type);
        SchemaFieldEntity entityField = createEntityField(name, desc,
                idFieldType, elementOrder, maxOccurs, false);
        boolean isNillable = false;
        if (element.isNillable() || element.getMinOccurs().intValue() == 0)
            isNillable = true;
        populateFieldEntity (entityField,
                element.getSimpleType().getRestriction(),
                null,
                element.getAnnotation(),
                isNillable,
                parentField,
                info);
    }

    private List<String> getAppInfo(Annotation annotation) {
        List<String> listApp = new ArrayList<String>();
        if (annotation != null) {
            List<Object> appInfoOrDocumentation = annotation
                    .getAppinfoOrDocumentation();
            for (Object object : appInfoOrDocumentation) {
                if (object instanceof Appinfo) {
                    Appinfo appInfo = (Appinfo) object;
                    List<Object> list = appInfo.getContent();
                    if (list != null && list.size() >= 0) {
                        for (Object obj : list)
                            listApp.add( obj.toString());
                    }
                }
            }

        }
        return listApp;
    }

    private void populateFieldFromRestriction(SchemaFieldEntity entityField,
                                              Restriction restriction, XsdSchemaFieldInfo info) {
        List<Object> facets = restriction.getFacets();
        for (Object object : facets) {
            JAXBElement<?> element;
            Facet facet = null;
            String name = null;
            if (object instanceof JAXBElement<?>) {
                element = (JAXBElement<?>) object;
                facet = (Facet) element.getValue();
                name = element.getName().getLocalPart();
            } else if (object instanceof Pattern) {
                // not sure why JAXB is not putting patter as JAXB element
                facet = (Facet) object;
                name = "pattern";
            } else if (object instanceof TotalDigits) {
                facet = (Facet) object;
                name = "totalDigits";
            }

            populateFieldFromFacet(entityField, name, facet, info);
            if (entityField.getIdFieldType() == FieldType.date) {
                postProcessFacetsFor(entityField, info);
            }
        }
    }

    private void postProcessFacetsFor(SchemaFieldEntity entityField,
                                      XsdSchemaFieldInfo info) {
        // need to adjust the max/min value for special type like date and
        // associated pattern

    }

    private void populateFieldFromFacet(SchemaFieldEntity entityField,
                                        String facetName, Facet facet, XsdSchemaFieldInfo info) {
        String value = facet.getValue();
        if ("length".equals(facetName)) {
            entityField.setMinLength(Integer.valueOf(value));
            entityField.setMaxLength(entityField.getMinLength());
        } else if ("minLength".equals(facetName)) {
            entityField.setMinLength(Integer.valueOf(value));
        } else if ("maxLength".equals(facetName)
                || "maxExclusive".equals(facetName)) {
            entityField.setMaxLength(Integer.valueOf(value));
        } else if ("maxInclusive".equals(facetName)) {
            entityField.setMaxInclusive(Double.parseDouble(value));
        } else if ("minLength".equals(facetName)
                || "minExclusive".equals(facetName)) {
            entityField.setMinLength(Integer.valueOf(value));
        } else if ("minInclusive".equals(facetName)) {
            entityField.setMinInclusive(Double.valueOf(value));
        } else if ("pattern".equals(facetName)) {
            // entityField.setRegularExpression(value);
        } else if ("enumeration".equals(facetName)) {
            info.addFieldEnumeration(entityField, value);
        } else if ("whiteSpace".equals(facetName)) {
            // no field in EntityField, hence
        } else if ("totalDigits".equals(facetName)) {
            // no separate attribute int entity field so reusing.
            entityField.setMinLength(Integer.valueOf(value));
            entityField.setMaxLength(entityField.getMinLength());
        } else if ("fractionDigits".equals(facetName)) {
            entityField.setFractionDigits(Integer.valueOf(value));
        } else {
            throw new IllegalStateException("facet " + facetName
                    + " is not supported");
        }
    }
    private int determineIdNumberTypeFromFieldType(String baseLocalPart) {
        int idNumType = GenericType.integer;
        if (baseLocalPart != null) {
            if ("integer".equals(baseLocalPart)){
                idNumType = GenericType.integer;
            }else if("decimal".equals(baseLocalPart)){
                idNumType = GenericType.decimal;
            }
        }

        return idNumType;
    }
    private int determineIdDateTypeFromFieldType(String baseLocalPart) {
        int idDateType = DateTimeType.xsdDate;

        if (baseLocalPart != null) {
            if ("date".equals(baseLocalPart)){
                idDateType = DateTimeType.xsdDate;
            }else if("time".equals(baseLocalPart)){
                idDateType = DateTimeType.xsdTime;
            }else if("dateTime".equals(baseLocalPart)){
                idDateType = DateTimeType.xsdDateTime;
            }
        }

        return idDateType;
    }
    private int determineIdFieldTypeFrom(SimpleType type) {
        int idFieldType = FieldType.alphanumeric;


        Union union = type.getUnion();
        if (union != null) {
            // TODO need to support the union type here. but how
            fieldImporterResults.add("union type reference not yet supported");
        }
        Restriction restriction = type.getRestriction();

        if (restriction != null) {
            idFieldType = findIdFieldTypeByTypeName(restriction.getBase());
        }

        return idFieldType;
    }

    private int findIdFieldTypeByTypeName(QName baseTypeName) {
        String uri = baseTypeName.getNamespaceURI();
        String name = baseTypeName.getLocalPart();
        Map<String, Integer> map = typeMap.get(uri);
        Integer type;
        if (map == null || (type = map.get(name)) == null) {
            // WARN not type match found in mapping.Using alphanumeric
            fieldImporterResults.add("type "+name+"reference not yet supported.Set default alphanumeric type ");
            type = FieldType.alphanumeric;
        }

        return type;
    }

    private Object unwarpParticle(Object object) {
        if (object instanceof JAXBElement<?>) {
            object = ((JAXBElement<?>) object).getValue();
        }
        return object;
    }

    private int determineIdFieldTypeFrom(ComplexType root) {
        int idFieldType = 0;
        if(root.getAll() != null) {
            idFieldType = FieldType.all;
        } else if (root.getChoice() != null) {
            idFieldType = FieldType.choice;
        } else if (root.getSequence() != null) {
            idFieldType = FieldType.sequence;
        }else if (root.getSimpleContent() != null){
            idFieldType = GenericType.simpleContent;
        }else if (root.getComplexContent() != null){
            idFieldType = GenericType.complexContent;
        }
        return idFieldType;
    }

    private SchemaFieldEntity createEntityField(String name, String desc,
                                                int idFieldType, int elementOrder,
                                                String maxOccurs, boolean is_Attribute) {
        SchemaFieldEntity field = new SchemaFieldEntity();
        field.setName(name);
        field.setDescription(desc);
        field.setElementOrder(elementOrder);
        field.setIs_Attribute(is_Attribute);
        if (!is_Attribute) {
            if (maxOccurs.equals("")) {
                field.setMaxOccurs(1);
            } else {
                if("unbounded".equals(maxOccurs)){
                    field.setMaxOccurs(0);
                }else{
                    field.setMaxOccurs(Integer.parseInt(maxOccurs));
                }
            }
        }
        if(idFieldType > 0){
            field.setIdFieldType(idFieldType);
        }
        // how can we link custom error
        field.setIdCustomError(0);

        return field;
    }

    private String getDescriptionFrom(Annotated root) {
        return findDocumentation(root.getAnnotation());
    }

    private String findDocumentation(Annotation annotation) {
        String description = null;
        if (annotation != null) {
            List<Object> appInfoOrDocumentation = annotation
                    .getAppinfoOrDocumentation();
            for (Object object : appInfoOrDocumentation) {
                if (object instanceof Documentation) {
                    Documentation doc = (Documentation) object;
                    List<Object> docObjects = doc.getContent();
                    if (docObjects != null && docObjects.size() > 0) {
                        description = docObjects.get(0).toString();
                    }
                    break;
                }
            }

        }

        return description;
    }

    private List<OpenAttrs> findTopSimpleTypeOrComplexType(List<OpenAttrs> elementList) {
        List<OpenAttrs> simpleTypeOrComplexType = new ArrayList<OpenAttrs>();
        for (OpenAttrs openAttrs : elementList) {
            if (openAttrs instanceof TopLevelSimpleType ||
                    openAttrs instanceof TopLevelComplexType) {
                simpleTypeOrComplexType.add(openAttrs);
            }
        }
        return simpleTypeOrComplexType;
    }
    private Annotation findAnnotationTopLevelElement(List<OpenAttrs> elementList) {
        Annotation type = null;
        for (OpenAttrs openAttrs : elementList) {
            if (openAttrs instanceof Annotation) {
                Annotation topAnnotation = ((Annotation) openAttrs);
                if (type != null) {
                    throw new IllegalStateException(
                            "multiple top Annotation found");
                }else {
                    type = (Annotation) openAttrs;
                }
            }
        }
        return type;
    }
    private Element findTopLevelElement(List<OpenAttrs> elementList) {
        Element type = null;
        for (OpenAttrs openAttrs : elementList) {
            if (openAttrs instanceof TopLevelElement) {
                TopLevelElement topElement = ((TopLevelElement) openAttrs);
                if (type != null) {
                    throw new IllegalStateException(
                            "multiple top level element found");
                }else if(!((Tag.TAG_SCHEMA_NAME).equals(topElement.getName()) ||
                        (Tag.TAG_DATA_STREAM_TYPE).equals(topElement.getName()) ||
                        (Tag.TAG_VALIDITY_START_DATE).equals(topElement.getName()) ||
                        (Tag.TAG_VALIDITY_END_DATE).equals(topElement.getName()) ||
                        (Tag.TAG_DESCRIPTION).equals(topElement.getName()) ||
                        (Tag.TAG_DELIMITER_CHAR).equals(topElement.getName()))) {
                    type = (Element) openAttrs;
                }
            }
        }
        return type;
    }

    public static XsdSchemaFieldInfo parseXSD(SchemaXSDEntity schemaXSDEntity) {
        XSDFieldImporter importer = new XSDFieldImporter(schemaXSDEntity);

        return importer.getSchemaFieldInfo();
    }
}