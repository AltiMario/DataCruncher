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
<%@page pageEncoding="UTF-8"%>
<jsp:include page="jsp/utils.jsp" />
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html">
	<title>SeerDataCruncher</title>
	<link rel="stylesheet" type="text/css" href="./extjs/css/ext-all-neptune.css"/>
	<link href="./images/favicon.ico" rel="shortcut icon" type="image/x-icon" />
    <script type="text/javascript" src="./extjs/ext-all.js"></script>

    <script type="text/javascript">
	    var language = window.navigator.userLanguage || window.navigator.language;
		if (language != 'en' || language != 'it' || language != 'de' || language != 'ru') {
            language = 'en';
        }
	    document.write("<script charset=\"UTF-8\" type=\"text/javascript\" src=\"./extjs/locale/ext-lang-" + language + ".js\"></scr" + "ipt>")
	    document.write("<script charset=\"UTF-8\" type=\"text/javascript\" src=\"./locale/custom-lang-" + language + ".js\"></scr" + "ipt>")
	    
	    var isApplicationConfigured = true;
	    
	    Ext.Ajax.request({
			params: {
				language: language
			},
			url: './language.json',
			failure : function(result) {
				if(result.status == 500) {
					callAlert(_message['dbConfigError']);
				}
			}
		});
	    
	     Ext.Ajax.request( {
			url : './isApplicationConfigured.json',
			success: function (result) {				
				recordResult = Ext.JSON.decode(result.responseText);
				window.setTimeout("loading()" , 3000);
				if(recordResult.success == 'false') {
					isApplicationConfigured = false;					
				}
			},
			failure : function() {
				
			}
	 	}); 
	     
	    if (Ext.BLANK_IMAGE_URL.substr(0 , 5) != 'data:') {
	    	Ext.BLANK_IMAGE_URL = './extjs/images/default/s.gif';
	    };
	    
	    
		function loading() { 
		    Ext.get("loading").fadeOut({duration: 2 , remove: true});
		    if(isApplicationConfigured) {
				popupLogin();
		    } else {
		    	popupDBConfig();
		    }		
		}  
	</script>
	<script type="text/javascript" src="./extjs/ux/App.js"></script>
	<script type="text/javascript" src="./extjs/ux/FileUploadField.js"></script>
    <script type="text/javascript" src="./extjs/ux/ProgressBarPager.js"></script>
	<script type="text/javascript" src="./extjs/ux/RowEditing.js"></script>
    <link rel="stylesheet" type="text/css" href="./extjs/ux/css/fileuploadfield.css"/>
	<link rel="stylesheet" type="text/css" href="./extjs/ux/css/RowEditor.css"/>
	<link rel="stylesheet" type="text/css" href="./css/style.css"/>
    <script type="text/javascript" src="./js/loginPopup.js"></script>
    <script type="text/javascript" src="./js/forgetPasswordPopup.js"></script>
	<script type="text/javascript" src="./js/userRegistrationPopup.js"></script>
    <script type="text/javascript" src="./js/utils.js"></script>
</head>
<body>
	<div id="loading_background"></div>
	<div id="loading">
        <a href='http://www.see-r.com' target='_blank'><img src="./images/logo.png" alt="SeerDataCruncher"></a>
			<p class="copyright">
				<script type="text/javascript">
					document.write(getHtmlMsgForCredits(true));
				</script>
			</p>
	</div>
</body>
</html>