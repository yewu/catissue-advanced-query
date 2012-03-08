package edu.wustl.common.query.queryobject.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.wustl.common.query.queryobject.impl.RecordProcessor.TreeCell;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.query.queryobject.impl.metadata.SelectedColumnsMetadata;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.query.bizlogic.RowProcessor;
import edu.wustl.query.util.global.Utility;

public class AssociationDataHandler
{
	/**
	 * This method will update the Maps needed for generating the Header
	 * & data list.
	 * @param denormalizedLst denormalizationMap
	 * @param rootExp rootExp
	 * @param dataHandler dataHandler
	 * @param mapIndex mapIndex
	 */
	public Table<TreeCell> updateRowDataList(final List<Map<OutputAssociationColumn,Object>> denormalizedLst, final IExpression rootExp, QueryExportDataHandler dataHandler)
	{
		final QueryHeaderData queryDataEntity = new QueryHeaderData(rootExp.getQueryEntity().getDynamicExtensionsEntity(), rootExp);
		final List<RecordProcessorNode<TreeCell>> nodes =
			generateQueryDatamap(denormalizedLst, queryDataEntity, null, dataHandler);
		final RowAppenderProcNode<TreeCell> root = new RowAppenderProcNode<TreeCell>();
        root.addChildren(nodes);
        return root.getTable(TreeCell.EMPTY_CELL);
	}

	/**
	 * This method will update the maps required for exporting the data
	 * (i.e. the data to be shown in the spreadsheet).
	 * @param denormalizedList denormalizedList
	 * @param queryDataEntity queryDataCont
	 * @param dataHandler dataHandler
	 * @param entityVsDataList entityVsDataList
	 * @param dataCnt dataCnt
	 */
	private List<RecordProcessorNode<TreeCell>> generateQueryDatamap(
			final List<Map<OutputAssociationColumn, Object>> denormalizedList,
			final QueryHeaderData queryDataEntity, final TreeCell parent, QueryExportDataHandler dataHandler)
	{
        final List<RecordProcessorNode<TreeCell>> res = new ArrayList<RecordProcessorNode<TreeCell>>();
        for (Map<OutputAssociationColumn, Object> rec : denormalizedList)
        {
        	final TreeCell cell = new TreeCell(queryDataEntity, rec);
            cell.setParentCell(parent);
            final RecordProcessorNode<TreeCell> recordProcNode = new RecordProcessorNode<TreeCell>(cell);
            res.add(recordProcNode);

            for (Map.Entry<OutputAssociationColumn, Object> entry : rec.entrySet())
            {
                if (entry.getKey().getAbstractAttr() instanceof AssociationInterface)
                {
                    processAssociations(cell, recordProcNode, entry, dataHandler);
                }
            }
        }
        return res;
	}

	/**
	 * To process the association attribute i.e. retrieves the inner map and
	 * gives call to generateQueryDatamap(...) method.
	 * @param cell cell
	 * @param recordProcNode recordProcNode
	 * @param entry entry
	 * @param dataHandler dataHandler
	 */
	private void processAssociations(final TreeCell cell,
			final RecordProcessorNode<TreeCell> recordProcNode,
			final Map.Entry<OutputAssociationColumn, Object> entry, QueryExportDataHandler dataHandler)
	{
		final AssociationInterface association = (AssociationInterface) entry.getKey().getAbstractAttr();
		AbstractTableProcessorNode<TreeCell> childNode;
		if (association.getAssociationType() == AssociationType.CONTAINTMENT)
		{
		    childNode = new ColumnAppenderProcNode<TreeCell>();
		}
		else
		{
		    childNode = new RowAppenderProcNode<TreeCell>();
		}
		recordProcNode.addChild(childNode);
		final List<Map<OutputAssociationColumn, Object>> childRecs =
			(List<Map<OutputAssociationColumn, Object>>) entry.getValue();
		IExpression expression = entry.getKey().getTgtExpression();
		final QueryHeaderData queryHeaderData = new QueryHeaderData(expression.getQueryEntity().getDynamicExtensionsEntity(),
					expression);
		final List<Map<OutputAssociationColumn, Object>> newChildList =
			dataHandler.updateTempList(childRecs,expression);
		if (!newChildList.isEmpty())
		{
			childNode.addChildren(generateQueryDatamap(newChildList, queryHeaderData, cell, dataHandler));
		}
	}

