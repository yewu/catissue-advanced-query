
package edu.wustl.query.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractBizLogicFactory;
import edu.wustl.common.query.pvmanager.impl.PVManagerException;
import edu.wustl.common.querysuite.queryobject.IConstraints;
import edu.wustl.common.querysuite.queryobject.ICustomFormula;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IParameterizedQuery;
import edu.wustl.common.querysuite.queryobject.IQuery;
import edu.wustl.common.querysuite.queryobject.impl.ParameterizedQuery;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.query.actionForm.SaveQueryForm;
import edu.wustl.query.bizlogic.QueryBizLogic;
import edu.wustl.query.htmlprovider.SavedQueryHtmlProvider;
import edu.wustl.query.util.global.Constants;
import edu.wustl.query.util.global.Utility;
import edu.wustl.query.util.querysuite.QueryModuleConstants;

public class LoadSaveQueryPageAction extends Action
{

	@Override
	/**
	 * This action loads all the conditions from the query.   
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
	     HttpSession session = request.getSession();
	     String isworkflow= (String)request.getAttribute(Constants.IS_WORKFLOW);
	     if(isworkflow==null)
	     {
	    	 isworkflow=request.getParameter(Constants.IS_WORKFLOW);
	     }
	    
         String pageOf= (String)request.getAttribute(Constants.PAGE_OF);
        if(pageOf==null)
        {
        	
           pageOf=request.getParameter(Constants.PAGE_OF);	
        }
		  request.setAttribute(Constants.PAGE_OF,pageOf);
		//System.out.println("");
		  if(Constants.TRUE.equals(isworkflow))
		{	
		  request.setAttribute(Constants.IS_WORKFLOW,Constants.TRUE);
		  String workflowName= (String)request.getSession().getAttribute(Constants.WORKFLOW_NAME);
		  request.setAttribute(Constants.WORKFLOW_NAME,workflowName);
		} 
		
		  IQuery queryObject = (IQuery) session.getAttribute(Constants.QUERY_OBJECT);
		  String queryName=(String)session.getAttribute("queryName");
		  request.setAttribute("query_Name",queryName);
		String target = Constants.FAILURE;
		
		
		boolean isDagEmpty = true;
		if (queryObject != null)
		{
			boolean isShowAll = request.getParameter(Constants.SHOW_ALL) == null ? false : true;
			target = loadPage(form, request, queryObject,isShowAll);			
			IConstraints constraints = queryObject.getConstraints();
			for(IExpression exp: constraints)
			{
				isDagEmpty = false;
			}
		}
		checkIsQueryAlreadyShared(queryObject,request);
		if(isDagEmpty)
		{
			// Handle null query 
			
			target = Constants.SUCCESS;
			String errorMsg = ApplicationProperties.getValue("query.noLimit.error");
			ActionErrors errors = Utility.setActionError(errorMsg,"errors.item");
			saveErrors(request, errors);
			request.setAttribute(Constants.IS_QUERY_SAVED,Constants.IS_QUERY_SAVED);
		}
		setErrorMessage(request);
		
		return mapping.findForward(target);
	}

	
	/**
	 * It will check weather the Query is shared or not & set the shared_queries flag in the request 
	 * accordingly.
	 * @param queryObject query which is to be checked. 
	 * @param request 
	 */
	private void checkIsQueryAlreadyShared(IQuery queryObject, HttpServletRequest request)
	{
		try
		{
			QueryBizLogic queryBizLogic = (QueryBizLogic)AbstractBizLogicFactory.getBizLogic(ApplicationProperties.getValue("app.bizLogicFactory"),
				"getBizLogic", Constants.ADVANCE_QUERY_INTERFACE_ID);
			
			boolean isShared = queryBizLogic.isSharedQuery((IParameterizedQuery)queryObject);
			request.setAttribute(Constants.SAHRED_QUERIES, isShared);
		}
		catch (BizLogicException e) {
			ActionErrors errors = Utility.setActionError(e.getMessage(),"errors.item");
			saveErrors(request, errors);
		}
	}


	/**
	 * This Method generates Html for Save query page 
	 * @param form
	 * @param request
	 * @param queryObject
	 * @param isShowAll
	 * @return
	 * @throws PVManagerException 
	 */
	private String loadPage(ActionForm form, HttpServletRequest request,
			IQuery queryObject,boolean isShowAll) throws PVManagerException
	{
		String target;
		Map<Integer,ICustomFormula> customFormulaIndexMap = new HashMap<Integer, ICustomFormula>();
		String htmlContents = new SavedQueryHtmlProvider().getHTMLForSavedQuery(queryObject, isShowAll,
				Constants.SAVE_QUERY_PAGE,customFormulaIndexMap);
		request.getSession().setAttribute(QueryModuleConstants.CUSTOM_FORMULA_INDEX_MAP, customFormulaIndexMap);
		request.setAttribute(Constants.HTML_CONTENTS, htmlContents);
		String showAllLink = isShowAll
				? Constants.SHOW_SELECTED_ATTRIBUTE
				: Constants.SHOW_ALL_ATTRIBUTE;
		request.setAttribute(Constants.SHOW_ALL_LINK, showAllLink);
		if (!isShowAll)
		{
			request.setAttribute(Constants.SHOW_ALL, Constants.TRUE);
		}
		target = Constants.SUCCESS;
		SaveQueryForm savedQueryForm = (SaveQueryForm)form;
		if (queryObject.getId() != null && queryObject instanceof ParameterizedQuery)
		{
			savedQueryForm.setDescription(((ParameterizedQuery)queryObject).getDescription());
			savedQueryForm.setTitle(((ParameterizedQuery)queryObject).getName());
		}
		//the title from GetCount query page should be displayed as default in Save Query page
		if (queryObject.getId() == null && queryObject instanceof ParameterizedQuery)
		{
			savedQueryForm.setTitle(((ParameterizedQuery)queryObject).getName());
		}
		return target;
	}
	
	private void setErrorMessage(HttpServletRequest request)
	{
		String errorMessage = (String) request.getSession().getAttribute("errorMessageForEditQuery");
		if (errorMessage != null)
		{
			ActionErrors errors = Utility.setActionError(errorMessage,"errors.item");
			saveErrors(request, errors);
			request.getSession().removeAttribute("errorMessageForEditQuery");
		}
	}
	
	

}
