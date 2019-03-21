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

import com.datacruncher.constants.StreamStatus;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RealTimeGraphController implements Controller, DaoSet {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		ServletOutputStream out;
		response.setContentType("application/json");
		out = response.getOutputStream();

		Calendar calender = new GregorianCalendar();
		SimpleDateFormat format = new SimpleDateFormat("HH.mm.ss");

		ReadList readList = datastreamsDao.getReportData(null, null, null, null, calender);
		List<ReportData> reportDataList = getReportDataList(readList.getResults(), format.format(calender.getTime()));
		readList.setResults(reportDataList);
		out.write(mapper.writeValueAsBytes(readList));
		out.flush();
		out.close();

		return null;
	}

	// -------------------HELPERS-----------------
	private List<ReportData> getReportDataList(@SuppressWarnings("rawtypes") List resultList, String time) {
		ReportData reportData;
		List<ReportData> reportDataList = new ArrayList<ReportData>();
		if (resultList == null || resultList.size() == 0) {
			reportData = new ReportData();
			reportData.setTime(time);
			reportData.setDataStreamKO(0);
			reportData.setDataStreamOK(0);
			reportData.setDataStreamWarn(0);
			reportDataList.add(reportData);
			return reportDataList;
		}

		@SuppressWarnings("rawtypes")
		ListIterator listIterator = resultList.listIterator();
		reportData = new ReportData();
		while (listIterator.hasNext()) {
			Object[] objArray = ((Object[]) (listIterator.next()));
			String dbCode = String.valueOf(objArray[0]);
			if (StreamStatus.Invalid.name().equals(StreamStatus.getStatus(dbCode).name())) {
				reportData.setDataStreamKO((Long) objArray[1]);
			} else if (StreamStatus.Valid.name().equals(StreamStatus.getStatus(dbCode).name())) {
				reportData.setDataStreamOK((Long) objArray[1]);
			} else if (StreamStatus.Warning.name().equals(StreamStatus.getStatus(dbCode).name())) {
				reportData.setDataStreamWarn((Long) objArray[1]);
			}
			reportData.setTime(time);
		}
		reportDataList.add(reportData);		
		return reportDataList;
	}

	private class ReportData {
		private String time;
		private long dataStreamKO;
		private long dataStreamOK;
		private long dataStreamWarn;

		/**
		 * @return the time
		 */
		@SuppressWarnings("unused")
		public String getTime() {
			return time;
		}

		/**
		 * @param time
		 *            the time to set
		 */
		public void setTime(String time) {
			this.time = time;
		}

		/**
		 * @return the dataStreamKO
		 */
		@SuppressWarnings("unused")
		public long getDataStreamKO() {
			return dataStreamKO;
		}

		/**
		 * @param dataStreamKO
		 *            the dataStreamKO to set
		 */
		public void setDataStreamKO(long dataStreamKO) {
			this.dataStreamKO = dataStreamKO;
		}

		/**
		 * @return the dataStreamOK
		 */
		@SuppressWarnings("unused")
		public long getDataStreamOK() {
			return dataStreamOK;
		}

		/**
		 * @param dataStreamOK
		 *            the dataStreamOK to set
		 */
		public void setDataStreamOK(long dataStreamOK) {
			this.dataStreamOK = dataStreamOK;
		}

		/**
		 * @return the dataStreamWarn
		 */
		@SuppressWarnings("unused")
		public long getDataStreamWarn() {
			return dataStreamWarn;
		}

		/**
		 * @param dataStreamWarn
		 *            the dataStreamWarn to set
		 */
		public void setDataStreamWarn(long dataStreamWarn) {
			this.dataStreamWarn = dataStreamWarn;
		}
	}
}