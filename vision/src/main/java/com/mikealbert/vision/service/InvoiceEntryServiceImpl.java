package com.mikealbert.vision.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.common.MalMessage;
import com.mikealbert.data.dao.CalendarYearDAO;
import com.mikealbert.data.dao.CapitalElementDAO;
import com.mikealbert.data.dao.CapitalElementRuleDAO;
import com.mikealbert.data.dao.CreditTermDAO;
import com.mikealbert.data.dao.DistDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocLinkDAO;
import com.mikealbert.data.dao.DocTranGlCodeDAO;
import com.mikealbert.data.dao.DocTransDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.DoclLinkDAO;
import com.mikealbert.data.dao.ExtAccTranTermDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.KeyCodeDAO;
import com.mikealbert.data.dao.KeyTypeCodeDAO;
import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.dao.MalCapitalCostDAO;
import com.mikealbert.data.dao.ModelDAO;
import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.ReclaimHeaderDAO;
import com.mikealbert.data.dao.ReclaimLineDAO;
import com.mikealbert.data.dao.TaxCodeDAO;
import com.mikealbert.data.dao.TimePeriodDAO;
import com.mikealbert.data.dao.WillowEntityDefaultDAO;
import com.mikealbert.data.entity.CalendarYear;
import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.CapitalElementRule;
import com.mikealbert.data.entity.CreditTerm;
import com.mikealbert.data.entity.CreditTermPK;
import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocLink;
import com.mikealbert.data.entity.DocLinkPK;
import com.mikealbert.data.entity.DocTran;
import com.mikealbert.data.entity.DocTranPK;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.DoclLink;
import com.mikealbert.data.entity.DoclLinkPK;
import com.mikealbert.data.entity.DoclPK;
import com.mikealbert.data.entity.ExtAccTranTerm;
import com.mikealbert.data.entity.ExtAccTranTermPK;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.KeyCode;
import com.mikealbert.data.entity.KeyTypeCode;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.MalCapitalCost;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.data.entity.ReclaimHeaders;
import com.mikealbert.data.entity.ReclaimLines;
import com.mikealbert.data.entity.TaxCode;
import com.mikealbert.data.entity.TimePeriod;
import com.mikealbert.data.entity.WillowEntityDefault;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.VinDecoderService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.vo.InvoiceEntryVO;
import com.mikealbert.vision.vo.InvoiceLineVO;

@Service
public class InvoiceEntryServiceImpl implements InvoiceEntryService {
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	@Resource
	private DocDAO docDAO;
	@Resource
	private DoclDAO doclDAO;
	@Resource
	private DistDAO distDAO;
	@Resource
	private FleetMasterDAO fleetMasterDAO;
	@Resource
	private ExternalAccountDAO externalAccountDAO;
	@Resource
	private KeyCodeDAO keyCodeDAO;
	@Resource
	private DocLinkDAO docLinkDAO;
	@Resource
	private KeyTypeCodeDAO keyTypeCodeDAO;
	@Resource
	private WillowEntityDefaultDAO willowEntityDefaultDAO;
	@Resource
	private ExtAccTranTermDAO extAccTranTermDAO;
	@Resource
	private CalendarYearDAO calendarYearDAO;
	@Resource
	private DocTransDAO docTransDAO;
	@Resource
	private TimePeriodDAO timePeriodDAO;
	@Resource
	private CreditTermDAO creditTermDAO;
	@Resource
	private TaxCodeDAO taxCodeDAO;
	@Resource
	private DocTranGlCodeDAO docTranGlCodeDAO;
	@Resource
	private ReclaimLineDAO reclaimLineDAO;
	@Resource
	private ModelDAO modelDAO;
	@Resource
	private WillowConfigService willowConfigService;
	@Resource
	private QuotationCapitalElementDAO quotationCapitalElementDAO;
	@Resource
	private CapitalElementDAO capitalElementDAO;
	@Resource
	private ReclaimHeaderDAO reclaimHeaderDAO;
	@Resource
	private CapitalElementRuleDAO capitalElementRuleDAO;
	@Resource
	private QuotationService quotationService;
	@Resource
	private DoclLinkDAO doclLinkDAO;
	@Resource
	private LeaseElementDAO leaseElementDAO;
	@Resource
	private MalCapitalCostDAO malCapitalCostDAO;
	@Resource
	private	RentalCalculationService	rentalCalculationService;
	@Resource
	private	QuotationModelDAO	quotationModelDAO;
	@Resource 
	VinDecoderService vinDecoderService;
	
	@Value("${chrome.enableService}")
	private String enableChromeService;
	
	@Resource protected MalMessage malMessage;

	public BigDecimal getLatestTax(Long taxId, Date date) throws MalBusinessException {

		TaxCode taxCode = taxCodeDAO.findById(taxId).orElse(null);
		if (taxCode != null) {
			TaxCode resultTaxCode = taxCodeDAO.findByTaxCodeAndMaxEffectiveDate(taxCode.getTaxCode(), date);
			if (resultTaxCode != null) {
				return resultTaxCode.getTaxRate();
			} else {
				throw new MalBusinessException("Unable to determine most uptodate tax record for the this tax code :"
						+ taxCode.getTaxCode());
			}

		} else {
			throw new MalBusinessException("Cannot locate tax code");
		}

	}

