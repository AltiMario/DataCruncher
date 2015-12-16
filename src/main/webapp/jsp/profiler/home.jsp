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

<html>
<head>
<title>Profiler</title>

<link rel="stylesheet" type="text/css"	href="./extjs/css/ext-all.css">
<link rel="stylesheet" type="text/css" href="./css/ItemSelector.css" />
<script type="text/javascript" src="./extjs/ext-base.js"></script>
<script type="text/javascript" src="./extjs/ext-all-debug.js"></script>
<script type="text/javascript" src="./js/profiler/common/include.js"></script>
<script type="text/javascript" src="./js/profiler/common/home.js"></script>
<script type="text/javascript" src="./js/profiler/common/condition.js"></script>
<script type="text/javascript" src="./js/profiler/data/data.js"></script>
<script type="text/javascript" src="./js/profiler/common/connection.js"></script>
<!-- <script type="text/javascript" src="js/common/menu.js"></script> -->
<style type="text/css">
.body-style {
            padding-top:0px;
            text-align:left;
            font-style:bold;
            color: gray;
            font-size:11px;
        }
</style>
</head>
<body>
<script>
var tables = <%=request.getAttribute("tableNames")%>;
var columns = <%=request.getAttribute("columnNames")%>;
</script>
	<!-- <div id="container">
		<div id="toolbar"></div>
	</div> -->
</body>
</html>