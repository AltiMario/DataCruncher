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
 */package com.datacruncher.spring;

import com.datacruncher.datastreams.DatastreamsInput;
import com.datacruncher.jpa.Create;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UploadController extends MultiActionController implements DaoSet {

	public ModelAndView uploadXMLDataStream(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String idSchema = request.getParameter("idSchema");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("file");
		byte[] byteArray = multipartFile.getBytes();
		DatastreamsInput datastreamsInput = new DatastreamsInput();

		String resMsg = datastreamsInput.datastreamsInput(
				new String(byteArray), Long.parseLong(idSchema), byteArray);

		response.setContentType("text/html");
        ServletOutputStream out = response.getOutputStream();
        Boolean success = true;

		out.write(("{success: " + success + " , message: '" + resMsg + "'}")
				.getBytes());
		out.flush();
		out.close();

		return null;
	}
	public ModelAndView uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		FileEntity fileEntity = new FileEntity();
		String idSchema = request.getParameter("idSchema");
		String description = request.getParameter("description");
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("file");

		fileEntity.setDescription(description);
		fileEntity.setContentType(multipartFile.getContentType());
		fileEntity.setName(multipartFile.getOriginalFilename());
		fileEntity.setContent(multipartFile.getBytes());
		if (idSchema != null && !idSchema.equals("")) {
			fileEntity.setIdSchema(Long.parseLong(idSchema));
		}

		Create create = fileDao.create(fileEntity);

		response.setContentType("text/html");
        ServletOutputStream out = response.getOutputStream();
		out.write(("{success: " + create.getSuccess() + ", message: '" + create.getMessage() + "'}").getBytes());
		out.flush();
		out.close();
		return null;
	}
}