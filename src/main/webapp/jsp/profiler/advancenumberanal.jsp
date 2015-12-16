<%@ page language="java" session="true" contentType="text/html; charset=iso-8859-1" %>
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

<div id='advancenumanal-grid<%=request.getAttribute("tabId")%>'></div>
<script type="text/javascript">

//Ext.onReady(function() {

Ext.QuickTips.init();
    
    // setup the state provider, all state information will be saved to a cookie
    Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));

    // sample static data for the store
    var myData = <%=request.getAttribute("gridData")%>;

   

    // create the data store
    var store = Ext.create('Ext.data.ArrayStore', {
        fields: <%=request.getAttribute("gridFields")%>,
        data: myData
    });

    // create the Grid
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        id: 'numAnalGrid',	
        stateful: true,
        stateId: 'stateGrid',
        columns: <%=request.getAttribute("gridColumns")%>,
        width: '100%',
        title: '',
        renderTo: 'advancenumanal-grid<%=request.getAttribute("tabId")%>',
        selModel: {
            mode: 'MULTI'
        },
        viewConfig: {
            stripeRows: true
        }
    });    
   
   
//});
</script>