<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="edu.wustl.query.util.global.CompositeQueryOperations"%>

<script language="JavaScript" type="text/javascript" src="dhtml_comp/js/dhtmlwindow.js"></script>
<script language="JavaScript" type="text/javascript" src="dhtml_comp/js/modal.js"></script>
<script language="JavaScript" type="text/javascript" src="jss/advancequery/newReleaseMsg.js"></script>
<link rel="stylesheet" type="text/css" href="css/advancequery/catissue_suite.css" />
<link href="css/advancequery/inside.css" rel="stylesheet" type="text/css" />
<script  src="jss/advancequery/workflows.js"></script>
<script src="jss/ajax.js"></script>	
<script type="text/JavaScript">


function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function showPopUp(pageOf)
{
	var url='QueryAction.do?pageOf='+pageOf+'&queryId=queryId&queryTitle=queryTitle&queryType=queryType';
	pvwindow=dhtmlmodal.open('Select queries', 'iframe', url,'Select queries', 'width=930px,height=400px,center=1,resize=0,scrolling=1');
}

function updateUI()
{
	addQuery();
}

function unionQueries()
{
	addCQToList("<%=CompositeQueryOperations.UNION.getOperation()%>");
}

function intersectQueries()
{
	addCQToList("<%=CompositeQueryOperations.INTERSECTION.getOperation()%>");
}

function minusQueries()
{
	addCQToList("<%=CompositeQueryOperations.MINUS.getOperation()%>");
}

function addCQToList(operation)
{
	var queryIdsToAdd='';
	var queryCount=0;
	queryCount=document.getElementById("table1").rows.length;
	var selectedQueryCount=0;
	for(var counter=0;counter<queryCount;counter++)
	{
		var checkboxControl=document.getElementById("checkbox_"+(counter));
		if(checkboxControl!=null && checkboxControl!=undefined && checkboxControl.checked==true)
		{
			queryIdsToAdd=queryIdsToAdd+","+counter;
			selectedQueryCount=selectedQueryCount+1;
		}
	}


	if(queryIdsToAdd!=""&& selectedQueryCount>=2)
	{
		createCQ(queryIdsToAdd,operation,queryCount);
	}		
}

function createCQ(queryIdsToAdd,operation,queryCount)
{
	var queryIds=queryIdsToAdd.split(",");
	var operandsTdContent="";
	var cqTitle="";
	var cqQueryId="";
	var operandsCounter=0;
	for(var counter=0;counter<queryIds.length;counter++)
	{
		if(queryIds[counter]!=null && queryIds[counter]!='')
		{
			if( cqTitle=='')
			{
				cqTitle="[Query : "+document.getElementById("selectedqueryId_"+queryIds[counter]).value+"]";
			}
			else
			{
				cqTitle=cqTitle+"  "+operation+"  "+ "[Query : "+document.getElementById("selectedqueryId_"+queryIds[counter]).value+"] ";
			}
			if(cqQueryId=='')
			{
				cqQueryId="("+document.getElementById("selectedqueryId_"+queryIds[counter]).value+")";
				operandsTdContent=document.getElementById("selectedqueryId_"+queryIds[counter]).value;
			}
			else
			{
				cqQueryId=cqQueryId+"_"+operation+"_("+document.getElementById("selectedqueryId_"+queryIds[counter]).value+")";
				operandsTdContent=operandsTdContent+"_"+document.getElementById("selectedqueryId_"+queryIds[counter]).value;
			}
			operandsCounter=operandsCounter+1;
		}
	}
	var cqType="Operation";
	var cqId="";
	
	var rowContents=new Array(7);
	rowContents[0]=createCheckBox("chkbox","checkbox_"+queryCount,'');
	rowContents[1]=createTextElement(cqTitle);
	rowContents[2]=createTextElement(cqType);
	//rowContents[3]=createTextElement(operandsTdContent);
	//rowContents[3]=getSelectObjectControl();
	rowContents[4]=createHiddenElement("selectedqueryId","selectedqueryId_"+queryCount,operandsTdContent);
	rowContents[5]=cqTitle;
	rowContents[6]=cqType;
	
	var operatorsTdContent=operation;
	//uncommented for the underscore separated operation string 
	for(var counter=0;counter<operandsCounter-2;counter++)
	{
		operatorsTdContent=operatorsTdContent+"_"+operation;
	}
	//create a table containing tbody with id "table1"
	addRowToTable("table1",rowContents,operandsTdContent,operatorsTdContent);	
}

