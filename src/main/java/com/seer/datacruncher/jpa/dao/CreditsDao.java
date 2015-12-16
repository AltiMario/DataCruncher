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

import com.seer.datacruncher.jpa.ReadList;
import com.seer.datacruncher.jpa.ReadOnlyTx;
import com.seer.datacruncher.jpa.entity.CreditsEntity;
import com.seer.datacruncher.utils.generic.I18n;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ReadOnlyTx
public class CreditsDao {

	Logger log = Logger.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    @Autowired
    CommonDao commonDao;

    protected CreditsDao() {
	}
	
	public ReadList read() {
		ReadList readList = new ReadList();
		try {
			readList.setResults(em.createNamedQuery("CreditsEntity.findAll").getResultList());
		} catch (Exception exception) {
			log.error("CreditsDao - read : " + exception);
			readList.setSuccess(false);
			readList.setMessage(I18n.getMessage("error.error") + " : CreditsDao - read");
			return readList;
		}
		readList.setSuccess(true);
		readList.setMessage(I18n.getMessage("success.listRecord"));
		return readList;
	}

	public void init() {
		CreditsEntity creditsEntity;
		try {
			@SuppressWarnings("unchecked")
			List<Long> result = em.createNamedQuery("CreditsEntity.count").getResultList();
			if (result.get(0) == 0L) {
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("JavaScript Framework for Rich Apps in Every Browser");
                creditsEntity.setLink("www.sencha.com");
                creditsEntity.setName("ExtJS");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("Hibernate is a collection of related projects enabling developers to utilize POJO-style domain models in their applications in ways extending well beyond Object/Relational Mapping.");
                creditsEntity.setLink("www.hibernate.org");
                creditsEntity.setName("Hibernate");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("The leading platform to build and run enterprise Java applications.");
                creditsEntity.setLink("www.springsource.org");
                creditsEntity.setName("Spring");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a software project management and comprehension tool.");
                creditsEntity.setLink("maven.apache.org");
                creditsEntity.setName("Maven");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a project focused on all aspects of reusable Java components.");
                creditsEntity.setLink("commons.apache.org");
                creditsEntity.setName("Apache Commons");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a Java library for working with XML, XPath, and XSLT.");
                creditsEntity.setLink("www.dom4j.org");
                creditsEntity.setName("Dom4j");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's an implementation of the W3C Efficient XML Interchange (EXI) format specification.");
                creditsEntity.setLink("exificient.sourceforge.net");
                creditsEntity.setName("Exificient");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("SCOWL (Spell Checker Oriented Word Lists) is a collection of word lists intended to be suitable for use in spell checkers.");
                creditsEntity.setLink("wordlist.sourceforge.net");
                creditsEntity.setName("English Word List");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("Linguistico is a linguistics tools project based on Italian language.");
                creditsEntity.setLink("linguistico.sourceforge.net");
                creditsEntity.setName("Italian Word List");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("Enterprise Job Scheduler");
                creditsEntity.setLink("www.quartz-scheduler.org");
                creditsEntity.setName("Quartz");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("High-performance JSON processor.");
                creditsEntity.setLink("jackson.codehaus.org");
                creditsEntity.setName("Jackson");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("Velocity is used to generate web pages in applications, usually as a direct replacement for JSP.");
                creditsEntity.setLink("velocity.apache.org");
                creditsEntity.setName("Velocity");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a shared library provided for parsing, generating, manipulating, and validating XML documents using the DOM, SAX, and SAX2 APIs.");
                creditsEntity.setLink("xerces.apache.org");
                creditsEntity.setName("Xerces");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a Java-based toolkit for applications or applets that want to use images in the Scalable Vector Graphics (SVG) format for various purposes.");
                creditsEntity.setLink("xmlgraphics.apache.org");
                creditsEntity.setName("Batik");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("MySQL is primarily an RDBMS and therefore ships with no GUI tools to administer MySQL databases or manage data contained within.");
                creditsEntity.setLink("www.mysql.com");
                creditsEntity.setName("MySQL");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's an object-relational database system that has the features of traditional proprietary database systems with enhancements to be found in next-generation DBMS systems.");
                creditsEntity.setLink("www.postgresql.org");
                creditsEntity.setName("PostgreSQL");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's an open source 100% pure Java (type 4) JDBC 3.0 driver for Microsoft SQL Server (6.5, 7, 2000, 2005 and 2008) and Sybase (10, 11, 12, 15).");
                creditsEntity.setLink("jtds.sourceforge.net");
                creditsEntity.setName("jTDS");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It serves as a simple facade or abstraction for various logging frameworks.");
                creditsEntity.setLink("www.slf4j.org");
                creditsEntity.setName("SLF4J");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It provides a world-class marketplace to manage, distribute and consume any kind of API in the world, both cloud and internal.");
                creditsEntity.setLink("www.mashape.com");
                creditsEntity.setName("Mashape");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a Semantic Web Framework for Java.");
                creditsEntity.setLink("jena.sourceforge.net");
                creditsEntity.setName("Jena");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a relational database management system.");
                creditsEntity.setLink("www.firebirdsql.org");
                creditsEntity.setName("Firebirdsql");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's an innovation of xml database.");
                creditsEntity.setLink("www.xerial.org");
                creditsEntity.setName("Xerial");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("SQLite is a software library that implements a self-contained, serverless, zero-configuration, transactional SQL database engine.");
                creditsEntity.setLink("sqlite.org");
                creditsEntity.setName("SQLite");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("HSQLDB is the leading SQL relational database engine written in Java.");
                creditsEntity.setLink("hsqldb.org");
                creditsEntity.setName("HSQLDB");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a HTTP/1.1 compliant HTTP agent implementation based on HttpCore.");
                creditsEntity.setLink("hc.apache.org");
                creditsEntity.setName("httpclient");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a Java HTML Parser.");
                creditsEntity.setLink("jsoup.org");
                creditsEntity.setName("jsoup");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a Java platform for creating and deploying web services applications.");
                creditsEntity.setLink("axis.apache.org");
                creditsEntity.setName("Apache Axis");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a Web Services Description Language for Ja Project(WSDL4J).");
                creditsEntity.setLink("wsdl4j.sourceforge.net");
                creditsEntity.setName("wsdl4j");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It provides a Java-based indexing and search implementation.");
                creditsEntity.setLink("lucene.apache.org");
                creditsEntity.setName("Lucene");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It provides bridging functionality to other JMS providers that implement the JMS 1.0.2 and above specification.");
                creditsEntity.setLink("activemq.apache.org");
                creditsEntity.setName("ActiveMQ");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It's a Java FTP server.");
                creditsEntity.setLink("mina.apache.org");
                creditsEntity.setName("FTPServer");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It is a Enterprise Open Source Database.");
                creditsEntity.setLink("www.sapdb.org");
                creditsEntity.setName("SAP DB");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It is a java library for transforming beans, maps, collections, java arrays and XML to JSON and back again to beans and DynaBeans.");
                creditsEntity.setLink("json-lib.sourceforge.net");
                creditsEntity.setName("JSON-lib");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It is a new XML object model. It is an open source (LGPL), tree-based API for processing XML with Java that strives for correctness, simplicity, and performance.");
                creditsEntity.setLink("www.xom.nu");
                creditsEntity.setName("XOM");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It is Java API for Microsoft Documents.");
                creditsEntity.setLink("poi.apache.org");
                creditsEntity.setName("POI");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It is a simple framework to write repeatable tests. It is an instance of the xUnit architecture for unit testing frameworks.");
                creditsEntity.setLink("junit.sourceforge.net");
                creditsEntity.setName("JUnit");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It is a simple interpreter of an expression language called XPath. JXPath applies XPath expressions to graphs of objects of all kinds: JavaBeans, Maps, Servlet contexts, DOM etc, including mixtures thereof.");
                creditsEntity.setLink("commons.apache.org/jxpath");
                creditsEntity.setName("commons-jxpath");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("It is Expression language which extends the Expression Language of the JSTL.");
                creditsEntity.setLink("commons.apache.org/jexl/");
                creditsEntity.setName("commons-jexl");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("Commons VFS provides a single API for accessing various different file systems.");
                creditsEntity.setLink("commons.apache.org/vfs/");
                creditsEntity.setName("commons-vfs2");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("General purpose forecasting model library.");
                creditsEntity.setLink("www.stevengould.org/software/openforecast/index.shtml");
                creditsEntity.setName("OpenForecast");
                commonDao.persist(creditsEntity);
                creditsEntity = new CreditsEntity();
                creditsEntity.setDescription("Apache Camel is a versatile open-source integration framework based on known Enterprise Integration Patterns.");
                creditsEntity.setLink("camel.apache.org/");
                creditsEntity.setName("Camel");
                commonDao.persist(creditsEntity);
			}
		} catch(Exception exception) {
			log.error("CreditsDao - init : " + exception);
		}
    }
}