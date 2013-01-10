<%@page import="edu.wustl.query.bizlogic.DashboardBizLogic"%><META
	HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
<%-- TagLibs --%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%-- Imports --%>
<%@
	page language="java" contentType="text/html"
	import="edu.wustl.query.util.global.AQConstants,org.apache.struts.Globals"%>
<%@ page
	import="org.apache.struts.action.ActionMessages,edu.wustl.query.util.global.Utility"%>
<%@ page import="edu.wustl.query.util.global.AQConstants"%>
<%@ page import="edu.wustl.query.actionForm.SaveQueryForm"%>
<%@ page
	import="edu.wustl.cab2b.client.ui.query.IClientQueryBuilderInterface,edu.wustl.cab2b.client.ui.query.ClientQueryBuilder,edu.wustl.query.flex.dag.DAGConstant,edu.wustl.common.querysuite.queryobject.IQuery,edu.wustl.common.querysuite.queryobject.impl.Query,edu.wustl.common.querysuite.queryobject.IParameterizedQuery"%>
<%@ page
	import="java.util.*,java.text.DateFormat,java.text.SimpleDateFormat"%>
<%@ page import="edu.wustl.query.beans.DashboardBean"%>
<head>
<script language="JavaScript" type="text/javascript"
	src="jss/advQuery/queryModule.js"></script>
<script type="text/javascript" src="jss/advQuery/wz_tooltip.js"></script>
<script type="text/javascript" src="jss/tag-popup.js"></script>
<link rel="stylesheet" type="text/css"
	href="css/advQuery/styleSheet.css" />
<link rel="stylesheet" type="text/css" href="css/advQuery/tag-popup.css" />
<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/css/dhtmlxgrid.css">
<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/css/dhtmlxtree.css">
<script src="dhtmlx_suite/js/dhtmlxcommon.js"></script>
<link rel="STYLESHEET" type="text/css"
	href="dhtmlx_suite/dhtml_pop/css/dhtmlxgrid_dhx_skyblue.css" />
<script src="dhtmlx_suite/js/dhtmlxgrid.js"></script>
<script src="dhtmlx_suite/js/dhtmlxtree.js"></script>
<script src="dhtmlx_suite/ext/dhtmlxgrid_filter.js"></script>
<link rel="STYLESHEET" type="text/css"
	href=" dhtmlx_suite/ext/dhtmlxgrid_pgn_bricks.css"/>
<script src="dhtmlx_suite/ext/dhtmlxgrid_pgn.js"></script>
<script src="dhtmlx_suite/js/dhtmlxgridcell.js"></script>
<script src="dhtmlx_suite/dhtml_pop/js/dhtmlXTreeGrid.js"></script>  
<script>
 
function QueryWizard()
{
	var rand_no = Math.random();
	document.forms[0].action='QueryWizard.do?random='+rand_no;
	document.forms[0].submit();
}
window.onload = function() {  
	ajaxQueryGridInitCall("QueryGridInitAction.do")
   	doInitGrid();
    }  
function f()
{
	searchDivTag=document.getElementById('searchDiv');
	searchDivTag.style.height = (document.body.clientHeight-105) + 'px';
}
var grid;
function doInitGrid()
{
	grid = new dhtmlXGridObject('mygrid_container');
	grid.setImagePath("dhtmlx_suite/dhtml_pop/imgs/");
 	grid.setHeader("My Folders");
 	grid.setInitWidthsP("100");
 	grid.setColAlign("left");
 	grid.setSkin("dhx_skyblue"); // (xp, mt, gray, light, clear, modern)
 	grid.enableRowsHover(true, "activebtn");
 	grid.setEditable(false);
   	grid.attachEvent("onRowSelect", doOnRowSelected);
 	grid.init();
 	grid.load ("TagGridInItAction.do");
  
}

function doOnRowSelected(rId)
{
	ajaxQueryGridInitCall("QueryGridInitAction.do?tagId="+rId);
	document.getElementById('myQueries').className='btn1';
	document.getElementById('allQueries').className='btn1';
	document.getElementById('sharedQueries').className='btn1';
}
 
