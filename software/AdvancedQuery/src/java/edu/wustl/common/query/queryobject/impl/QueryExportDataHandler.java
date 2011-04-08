package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.AbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.impl.JoinGraph;

/**
 * This class is responsible for exporting the query results
 * (i.e. displaying results in spreadsheet in denormalized form)
 * @author pooja_tavase
 *
 */
public class QueryExportDataHandler
{
	protected final Map<QueryHeaderData, Integer> entityVsMaxCount =
		new HashMap<QueryHeaderData, Integer>();
	private final EntityInterface rootEntity;
	private final IConstraints constraints;
	private Map<IExpression,BaseAbstractAttributeInterface> expVsAssoc =
		new HashMap<IExpression, BaseAbstractAttributeInterface>();
	private Map<IExpression,BaseAbstractAttributeInterface> tgtExpVsAssoc =
		new HashMap<IExpression, BaseAbstractAttributeInterface>();

	public QueryExportDataHandler(IExpression rootExp, IConstraints constraints)
	{
		if(rootExp == null)
		{
			this.rootEntity = null;
		}
		else
		{
			this.rootEntity = rootExp.getQueryEntity().getDynamicExtensionsEntity();
		}
		this.constraints = constraints;
	}

	public EntityInterface getRootExp()
	{
		return rootEntity;
	}

	/**
	 * @param entityVsAssoc the entityVsAssoc to set
	 */
	public void setExpVsAssoc(Map<IExpression,BaseAbstractAttributeInterface> expVsAssoc)
	{
		this.expVsAssoc = expVsAssoc;
	}

	/**
	 * @return the entityVsAssoc
	 */
	public Map<IExpression,BaseAbstractAttributeInterface> getExpVsAssoc()
	{
		return expVsAssoc;
	}

	public void setTgtExpVsAssoc(
			Map<IExpression, BaseAbstractAttributeInterface> tgtExpVsAssoc)
	{
		this.tgtExpVsAssoc = tgtExpVsAssoc;
	}

	/**
	 * @return the tgtEntityVsAssoc
	 */
	public Map<IExpression, BaseAbstractAttributeInterface> getTgtExpVsAssoc()
	{
		return tgtExpVsAssoc;
	}

	/**
	 * Gets the list of associations & attributes for the passed entity.
	 * @param expression entity
	 * @return finalList finalList
	 */
	private Collection<AbstractAttributeInterface> getAbstractAttributeCollection(
			IExpression expression)
	{
		Collection<AbstractAttributeInterface> finalList = new ArrayList<AbstractAttributeInterface>();
		Collection<AttributeInterface> attributeList =
			expression.getQueryEntity().getDynamicExtensionsEntity().getAllAttributes();
		Collection<AbstractAttributeInterface> associationList = getActualAssociations(expression);
		finalList.addAll(attributeList);
		finalList.addAll(associationList);
		for(IExpression exp : expVsAssoc.keySet())
		{
			if(exp.equals(expression))
			{
				AbstractAttributeInterface assocInterface = (AbstractAttributeInterface)expVsAssoc.get(exp);
				if(assocInterface != null && !finalList.contains(assocInterface))
				{
					finalList.add(assocInterface);
				}
			}
		}
		return finalList;
	}

	/**
	 * Gets only those associations that are present in the formed query.
	 * @param expression expression
	 * @return finalList
	 */
	private List<AbstractAttributeInterface> getActualAssociations(
			IExpression expression)
	{
		List<AbstractAttributeInterface> assocList = new ArrayList<AbstractAttributeInterface>();
		JoinGraph joinGraph = (JoinGraph)constraints.getJoinGraph();
		IIntraModelAssociation association;
		for(IExpression exp : constraints)
		{
			if(joinGraph.containsAssociation(expression, exp))
			{
				association =
					(IIntraModelAssociation)joinGraph.getAssociation(expression, exp);
				if(!assocList.contains(association.getDynamicExtensionsAssociation()))
				{
					assocList.add(association.getDynamicExtensionsAssociation());
				}
			}
		}
		return assocList;
	}

