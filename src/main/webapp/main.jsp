<%--
  ~ Copyright (c) 2015  www.see-r.com
  ~ All rights reserved
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as
  ~ published by the Free Software Foundation, either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.seer.datacruncher.jpa.entity.UserEntity" session="true" %>
<%@ page import="java.util.List" %>
<%@ page import="com.seer.datacruncher.spring.AppContext"%>
<%@ page import="com.seer.datacruncher.spring.AppInfoBean"%>
<%@ page import="com.seer.datacruncher.utils.generic.I18n"%>
<jsp:include page="jsp/utils.jsp" />
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>SeerDataCruncher</title>
	<link href="./images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
    <script type="text/javascript" src="./extjs/ext-all.js"></script>

	<%  
		boolean isSessionValid = false;
		String theme = "neptune";
		UserEntity userEntity = (UserEntity) request.getSession().getAttribute("user");
		long idRole = 0;
		long userId = 0;
		String language = "en";
		List<String> rActs = null;
		if(userEntity!=null){
			idRole = userEntity.getIdRole();
			userId = userEntity.getIdUser();
			rActs = userEntity.getRoleActivities();
			language = userEntity.getLanguage();
			theme = userEntity.getTheme();
		}
		if(theme.equals("neptune")) {		
	%>
	<link rel="stylesheet" type="text/css" href="./extjs/css/ext-all-neptune.css"/>
	<%
		} else {
	%>
	<link rel="stylesheet" type="text/css" href="./extjs/css/ext-all.css"/>
	<%
		}
	%>
    <script type="text/javascript">
		
		var language = '<%=language%>';
		document.write("<script charset=\"UTF-8\" type=\"text/javascript\" src=\"./extjs/locale/ext-lang-" + language + ".js\"></scr" + "ipt>")
		document.write("<script charset=\"UTF-8\" type=\"text/javascript\" src=\"./locale/custom-lang-" + language + ".js\"></scr" + "ipt>")
		
		Ext.Ajax.request({
			params: {
				language: language
			},
			url: './language.json'
		});
		var role;
		var rActivities = new Array();
		var roleActivities;
		var userId;
		var searchFeature = {
		   ftype: 'searching',
		   minChars: 1,
		   mode: 'remote'
		};
		role = <%=idRole%>;
		userId = <%=userId%>;
		rActivities = '<%=rActs%>';
		roleActivities = rActivities.toString();
		if(role==0){
			window.location='index.jsp';
		}
		
	</script>
	<script type="text/javascript" src="./extjs/ux/GridSearch.js"></script>
	<script type="text/javascript" src="./extjs/ux/App.js"></script>
	<script type="text/javascript" src="./extjs/ux/FileUploadField.js"></script>
    <script type="text/javascript" src="./extjs/ux/ProgressBarPager.js"></script>
    <script type="text/javascript" src="./extjs/ux/SearchField.js"></script>
	<script type="text/javascript" src="./extjs/ux/RowEditing.js"></script>
	<script type="text/javascript" src="./js/classes/RowEditingValidation.js"></script>
    <link rel="stylesheet" type="text/css" href="./extjs/ux/css/fileuploadfield.css"/>
	<link rel="stylesheet" type="text/css" href="./extjs/ux/css/RowEditor.css"/>
    <link rel="stylesheet" type="text/css" href="./css/style.css"/>
    <script type="text/javascript" src="./js/applicationsContent.js"></script>
    <script type="text/javascript" src="./js/applicationsPopup.js"></script>
    <script type="text/javascript" src="./js/databasesContent.js"></script>
    <script type="text/javascript" src="./js/databasesPopup.js"></script>
    <script type="text/javascript" src="./js/loadingStreamPopup.js"></script>
    <script type="text/javascript" src="./js/schemasContent.js"></script>
    <script type="text/javascript" src="./js/generationStreamContent.js"></script>
    <script type="text/javascript" src="./js/loadingStreamContent.js"></script>
    <script type="text/javascript" src="./js/standardContent.js"></script>
    <script type="text/javascript" src="./js/schemasPopup.js"></script>
    <script type="text/javascript" src="./js/generationStreamPopup.js"></script>
    <script type="text/javascript" src="./js/schemaFieldsTreePopup.js"></script>
    <script type="text/javascript" src="./js/generationStreamFieldsTreePopup.js"></script>
    <script type="text/javascript" src="./js/loadAttributesPopup.js"></script>
    <script type="text/javascript" src="./js/schemaFieldsPopup.js"></script>
    <script type="text/javascript" src="./js/schemaAttributesPopup.js"></script>
    <script type="text/javascript" src="./js/serversContent.js"></script>
    <script type="text/javascript" src="./js/connectionsContent.js"></script>
    <script type="text/javascript" src="./js/jobsContent.js"></script>    
    <script type="text/javascript" src="./js/tasksSchedulerContent.js"></script>    
    <script type="text/javascript" src="./js/validateDatastreamPopup.js"></script>
    <script type="text/javascript" src="./js/validateFilePopup.js"></script>
    <script type="text/javascript" src="./js/datastreamsReceivedPopup.js"></script>
    <script type="text/javascript" src="./js/loadDocumentsPopup.js"></script>
    <script type="text/javascript" src="./js/loadSchemaExtraCheckPopup.js"></script>
    <script type="text/javascript" src="./js/loadTriggerPopup.js"></script>    
    <script type="text/javascript" src="./js/uploadDocSchemaPopup.js"></script>    
    <script type="text/javascript" src="./js/usersContent.js"></script>
    <script type="text/javascript" src="./js/usersPopup.js"></script>
    <script type="text/javascript" src="./js/utils.js"></script>    
    <script type="text/javascript" src="./js/creditsContent.js"></script>
    <script type="text/javascript" src="./js/reportContent.js"></script>
    <script type="text/javascript" src="./js/reportsPopup.js"></script>
    <script type="text/javascript" src="./js/generationStreamSend.js"></script>
    <script type="text/javascript" src="./js/standardPopup.js"></script>
    <script type="text/javascript" src="./js/loginPopup.js"></script>
    <script type="text/javascript" src="./js/profilerContents.js"></script>
	<script type="text/javascript" src="./js/loadingStreamFieldsPopup.js"></script>
    <script type="text/javascript" src="./js/contactUs.js"></script>
    <script type="text/javascript" src="./js/forecastContentPopup.js"></script>
    
    <link rel="stylesheet" type="text/css" href="./css/ItemSelector.css" />
    <script type="text/javascript" src="./ux/Printer.js"></script> 
	<script type="text/javascript" src="./ux/MultiSelect.js"></script> 
	<script type="text/javascript" src="./ux/ItemSelector.js"></script>
	<script type="text/javascript" src="./js/profiler/common/condition.js"></script>
	<script type="text/javascript" src="./js/profiler/common/connection.js"></script>
	<script type="text/javascript" src="./js/profiler/common/home.js"></script>
	<script type="text/javascript" src="./js/profiler/data/data.js"></script>
	

	<script type="text/javascript" src="./js/index.js"></script>
	<script type="text/javascript" src="js/includes.js"></script>	
	<%
		AppInfoBean appInfo = (AppInfoBean) AppContext.getApplicationContext().getBean("appInfoBean");
	%>

</head>

</html>