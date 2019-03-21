<%--
  ~ Copyright (c) 2019  Altimari Mario
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
<%@page import="com.datacruncher.spring.AppContext"%>
<%@page import="com.datacruncher.spring.AppInfoBean"%>
<%@page import="com.datacruncher.utils.generic.I18n"%>
<%
	AppInfoBean appInfo = (AppInfoBean) AppContext.getApplicationContext().getBean("appInfoBean");
%>
<html>
<head>
    <script type="text/javascript">
		function getHtmlMsgForCredits(showCredits) {
			return 'DataCruncher release <%=appInfo.getAppVersion()%>' + (showCredits ? _message['credits'] : '');
		}
    </script>
</head>
<body>
</body>
</html>