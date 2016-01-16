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

package com.seer.datacruncher.jpa.dao;

import com.seer.datacruncher.constants.ReportType;
import com.seer.datacruncher.jpa.entity.DatastreamEntity;
import com.seer.datacruncher.utils.annotations.AnnotationsUtils;
import com.seer.datacruncher.utils.generic.ReportsUtils;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.impetus.kundera.PersistenceProperties;
import com.impetus.kundera.metadata.model.KunderaMetadata;
import com.impetus.kundera.metadata.model.PersistenceUnitMetadata;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

public class MongoDbDao {

	private static final String MONGO_PU_NAME = "mongoPU";
	private static final String MONGO_KEY_SPACE = "jv";

	private static final String _APP_ID;
	private static final String _SCHEMA_ID;
	private static final String _RECEIVED_DATE;
	private static final String _CHECKED;

	private static Logger log = Logger.getLogger(MongoDbDao.class);
	private static Mongo mongoPool;
	private static String contactNode;
	private static String defaultPort;
	private static String poolSize;
	private static String userName;
	private static String password;

	private static final EntityManagerFactory emf;

	static {
		AnnotationsUtils.setSchemaAnnotationAttrForDatastreamEntity(MONGO_KEY_SPACE + "@" + MONGO_PU_NAME);
		emf = Persistence.createEntityManagerFactory(MongoDbDao.MONGO_PU_NAME);
		PersistenceUnitMetadata puMetadata = KunderaMetadata.INSTANCE.getApplicationMetadata().getPersistenceUnitMetadata(MONGO_PU_NAME);
		if (puMetadata != null) {
			Properties props = puMetadata.getProperties();
			contactNode = (String) props.get(PersistenceProperties.KUNDERA_NODES);
			defaultPort = (String) props.get(PersistenceProperties.KUNDERA_PORT);
			poolSize = props.getProperty(PersistenceProperties.KUNDERA_POOL_SIZE_MAX_ACTIVE);
			userName = (String) props.get(PersistenceProperties.KUNDERA_USERNAME);
			password = (String) props.get(PersistenceProperties.KUNDERA_PASSWORD);
		}
		if (MONGO_KEY_SPACE == null) {
			throw new IllegalArgumentException("mongoDB keySpace is not specified");
		}
		if (contactNode == null) {
			throw new IllegalArgumentException("mongoDB contactNode is not specified");
		}
		if (defaultPort == null) {
			throw new IllegalArgumentException("mongoDB defaultPort is not specified");
		}
		_APP_ID = getFieldName("idApplication");
		_SCHEMA_ID = getFieldName("idSchema");
		_RECEIVED_DATE = getFieldName("receivedDate");
		_CHECKED = getFieldName("checked");
	}

