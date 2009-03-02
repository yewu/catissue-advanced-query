
package edu.wustl.query.exportmanager;

import java.util.Map;

/**
 * This is data carrier object which will be passed to the ExportDataThread for the execution of export data. 
 * This object will contain all the required information like instance of CiderQuery object, SessionDataBean etc. 
 * 
 * @author ravindra_jain
 * @version 1.0
 * @created 02-Mar-2009 3:24:31 PM
 */
public class ExportDataObject
{

	private Map exportObjectDetails;

	/**
	 * Default Constructor
	 */
	public ExportDataObject()
	{
		// Default Constructor
	}

}