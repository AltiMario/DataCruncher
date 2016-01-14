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
<%@page import="com.seer.datacruncher.spring.AppContext"%>
<%@page import="com.seer.datacruncher.spring.AppInfoBean"%>
<%@page import="com.seer.datacruncher.utils.generic.I18n"%>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.ParseException" %>
<%
	AppInfoBean appInfo = (AppInfoBean) AppContext.getApplicationContext().getBean("appInfoBean");
%>
<html>
<head>
    <script type="text/javascript">
		function getHtmlMsgForCredits(showCredits) {
			var moduleName = (e instanceof ReferenceError) ? _message['moduleName']
						: 'getHtmlMsgForCredits() :: unknown error';

			return 'SeerDataCruncher <%=appInfo.getAppVersion()%>'
				+ ' ' + moduleName
                <%if (appInfo.getModule() != null) {%> + ' + <%=I18n.getMessage("label.module") + ' '+appInfo.getModule()%>' <%};%>
				<%if (appInfo.getDealer() != null) {%> + '<br/><%=I18n.getMessage("label.marketedBy") + ' '+appInfo.getDealer()%>' <%};%>
				<%if (appInfo.getClient() != null) {%> + '<br/><%=I18n.getMessage("label.for") + ' '+appInfo.getClient()%>' <%};%>
                <%if (appInfo.getValidity() != null) {%> + '<br/><%=I18n.getMessage("label.valid-until" + (appInfo.isExpired() ? "-exp" : "")) + ' '+appInfo.getValidity()%><br/>'<%};%>
                    + (showCredits ? _message['credits'] : '');
		}
    </script>
</head>
<body>
</body>
</html>