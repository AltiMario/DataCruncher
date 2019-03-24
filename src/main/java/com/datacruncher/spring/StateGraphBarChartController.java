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

import com.datacruncher.constants.StreamStatus;
import com.datacruncher.jpa.ReadList;
import com.datacruncher.jpa.dao.DaoSet;
import com.datacruncher.jpa.entity.ApplicationEntity;
import com.datacruncher.jpa.entity.SchemaEntity;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author c_rsharv
 *
 */
public class StateGraphBarChartController implements Controller, DaoSet {
	private String startingYear;

	public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response) throws ServletException, IOException {

 		ObjectMapper mapper = new ObjectMapper();
 		ServletOutputStream out = null;
		response.setContentType("application/json");
		out = response.getOutputStream();
		String appId = request.getParameter("appId");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		String rptType = request.getParameter("rptType");
		String schemaId = request.getParameter("schemaId");
		String day = request.getParameter("day");
		Calendar now = Calendar.getInstance();		   
		try{
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
			
			Calendar startDate = new GregorianCalendar();
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			startDate.set(Calendar.MONTH, (Integer.parseInt(month) - 1));
			startDate.set(Calendar.YEAR, Integer.parseInt(year));
			
			Calendar endDate = new GregorianCalendar();
			endDate.set(Calendar.DAY_OF_MONTH, 1);
			endDate.set(Calendar.MONTH, (Integer.parseInt(month) - 1));			
			endDate.set(Calendar.YEAR, Integer.parseInt(year));
			endDate.add(Calendar.MONTH, 1);
			
			ReadList readList = null;
			List<ReportData> resultReportDataList = new ArrayList<ReportData>();
								
			if(rptType == null || rptType.trim().length() == 0) {
				while(startDate.before(endDate)) {
					 
					readList = datastreamsDao.getBarChartData(appId, schemaId, startDate, null);
					List<ReportData> reportDataList = getReportDataList(readList.getResults(), startDate);
					resultReportDataList.addAll(reportDataList);
					
					startDate.add(Calendar.DAY_OF_MONTH, 1);
				}
			} else if(rptType.equals("annual")){
				int count  = 0;			
				
				String yearName[] = {"January","February","March","April","May","June","July","August","September","October","November","December" };
				startDate.set(Calendar.MONTH, 0);
				startDate.set(Calendar.DAY_OF_MONTH, 1);
				startDate.set(Calendar.YEAR, Integer.parseInt(year));
				
				while(count < 12) {
					
					readList = datastreamsDao.getBarChartData(appId, schemaId, startDate, rptType);
					List<ReportData> reportDataList = getReportDataList(readList.getResults(), startDate);
					
					ReportData instance = new ReportData();
					
					for(ReportData reportData : reportDataList) {
						instance.setDataStreamKO(reportData.getDataStreamKO() + instance.getDataStreamKO());
						instance.setDataStreamOK(reportData.getDataStreamOK() + instance.getDataStreamOK());
						instance.setDataStreamWarn(reportData.getDataStreamWarn() + instance.getDataStreamWarn());
						instance.setDay(yearName[count]);
					}
					resultReportDataList.add(instance);
										
					startDate.add(Calendar.MONTH, 1);													
					count++;
				}
			} else if(rptType.equals("detailed")) {

				readList = schemasDao.readByApplicationId(-1, -1, Long.valueOf(appId));
				if(readList != null && readList.getResults().size() > 0) {
					List<SchemaEntity> listSchemas = (List<SchemaEntity>)readList.getResults();
					startDate.set(Calendar.MONTH, (Integer.parseInt(month) - 1));
					startDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
					startDate.set(Calendar.YEAR, Integer.parseInt(year));
					
					for(SchemaEntity instance : listSchemas) {
						readList = datastreamsDao.getBarChartData(appId, String.valueOf(instance.getIdSchema()), startDate, rptType);
						List<ReportData> reportDataList = getReportDataList(readList.getResults(), startDate);
						
						for(ReportData rptData : reportDataList) {

							ReportData reportData = new ReportData();

							reportData.setSchemaName(instance.getName());
							reportData.setDataStreamKO(rptData.getDataStreamKO());
							reportData.setDataStreamWarn(rptData.getDataStreamWarn());
							reportData.setDataStreamOK(rptData.getDataStreamOK());
							resultReportDataList.add(reportData);
						}						
					}
				} else {
					ReportData reportData = new ReportData();

					reportData.setSchemaName("");
					reportData.setDataStreamKO(0);
					reportData.setDataStreamWarn(0);
					reportData.setDataStreamOK(0);
					resultReportDataList.add(reportData);
				}
			}
			
			readList.setResults(resultReportDataList);
			out.write(mapper.writeValueAsBytes(readList));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				out.flush();
				out.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}

		mapper = null;
 		return null;
	}
	
	//-------------------HELPERS-----------------
	private List<ReportData> getReportDataList(List resultList, Calendar startDate) {
		
		ReportData reportData;
		List<ReportData> reportDataList = new ArrayList<ReportData>();
		HashMap<Object, String> reportDataMap = new HashMap<Object, String>();
		
		if (resultList == null || resultList.size() == 0) {			
			reportData = new ReportData();			
			reportData.setDay(String.valueOf(startDate.get(Calendar.DAY_OF_MONTH)));
			reportData.setSchemaName("");
			reportData.setDataStreamKO(0);
			reportData.setDataStreamOK(0);
			reportData.setDataStreamWarn(0);
			reportDataList.add(reportData);			
			return reportDataList;
		}
		ListIterator listIterator = resultList.listIterator();
		int count = 0;
		boolean val = false;
		while (listIterator.hasNext()) {
			Object[] objArray = ((Object[]) (listIterator.next()));			
			if (count == 0) {
				reportDataMap.put(objArray[2], objArray[2] + "=" + String.valueOf(objArray[0]) + "," + String.valueOf(objArray[1]));
			}
			if (count != 0) {
				Set<Object> keySet = reportDataMap.keySet();
                for (Object keyValue : keySet) {
                    if (keyValue.equals(objArray[2])) {
                        String values = reportDataMap.get(objArray[2]);
                        reportDataMap.put(objArray[2], values + "=" + String.valueOf(objArray[0]) + "," + String.valueOf(objArray[1]));
                    } else {
                        val = true;
                    }
                }
			}
			if (val) {				
				if(!reportDataMap.containsKey(objArray[2])) {					
					reportDataMap.put(objArray[2], objArray[2] + "=" + String.valueOf(objArray[0]) + "," + String.valueOf(objArray[1]));
				}
			}
			count++;			
		}		
		Map<Object, String> reportDataSortMap = new HashMap<Object, String>();
		reportDataSortMap = sortHashMap(reportDataMap);
		Collection<String> coll = reportDataSortMap.values();		
		Iterator<String> itr = coll.iterator();
		String[] splitData = null;
		String[] dateArr = null;
		String[] values = null;		
		while (itr.hasNext()) {
			reportData = new ReportData();
			splitData = itr.next().split("=");					
			dateArr = splitData[0].split("-");			
			if (splitData.length > 2) {				
				for (int j = 1; j < splitData.length; j++) {
					values = splitData[j].split(",");
					String dbCode = String.valueOf(values[0]);
                    if (StreamStatus.Invalid.name().equals(StreamStatus.getStatus(dbCode).name())) {
                        reportData.setDataStreamKO(Long.parseLong(values[1]));
                    } else if (StreamStatus.Valid.name().equals(StreamStatus.getStatus(dbCode).name())) {
                        reportData.setDataStreamOK(Long.parseLong(values[1]));
                    } else {
                        reportData.setDataStreamWarn(Long.parseLong(values[1]));
                    }
				}

			} else {				
				values = splitData[1].split(",");				
				String dbCode = String.valueOf(values[0]);
				if (StreamStatus.Invalid.name().equals(
						StreamStatus.getStatus(dbCode).name())) {
					reportData.setDataStreamKO(Long.parseLong(values[1]));
				} else if (StreamStatus.Valid.name().equals(
						StreamStatus.getStatus(dbCode).name())) {
					reportData.setDataStreamOK(Long.parseLong(values[1]));
				} else {
					reportData.setDataStreamWarn(Long.parseLong(values[1]));
				}
			}
			reportData.setDay(dateArr[2]);		
			reportDataList.add(reportData);
			reportData = null;
		}		
		reportDataSortMap = null;
		reportDataMap = null;
		return reportDataList;
	}
	
	private HashMap<Object, String> sortHashMap(HashMap<Object, String> reportDataMap) {
		Map<Object, String> tempMap = new HashMap<Object, String>();
		for (Object wsState : reportDataMap.keySet()) {
			tempMap.put(wsState, reportDataMap.get(wsState));
		}

		List<Object> mapKeys = new ArrayList<Object>(tempMap.keySet());
		List<String> mapValues = new ArrayList<String>(tempMap.values());
		HashMap<Object, String> sortedMap = new LinkedHashMap<Object, String>();
		TreeSet<String> sortedSet = new TreeSet<String>(mapValues);
		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;
		for (int i = 0; i < size; i++) {
			sortedMap.put(mapKeys.get(mapValues.indexOf(sortedArray[i])),
					(String) sortedArray[i]);
		}
		tempMap = null;
		mapKeys = null;
		mapValues = null;
		return sortedMap;
	}
	
	private class ReportData{
		private String day;
		private long dataStreamKO;
		private long dataStreamOK;
		private long dataStreamWarn;
		private String schemaName;
		
		public String getDay() {
			return day;
		}
		
		/**
		 * @param day
		 */
		public void setDay(String day) {
			this.day = day;
		}
		
		/**
		 * @return the dataStreamKO
		 */
		public long getDataStreamKO() {
			return dataStreamKO;
		}
		/**
		 * @param dataStreamKO the dataStreamKO to set
		 */
		public void setDataStreamKO(long dataStreamKO) {
			this.dataStreamKO = dataStreamKO;
		}
		/**
		 * @return the dataStreamOK
		 */
		public long getDataStreamOK() {
			return dataStreamOK;
		}
		/**
		 * @param dataStreamOK the dataStreamOK to set
		 */
		public void setDataStreamOK(long dataStreamOK) {
			this.dataStreamOK = dataStreamOK;
		}
		/**
		 * @return the dataStreamWarn
		 */
		public long getDataStreamWarn() {
			return dataStreamWarn;
		}
		/**
		 * @param dataStreamWarn the dataStreamWarn to set
		 */
		public void setDataStreamWarn(long dataStreamWarn) {
			this.dataStreamWarn = dataStreamWarn;
		}

		public String getSchemaName() {
			return schemaName;
		}

		public void setSchemaName(String schemaName) {
			this.schemaName = schemaName;
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
