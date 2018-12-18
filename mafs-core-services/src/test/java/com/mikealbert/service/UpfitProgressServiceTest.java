package com.mikealbert.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.UpfitterProgressDAO;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.UpfitterProgress;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;


public class UpfitProgressServiceTest extends BaseTest{
	@Resource QuotationService quotationService;
	@Resource UpfitProgressService upfitProgressService;
	@Resource UpfitterProgressDAO upfitterProgressDAO;
	@Resource DocDAO docDAO;
	
	static final String QMD_ID_WITH_UPFITS_WITH_LEAD_TIME = 
			"SELECT MAX(qmd.qmd_id) "
            + "    FROM quotation_models qmd "
            + "    WHERE EXISTS ( SELECT 1 "
            + "                       FROM quotation_dealer_accessories qda "
            + "                       WHERE qda.qmd_qmd_id = qmd.qmd_id " 
            + "                           AND (SELECT quotation_report_data.get_accessory_lead_time(qda.qda_id) FROM DUAL) > 0 ) "
            + "        AND qmd.quote_status = 3  "
            + "        AND ROWNUM = 1 ";	
	
	
	static final String QMD_ID_WITHOUT_UPFITS = 
			"SELECT MAX(qmd.qmd_id) "
            + "    FROM quotation_models qmd "
            + "    WHERE NOT EXISTS ( SELECT 1 "
            + "                       FROM quotation_dealer_accessories qda "
            + "                       WHERE qda.qmd_qmd_id = qmd.qmd_id) " 
            + "        AND qmd.quote_status = 3  "
            + "        AND ROWNUM = 1 ";		

	static final String QMD_ID_WITH_UPFITTER_PROGRESS = 
			"SELECT DISTINCT qmd_qmd_id "
            + "    FROM upfitter_progress "
            + "        WHERE ROWNUM = 1 ";	
	
	@Test
	public void testCreateReadUpfitProgressList() throws Exception{
//		Li?
	}
	
	@Ignore
	public void testCreateReadUpfitProgressListWithMissingUpfit() throws Exception{
		List<UpfitterProgress> upfitterProgressList;
		Long qmdId;
		List<DocumentStatus> docStatuses;
		int oldSize, newSize;
		
		docStatuses = new ArrayList<DocumentStatus>();
		docStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN);
		docStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED);		
		
		qmdId = ((BigDecimal)em.createNativeQuery(QMD_ID_WITH_UPFITS_WITH_LEAD_TIME).getSingleResult()).longValue();
		QuotationModel qmd = quotationService.getQuotationModel(qmdId);
		Long mainPoDocID = docDAO.getUnitPurchaseOrderDocIdFromQmdId(qmdId, DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());
		
		upfitterProgressList = upfitProgressService.generateUpfitProgressList(qmdId, qmd.getUnitNo(), docStatuses, mainPoDocID);
		em.clear();
		
		oldSize = upfitterProgressList.size();
		upfitterProgressList = upfitProgressService.saveOrUpdateUpfitterProgress(upfitterProgressList, "JUNIT_TEST", qmdId, qmd.getUnitNo(),mainPoDocID, qmd.getModel().getModelId() );
		em.flush();
		em.clear();
		
		upfitterProgressList = upfitProgressService.generateUpfitProgressList(qmdId, qmd.getUnitNo(), docStatuses, mainPoDocID);		
		upfitterProgressDAO.delete(upfitterProgressList.get(0));
		em.flush();
		em.clear();
		
		upfitterProgressList = upfitProgressService.generateUpfitProgressList(qmdId, qmd.getUnitNo(), docStatuses, mainPoDocID);
		newSize = upfitterProgressList.size();		
		
		assertTrue("Upfitter Progress list was not reconciled with the addition of an upfit PO", oldSize == newSize);

	}	
	
	@Test
	public void testHasUpfitProgressListBeenGenerated() throws Exception{
		Long qmdId;
		boolean isListGenerated;
		Object result;
		List<DocumentStatus> docStatuses;		

		try {
			docStatuses = new ArrayList<DocumentStatus>();
			docStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN);
			docStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED);
			
			result = em.createNativeQuery(QMD_ID_WITH_UPFITTER_PROGRESS).getSingleResult();		
			if(!MALUtilities.isEmpty(result)) {	
				qmdId = ((BigDecimal)result).longValue();
				QuotationModel qmd = quotationService.getQuotationModel(qmdId);
				Long mainPoDocID = docDAO.getUnitPurchaseOrderDocIdFromQmdId(qmdId, DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());
				
				upfitProgressService.generateUpfitProgressList(qmdId, qmd.getUnitNo(), docStatuses, mainPoDocID);
				isListGenerated = upfitProgressService.hasUpfitProgressList(qmdId);

				assertTrue("Did not detect Upfit Progress list", isListGenerated);
			}	
		} catch (NoResultException nre) {
			System.out.println("testHasUpfitProgressListBeenGenerated was skipped due to no UNIT_PROGRESS records");
		}
		
	}
	
	
	@Test
	public void testHasUpfitProgressListNotBeenGenerated() throws Exception {
		Long qmdId;
		boolean isListGenerated;
		List<DocumentStatus> docStatuses;

		docStatuses = new ArrayList<DocumentStatus>();
		docStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_OPEN);
		docStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED);	
		
		qmdId = ((BigDecimal)em.createNativeQuery(QMD_ID_WITHOUT_UPFITS).getSingleResult()).longValue();
		QuotationModel qmd = quotationService.getQuotationModel(qmdId);
		Long mainPoDocID = docDAO.getUnitPurchaseOrderDocIdFromQmdId(qmdId, DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());
		
		upfitProgressService.generateUpfitProgressList(qmdId, qmd.getUnitNo(), docStatuses, mainPoDocID);
		isListGenerated = upfitProgressService.hasUpfitProgressList(qmdId);
		
		assertFalse("Did not detect missing Upfit Progress list", isListGenerated);
		
	}
	
	@Test
	public void testGetVendorList() throws Exception {
		Long qmdId;			
		List<VendorInfoVO> vendorInfoVOs;
		List<DocumentStatus> docStatuses = new ArrayList<DocumentStatus>();
		
		qmdId = ((BigDecimal)em.createNativeQuery(QMD_ID_WITH_UPFITS_WITH_LEAD_TIME).getSingleResult()).longValue();
		
		docStatuses.add(DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED);
		
		QuotationModel qmd = quotationService.getQuotationModel(qmdId);
		Long mainPoDocID = docDAO.getUnitPurchaseOrderDocIdFromQmdId(qmdId, DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());
		
		vendorInfoVOs = upfitProgressService.readonlyVendorList(qmdId, qmd.getUnitNo(), docStatuses, false, true, false, mainPoDocID);
		
		assertTrue("Vendor Info list is not propulated ", vendorInfoVOs.size() > 0);
	}
}

