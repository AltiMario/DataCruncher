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

import com.seer.datacruncher.constants.StreamStatus;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class StateGraphController implements Controller, DaoSet {
	private String startingYear;	

	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {
 		ObjectMapper mapper = new ObjectMapper();
 		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		String reqType = request.getParameter("req");
		try{
		if("yearList".equalsIgnoreCase(reqType)){		
			out.write(mapper.writeValueAsBytes(getYearList()));
		}else{
			String appId = request.getParameter("appId");
			String year = request.getParameter("year");
			String month = request.getParameter("month");
			String schemaId = request.getParameter("schemaId");
			Calendar now = Calendar.getInstance();
			if("null".equalsIgnoreCase(appId) || "undefined".equalsIgnoreCase(appId)){						
				ReadList readList = null;			
				readList = appDao.readSingleResult();			
				@SuppressWarnings("rawtypes")
				List val = readList.getResults();
				if (val != null && val.size() > 0) {
					ApplicationEntity appEntity = (ApplicationEntity)val.get(0);
					appId = String.valueOf((Long)appEntity.getIdApplication());
				}else{
					appId = null;
				}
				
			}
			if("null".equalsIgnoreCase(year) || "undefined".equalsIgnoreCase(year)){
				year = String.valueOf(now.get(Calendar.YEAR));
			}
			if("null".equalsIgnoreCase(month)|| "undefined".equalsIgnoreCase(month)){
				month = String.valueOf(now.get(Calendar.MONTH) + 1);
			}
			if("null".equalsIgnoreCase(schemaId) || "undefined".equalsIgnoreCase(schemaId) || "-1".equalsIgnoreCase(schemaId)){
				schemaId = null;
			}			
			ReadList readList = datastreamsDao.getReportData(appId,year,month, schemaId, null);
			List<ReportData> reportDataList = getReportDataList(readList.getResults());
			readList.setResults(reportDataList);
			out.write(mapper.writeValueAsBytes(readList));
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				out.flush();
				out.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
 			
 		return null;
	}

	//---------- HELPERS ----------
	
	private ReadList getYearList(){
		ReadList readList = new ReadList();
		Calendar calender = Calendar.getInstance();
		int currentYear = calender.get(Calendar.YEAR);
		if (startingYear == null || startingYear.length() == 0) {
			startingYear = "2010";
		}
		int starYear = Integer.parseInt(startingYear);
		if (starYear > currentYear) {
			starYear = 2010;
		}
		List<ReportData> beanList = new ArrayList<ReportData>();
		ReportData bean = null;
		for (int i = starYear; i <= currentYear; i++) {
			bean = new ReportData();
			bean.setValue(Long.valueOf(i));
			bean.setName(String.valueOf(i));
			beanList.add(bean);
			bean = null;
		}
		readList.setResults(beanList);
		readList.setSuccess(true);
		readList.setMessage("success");
		return readList;
	}
	
	private List<ReportData> getReportDataList(List resultList) {
		ReportData reportData = null;
		List<ReportData> reportDataList = new ArrayList<ReportData>();
		if (resultList == null || resultList.size() == 0) {
			reportData = new ReportData();
			reportData.setName("");
			reportData.setValue(0L);
			reportDataList.add(reportData);
			return reportDataList;
		}
		
		ListIterator listIterator = resultList.listIterator();
		while (listIterator.hasNext()) {
			Object[] objArray = ((Object[]) (listIterator.next()));
			reportData = new ReportData();
			String dbCode = String.valueOf(objArray[0]);
			reportData.setName(StreamStatus.getStatus(dbCode).name());
			reportData.setValue((Long) objArray[1]);
			reportDataList.add(reportData);
			reportData = null;
		}
		if(!(reportDataList.size() > 0 && reportDataList.get(0).getName().equals(StreamStatus.getStatus("0").name()))) {
			reportData = new ReportData();
			reportData.setName(StreamStatus.getStatus("0").name());
			reportData.setValue(0l);
			reportDataList.add(0, reportData);
		}
		
		if(!(reportDataList.size() > 1 && reportDataList.get(1).getName().equals(StreamStatus.getStatus("1").name()))) {
			reportData = new ReportData();
			reportData.setName(StreamStatus.getStatus("1").name());
			reportData.setValue(0l);
			reportDataList.add(1, reportData);
		}
		if(!(reportDataList.size() > 2 && reportDataList.get(2).getName().equals(StreamStatus.getStatus("2").name()))) {
			reportData = new ReportData();
			reportData.setName(StreamStatus.getStatus("2").name());
			reportData.setValue(0l);
			reportDataList.add(reportData);
		}
		return reportDataList;
	}
	
	private class ReportData{
		private String name;
		private Long value;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the value
		 */
		public Long getValue() {
			return value;
		}
		/**
		 * @param value the value to set
		 */
		public void setValue(Long value) {
			this.value = value;
		}
	}
	//---------- SETTERS ----------
	/**
	 * @param startingYear the startingYear to set
	 */
	public void setStartingYear(String startingYear) {
		this.startingYear = startingYear;
	}
	
}