function submitWorflow()
{
	document.forms[0].submit();
}

function executeGetCountQuery(queryId)
{
	var url="WorkflowAjaxHandler.do?operation=execute&queryId="+queryId;
	var request=newXMLHTTPReq();
	if(request == null)
	{
		alert ("Your browser does not support AJAX!");
		return;
	}
	var handlerFunction = getReadyStateHandler(request,responseHandler,true); 
	request.onreadystatechange = handlerFunction; 
	request.open("POST",url,true);    
	request.setRequestHeader("Content-Type","application/x-www-form-urlencoded");	
	request.send("");
}

function responseHandler(response)
{
	  var jsonResponse = eval('('+ response+')');
          var hasValue = false;
          if(jsonResponse.executionQueryResults!=null)
          {
             var num = jsonResponse.executionQueryResults.length; 
				for(var i=0;i<num;i++)
			  {
					 var queryId = jsonResponse.executionQueryResults[i].queryId;
					 var queryIndex = jsonResponse.executionQueryResults[i].queryIndex;
					 var queryResult = jsonResponse.executionQueryResults[i].queryResult;
					if(queryResult!=-1)
					{
						var object=document.getElementById("selectedqueryId_"+queryIndex);
						var parentIObj=object.parentNode;
						parentIObj.appendChild(createTextElement(queryResult));
					}
				}
          } 
}

//-->
</script>
<script type="text/javascript">
<!--
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);

function MM_openBrWindow(theURL,winName,features) { //v2.0
  window.open(theURL,winName,features);
}
//-->
</script>
<script type="text/javascript" src="wz_tooltip.js"></script>

<body >
<%@ include file="/pages/advancequery/common/ActionErrors.jsp" %>

<html:form action="SaveWorkflow">

<html:hidden property="operation" styleId="operation" value="${requestScope.operation}"/>
<html:hidden property="id" styleId="id" value="${requestScope.id}"/>

<select name="queryId" id="queryId" style="display:none">
								</select>
								<select name="queryTitle" id="queryTitle" style="display:none">
								</select>
								<select name="queryType" id="queryType" style="display:none">
								</select>
								 <input type="button" name="btn" id="btn" onclick="updateUI()" style="display:none">

<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
<tr>
	<td  style="padding:0 10px;">
<table width="98%" border="0" align="center" cellpadding="0" cellspacing="0"  class="login_box_bg">
<tr>
	<td height="28" background="images/advancequery/bg_content_header.gif"><img src="images/advancequery/t_new_workflow.gif" alt="New Workflow" width="124" height="26" hspace="5" vspace="0">
	</td>
