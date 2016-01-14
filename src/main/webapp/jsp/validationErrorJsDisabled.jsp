<%--
  ~   SeerDataCruncher is a Data Quality Firewall, a Data Quality Monitor and an ETL middleware to manage data streams on the fly.
  ~   SeerDataCruncher is released under AGPL license.
  ~   Copyright (c) 2015 foreSEE-Revolution ltd
  ~   All rights reserved
  ~
  ~   Site: http://www.see-r.com
  ~   Contact:  info@see-r.com
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