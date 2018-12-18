package com.mikealbert.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.entity.WillowConfig;
import com.mikealbert.data.vo.AccountVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("willowConfigService")
public class WillowConfigServiceImpl implements WillowConfigService{

	@Resource
	private WillowConfigDAO willowConfigDAO;
	
	
	final static String LEASE_PLAN_PAYEE_ACC_CODE = "LEASE_PLAN_PAYEE_ACC_CODE";
	final static String LEASE_PLAN_PAYEE_ACC_TYPE = "LEASE_PLAN_PAYEE_ACC_TYPE";
	final static String LEASE_PLAN_PAYEE_ACC_ID = "LEASE_PLAN_PAYEE_ACC_ID";
	
	/**
	 * This method will return config value for a given config name from willow_config table
	 */
	@Transactional
	public String getConfigValue(String configName){
		try{
			WillowConfig willowConfig = willowConfigDAO.findById(configName).orElse(null);
			
			if(!MALUtilities.isEmpty(willowConfig)){
				return willowConfig.getConfigValue();
			}else{
				return null;
			}	
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding willow config value" }, ex);
		}
	}
	
	/**
	 * This method will return list of lease plan account pulling from willow_config table.
	 * This does not below here longer term; this is not generic to willow config entries
	 * It is more more approprate in vision code or in an external accounts (payee) service (under a payee specific method)
	 * The concept might change for this as well!
	 * @return List
	 */
	@Deprecated
	@Transactional
	public List<AccountVO> getLeasePlanPayeeDetail(){
		try{
			List<AccountVO> leasePlanPayeeList = new ArrayList<AccountVO>();
			
			String payeeIds = getConfigValue(LEASE_PLAN_PAYEE_ACC_ID);
			String payeeTypes = getConfigValue(LEASE_PLAN_PAYEE_ACC_TYPE);
			String payeeCodes = getConfigValue(LEASE_PLAN_PAYEE_ACC_CODE);
			
			if(!MALUtilities.isEmptyString(payeeIds) && !MALUtilities.isEmptyString(payeeTypes) && !MALUtilities.isEmptyString(payeeCodes)){
				String[] payeeIdArray = payeeIds.split(",");
				String[] payeeTypeArray = payeeTypes.split(",");
				String[] payeeCodeArray = payeeCodes.split(",");
				int size = payeeIdArray.length;
				if(payeeTypeArray.length == size && payeeCodeArray.length == size){
					for(int index = 0 ; index < size ; index++){
						AccountVO account = new AccountVO();
						account.setCorpId(Long.parseLong(payeeIdArray[index]));
						account.setAccountType(payeeTypeArray[index]);
						account.setAccountCode(payeeCodeArray[index]);
						leasePlanPayeeList.add(account);
					}
				}
			}
			
			return leasePlanPayeeList;
		
		}catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding lease plan accounts from willow config" }, ex);
		}
	}

	@Override
	public List<WillowConfig> getMultipleWillowConfigWithValue(List<String> configNames) {
		return willowConfigDAO.getMultipleWillowConfigWithValue(configNames);
	}
	
	
}
