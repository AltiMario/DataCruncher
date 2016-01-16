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

import com.seer.datacruncher.constants.FieldType;
import com.seer.datacruncher.constants.StreamStatus;
import com.seer.datacruncher.constants.StreamType;
import com.seer.datacruncher.datastreams.DatastreamDTO;
import com.seer.datacruncher.jpa.dao.DaoSet;
import com.seer.datacruncher.jpa.entity.MacroEntity;
import com.seer.datacruncher.jpa.entity.SchemaFieldEntity;
import com.seer.datacruncher.macros.JEXLFieldFactory;
import com.seer.datacruncher.macros.JexlEngineFactory;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.utils.generic.StreamsUtils;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.persistence.Query;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroRulesValidation implements DaoSet {

	static Logger log = Logger.getLogger(MacroRulesValidation.class);
	public static final String MACRO_SQL_VALIDATOR_PATTERN = "SELECT (\\w+|\\w+[.]\\w+) FROM \\w+[.]\\w+ WHERE (\\w+|\\w+[.]\\w+)\\s?(>|<|=|!=)\\s?(\\w+)";

	/**
	 * Apply validation macro rules.
	 * @param datastreamDTO
	 * @return ResultStepValidation
	 */
	protected ResultStepValidation doValidation(DatastreamDTO datastreamDTO) {
		long schemaId = datastreamDTO.getIdSchema();
		List<MacroEntity> list = new ArrayList<MacroEntity>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(schemasXSDDao.find(schemaId).getSchemaXSD()
					.getBytes()));
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("//annotation/appinfo/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				String val = nodes.item(i).getNodeValue();
				if (val.startsWith("@RuleCheck")) {
					String[] arr = val.split(";\n");
					for (String s : arr) {
						list.add(macrosDao.getMacroEntityByName(s.trim().substring(11)));
					}
					break;
				}
			}
		} catch (ParserConfigurationException e){
			log.error("ParserConfigurationException", e);
		} catch (SAXException e) {
			log.error("SAXException", e);
		} catch (IOException e) {
			log.error("IOException", e);
		} catch (XPathExpressionException e) {
			log.error("XPathExpressionException", e);
		}
		ResultStepValidation resultStepValidation = new ResultStepValidation();
		resultStepValidation.setValid(true);
		resultStepValidation.setMessageResult(I18n.getMessage("success.validationOK"));
		List<Element> xmlTextNodes = null;
		try {
			xmlTextNodes = StreamsUtils.parseStreamXml(datastreamDTO.getOutput());
		} catch (DocumentException e) {
			log.error("Stream parse exception", e);
		}
		JexlEngine jexl = JexlEngineFactory.getInstance();
		boolean isSuccess = true;
		Pattern pattern = Pattern.compile(MACRO_SQL_VALIDATOR_PATTERN, Pattern.CASE_INSENSITIVE);
		for (MacroEntity ent : list) {
			if (!isSuccess) continue;
			String rule = ent.getRule();
			List<Map<String, String>> varsList = parseVars(ent.getVars());
			combineVariableLists(varsList, schemaId);
			Matcher matcher = pattern.matcher(rule);
			while (matcher.find()) {
				String varName = matcher.group(4);
				String sqlRes = "false";
				for (Map<String, String> m : varsList) {
					if (m.get("uniqueName").equals(varName)) {
						for (Element el : xmlTextNodes) {
							int t = datastreamDTO.getIdStreamType();
							String fieldPath = (t == StreamType.XML || t == StreamType.XMLEXI ? ""
									: "ROOT/") + m.get("nodePath");
							if (fieldPath.equals(StreamsUtils.formatPathForXmlNode(el.getPath()))) {
								String prepSql = matcher.group().replaceAll(matcher.group(4), "\"" + el.getText() + "\"");
								String signum = matcher.group(3);
								if (signum.equals("<")) {
									prepSql = prepSql.replaceAll(signum, ">=");
								} else if (signum.equals(">")) {
									prepSql = prepSql.replaceAll(signum, "<=");
								} else if (signum.equals("!=")) {
									prepSql = prepSql.replaceAll(signum, "=");
								}
								Query q = entityManager.createNativeQuery(prepSql);
								@SuppressWarnings("rawtypes")
								List resList = q.getResultList();
								if ((signum.equals("=") && resList.size() > 0)
										|| (signum.equals("!=") && resList.size() == 0)
										|| (signum.equals(">") && resList.size() == 0)
										|| (signum.equals("<") && resList.size() == 0)) {
									sqlRes = "true";
								}
								break;
							}
						}
					}
				}
				rule = rule.replaceAll(matcher.group(), sqlRes);
			}
			Expression e = jexl.createExpression(rule);
			JexlContext context = new MapContext();
			for (Map<String, String> m : varsList) {
				for (Element el : xmlTextNodes) {
					int t = datastreamDTO.getIdStreamType();
					String fieldPath = (t == StreamType.XML || t == StreamType.XMLEXI ? ""
							: "ROOT/") + m.get("nodePath");
					if (fieldPath.equals(StreamsUtils.formatPathForXmlNode(el.getPath()))) {
						context.set(m.get("uniqueName"), JEXLFieldFactory.getField(m.get("fieldType"), el.getText()).getValue());
						break;
					}
				}
			}
			Object res = e.evaluate(context);
			if (res != null) {
				isSuccess = false;
				resultStepValidation.setValid(false);
				if (ent.getErrorType() == StreamStatus.Warning.getCode()) {
					resultStepValidation.setWarning(true);
				}
				resultStepValidation.setMessageResult(I18n.getMessage("error.validationMacro") + ": " + res.toString());
			}
		}
		return resultStepValidation;
	}

	/**
	 * Combines macro's variable lists: from jv_schema_fields and from jv_macros
	 *
	 * @param list - list of variables retrieved from jv_macros
	 * @param schemaId - id of the schema for which schema fields are taken to make tree leaves variable list
	 */
	public static void combineVariableLists(List<Map<String, String>> list, long schemaId) {
		List<SchemaFieldEntity> leaves = schemasDao.retrieveAllLeaves(schemaId);
		for (SchemaFieldEntity ent : leaves) {
			boolean isExists = false;
			for (Map<String, String> m : list) {
				if (m.get("uniqueName").equals(ent.getName())) {
					isExists = true;
					break;
				}
			}
			if (!isExists) {
				Map<String, String> m = new HashMap<String, String>();
				m.put("uniqueName", ent.getName());
				m.put("fieldType", FieldType.getFieldTypeAsStringById(ent.getIdFieldType()));
				m.put("nodePath", ent.getPath("/"));
				list.add(m);
			}
		}
	}

	/**
	 * Parses list of variables for macros in db.
	 *
	 * @param varList
	 * @return
	 */
	public static List<Map<String, String>> parseVars(String varList) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (varList == null || varList.isEmpty()) return list;
		for (String s : varList.split("\n")) {
			if (s.length() == 0) continue;
			Map<String, String> m = new HashMap<String, String>();
			String[] arr = s.split(";");
			m.put("uniqueName", arr[0]);
			m.put("fieldType", arr[1]);
			m.put("nodePath", arr[2]);
			list.add(m);
		}
		return list;
	}
}
