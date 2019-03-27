<%--
  ~ DataCruncher
  ~ Copyright (c) Mario Altimari. All rights reserved.
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
  ~
  --%>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>Form Sample</title>
    <link rel="stylesheet" type="text/css" href="css/bootstrap.css"/>
    <script type="text/javascript">
		var APPLICATION_CONTEXT_URL = '<%= StringUtils.removeEnd(request.getRequestURL().toString(), request.getServletPath()) %>';
    </script>
</head>
<body>
<div style="margin: 0 auto; width: 95%">
    <form></form>
</div>
<div id="res" class="alert"></div>
<script type="text/javascript" src="js/jsonform/jquery.min.js"></script>
<script type="text/javascript" src="js/jsonform/underscore.js"></script>
<script type="text/javascript" src="js/jsonform/jsv.js"></script>
<script type="text/javascript" src="js/jsonform/jsonform.js"></script>
<script type="text/javascript" src="js/jsonform/sample.js"></script>
<script type="text/javascript" src="js/ajaxRequest.js"></script>
<script type="text/javascript">
    makeRequest(requestJsonForm);
</script>
</body>
</html>