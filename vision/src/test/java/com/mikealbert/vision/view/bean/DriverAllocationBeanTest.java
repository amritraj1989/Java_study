package com.mikealbert.vision.view.bean;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.data.domain.PageRequest;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.DriverAllocationDAO;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverService;
import com.mikealbert.util.SpringAppContext;
import com.mikealbert.vision.view.ViewConstants;


public class DriverAllocationBeanTest extends BeanTestCaseSetup{
	
	private String unitNo =  null;	
	final String ODO_UOM = "MILE";
	final String OP_CODE = "VIVEK";
	
	@Resource DriverService driverService;
	@Resource DriverAllocationDAO driverAllocationDAO;
	@Resource CustomerAccountService customerAccountService;
	DriverAllocationBean spyDriverAllocationBean = null;

    @PersistenceContext
	private EntityManager em;
	
	@Before
	public void setup() {
			
			Map<String, Object> pageInitParam = new HashMap<String, Object>();
			pageInitParam.put(ViewConstants.VIEW_PARAM_UNIT_NO, getUnitNo());		
			setupPageContract(pageInitParam);
			clearFaceMessages();
			DriverAllocationBean driverAllocationBean = (DriverAllocationBean) SpringAppContext.getBean(DriverAllocationBean.class);		
			spyDriverAllocationBean = Mockito.spy(driverAllocationBean);
			Mockito.doReturn(getUser()).when(spyDriverAllocationBean).getLoggedInUser();
		
	}	
	
	@Test
	public void testSave() {
			
			String customerCode= spyDriverAllocationBean.getSelectedAllocation().getFleetMaster().getContractLine().getContract().getExternalAccount().getExternalAccountPK().getAccountCode();
			List<Driver> resultList = driverService.getDrivers("%",customerAccountService.getReleatedAccountCodes(customerCode),CorporateEntity.MAL, MalConstants.FLAG_Y,new PageRequest(0,10));
			
			long oldDriverId  =  spyDriverAllocationBean.getSelectedAllocation().getDriver().getDrvId();
			Driver newDriver  = null; 			
			for (Driver driver : resultList) {
				if(driver.getDrvId() != oldDriverId ){
					newDriver  = driver;
					break;
				}	
			}
			
			spyDriverAllocationBean.setSelectedDriverId(oldDriverId);		
			Calendar calendar = Calendar.getInstance();		
			DriverAllocation selectedDriverAllocation = spyDriverAllocationBean.getSelectedAllocation(); 
			DriverAllocation newDriverAllocation = spyDriverAllocationBean.getNewAllocation();
			
			calendar.add(Calendar.DATE, -2);	
			selectedDriverAllocation.setAllocationDate(calendar.getTime());	 
			calendar.add(Calendar.DATE, 1);
			selectedDriverAllocation.setDeallocationDate(calendar.getTime());
	        calendar.add(Calendar.DATE, 1);
	        newDriverAllocation.setAllocationDate(calendar.getTime());
	        newDriverAllocation.setDriver(newDriver);
	        newDriverAllocation.setFromOdoReading(1000L);
	        newDriverAllocation.setOdoUom(ODO_UOM);
	        newDriverAllocation.setOpCode(OP_CODE);
	        
	    	spyDriverAllocationBean.save();
	    	
	    	Map<String,Object> errorMsgMap  = getErrorFaceMessages();
			Map<String,Object> successMsgMap  = getSuccessFaceMessages();
		
			assertTrue("New Aloocation should get save properly have have success message",successMsgMap.size()== 1);
			assertTrue("New Aloocation should get save properly with out any error message" ,errorMsgMap.size()== 0);
		
	}
	
	
	private String getUnitNo(){
		
		if(unitNo == null){
	       
	       unitNo = (String) em.createNativeQuery(TestQueryConstants.READ_UNIT_NO_CURRENT_OR_FUTURE_ALLOCATION).getSingleResult();		
		}
		
		return unitNo;
		
	} 
}