var xmlHttpobj = false;
function ajaxQueryGridInitCall(url) {
	
 if (window.XMLHttpRequest)
 { 
  	xmlHttpobj=new XMLHttpRequest();
 }
 else
 { 
  	xmlHttpobj=new ActiveXObject("Microsoft.XMLHTTP");
 }
	  
	xmlHttpobj.onreadystatechange = showGrid;
	xmlHttpobj.open("POST", url,false);
	xmlHttpobj.send(null);
}
var responseString;
var queryGrid;
function showGrid() {
	if (xmlHttpobj.readyState == 4) 
	{
		 responseString =xmlHttpobj.responseText;
		 queryGrid = new dhtmlXGridObject('mygrid_right_container');
		 queryGrid.setImagePath("dhtmlx_suite/dhtml_pop/imgs/");
		 queryGrid.setHeader(",<b>Query Id,<b>Title,<b>Results,<b>ExecutedOn,<b>Owner,<b>Actions ");
		 queryGrid.attachHeader("#rspan,#numeric_filter,#text_filter,#text_filter,#rspan,#select_filter,#rspan"); 
		 queryGrid.setInitWidthsP("3,10,22,20,17,15,*");
		 queryGrid.setColAlign("center,center,left,left,left,left,left");
		 queryGrid.setColTypes("txt,txt,txt,txt,txt,txt,txt");
		 queryGrid.setColSorting("str,int,str,str,str,str");
		 queryGrid.setSkin("dhx_skyblue"); // (xp, mt, gray, light, clear, modern)
		 queryGrid.enableRowsHover(true, "activebtn");
		 queryGrid.setEditable(false);
		 queryGrid.enableTooltips("false,false,false,false,false,false,false");
		 queryGrid.clearAll(true);
		 queryGrid.init();
		 queryGrid.enablePaging(true,20,10,"pagingArea",true);
		 queryGrid.setPagingSkin("bricks");
		 queryGrid.loadXMLString(responseString); 
	}
}

</script>
<body onunload="doInitOnLoad();" onresize='f()'>
	<%
		boolean mac = false;
		Object os = request.getHeader("user-agent");
		if (os != null && os.toString().toLowerCase().indexOf("mac") != -1) {
			mac = true;
		}
		String message = null;
		String entityTag="QueryTag";
		String entityTagItem="QueryTagItem";
		String popupHeader=(String) request.getAttribute(AQConstants.POPUP_HEADER);
		String popupDeleteMessage=(String) request.getAttribute(AQConstants.POPUP_DELETE_QUERY_MESSAGE);
		String popupAssignMessage=(String) request.getAttribute(AQConstants.POPUP_ASSIGN_MESSAGE);
		String popupAssignConditionMessage=(String) request.getAttribute(AQConstants.POPUP_ASSIGN_QMESSAGE);
		String popupFolderDeleteMessage=(String) request.getAttribute(AQConstants. POPUP_DELETE_QUERY_FOLDER_MESSAGE);
		String popupMessage = (String) request
				.getAttribute(AQConstants.POPUP_MESSAGE);
		String popupText = (String) request
				.getAttribute(AQConstants.POPUP_TEXT);
		String queryOption = (String) request.getAttribute("queryOption");
	%>
 

	<html:messages id="messageKey" message="true">
		<%
			message = messageKey;
		%>
	</html:messages>
	<html:form styleId='saveQueryForm'
		action='<%=AQConstants.FETCH_QUERY_ACTION%>'
		style="margin:0;padding:0;">
		<table width='100%' cellpadding='0' cellspacing='0' border='0'
			align='center'>
			<!-- style="width:100%;height:100%;overflow:auto"-->

			<tr valign="center" class="bgImage">
				<td width="50%">&nbsp; <img
					src="images/advQuery/ic_saved_queries.gif" id="savedQueryMenu"
					alt="Saved Queries" width="38" height="48" hspace="5"
					align="absmiddle" /> <span class="savedQueryHeading"> <bean:message
							key="query.savedQueries.label" /> </span></td>

				<td width="1" valign="middle" class="bgImage" align="left"><img
					src="images/advQuery/dot.gif" width="1" height="25" /></td>
			</tr>
			<tr>


				<td class="savedQueryHeading">
					<p>
						<html:errors />
				</td>
				<td>
					<div>
						<%
 						String	organizeTarget = "ajaxTreeGridInitCall('"+popupDeleteMessage+"','"+popupFolderDeleteMessage+"','"+entityTag+"','"+entityTagItem+"')";
 %>
						<input type="button" value="ORGANIZE"
							onclick="<%=organizeTarget%> " title ="Organize"  class="btn"> <input
							type="button" value="CREATE NEW QUERY" title ="Create New Query" onclick="QueryWizard()"
							class="btn2">
				</td>

			</tr>
		</table>
