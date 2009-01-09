
package edu.wustl.query.util.global;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Variables extends edu.wustl.common.util.global.Variables
{

	public static int maximumTreeNodeLimit;
	public static Map<String, String> aliasAndPageOfMap = new HashMap<String, String>();
	public static String queryGeneratorClassName = "";
	public static Properties properties;

	public static String prepareColTypes(List dataColl)
	{
		return prepareColTypes(dataColl, false);
	}

	public static String prepareColTypes(List dataColl, boolean createCheckBoxColumn)
	{
		String colType = "";
		if (dataColl != null && !dataColl.isEmpty())
		{
			List rowDataColl = (List) dataColl.get(0);

			Iterator it = rowDataColl.iterator();
			if (createCheckBoxColumn == true)
			{
				colType = "ch,";
			}
			while (it.hasNext())
			{
				Object obj = it.next();
				if (obj != null && obj instanceof Number)
				{
					colType = colType + "int,";
				}
				else if (obj != null && obj instanceof Date)
				{
					colType = colType + "date,";
				}
				else
				{
					colType = colType + "str,";
				}
			}
		}
		if (colType.length() > 0)
		{
			colType = colType.substring(0, colType.length() - 1);
		}
		return colType;
	}
	
	/**
	 * Used for inserting data in batches - Statement.executeBatch()
	 */
	public static int batchSize;

	/**
	 * Query ITABLE
	 */
	public static String ITABLE;
	
	/**
	 * QUERY EXECUTION LOG TABLE
	 */
	public static String EXECUTION_LOG_TABLE;
	
}
