package com.mikealbert.service;

import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.dao.WillowUserDAO;
import com.mikealbert.data.entity.DebitCreditMemoTransaction;
import com.mikealbert.data.vo.DebitCreditTransactionVO;
import com.mikealbert.testing.BaseTest;

public class DebitCreditMemoServiceTest extends BaseTest {
	@Resource DebitCreditMemoService debitCreditMemoService;
	@Resource UserService userService;
	@Resource WillowUserDAO willowUserDAO;

	@Test
	public void testValidateDebitCreditMemoTransaction() throws Exception{
		DebitCreditTransactionVO dcVO = new DebitCreditTransactionVO();
		DebitCreditMemoTransaction debitCreditMemoTransaction = new DebitCreditMemoTransaction();
		
		dcVO.setDebitCreditType("DEBIT");
		dcVO.setTicketNo("TICKET999");
		dcVO.setReason("This is a reason");
		dcVO.setAccountCode("00009160");
		dcVO.setUnitNo("00994283");
		dcVO.setIsClientUnit("Y");
		dcVO.setCategory("FLBILLING");
		dcVO.setAnalysisCode("CE_LTD");
		dcVO.setNetAmount("15.99");
		dcVO.setTaxAmount("");
		dcVO.setTransactionDate("02/15/2017");
		dcVO.setInvoiceNo("INV00632609");
		dcVO.setRentApplicableDate("03/01/17");
		dcVO.setInvoiceNote("This is an invoice note");
		dcVO.setSelectedApprover("styons_c");
		dcVO.setLineDescription("Additional line descripton");
		dcVO.setGlCode("01100001000");
		dcVO.setSubmitter("opitz_k");
		dcVO.setSubmittedDate("03/24/2017");

		try {
			debitCreditMemoTransaction = debitCreditMemoService.convertDebitCreditMemoTransaction(dcVO, false);
			assertTrue("Validation failed", debitCreditMemoService.validateDebitCreditMemoTransaction(debitCreditMemoTransaction));
		} catch(Exception ex) {
			ex.printStackTrace();
		}	


	}

}
