package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.CcAddressDAO;
import com.mikealbert.data.dao.DistDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocLinkDAO;
import com.mikealbert.data.dao.DocTransDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.DoclLinkDAO;
import com.mikealbert.data.dao.DriverAddressDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.ReclaimHeaderDAO;
import com.mikealbert.data.dao.ReclaimLineDAO;
import com.mikealbert.data.entity.CcAddress;
import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocLink;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.DoclLink;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.ReclaimHeaders;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.QuotationServiceImpl;
import com.mikealbert.service.TALTransactionService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.service.vo.TALTransactionVO;
import com.mikealbert.util.MALUtilities;

@Service("invoiceService")
public class InvoiceServiceImpl implements InvoiceService {
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	private static long TAL_WILLOW_USER = 2;
	private static String TAL_REASON_CODE_PURCHASE = "PURCHASE";

	@Resource
	private TALTransactionService talTransactionService;
	@Resource
	private SupplierService supplierService;
	@Resource
	private QuotationModelDAO quotationModelDAO;
	@Resource
	private CcAddressDAO ccAddressDAO;
	@Resource
	private FleetMasterDAO fleetMasterDAO;
	@Resource
	private WillowConfigService willowConfigService;
	@Resource
	private DocTransDAO docTransDAO;
	@Resource
	private DocDAO docDAO;
	@Resource
	private DoclDAO doclDAO;
	@Resource
	private DistDAO distDAO;
	@Resource
	private DocLinkDAO docLinkDAO;
	@Resource
	private	DriverAddressDAO driverAddressDAO;
	@Resource
	private ReclaimLineDAO reclaimLineDAO;
	@Resource
	private ReclaimHeaderDAO reclaimHeaderDAO;
	@Resource
	private DoclLinkDAO	doclLinkDAO;
	@Resource
	private QuotationService quotationService;
	

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean  postInvoice(Doc poOrderdoc, Doc invoiceDoc, String employeeNo, long contextId) throws MalBusinessException, MalException {
		
		//post invoice call
		boolean isPostSuccessfull = docDAO.postInvoice(invoiceDoc.getDocId(), employeeNo);
		if(!isPostSuccessfull){
			throw new MalBusinessException("generic.error.occured.while", new String[] { "posting invoice" });
		}
		
		try {
		
			String orderType = poOrderdoc.getOrderType() != null ? poOrderdoc.getOrderType() : String.valueOf("X");
			if(! orderType.equalsIgnoreCase("T")){	
				String supplier = willowConfigService.getConfigValue("DEFAULT_SUPPLIER");
				String mbsiInvoice = willowConfigService.getConfigValue("MBSI_INVOICE");
				String mbsiInvProc = willowConfigService.getConfigValue("MBSI_INVPROC");
				String mbsiInvRec = willowConfigService.getConfigValue("MBSI_INV_REC");
	
				supplierService.createSupplierProgress(poOrderdoc.getDocId(), mbsiInvoice, employeeNo, poOrderdoc.getDocDate(), new Date(), supplier);
				supplierService.createSupplierProgress(poOrderdoc.getDocId(), mbsiInvProc, employeeNo, new Date(), new Date(), supplier);
				supplierService.createSupplierProgress(poOrderdoc.getDocId(), mbsiInvRec, employeeNo, new Date(), new Date(), supplier);			
			}
			isPostSuccessfull = true;
		} catch (Exception e) {
			isPostSuccessfull = false;
			throw new MalBusinessException("generic.error.occured.while", new String[] { "updaing posting invoice supplier progress" });			
		}
		
		return isPostSuccessfull ;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void postInvoiceTALNotification(Doc poOrderdoc,long contextId) throws MalBusinessException, MalException {

		
		try {			
			
			List<Dist> distList = distDAO.findDistByDocId(poOrderdoc.getDocId());
			FleetMaster fleetMaster = null;
			if (distList.size() > 0) {
				Long fmsId = Long.parseLong(distList.get(0).getCdbCode1());
				if (fmsId != null)
					fleetMaster = fleetMasterDAO.findById(fmsId).orElse(null);
			}
			if (fleetMaster == null) {
				throw new MalBusinessException("Unable to determine fleet master for doc " + poOrderdoc.getDocId());
			}
			
			TALTransactionVO transactionVO = new TALTransactionVO();
			
			transactionVO.setReasonCode(TAL_REASON_CODE_PURCHASE);
			transactionVO.setUserId(TAL_WILLOW_USER);
			transactionVO.setUnitNumber(fleetMaster.getUnitNo());
			transactionVO.setCheckIfExist(true);
			transactionVO.setTxnOriginPGM(POST_INV_TRX_SOURCE);
			
			CcAddress ccAddress= ccAddressDAO.findCcAddress(contextId).get(0);
			
			if (STOCK.equals(poOrderdoc.getVehDesignationCode())) {	
				
					transactionVO.setTransactionCode(TXN_140);	
					transactionVO.setRegionCode(ccAddress.getRegion());
					transactionVO.setCountryCode(ccAddress.getCountry());	
					
					talTransactionService.createTransaction(transactionVO, true);

			} else if (!WHOLESALE.equals(poOrderdoc.getVehDesignationCode())) {				
				String leaseBackConfig = willowConfigService.getConfigValue(PUR_LEASE_BACK_DEALER);
				
				if (poOrderdoc.getSubAccCode() != null && poOrderdoc.getSubAccCode().equals(leaseBackConfig)) {
					transactionVO.setTransactionCode(TXN_525);
				}else{
					transactionVO.setTransactionCode(TXN_155);
				}

				if(FLQUOTE.equals(poOrderdoc.getSourceCode())){					
				
					if (poOrderdoc.getGenericExtId() != null) {						
						QuotationModel quotationModel = quotationModelDAO.findById(poOrderdoc.getGenericExtId()).orElse(null);
						ExternalAccount externalAccount = quotationModel.getQuotation().getExternalAccount();
						if (externalAccount != null) {
							transactionVO.setAccountCode(externalAccount.getExternalAccountPK().getAccountCode());
							transactionVO.setAccountType(externalAccount.getExternalAccountPK().getAccountType());
							transactionVO.setContextId(externalAccount.getExternalAccountPK().getCId());
						}
						
						if(quotationModel.getQuotation().getDrvDrvId() != null){

							transactionVO.setDriverId(quotationModel.getQuotation().getDrvDrvId());	
							transactionVO.setFetchDriverAddress(true);	
							DriverAddress driverAddress = driverAddressDAO.findByDrvIdAndType(quotationModel.getQuotation().getDrvDrvId(), GARAGED);
							if(driverAddress != null && driverAddress.getDefaultInd().equals(MalConstants.FLAG_Y)){
								transactionVO.setRegionCode(driverAddress.getTownCityCode().getRegionCode().getRegionCodesPK().getRegionCode());								
								transactionVO.setCountryCode(driverAddress.getTownCityCode().getCountyCode().getCountyCodesPK().getCountryCode());
							}							
						}
					
					}
				}else if(FLORDER.equals(poOrderdoc.getSourceCode())){
					transactionVO.setRegionCode(ccAddress.getRegion());
					transactionVO.setCountryCode(ccAddress.getCountry());	
				}
			
				if (transactionVO.getRegionCode() == null) {
					throw new MalBusinessException("Unable to determine target state to create TAL transaction for doc " + poOrderdoc.getDocId());
				}
				
				talTransactionService.createTransaction(transactionVO, true);			
			}
			
			
		} catch (Exception ex) {
			logger.error(ex,"Failed to create TAL transaction after post invoice of PO doc "+poOrderdoc.getDocId());
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			if (ex instanceof MalException) {
				throw (MalException) ex;
			}
			throw new MalBusinessException("generic.error.occured.while", new String[] { "posting invoice" }, ex);
		}
	}

	public Map<String, Object> prePostInvoiceValidations(Long invoiceDocId) throws MalBusinessException {
		
		Map<String, Object> resultWithMessage = new HashMap<String, Object>();
		Doc docInvHeader = docDAO.findById(invoiceDocId).orElse(null);
		List<Docl> docl = doclDAO.findByDocId(invoiceDocId);

		if (docl != null && docl.size() > 0) {
			DocLink docLink = docLinkDAO.findByChildDocId(invoiceDocId);
			if (docLink != null) {
				Long parentDocId = docLink.getId().getParentDocId();
				Doc doc = docDAO.findById(parentDocId).orElse(null);
				String orderType = doc.getOrderType() != null ? doc.getOrderType() : String.valueOf("X");

				if ((doc.getSourceCode().equalsIgnoreCase(FLQUOTE) || doc.getSourceCode().equalsIgnoreCase(FLORDER))
						&& (!orderType.equalsIgnoreCase("T"))) {				
						// Check Vehicle Line record in DOCL
						Long modelInvoiceLineCount =	docDAO.getModelInvoiceLineCount(invoiceDocId);						
						if (modelInvoiceLineCount != null && modelInvoiceLineCount.longValue() == 0) {
							resultWithMessage.put(InvoiceEntryService.ERROR_TYPE_BLOCKER, true);
							resultWithMessage.put(InvoiceEntryService.MESSAGE, "Cannot post invoice with no vehicle line");
							return resultWithMessage;
						}
						//Check missing line(s) on the invoice
						Long missingInvoiceLineCount = docDAO.getMissingInvoiceLineCount(invoiceDocId, parentDocId);						
						if (modelInvoiceLineCount != null && missingInvoiceLineCount.longValue() != 0) {
							resultWithMessage.put(InvoiceEntryService.ERROR_TYPE_BLOCKER, true);
							resultWithMessage.put(InvoiceEntryService.MESSAGE, "There are missing line(s) on the invoice");
							return resultWithMessage;
						}
				}
			}
			BigDecimal	invoiceDetailTotal =BigDecimal.ZERO;
			
			// Check Dist Amount mismatch
			for (Docl doclLine : docl) {
				List<Dist> dist = distDAO.findDistByDoclIdAndDoclLineId(invoiceDocId, doclLine.getId().getLineId());
				BigDecimal distAmount = BigDecimal.ZERO;
				for (Dist distLine : dist) {
					BigDecimal distLineAmount = distLine.getAmount() != null ? distLine.getAmount() : BigDecimal.ZERO;
					distAmount = distAmount.add(distLineAmount, CommonCalculations.MC);
				}
				
				distAmount = distAmount  !=  null ? distAmount : BigDecimal.ZERO;
				BigDecimal totalPrice = doclLine.getTotalPrice() != null ? doclLine.getTotalPrice() : BigDecimal.ZERO;
				
				if (totalPrice.compareTo(distAmount) != 0) {
					resultWithMessage.put(InvoiceEntryService.ERROR_TYPE_BLOCKER, true);
					resultWithMessage.put(InvoiceEntryService.MESSAGE, "Distributions on document "+ docInvHeader.getDocNo() + " does not balance. Cannot post");
					return resultWithMessage;
				}
				BigDecimal	currentInvoiceDetailAmount = doclLine.getTotalPrice() != null ? doclLine.getTotalPrice():BigDecimal.ZERO;
				invoiceDetailTotal	= invoiceDetailTotal.add(currentInvoiceDetailAmount,CommonCalculations.MC);
				
			}
			BigDecimal	invoiceValue = docInvHeader.getTotalDocPrice() != null ? docInvHeader.getTotalDocPrice() :BigDecimal.ZERO;
			if(invoiceValue.compareTo(BigDecimal.ZERO)== 0){
				resultWithMessage.put(InvoiceEntryService.ERROR_TYPE_BLOCKER, true);
				resultWithMessage.put(InvoiceEntryService.MESSAGE, "Zero value invoice, cannot post");
				return resultWithMessage;
			}
			if (invoiceValue.compareTo(invoiceDetailTotal) != 0) {
				resultWithMessage.put(InvoiceEntryService.ERROR_TYPE_WARNING, true);
				resultWithMessage
						.put(InvoiceEntryService.MESSAGE,
								"Invoice Details do not match the Invoice Amount. Please correct the data and post.");
				resultWithMessage.put(InvoiceEntryService.INVOICE_HEADER_TOTAL, invoiceValue);
				resultWithMessage.put(InvoiceEntryService.INVOICE_DETAIL_TOTAL, invoiceDetailTotal);
				return resultWithMessage;
			}else{
				resultWithMessage.put(InvoiceEntryService.ERROR_TYPE_WARNING, true);
				resultWithMessage
						.put(InvoiceEntryService.MESSAGE,
								"Post the invoice for payment?");
				resultWithMessage.put(InvoiceEntryService.INVOICE_HEADER_TOTAL, invoiceValue);
				resultWithMessage.put(InvoiceEntryService.INVOICE_DETAIL_TOTAL, invoiceDetailTotal);
				return resultWithMessage;
			}
		}
		
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteInvoice(String poNumber,Long invoiceId) throws MalBusinessException {
		try {
			Doc docInvoiceHeader = null;
		
			if (invoiceId == null) {
				if (!MALUtilities.isEmpty(poNumber)) {
					List<Doc> existingHeaderList = docDAO.findInvoiceHeaderForPoNo(poNumber);
					if (existingHeaderList != null && !existingHeaderList.isEmpty()) {
						if (existingHeaderList.size() == 1) {
							docInvoiceHeader = existingHeaderList.get(0);
						}else{
							return;
						}
					}
				}else{
					return ;
				}
			} else {
				docInvoiceHeader = docDAO.findById(invoiceId).orElse(null);
			}
			// delete invoice data
			Long releasedPoDocId = null;
			
			if (docInvoiceHeader != null) {
				invoiceId	= docInvoiceHeader.getDocId();
				DocLink docLink = docLinkDAO.findByChildDocId(invoiceId);
				if (docLink != null) {
					releasedPoDocId = docLink.getId().getParentDocId();
					docLinkDAO.delete(docLink);
				}
				ReclaimHeaders reclaimHeaders = reclaimHeaderDAO.findByDocId(invoiceId);
				if (reclaimHeaders != null) {
					reclaimHeaderDAO.delete(reclaimHeaders);
				}
				List<DoclLink> doclLinks = doclLinkDAO.findByParentDocIdAndChildDocId(releasedPoDocId, invoiceId);
				if (doclLinks != null) {
					doclLinkDAO.deleteAll(doclLinks);
				}
				List<Docl> docls = docInvoiceHeader.getDocls();
				doclDAO.deleteAll(docls);
				docDAO.delete(docInvoiceHeader);
				// update/reset released PO respective data which was updated
				// when invoice was created
				// delete revised quote data
				Doc releasedPO = docDAO.findById(releasedPoDocId).orElse(null);
				List<Docl> POdocls = releasedPO.getDocls();
				for (Docl docl : POdocls) {
					docl.setQtyInvoice(null);
				}
				doclDAO.saveAll(POdocls);
				releasedPO.setTotalDocTax(BigDecimal.ZERO);
				docDAO.save(releasedPO);
				Long acceptedQmdId = docInvoiceHeader.getGenericExtId();
				if (acceptedQmdId != null) {
					Long revisedQmdId = quotationModelDAO.getRevisedQmd(acceptedQmdId);
					if(revisedQmdId != null){
						quotationModelDAO.deleteQuotationModel(revisedQmdId);
						
					}
				}
			}

		} catch (Exception ex) {
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			throw new MalBusinessException("generic.error.occured.while", new String[] { "deleting invoice" }, ex);
		}

	}

}
