/*
 *   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
 *   SeerDataCruncher is released under AGPL license.

 *   Copyright (c) 2015 foreSEE-Revolution ltd
 *   All rights reserved
 *
 *   Site: http://www.see-r.com
 *   Contact:  info@see-r.com
 */

package com.seer.datacruncher.spring;

import com.seer.datacruncher.macros.JEXLFieldFactory;
import com.seer.datacruncher.macros.JexlEngineFactory;
import com.seer.datacruncher.utils.generic.I18n;
import com.seer.datacruncher.validation.MacroRulesValidation;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacrosValidateController implements Controller {

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String varList = request.getParameter("varList");
		String rule = request.getParameter("rule");
		Long schemaId = Long.valueOf(request.getParameter("schemaId"));
		if (rule == null) {
			throw new IllegalArgumentException("MacrosValidateController: null parameter 'rule'");
		}	
		String success = "true";		
		Map<String, String> resMap = new HashMap<String, String>();		
		if (rule.trim().isEmpty()) {
			success = "false";
			resMap.put("errMsg", I18n.getMessage("error.macro.emptyRule"));
		}
		Pattern pattern = Pattern.compile(MacroRulesValidation.MACRO_SQL_VALIDATOR_PATTERN, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(rule);
		rule = matcher.replaceAll("true");
		List<Map<String, String>> list = MacroRulesValidation.parseVars(varList);
		MacroRulesValidation.combineVariableLists(list, schemaId);
		JexlEngine jexl = JexlEngineFactory.getInstance();
	    try {
			Expression e =  jexl.createExpression(rule);
		    JexlContext context = new MapContext();
		    for (Map<String, String> m : list) {
		    	String anyStringDigit = "7";
				context.set(m.get("uniqueName"), JEXLFieldFactory.getField(m.get("fieldType"), anyStringDigit).getValue());
			}	    	
	    	e.evaluate(context);	
	    } catch (Exception e1) {
	    	success = "false";
	    	resMap.put("errMsg", e1.getMessage() + "\n" + getStackTrace(e1));
	    }
		resMap.put("success", success);
		response.getWriter().print(new JSONObject(resMap).toString());
		return null;
	}
	
	private static String getStackTrace(Throwable throwable) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		throwable.printStackTrace(printWriter);
		return writer.toString();
	}
}