	/**
	 * Returns a list which contains data related only to the entity passed
	 * (This is possible when the query contains an association twice & so there is list of data
	 * for more than one entity corresponding to same association).
	 * @param tempList tempList
	 * @param expression entity
	 * @return newList
	 */
	public List updateTempList(List tempList, IExpression expression)
	{
		List newList = new ArrayList();
		Map<OutputAssociationColumn, Object> newMap;
		Collection<AbstractAttributeInterface> attributeList = getAbstractAttributeCollection(expression);
		for(int cnt=0;cnt<tempList.size();cnt++)
		{
			newMap = new HashMap<OutputAssociationColumn, Object>();
			Map<OutputAssociationColumn, Object> obj =
				(Map<OutputAssociationColumn, Object>) tempList.get(cnt);
			for(OutputAssociationColumn attribute : obj.keySet())
			{
				if(attributeList.contains(attribute.getAbstractAttr()))
				{
					newMap.put(attribute, obj.get(attribute));
				}
			}
			if(!newMap.isEmpty())
			{
				newList.add(newMap);
			}
		}
		return newList;
	}

	/**
	 * Update the map of entity v/s maximum count.
	 * @param tempList tempList
	 * @param queryDataEntity queryDataEntity
	 */
	public void updateMaxRecordCount(List tempList,
			QueryHeaderData queryDataEntity)
	{
		Integer dataListCount = entityVsMaxCount.get(queryDataEntity);
		if (dataListCount == null || tempList.size() > dataListCount)
		{
			dataListCount = tempList.size();
		}
		if(entityVsMaxCount.get(queryDataEntity) != null)
		{
			entityVsMaxCount.remove(dataListCount);
		}
		entityVsMaxCount.put(queryDataEntity, dataListCount);
	}

	/**
	 * Add all the associations (of parents) and then
	 * filter the list to contain only the required attributes and associations.
	 * @param queryHeaderData queryHeaderData
	 * @return attributeList
	 */
	public List<AbstractAttributeInterface> getFinalAttributeList(
			EntityInterface entity)
	{
		List<AbstractAttributeInterface> attributeList =
			(List<AbstractAttributeInterface>) entity.getAllAbstractAttributes();
		List<AbstractAttributeInterface> newAttributeList = new ArrayList<AbstractAttributeInterface>();
		for(AbstractAttributeInterface attribute : attributeList)
		{
			populateNewAttributeList(entity, newAttributeList, attribute);
		}
		for(IExpression exp : expVsAssoc.keySet())
		{
			populateNewAttrList(entity, newAttributeList, exp);
		}
		return newAttributeList;
	}

	/**
	 * Populate the final attribute list by adding all the associations (of parents) and then
	 * filter the list to contain only the required attributes and associations.
	 * @param entity entity
	 * @param newAttributeList  newAttributeList
	 * @param exp expression
	 */
	private void populateNewAttrList(EntityInterface entity,
			List<AbstractAttributeInterface> newAttributeList, IExpression exp) {
		if(exp.getQueryEntity().getDynamicExtensionsEntity().equals(entity))
		{
			AbstractAttributeInterface assocInterface = (AbstractAttributeInterface)expVsAssoc.get(exp);
			if(assocInterface != null && !newAttributeList.contains(assocInterface))
			{
				newAttributeList.add(assocInterface);
			}
		}
	}

	/**
	 * Populate new attribute list.
	 * @param entity entity
	 * @param newAttributeList newAttributeList
	 * @param attribute attribute
	 */
	private void populateNewAttributeList(EntityInterface entity,
			List<AbstractAttributeInterface> newAttributeList,
			AbstractAttributeInterface attribute)
	{
		if(attribute instanceof AssociationInterface)
		{
			if(!((AssociationInterface)attribute).getTargetEntity().getName().
					equals(entity.getName()))
			{
				newAttributeList.add(attribute);
			}
		}
		else
		{
			newAttributeList.add(attribute);
		}
	}

	/**
	 * Get maximum record count for a particular QueryHeaderData object.
	 * @param queryHeaderData queryHeaderData
	 * @return maxCount
	 */
	public Integer getMaxRecordCountForQueryHeader(
			QueryHeaderData queryHeaderData)
	{
		Integer maxCount = entityVsMaxCount.get(queryHeaderData);
		if (maxCount == null)
		{
			maxCount = 0;
		}
		return maxCount;
	}
}
