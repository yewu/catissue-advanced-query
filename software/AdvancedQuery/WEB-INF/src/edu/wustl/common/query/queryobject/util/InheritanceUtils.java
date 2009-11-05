/**
 * 
 */

package edu.wustl.common.query.queryobject.util;

import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.wustl.cab2b.server.util.InheritanceUtil;
import edu.wustl.common.querysuite.querableobjectInterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectInterface.QueryableObjectInterface;

/**
 * Factory pattern for calling InheritanceUtil class methods.
 * @author prafull_kadam
 */
public final class InheritanceUtils implements InheritanceUtilsInterface
{

	private static InheritanceUtilsInterface instance = new InheritanceUtils();

	/**
	 * constructor for singleton implementation.
	 */
	private InheritanceUtils()
	{
	}

	/**
	 * @return the instance
	 */
	public static InheritanceUtilsInterface getInstance()
	{

		if (instance == null)
		{
			synchronized (instance)
			{
				instance = new InheritanceUtils();
			}
		}

		return instance;
	}

	/**
	 * This method will be used for mocking purpose only.
	 * @param instance the instance to set
	 */
	public static void setInstance(InheritanceUtilsInterface instance)
	{

		InheritanceUtils.instance = instance;
	}

	/**
	 * TO check whether given attribute is inherited or not.
	 * @param abstractAttribute reference to AbstractAttributeInterface
	 * @return true if attribute is inherited.
	 */
	public boolean isInherited(AbstractAttributeInterface abstractAttribute)
	{
		return InheritanceUtil.isInherited(abstractAttribute);
	}

	/**
	 * Returns actual attribute if passed attribute is a derieved one. Else
	 * returns the passed attribute
	 * @param attribute Attribute for which actual attribute is expected.
	 * @return The actual attribute
	 */
	public QueryableAttributeInterface getActualAttribute(QueryableAttributeInterface attribute)
	{
		QueryableObjectInterface parentEntity = attribute.getActualEntity().getParentEntity();
		while (parentEntity != null)
		{
			Collection<QueryableAttributeInterface> attributeCollection = parentEntity
					.getAttributeCollection();
			for (QueryableAttributeInterface att : attributeCollection)
			{
				if (att.getName().equals(attribute.getName()))
				{
					attribute = att;
					break;
				}
			}
			parentEntity = parentEntity.getParentEntity();
		}
		return attribute;
		//		return InheritanceUtil.getActualAttribute(attribute);
	}

	/**
	 * Returns actual association if passed association is a derieved one. Else
	 * returns the passed association
	 * @param association Attribute for which actual association is expected.
	 * @return The actual association
	 */
	public AssociationInterface getActualAassociation(AssociationInterface association)
	{
		return InheritanceUtil.getActualAassociation(association);
	}
}