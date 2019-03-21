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

package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.FileInfo;
import com.seer.datacruncher.constants.SchemaType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.constants.Tag;
import com.seer.datacruncher.datastreams.XSDFieldStorer;
import com.seer.datacruncher.datastreams.XSDentities.OpenAttrs;
import com.seer.datacruncher.datastreams.XSDentities.Schema;
import com.seer.datacruncher.datastreams.XSDentities.TopLevelElement;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.CryptoUtil;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.validation.ResultStepValidation;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchemaImportFromJvController extends MultiActionController implements DaoSet {
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
        String idApplication = request.getParameter("idApplication");
        String idSchemaType = request.getParameter("idSchemaType");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("file");

        response.setContentType("text/html");
        ServletOutputStream out = response.getOutputStream();	
		if (!multipartFile.getOriginalFilename().endsWith(FileInfo.EXIMPORT_FILE_EXTENSION)) {

			out.write(("{success:false, message: '1'}").getBytes());
			out.flush();
			out.close();
			return null;
		}
		
		try {
            Create create = new Create();
			String fileContent = new CryptoUtil().decrypt(new String(multipartFile.getBytes()));
			if  (fileContent ==null) {
                create.setSuccess(false);
                create.setMessage("The file can not be read.");
            } else{
                create = parseXSDAndCreateSchema(idApplication,fileContent,idSchemaType);
            }
			
			if(create.getSuccess()) {
                long idSchema = ((SchemaEntity)create.getResults()).getIdSchema();
                ResultStepValidation result = new XSDFieldStorer(idSchema,fileContent.getBytes()).storeFields();
                String msg = result.getMessageResult();
                if (result.isValid()) {
                    if(Integer.parseInt(idSchemaType) == SchemaType.GENERATION){
                        // TODO Managing the attributes in the "Stream Generation".
                        // Delete attributes because they are not yet handled in the "Stream Generation".
                        List<SchemaFieldEntity> listAttrs = schemaFieldsDao.listAttr(idSchema);
                        if(listAttrs.size()>0){
                            for (int i = listAttrs.size() - 1; i >= 0; i--)
                                schemaFieldsDao.destroy(listAttrs.get(i).getIdSchemaField());
                            msg = msg.trim().length()>0? msg+"<br>": msg.trim();
                            msg = msg + "<b>The attributes in the schema were not imported</b>";
                        }
                        //end delete
                    }
                    create.setSuccess(true);
                    create.setMessage(msg);
                }else{
                    create.setSuccess(false);
                    create.setMessage(result.getMessageResult());
                }

			}
            if(!create.getSuccess()){
              // Delete schema
                SchemaEntity schemaEntity = (SchemaEntity)create.getResults();
                if(schemaEntity != null){
                    schemasDao.destroy(schemaEntity.getIdSchema());
                }
            }
            out.write(("{success: "
                    + create.getSuccess() + ", message: '" + create.getMessage() + "'}")
                    .getBytes());

		} catch(Exception ex) {
            String msg = CommonUtils.getExceptionMessage(ex);
			ex.printStackTrace();
            out.write(("{success: false, message: '" + msg + "'}")
                    .getBytes());
		}
		
		out.flush();
		out.close();
		
		return null;
	}
	private Create parseXSDAndCreateSchema(String idApplication,String schemaXSD,String idSchemaType) {
        Create create = loadSchema(schemaXSD);
        if(create.getSuccess()){
            Schema schemaModel = (Schema)create.getResults();
            create = new Create();
            List<OpenAttrs> elementList = schemaModel.getSimpleTypeOrComplexTypeOrGroup();
            Map<String,String> mapSchemaData = findSchemaData(elementList);
    
            if (mapSchemaData.size()>0){
                SchemaEntity schemaEntity = new SchemaEntity();
                schemaEntity.setIdApplication(Long.parseLong(idApplication));
                schemaEntity.setIdDatabase(0);
                schemaEntity.setName(mapSchemaData.get(Tag.TAG_SCHEMA_NAME));
                if(mapSchemaData.containsKey(Tag.TAG_DESCRIPTION)) {
                    schemaEntity.setDescription(mapSchemaData.get(Tag.TAG_DESCRIPTION));
                } else {
                    schemaEntity.setDescription("");
                }
                schemaEntity.setIdSchemaType(Integer.parseInt(idSchemaType));
                schemaEntity.setService(-1);
    
                schemaEntity.setIdStreamType(Integer.parseInt(mapSchemaData.get(Tag.TAG_DATA_STREAM_TYPE)));
    
                if(schemaEntity.getIdStreamType() == StreamType.flatFileDelimited) {
                    schemaEntity.setDelimiter(mapSchemaData.get(Tag.TAG_DELIMITER_CHAR));
                } else {
                    schemaEntity.setDelimiter("");
                }
                schemaEntity.setIsActive(1);
                schemaEntity.setPlannedName(-1);
    
                try {
                    if (mapSchemaData.get(Tag.TAG_VALIDITY_START_DATE).trim().length() > 0) {
                        schemaEntity.setStartDate(new SimpleDateFormat("yyyy-MM-dd")
                                .parse(mapSchemaData.get(Tag.TAG_VALIDITY_START_DATE)));
                    }
                    if (mapSchemaData.get(Tag.TAG_VALIDITY_END_DATE).trim().length() > 0) {
                        schemaEntity.setEndDate(new SimpleDateFormat("yyyy-MM-dd")
                                .parse(mapSchemaData.get(Tag.TAG_VALIDITY_END_DATE)));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE , null , ex);
                    create.setSuccess(false);
                    create.setResults(null);
                    create.setMessage(I18n.getMessage("error.parametersNotFound"));
                    return create;
                }
                create = schemasDao.create(schemaEntity);
            }else{
                create.setSuccess(false);
                create.setResults(null);
                create.setMessage(I18n.getMessage("error.parametersNotFound"));
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Not found schema parameters.");
            }
        }
		return create;
	}		
		
	private Create loadSchema(String schemaXSD) {
        Create create = new Create();
        create.setSuccess(true);
        create.setMessage("");
		Schema schema;

        try {
            JAXBContext context = JAXBContext
                    .newInstance("com.seer.datacruncher.datastreams.XSDentities");
            Unmarshaller unmarshaller = context.createUnmarshaller();
            schema = (Schema) unmarshaller.unmarshal(new StringReader(schemaXSD));
            create.setResults(schema);
        } catch (Exception ex) {
            String msg = CommonUtils.getExceptionMessage(ex);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"load Schema error", ex);
            create.setSuccess(false);
            create.setMessage(msg);
        } finally {
            return create;
        }
	}
	private Map<String,String> findSchemaData(List<OpenAttrs> elementList) {
        
        Map<String,String> mapSchemaData = new HashMap<String,String>();
        
        for (OpenAttrs openAttrs : elementList) {
            if (openAttrs instanceof TopLevelElement) {
            	TopLevelElement topElement = ((TopLevelElement) openAttrs);
               
                if((Tag.TAG_SCHEMA_NAME).equals(topElement.getName()) ||
                    (Tag.TAG_DATA_STREAM_TYPE).equals(topElement.getName()) ||
                    (Tag.TAG_VALIDITY_START_DATE).equals(topElement.getName()) ||
                    (Tag.TAG_VALIDITY_END_DATE).equals(topElement.getName()) ||
                    (Tag.TAG_DESCRIPTION).equals(topElement.getName()) ||
                    (Tag.TAG_DELIMITER_CHAR).equals(topElement.getName())) {
                	mapSchemaData.put(topElement.getName(), topElement.getFixed());
                }
            }
        }
        return mapSchemaData;
    }
}