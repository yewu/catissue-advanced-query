/**
 * 
 */

package edu.wustl.common.query.queryobject.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.wustl.common.query.queryobject.impl.metadata.QueryOutputTreeAttributeMetadata;
import edu.wustl.common.querysuite.queryobject.IOutputEntity;
import edu.wustl.query.util.global.Constants;

/**
 * @author prafull_kadam
 * Output tree node Class for Advance Query tree. 
 * Only SQLGenerator class will instantiate objects of this class, & It is expected that other classes should use getter methods only.
 */
public class OutputTreeDataNode implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4865638917714061869L;

	private long id;

	private static long lastId = 0;
	private OutputTreeDataNode parentNode;

	private List<OutputTreeDataNode> children = new ArrayList<OutputTreeDataNode>();
	IOutputEntity outputEntity;
	private int treeNo;
	private int expressionId;
	private boolean iscontainedobject = false;

	private List<QueryOutputTreeAttributeMetadata> attributes = new ArrayList<QueryOutputTreeAttributeMetadata>();

	/**
	 * The Constructor to instantiate the object of this class.
	 * @param outputEntity The reference to the output Entity.
	 * @param expressionId The expression id corresponding to this Output tree.
	 * @param treeNo The integer representing tree no.
	 */
	public OutputTreeDataNode(IOutputEntity outputEntity, int expressionId, int treeNo,
			boolean iscontainedobject)
	{
		this.outputEntity = outputEntity;
		this.expressionId = expressionId;
		id = lastId++;
		this.treeNo = treeNo;
		this.iscontainedobject = iscontainedobject;
	}

	/**
	 * To get the auto generated id for the class instance. 
	 * @return The long value representing auto generated id for the class instance.
	 * @see edu.wustl.common.querysuite.queryobject.IOutputTreeNode#getId()
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * To add the child node. 
	 * @param outputEntity The output entity of the child node to be added.
	 * @param expressionId The expression id corresponding to this Output tree.
	 * @return The reference to the child node.
	 */
	public OutputTreeDataNode addChild(IOutputEntity outputEntity, int expressionId,
			boolean isContained)
	{
		OutputTreeDataNode child = new OutputTreeDataNode(outputEntity, expressionId, treeNo,
				isContained);
		child.parentNode = this;
		children.add(child);

		return child;
	}

	/**
	 * @return The list all children of this node
	 */
	public List<OutputTreeDataNode> getChildren()
	{
		return children;
	}

	/**
	 * @return The reference to the output Entity associated with this node.
	 */
	public IOutputEntity getOutputEntity()
	{
		return outputEntity;
	}

	/**
	 * @return Returns the reference to the parent node, null if its root node.
	 */
	public OutputTreeDataNode getParent()
	{
		return parentNode;
	}

	/**
	 * @return the expressionId The expression id corresponding to this expression.
	 */
	public int getExpressionId()
	{
		return expressionId;
	}

	/**
	 * @return the treeNo
	 */
	public int getTreeNo()
	{
		return treeNo;
	}

	/**
	 * @param obj the object to be compared.
	 * @return true if following attributes of both object matches:
	 * 			- id
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean retVal = false;
		if (this == obj)
		{
			retVal = true;
		}

		if (obj != null && this.getClass() == obj.getClass())
		{
			OutputTreeDataNode node = (OutputTreeDataNode) obj;
			if (this.id == node.getId())
			{
				retVal = true;
			}

		}
		return retVal;
	}

	/**
	 * To get the HashCode for the object. It will be calculated based on Following attributes:
	 * 			- treeNo
	 * 	 		- id
	 * @return The hash code value for the object.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = 1;
		//		hash = hash * Constants.HASH_PRIME + (int) treeNo;
		hash = hash * Constants.HASH_PRIME + (int) id;
		return hash;
	}

	/**
	 * @return String representation of object in the form: [outputEntity : parentNode]
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + id + ":" + outputEntity.toString() + "("
				+ (parentNode == null ? "-" : parentNode.getId()) + ")" + "]";
	}

	/**
	 * @return the attributes
	 */
	public List<QueryOutputTreeAttributeMetadata> getAttributes()
	{
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<QueryOutputTreeAttributeMetadata> attributes)
	{
		this.attributes = attributes;
	}

	/**
	 * Returns a unique id for each node of each tree
	 * @return String id
	 */
	public String getUniqueNodeId()
	{
		return this.treeNo + Constants.NODE_DATA_SEPARATOR + id;
	}

	/**
	 * To add attribute meta data Object in the list.
	 * @param attribute The reference to the attribute meta data object.
	 */
	public void addAttribute(QueryOutputTreeAttributeMetadata attribute)
	{
		attributes.add(attribute);
	}

	/**
	 * TO get the meta data object for the given attribute.
	 * @param attribute The reference to the attribute, this attribute must be part of this outputTreeDataNode.
	 * @return The reference to the QueryOutputTreeAttributeMetadata.
	 */
	public QueryOutputTreeAttributeMetadata getAttributeMetadata(AttributeInterface attribute)
	{
		QueryOutputTreeAttributeMetadata attributeMetadata = null;
		for (QueryOutputTreeAttributeMetadata loopCntr : attributes)
		{
			if (loopCntr.getAttribute().equals(attribute))
			{
				attributeMetadata = loopCntr;
				break;
			}

		}
		return attributeMetadata;
	}

	public boolean isContainedObject()
	{
		return iscontainedobject;
	}

	public void setContainedObject(boolean iscontainedobject)
	{
		this.iscontainedobject = iscontainedobject;
	}
}
