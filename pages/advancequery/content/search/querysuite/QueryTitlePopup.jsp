<link href="css/advancequery/inside.css" rel="stylesheet" type="text/css"/>
<script type="text/JavaScript">
function confirmDelete()
{
	parent.pvwindow1.hide();
}

</script>
<body>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0"  class="dynamic_table_bg">
  <tr>
     
        <td height="40" align="center"  style="font-family:Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: #3c3c3c;"><div id="queryTitleDiv"></div> 
	</td>
  </tr>
</table>
<script>
document.getElementById("queryTitleDiv").innerHTML='Query with title "' + parent.window.document.getElementById("sameQueryTitle").value +' " is already present in workflow';
</script>
</body>
