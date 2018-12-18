package com.mikealbert.service;


import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.CostCenterDAO;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.CostCentreCodePK;
import com.mikealbert.data.entity.DriverCostCenter;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.exception.MalException;

/**
 * Implementation of {@link com.mikealbert.vision.service.CostCenterService}
 */
@Service("costCenterService")
public class CostCenterServiceImpl implements CostCenterService {
	
	
	@Resource private CostCenterDAO costCenterDAO;
	
	/**
	 * Returns a list of cost centres for the supplied external account while filtering out the "NONE" cost centre value and pre-pending a Null entry used 
	 * to allow the user to make a deliberate choice to remove the cost centre (set it to Null) and have the back end terminate date it.<br>
	 * Used by:<br>
	 * 1) Driver Add Edit when populating a list of available cost centers to assign to a driver.
	 * 
	 * @param externalAccount Used to find cost centre codes for this account only
	 * @return List of cost centre codes for supplied external account filtering out "NONE" 
	 */
	public List<CostCentreCode> getCostCenters(ExternalAccount externalAccount) {
		try {
			List<CostCentreCode> retList = new ArrayList<CostCentreCode>();
			
			CostCentreCodePK nullCostCenterPK = new CostCentreCodePK("", externalAccount.getExternalAccountPK().getCId(), externalAccount.getExternalAccountPK().getAccountType(), externalAccount.getExternalAccountPK().getAccountCode());
			CostCentreCode nullCostCenter = new CostCentreCode(nullCostCenterPK,"-----","N");
			
			retList.add(nullCostCenter);
			
			for(CostCentreCode costCenter : costCenterDAO.findByAccount(externalAccount.getExternalAccountPK().getAccountCode(), 
					externalAccount.getExternalAccountPK().getAccountType(), 
					externalAccount.getExternalAccountPK().getCId())){
				
				if(!costCenter.getCostCentreCodesPK().getCostCentreCode().equalsIgnoreCase(MalConstants.NONE)){
					retList.add(costCenter);
				}
			}
			
			return retList;
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding cost centers" }, ex);
		}	
	}
	
	/**
	 * This gets all the cost centers for the passed in account
	 * @param account Account to use to retrieve cost centers
	 * @return List of cost center codes
	 */
	public List<CostCentreCode> getAllCostCentersByAccount(ExternalAccount account){
		List<CostCentreCode> costCentreCodes = new ArrayList<CostCentreCode>();
		try{
			if(account != null && account.getExternalAccountPK() != null){
				costCentreCodes = costCenterDAO.findByAccount(account.getExternalAccountPK().getAccountCode(), account.getExternalAccountPK().getAccountType(), account.getExternalAccountPK().getCId());
			}
			return costCentreCodes;
		}catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding cost centers" }, ex);
		}	
	}

	/**
	 * Checks if the supplied cost centre exists for the supplied account. <br>
	 * Used by:<br>
	 * 1) Driver service when updating a cost center for a driver.
	 * 
	 * @param externalAccount Account to check has the supplied cost center.
	 * @param costCenter Cost Center to check exists for the account.
	 * @return Returns true if the supplied cost centre exists for the supplied account.
	 */
	public boolean accountHasCostCenter(ExternalAccount externalAccount, String costCenter) {
		CostCentreCodePK costCentreCodePK = new CostCentreCodePK(costCenter, externalAccount.getExternalAccountPK().getCId(), 
				externalAccount.getExternalAccountPK().getAccountType(), externalAccount.getExternalAccountPK().getAccountCode()); 
		return costCenterDAO.existsById(costCentreCodePK);
		
	}

	/**
	 * Retrieves the description for the cost center provided as parameter.<br>
	 * Used by:<br>
	 * 1) Driver Overview to show the cost center description
	 * @param driverCostCenter
	 * @return Description of the cost center as String
	 */
	public String getCostCenterDescription(DriverCostCenter driverCostCenter) {
		CostCentreCodePK costCenterCodePK = new CostCentreCodePK(driverCostCenter.getCostCenterCode(), 
																 driverCostCenter.getExternalAccount().getExternalAccountPK().getCId(),
																 driverCostCenter.getExternalAccount().getExternalAccountPK().getAccountType(),
																 driverCostCenter.getExternalAccount().getExternalAccountPK().getAccountCode());
		
		return costCenterDAO.findById(costCenterCodePK).orElse(null).getDescription();
	}
	
	
	/**
	 * Retrieves the cost center entity.
	 * @param ExternalAccount the client account
	 * @param String the cost center code
	 * @return CostCentreCode entity
	 */
	public CostCentreCode getCostCenter(ExternalAccount externalAccount, String costCenterCode){
		return costCenterDAO.findByAccountCostCenterCode(externalAccount.getExternalAccountPK().getAccountCode(), 
				externalAccount.getExternalAccountPK().getAccountType(), 
				externalAccount.getExternalAccountPK().getCId(), 
				costCenterCode);
	}
	
}
