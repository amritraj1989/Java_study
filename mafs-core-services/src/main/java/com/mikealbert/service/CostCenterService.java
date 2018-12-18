package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.DriverCostCenter;
import com.mikealbert.data.entity.ExternalAccount;
/**
* Public Interface implemented by {@link com.mikealbert.vision.service.CostCenterServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.CostCentreCodePK}(s) and {@link com.mikealbert.data.entity.CostCentreCode}(s).
*  @see com.mikealbert.data.entity.CostCentreCodePK
 * @see com.mikealbert.data.entity.CostCentreCode
 * @see com.mikealbert.vision.service.CostCenterServiceImpl
**/
public interface CostCenterService {	
	
	public List<CostCentreCode> getCostCenters(ExternalAccount externalAccount);
	
	public List<CostCentreCode> getAllCostCentersByAccount(ExternalAccount account);

	boolean accountHasCostCenter(ExternalAccount externalAccount, String costCenter);
	
	public String getCostCenterDescription(DriverCostCenter driverCostCenter);
	
	public CostCentreCode getCostCenter(ExternalAccount externalAccount, String costCenterCode);	
}
