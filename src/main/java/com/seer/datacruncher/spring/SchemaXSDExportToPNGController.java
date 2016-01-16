/*
 * Copyright (c) 2015  www.see-r.com
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

import com.seer.datacruncher.documentation.Xsd2png;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.schema.SchemaValidator;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SchemaXSDExportToPNGController extends MultiActionController {
	private static Logger log = Logger.getLogger(SchemaExportToXSDController.class);
	private Long validatedSchemaId;
	private String keptXSD;

	public ModelAndView checkValidity(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Long idSchema = Long.parseLong(request.getParameter("idSchema"));
		Map<String, String> resMap = new HashMap<String, String>();
		SchemaValidator schemaValidator = new SchemaValidator();
		resMap = schemaValidator.validateSchema(idSchema);
		keptXSD = schemaValidator.getSchemaCantaints();
        String msg =   new JSONObject(resMap).toString();
		response.getWriter().print(msg);
		validatedSchemaId = idSchema;
		return null;
	}

	public ModelAndView downloadPNG(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		Long idSchema = Long.parseLong(request.getParameter("idSchema"));
        try {
            if (!idSchema.equals(validatedSchemaId))
                throw new RuntimeException("Validated schema : id is not matched with current schema!");
            if (keptXSD == null)
                throw new RuntimeException("Validated schema : xsd schema is empty!");
			Xsd2png xsd2png = new Xsd2png();
			String strSchemaXSD = keptXSD;
			String filePngName = HttpUtils.encodeContentDispositionForDownload(request, idSchema + ".png", false);

			ByteArrayOutputStream os = (ByteArrayOutputStream) xsd2png.createImage(idSchema, strSchemaXSD);
			byte[] fileBytes = os.toByteArray();
			os.flush();
			os.close();

			response.setContentType("application/force-download");
			response.setContentLength(fileBytes.length);
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.setHeader("Content-Disposition", filePngName);

			FileCopyUtils.copy(fileBytes, response.getOutputStream());

			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			String failMsg = I18n.getMessage("error.XsdExport");
			log.error(failMsg, e);
			response.getWriter().print("{\"responseMsg\":\"" + failMsg + ".\",\"success\":\"false\"}");
			response.getWriter().flush();
			response.getWriter().close();
		} catch (OutOfMemoryError err) {
			log.error("Out of memory occured please set the java memory parameters", err);
			String failMsg = I18n.getMessage("error.outOfMemoryError");
			response.getWriter().print("{\"responseMsg\":\"" + failMsg + "\",\"success\":\"false\"}");
			response.getWriter().flush();
			response.getWriter().close();
		}
		return null;
	}
}