</tr>
<tr>
<td  style="padding:0 7px 7px 7px;">
<table width="100%" border="0" cellspacing="0" cellpadding="2">
	<tr>
		<td height="25"><span class="red_star">*</span> <span class="small_txt_grey">Denotes mandatory fields</span></td>
	</tr>
	<tr>
		<td style="padding-bottom:10px;">
			<span class="content_txt_bold"><bean:message key="workflow.name"/></span><span class="red_star">*</span>:<span class="content_txt">
			<html:text styleId="name" property="name" styleClass="textfield_undefined" size="80"/>
			 &nbsp;&nbsp;</span>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0"  class="login_box_bg">
	<tr>
	<td>
		<table width="100%" border="0" cellspacing="0" cellpadding="4">
			<tr class="td_subtitle">
				<td height="25" class="blue_title">Queries</td>
			</tr>
		</table>
	</td>
	</tr>
	<tr>
	<td>
	<table  width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="160px" height="240px" class="tr_color_lgrey" style="border-right:1px solid #dddddd;" valign="top">
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td height="5" colspan="3" valign="middle">
						</td>
					 </tr>
					 <tr>
						<td valign="middle">&nbsp;
						</td>
						<td colspan="2" valign="bottom" class="blue_title">Add Existing Query
						</td>
					</tr>
					 <tr>
					   <td valign="middle">&nbsp;</td>
					   <td valign="middle"><img src="images/advancequery/ic_arrow_small.gif" alt=""  align="absmiddle"></td>
					<td valign="middle"><a href="#" class="blacklink" onClick="showPopUp('myQueriesForWorkFlow')"><bean:message
						key="query.myqueries" /></a></td>
					 </tr>
					<tr>
						<td valign="middle">&nbsp;</td>
						<td valign="middle"><img src="images/advancequery/ic_arrow_small.gif" alt=""  align="absmiddle"></td>
						<td valign="middle"><a href="#" class="blacklink" onClick="showPopUp('publicQueryForWorkFlow')"><bean:message key="query.sharedqueries"/></a></td>
					</tr>
		  
					<tr>
						 <td valign="middle">&nbsp;</td>
						<td colspan="2" valign="bottom" class="blue_title">&nbsp;</td>
					 </tr>
					<tr>
						 <td width="12" valign="middle">&nbsp;</td>
						<td colspan="2" valign="bottom" class="blue_title">Add New Query</td>
					</tr>
					<tr>
						<td width="12" valign="middle">&nbsp;</td>
						<td width="10" valign="middle"><img src="images/advancequery/ic_arrow_small.gif" alt=""  align="absmiddle"></td>
						 <td valign="middle"><a href="javascript:showNextReleaseMsg()" class="blacklink"><bean:message key="query.getcount"/></a></td>
					</tr>
					<tr>
						<td width="12" valign="middle">&nbsp;</td>
						<td width="10" valign="middle"><img src="images/advancequery/ic_arrow_small.gif" alt=""  align="absmiddle"></td>
						<td valign="middle"><a href="javascript:showNextReleaseMsg()" class="blacklink"><bean:message key="query.getpatientdata"/></a></td>
					</tr>
					<tr>
						<td valign="middle">&nbsp;</td>
						<td colspan="2" valign="bottom" class="blue_title">&nbsp;</td>
					</tr>
				</table>
			</td>

			<td valign="top">
				<table width="100%" border="0" cellpadding="3" cellspacing="0">
				<tr>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
							  <td align="left" width="70"><a href="javascript:unionQueries()"><img src="images/advancequery/b_union-copy.gif" alt="Union" width="60" height="23" border="0">
							  </a></td>
							  <td width="106" align="left"><a href="javascript:intersectQueries()"><img src="images/advancequery/b_intersection.gif" alt="Intersection" width="96" height="23" border="0">
							  </a></td>
							  <td width="73" align="left"><a href="javascript:minusQueries()"><img src="images/advancequery/b_minus.gif" alt="Minus" width="63" height="23" border="0">
							  </a></td>
							</tr>
						</table>
					</td>

					<td align="right">
						<table border="0" cellpadding="4" cellspacing="0">
                          <tr>
                            <td align="right"><span class="content_txt_bold"><bean:message  key="workflow.project"/>
                              <select name="select2" class="texttype" disabled="true">
                                    <option>Select..</option>
                                  </select>
                            </span></td>
                            <td width="167" align="right" valign="middle" ><a href="javascript:showNextReleaseMsg()" class="bluelink"><bean:message key="workflow.runworkflow"/></a>&nbsp;</td>
                          </tr>
                        </table>
					</td>
				</tr>
				</table>

				<table width="100%" border="0" cellpadding="2" cellspacing="0">
				<tr>
				<td>
					<table width="100%" border="0" cellpadding="2" cellspacing="1"  bgcolor="#EAEAEA">
					  <tr class="td_bgcolor_grey">
							<td width="10" height="25" valign="middle" ><input type="checkbox" name="checkbox8" value="checkbox">                                    </td>

							<td valign="middle" class="grid_header_text"><bean:message key="workflow.queryTitle"/></td>
							<td width="111" valign="middle" class="grid_header_text"><bean:message key="workflow.type"/></td>
						
							<td width="100" valign="middle" class="grid_header_text"><bean:message key="workflow.patientcount"/> </td>
							<td width="90" valign="middle" class="grid_header_text">&nbsp;</td>
							<td width="55" valign="middle" class="grid_header_text"><bean:message key="workflow.reorder"/></td>
					  </tr>
						   <tbody id="table1">
						   <logic:notEmpty name="workflowForm" property="selectedqueryId">
						   			<logic:iterate id="singleQueryId" name="workflowForm" property="selectedqueryId" indexId="queryIndex" >

									<tr bgcolor="#ffffff" styleClass="td_bgcolor_white">
						   				<td styleClass="content_txt">
						   					<c:set var="chkId">chk_<c:out value="${queryIndex}"/></c:set>
						   					<html:checkbox property="chkbox" styleId="checkbox_${queryIndex}"></html:checkbox>
						   				</td>
  										<td styleClass="content_txt">
						   					<html:hidden property="displayQueryTitle" styleId="displayQueryTitle_${queryIndex}" value="${workflowForm.displayQueryTitle[queryIndex]}"
						   					/>
											${workflowForm.displayQueryTitle[queryIndex]}
						   				</td>
										<td styleClass="content_txt">
											<html:hidden property="queryTypeControl" styleId="queryTypeControl_${queryIndex}" value="${workflowForm.queryTypeControl[queryIndex]}"/>
											${workflowForm.displayQueryType[queryIndex]}
										</td>


										<td styleClass="content_txt">
											<html:hidden property="selectedqueryId" styleId="selectedqueryId_${queryIndex}" value="${workflowForm.selectedqueryId[queryIndex]}"/>
										</td>
										<td>
											<html:hidden property="operands" styleId="operands_${queryIndex}"  value="${workflowForm.operands[queryIndex]}"/>
											<html:hidden property="operators" styleId="operators_${queryIndex}" value="${workflowForm.operators[queryIndex]}"/>
											<html:hidden property="displayQueryType" styleId="displayQueryType_${queryIndex}" value="${workflowForm.displayQueryType[queryIndex]}"/>
											<html:link styleId="execute_${queryIndex}" href="javascript:executeGetCountQuery('${queryIndex}','1')" styleClass="bluelink">
												Execute
											</html:link>
											&nbsp;
											<html:link styleId="delete_${queryIndex}" href="javascript:deleteWorkflowItem(${queryIndex})" styleClass="bluelink">
												Delete
											</html:link>
										</td>

									<td  align="center"><table border="0" cellspacing="0" cellpadding="0">
                                      <tr>
                                        <td><img src="images/advancequery/ic_up.gif" onMouseOut="UnTip()"></td>
                                        <td width="10">&nbsp;</td>
                                        <td><img src="images/advancequery/ic_down.gif" onMouseOut="UnTip()"></td>
                                      </tr>
                                    </table></td>
						   			</tr>
									</logic:iterate>
								</logic:notEmpty>
						   </tbody>
                    </table>
				</td>
				</tr>
				</table>
			</td>
		</tr>
</table>
</td>
</tr>
</table>
<table border="0" cellspacing="0" cellpadding="2" style="margin-top:7px;">
	<tr>
		<td align="left" width="65" valign="top"><a href="javascript:submitWorflow()"><img src="images/advancequery/b_save.gif" alt="Save" width="55" height="23" border="0"></a></td>
		<td width="76" align="left" valign="top"><img src="images/advancequery/b_cancel.gif" alt="Cancel" width="66" height="23"></td>
	</tr>
</table>

</td>
</tr>
</table>

</td>
</tr>
</table>
</html:form>
</body>