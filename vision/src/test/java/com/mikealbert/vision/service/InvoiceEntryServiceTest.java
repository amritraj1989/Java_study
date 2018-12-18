package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.vo.InvoiceEntryVO;

public class InvoiceEntryServiceTest extends BaseTest{
	@Resource
	private InvoiceEntryService	invoiceEntryService;
	
	@Resource
	private FleetMasterDAO	fleetDAO;
	
	@Resource
	private DocDAO	docDAO;
	String poNumber ="PON00140587"; 
	
	@Test
	public void testGetInvoiceEntryHeader(){
		String poNumber ="PON00146663";
		try {
			InvoiceEntryVO vo = invoiceEntryService.getInvoiceEntryHeader(poNumber) ;
			if(vo != null){
				System.out.println("Unit Number:"+vo.getUnitNumber());
				System.out.println("Due Date:"+vo.getDueDate());
				System.out.println("Third party PO:"+vo.isThirdPartyPO());
				System.out.println("Ship Weight:"+vo.getShipWeight());
				System.out.println("GVW:"+vo.getGrossVehicleWeight());
			}
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testSaveInvoiceEntryHeader(){
		String poNumber ="PON00133467";
		try {
			InvoiceEntryVO vo = invoiceEntryService.getInvoiceEntryHeader(poNumber) ;
			if(vo != null){
				System.out.println("Unit Number:"+vo.getUnitNumber());
				System.out.println("Due Date:"+vo.getDueDate());
				System.out.println("Third party PO:"+vo.isThirdPartyPO());
				System.out.println("MSRP:"+vo.getMsrp());
				vo.setMsrp(new BigDecimal("30000")); //20604
				invoiceEntryService.saveInvoiceHeader(vo,null);
				System.out.println("Saved Successfully");
			}
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testGetDueDate(){
		poNumber = "PON00133467";
		List<String> sourceCode = new ArrayList<String>();
		sourceCode.add("FLQUOTE");
		sourceCode.add("FLORDER");
		sourceCode.add("FLGRD");
		Doc docReleasedPo = docDAO.findByDocNoAndDocTypeAndSourceCodeAndStatusForInvoiceEntry(poNumber, "PORDER", sourceCode, "R");
		if (docReleasedPo != null) {
		Map<String, Object> values;
		try {
			values = invoiceEntryService.getDueDatePaymentMethodAndDiscDate(docReleasedPo.getCId().longValue(),
					docReleasedPo.getAccountCode(), docReleasedPo.getAccountType(), docReleasedPo.getDocType(),
					docReleasedPo.getUpdateControlCode(), docReleasedPo.getPaymentTermsCode(), docReleasedPo.getCrtExtAccType(),
					docReleasedPo.getDocDate(), docReleasedPo.getTpSeqNo(), null, docReleasedPo.getPaymentMethod());
			if(values != null && !values.isEmpty()){
				Date dueDate = values.get(InvoiceEntryService.DOC_DUE_DATE) != null?(Date) values.get(InvoiceEntryService.DOC_DUE_DATE): null;
				System.out.println("Due Date:"+dueDate);
				String paymentMethod = values.get(InvoiceEntryService.DOC_PAYMENT_METHOD) != null?(String) values.get(InvoiceEntryService.DOC_PAYMENT_METHOD): null;
				System.out.println("paymentMethod:"+paymentMethod);
				String paymentTermCode = values.get(InvoiceEntryService.DOC_PAYMENT_TERM_CODE) != null?(String) values.get(InvoiceEntryService.DOC_PAYMENT_TERM_CODE): null;
				System.out.println("paymentTermCode:"+paymentTermCode);
			}
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
		}
	}
/*	@Test
	public void testValidateVIN(){
	
		List<FleetMaster> list = fleetDAO.findAllTemp();
		System.out.println("List size:"+list.size());
		for (FleetMaster fleetMaster : list) {
			if(fleetMaster.getVin() != null){
				Map<String,Object> resultMap = invoiceEntryService.validateVINAndReturnMessage(fleetMaster.getVin(), fleetMaster.getFmsId());
				if(resultMap != null && !resultMap.isEmpty()){
					boolean isFail = MALUtilities.isEmpty(resultMap.get(invoiceEntryService.ERROR_TYPE_BLOCKER)) ? false
							: (Boolean) resultMap.get(invoiceEntryService.ERROR_TYPE_BLOCKER);
					if(isFail){
						System.out.println("Unable to validate VIN:"+fleetMaster.getVin());
					}
				}
			}
		}
		}*/
}
