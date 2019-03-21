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

package com.seer.datacruncher.jpa.dao;

import com.seer.datacruncher.constants.GenericType;
import com.seer.datacruncher.constants.ReportType;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.Destroy;
import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.entity.DatastreamEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.spring.AppContext;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.generic.ReportsUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class DatastreamsDao {

    Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;
    
    private EntityManager emMongo;

	private EntityManager getEntityManager() {
		if (CommonUtils.isMongoDB() && emMongo == null) {
			emMongo = MongoDbDao.getEntityManagerFactory().createEntityManager();
		}
		return CommonUtils.isMongoDB() ? emMongo : em;
	}
	
	/**
	 * Gets entity manager for sql databases.
	 * 
	 * @return em
	 */
	private EntityManager getRDBMSEntityManager() {
		return em;
	}
	
    @Transactional(readOnly = true)
	public ReadList read(long idSchema, int start, int limit, SchemaEntity entity, String fields, String searchValue) {
		String queryString = null;//"SELECT count (d) FROM DatastreamEntity d WHERE d.idSchema = :idSchema";
		Query query = null;

		/*
		 * SM: do we need it?
		 * 
		 * String queryCriteria = "";
		if (entity.getIsValid() == 1 || entity.getIsInValid() == 1 || entity.getIsWarning() == 1) {
			if (entity.getIsValid() == 1) {
				queryCriteria += "1";
			}
			if (entity.getIsInValid() == 1) {
				queryCriteria += (entity.getIsValid() == 1) ? " ," : " ";
				queryCriteria += "0";
			}
			if (entity.getIsWarning() == 1) {
				queryCriteria += (entity.getIsValid() == 1 || entity.getIsInValid() == 1) ? " ," : " ";
				queryCriteria += "2";
			}
		} else {
			queryCriteria += "999";
		}*/
        
		String filterClause = "";
		if (fields != null) {
			String[] fieldsList = fields.split(",");
			if (fieldsList.length > 0) {
				filterClause = " AND (";
				for (String strFieldName : fieldsList) {
					filterClause += "d." + strFieldName + " LIKE '%" + searchValue + "%'";
					filterClause += " OR ";
				}
				filterClause = filterClause.substring(0, filterClause.lastIndexOf("OR")) + ")";
			}
		}
		queryString += filterClause;
        
        //SM: do we need the total amount of streams? Kundera change.
        //@SuppressWarnings("unchecked")
        //List<Long> count = getEntityManager().createQuery(queryString).setParameter("idSchema", idSchema).getResultList();

        queryString = "SELECT d FROM DatastreamEntity d WHERE d.idSchema = :idSchema " ;//AND ( " + queryCriteria + " ) ";
        //MongoDB driver uses native column names for ordering 
        String orderBy = MessageFormat.format(" ORDER BY {0} DESC", CommonUtils.isMongoDB() ? "_id" : "d.idDatastream");
        queryString += filterClause + orderBy;
        query = getEntityManager().createQuery(queryString);
        ReadList readList = new ReadList();
        query.setParameter("idSchema", idSchema);
        //query.setFirstResult(start);
        query.setMaxResults(limit);
        try {
        	//kundera change. SM: do we need it?
            readList.setTotal(0/*count.longValue()*/);
            try {
            	List<?> list = query.getResultList();
            	//System.out.println(list.size());
                readList.setResults(list);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception exception) {
            log.error("DatastreamsDao - read : " + exception);
            readList.setSuccess(false);
            readList.setMessage(null);
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(null);
        return readList;
    }

    @Transactional(readOnly = true)
	public long getTotalValidatedStreams() {
		return (long) getEntityManager().createQuery("SELECT d.idDatastream FROM DatastreamEntity d").getResultList().size();
	}

    @Transactional
    public Create create(DatastreamEntity dataStreamEntity) {
        Create create = new Create();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        dataStreamEntity.setReceivedDate(gregorianCalendar.getTime());
		long schemaId = dataStreamEntity.getIdSchema();
		SchemasDao schemasDao = (SchemasDao) AppContext.getApplicationContext().getBean("SchemasDao");
		dataStreamEntity.setIdApplication(schemasDao.find(schemaId).getIdApplication());
        try {
        	getEntityManager().persist(dataStreamEntity);
        } catch (Exception exception) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("DatastreamsDao - create : " + exception);
            create.setSuccess(false);
            create.setResults(null);
            create.setMessage("DatastreamsDao - create : " + exception);
            return create;
        }
        create.setSuccess(true);
        create.setResults(null);
        create.setMessage(null);
        return create;
    }

	/*public Update update(DatastreamEntity dataStreamEntity) {
		Update update = new Update();

		try {
			getEntityManager().merge(dataStreamEntity);
		} catch (Exception exception) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("DatastreamsDao - update : " + exception);
			update.setSuccess(false);
			update.setMessage(null);
			return update;
		}
		update.setSuccess(true);
		update.setMessage(null);
		return update;
	}*/

    @Transactional
	public Destroy destroy(String idDatastream) {
		Destroy destroy = new Destroy();
		try {
			DatastreamEntity entity = getEntityManager().find(DatastreamEntity.class, idDatastream);
			if (entity != null) {
				getEntityManager().remove(entity);
			} else {
				throw new EntityNotFoundException();
			}
		} catch (Exception exception) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			log.error("DatastreamsDao - destroy : " + exception);
			destroy.setSuccess(false);
			destroy.setResults(null);
			return destroy;
		}
		destroy.setSuccess(true);
		destroy.setResults(null);
		destroy.setMessage(I18n.getMessage("success.fieldCanc"));
		return destroy;
	}

	@Transactional(readOnly = true)
	public ReadList getReportData(String appId, String year, String month, String schemaId, Calendar currentTime) {
		ReadList readList = new ReadList();
		readList.setSuccess(true);
		readList.setMessage(null);
		// there are 2 types of requests: 1. stateGraph (donut) (all pars exist
		// except currTime == null and schemaId can be absent)2. realTime (all
		// pars == null except time)
		boolean isDonut = (appId != null && year != null && month != null) && currentTime == null;
		if (CommonUtils.isMongoDB()) {
			readList.setResults(MongoDbDao.getList(appId == null ? 0 : Integer.valueOf(appId),
					schemaId == null ? 0 : Integer.valueOf(schemaId), year == null ? 0 : Integer.valueOf(year),
					month == null ? 0 : Integer.valueOf(month), currentTime, isDonut));
		} else {
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("SELECT d.checked, count(d.checked) FROM DatastreamEntity d "
					+ " WHERE d.receivedDate between :start AND :end ");
			if (isDonut) {
				sBuilder.append(" AND d.idApplication = :idApplication ");
				if (schemaId != null) {
					sBuilder.append(" AND d.idSchema = :idSchema ");
				}
			}
			sBuilder.append(" GROUP BY d.checked ");
			Query query = getEntityManager().createQuery(sBuilder.toString());

			if (appId != null) {
				query.setParameter("idApplication", Long.parseLong(appId));
			}
			if (schemaId != null) {
				query.setParameter("idSchema", Long.parseLong(schemaId));
			}
			
			query.setParameter("start", isDonut ? ReportsUtils.getDonutStartDate(Integer.valueOf(year), Integer.valueOf(month))
					: ReportsUtils.getStartDate(ReportType.REAL_TIME, currentTime));
			query.setParameter(
					"end",
					isDonut ? ReportsUtils.getDonutEndDate(Integer.valueOf(year), Integer.valueOf(month)) : ReportsUtils.getEndDate(
							ReportType.REAL_TIME, currentTime));

			readList.setResults(query.getResultList());
		}
		if (readList.getResults() != null && readList.getResults().size() > 0) {
			readList.setSuccess(true);
			readList.setMessage(null);
		} else {
			readList.setSuccess(true);
			readList.setMessage(null);
		}
		return readList;
	}

    @Transactional(readOnly = true)
	public ReadList getBarChartData(String appId, String schemaId, Calendar currentDate, String reportType) {
		ReportType type = reportType == null || reportType.trim().length() == 0 ? ReportType.MONTHLY
				: reportType.equals("annual") ? ReportType.ANNUAL : reportType.equals("detailed") ? ReportType.DETAILED : null;
		if (type == null) {
			throw new IllegalArgumentException("DatastreamsDao :: undefined report parameter");
		}
        ReadList readList = new ReadList();
        
		if (CommonUtils.isMongoDB()) {
			readList.setResults(MongoDbDao.getList(type, appId == null ? 0 : Integer.valueOf(appId),
					schemaId == null ? 0 : Integer.valueOf(schemaId), currentDate));
		} else {
	        StringBuilder sBuilder = new StringBuilder();
	        StringBuilder whereClause = new StringBuilder();
	
	        if (type == ReportType.MONTHLY) {
	            sBuilder.append("SELECT d.checked, count(d.checked), DATE_FORMAT(d.receivedDate, '%Y-%m-%d') FROM DatastreamEntity d ");
	        } else if(type == ReportType.ANNUAL) {
	            sBuilder.append("SELECT d.checked, count(d.checked), min(d.receivedDate) FROM DatastreamEntity d ");
	        } else if(type == ReportType.DETAILED) {
	            sBuilder.append("SELECT d.checked, count(d.checked), max(d.receivedDate) FROM DatastreamEntity d ");
	        }
	
	        //System.out.println(schemaId);
	        if (appId != null) {
	            //_report sBuilder.append(" , SchemaEntity s ");
				whereClause.append("WHERE d.idApplication = :idApplication ");
				if (schemaId != null) {
					whereClause.append(" AND d.idSchema= :idSchema");
	            }
	        } else if (schemaId != null) {
	            whereClause.append(" WHERE d.idSchema= :idSchema");
	        }
	
	        //if (isMonthly || reportType.equals("annual") || reportType.equals("detailed")){
	            if (whereClause.toString().length() == 0)
	                whereClause
	                        .append(" WHERE d.receivedDate >= :start and d.receivedDate < :end");
	            else
	                whereClause
	                        .append(" AND d.receivedDate >= :start and d.receivedDate < :end");
	        /*} else if(reportType.equals("detailed")){
	            if (whereClause.toString().length() == 0)
	                whereClause
	                        .append(" WHERE d.receivedDate = :start");
	            else
	                whereClause
	                        .append(" AND d.receivedDate = :start");
	        }*/
	
	        sBuilder.append(whereClause.toString());
	
	        if (type == ReportType.MONTHLY) {
	            //sBuilder.append(" GROUP BY day(d.receivedDate), d.checked ORDER BY d.receivedDate ASC");
	        	sBuilder.append(" GROUP BY d.checked ");
	        } else if(type == ReportType.ANNUAL){
	            //sBuilder.append(" GROUP BY month(d.receivedDate), d.checked ORDER BY min(d.receivedDate) ASC");
	        	sBuilder.append(" GROUP BY d.checked");
	        } else if(type == ReportType.DETAILED){
	            sBuilder.append(" GROUP BY d.checked");
	        }
	
	        Query query = getEntityManager().createQuery(sBuilder.toString());
	        //System.out.println(sBuilder.toString());
	        if (appId != null) {
	            query.setParameter("idApplication", Long.parseLong(appId));
	        }
	        if (schemaId != null) {
	            query.setParameter("idSchema", Long.parseLong(schemaId));
	        }
	
	        query.setParameter("start", ReportsUtils.getStartDate(type, currentDate));
	        query.setParameter("end", ReportsUtils.getEndDate(type, currentDate));
	        
            try {
                readList.setResults(query.getResultList());
			} catch (Exception ex) {
				ex.printStackTrace();
				log.error(sBuilder.toString());
				for (Parameter<?> parameter : query.getParameters()) {
					log.error(parameter.getName() + ":" + query.getParameterValue(parameter.getName()));
				}
			}
        }


		if (readList.getResults() != null && readList.getResults().size() > 0) {
			readList.setSuccess(true);
			readList.setMessage(null);
		} else {
			readList.setSuccess(true);
			readList.setMessage(null);
		}
		return readList;
    }
    
    // 1st alert method
	@Transactional(readOnly = true)
	public Long countInvalidDataStreamsByUser(Date startDate, Date endDate, UserEntity user, String streamStatuses) {
		// not admin user is connected only with one schema
		StringBuilder queryStr = new StringBuilder("SELECT us.idSchema FROM UserSchemasEntity us, SchemaEntity s ");
		queryStr.append(" WHERE us.idSchema = s.idSchema ");
		queryStr.append(" AND s.isAvailable = 1 ");
		queryStr.append(" AND us.idUser = :idUser ");
		// here always call RDBMS entityManager
		Query query = getRDBMSEntityManager().createQuery(queryStr.toString());
		query.setParameter("idUser", user.getIdUser());
		try {
			@SuppressWarnings("unchecked")
			List<Long> list = query.getResultList();
			long count = 0;
			if (list.size() > 0) {
				long schemaId = list.get(0).longValue();
				count = getInvalidDataStreamsCountByStatusMulti(schemaId, streamStatuses, startDate, endDate);
			}
			return count;
		} catch (Exception exception) {
			log.error("DatastreamsDao - countInvalidDataStreamsByUser : " + exception);
			exception.printStackTrace();
			return 0L;
		}
	}
	
	private long getInvalidDataStreamsCountByStatusMulti(long schemaId, String streamStatuses, Date startDate, Date endDate) {
		int count = 0;
		// 0 - error, 2 - warning
		String[] arr = streamStatuses.split(",");
		if (arr.length == 2) {
			int a = Integer.valueOf(arr[0].trim());
			int b = Integer.valueOf(arr[1].trim());
			if (a == 0 && b == 2 || a == 2 && b == 0) {
				count += getInvalidDataStreamsCountByStatusSingle(schemaId, a, startDate, endDate);
				count += getInvalidDataStreamsCountByStatusSingle(schemaId, b, startDate, endDate);
			}
		} else if (arr.length == 1) {
			int c = Integer.valueOf(arr[0].trim());
			count += getInvalidDataStreamsCountByStatusSingle(schemaId, c, startDate, endDate);
		}
		return count;
	}
 	
	private long getInvalidDataStreamsCountByStatusSingle(long schemaId, int status, Date startDate, Date endDate) {
		String q = "SELECT count(d) FROM DatastreamEntity d WHERE d.checked = :checked AND d.idSchema = :idSchema  AND d.receivedDate BETWEEN :start  AND :end ";
		@SuppressWarnings("unchecked")
		List<Long> res = getEntityManager().createQuery(q).setParameter("start", startDate).setParameter("end", endDate)
				.setParameter("idSchema", schemaId).setParameter("checked", status).getResultList();
		if (res.size() > 0) {
			long result = CommonUtils.isMongoDB() ? res.size() : res.get(0).longValue();
			return result;
		}
		return 0L;
	}
    
    // 2nd alert method
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
    public ReadList getInvalidDataStreamsByUser(Date startDate, Date endDate,UserEntity user, String streamStatuses) {
        ReadList readList = new ReadList();
		// not admin user is connected only with one schema
		StringBuilder queryStr = new StringBuilder("SELECT us.idSchema FROM UserSchemasEntity us, SchemaEntity s ");
		queryStr.append(" WHERE us.idSchema = s.idSchema ");
		queryStr.append(" AND s.isAvailable = 1 ");
		queryStr.append(" AND us.idUser = :idUser ");
		// here always call RDBMS entityManager
		Query query = getRDBMSEntityManager().createQuery(queryStr.toString());
		query.setParameter("idUser", user.getIdUser());
        try {
			List<Long> list = query.getResultList();
			@SuppressWarnings("rawtypes")
			List resultList = new ArrayList();
			if (list.size() > 0) {
				long schemaId = list.get(0).longValue();
				resultList = getInvalidDataStreamsByStatusMulti(schemaId, streamStatuses, startDate, endDate);
			}
            readList.setResults(resultList);
        } catch (Exception exception) {
            log.error("DatastreamsDao - getInvalidDataStreamsByUser : " + exception);
            readList.setSuccess(false);
            readList.setMessage(null);
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(null);
        return readList;
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getInvalidDataStreamsByStatusMulti(long schemaId, String streamStatuses, Date startDate, Date endDate) {
		List resultList = new ArrayList();
		// 0 - error, 2 - warning
		String[] arr = streamStatuses.split(",");
		if (arr.length == 2) {
			int a = Integer.valueOf(arr[0].trim());
			int b = Integer.valueOf(arr[1].trim());
			if (a == 0 && b == 2 || a == 2 && b == 0) {
				resultList.addAll(getInvalidDataStreamsListByStatusSingle(schemaId, a, startDate, endDate));
				resultList.addAll(getInvalidDataStreamsListByStatusSingle(schemaId, b, startDate, endDate));
			}
		} else if (arr.length == 1) {
			int c = Integer.valueOf(arr[0].trim());
			resultList.addAll(getInvalidDataStreamsListByStatusSingle(schemaId, c, startDate, endDate));
		}
		return resultList;
	}
    
	@SuppressWarnings("rawtypes")
	private List getInvalidDataStreamsListByStatusSingle(long schemaId, int status, Date startDate, Date endDate) {
		String q = "SELECT d FROM DatastreamEntity d WHERE d.checked = :checked AND d.idSchema = :idSchema  AND d.receivedDate BETWEEN :start  AND :end ";
		List res = getEntityManager().createQuery(q).setParameter("start", startDate).setParameter("end", endDate)
				.setParameter("idSchema", schemaId).setParameter("checked", status).setMaxResults(GenericType.maxEmailStream)
				.getResultList();
		return res;
	}
    
    // 3d alert method
    @Transactional(readOnly = true)
    public Long countInvalidDataStreams(Date startDate, Date endDate) {
    	long count = 0;
		// here always call RDBMS entityManager
		Query query = getRDBMSEntityManager().createQuery("SELECT s FROM SchemaEntity s WHERE s.isAvailable = 1");
        try {
            @SuppressWarnings("unchecked")
            List<SchemaEntity> schemas = query.getResultList();
            for (SchemaEntity s : schemas) {
            	count += getInvalidDataStreamsCountByStatusMulti(s.getIdSchema(), "0,2", startDate, endDate);
            }
            return count;
        } catch (Exception exception) {
            log.error("DatastreamsDao - countInvalidDataStreams : " + exception);
            return 0L;
        }
    }
    
    // 4th alert method
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = true)
    public ReadList getInvalidDataStreams(Date startDate, Date endDate) {
        ReadList readList = new ReadList();
		List resultList = new ArrayList();
		// here always call RDBMS entityManager
		Query query = getRDBMSEntityManager().createQuery("SELECT s FROM SchemaEntity s WHERE s.isAvailable = 1");
        try {
            List<SchemaEntity> schemas = query.getResultList();
            for (SchemaEntity s : schemas) {
            	resultList.addAll(getInvalidDataStreamsByStatusMulti(s.getIdSchema(), "0,2", startDate, endDate));
            }
            readList.setResults(resultList);
        } catch (Exception exception) {
            log.error("DatastreamsDao - getInvalidDataStreams : " + exception);
            readList.setSuccess(false);
            readList.setMessage(null);
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(null);
        return readList;
    }
    
    @Transactional(readOnly = true)
    public ReadList read(long idDatastream) {
        String logMsg = "DatastreamsDao:read:";
        log.debug(logMsg + "Entry");
        ReadList readList = new ReadList();
        try {
            @SuppressWarnings("unchecked")
            List<DatastreamEntity> result = getEntityManager()
                    .createNamedQuery("DatastreamEntity.findById")
                    .setParameter("idDatastream", idDatastream).getResultList();
            if (result != null && result.size() > 0) {
                readList.setResults(result);
            }
        } catch (Exception exception) {
            log.error(logMsg + "Exception: " + exception);
            readList.setSuccess(false);
            readList.setMessage(null);
            return readList;
        }
        readList.setSuccess(true);
        readList.setMessage(null);
        return readList;
    }
}