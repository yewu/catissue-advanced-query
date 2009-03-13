package edu.wustl.common.beans;
import java.sql.Timestamp;


public class NodeInfo
{
	Object obj;
	Timestamp dob;
	Long patientDeid;
	
	/**
	 * @return the obj
	 */
	public Object getObj()
	{
		return obj;
	}
	
	/**
	 * @param obj the obj to set
	 */
	public void setObj(Object obj)
	{
		this.obj = obj;
	}
	
	/**
	 * @return the dob
	 */
	public Timestamp getDob()
	{
		return dob;
	}
	
	/**
	 * @param dob the dob to set
	 */
	public void setDob(Timestamp dob)
	{
		this.dob = dob;
	}

	/**
	 * 
	 * @return
	 */
	public Long getPatientDeid()
	{
		return patientDeid;
	}
	
	/**
	 * 
	 * @param patientDeid
	 */
	public void setPatientDeid(Long patientDeid)
	{
		this.patientDeid = patientDeid;
	}
	
}
