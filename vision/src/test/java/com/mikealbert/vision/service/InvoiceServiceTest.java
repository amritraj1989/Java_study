package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.vision.service.InvoiceEntryService;
import com.mikealbert.vision.vo.InvoiceEntryVO;

public class InvoiceServiceTest extends BaseTest{
	@Resource
	private InvoiceService	invoiceService;
	
	@Resource private DocDAO	docDAO;
	
	//Long  stockDoc =4501537L; 
	//Long wholesaleDoc= 4507158L;
	
	Long wholesaleDoc= 4506966L; 
	
			
	
	//@Test
	public void testPostInvoiceNotification(){
	
		try {
			Doc doc = docDAO.findById(wholesaleDoc).orElse(null);
			 invoiceService.postInvoiceTALNotification(doc, 1L);
			
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public	void	testDeleteInvoice(){
		try{
			Long invoiceId	= 4508540L;
			invoiceService.deleteInvoice(null,invoiceId);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