	@Transactional(readOnly = true)
	public InvoiceEntryVO getInvoiceEntryHeader(String poNumber) throws MalBusinessException {
		try {
			if (MALUtilities.isEmpty(poNumber)) {
				return null;
			}
			// get doc
			Doc docInvoiceHeader = null;
			Doc docReleasedPo = null;
			InvoiceEntryVO invoiceEntryVO = null;
			// see id doc_links is having record for it. If yes, take
			// child_doc_id as invoiced doc
			List<Doc> existingHeaderList = docDAO.findInvoiceHeaderForPoNo(poNumber);
			if (existingHeaderList != null && !existingHeaderList.isEmpty()) {
				if (existingHeaderList.size() == 1) {
					docInvoiceHeader = existingHeaderList.get(0);
					invoiceEntryVO = new InvoiceEntryVO();
					invoiceEntryVO.setInvoiceNumber(docInvoiceHeader.getDocNo());
					invoiceEntryVO.setInvoiceDocId(docInvoiceHeader.getDocId());
					invoiceEntryVO.setDueDate(docInvoiceHeader.getDueDate());
					invoiceEntryVO.setInvoiceDate(docInvoiceHeader.getDocDate() != null ? docInvoiceHeader.getDocDate() : new Date());
					invoiceEntryVO.setReceivedDate(docInvoiceHeader.getReceivedDate() != null ? docInvoiceHeader.getReceivedDate()
							: new Date());
					invoiceEntryVO.setInvoiceAmount(docInvoiceHeader.getTotalDocPrice());
				} else {
					// may be a error condition, not sure if it is possible
				}
			}

			List<String> sourceCode = new ArrayList<String>();
			sourceCode.add("FLQUOTE");
			sourceCode.add("FLORDER");
			sourceCode.add("FLGRD");
			docReleasedPo = docDAO.findByDocNoAndDocTypeAndSourceCodeAndStatusForInvoiceEntry(poNumber, DOC_TYPE_PORDER, sourceCode, "R");
			if (docReleasedPo != null) {
				if (invoiceEntryVO == null) {
					invoiceEntryVO = new InvoiceEntryVO();
				}
				invoiceEntryVO.setReleasedPODocId(docReleasedPo.getDocId());
				invoiceEntryVO.setPoNumber(docReleasedPo.getDocNo());
				invoiceEntryVO.setPoRevNo(docReleasedPo.getRevNo());

				if (invoiceEntryVO.getInvoiceDate() == null) {
					invoiceEntryVO.setInvoiceDate(new Date());
				}
				if (invoiceEntryVO.getReceivedDate() == null) {
					invoiceEntryVO.setReceivedDate(new Date());
				}
				// check if third party PO
				DocLink docLink = docLinkDAO.findByChildDocId(docReleasedPo.getDocId());
				if (docLink != null) {
					invoiceEntryVO.setThirdPartyPO(true);
				} else {
					invoiceEntryVO.setThirdPartyPO(false);
				}
				// set due date
				Map<String, Object> values = getDueDatePaymentMethodAndDiscDate(docReleasedPo.getCId().longValue(),
						docReleasedPo.getAccountCode(), docReleasedPo.getAccountType(), DOC_TYPE_INVOICEAP,
						docReleasedPo.getUpdateControlCode(), docReleasedPo.getPaymentTermsCode(), docReleasedPo.getCrtExtAccType(),
						invoiceEntryVO.getInvoiceDate(), docReleasedPo.getTpSeqNo(), "Y", docReleasedPo.getPaymentMethod());
				if (values != null && !values.isEmpty()) {
					Date dueDate = values.get(DOC_DUE_DATE) != null ? (Date) values.get(DOC_DUE_DATE) : null;
					String paymentMethod = !MALUtilities.isEmpty(values.get(DOC_PAYMENT_METHOD)) ? (String) values.get(DOC_PAYMENT_METHOD)
							: null;
					String paymentTermCode = !MALUtilities.isEmpty(values.get(DOC_PAYMENT_TERM_CODE)) ? (String) values
							.get(DOC_PAYMENT_TERM_CODE) : null;
					invoiceEntryVO.setDueDate(dueDate);
					invoiceEntryVO.setPaymentMethod(paymentMethod);
					invoiceEntryVO.setPaymentTermCode(paymentTermCode);

				}

				// get dist
				List<Dist> distList = distDAO.findDistByDocId(docReleasedPo.getDocId());
				if (distList != null && !distList.isEmpty()) {
					Dist mainDist = distList.get(0);
					// get fleet AND get model
					Long fmsId = Long.parseLong(mainDist.getCdbCode1());
					if (fmsId != null) {
						FleetMaster fleetMaster = fleetMasterDAO.findById(fmsId).orElse(null);
						invoiceEntryVO.setFmsId(fleetMaster.getFmsId());
						invoiceEntryVO.setUnitNumber(fleetMaster.getUnitNo());
						invoiceEntryVO.setUnitDesc(fleetMaster.getModel().getModelDescription());
						invoiceEntryVO.setVin(fleetMaster.getVin());
						invoiceEntryVO.setMsrp(fleetMaster.getRetailPrice());
						invoiceEntryVO.setShipWeight(fleetMaster.getKerbWeight());
						invoiceEntryVO.setGrossVehicleWeight(fleetMaster.getGrossVehicleWeight());
						// get key code
						List<KeyCode> kcList = keyCodeDAO.findByFmsId(fleetMaster.getFmsId());
						if (kcList != null && !kcList.isEmpty()) {
							for (KeyCode keyCode : kcList) {
								if (KEY_TYPE_IGNITION.equals(keyCode.getKeyTypeCode().getKeyType())) {
									invoiceEntryVO.setKeyCode(keyCode.getKeyCode());
									invoiceEntryVO.setKeyCodeDesc(keyCode.getKeyTypeCode().getDescription());
									invoiceEntryVO.setKeyCodeId(keyCode.getKcdId());
									break;
								}
							}
							invoiceEntryVO.setExistingKeyCodes(kcList);
						}
					}
				}

				// get vendor
				ExternalAccountPK vendorId = new ExternalAccountPK();
				if(docInvoiceHeader != null){
					vendorId.setCId(docInvoiceHeader.getCId().longValue());
					vendorId.setAccountCode(docInvoiceHeader.getAccountCode());
					vendorId.setAccountType(docInvoiceHeader.getAccountType());
				}else{
					vendorId.setCId(docReleasedPo.getCId().longValue());
					vendorId.setAccountCode(docReleasedPo.getAccountCode());
					vendorId.setAccountType(docReleasedPo.getAccountType());
				}
				
				ExternalAccount vendor = externalAccountDAO.findById(vendorId).orElse(null);
				invoiceEntryVO.setVendorCode(vendor.getExternalAccountPK().getAccountCode());
				invoiceEntryVO.setVendorName(vendor.getAccountName());
				invoiceEntryVO.setVendorContextId(docReleasedPo.getCId().longValue());
				return invoiceEntryVO;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
			throw new MalBusinessException("generic.error.occured.while", new String[] { "getting invoice header" }, ex);
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public Map<String, Object> saveInvoiceHeader(InvoiceEntryVO invoiceEntryVO,Long acceptedQmdId) throws MalBusinessException {
		Map<String, Object>	resultMap = new HashMap<String, Object>();
		try {
			if (invoiceEntryVO == null) {
				return null;
			}
			if (MALUtilities.isEmpty(invoiceEntryVO.getInvoiceNumber())) {
				throw new MalBusinessException("required.field", new String[] { "Invoice Number" });
			} else {
				if (invoiceEntryVO.getInvoiceNumber().length() > 25) {
					throw new MalBusinessException("max.length.error", new String[] { "Invoice Number", "25" });
				}
			}
			if (MALUtilities.isEmpty(invoiceEntryVO.getInvoiceAmount())) {
				throw new MalBusinessException("required.field", new String[] { "Invoice Amount" });
			} else {
				if (invoiceEntryVO.getInvoiceAmount().compareTo(BigDecimal.ZERO) == 0) {
					throw new MalBusinessException("err.value.zero.message", new String[] { "Invoice Amount" });
				}
			}

			if (MALUtilities.isEmpty(invoiceEntryVO.getReceivedDate())) {
				throw new MalBusinessException("required.field", new String[] { "Received Date" });
			} else {
				if (invoiceEntryVO.getReceivedDate().compareTo(new Date()) > 0) {
					throw new MalBusinessException("future.date.error", new String[] { "Received Date" });
				}
			}

			if (MALUtilities.isEmpty(invoiceEntryVO.getInvoiceDate())) {
				throw new MalBusinessException("required.field", new String[] { "Invoice Date" });
			} else {
				if (invoiceEntryVO.getInvoiceDate().compareTo(new Date()) > 0) {
					throw new MalBusinessException("future.date.error", new String[] { "Invoice Date" });
				}
			}

			if (MALUtilities.isEmpty(invoiceEntryVO.getDueDate())) {
				throw new MalBusinessException("required.field", new String[] { "Due Date" });
			}

			if (MALUtilities.isEmpty(invoiceEntryVO.getVin()) && !invoiceEntryVO.isThirdPartyPO()) {
				throw new MalBusinessException("required.field", new String[] { "VIN" });
			}
			if (MALUtilities.isEmpty(invoiceEntryVO.getMsrp()) && !invoiceEntryVO.isThirdPartyPO()) {
				throw new MalBusinessException("required.field", new String[] { "MSRP" });
			}

			if (!MALUtilities.isEmpty(invoiceEntryVO.getMsrp()) && !invoiceEntryVO.isThirdPartyPO()) {
				if (invoiceEntryVO.getMsrp().compareTo(BigDecimal.ZERO) == 0) {
					throw new MalBusinessException("err.value.zero.message", new String[] { "MSRP" });
				}

			}
			if (!MALUtilities.isEmpty(invoiceEntryVO.getKeyCode())) {
				if (invoiceEntryVO.getKeyCode().length() > 10) {
					throw new MalBusinessException("max.length.error", new String[] { "Key Code", "10" });
				}
			}
			//For RC-1958
			List<Doc> docTemps = docDAO.findByDocNo(invoiceEntryVO.getInvoiceNumber());
			if (docTemps != null && !docTemps.isEmpty()) {
				String currentDocVendor = invoiceEntryVO.getVendorCode();
				boolean isDuplicateInvNo = false;
				if (docTemps.size() == 1) {
					Doc docTemp = docTemps.get(0);
					String existingDocVendorCode = docTemp.getAccountCode();
					String	existingDocSubVendorCode = docTemp.getSubAccCode();
					existingDocSubVendorCode	= existingDocSubVendorCode != null ? existingDocSubVendorCode :"";
					if (currentDocVendor.equals(existingDocVendorCode) || currentDocVendor.equals(existingDocSubVendorCode)) {
						if (invoiceEntryVO.getInvoiceDocId() == null) {
							isDuplicateInvNo = true;
						} else {
							if (invoiceEntryVO.getInvoiceDocId().longValue() != docTemp.getDocId()) {
								isDuplicateInvNo = true;
							}
						}
					}
				} else {
					for (Doc doc : docTemps) {
						String existingDocVendorCode = doc.getAccountCode();
						String	existingDocSubVendorCode = doc.getSubAccCode();
						existingDocSubVendorCode	= existingDocSubVendorCode != null ? existingDocSubVendorCode :"";
						if (currentDocVendor.equals(existingDocVendorCode) || currentDocVendor.equals(existingDocSubVendorCode)) {
							if (invoiceEntryVO.getInvoiceDocId() == null) {
								isDuplicateInvNo = true;
								break;
							} else {
								if (invoiceEntryVO.getInvoiceDocId().longValue() != doc.getDocId()) {
									isDuplicateInvNo = true;
									break;
								}
							}
						}
					}
				}
				if (isDuplicateInvNo) {
					throw new MalBusinessException("duplicate.record", new String[] { "Invoice Number" });
				}

			}
			
			Doc releasedPODoc = docDAO.findById(invoiceEntryVO.getReleasedPODocId()).orElse(null);
			// insert into doc
			Doc docToSave = null;
			if (invoiceEntryVO.getInvoiceDocId() != null) {
				docToSave = docDAO.findById(invoiceEntryVO.getInvoiceDocId()).orElse(null);
			} else {
				docToSave = new Doc();
				if (releasedPODoc != null) {
					BeanUtils.copyProperties(releasedPODoc, docToSave, new String[] { "docId", "docNo", "dueDate", "docType", "dsDocType",
							"SourceCode", "holdInd", "exchangeRateType,totalDocCost", "crtCId", "crtExtAccType", "pSeqNo", "opCode",
							"origCode", "dists", "docls","docSuppliers","docPropertyValues", "versionts" });
				}

				docToSave.setOpCode(invoiceEntryVO.getOpCode());
				docToSave.setDocType(DOC_TYPE_INVOICEAP);
				docToSave.setDocStatus(INV_DOC_STATUS_OPEN);
				docToSave.setDsDocType(DOC_TYPE_INVOICEAP);
				docToSave.setSourceCode(SOURCE_CODE_POINV);
				docToSave.setHoldInd("N");
				docToSave.setExchangeRateType(EXCHANGE_RATE_FIXED);
				docToSave.setTotalDocCost(null);
				// Payment method is not set yet
				if (invoiceEntryVO.getVendorContextId() != null) {
					ExternalAccountPK vendorId = new ExternalAccountPK();
					vendorId.setCId(invoiceEntryVO.getVendorContextId());
					vendorId.setAccountCode(invoiceEntryVO.getVendorCode());
					vendorId.setAccountType("S");
					ExternalAccount vendor = externalAccountDAO.findById(vendorId).orElse(null);
					if (vendor != null) {
						docToSave.setCrtCId(vendor.getCrtCId());
						docToSave.setCrtExtAccType(vendor.getCrtExtAccType());
					}
					WillowEntityDefault willowEntityDefault = willowEntityDefaultDAO.findByContextId(invoiceEntryVO.getVendorContextId());
					if (willowEntityDefault != null) {
						docToSave.setTpSeqNo(willowEntityDefault.getCurrentTpSeqAp());
					}
				}
			}
			FleetMaster fleetMaster = fleetMasterDAO.findById(invoiceEntryVO.getFmsId()).orElse(null);
			
			if (!invoiceEntryVO.isThirdPartyPO()) {
				// update fleet master
				fleetMaster.setRetailPrice(invoiceEntryVO.getMsrp());
				fleetMaster.setKerbWeight(invoiceEntryVO.getShipWeight());
				fleetMaster.setGrossVehicleWeight(invoiceEntryVO.getGrossVehicleWeight());
				fleetMaster.setVin(invoiceEntryVO.getVin().toUpperCase());
				fleetMaster.setRegDate(invoiceEntryVO.getInvoiceDate());
				
				docToSave.setExternalRefNo(fleetMaster.getRegNo());

				fleetMasterDAO.save(fleetMaster);
				// insert/update key codes
				if (!MALUtilities.isEmpty(invoiceEntryVO.getKeyCode())) {
					boolean isExistingKeyCode = false;
					if (invoiceEntryVO.getExistingKeyCodes() != null) {
						for (KeyCode existKeyCode : invoiceEntryVO.getExistingKeyCodes()) {
							if (invoiceEntryVO.getKeyCode().equals(existKeyCode.getKeyCode())) {
								isExistingKeyCode = true;
								if (!KEY_TYPE_IGNITION.equals(existKeyCode.getKeyTypeCode().getKeyType())) {
									KeyTypeCode newKeyTypeCode = keyTypeCodeDAO.findById(KEY_TYPE_IGNITION).orElse(null);
									if (newKeyTypeCode != null) {
										existKeyCode.setKeyTypeCode(newKeyTypeCode);
										keyCodeDAO.save(existKeyCode);
										break;
									}
								}
							}
						}
					}
					if (!isExistingKeyCode) {
						KeyTypeCode newKeyTypeCode = keyTypeCodeDAO.findById(KEY_TYPE_IGNITION).orElse(null);
						KeyCode newKeyCode = new KeyCode();
						newKeyCode.setKeyTypeCode(newKeyTypeCode);
						newKeyCode.setKeyCode(invoiceEntryVO.getKeyCode());
						newKeyCode.setFmsFmsId(fleetMaster.getFmsId());
						keyCodeDAO.save(newKeyCode);
					}
				} else {
					if (invoiceEntryVO.getKeyCodeId() != null) {
						keyCodeDAO.deleteById(invoiceEntryVO.getKeyCodeId());
					}
				}
			}else{
				if(!MALUtilities.isEmpty(invoiceEntryVO.getUpdatedVendorCode())){
					docToSave.setAccountCode(invoiceEntryVO.getUpdatedVendorCode());
					
				}
			}

			docToSave.setDocNo(invoiceEntryVO.getInvoiceNumber());
			docToSave.setDocDate(invoiceEntryVO.getInvoiceDate());
			docToSave.setReceivedDate(invoiceEntryVO.getReceivedDate());
			docToSave.setDueDate(invoiceEntryVO.getDueDate());
			docToSave.setTotalDocPrice(invoiceEntryVO.getInvoiceAmount());
			docToSave.setPaymentMethod(invoiceEntryVO.getPaymentMethod());
			docToSave.setPaymentTermsCode(invoiceEntryVO.getPaymentTermCode());
			if (docToSave.getTotalDocPrice() == null) {
				docToSave.setUnallocAmount(BigDecimal.ZERO);
			}
			docToSave = docDAO.save(docToSave);
			resultMap.put("INVOICE_HEADER_DOC", docToSave);
			if(!MALUtilities.isEmpty(invoiceEntryVO.getUpdatedVendorCode())){
				if (invoiceEntryVO.isThirdPartyPO()) {
					releasedPODoc.setAccountCode(invoiceEntryVO.getUpdatedVendorCode());
					docDAO.save(releasedPODoc);
				}
				
			}
			// insert into docl
			if (invoiceEntryVO.getInvoiceDocId() == null) {
				DocLink docLink = new DocLink();
				DocLinkPK id = new DocLinkPK();
				id.setParentDocId(invoiceEntryVO.getReleasedPODocId());
				id.setChildDocId(docToSave.getDocId());
				docLink.setId(id);
				docLinkDAO.save(docLink);
			}
			// create invoice line items
			if (invoiceEntryVO.getInvoiceDocId() == null) {
				
				createInvoiceDetails(fleetMaster ,releasedPODoc, docToSave);
				List<ReclaimLines> reclaimLines =  createReclaims(docToSave, fleetMaster, docToSave.getOpCode());
				
				//create finalize quote
				Long finalizeQmd =  null;
				if(acceptedQmdId != null){
					boolean isOnContract = false;
					boolean isStockOrder	= false;
					//for RC-1764/RC-1956
					/*String fleetStatus = quotationModelDAO.getFleetStatus(invoiceEntryVO.getFmsId());
					if(!MALUtilities.isEmpty(fleetStatus)){
						//2= on contract 18= pending live
						if("2".equals(fleetStatus) || "18".equals(fleetStatus)){
							isOnContract	= true;
						}
					}*/
					QuotationModel acceptedQuote = quotationModelDAO.findById(acceptedQmdId).orElse(null);
					if(acceptedQuote != null){
						QuotationModel possibleOnContractQuote	= quotationModelDAO.findByQuoteIdAndStatus(acceptedQuote.getQuotation().getQuoId(), 6);
						if(possibleOnContractQuote != null){
							isOnContract	= true;
						}
					}
					//For RC-1956
					isStockOrder	=	isStockOrder(  acceptedQmdId, releasedPODoc);
					if(! isOnContract && !isStockOrder){
						finalizeQmd = quotationService.createRevisedQuote(acceptedQmdId,  docToSave.getOpCode());
						if(finalizeQmd == null){
							throw new MalBusinessException("generic.error.occured.while", new String[] { "creating revised quote" });
						}
					
						setupMalCapitalCost(docToSave, finalizeQmd, fleetMaster.getFmsId() ,reclaimLines );
					}
					
				}
				
				resultMap.put("REVISED_QMD_ID", finalizeQmd);
					
			}
			return resultMap;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			throw new MalBusinessException("generic.error.occured.while", new String[] { "saving invoice header" }, ex);
		}
		

	}
	
	private boolean isStockOrder( Long targetQmdId,Doc docReleasedPo ){
		//	logger.debug("targetQmdId=="+targetQmdId +"-docReleasedPo-getSourceCode"+docReleasedPo.getSourceCode());
			 if(targetQmdId == null){
				 return true;
			 }
			 if(docReleasedPo != null && docReleasedPo.getSourceCode().equals("FLGRD")){
				 return true;
			 }
			 
			 if(docReleasedPo != null && docReleasedPo.getSourceCode().equals("FLORDER")){
				 return true;
			 }
			 
			 List<Doc> docGRDList = docDAO.findDocForGenericExtIdByDocTypeAndSourceCode(targetQmdId , "PORDER" , "FLGRD");//Stock unit 3rd party PO will have source code as FLQUOTE so looking for FLGRD
			 if(docGRDList != null && docGRDList.size() > 0){
				 return true;
			 }
			return false;	
		 }

	public void setupMalCapitalCost(Doc docToSave ,Long finalizeQmd , Long fmsId ,List<ReclaimLines> reclaimLines ) throws MalBusinessException{
		
		List<MalCapitalCost> malCapitalCostList =  malCapitalCostDAO.findMalCapitalCostByFinalizeQuote(finalizeQmd);;
		QuotationModel finalizedQuotationModel = quotationService.getQuotationModelWithCostAndAccessories(finalizeQmd);
		
		List<MalCapitalCost> newMalCapitalCostList = new ArrayList<MalCapitalCost>();
		List<Docl> invoiceLineItems = doclDAO.findByDocId(docToSave.getDocId());
		for (Docl savedDocl : invoiceLineItems) {
			
			MalCapitalCost malCapitalCost = new MalCapitalCost();
			
			for (MalCapitalCost malCapitalCostInDB : malCapitalCostList) {
				
				if(malCapitalCostInDB.getElementId().longValue() == savedDocl.getGenericExtId().longValue()
						&& malCapitalCostInDB.getElementType().equals(savedDocl.getUserDef4())
						&& malCapitalCostInDB.getDoclDocId() == null){
					
					//malCapitalCost.setMalId(malCapitalCostInDB.getMalId()); 
					malCapitalCost = malCapitalCostInDB;
					break;
				}
			}	
			
			malCapitalCost.setDoclDocId(savedDocl.getId().getDocId());
			malCapitalCost.setDoclLineId(savedDocl.getId().getLineId());
			malCapitalCost.setElementType(savedDocl.getUserDef4());
			malCapitalCost.setElementId(savedDocl.getGenericExtId());
			malCapitalCost.setFmsFmsId(fmsId);
			malCapitalCost.setQmdQmdId(finalizeQmd);
			malCapitalCost.setUnitCost(savedDocl.getUnitCost());
			malCapitalCost.setUnitPrice(savedDocl.getUnitPrice());
			malCapitalCost.setUnitDisc(savedDocl.getUnitDisc());
			malCapitalCost.setTotalPrice(savedDocl.getTotalPrice());						
			malCapitalCost.setOpCode(docToSave.getOpCode());
			for (ReclaimLines reclaimLine : reclaimLines) {
				if(reclaimLine.getReferenceDoclPK().equals(savedDocl.getId())){								
					malCapitalCost.setTotalPrice(reclaimLine.getReclaimAmount().negate());
					malCapitalCost.setUnitCost(malCapitalCost.getTotalPrice());
					malCapitalCost.setUnitPrice(malCapitalCost.getTotalPrice());
				
				}
				
			}
			
			
			for (QuotationDealerAccessory qDealerAccessory : finalizedQuotationModel.getQuotationDealerAccessories()) {
				
				if(qDealerAccessory.getRechargeAmount() != null
						&&	qDealerAccessory.getDealerAccessory().getDacId().longValue() == malCapitalCost.getElementId().longValue()){
					if(malCapitalCost.getUnitCost() != null){
						malCapitalCost.setUnitCost(malCapitalCost.getUnitCost().subtract(qDealerAccessory.getRechargeAmount()));	
					}
					if(malCapitalCost.getUnitPrice() != null){
						malCapitalCost.setUnitPrice(malCapitalCost.getUnitPrice().subtract(qDealerAccessory.getRechargeAmount()));
					}
					if(malCapitalCost.getUnitDisc() != null){
						malCapitalCost.setUnitDisc(malCapitalCost.getUnitDisc().subtract(qDealerAccessory.getRechargeAmount()));
					}
					if(malCapitalCost.getTotalPrice() != null){
						malCapitalCost.setTotalPrice(malCapitalCost.getTotalPrice().subtract(qDealerAccessory.getRechargeAmount()));
					}
				}
				
			}
			
			for (QuotationModelAccessory qModelAccessory : finalizedQuotationModel.getQuotationModelAccessories()) {
				
				if(qModelAccessory.getRechargeAmount() != null
						&&	qModelAccessory.getOptionalAccessory().getOacId().longValue() == malCapitalCost.getElementId().longValue()){
					if(malCapitalCost.getUnitCost() != null){
						malCapitalCost.setUnitCost(malCapitalCost.getUnitCost().subtract(qModelAccessory.getRechargeAmount()));	
					}
					if(malCapitalCost.getUnitPrice() != null){
						malCapitalCost.setUnitPrice(malCapitalCost.getUnitPrice().subtract(qModelAccessory.getRechargeAmount()));
					}
					if(malCapitalCost.getUnitDisc() != null){
						malCapitalCost.setUnitDisc(malCapitalCost.getUnitDisc().subtract(qModelAccessory.getRechargeAmount()));
					}
					if(malCapitalCost.getTotalPrice() != null){
						malCapitalCost.setTotalPrice(malCapitalCost.getTotalPrice().subtract(qModelAccessory.getRechargeAmount()));
					}
				}
				
			}
			
			
			newMalCapitalCostList.add(malCapitalCost);
		}
		
		malCapitalCostDAO.saveAll(newMalCapitalCostList);
		
	}
	
	
	public Map<String, Object> getDueDatePaymentMethodAndDiscDate(Long cId, String accountCode, String accountType, String docType,
			String updateControlCode, String paymentTermsCode, String crtExtAccType, Date docDate, BigDecimal tpSeqNo, String updateInd,
			String paymentMethod) throws MalBusinessException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		try {
			Long eattCrCId = null;
			String eattCrCode = null;
			String eattPmcCode = null;
			String eattExtAccType = null;
			Date tpEndDate = null;
			BigDecimal tpTimePeriodSequence = null;
			CalendarYear tpCalendar = null;
			String tpYear = null;
			Date tpEndDateNext = null;
			BigDecimal cyrsNextYearSeqno = null;
			Date crtDueDiscDate = null;
			Date crtDueDate = null;
			updateInd = updateInd != null ? updateInd : "N";
			if (updateInd.equals("Y")) {
				ExtAccTranTermPK extAccTranTermPK = new ExtAccTranTermPK();
				extAccTranTermPK.setEaCId(cId);
				extAccTranTermPK.setEaAccountCode(accountCode);
				extAccTranTermPK.setEaAccountType(accountType);
				DocTranPK docTranPK = new DocTranPK();
				docTranPK.setCId(cId);
				docTranPK.setDocType(docType);
				docTranPK.setTranType(updateControlCode);
				DocTran docTran = new DocTran();
				docTran.setId(docTranPK);
				extAccTranTermPK.setDocTran(docTran);

				ExtAccTranTerm extAccTranTerm = extAccTranTermDAO.findById(extAccTranTermPK).orElse(null);
				if (extAccTranTerm != null) {
					eattCrCId = extAccTranTerm.getCrCId() != null ? extAccTranTerm.getCrCId().longValue() : null;
					eattCrCode = extAccTranTerm.getCrCode();
					eattPmcCode = extAccTranTerm.getPmcCode();
					eattExtAccType = extAccTranTerm.getCrExtAccType();
				}
			}
			if (MALUtilities.isEmpty(eattCrCode)) {
				DocTranPK docTranPK = new DocTranPK();
				docTranPK.setCId(cId);
				docTranPK.setDocType(docType);
				docTranPK.setTranType(updateControlCode);
				DocTran docTran = docTransDAO.findById(docTranPK).orElse(null);
				if (docTran != null) {
					eattCrCode = docTran.getDefTermsCode();
					eattExtAccType = docTran.getCrtExtAccType();
				}
			}
			ExternalAccountPK externalAccountPK = new ExternalAccountPK();
			externalAccountPK.setCId(cId);
			externalAccountPK.setAccountCode(accountCode);
			externalAccountPK.setAccountType(accountType);
			ExternalAccount externalAccount = externalAccountDAO.findById(externalAccountPK).orElse(null);
			if (MALUtilities.isEmpty(eattCrCode)) {
				if (externalAccount != null) {
					eattCrCode = externalAccount.getCreditTermsCode();
					eattExtAccType = externalAccount.getCrtExtAccType();
				}
			}
			if (MALUtilities.isEmpty(eattPmcCode)) {
				eattPmcCode = externalAccount.getPaymentMethod();
			}
			if (MALUtilities.isEmpty(eattPmcCode)) {
				eattPmcCode = paymentMethod;
			}
			if (MALUtilities.isEmpty(eattCrCode)) {
				eattCrCode = paymentTermsCode;
				eattExtAccType = crtExtAccType;

			}
			TimePeriod timePeriod = null;
			if (tpSeqNo != null) {
				timePeriod = timePeriodDAO.findById(tpSeqNo.longValue()).orElse(null);
			}
			if (timePeriod != null) {
				tpEndDate = timePeriod.getEndDate();
				tpTimePeriodSequence = timePeriod.getTimePeriodSequence();
				tpCalendar = timePeriod.getCalendarYear();
				tpYear = timePeriod.getCalendarYear().getId().getYear();

			} else {
				tpEndDate = new Date();
			}
			Date timePeriodMinEndDate = timePeriodDAO.getMinEndDate(cId, tpCalendar.getId().getCalendar(), tpYear, tpTimePeriodSequence);
			if (timePeriodMinEndDate != null) {
				tpEndDateNext = timePeriodMinEndDate;
			} else {
				BigDecimal calendarYearMinYearSeq = calendarYearDAO.getMinYearSequence(cId, tpCalendar.getId().getCalendar(), tpYear);
				if (calendarYearMinYearSeq != null) {
					cyrsNextYearSeqno = calendarYearMinYearSeq;
				}
				cyrsNextYearSeqno = cyrsNextYearSeqno != null ? cyrsNextYearSeqno : BigDecimal.ZERO;
				if (cyrsNextYearSeqno.compareTo(BigDecimal.ZERO) == 0) {
					tpEndDateNext = new Date();
				} else {
					timePeriodMinEndDate = timePeriodDAO.getMinEndDate(cId, tpCalendar.getId().getCalendar(), tpYear, cyrsNextYearSeqno);
					if (timePeriodMinEndDate != null) {
						tpEndDateNext = timePeriodMinEndDate;
					} else {
						tpEndDateNext = new Date();
					}
				}
			}
			CreditTermPK creditTermPK = new CreditTermPK();
			creditTermPK.setCId(cId);
			creditTermPK.setExtAccType(eattExtAccType);
			creditTermPK.setCreditTermsCode(eattCrCode);
			CreditTerm creditTerm = creditTermDAO.findById(creditTermPK).orElse(null);
			if (creditTerm != null) {
				if (MALUtilities.isEmpty(creditTerm.getAgeOnInvoice())) {
					creditTerm.setAgeOnInvoice("S");
				}
				if (MALUtilities.isEmpty(creditTerm.getCreditDayOfMonth())) {
					creditTerm.setCreditDayOfMonth(new BigDecimal("99"));
				}
				if (MALUtilities.isEmpty(creditTerm.getDiscDays1())) {
					creditTerm.setDiscDays1(BigDecimal.ZERO);
				}
				if (MALUtilities.isEmpty(creditTerm.getCreditNoOfDays())) {
					creditTerm.setCreditNoOfDays(BigDecimal.ZERO);
				}
				if (creditTerm.getAgeOnInvoice().equals("I")) {
					if("10TH OF MO".equals(creditTerm.getId().getCreditTermsCode())){
						Calendar nextMonthCal = Calendar.getInstance(); 
						Date nextMonth = addMonth(docDate, 1);
						nextMonthCal.setTime(nextMonth) ;
						int month= nextMonthCal.get(Calendar.MONTH);
						int year = nextMonthCal.get(Calendar.YEAR);
						Calendar	targetDate = Calendar.getInstance();
						targetDate.set(Calendar.DATE, 10);
						targetDate.set(Calendar.MONTH, month);
						targetDate.set(Calendar.YEAR, year);
						crtDueDate= targetDate.getTime();
					}else{
						crtDueDate = MALUtilities.addDays(docDate, creditTerm.getCreditNoOfDays().intValue());
					}
					
				} else {
					if (creditTerm.getCreditDayOfMonth().equals(new BigDecimal("99"))) {
						crtDueDate = MALUtilities.addDays(tpEndDateNext, creditTerm.getCreditNoOfDays().intValue());
					} else if (creditTerm.getCreditDayOfMonth().equals(new BigDecimal("98"))) {
						Integer valueToSubtract = null;
						Date modifiedDate = addMonth(docDate, 1);
						Integer lastDayOfMonthInWeek = getLastDayOfMonth(modifiedDate);
						if (lastDayOfMonthInWeek.intValue() == 6) {
							valueToSubtract = 0;
						} else if (lastDayOfMonthInWeek.intValue() == 7) {
							valueToSubtract = 1;
						} else {
							valueToSubtract = lastDayOfMonthInWeek + 1 + creditTerm.getCreditNoOfDays().intValue();
						}
						modifiedDate = addMonth(docDate, 1);
						Date lastDateOfMonth = getLastDateOfMonth(modifiedDate);
						crtDueDate = MALUtilities.addDays(lastDateOfMonth, -valueToSubtract);
					} else if (creditTerm.getCreditDayOfMonth().equals(new BigDecimal("97"))) {
						Integer valueToSubtract = null;
						Integer lastDayOfMonthInWeek = getLastDayOfMonth(docDate);
						if (lastDayOfMonthInWeek.intValue() == 6) {
							valueToSubtract = 0;
						} else if (lastDayOfMonthInWeek.intValue() == 7) {
							valueToSubtract = 1;
						} else {
							valueToSubtract = lastDayOfMonthInWeek + 1 + creditTerm.getCreditNoOfDays().intValue();
						}
						Date modifiedDate = addMonth(docDate, 1);
						Date lastDateOfMonth = getLastDateOfMonth(modifiedDate);
						crtDueDate = MALUtilities.addDays(lastDateOfMonth, -valueToSubtract);
					} else if (creditTerm.getCreditDayOfMonth().equals(new BigDecimal("31"))) {
						Date lastDateOfMonth = getLastDateOfMonth(docDate);
						crtDueDate = MALUtilities.addDays(lastDateOfMonth, creditTerm.getCreditNoOfDays().intValue());
					} else {
						crtDueDate = MALUtilities.addDays(tpEndDate, creditTerm.getCreditNoOfDays().intValue());
					}
				}
				// TODO:discount date is pending.It is not required
			} else {
				crtDueDate = docDate;
				crtDueDiscDate = docDate;
			}
			if (updateInd.equals("Y")) {
				paymentMethod = eattPmcCode;
				paymentTermsCode = eattCrCode;
			}
			valueMap.put(DOC_DUE_DATE, crtDueDate);
			valueMap.put(DOC_PAYMENT_METHOD, paymentMethod);
			valueMap.put(DOC_PAYMENT_TERM_CODE, paymentTermsCode);
			return valueMap;

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			throw new MalBusinessException("generic.error.occured.while", new String[] { "getting due date" }, ex);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<ReclaimLines> createReclaims(Doc doc, FleetMaster fleetMaster, String employeeNo) throws MalBusinessException, MalException {

		List<Docl> invoiceLineItems = doclDAO.findByDocId(doc.getDocId());
		if (invoiceLineItems == null) {
			return null;
		}
		
		ReclaimHeaders reclaimHeader = reclaimHeaderDAO.findReclaimHeadersForInvoice(doc.getDocId());
		
		if(reclaimHeader == null){
			
			reclaimHeader = new ReclaimHeaders();	
			reclaimHeader.setRunDate(new Date());
			reclaimHeader.setStatus("L");
			reclaimHeader.setOpCode(employeeNo);
			reclaimHeader.setDocId(doc.getDocId());
			reclaimHeader.setFmsId(fleetMaster.getFmsId());
	
			//reclaimHeaderDAO.save(reclaimHeader);
		}

		boolean isDPO = false;
		List<Long> parentDocIdList = docLinkDAO.findByChildDocIds(Arrays.asList(doc.getDocId()));
		Doc parentDoc = docDAO.findById(parentDocIdList.get(0)).orElse(null); //here we expect only one parent
		if (parentDoc.getGenericExtId() == null && 
				parentDoc.getDocType() != null && parentDoc.getDocType().equals("PORDER")
				&& parentDoc.getSourceCode() != null && parentDoc.getSourceCode().equals("FLORDER") ) {
			isDPO = true;
		}
		
		List<ReclaimLines> newReclaimLines = new ArrayList<ReclaimLines>();
		List<QuotationCapitalElement> quotationCapitalElementList = quotationCapitalElementDAO.findByQmdID(doc.getGenericExtId());
		List<CapitalElement> capitalElementList = capitalElementDAO.findAll();//ne(docl.getGenericExtId());

		for (Docl docl : invoiceLineItems) {
			
			if(! docl.getUserDef4().equals("CAPITAL"))
				continue;
			
			ReclaimLines reclaimLine = null;
			if (isDPO == false) {
				if (isReclaimable(doc.getGenericExtId(), docl.getGenericExtId(), quotationCapitalElementList).equals("R")) {
				
					for (QuotationCapitalElement quotationCapitalElement : quotationCapitalElementList) {
						if (quotationCapitalElement.getCapitalElement().getCelId() == docl.getGenericExtId().longValue()) {

							reclaimLine = new ReclaimLines();
							reclaimLine.setReclaimAmount(quotationCapitalElement.getValue().abs());
							reclaimLine.setClaimAgainst(quotationCapitalElement.getCapitalElement().getReclaimAgainst());
							reclaimLine.setInclude("Y");
							reclaimLine.setQceId(quotationCapitalElement.getQceId());
							reclaimLine.setDoclDocId(null);
							reclaimLine.setDocLineId(null);
							reclaimLine.setReferenceDoclPK(docl.getId());
							reclaimLine.setFleetMaster(fleetMaster);
							//reclaimLine.setRchId(reclaimHeader.getRchId());
							reclaimLine.setReclaimHeaders(reclaimHeader);
						}
					}
				}

			} else if ("R".equals(isDPOReclaimable(fleetMaster , doc , docl.getGenericExtId()))) {

				BigDecimal dpoAmount = BigDecimal.ZERO;
				for (Docl parentDocl : parentDoc.getDocls()) {
					if(parentDocl.getId().getLineId() == docl.getId().getLineId()){
						dpoAmount = parentDocl.getExpFobCost().abs();
					}					
				}
				
				reclaimLine = new ReclaimLines();
				reclaimLine.setReclaimAmount(dpoAmount);				
				reclaimLine.setInclude("Y");
				reclaimLine.setQceId(null);
				reclaimLine.setDoclDocId(docl.getDoc().getDocId());
				reclaimLine.setDocLineId(docl.getId().getLineId());
				reclaimLine.setReferenceDoclPK(docl.getId());
				reclaimLine.setFleetMaster(fleetMaster);
				//reclaimLine.setRchId(reclaimHeader.getRchId());
				reclaimLine.setReclaimHeaders(reclaimHeader);
				
				for (CapitalElement capitalElement : capitalElementList) {
					if(docl.getGenericExtId() == capitalElement.getCelId()){
						reclaimLine.setClaimAgainst(capitalElement.getReclaimAgainst());	
					}
				}

			}
			
			if (reclaimLine != null ){				
				newReclaimLines.add(reclaimLine);
			}
		}

		//reclaimLineDAO.save(newReclaimLines);
		reclaimHeader.setReclaimLines(newReclaimLines);
		reclaimHeaderDAO.save(reclaimHeader); 
		return newReclaimLines;
	}

	private String isDPOReclaimable(FleetMaster fleetMaster,  Doc doc , Long celId) throws MalBusinessException {

		Model model = fleetMaster.getModel();

		String reclaimable = "X";
		String inventoryConfigValue = willowConfigService.getConfigValue("LEASE_ELE_DIRECT_PURCH");
		if (inventoryConfigValue == null) {
			throw new MalBusinessException("willow config entry not found  for LEASE_ELE_DIRECT_PURCH");
		}

		List<LeaseElement>  leaseElementList= leaseElementDAO.findByLeaseElementName(inventoryConfigValue);
		if (leaseElementList == null || leaseElementList.size() != 1) {
			throw new MalBusinessException("willow config entry  is setup wrong for LEASE_ELE_DIRECT_PURCH");
		}

		long lelId = leaseElementList.get(0).getLelId();

		// pull by delivering dealer account
		CapitalElementRule capitalElementRule = capitalElementRuleDAO.findCERuleByCapitalElementAndSupplier(celId, doc.getSubAccCId(),doc.getSubAccType(), doc.getSubAccCode());
		if (capitalElementRule == null) {
			// pull by ordering dealer account
			capitalElementRule = capitalElementRuleDAO.findCERuleByCapitalElementAndSupplier(celId, doc.getEaCId(), doc.getAccountType(),doc.getAccountCode());
		}
		if (capitalElementRule == null) {
			capitalElementRule = capitalElementRuleDAO.findCERuleByCapitalElementAndMake(celId, model.getMake().getMakId());
		}
		if (capitalElementRule == null) {
			capitalElementRule = capitalElementRuleDAO.findCERuleByCapitalElementAndLeaseElement(celId, lelId);
		}

		if (capitalElementRule != null) {
			reclaimable = capitalElementRule.getReclaimable();
		} else {
			if(celId == null){
				return null;
			}
			CapitalElement capitalElement = capitalElementDAO.findById(celId).orElse(null);
			reclaimable = capitalElement.getReclaimable();
		}

		return reclaimable;
	}

	private String isReclaimable(Long qmdId, Long celId, List<QuotationCapitalElement> quotationCapitalElementList) {
		String reclaimable = "X";
		for (QuotationCapitalElement quotationCapitalElement : quotationCapitalElementList) {

			if (quotationCapitalElement.getCapitalElement().getCelId() == celId.longValue()) {
				if (quotationCapitalElement.getReclaimable() != null) {
					reclaimable = quotationCapitalElement.getReclaimable();
				} else if (quotationCapitalElement.getCapitalElementRule() != null
						&& quotationCapitalElement.getCapitalElementRule().getReclaimable() != null) {
					reclaimable = quotationCapitalElement.getCapitalElementRule().getReclaimable();
				} else if (quotationCapitalElement.getCapitalElement().getReclaimable() != null) {
					reclaimable = quotationCapitalElement.getCapitalElement().getReclaimable();
				}

			}

		}
		return reclaimable;
	}


	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean createInvoiceDetails(FleetMaster fleetMaster, Doc releasedPO, Doc invoiceHeader) throws MalBusinessException, MalException {
		try {
			Long taxId;
			BigDecimal taxRate;
			String glCode = null;
			String reclaimable;
			BigDecimal totalPrice;
			String sourceCode;
			
			if (invoiceHeader.getDocls() != null && invoiceHeader.getDocls().size() > 0) {
				throw new MalBusinessException("Lines already exist on invoice document " + invoiceHeader.getDocNo());
			}
			List<Docl> doclList = doclDAO.findByDocId(releasedPO.getDocId());
			if (doclList != null && !doclList.isEmpty()) {
				taxId = doclList.get(0).getTaxId();
				taxId = taxId != null ? taxId : 0L;
				taxRate = doclList.get(0).getTaxRate();
				taxRate = taxRate != null ? taxRate : BigDecimal.ZERO;
				for (Docl docl : doclList) {
					// Check Inspection
					if ("Y".equals(docl.getInspReqInd()) && "Y".equals(docl.getInspStatus())) {
						throw new MalBusinessException("Receipt line found that requires inspection.Doc No :=" + releasedPO.getDocNo());
					}
					// Check if Lines already Invoiced
					BigDecimal	qtyInvoice = docl.getQtyInvoice() != null ? docl.getQtyInvoice() : BigDecimal.ZERO;
					
					if (qtyInvoice.compareTo(BigDecimal.ZERO) > 0) {
						throw new MalBusinessException(
								"This purchase order has already been partially processed. Please use the lines button to complete processing.");
					}
				}
			} else {
				throw new MalBusinessException("Unable to determine the GRN document.Doc No :=" + releasedPO.getDocNo());
			}
			sourceCode = releasedPO.getSourceCode();

			DocTranPK docTranPK = new DocTranPK();
			docTranPK.setCId(invoiceHeader.getCId().longValue());
			docTranPK.setDocType("INVOICEAP");
			docTranPK.setTranType(invoiceHeader.getUpdateControlCode());
			DocTran docTran = docTransDAO.findById(docTranPK).orElse(null);
			if (docTran == null || (docTran.getDisable() != null && !docTran.getDisable().equals("N"))) {
				throw new MalBusinessException("Must Enter Valid Transaction Type for INVOICAP document");
			}

			CreditTermPK creditTermPK = new CreditTermPK();
			creditTermPK.setCId(invoiceHeader.getCId().longValue());
			creditTermPK.setExtAccType("S");
			creditTermPK.setCreditTermsCode(invoiceHeader.getPaymentTermsCode());
			CreditTerm creditTerm = creditTermDAO.findById(creditTermPK).orElse(null);

			if (creditTerm == null) {
				throw new MalBusinessException("Must Enter Valid Credit Terms Code for document");
			}

			glCode = docTranGlCodeDAO.findGlCodeByCidDocTypeTranType(invoiceHeader.getCId().longValue(), invoiceHeader.getDocType(),
					invoiceHeader.getUpdateControlCode());

			if (taxId.intValue() == 0 && taxRate.compareTo(BigDecimal.ZERO) == 0) {
				taxId = null;
				taxRate = null;
			} else {

				taxRate = getLatestTax(taxId, invoiceHeader.getDocDate());
			}

			List<Docl> poLineItems = doclDAO.findByDocId(releasedPO.getDocId());
			if (poLineItems != null) {
				for (Docl docl : poLineItems) {
					BigDecimal expFabCost = docl.getExpFobCost() != null ? docl.getExpFobCost() : BigDecimal.ZERO;
					BigDecimal disc1 = docl.getDisc1() != null ? docl.getDisc1() : BigDecimal.ZERO;
					BigDecimal qtyOrdered = docl.getQtyOrdered() != null ? docl.getQtyOrdered() : BigDecimal.ZERO;
					BigDecimal expr = expFabCost.multiply(disc1, CommonCalculations.MC);
					expr = expr.divide(new BigDecimal("100"), CommonCalculations.MC);
					expr = expFabCost.subtract(expr, CommonCalculations.MC);
					totalPrice = CommonCalculations.getRoundedValue(expr.multiply(qtyOrdered, CommonCalculations.MC), 2);
					reclaimable = null;
					BigDecimal actFobCost = docl.getActFobCost();
					BigDecimal expFobCost = docl.getExpFobCost();
					if (docl.getUserDef4().equals("CAPITAL")) {
						if ("FLORDER".equals(sourceCode)) {
							// logic for fl_po.check_dpo_reclaimable
							reclaimable = isDPOReclaimable(fleetMaster ,releasedPO, docl.getGenericExtId());

						} else {
							// logic for quotation1.check_reclaimable
							List<QuotationCapitalElement> quotationCapitalElementList = quotationCapitalElementDAO
									.findByQmdID(invoiceHeader.getGenericExtId());
							reclaimable = isReclaimable(invoiceHeader.getGenericExtId(), docl.getGenericExtId(),
									quotationCapitalElementList);
						}
						reclaimable = !MALUtilities.isEmpty(reclaimable) ? reclaimable : "N";
						if ("R".equals(reclaimable)) {
							totalPrice = BigDecimal.ZERO;
							actFobCost = BigDecimal.ZERO;
							expFobCost = BigDecimal.ZERO;
						}
					}
					expFobCost	= expFobCost != null ? expFobCost:BigDecimal.ZERO;
					// Determine the line type for the docl
					String lineType = null;
					if (expFobCost.compareTo(BigDecimal.ZERO) >= 0) {
						lineType = "INVOICEAP";
					} else {
						lineType = "CREDITAP";
					}

					Docl invoiceLineItem = new Docl();
					BeanUtils.copyProperties(docl, invoiceLineItem, new String[] { "id", "doc", "dists","fleetLineInd", "versionts" });
					DoclPK id = new DoclPK();
					id.setDocId(invoiceHeader.getDocId());
					id.setLineId(docl.getId().getLineId());
					invoiceLineItem.setId(id);
					invoiceLineItem.setLineStatus("O");
					invoiceLineItem.setLineType(lineType);
					invoiceLineItem.setDoc(invoiceHeader);
					invoiceLineItem.setDocNo(invoiceHeader.getDocNo());
					invoiceLineItem.setDocDate(invoiceHeader.getDocDate());
					invoiceLineItem.setTotalPrice(totalPrice);
					invoiceLineItem.setActFobCost(actFobCost);
					invoiceLineItem.setExpFobCost(expFobCost);
					invoiceLineItem.setUnitCost(CommonCalculations.getRoundedValue(expFobCost, 2));
					invoiceLineItem.setUnitPrice(CommonCalculations.getRoundedValue(expFobCost, 2));
					invoiceLineItem.setUnitTax(CommonCalculations.getRoundedValue(docl.getUnitTax(), 2));
					invoiceLineItem.setTotalCost(CommonCalculations.getRoundedValue(docl.getTotalCost(), 2));
					invoiceLineItem.setQtyInvoice(invoiceLineItem.getQtyOrdered());
					// insert in dist table
					reclaimable = !MALUtilities.isEmpty(reclaimable) ? reclaimable : "N";
					// List<Dist> distList =
					// distDAO.findDistByDoclIdAndDoclLineId(docl.getId().getDocId(),
					// docl.getId().getLineId());
					List<Dist> distList = docl.getDists();
					List<Dist> newDistList = new ArrayList<Dist>();
					if (distList != null) {
						for (Dist dist : distList) {
							BigDecimal distAmount = dist.getAmount();
							if ("R".equals(reclaimable)) {
								distAmount = BigDecimal.ZERO;
							}
							Dist distToInsert = new Dist();
							BeanUtils.copyProperties(dist, distToInsert, new String[] { "disId", "doc", "docl", "versionts" });
							distToInsert.setDoc(invoiceHeader);
							distToInsert.setDocl(invoiceLineItem);
							distToInsert.setAmount(distAmount);
							distToInsert.setGlCode(glCode);
							distToInsert.setCdbCode8(invoiceHeader.getDocType());
							newDistList.add(distToInsert);
						}
					}
					if (invoiceLineItem.getDists() != null) {
						invoiceLineItem.getDists().clear();
						invoiceLineItem.setDists(newDistList);
					} else {
						invoiceLineItem.setDists(newDistList);
					}
					/*if(invoiceHeader.getDocls() == null){
						List<Docl> docls = new ArrayList<Docl>();
						invoiceHeader.setDocls(docls);;
					}
					
					invoiceHeader.getDocls().add(invoiceLineItem);*/
					doclDAO.save(invoiceLineItem);
					
					docl.setQtyInvoice(docl.getQtyOrdered());
					doclDAO.save(docl);
					// insert into docl_Links
					DoclLink doclLinkToInsert = new DoclLink();
					DoclLinkPK doclLinkPK = new DoclLinkPK();
					doclLinkPK.setParentDocId(releasedPO.getDocId());
					doclLinkPK.setParentLineId(docl.getId().getLineId());
					doclLinkPK.setChildDocId(invoiceHeader.getDocId());
					doclLinkPK.setChildLineId(docl.getId().getLineId());
					doclLinkToInsert.setId(doclLinkPK);
					doclLinkDAO.save(doclLinkToInsert);
				}
			}
			BigDecimal totalDocTax = BigDecimal.ZERO;
			BigDecimal totalSales = BigDecimal.ZERO;
			
			//invoiceHeader = docDAO.findOne(invoiceHeader.getDocId());
			List<Docl> invoiceLineItems = doclDAO.findByDocId(invoiceHeader.getDocId());
			// List<Docl> invoiceLineItems = invoiceHeader.getDocls();
			if (invoiceLineItems != null) {
				BigDecimal actFobCost = null;
				BigDecimal qtyChange = null;
				BigDecimal taxRateInv = null;
				BigDecimal itemDim1 = null;
				BigDecimal itemDim2 = null;
				BigDecimal itemDim3 = null;
				BigDecimal BIGDECIMAL_100 = new BigDecimal("100");
				BigDecimal totalPriceInv = null;
				for (Docl doclInv : invoiceLineItems) {
					actFobCost = doclInv.getActFobCost() != null ? doclInv.getActFobCost() : BigDecimal.ZERO;
					qtyChange = doclInv.getQtyChange() != null ? doclInv.getQtyChange() : BigDecimal.ZERO;
					taxRateInv = doclInv.getTaxRate() != null ? doclInv.getTaxRate() : BigDecimal.ZERO;
					itemDim1 = doclInv.getItemDim1() != null ? doclInv.getItemDim1() : BigDecimal.ZERO;
					itemDim2 = doclInv.getItemDim2() != null ? doclInv.getItemDim2() : BigDecimal.ZERO;
					itemDim3 = doclInv.getItemDim3() != null ? doclInv.getItemDim3() : BigDecimal.ZERO;

					BigDecimal expr1 = actFobCost.multiply(qtyChange, CommonCalculations.MC);
					BigDecimal expr2 = taxRateInv.divide(BIGDECIMAL_100);

					BigDecimal expr12 = expr1.multiply(expr2, CommonCalculations.MC);
					BigDecimal expr3 = itemDim1.multiply(qtyChange, CommonCalculations.MC).multiply(expr2, CommonCalculations.MC);
					BigDecimal expr4 = itemDim2.multiply(qtyChange, CommonCalculations.MC).multiply(expr2, CommonCalculations.MC);
					BigDecimal expr5 = itemDim3.multiply(qtyChange, CommonCalculations.MC).multiply(expr2, CommonCalculations.MC);
					totalDocTax = totalDocTax.add(expr12, CommonCalculations.MC).add(expr3, CommonCalculations.MC)
							.add(expr4, CommonCalculations.MC).add(expr5, CommonCalculations.MC);
					totalPriceInv = doclInv.getTotalPrice();
					totalPriceInv = totalPriceInv != null ? totalPriceInv : BigDecimal.ZERO;
					totalSales = totalSales.add(totalPriceInv, CommonCalculations.MC);
				}
				invoiceHeader.setTotalDocTax(totalDocTax);

				BigDecimal totalDocDisc = invoiceHeader.getTotalDocDisc() != null ? invoiceHeader.getTotalDocDisc() : BigDecimal.ZERO;
				BigDecimal totalDocFreight = invoiceHeader.getTotalDocFreight() != null ? invoiceHeader.getTotalDocFreight()
						: BigDecimal.ZERO;
				BigDecimal exprTotDocPrice = totalDocDisc.add(totalDocFreight, CommonCalculations.MC).add(totalDocTax,
						CommonCalculations.MC);
				exprTotDocPrice = totalSales.subtract(exprTotDocPrice, CommonCalculations.MC);
				// No need to override total doc price in invoice header because
				// user has already entered it on UI as invoice amount
				// invoiceHeader.setTotalDocPrice(exprTotDocPrice);
			}
			if (totalDocTax.compareTo(BigDecimal.ZERO) != 0) {
				releasedPO = docDAO.findById(releasedPO.getDocId()).orElse(null);
				releasedPO.setTotalDocTax(totalDocTax);
				docDAO.save(releasedPO);
				
			}
			/*invoiceHeader = docDAO.findById(invoiceHeader.getDocId()).orElse(null);
			invoiceHeader.setTotalDocTax(totalDocTax);
			docDAO.save(invoiceHeader);*/
			
			
			return true;

		} catch (Exception ex) {
			if (ex instanceof MalBusinessException) {
				throw (MalBusinessException) ex;
			}
			if (ex instanceof MalException) {
				throw (MalException) ex;
			}
			throw new MalBusinessException("generic.error.occured.while", new String[] { "saving invoice line items" }, ex);
		}
	}
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateInvoiceLineItems(List<InvoiceLineVO> invoiceLineVOList) throws MalBusinessException {
		try {
			//business validation will go first
			if (invoiceLineVOList != null) {
				List<Docl> docLinesToUpdate = new ArrayList<Docl>();
				List<ReclaimLines> reclaimableLinesToUpdate = new ArrayList<ReclaimLines>();
				for (InvoiceLineVO invoiceLineVO : invoiceLineVOList) {
					DoclPK doclPK =  new DoclPK(invoiceLineVO.getDocId(), invoiceLineVO.getLineId().longValue());
					Docl doclToUpdate = doclDAO.findById(doclPK).orElse(null);
					
					doclToUpdate.setTotalPrice(!invoiceLineVO.isReclaimable() ? invoiceLineVO.getLineCost():BigDecimal.ZERO);
					doclToUpdate.setUnitCost(doclToUpdate.getTotalPrice());
					doclToUpdate.setUnitPrice(doclToUpdate.getTotalPrice());
					doclToUpdate.setActFobCost(doclToUpdate.getTotalPrice());
					List<Dist> distList = doclToUpdate.getDists();//distDAO.findDistByDoclIdAndDoclLineId(doclToSave.getId().getDocId(),doclToSave.getId().getLineId());
					if(distList.size() == 1){// here we are always expecting at most one dist
						Dist dist = distList.get(0);
						dist.setAmount(doclToUpdate.getTotalPrice());
						doclToUpdate.getDists().clear();
						doclToUpdate.getDists().add(dist);
					}
					
					if(invoiceLineVO.isReclaimable()){
						ReclaimLines reclaimLine = reclaimLineDAO.findById(invoiceLineVO.getReclaimLineId()).orElse(null);
						BigDecimal reclaimAmount = invoiceLineVO.getLineCost();
						// reclaims are stored in the reclaim_lines table as a positive amount
						if(reclaimAmount != null) {
							reclaimAmount = reclaimAmount.abs();
						}
						reclaimLine.setReclaimAmount(reclaimAmount == null ? BigDecimal.ZERO : reclaimAmount );
						reclaimableLinesToUpdate.add(reclaimLine);
					}
					
					//if(doclToUpdate.getTotalPrice().compareTo(invoiceLineVO.getLineCost() ) != 0)
						docLinesToUpdate.add(doclToUpdate);
				}
				
				if(reclaimableLinesToUpdate.size() > 0)
					reclaimLineDAO.saveAll(reclaimableLinesToUpdate);
				
				if(docLinesToUpdate.size() > 0)
					doclDAO.saveAll(docLinesToUpdate);
			}
		} catch (Exception ex) {
			throw new MalBusinessException("generic.error.occured.while", new String[] { "saving invoice line items" }, ex);
		}

	}

	public static Date addMonth(Date date, int numberOfMonth)

	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat(MALUtilities.DATE_PATTERN);
		Calendar calendar = MALUtilities.getCalendar(date, 12, 0, 0, 0);
		calendar.add(Calendar.MONTH, numberOfMonth);
		String newDate = dateFormatter.format(calendar.getTime());
		Date newDateObject = MALUtilities.dataBaseDateStringToUtilDate(newDate, MALUtilities.DATE_PATTERN);
		return newDateObject;
	}

	public static Integer getLastDayOfMonth(Date date) {
		Calendar calendar = MALUtilities.getCalendar(date, 12, 0, 0, 0);
		Integer lastDayOnMonthInWeek = calendar.getActualMaximum(Calendar.DAY_OF_WEEK);
		return lastDayOnMonthInWeek;
	}

	public static Date getLastDateOfMonth(Date date) {
		Calendar calendar = MALUtilities.getCalendar(date, 12, 0, 0, 0);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		Date lastDayOfMonth = cal.getTime();
		return lastDayOfMonth;
	}

}