	/**
     * Traverse through the table and populate the list(List<List<Object>>)
     * which contains the final result to be shown on the spreadsheet.
     * @param table table
	 * @param dataHandler dataHandler
	 * @param selectedColumnsMetadata
     * @return entityDataList
     */
    public List<List<OutputAttributeColumn>> getEntityDataList(final Table<TreeCell> table, QueryExportDataHandler dataHandler, SelectedColumnsMetadata selectedColumnsMetadata)
	{
    	Map<QueryHeaderData, Integer>entityVsMaxCnt = getEntityVsMxCnt(table);
    	dataHandler.entityVsMaxCount = entityVsMaxCnt;
    	final List<List<OutputAttributeColumn>> entityDataList = new ArrayList<List<OutputAttributeColumn>>(); // this is the method which is adding those output attributes in line to the List
    	Map<QueryHeaderData,List<TreeCell>> treeCellMap;
    	List<OutputAttributeColumn> rowDataList;
        for (int i = 0; i < table.numRows(); i++)
        {
        	treeCellMap = new HashMap<QueryHeaderData, List<TreeCell>>();
            for (int j = 0; j < table.numColumns(); j++)
            {
            	TreeCell cell = table.get(i,j);
            	if(cell != TreeCell.EMPTY_CELL )
            	{
            		List<TreeCell>treeCellList;
            		if(treeCellMap.get(cell.getQueryHeaderData()) == null)
            		{
            			treeCellList = new ArrayList<TreeCell>();
            			treeCellList.add(cell);
            			treeCellMap.put(cell.getQueryHeaderData(), treeCellList);
            		}
            		else
            		{
            			treeCellList = treeCellMap.get(cell.getQueryHeaderData());
            			treeCellList.add(cell);
            		}
            	}
            }
            rowDataList = populateRowData(treeCellMap, entityVsMaxCnt,selectedColumnsMetadata);// this method adds all denormalised output attribtues in the RowDataList without sorting according to the defined View.
            entityDataList.add(rowDataList);
        }
        return entityDataList;
    }

    /**
     * Populate entity v/s max count by traversing the entire table.
     * @param table table
     * @return entityVsMaxCnt
     */
    private Map<QueryHeaderData, Integer> getEntityVsMxCnt(Table<TreeCell> table)
    {
    	Map<QueryHeaderData, Integer>tempMap;
    	Map<QueryHeaderData, Integer>entityVsMaxCnt = new HashMap<QueryHeaderData, Integer>();
    	for (int i = 0; i < table.numRows(); i++)
        {
    		tempMap = new HashMap<QueryHeaderData, Integer>();
            for (int j = 0; j < table.numColumns(); j++)
            {
            	TreeCell cell = table.get(i,j);
            	if(cell != TreeCell.EMPTY_CELL )
            	{
	            	if(tempMap.get(cell.getQueryHeaderData()) == null)
	            	{
	            		tempMap.put(cell.getQueryHeaderData(), 1);
	            	}
	            	else
	            	{
	            		Integer cnt = tempMap.get(cell.getQueryHeaderData());
	            		tempMap.put(cell.getQueryHeaderData(), cnt+1);
	            	}
            	}
            }
            if(entityVsMaxCnt.isEmpty())
            {
            	entityVsMaxCnt = tempMap;
            }
            else
            {
            	for(QueryHeaderData queryData : tempMap.keySet())
            	{
            		if(tempMap.get(queryData)>entityVsMaxCnt.get(queryData))
            		{
            			entityVsMaxCnt.put(queryData, tempMap.get(queryData));
            		}
            	}
            }
        }
		return entityVsMaxCnt;
	}

	/**
     * Populate each row of the table with data from the map.
     * @param treeCellMap treeCellMap
	 * @param selectedColumnsMetadata
	 * @param dataHandler
     * @param dataHandler dataHandler
     * @return rowDataList
     */
    private List<OutputAttributeColumn> populateRowData(Map<QueryHeaderData,List<TreeCell>> treeCellMap,
    		 Map<QueryHeaderData, Integer>entityVsMaxCnt, SelectedColumnsMetadata selectedColumnsMetadata)
    {
    	//first iterate on each query header & sort the the output attributes in each Record of the queryHeader, as well as find out the lowest index,
    	//of each header.
    	//Now iterate on each header according to sort order of its lowest index & then merge all the attributes in one list.
    	Map<QueryHeaderData,List<List<OutputAttributeColumn>>> queryHeaderVsRecordList = new HashMap<QueryHeaderData, List<List<OutputAttributeColumn>>>();
    	Map<Integer,QueryHeaderData> indexVsHeaderMap = new TreeMap<Integer,QueryHeaderData >();

    	for(QueryHeaderData queryHeaderData : treeCellMap.keySet())
    	{
    		List<TreeCell> cellList = treeCellMap.get(queryHeaderData);
    		int listSize = cellList.size();
    		int maxRecordCnt = entityVsMaxCnt.get(queryHeaderData);
    		int index = 0;
    		for(TreeCell cell : cellList)
    		{
    			Map<OutputAssociationColumn, Object> record = cell.getRec();
    			List<OutputAttributeColumn> rowDataList = new ArrayList<OutputAttributeColumn>();
    	        for (OutputAssociationColumn key : record.keySet())
    	    	{

    	        	if(key.getAbstractAttr() instanceof AttributeInterface)
    	        	{
    	        		final OutputAttributeColumn opAttributeColumn =
    	        			(OutputAttributeColumn)record.get(key);
    	        		rowDataList.add(opAttributeColumn);
    	        		//set attribute header
    	        		setHeaderDisplayName(index, opAttributeColumn);
    	        	}
    	    	}
    	        updateQueryHeaderRecordMap(queryHeaderVsRecordList, indexVsHeaderMap,
						queryHeaderData, rowDataList);
    	        index++;
    		}


    		while(listSize<maxRecordCnt)
    		{
    			Map<OutputAssociationColumn, Object> record = getEmptyRecordMap(queryHeaderData.getExpression(), cellList.get(0), selectedColumnsMetadata);
    			List<OutputAttributeColumn> rowDataList = new ArrayList<OutputAttributeColumn>();
    			for (OutputAssociationColumn key : record.keySet())
            	{
                	if(key.getAbstractAttr() instanceof AttributeInterface)
                	{
                		final OutputAttributeColumn prevOpAttrCol =
                			(OutputAttributeColumn)record.get(key);
                		OutputAttributeColumn opAttributeColumn = new OutputAttributeColumn
                		("", prevOpAttrCol.getColumnIndex(),prevOpAttrCol.getAttribute(),
                				prevOpAttrCol.getExpression(), null);
                		setHeaderDisplayName(index, opAttributeColumn);
                		rowDataList.add(opAttributeColumn);
                	}
            	}
    			updateQueryHeaderRecordMap(queryHeaderVsRecordList, indexVsHeaderMap,
						queryHeaderData, rowDataList);
    			listSize++;
    			index++;
    		}
    	}

    	// now collect all the output attributes in order as per the index in headerVsIndexMap
    	List<OutputAttributeColumn> finalRowDataList = new ArrayList<OutputAttributeColumn>();
    	for(Entry<Integer, QueryHeaderData> queryHeaderEntry : indexVsHeaderMap.entrySet() )
    	{
    		final List<List<OutputAttributeColumn>> recordList = queryHeaderVsRecordList.get(queryHeaderEntry.getValue());
    		for(List<OutputAttributeColumn> record : recordList)
    		{
    			finalRowDataList.addAll(record);
    		}

    	}
    	return finalRowDataList;
    }


