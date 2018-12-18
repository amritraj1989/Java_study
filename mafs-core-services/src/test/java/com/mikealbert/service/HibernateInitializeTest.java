package com.mikealbert.service;

import static org.junit.Assert.*;
import java.util.List;
import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DocSupplier;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.testing.BaseTest;

public class HibernateInitializeTest extends BaseTest {

	@Resource 
	private ContractService contractService;	
	@Resource
	private DriverService driverService;
	@Resource
	private FleetMasterService fleetMasterService;
	@Resource
	private ModelService modelService;
	@Resource
	private PurchaseOrderService purchaseOrderService;
	@Resource
	private QuotationService quotationService;

	
	@Test
	public void testGetDriverOnContractLine() throws Exception {
		Long clnId = 221459l;
		ContractLine cl = contractService.getDriverOnContractLine(clnId);
		assertNotNull(cl.getDriver().getGaragedAddress().getAddressLine1());
	}	

	@Test
	public void testGetQuotationModelOnContractLine() throws Exception {
		Long clnId = 221459l;
		ContractLine cl = contractService.getQuotationModelOnContractLine(clnId);
		assertNotNull(cl.getQuotationModel().getLastAmendedUser());
	}	

	@Test
	public void testGetDriver() throws Exception {
		Long drvId = 4l;
		Driver d = driverService.getDriver(drvId);
		assertNotNull(d.getDriverForename());
	}	
	
	@Ignore
	@Test
	public void testGetFleetMasterFilterCurrentAllocaton() throws Exception {
		Long drvId = 111105l;
		String unitNumber = "00925747";
		List<FleetMaster> list = fleetMasterService.getFleetMasterFilterCurrentAllocaton(unitNumber, drvId);
		assertNotNull(list.get(0).getContractLine().getQuotationModel().getQmdId());
	}	
	
	@Test
	public void testGetDealerAccessoryWithPrices() throws Exception {
		Long dacId = 116409l;
		DealerAccessory da = modelService.getDealerAccessoryWithPrices(dacId);
		assertNotNull(da.getDealerAccessoryPrices().get(0).getBasePrice());
	}	

	@Test
	public void testGetOrderingDealerByDocId() throws Exception {
		Long docId = 2693410L;
		DocSupplier ds = purchaseOrderService.getOrderingDealerByDocId(docId);
		assertNotNull(ds.getDspId());
	}	

	@Test
	public void testGetQuotationModelWithCapitalCosts() throws Exception {
		Long qmdId = 118355l;
		QuotationModel qm = quotationService.getQuotationModelWithCapitalCosts(qmdId);
		assertNotNull(qm.getQuotationCapitalElements().get(0).getQceId());
	}	
	
	@Test
	public void testGetQuotationModelWithCostAndAccessories() throws Exception {
		Long qmdId = 118355l;
		QuotationModel qm = quotationService.getQuotationModelWithCostAndAccessories(qmdId);
		assertNotNull(qm.getQuotationCapitalElements().get(0).getQceId());
	}	

	@Test
	public void testGetOriginalQuoteModelOnContractLine() throws Exception {
		Long qmdId = 2576l;
		QuotationModel qm = quotationService.getOriginalQuoteModelOnContractLine(qmdId);
		assertNotNull(qm.getQmdId());
	}	

}
