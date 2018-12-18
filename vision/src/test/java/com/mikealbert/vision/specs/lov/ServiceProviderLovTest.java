package com.mikealbert.vision.specs.lov;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.data.vo.ServiceProviderLOVVO;

public class ServiceProviderLovTest extends BaseSpec {
	@Resource
	ServiceProviderService serviceProviderService;
	
	String phoneNumber = null;
	Boolean correctPhoneFormat = false;

	public boolean testSearchServiceProviderByProvider(String provider)
			throws MalBusinessException {

		try {
			List<ServiceProviderLOVVO> listServiceProviders = null;
			listServiceProviders = serviceProviderService.getAllServiceProviders(provider,null,null,null,null,false,null);
			if (listServiceProviders.size() == 1) {
				setPhoneNumber(listServiceProviders.get(0).getServiceProviderTelephoneNumber());
			}
			return listServiceProviders.size() > 0 ? true : false;

			
		} catch (MalException malEx) {
			return false;
		}
	}
	
	public boolean testSearchServiceProviderByZipCode(String zipCode)
			throws MalBusinessException {

		try {
			List<ServiceProviderLOVVO> listServiceProviders = null;
			listServiceProviders = serviceProviderService.getAllServiceProviders(null,null,zipCode,null,null,false,null);
			return listServiceProviders.size() > 0 ? true : false;
		} catch (MalException malEx) {
			return false;
		}
	}
	
	public boolean testSearchServiceProviderByPhone(String phone)
			throws MalBusinessException {

		try {
			List<ServiceProviderLOVVO> listServiceProviders = null;
			listServiceProviders = serviceProviderService.getAllServiceProviders(null,null,null,phone,null,false,null);
			return listServiceProviders.size() > 0 ? true : false;
		} catch (MalException malEx) {
			return false;
		}
	}
	
	public boolean testSearchServiceProviderByPayee(String payee)
			throws MalBusinessException {

		try {
			List<ServiceProviderLOVVO> listServiceProviders = null;
			listServiceProviders = serviceProviderService.getAllServiceProviders(null,payee,null,null,null,false,null);
			return listServiceProviders.size() > 0 ? true : false;
		} catch (MalException malEx) {
			return false;
		}
	}
	
	public boolean testSearchServiceProviderByPhoneFormat(String phone)
			throws MalBusinessException {

		try {
			List<ServiceProviderLOVVO> listServiceProviders = null;
			listServiceProviders = serviceProviderService.getAllServiceProviders(null,null,null,'('+phone,null,false,null);
			Boolean patn1 = false;
			Boolean patn2 = false;
			
			for (ServiceProviderLOVVO servProvs : listServiceProviders) {
				Pattern p1 = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
				Pattern p2 = Pattern.compile("\\(\\d{3}\\)\\d{3}-\\d{4}");
				Matcher matcher1 = p1.matcher(servProvs.getServiceProviderTelephoneNumber());
				Matcher matcher2 = p2.matcher(servProvs.getServiceProviderTelephoneNumber());
				if (matcher1.matches()) {
					patn1 = true;
				}
					
				if (matcher2.matches()) {
					patn2 = true;
				}
				
				if (patn1 && patn2) {
					correctPhoneFormat = true;
					break;
				}
			}
			
			return listServiceProviders.size() > 0 ? true : false;
		} catch (MalException malEx) {
			return false;
		}
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Boolean getCorrectPhoneFormat() {
		return correctPhoneFormat;
	}

	public void setCorrectPhoneFormat(Boolean correctPhoneFormat) {
		this.correctPhoneFormat = correctPhoneFormat;
	}


}
