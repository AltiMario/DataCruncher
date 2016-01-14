/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */

package com.seer.datacruncher.spring;

import com.seer.datacruncher.constants.FileInfo;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.utils.CryptoUtil;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.schema.SchemaValidator;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class SchemaExportToXSDController extends MultiActionController implements DaoSet {

    private static Logger log = Logger.getLogger(SchemaExportToXSDController.class.getName());

    private String keptXSD;
    private Long validatedSchemaId;

    public ModelAndView checkValidity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		Map<String, String> resMap;

    	Long idSchema = Long.parseLong(request.getParameter("idSchema"));
        schemasXSDDao.destroy(idSchema);
        SchemaValidator schemaValidator = new SchemaValidator();
        resMap = schemaValidator.validateSchema(idSchema);
        keptXSD = schemaValidator.getSchemaCantaints();
        response.getWriter().print(new JSONObject(resMap).toString());
        validatedSchemaId = idSchema;
        return null;
    }

    public ModelAndView downloadXSD(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        Long idSchema = Long.parseLong(request.getParameter("idSchema"));
        if (!idSchema.equals(validatedSchemaId))
            throw new RuntimeException("Validated schema id is not matched with current schema!");
        try {
            String strSchemaXSD = keptXSD;
            String fileName = HttpUtils.encodeContentDispositionForDownload(request, idSchema + FileInfo.EXIMPORT_FILE_EXTENSION, false);
            OutputStream ostr = response.getOutputStream();
            response.setContentType("application/binary");
            response.setHeader("Content-Disposition", fileName);
            ostr.write(new CryptoUtil().encrypt(strSchemaXSD).getBytes("UTF-8"));
            ostr.flush();
            ostr.close();
        } catch (Exception e) {
            String failMsg = I18n.getMessage("error.XsdExport");
            log.error(failMsg, e);
        }
        return null;
    }
}
