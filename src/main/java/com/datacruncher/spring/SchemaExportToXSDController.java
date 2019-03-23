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

package com.datacruncher.spring;

import com.datacruncher.constants.FileInfo;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.ConnectionsEntity;
import com.datacruncher.utils.CryptoUtil;
import com.datacruncher.utils.generic.I18n;
import com.datacruncher.utils.schema.SchemaValidator;
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
            ostr.write(keptXSD.getBytes());
            ostr.flush();
            ostr.close();
        } catch (Exception e) {
            String failMsg = I18n.getMessage("error.XsdExport");
            log.error(failMsg, e);
        }
        return null;
    }
}
