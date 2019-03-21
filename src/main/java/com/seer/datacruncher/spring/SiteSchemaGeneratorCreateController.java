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

import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.jpa.Create;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.ApplicationEntity;
import com.seer.datacruncher.jpa.entity.SchemaEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.jpa.entity.UserEntity;
import com.seer.datacruncher.scraper.Crawler;
import com.seer.datacruncher.scraper.CrawlerHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SiteSchemaGeneratorCreateController implements Controller, DaoSet {

	private final Logger log = Logger.getLogger(this.getClass());
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserEntity user = (UserEntity) session.getAttribute("user");
		if (user == null) {
			return null;
		}

		String webSiteURL = request.getParameter("websiteURL");
		boolean isSinglePage = Boolean.parseBoolean(request.getParameter("isSinglePage"));
				
		ApplicationEntity applicationEntity = null;
		
		String appName = "";
		
		if(webSiteURL.indexOf("www.") != -1) {
			appName = webSiteURL.substring(webSiteURL.indexOf("www.") + 4);
		} else {
			appName = webSiteURL.substring(webSiteURL.indexOf("//") + 2);
		}

		if(appName.indexOf("/") != -1) {
			appName = appName.substring(0, appName.indexOf("/"));
		}
		
		Create create = new Create();
		
		List<ApplicationEntity> listApplications = appDao.findByName(appName);
		
		if(listApplications != null && listApplications.size() > 0) {
			applicationEntity = listApplications.get(0);
		} else {
			applicationEntity = new ApplicationEntity();
			applicationEntity.setName(appName);
			applicationEntity.setIsActive(1);
			applicationEntity.setIsSiteGenerated(true);
			create = appDao.create(applicationEntity);
			if(create.getSuccess()) {
				applicationEntity = (ApplicationEntity)create.getResults();
			}
		}
				
		try {
			
			if (applicationEntity != null) {
								
				List<CrawlerHelper> listData = null;
				
				if(isSinglePage) {
					CrawlerHelper crawlerInstance = scrapeSingleWebPage(webSiteURL);
					listData = new ArrayList<CrawlerHelper>();
					listData.add(crawlerInstance);					
				} else {
					Crawler crawlerInstance = new Crawler();
					listData = crawlerInstance.getData(webSiteURL);
				}
				
				if (listData != null && listData.size() > 0) {
					
					for (CrawlerHelper instance : listData) {
						
						String schemaName = instance.getName();
												
						schemaName = schemaName.replaceAll("[^A-Za-z0-9]", "");
						
						List<SchemaEntity> listSchemaEntity = schemasDao.findByName(schemaName);
						
						if(listSchemaEntity != null && listSchemaEntity.size() > 0) {
							schemasDao.destroy(listSchemaEntity.get(0).getIdSchema());
						}
						
						SchemaEntity schemaEntity = new SchemaEntity();
						schemaEntity.setName(schemaName);
						schemaEntity.setIdApplication(applicationEntity.getIdApplication());
						schemaEntity.setIdSchemaType(1);
						
						schemaEntity.setLoadedXSD(false);
						schemaEntity.setDelimiter("");
						schemaEntity.setDescription(instance.getName());
						schemaEntity.setIsActive(1);
						schemaEntity.setIdStreamType(StreamType.JSON);
						
						create = schemasDao.create(schemaEntity);
						if(create.getSuccess()) {
							
							schemaEntity = (SchemaEntity)create.getResults();
														
							for (String strFieldName : instance.getFields()) {

								SchemaFieldEntity schemaFieldEntity = new SchemaFieldEntity ();
								schemaFieldEntity.setIdParent(0l);
								schemaFieldEntity.setIdSchema(schemaEntity.getIdSchema());
								
								schemaFieldEntity.setName(strFieldName);
								schemaFieldEntity.setDescription("");
								schemaFieldEntity.setIdFieldType(FieldType.alphanumeric);
								schemaFieldEntity.setNillable(false);
								schemaFieldEntity.setIdCheckType(0);
								schemaFieldEntity.setIdAlign(1);
								schemaFieldEntity.setFillChar(" ");				
								
								schemaFieldEntity.setElementOrder(0);
								
								schemaFieldsDao.create(schemaFieldEntity);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			create.setSuccess(false);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		response.setContentType("application/json");
        ServletOutputStream out = response.getOutputStream();
        
        out.write(mapper.writeValueAsBytes(create));
		out.flush();
		out.close();
		return null;
	}

	public CrawlerHelper scrapeSingleWebPage(String webPageUrl) {

		String htmlContent = "";
		CrawlerHelper instance = new CrawlerHelper();
		
		try {
		    
            WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3_6);            
            HtmlPage htmlPage = webClient.getPage(webPageUrl);
            HtmlElement body = htmlPage.getBody();
            webClient.waitForBackgroundJavaScript(1000); //was 5000
            Set<String> tags = new HashSet<String>();
            htmlContent = getStringContent(webPageUrl);
            Elements elements = Jsoup.parse(htmlContent).body().getAllElements();
            for (Element e : elements) {
                tags.add(e.tagName());

            }
            String html = htmlContent;
            for (String str : tags) {
                for (HtmlElement elmt : body.getElementsByTagName(str)) {
                    if (elmt.hasEventHandlers("onclick")&&!elmt.hasAttribute("href")&&(!elmt.hasAttribute("unselectable")||!elmt.getAttribute("unselectable").equalsIgnoreCase("ON")) &&elmt.isDisplayed()) {
                        String s = ((HtmlPage) elmt.click()).asXml();
                        if(s.length()>html.length()){
                            html = s;
                        }
                    }
                }
            }
            Document doc = Jsoup.parse(html);
            Elements e = doc.select("form");

            if (!e.isEmpty()) {
            	log.info("url=> " + webPageUrl);
                     
                for (Element et : e) {
                    instance.setName(et.attr("action"));
                    List<String> fields = new ArrayList<String>();
                    log.info("form=> " + et.attr("action"));
                    Elements ets = et.getAllElements();
                    for (Element e1 : ets) {
                        if (e1.tagName().equals("input")) {
                            if (e1.attr("type").equalsIgnoreCase("hidden") || e1.attr("type").equalsIgnoreCase("submit") ||
                                    e1.attr("type").equalsIgnoreCase("button") || e1.attr("type").equalsIgnoreCase("reset") ||
                                    e1.attr("type").equalsIgnoreCase("checkbox") || e1.attr("type").equalsIgnoreCase("radio") ){

                            	log.debug("discarded: "+e1.attr("name")); //don't need
                            } else {
                                fields.add(e1.attr("name"));
                                log.info(e1.attr("type") + ": " + e1.attr("name"));
                            }
                        }
                        if (e1.tagName().equals("textarea")) {
                            fields.add(e1.attr("name"));
                            log.info("A:textarea: " + e1.attr("name"));
                        }
                        if (e1.tagName().equals("select")) {
                        	log.debug("discarded: "+e1.attr("name")); //don't need
                        }
                        if (e1.tagName().equals("button")) {
                        	log.debug("discarded: "+e1.attr("name")); // don't need
                        }
                    }
                    instance.setFields(fields);
                }
            }
		 } catch (IOException ex) {
             log.error(ex.getMessage());
         } catch (FailingHttpStatusCodeException ex) {
             log.error(ex.getMessage());
         }
		
		return instance;
	}
	public String getStringContent(String webPageUrl) {
		URL url;
		 
		try {
			// get URL content
			url = new URL(webPageUrl);
			URLConnection conn = url.openConnection();
 
			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));
 
			String inputLine;
			StringBuilder result = new StringBuilder();
 
			while ((inputLine = br.readLine()) != null) {
				result.append(inputLine);
			}
 
			br.close();
 
			return result.toString();
			
		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
		
		return null;
	}
}