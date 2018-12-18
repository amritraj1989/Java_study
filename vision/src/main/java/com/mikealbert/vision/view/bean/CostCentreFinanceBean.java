package com.mikealbert.vision.view.bean;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.enumeration.ClientFinanceTypeCodes;
import com.mikealbert.data.vo.ClientFinanceVO;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class CostCentreFinanceBean extends StatefulBaseBean {	
	private static final long serialVersionUID = -7170421837551409230L;

	@Resource FinanceParameterService financeParameterService;
	@Resource CustomerAccountService customerAccountService;
	
	private List<ClientFinanceVO> costCentreFinanceList;
	private ClientFinanceVO inputClientFinanceVO;
	private ExternalAccount customer;
	
	private TreeNode rootNode;
	
	@PostConstruct
	public void init() {
		initializeDataTable(500, 500, new int[] {10, 10, 10, 10, 10, 10}).setHeight(0);      	
    	super.openPage();
    	try {
    		loadCostCentreFinanceParams();
    		populateCostCentres();
    	} catch (Exception ex) {
    		logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
    	}
	}
	
	@Override
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		thisPage.setPageUrl(ViewConstants.COST_CENTRE_FINANCE);
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_COST_CENTRE_FINANCE);
		this.setInputClientFinanceVO((ClientFinanceVO) map.get(ViewConstants.VIEW_PARAM_CLIENT_FINANCE_VO));
	}

	public String cancel(){
		return cancelPage(); 
	}
	
	@Override
	protected void restoreOldPage() {
		// TODO Auto-generated method stub
		
	}
	
    private void loadCostCentreFinanceParams(){
   		setCostCentreFinanceList(financeParameterService.searchClientFinanceParametersByCostCentre(getInputClientFinanceVO().getEaCId(), getInputClientFinanceVO().getEaAccountType(), getInputClientFinanceVO().getEaAccountCode(), getInputClientFinanceVO().getParameterId()));
   		setCustomer(customerAccountService.getOpenCustomerAccountByCode(getInputClientFinanceVO().getEaAccountCode()));
    }
    
    public void populateCostCentres() {
    	rootNode = new DefaultTreeNode("root", null);
    	TreeNode parentNode = null;
    	int prevLevel = 0;
    	int curLevel = 0;
    	for (ClientFinanceVO clientFinanceVO : costCentreFinanceList) {
    		curLevel = clientFinanceVO.getClientCostCentreLevel();
    		if (MALUtilities.isEmptyString(clientFinanceVO.getClientCostCentreParent())) {
    			prevLevel = 1;
    			parentNode = new DefaultTreeNode(clientFinanceVO, rootNode);
    			parentNode.setExpanded(true);
    		}
    		
    		if (curLevel > prevLevel) {
    			parentNode = new DefaultTreeNode(clientFinanceVO, parentNode);
    			parentNode.setExpanded(true);
    		} else if (curLevel <= prevLevel && curLevel != 1) {
    			String parentCode = clientFinanceVO.getClientCostCentreParent();
    			while (!parentCode.equals(((ClientFinanceVO)(parentNode.getData())).getClientCostCentreCode())) {
    				parentNode = parentNode.getParent();
    			}
    			parentNode = new DefaultTreeNode(clientFinanceVO, parentNode);
    			parentNode.setExpanded(true);
    		}
    		prevLevel = clientFinanceVO.getClientCostCentreLevel();
    	}
    	
    }
    
    public void saveCostCentreFinanceParams() {
    	try {
    		financeParameterService.saveUpdateOrDeleteClientFinanceParameters(costCentreFinanceList, ClientFinanceTypeCodes.DRIVER_COST_CENTRE.getCode());
    		super.addSuccessMessage("process.success","Save Cost Centre Finance Parameters");
    		loadCostCentreFinanceParams();
    		populateCostCentres();
    	} catch (Exception ex) {
    		logger.error(ex);		
			handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
    	}
    }

    public List<ClientFinanceVO> getCostCentreFinanceList() {
		return costCentreFinanceList;
	}

	public void setCostCentreFinanceList(List<ClientFinanceVO> costCentreFinanceList) {
		this.costCentreFinanceList = costCentreFinanceList;
	}

	public ClientFinanceVO getInputClientFinanceVO() {
		return inputClientFinanceVO;
	}

	public void setInputClientFinanceVO(ClientFinanceVO inputClientFinanceVO) {
		this.inputClientFinanceVO = inputClientFinanceVO;
	}

	public ExternalAccount getCustomer() {
		return customer;
	}

	public void setCustomer(ExternalAccount customer) {
		this.customer = customer;
	}

	public TreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(TreeNode rootNode) {
		this.rootNode = rootNode;
	}
	
}