    private Map<OutputAssociationColumn, Object> getEmptyRecordMap(IExpression expression, TreeCell treeCell,SelectedColumnsMetadata selectedColumnsMetadata)
    {
    	Map<OutputAssociationColumn, Object> record =treeCell.getRec();
    	if(treeCell.getRec().isEmpty())
    	{
    		QueryExportDataHandler dataHandler = new QueryExportDataHandler(null, null);
    		Collection<AttributeInterface> attributeList = expression.getQueryEntity().getDynamicExtensionsEntity().getAttributeCollection();
			RowProcessor rowProcessor = new RowProcessor();
			for(AttributeInterface attribute : attributeList)
			{

					// isPresent should take AttributeInterface as input
					/*OutputAttributeColumn val = rowProcessor.getValueForAttribute(
							 attribute, selectedColumnsMetadata
									.getSelectedAttributeMetaDataList(), expression);*/

				OutputAttributeColumn opAttrCol = null;
				String value;
				int columnIndex = -1;

				for (QueryOutputTreeAttributeMetadata outputTreeAttributeMetadata : selectedColumnsMetadata.getSelectedAttributeMetaDataList())
				{
					columnIndex++;
					BaseAbstractAttributeInterface presentAttribute = outputTreeAttributeMetadata
							.getAttribute();
					if (presentAttribute.equals(attribute)
							&& outputTreeAttributeMetadata.getTreeDataNode().getExpressionId() == expression
									.getExpressionId())
					{
						value = " ";
						opAttrCol = new OutputAttributeColumn(value, columnIndex, attribute, expression,
								null);
						break;
					}
				}

				if (opAttrCol != null)
				{
						OutputAssociationColumn opAssocCol = new OutputAssociationColumn(attribute,
								expression, null);
						record.put(opAssocCol, opAttrCol);
				}

			}
    	}
    	return record;
    }

	private void setHeaderDisplayName(int index, final OutputAttributeColumn opAttributeColumn)
	{
		StringBuffer headerDisplay = new StringBuffer();
		headerDisplay.append(Utility.getDisplayNameForColumn(opAttributeColumn.getAttribute()));
		if(index>0)
		{
			headerDisplay.append('_').append(index);
		}
		opAttributeColumn.setHeader(headerDisplay.toString());
	}

	private void updateQueryHeaderRecordMap(
			Map<QueryHeaderData, List<List<OutputAttributeColumn>>> queryHeaderVsRecordList,
			Map<Integer,QueryHeaderData> indexVsHeaderMap, QueryHeaderData queryHeaderData,
			List<OutputAttributeColumn> rowDataList)
	{
		Collections.sort(rowDataList, new AttributeOrderComparator());
		List<List<OutputAttributeColumn>> headerRecordList = queryHeaderVsRecordList.get(queryHeaderData);
		if(headerRecordList==null)
		{
			headerRecordList = new ArrayList<List<OutputAttributeColumn>>();
			queryHeaderVsRecordList.put(queryHeaderData, headerRecordList);
		}
		headerRecordList.add(rowDataList);

		if(!rowDataList.isEmpty())
		{
		  	indexVsHeaderMap.put(rowDataList.get(0).getColumnIndex(),queryHeaderData);
		}
	}
}
