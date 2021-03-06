/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-advanced-query/LICENSE.txt for details.
 */


package edu.wustl.query.util.global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.entitymanager.EntityManagerConstantsInterface;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.wustl.common.beans.QueryResultObjectData;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.querysuite.queryobject.ICondition;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.LogicalOperator;
import edu.wustl.common.querysuite.queryobject.RelationalOperator;
import edu.wustl.common.querysuite.utils.QueryUtility;
import edu.wustl.common.util.PagenatedResultData;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.global.QuerySessionData;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.query.generator.ColumnValueBean;
import edu.wustl.query.beans.QueryResultObjectDataBean;
import edu.wustl.query.bizlogic.CommonQueryBizLogic;
import edu.wustl.query.bizlogic.QueryOutputSpreadsheetBizLogic;
import edu.wustl.query.executor.AbstractQueryExecutor;
import edu.wustl.query.executor.MysqlQueryExecutor;
import edu.wustl.query.executor.OracleQueryExecutor;
import edu.wustl.query.security.QueryCsmCacheManager;


public class Utility //extends edu.wustl.common.util.Utility
{

	/**
	 * Date pattern.
	 */
	private static String pattern = "MM-dd-yyyy";

	/**
	 * Returns the formatted object compatible to grid format.
	 * @param obj object
	 * @return obj The formatted object
	 */
	public static Object toNewGridFormat(Object obj)
	{
	    obj = edu.wustl.common.util.Utility.toGridFormat(obj);
		if (obj instanceof String)
		{
			String objString = (String) obj;
			StringBuffer tokenedString = new StringBuffer();

			StringTokenizer tokenString = new StringTokenizer(objString, ",");

			while (tokenString.hasMoreTokens())
			{
				tokenedString.append(tokenString.nextToken()).append(' ');
			}
			String gridFormattedStr = new String(tokenedString);
			obj = gridFormattedStr;
		}

		return obj;
	}
	/**
	 * Executes SQL through JDBC and returns the list of records.
	 * @param sql SQL to be fired
	 * @return list list<string>
	 * @throws DAOException DAOException
	 * @throws ClassNotFoundException ClassNotFoundException
	 */
	public static List executeSQL(String sql,LinkedList<ColumnValueBean> columnValueBean) throws DAOException, ClassNotFoundException
	{
		String appName=CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		JDBCDAO jdbcDao = daofactory.getJDBCDAO();

		jdbcDao.openSession(null);
		List list = null;
		if(columnValueBean == null)
		{
			list = jdbcDao.executeQuery(sql);
		}
		else
		{
			list = jdbcDao.executeQuery(sql, null, columnValueBean);
		}
		jdbcDao.closeSession();
		return list;
	}