<table width='100%' height='100%' cellpadding='0' cellspacing='0' border='0'
			align='center'>
<tr>
<td>
		<div id="left">
			<table class="tags" width="100%" cellspacing="0" cellpadding="0"
				border="0">
 
				<tbody>
					<tr>
						<td><div style="height: 30px; width: 166px;" id="toolbarObj1">
									<input type="button" value="My Queries" id="myQueries" title ="My Queries"
										onclick="submitTheForm('QueryGridInitAction.do?pageOf=myQueries',this.id);"
										class="activebtn">
							</div>
						</td>
					</tr>
					<tr>
						<td><div style="height: 30px; width: 166px;" id="toolbarObj2">
									<input type="button" value="All Queries" id="allQueries" title="All Queries"
										onclick="submitTheForm('QueryGridInitAction.do?pageOf=allQueries',this.id);"
										class="btn1">
							</div>
						</td>
					</tr>
					<tr>
						<td><div style="height: 30px; width: 166px;" id="toolbarObj3">
									<input type="button" value="Shared Queries" id="sharedQueries" title="Shared Queries"
										onclick="submitTheForm('QueryGridInitAction.do?pageOf=sharedQueries',this.id);"
										class="btn1">
							</div>
						</td>
					</tr>
					<tr>
						<td height=100%><div id="mygrid_container" height="26em" width="97%"></div></td>
					</tr>
				</tbody>
			</table>

		</div>
 
		<div id="wrapper">
			<div id="mainContent">
				<!--POPUP-->
				<div id="blanket" style="display: none;"></div>
				<div id="popUpDiv" style="display: none; top: 100px; left: 210.5px;">

					<a onclick="popup('popUpDiv')"><img style="float: right;"
						height='23' width='24' title="Close" src='images/advQuery/close_button.gif'
						border='0'> </a>
					<table class=" manage tags" width="100%" cellspacing="0"
						cellpadding="5" border="0">

						<tbody>
							<tr valign="center" height="35" bgcolor="#d5e8ff">
								<td width="28%" align="left"
									style="font-size: .82em; font-family: verdana;">
									<p>
										&nbsp&nbsp&nbsp&nbsp<b> <%=popupHeader%></b>
									</p>
								</td>
							</tr>
					</table>


					<div id="treegridbox"
						style="width: 530px; height: 237px; background-color: white;"></div>

					<p>
						&nbsp&nbsp&nbsp<label width="28%" align="left"
							style="font-size: .82em; font-family: verdana;"><b> <%=popupText%>
								: </b> </label> <input type="text" id="newTagName" name="newTagName"
							size="20" onclick="this.value='';" maxlength="50" /><br>
					</p>
					<p>
						<%
 String	assignTarget = "ajaxAssignTagFunctionCall('AssignTagAction.do','"+popupAssignMessage+"','"+popupAssignConditionMessage+"')";
 %>
						<input type="button" value="ASSIGN" title="Assign" onclick="<%=assignTarget%> "
							onkeydown="<%=assignTarget%> " class="btn3">
					</p>
				</div>
			</div>
 

			<div id="right">
				<table width="100%" cellpadding='0' cellspacing='0' border='0'>
						<tr>
							<td>
								<div id="mygrid_right_container" height="31em" width="100%"></div>
								<div id="pagingArea"></div>
							</td>
						</tr>
				</table>
			</div>

		</div>
</td>
</tr> 
</table>
<html:hidden styleId="queryId" property="queryId" />
</html:form>
</body>
