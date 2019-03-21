<%@ page import="java.util.Hashtable, com.seer.datacruncher.profiler.framework.rdbms.TableRelationInfo" %>
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

<style type='text/css'>
.div_canvas
{
border:2px solid;
height:100px;
width:100px;         
color: gray;
font-size:12px;
} 
</style>
<div>
<%
Hashtable<String,TableRelationInfo > data = (Hashtable<String,TableRelationInfo >)request.getAttribute("relationData");
int index =1;
int position = 0;
int top = 0;
for (String table : data.keySet()) {
	if(index%4 == 0 && index != 0){
		position = position + 110;
		top = 0;
	}
%>

<div class='div_canvas' style="position:absolute; top:<%=top%>px; left:<%=position%>px;"> 
<%
out.println("<b>" +table + "</b><br />");
TableRelationInfo trInfo = data.get(table);
for (String pk : trInfo.pk) {
	if (pk != null) {
		out.println(pk);
	}
	else{
		break;
	}
}
index++;
top = top + 110;
%>
</div>

<%
}
%>
</div>