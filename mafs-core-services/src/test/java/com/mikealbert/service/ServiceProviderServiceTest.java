package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderAddress;
import com.mikealbert.data.entity.ServiceProviderDiscount;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.ServiceProviderLOVVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.dao.ServiceProviderDAO;

public class ServiceProviderServiceTest extends BaseTest{
	@Resource ServiceProviderService serviceProviderService;
	
	@Resource
	ServiceProviderDAO serviceProviderDAO;

	Long parentProviderId = 18655L;
	
	@Test
	public void findAllServiceProvidersByName(){
		List<ServiceProviderLOVVO> list = serviceProviderService.getAllServiceProviders("Frankl",null,null,null,null,false,null);	
        Assert.assertTrue(list.size() > 0);
        }
	
	
	@Test
	public void testSearchServiceProviderByPayee()
			throws MalBusinessException {

		try {
			List<ServiceProviderLOVVO> listServiceProviders = null;
			listServiceProviders = serviceProviderService.getAllServiceProviders(null,"Consolidated Service",null,null,null,false,null);
			Assert.assertTrue(listServiceProviders.size() > 0);
		} catch (MalException malEx) {
			malEx.printStackTrace();
		}
	}
	
	@Test
	public void testSearchServiceProviderIndByCode() {
		
		Assert.assertTrue(serviceProviderService.isAutoCompleteServiceProvider(serviceProviderService.getServiceProvider(1L)));
		Assert.assertTrue(serviceProviderService.isAutoCompleteServiceProvider(serviceProviderService.getServiceProvider(32699L)));
		Assert.assertTrue(serviceProviderService.isAutoCompleteServiceProvider(serviceProviderService.getServiceProvider(38323L)));
	}
	
	
	@Ignore
	public void testGetServiceProviderDisountsByDate(){
		long serviceProviderId = 203L;
		List<ServiceProviderDiscount> list = serviceProviderService.getServiceProviderDisountsByDate(serviceProviderId, new Date());
		
		Assert.assertTrue(list.size() > 0);
		
	}
	
	@Test
	public void testGetServiceProviderDisountDisplay(){
		long serviceProviderId = 202L;
	
		String str =  DisplayFormatHelper.formatSupplierDiscountForDisplay(serviceProviderService.getServiceProvider(serviceProviderId), serviceProviderService.getServiceProviderDisountsByDate(serviceProviderId, new Date()), "<br/>");
		
		Assert.assertNotNull(str);
		
	}

	
	@Test
	public void testGetServiceProviderByCodeAndParent(){
		ServiceProvider parent = serviceProviderDAO.findById(parentProviderId).orElse(null);
		
		ServiceProvider provider = serviceProviderService.getServiceProviderByProviderCode("00079338", parent);
		
		Assert.assertNotNull(provider);
		Assert.assertEquals(provider.getServiceProviderNumber(),"00079338");
		
	}
	
	@Test
	public void testSaveServiceProvider() {
		ServiceProvider parent = serviceProviderDAO.findById(parentProviderId).orElse(null);
		ServiceProvider provider = new ServiceProvider();
		
		provider.setParentServiceProvider(parent);
		provider.setNetworkVendor(parent.getNetworkVendor());
		provider.setPayeeAccount(parent.getPayeeAccount());
		provider.setServiceTypeCode(parent.getServiceTypeCode());
		provider.setServiceProviderName("Cassidy Tire & Service - TEST");
		provider.setServiceProviderNumber("00079123");
		provider.setEnteredBy("AUTO");
		provider.setLastUpdateDate(new Date());
		provider.setNoOfBays(10L);
		provider.setNormalHoursSat("10-00am - 1:00pm");
		provider.setNormalHoursWeek("11-00am - 2:00pm");
		provider.setInactiveInd("N");
		
		provider.setServiceProviderCategory(parent.getServiceProviderCategory());
		
		ServiceProvider serviceProvider;
		try {
			serviceProvider = serviceProviderService.saveServiceProvider(provider);
			
			Assert.assertNotNull(serviceProvider);
			
			Assert.assertTrue(provider.getServiceProviderName().equals(serviceProvider.getServiceProviderName()));
			
			Assert.assertEquals("Service Provider saved successfully",serviceProvider,provider);
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	@Test
	public void testSaveNewDiscountDetails(){
		ServiceProvider parent = serviceProviderDAO.findById(parentProviderId).orElse(null);
		ServiceProvider provider = new ServiceProvider();
		
		provider.setParentServiceProvider(parent);
		provider.setNetworkVendor(parent.getNetworkVendor());
		provider.setPayeeAccount(parent.getPayeeAccount());
		provider.setServiceTypeCode(parent.getServiceTypeCode());
		provider.setServiceProviderName("Cassidy Tire & Service - TEST");
		provider.setServiceProviderNumber("00079123");
		provider.setEnteredBy("AUTO");
		provider.setLastUpdateDate(new Date());
		
		provider.setEnteredBy("AUTO");
		provider.setTelephoneNo("(847)100-1000");
		provider.setWorkshopClearanceFeet("11 Ft.");
		provider.setNormalHoursWeek("12 Hrs");
		provider.setNormalHoursSat("10 Hrs");
		provider.setInactiveInd("N");
		
		ServiceProviderAddress providerAddress = new ServiceProviderAddress();
		List<ServiceProviderAddress> addresses = new ArrayList<ServiceProviderAddress>();
		providerAddress.setAddressType("POST");
		providerAddress.setAddressLine1("1234 Somewhere Street");
		providerAddress.setAddressLine2("Noida");
		providerAddress.setAddressLine3("Delhi");
		providerAddress.setAddressLine3("ABCD");
		providerAddress.setCountryCode("USA");
		providerAddress.setCountyCode("095");
		providerAddress.setDefaultInd("Y");
		providerAddress.setGeoCode("36-095-2490");
		providerAddress.setPostcode("43623");
		providerAddress.setRegion("OH");
		providerAddress.setTownCity("TOLEDO");
		providerAddress.setServiceProvider(provider);
		addresses.add(providerAddress);
		
		provider.setServiceProviderAddresses(addresses);
		provider.setServiceProviderCategory(parent.getServiceProviderCategory());
		
		List<ServiceProviderDiscount> discounts = new ArrayList<ServiceProviderDiscount>();
		ServiceProviderDiscount providerDiscount = new ServiceProviderDiscount();
		providerDiscount.setDiscAppl("O");
		providerDiscount.setEffectiveDate(new Date());
		providerDiscount.setLabourDisc(new BigDecimal(14.2));
		providerDiscount.setPartsDisc(new BigDecimal(10));
		providerDiscount.setReviewDate(new Date());
		providerDiscount.setTyreDisc(new BigDecimal(5));
		providerDiscount.setServiceProvider(provider);
		discounts.add(providerDiscount);
		provider.setServiceProviderDiscounts(discounts);
	
		ServiceProvider serviceProvider;
		try {
			serviceProvider = serviceProviderService.saveServiceProvider(provider);
			
			Assert.assertNotNull(serviceProvider);
			
			Assert.assertTrue(provider.getServiceProviderName().equals(serviceProvider.getServiceProviderName()));
			
			Assert.assertEquals("Service Provider saved successfully",serviceProvider,provider);
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
}
