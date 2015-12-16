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

package com.seer.datacruncher.jpa.entity;

import com.seer.datacruncher.constants.DSCompressType;
import com.seer.datacruncher.datastreams.EXI;
import com.seer.datacruncher.utils.entity.IDGenerator;
import com.seer.datacruncher.utils.generic.CommonUtils;
import com.seer.datacruncher.utils.generic.DomToOtherFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import javax.persistence.*;
import javax.xml.transform.TransformerException;

import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.exceptions.EXIException;



@Entity
@Table(name="jv_datastreams")
@NamedQueries({
	@NamedQuery(name="DatastreamEntity.findById",query="SELECT d FROM DatastreamEntity d WHERE d.idDatastream = :idDatastream"),
	//@NamedQuery(name="DatastreamEntity.findBetweenDateRange", query="SELECT d FROM DatastreamEntity d WHERE d.receivedDate BETWEEN :start  AND :end ORDER BY d.idDatastream DESC"),
	//@NamedQuery(name="DatastreamEntity.countByValidatedOK", query="SELECT count (d) FROM DatastreamEntity d"),
    //@NamedQuery(name="DatastreamEntity.findInvalidBetweenDateRange", query="SELECT d FROM DatastreamEntity d,SchemaEntity e WHERE d.idSchema = e.idSchema AND e.isAvailable = 1 AND d.checked <>1 AND d.receivedDate BETWEEN :start  AND :end ORDER BY d.idDatastream DESC"),
    //@NamedQuery(name="DatastreamEntity.countInvalidBetweenDateRange", query="SELECT count (d) FROM DatastreamEntity d,SchemaEntity e WHERE d.idSchema = e.idSchema AND e.isAvailable = 1 AND d.checked <>1 AND d.receivedDate BETWEEN :start  AND :end"),
    //@NamedQuery(name="DatastreamEntity.findInvalidBetweenDateRangeAndType", query="SELECT d FROM DatastreamEntity d,SchemaEntity e WHERE d.idSchema = e.idSchema AND e.isAvailable = 1 AND d.checked = :checked  AND d.receivedDate BETWEEN :start  AND :end ORDER BY d.idDatastream DESC"),
	@NamedQuery(name="DatastreamEntity.findByIdSchema", query="SELECT d FROM DatastreamEntity d where d.idSchema = :idSchema")
})
public class DatastreamEntity  {
	@Id
	/*
	 * See constructor for description.
	 * 
	 * @GeneratedValue(strategy = GenerationType.AUTO)*/
	@Column (name= "id_datastream")
	private String idDatastream;
	
	@Column(name = "id_schema")
    private long idSchema;
	
	@Column(name = "id_application")
    private long idApplication = 0;

	@Column(name = "compress_type")
    private int compressType = 0;
	
	@Lob
	@Column (name= "datastream")
	private byte[] datastream;
	
	@Column (name= "checked", columnDefinition="int default 0", nullable=false)
	private Integer checked = 0;
	
	@Lob
	@Column (name= "message")
	private String message;
	
	@Column(name = "received_date")
    private Date receivedDate;
	
	/* Kundera compliance comment-in
	@Temporal(TemporalType.TIME)
	@Column(name = "received_time")
    private Date receivedTime;*/

    @Transient
    private String schemaName =" ";

    public DatastreamEntity() {
    	//id of String type is used for faster search within MongoDB,  
    	//but String id is not auto-generated for MySQL that's why our own implementation.
    	setIdDatastream(IDGenerator.nextID());
    }
    
	public String getIdDatastream() {
		return idDatastream;
	}

	public void setIdDatastream(String idDatastream) {
		this.idDatastream = idDatastream;
	}

	public long getIdSchema() {
        return idSchema;
    }
    
    public void setIdSchema(long idSchema) {
        this.idSchema = idSchema;
    }
	
	public long getIdApplication() {
		return idApplication;
	}

	public void setIdApplication(long idApplication) {
		this.idApplication = idApplication;
	}

	public String getDatastream() {
		String stream = new String(datastream);
		if (getCompressType() == DSCompressType.EXI.getNum()) {
			try {
				return EXI.decode(datastream);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (EXIException e) {
				e.printStackTrace();
			}
		} else if (getCompressType() == DSCompressType.BSON.getNum()) {
			stream = BSON.decode(datastream).toString();			
		} else {
			try {
				stream = CommonUtils.gzipDecode(datastream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return stream;
	}

	public void setDatastream(String datastream) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] bArr = null;
		if (DomToOtherFormat.isXml(datastream)) {
			//exi compression for xml streams (exi, xml, excel)
			try {
				EXI.encodeXmlToEXI(new ByteArrayInputStream(datastream.getBytes("UTF-8")), baos);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (EXIException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			setCompressType(DSCompressType.EXI.getNum());
			bArr = baos.toByteArray();
		} else if (CommonUtils.isJSON(datastream)){
			//bson compression for json streams
			try {
				@SuppressWarnings("unchecked")
				Map<String,Object> map =
				        new ObjectMapper().readValue(datastream, Map.class);
				BSONObject bson = new BasicBSONObject(map);
				setCompressType(DSCompressType.BSON.getNum());
				bArr = BSON.encode(bson);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {		
			//gzip compression for all others (flatfiles)
			setCompressType(DSCompressType.GZIP.getNum());		
			try {
				bArr = CommonUtils.gzipEncode(datastream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.datastream = bArr;
	}

	public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public int getCompressType() {
		return compressType;
	}

	public void setCompressType(int compressType) {
		this.compressType = compressType;
	}

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}