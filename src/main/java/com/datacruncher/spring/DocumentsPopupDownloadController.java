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

package com.datacruncher.spring;

import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.FileEntity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class DocumentsPopupDownloadController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String idFile = request.getParameter("fileid");

		FileEntity fileEntity = new FileEntity();
		fileEntity = fileDao.getElement(Long.parseLong(idFile));
		byte[] fileBytes = fileDao.getFileContent(Long.parseLong(idFile));

		response.setContentType("application/force-download");
		response.setContentLength(fileBytes.length);
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileEntity.getName() + "\"");

		FileCopyUtils.copy(fileBytes, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
		return null;

	}
}