	/**
	 * Adds the attribute values in the list in sorted order and returns the
	 * list containing the attribute values in proper order.
	 * @param dataType -
	 *            data type of the attribute value
	 * @param value1 -
	 *            first attribute value
	 * @param value2 -
	 *            second attribute value
	 * @return List containing value1 and value2 in sorted order
	 */
	public static ArrayList<String> getAttributeValuesInProperOrder
					(String dataType, String value1,String value2)
	{
		String attributeValue1 = value1;
		String attributeValue2 = value2;
		ArrayList<String> attributeValues = new ArrayList<String>();
		if (dataType.equalsIgnoreCase(EntityManagerConstantsInterface.DATE_ATTRIBUTE_TYPE))
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			try
			{
				Date date1 = dateFormat.parse(value1);
				Date date2 = dateFormat.parse(value2);
				if (date1.after(date2))
				{
					attributeValue1 = value2;
					attributeValue2 = value1;
				}
			}
			catch (ParseException e)
			{
				Logger.out.error("Can not parse the given date in " +
					"getAttributeValuesInProperOrder() method :"+ e.getMessage());
				Logger.out.info(e.getMessage(), e);
			}
		}
		else
		{
			if (dataType.equalsIgnoreCase(EntityManagerConstantsInterface.INTEGER_ATTRIBUTE_TYPE)
			|| dataType.equalsIgnoreCase(EntityManagerConstantsInterface.LONG_ATTRIBUTE_TYPE))
			{
				if (Long.parseLong(value1) > Long.parseLong(value2))
				{
					attributeValue1 = value2;
					attributeValue2 = value1;
				}
			}
			else
			{
				if((dataType.equalsIgnoreCase(EntityManagerConstantsInterface.
				DOUBLE_ATTRIBUTE_TYPE))&& (Double.parseDouble(value1) >
				Double.parseDouble(value2)))
				{
					attributeValue1 = value2;
					attributeValue2 = value1;
				}
			}
		}
		attributeValues.add(attributeValue1);
		attributeValues.add(attributeValue2);
		return attributeValues;
	}

	/**
	 *
	 * @param row List of rows in the grid
	 * @param hyperlinkColMap Hyper Link Column Map
	 * @param index index
	 * @return obj object
	 */
	public static Object toNewGridFormatWithHref(List<String> row,
			Map<Integer, QueryResultObjectData> hyperlinkColMap, int index)
	{
		Object obj = row.get(index);
		if (obj instanceof String)
		{
			obj = toNewGridFormat(obj);
			QueryResultObjectData queryResObjData = hyperlinkColMap.get(index);

			if (queryResObjData != null)// This column is to be shown as
			// hyperlink.
			{
				if (obj == null || obj.equals(""))
				{
					obj = "NA";
				}

				/**
				 * Name : Prafull Bug ID: 4223 Patch ID: 4223_1 Description:
				 * Edit User: password fields empty & error on submitting new
				 * password Added PageOf Attribute as request parameter in the
				 * link.
				 */
				String aliasName = queryResObjData.getAliasName();
				String link = "SimpleSearchEdit.do?"
						+ edu.wustl.common.util.global.Constants.TABLE_ALIAS_NAME +
						"=" + aliasName + "&" + AQConstants.IDENTIFIER + "="
						+ row.get(queryResObjData.getIdentifierColumnId()) + "&"
						+ AQConstants.PAGE_OF + "="
						+ Variables.aliasAndPageOfMap.get(aliasName);
				/**
				 * bug ID: 4225 Patch id: 4225_1 Description : Passing a
				 * different name to the popup window
				 */
				String onclickStr = " onclick=javascript:NewWindow('" + link
						+ "','simpleSearch','800','600','yes') ";
				String hrefTag = "<a class='normalLink' href='#'" +
				onclickStr + ">" + obj + "</a>";
				// String hrefTag = "<a href='"+ link+ "'>"+obj+"</a>";
				obj = hrefTag;
			}
		}
		return obj;
	}

	/**
	 * Replaces the escape characters with the original special characters (i.e. single/double quotes)
	 * @param dataList dataList
	 * @return newList
	 */
	public static List<List<String>> getFormattedOutput(List<List<String>> dataList)
	{
		List<List<String>> newList = new ArrayList<List<String>>();
		List<String> rowList;
		for(int i=0;i<dataList.size();i++)
		{
			rowList = new ArrayList<String>();
			List<String> row = dataList.get(i);
			for(int j=0;j<row.size();j++)
			{
				populateInternalRow(rowList, row, j);
			}
			newList.add(rowList);
		}
		return newList;
	}

	/**
	 * Populates the row.
	 * @param rowList rowList
	 * @param row row
	 * @param j j
	 */
	private static void populateInternalRow(List<String> rowList,
			List<String> row, int j)
	{
		String data = row.get(j);
		if(data != null && (data.contains("&#39") || data.contains("&#34")))
		{
			data = DynamicExtensionsUtility.getUnEscapedStringValue(data);
		}
		rowList.add(data);
	}
	/**
	 * This method creates a comma separated string of numbers representing
	 * column width.
	 * @param columnNames List of column names
	 * @return colWidth Comma separated column widths
	 */
	public static String getColumnWidth(List columnNames)
	{
		String colWidth = getColumnWidth((String) columnNames.get(0));
		StringBuffer columnWidth = new StringBuffer(colWidth);

		for (int col = 1; col < columnNames.size(); col++)
		{
			String columnName = (String) columnNames.get(col);
			columnWidth.append(',').append(getColumnWidth(columnName));
		}
		return columnWidth.toString();
	}

	/**
	 * Get column width of the grid.
	 * @param columnName column name
	 * @return columnWidth column width
	 */
	private static String getColumnWidth(String columnName)
	{
		/*
		 * Patch ID: Bug#3090_31 Description: The first column which is just a
		 * checkbox, used to select the rows, was always given a width of 100.
		 * Now width of 20 is set for the first column. Also, width of 100 was
		 * being applied to each column of the grid, which increasing the total
		 * width of the grid. Now the width of each column is set to 80.
		 */
		String columnWidth = null;
		if ("ID".equals(columnName.trim()))
		{
			columnWidth = "0";
		}
		else if ("".equals(columnName.trim()))
		{
			columnWidth = "20";
		}
		else
		{
			columnWidth = "80";
		}
		return columnWidth;
	}

	/**
	 * Get column width in case of mozilla browser.
	 * @param columnNames column names
	 * @return colWidth column width
	 */
	public static String getColumnWidthP(List columnNames)
	{
		StringBuffer colWidth = new StringBuffer("");

		int size = (columnNames.size()-1);
		float temp = 99.5f;
		for (int col = 0; col <= size; col++)
		{
			if(columnNames.get(0).equals(" "))
			{
				if(col == 0)
				{
					colWidth = colWidth.append("3");
				}
				else
				{
					colWidth = colWidth.append(String.valueOf(temp / (size)));
				}
			}
			else
			{
				colWidth = colWidth.append(String.valueOf(temp / (size)));
			}
			colWidth = colWidth.append(",");
		}
		if(colWidth.lastIndexOf(",")== colWidth.length()-1)
		{
			colWidth.deleteCharAt(colWidth.length()-1);
		}
		return colWidth.toString();
	}

	/**
	 * Get the grid width.
	 * @param columnNames column names
	 * @return colWidth column width
	 */
	public static String getGridWidth(List columnNames)
	{
		StringBuffer colWidth = new StringBuffer("");
		return colWidth.toString();
	}

	/**
	 * limits the title of the saved query to 125 characters to avoid horizontal
	 * scroll bar.
	 * @param title -
	 *            title of the saved query (may be greater than 125 characters)
	 * @return - query title up to 125 characters
	 */

	public static String getQueryTitle(String title)
	{
		String multilineTitle = "";
		if (title.length() <= AQConstants.CHARACTERS_IN_ONE_LINE)
		{
			multilineTitle = title;
		}
		else
		{
			multilineTitle = title.substring(0, AQConstants.CHARACTERS_IN_ONE_LINE) + ".....";
		}
		return multilineTitle;
	}

	/**
	 * returns the entire title to display it in tooltip.
	 * @param title -
	 *            title of the saved query
	 * @return tool tip string
	 */
	public static String getTooltip(String title)
	{
		String tooltip = title.replaceAll("'", AQConstants.SINGLE_QUOTE_ESCAPE_SEQUENCE);
		// escape sequence for '
		return tooltip;
	}

	/**
	 * @param request Object of HttpServletRequest
	 * @param sessionData A data bean that contains information related to user logged in.
	 * @param recordsPerPage To specify records to be displayed per page
	 * @param pageNum page number
	 * @param querySessionData QuerySessionData object
	 * @return paginationDataList paginationDataList
	 * @throws DAOException DAO Exception
	 */
	public static List getPaginationDataList(HttpServletRequest request,
			SessionDataBean sessionData, int recordsPerPage, int pageNum,
			QuerySessionData querySessionData) throws DAOException
	{
		List paginationDataList;
		querySessionData.setRecordsPerPage(recordsPerPage);
		int startIndex = recordsPerPage * (pageNum - 1);
		CommonQueryBizLogic qBizLogic = new CommonQueryBizLogic();
		PagenatedResultData pagenatedResult = qBizLogic.execute(sessionData, querySessionData,
				startIndex);
		paginationDataList = pagenatedResult.getResult();
		String isSimpleSearch = (String) request.getSession().getAttribute(
				AQConstants.IS_SIMPLE_SEARCH);
		if (isSimpleSearch == null || (!isSimpleSearch.equalsIgnoreCase(AQConstants.TRUE)))
		{
			Map<Long, QueryResultObjectDataBean> queryResultObjectDataBeanMap = querySessionData
					.getQueryResultObjectDataMap();
			if (queryResultObjectDataBeanMap != null)
			{
				for (Iterator<Long> beanMapIterator = queryResultObjectDataBeanMap.keySet()
						.iterator(); beanMapIterator.hasNext();)
				{
					Long identifier = beanMapIterator.next();
					QueryResultObjectDataBean bean =
						queryResultObjectDataBeanMap.get(identifier);
					if (bean.isClobeType())
					{
						List<String> columnsList = (List<String>) request.getSession()
								.getAttribute(AQConstants.SPREADSHEET_COLUMN_LIST);
						QueryOutputSpreadsheetBizLogic queryBizLogic =
							new QueryOutputSpreadsheetBizLogic();
						Map<Integer, Integer> fileTypMnEntMap =
							queryBizLogic.updateSpreadSheetColumnList(columnsList,
										queryResultObjectDataBeanMap);
						Map exportMetataDataMap = QueryOutputSpreadsheetBizLogic.
						updateDataList(paginationDataList, fileTypMnEntMap);
						request.getSession().setAttribute(AQConstants.ENTITY_IDS_MAP,
							exportMetataDataMap.get(AQConstants.ENTITY_IDS_MAP));
						request.getSession().setAttribute(AQConstants.EXPORT_DATA_LIST,
							exportMetataDataMap.get(AQConstants.EXPORT_DATA_LIST));
						break;
					}
				}
			}
		}
		return paginationDataList;
	}

	/**
	 * Set the grid data.
	 * @param dataList DataList
	 * @param columnList List of columns
	 * @param request Object of HttpServletRequest
	 */
	public static void setGridData(List dataList, List columnList, HttpServletRequest request)
	{
		request.setAttribute("myData",getmyData(dataList));
		request.setAttribute("columns", getcolumns(columnList));
		boolean isWidthInPercent=false;
		if( columnList.size()<AQConstants.TEN)
		{
			isWidthInPercent=true;
		}
		request.setAttribute("colWidth",getcolWidth(columnList,isWidthInPercent));
		request.setAttribute("isWidthInPercent",isWidthInPercent);
		request.setAttribute("colTypes",getcolTypes(dataList));
		int heightOfGrid = AQConstants.HUNDRED;
		if(dataList!=null)
		{
			int noOfRows = dataList.size();
			heightOfGrid = (noOfRows + AQConstants.TWO) * AQConstants.TWENTY;
			if(heightOfGrid > AQConstants.TWO_HUNDRED_FORTY)
			{
				heightOfGrid = AQConstants.TWO_HUNDRED_THIRTY;
			}
		}
		request.setAttribute("heightOfGrid", heightOfGrid);
		int col = 0;
		int index=0;
		String hiddenColNos="";
		if(columnList!=null)
		{
			for(col=0;col<columnList.size();col++)
			{
				if (columnList.get(col).toString().trim().equals("ID") ||
				columnList.get(col).toString().trim().equals("Status")
				|| columnList.get(col).toString().trim().equals("Site Name")||
				columnList.get(col).toString().trim().equals("Report Collection Date"))
				{
					hiddenColNos=hiddenColNos+"hiddenColumnNumbers["+index+"] = "+col+";";
					index++;
				}
			}
		}
		request.setAttribute("hiddenColumnNumbers", hiddenColNos);
	}

	/**
	 * Get column width.
	 * @param columnList columnList
	 * @param isWidthInPercent isWidthInPercent
	 * @return colWidth The column width
	 */
	public static String getcolWidth(List columnList, boolean isWidthInPercent)
	{
		StringBuffer colWidth= new StringBuffer("\"");
		int col;
		if(columnList!=null)
		{
			String fixedColWidth = null;
			if(isWidthInPercent)
			{
				fixedColWidth = String.valueOf(AQConstants.HUNDRED/columnList.size());
			}
			else
			{
				fixedColWidth="100";
			}
			for(col=0;col<(columnList.size()-1);col++)
			{
				colWidth.append(fixedColWidth).append(',');
			}
			colWidth.append(fixedColWidth);
		}
		colWidth.append('\'');
		return colWidth.toString();
	}

	/**
	 * Get column types.
	 * @param dataList dataList
	 * @return colTypes The column types
	 */
	public static String getcolTypes(List dataList)
	{
		StringBuffer colTypes= new StringBuffer();
		colTypes.append('"');
		colTypes.append(edu.wustl.query.util.global.Variables.prepareColTypes(dataList));
		colTypes.append('"');
		return colTypes.toString();
	}

	/**
	 *
	 * @param dataList DataList
	 * @return myData myData
	 */
	public static String getmyData(List dataList)
	{
		StringBuffer myData = new StringBuffer("[");
		int index=0;
		if(dataList !=null && !dataList.isEmpty())
		{
			for (index=0;index<(dataList.size()-1);index++)
			{
				List row = (List)dataList.get(index);
				int counter;
				myData.append('\'');
				for (counter=0;counter < (row.size()-1);counter++)
				{
					myData.append(Utility.toNewGridFormat(row.get(counter)).toString());
					myData.append(',');
				}
				myData.append(Utility.toNewGridFormat(row.get(counter)).toString());
				myData.append("\",");
			}
			List row = (List)dataList.get(index);
			int rowCtr;
			myData.append('\'');
			for (rowCtr=0;rowCtr < (row.size()-1);rowCtr++)
			{
				myData.append(Utility.toNewGridFormat(row.get(rowCtr)).toString());
				myData.append(',');
			}
			myData.append(Utility.toNewGridFormat(row.get(rowCtr)).toString());
			myData.append('\'');
		}
		myData.append(']');
		return myData.toString();
	}

	/**
	 * Returns the columns separated by comma.
	 * @param columnList List of columns
	 * @return columns Columns
	 */
	public static String getcolumns(List columnList)
	{
		StringBuffer columns= new StringBuffer("\"");
		int col;
		if(columnList!=null)
		{
			for(col=0;col<(columnList.size()-1);col++)
			{
				columns.append(columnList.get(col));
				columns.append(',');
			}
			columns.append(columnList.get(col));
		}
		columns.append('\'');
		return columns.toString();
	}
	/** Added By Rukhsana
     * Added list of objects on which read denied has to be checked while filtration of result
     * for csm-query performance.
     * A map that contains entity name as key and sql to get Main_Protocol_Object
     * (Collection protocol, Clinical Study) Ids for that entity id as value for csm-query performance.
     * Reading the above values from a properties file to make query module application independent
     */
    public static void setReadDeniedAndEntitySqlMap()
    {
        List<String> readDeniedObjList = new ArrayList<String>();
        Map<String,String> entityCSSqlMap = new HashMap<String, String>();
        String mainPrtclClassNm="";
        String validatorClassNm ="";
        String mainProtocolQuery="";
        String appName = CommonServiceLocator.getInstance().getAppHome();
        File file = new File(appName+ System.getProperty("file.separator")+"WEB-INF"+
        		System.getProperty("file.separator")+"classes"+System.getProperty("file.separator")
        		+edu.wustl.security.global.Constants.CSM_PROPERTY_FILE);
        if(file.exists())
        {
           Properties csmPropertyFile = new Properties();
           try
           {
                csmPropertyFile.load(new FileInputStream(file));
                mainPrtclClassNm = csmPropertyFile.getProperty
                (edu.wustl.security.global.Constants.MAIN_PROTOCOL_OBJECT);
                validatorClassNm = csmPropertyFile.getProperty
                (edu.wustl.security.global.Constants.VALIDATOR_CLASSNAME);
                mainProtocolQuery = csmPropertyFile.getProperty
                (AQConstants.MAIN_PROTOCOL_QUERY);
                String readdenied = csmPropertyFile.getProperty
                (edu.wustl.security.global.Constants.READ_DENIED_OBJECTS);
                String [] readDeniedObjects=readdenied.split(",");
                for(int i=0;i<readDeniedObjects.length;i++)
                {
                      readDeniedObjList.add(readDeniedObjects[i]);
                      if(csmPropertyFile.getProperty(readDeniedObjects[i])!=null)
                      {
                          entityCSSqlMap.put(readDeniedObjects[i],csmPropertyFile.getProperty
                        		  (readDeniedObjects[i]));
                      }
                }
            }
            catch (FileNotFoundException e)
            {
                Logger.out.debug("csm.properties not found");
                Logger.out.info(e.getMessage(), e);
            }
            catch (IOException e)
            {
                Logger.out.debug("Exception occured while reading csm.properties");
                Logger.out.info(e.getMessage(), e);
            }
           edu.wustl.query.util.global.Variables.mainProtocolObject = mainPrtclClassNm;
           edu.wustl.query.util.global.Variables.queryReadDeniedObjectList.addAll(readDeniedObjList);
           edu.wustl.query.util.global.Variables.entityCPSqlMap.putAll(entityCSSqlMap);
           edu.wustl.query.util.global.Variables.validatorClassname = validatorClassNm;
           edu.wustl.query.util.global.Variables.mainProtocolQuery = mainProtocolQuery;
        }
    }

    /**
     * @param objName Object name
     * @param identifier Identifier
     * @param sessionDataBean A data bean that contains information related to user logged in
     * @return cpIdsList List of CollectionProtocol ids
     */
	  public static List getCPIdsList(String objName, Long identifier, SessionDataBean sessionDataBean)
	    {
	        List cpIdsList = new ArrayList();
	        if (objName != null && !objName.equalsIgnoreCase
	        		(edu.wustl.query.util.global.Variables.mainProtocolObject))
	        {
	            String cpQuery = QueryCsmCacheManager.getQueryStringForCP
	            (objName, Integer.valueOf(identifier.toString()));
	            JDBCDAO jdbcDao = null;

	            String appName=CommonServiceLocator.getInstance().getAppName();
	            IDAOFactory daofactory = DAOConfigFactory.getInstance().getDAOFactory(appName);

	            try
	            {
	                jdbcDao = daofactory.getJDBCDAO();
	                jdbcDao.openSession(sessionDataBean);

	                List<Object> list = null;
	                list = jdbcDao.executeQuery(cpQuery);
	                if (list != null && !list.isEmpty())
	                {
	                    for(Object obj : list)
	                    {
	                        List list1 = (List)obj;
	                        cpIdsList.add(Long.valueOf(list1.get(0).toString()));
	                    }
	                }
	            }
	            catch (Exception e)
	            {
	                return null;
	            }
	            finally
	            {
	                try
	                {
	                    jdbcDao.closeSession();
	                }
	                catch (DAOException e)
	                {
	                    e.printStackTrace();
	                }
	            }
	        }
	        else
	        {
	            cpIdsList.add(identifier);
	        }
	        return cpIdsList;
	    }
	  /**
	     * To check whether there is condition on identifier field or not.
	     * @param query the reference to the Query Object.
	     * @return true if there is any condition on the Identified attribute, else returns false.
	     */
	    public static boolean isConditionOnIdentifiedField(IQuery query)
	    {
	    	boolean isConditionOnIdentifiedField = false;
	        Map<IExpression, Collection<ICondition>> allSelectedConditions = QueryUtility
	                .getAllSelectedConditions(query);
	        Collection<Collection<ICondition>> values = allSelectedConditions.values();
	        Boolean trueValue = Boolean.TRUE;
	        for (Collection<ICondition> conditions : values)
	        {
	            for (ICondition condition : conditions)
	            {
	                Boolean isConditionOnIdentifiedAttribute = condition.getAttribute().getIsIdentified();

	                if (trueValue.equals(isConditionOnIdentifiedAttribute))
	                {
	                	isConditionOnIdentifiedField = true;
	                	break;
	                }
	            }
	            if(isConditionOnIdentifiedField)
	            {
	            	break;
	            }
	        }
	        return isConditionOnIdentifiedField;
	    }

	    /**
	     * Returns the Query Executor depending upon the database (MySql/Oracle).
	     * @return queryExecutor The queryExecutor
	     */
	    public static AbstractQueryExecutor getQueryExecutor()
	    {
	        AbstractQueryExecutor queryExecutor = null;

	        String appName = CommonServiceLocator.getInstance().getAppName();
	        IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
	        if("MYSQL".equalsIgnoreCase(daoFactory.getDataBaseType()))
	        {
	            queryExecutor = new MysqlQueryExecutor();
	        }
	        else
	        {
	            queryExecutor = new OracleQueryExecutor();
	        }
	        return queryExecutor;
	    }
	    /**
	     * Forms display name for attribute as className : attribute name.
	     * @param attribute AttributeInterface
	     * @return columnDisplayName
	     */
	    public static String getDisplayNameForColumn(AttributeInterface attribute)
	    {
	        StringBuffer columnDisplayName = new StringBuffer();
	        String className = parseClassName(attribute.getEntity().getName());
	        String attributeLabel = edu.wustl.common.util.Utility.getDisplayLabel(attribute.getName());
	        columnDisplayName.append(className).append(" : ").append(attributeLabel);
	        return columnDisplayName.toString();
	    }

	    /**
	     * @param fullyQualifiedName Fully Qualified Name of the class.
	     * @return fullyQualifiedName The parsed class name
	     */
	    public static String parseClassName(String fullyQualifiedName)
	    {
	    	String className = fullyQualifiedName;
	        try
	        {
	            className = fullyQualifiedName.substring(fullyQualifiedName
	                    .lastIndexOf('.') + 1);
	        }
	        catch (Exception e)
	        {
	        	Logger.out.error("Error in parsing the class name");
	        }
	        return className;
	    }
	    /**
	     * @param entity entity for which Primary Key list is required
	     * @return primaryKeyList primary key list
	     */
	    public static List<String> getPrimaryKey(EntityInterface entity)
	    {
	        Collection<TaggedValueInterface> taggedValueCollection = entity.getTaggedValueCollection();

	        List<String> primaryKeyList = new ArrayList<String>();
	        for (TaggedValueInterface tag : taggedValueCollection)
	        {
	            if (AQConstants.PRIMARY_KEY_TAG_NAME.equals(tag.getKey()))
	            {
	                StringTokenizer stringTokenizer = new StringTokenizer(tag.getValue(), ",");
	                while(stringTokenizer.hasMoreTokens())
	                {
	                    primaryKeyList.add(stringTokenizer.nextToken());
	                }
	            }
	        }
	        return primaryKeyList;
	    }

	    /**
	     * Method to generate SQL for Node.
	     * @param parentNodeId parent node id which contains data
	     * @param tableName temporary table name
	     * @param parentIdColumnName parent node ID column names (, separated)
	     * @param selectSql SQL
	     * @param idColumnOfCurrentNode id column name (, separated) of current node
	     * @return SQL for current node
	     */
	    public static String getSQLForNode(String parentNodeId, String tableName,
	            String parentIdColumnName, String selectSql, String idColumnOfCurrentNode)
	    {
	        selectSql = selectSql + AQConstants.FROM + tableName;
	        if (parentNodeId != null)
	        {
	            selectSql = selectSql + AQConstants.WHERE + " (";
	            StringTokenizer stringTokenizerParentID = new StringTokenizer(parentIdColumnName, ",");
	            StringTokenizer stringTokenizerParentNodeID = new StringTokenizer(parentNodeId,
	                    AQConstants.PRIMARY_KEY_ATTRIBUTE_SEPARATOR);
	            while (stringTokenizerParentID.hasMoreElements())
	            {
	                selectSql = selectSql + stringTokenizerParentID.nextElement() + " = '"
	                        + stringTokenizerParentNodeID.nextElement() + "' " + LogicalOperator.And
	                        + " ";
	            }
	            StringTokenizer stringTokenizerIDofCurrentNode = new StringTokenizer(
	                    idColumnOfCurrentNode, ",");
	            while (stringTokenizerIDofCurrentNode.hasMoreElements())
	            {
	                selectSql = selectSql + " " + stringTokenizerIDofCurrentNode.nextElement() + " "
	                        + RelationalOperator.getSQL(RelationalOperator.IsNotNull) + " "
	                        + LogicalOperator.And;
	            }
	            if (selectSql.substring(selectSql.length() - AQConstants.THREE).equals
	            		(LogicalOperator.And.toString()))
	            {
	                selectSql = selectSql.substring(0, selectSql.length() - AQConstants.THREE);
	            }
	            selectSql = selectSql + ")";
	        }
	        return selectSql;
	    }


	/**
	 * @param feature feature
	 * @return <CODE>true</CODE> feature is used,
	 * <CODE>false</CODE> otherwise
	 */
	public static boolean checkFeatureUsage(String feature)
	{
		ResourceBundle appResources = ResourceBundle.getBundle("ApplicationResources");
		String isFeatureUsed = appResources.getString(feature);

		boolean hasUsage = false;

		if (isFeatureUsed == null || "".equals(isFeatureUsed) || !"false".equals(isFeatureUsed))
		{
			hasUsage = true;
		}
		return hasUsage;
	}
}