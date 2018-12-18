package com.mikealbert.vision.specs.lov;

import java.util.List;

import javax.annotation.Resource;

import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.CustomerContactVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.testing.BaseSpec;

public class ClientLovTest extends BaseSpec {

	@Resource CustomerAccountService customerAccountService;
	
	public boolean testFindCustomerContacts(String searchParam) throws MalBusinessException{
		
		List<CustomerContactVO> customerContactVOList = customerAccountService.findCustomerContacts(searchParam, CorporateEntity.MAL, null, false);
		
		if(customerContactVOList != null && customerContactVOList.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
}
