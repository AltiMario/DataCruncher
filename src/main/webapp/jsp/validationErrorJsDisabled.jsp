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

<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>Validation error page</title>
  		<style>
   			table {
    			width: 100%; /* table's width */
    			background: white; /* background color */
    			color: black; /* Font color */
   				border: 1px solid #000;
   				border-collapse: collapse;
   			}
			tr:nth-child(even) {
   				background: #F0F0F0;
			}
   			td {
    			padding: 5px; /* padding */
    			border-left: 1px solid #000;
				border-right: 1px solid #000;
				
   			}
   			th {
    			background: #33CCFF;
    			border: 1px solid #000; 				
   			}
   			span {
   				color: red;
   			}
  		</style>        
    </head>
    <body>
        <p>Your form has following validation errors: <br></p>
        <div> 
        <table> 
        <tr><th>Field name</th><th>Error description</th></tr>
        <c:forEach items="${errMap}" var="entry">
		    <tr><td>${entry.key}</td><td>${entry.value}</td></tr>
		</c:forEach>
		</table>
		</div>
		<noscript>
			<br><br><span>To go back</span>, please click/tap "back" button of your browser.
		</noscript>         
    </body>
</html>