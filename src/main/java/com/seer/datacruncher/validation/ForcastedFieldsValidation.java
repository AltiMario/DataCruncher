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

package com.seer.datacruncher.validation;

import com.seer.datacruncher.connection.ConnectionPoolsSet;
import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.persistence.manager.QuickDBRecognizer;
import com.seer.datacruncher.utils.generic.I18n;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Forecaster;
import net.sourceforge.openforecast.ForecastingModel;
import net.sourceforge.openforecast.Observation;

import org.apache.log4j.Logger;

public class ForcastedFieldsValidation implements DaoSet {
	
	private static int minElems;
	private static int maxElems;
	private static Logger log = Logger.getLogger(ForcastedFieldsValidation.class);
	
	static {
		Properties properties = new Properties();
		InputStream in = ForcastedFieldsValidation.class.getClassLoader().getResourceAsStream("forecast.properties");
		try {
			properties.load(in);
			minElems = Integer.parseInt(properties.get("forecast.min_elems").toString());
			maxElems = Integer.parseInt(properties.get("forecast.max_elems").toString());
		} catch (IOException e) {
			log.error("error reading forecast.properties", e);
		}		
	}
	
	private ForcastedFieldsValidation() {}
	
	/**
	 * Validates whether stream value within forecasted range.
	 * 
	 * @param schemaId 
	 * @return String with success/error message
	 */
	public static String validate(long schemaId) {
		int minAmountOfDp = 3;
		String result = "";
		SchemaEntity schemaEntity = schemasDao.find(schemaId);
		if (schemaEntity.getIsForecasted() && schemaEntity.getPublishToDb() && schemaEntity.getIdDatabase() > 0) {
			List<SchemaFieldEntity> list = schemasDao.retrieveAllLeaves(schemaEntity.getIdSchema());
			String schemaName = QuickDBRecognizer.getSchemaNamePlusVersion(schemaEntity);
			try {
				Connection connection = ConnectionPoolsSet.getConnection(schemaEntity.getIdDatabase());
				for (SchemaFieldEntity ent : list) {
					if (ent.getIsForecastable() && ent.getIdFieldType() == FieldType.numeric) {
						String fieldName = ent.getName();
						String sql = MessageFormat.format("SELECT {0} FROM {1}", fieldName, schemaName);
			            Statement statement = connection.createStatement();
			            ResultSet rs = statement.executeQuery(sql);
			            List<Object> resList = new ArrayList<Object>();
			            while (rs.next()) {
			            	resList.add(rs.getObject(1));
			            }
			            rs.close();
			            statement.close();
			            connection.close();			            
			            int size = resList.size();
						//forecasting algorithm can work only with more than 3 elements, otherwise it throws exception 
						if (size <= minAmountOfDp) {
							continue;
						}				
						DataSet dataSet = new DataSet();
						float accuracy = (ent.getForecastAccuracy() == 0 ? 1 : ent.getForecastAccuracy()) / (float)100;
						float amount = size * accuracy;
						if (amount < minElems) { 
							amount = minElems;
						} else if (amount > maxElems) {
							amount = maxElems;
						}
						float range = size / amount;
						if (range > size) {
							throw new RuntimeException("Forecasting: range is bigger than result size");
						}
						int i = 0;					
						for (float k = 0; k < size; k += range) {
							i++;
							int index = (int) k;
							Object o = resList.get(index);						
							Observation observationQ1 = new Observation(
									o instanceof Long ? ((Long) o).intValue() : Float.parseFloat(o.toString()));
							observationQ1.setIndependentValue(fieldName, i);
							dataSet.add(observationQ1);	
						}					
						//forecasting algorithm can work only with more than 3 elements, otherwise it throws exception 
						if (i <= minAmountOfDp) {
							String s = "Condition i < minAmountOfDp executed"; 
							log.warn("ForcastedFieldsValidation :: " +s);
							continue;
						}	
						ForecastingModel model = Forecaster.getBestForecast(dataSet);
						model.init(dataSet);
						//0 - any random value
						DataPoint dp = new Observation(0);
						dp.setIndependentValue(fieldName, i);
						DataSet resDataSet = new DataSet();
						resDataSet.add(dp);
						model.forecast(resDataSet);
						if (resDataSet.iterator().hasNext()) {
							float averageStep = getAverageStep(resList);
							DataPoint forecastedDp = (DataPoint) resDataSet.iterator().next();
							double forecastedValue = forecastedDp.getDependentValue();
							Object o = resList.get(size - 1);
							boolean isFloat = !(o instanceof Long);
							float streamValue = isFloat ? Float.parseFloat(o.toString()) : ((Long) o).intValue();
							if (isValueInRange(streamValue, forecastedValue, averageStep)) continue;
							result += MessageFormat.format(I18n.getMessage("message.forecastWarn"), streamValue,
									isFloat ? forecastedValue - averageStep : (int) (forecastedValue - averageStep),
									isFloat ? forecastedValue + averageStep : (int) (forecastedValue + averageStep))
									+ "<br>";
						}
					}
				}
			} catch (SQLException e) {
				log.error("Sql Query execution error", e);
			}
		}
		return result.isEmpty() ? null : (result.endsWith("<br>") ? result.substring(0, result.length() - 4) : result);
	}
	
	/**
	 * Checks whether stream value is in an allowed forecasted range.
	 * Example: stream value = 23, forecasted value 40, range = 3. Will
	 * 		result in false, because 23 not in a range [37 .. 43]. 
	 * 
	 * @param streamValue
	 * @param forecastedValue
	 * @param range
	 * @return true/false
	 */
	private static boolean isValueInRange(float streamValue, double forecastedValue, float range) {
		return (streamValue >= forecastedValue - range) && (streamValue <= forecastedValue + range);
	}
	
	/**
	 * Gets average step length of all elements in a list.
	 * Example: list with elements {10, 40, 20, 70} will return 30 + 20 + 50 / 3
	 * 
	 * @param list 
	 * @return average step length
	 */
	private static float getAverageStep(@SuppressWarnings("rawtypes") List list) {
		float v2 = -1;
		float step = 0;
		float overall = 0;
		int i = 0;
		for (Object o : list) {
			float v = o instanceof Long ? ((Long) o).intValue() : Float.parseFloat(o.toString());
			if (v2 == -1) {
				v2 = v;
			} else {
				step = v > v2 ? v - v2 : v2 - v;
				overall += (step == 0 ? 1 : step);
				i++;
				v2 = v;
			}			
		}
		return i > 0 ? overall / i : 0;		
	}
}