	/**
	 * Gets mongoDB entityManagerFactory.
	 *
	 * @return
	 */
	public static synchronized EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}


	/**
	 * Gets data for Annual, Detailed, Monthly reports.
	 *
	 * 		 db.jv_datastreams.aggregate({$match : {id_application : 2}},
	 *	 {$project : {_id : 0, checked : 1, received_date : 1}},
	 *	 {$group: { _id: "$checked", result_sum : {$sum : 1}, result_date :
	 *	 {$first : "$received_date"}}})
	 *	 + date_range
	 *
	 * @return reports data
	 */
	public static List<?> getList(ReportType type, int appId, int schemaId, Calendar currentDate) {
		List<Object> list = new ArrayList<Object>();
		DBCollection coll = getDatastreamsCollection();
		if (type == ReportType.MONTHLY || type == ReportType.ANNUAL || type == ReportType.DETAILED) {
			// $match operation
			DBObject matchFields = new BasicDBObject(_APP_ID, appId);
			if (type == ReportType.DETAILED) {
				matchFields.put(_SCHEMA_ID, schemaId);
			}
			DBObject dateRange = new BasicDBObject("$gt", ReportsUtils.getStartDate(type, currentDate));
			dateRange.put("$lt", ReportsUtils.getEndDate(type, currentDate));
			matchFields.put(_RECEIVED_DATE, dateRange);
			DBObject match = new BasicDBObject("$match", matchFields);
			// $projection operation
			DBObject fields = new BasicDBObject("_id", 0);
			fields.put(_CHECKED, 1);
			fields.put(_RECEIVED_DATE, 1);
			DBObject project = new BasicDBObject("$project", fields);
			// $group operation
			DBObject groupFields = new BasicDBObject("_id", "$" + _CHECKED);
			final String resultSum = "result_sum";
			final String resultDate = "result_date";
			groupFields.put(resultSum, new BasicDBObject("$sum", 1));
			groupFields.put(resultDate, new BasicDBObject("$first", "$" + _RECEIVED_DATE));
			DBObject group = new BasicDBObject("$group", groupFields);
			AggregationOutput aggr = coll.aggregate(match, project, group);
			Iterator<DBObject> it = aggr.results().iterator();
			while (it.hasNext()) {
				DBObject obj = it.next();
				Object[] arr = new Object[3];
				arr[0] = obj.get("_id");
				arr[1] = obj.get(resultSum);
				arr[2] = new SimpleDateFormat("yyyy-MM-dd").format(obj.get(resultDate));
				list.add(arr);
			}
		}
		return list;
	}

	/**
	 * Gets data for Donut and RealTime report.
	 *
	 * 		 db.jv_datastreams.aggregate({$match : {id_application : 2}},
	 *	 {$project : {_id : 0, checked : 1, received_date : 1}},
	 *	 {$group: { _id: "$checked", result_sum : {$sum : 1}, result_date :
	 *	 {$first : "$received_date"}}})
	 *	 + date_range
	 *
	 * @return
	 */
	public static List<?> getList(int appId, int schemaId, int year, int month, Calendar currentDate, boolean isDonut) {
		List<Object> list = new ArrayList<Object>();
		DBCollection coll = getDatastreamsCollection();
		DBObject matchFields = new BasicDBObject();
		if (isDonut) {
			matchFields.put(_APP_ID, appId);
			if (schemaId != 0) {
				matchFields.put(_SCHEMA_ID, schemaId);
			}
		}
		DBObject dateRange = new BasicDBObject("$gt", isDonut ? ReportsUtils.getDonutStartDate(year, month) : ReportsUtils.getStartDate(
				ReportType.REAL_TIME, currentDate));
		dateRange.put("$lt",
				isDonut ? ReportsUtils.getDonutEndDate(year, month) : ReportsUtils.getEndDate(ReportType.REAL_TIME, currentDate));
		matchFields.put(_RECEIVED_DATE, dateRange);
		DBObject match = new BasicDBObject("$match", matchFields);
		// $projection operation
		DBObject fields = new BasicDBObject("_id", 0);
		fields.put(_CHECKED, 1);
		DBObject project = new BasicDBObject("$project", fields);
		// $group operation
		DBObject groupFields = new BasicDBObject("_id", "$" + _CHECKED);
		final String resultSum = "result_sum";
		groupFields.put(resultSum, new BasicDBObject("$sum", 1));
		DBObject group = new BasicDBObject("$group", groupFields);
		AggregationOutput aggr = coll.aggregate(match, project, group);
		Iterator<DBObject> it = aggr.results().iterator();
		while (it.hasNext()) {
			DBObject obj = it.next();
			Object[] arr = new Object[2];
			arr[0] = obj.get("_id");
			arr[1] = Long.valueOf((Integer)obj.get(resultSum));
			list.add(arr);
		}
		return list;
	}

	/**
	 *  db.jv_datastreams.find({$or : [{checked : 0}, {checked : 1}], id_schema : 1, ...})
	 *
	 * @return
	 */
	/*public static synchronized long countInvalidStreamsByUser(long idSchema, Date start, Date end, String checks) {
		DBCollection coll = getDatastreamsCollection();
		DBObject root = new BasicDBObject("id_schema", idSchema);
		List<String> list = Arrays.asList(checks.split(","));
		List<DBObject> orList = new ArrayList<DBObject>();
		for (String check : list) {
			orList.add(new BasicDBObject("checked", Integer.valueOf(check)));
		}
		root.put("$or", orList);
		root.put(_RECEIVED_DATE, new BasicDBObject("$gt", start));
		root.put(_RECEIVED_DATE, new BasicDBObject("$lt", end));
		return coll.find(root).count();
	}*/

	private static synchronized DBCollection getDatastreamsCollection() {
		DB db = getConnection();
		return db.getCollection(DatastreamEntity.class.getAnnotation(Table.class).name());
	}

	/**
	 * Gets connection to data base.
	 * Note: mongoPool is thread-safe.
	 *
	 * @return connection
	 */
	private static DB getConnection() {
		try {
			if (mongoPool == null) {
				mongoPool = new Mongo(contactNode, Integer.parseInt(defaultPort));
				MongoOptions mo = mongoPool.getMongoOptions();
				if (mo.getConnectionsPerHost() <= 0 && !StringUtils.isEmpty(poolSize)) {
					mo.connectionsPerHost = Integer.parseInt(poolSize);
				}
			}
			DB mongoDB = mongoPool.getDB(MONGO_KEY_SPACE);
			if (userName != null && !userName.isEmpty() && password != null && !password.isEmpty()) {
				mongoDB.authenticate(userName, password.toCharArray());
			}
			return mongoDB;
		} catch (NumberFormatException e) {
			log.error("Number format exception", e);
		} catch (UnknownHostException e) {
			log.error(e);
		}
		return null;
	}

	/**
	 * Gets table field name by class field.
	 * Example: fieldId = 'idApplication', return = 'id_application'
	 *
	 * @param fieldId
	 * @return
	 */
	private static String getFieldName(String fieldId) {
		for (Field field : DatastreamEntity.class.getDeclaredFields()) {
			if (field.getName() == fieldId) {
				return field.getAnnotation(Column.class).name();
			}
		}
		return null;
	}

}
