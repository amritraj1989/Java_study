package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.data.dao.DistDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.DocLinkDAO;
import com.mikealbert.data.dao.DoclDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.ReclaimLineDAO;
import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocLink;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.ReclaimLines;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.QuotationElementService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.InvoiceEntryService;
import com.mikealbert.vision.service.InvoiceService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.InvoiceLineVO;

@Component
@Scope("view")
public class PostInvoiceBean  extends StatefulBaseBean{
	private static final long serialVersionUID = 1L;
	@Resource
	private DocDAO docDAO;
	@Resource
	private DoclDAO doclDAO;
	@Resource
	private DistDAO distDAO;
	@Resource
	private	FleetMasterDAO	fleetMasterDAO;
	@Resource
	private DocLinkDAO	docLinkDAO;	
	@Resource
	private	InvoiceEntryService	invoiceEntryService;
	@Resource
	private	InvoiceService	invoiceService;
	@Resource
	private ExternalAccountDAO	externalAccountDAO;
	
	@Resource
	private QuotationModelDAO	quotationModelDAO;
	@Resource QuotationService quotationService;
	@Resource QuotationElementService quotationElementService;
	
	private Long	poDocId;
	private List<InvoiceLineVO>	lineItems = new ArrayList<InvoiceLineVO>();
	private Doc			invDoc;
	private Long	invoiceDocId;
	private String	poDocNo;
	private boolean totalCost100k = false;
	
	private BigDecimal totalInvoice;
	private String	unitNumber;
	private String	modelDesc;
	private String	quoteInfo;
	private String orderingDealerCode;
	private String orderingDealerName;
	private String deliveringDealerCode;
	private String deliveringDealerName;
	
	
	
	private boolean userConfirmation = false;
	private boolean showWarning = false;
	private String warningMessage;
	private BigDecimal	invoiceHeaderTotal= null;
	private BigDecimal	invoiceDetailTotal= null;
	private boolean		invoiveAmountMismatch = false;
	private	boolean	disableSave = false;
	private	boolean	disablePost = false;
	private boolean	isMainPO = false;
	private QuotationModel quotationModel;
	
	
	
	@Resource
	private ReclaimLineDAO reclaimLineDAO;
	
	@PostConstruct
	public void init(){
		try{
			initializeDataTable(460, 500, new int[] {70,30}); 
			super.openPage();
			if(invoiceDocId != null){
				invDoc	= docDAO.findById(invoiceDocId).orElse(null);
				
				List<Dist> distList = distDAO.findDistByDocId(invoiceDocId);
				if(distList != null && !distList.isEmpty()){
					String fmsIdString = distList.get(0).getCdbCode1();
					if(!MALUtilities.isEmpty(fmsIdString)){
						FleetMaster fleet = fleetMasterDAO.findById(Long.parseLong(fmsIdString)).orElse(null);
						unitNumber	=	fleet.getUnitNo();
						modelDesc	=	fleet.getModel().getModelDescription();
					}
				}
				if(invDoc !=	 null){
					DocLink docLink = docLinkDAO.findByChildDocId(poDocId);
					String accountTypeMainPo = null;
					String accountCodeMainPo	= null;
					Long cIdMainPo	= null;
					Long subcIdMainPo	= null;
					String subAccountTypeMainPo = null;
					String subAccountCodeMainPo = null;
					
					if (docLink != null) {
						// a third party invoice
						Doc mainDoc = docDAO.findById(docLink.getId().getParentDocId()).orElse(null);
						accountTypeMainPo = mainDoc.getAccountType();
						accountCodeMainPo = mainDoc.getAccountCode();
						cIdMainPo = mainDoc.getCId();

						subAccountTypeMainPo = mainDoc.getSubAccType();
						subAccountCodeMainPo = mainDoc.getSubAccCode();
						subcIdMainPo = mainDoc.getSubAccCId() != null? mainDoc.getSubAccCId() :cIdMainPo;
						loadMainInvoiceLinesForThirdParty();
						//invoice in context
						lineItems.addAll(loadInvoiceDetails(invoiceDocId));
						
						
					}else{
						//main invoice
						accountTypeMainPo = invDoc.getAccountType();
						accountCodeMainPo = invDoc.getAccountCode();
						cIdMainPo = invDoc.getCId();
						isMainPO = true;
						subAccountTypeMainPo = invDoc.getSubAccType();
						subAccountCodeMainPo = invDoc.getSubAccCode();
						subcIdMainPo = invDoc.getSubAccCId() != null? invDoc.getSubAccCId() :cIdMainPo;
						lineItems.addAll(loadInvoiceDetails(invoiceDocId));
						//get all 3rd party PO's for RC-1737
						List<InvoiceLineVO> thirdPartyInvoice = getThirdPartyInvoiceLinesForMainInvoice();
						//check if  current PO is not a third party invoice if so don't add again in display list
						
						 for (InvoiceLineVO thirdPartyVO : thirdPartyInvoice) {
							 boolean present = false;
							 for (InvoiceLineVO mainVO : lineItems) {
								if(thirdPartyVO.getDocId().longValue() == mainVO.getDocId().longValue()
										&& thirdPartyVO.getLineId().longValue() == mainVO.getLineId().longValue()){
									
									present = true;
								}
							}
							if(present == false){
								lineItems.add(thirdPartyVO);
							}
						}
					}
					ExternalAccountPK orderingDealerPK = new ExternalAccountPK();
					orderingDealerPK.setCId(cIdMainPo);
					orderingDealerPK.setAccountCode(accountCodeMainPo);
					orderingDealerPK.setAccountType(accountTypeMainPo);
					ExternalAccount orderingDealer = externalAccountDAO.findById(orderingDealerPK).orElse(null);
					if (orderingDealer != null) {
						orderingDealerCode = orderingDealer.getExternalAccountPK().getAccountCode();
						orderingDealerName = orderingDealer.getAccountName();
					}

					ExternalAccountPK deliveringDealerPK = new ExternalAccountPK();
					deliveringDealerPK.setCId(subcIdMainPo);
					deliveringDealerPK.setAccountCode(subAccountCodeMainPo);
					deliveringDealerPK.setAccountType(subAccountTypeMainPo);
					ExternalAccount deliveringDealer = externalAccountDAO.findById(deliveringDealerPK).orElse(null);
					if (deliveringDealer != null) {
						deliveringDealerCode = deliveringDealer.getExternalAccountPK().getAccountCode();
						deliveringDealerName = deliveringDealer.getAccountName();
					}
					totalInvoice	= getCalculatedTotal();
					disablePost =  hasInvoiceBeenPosted(invDoc);
					
				}
			}
		}catch(Exception ex){
			handleException("generic.error", new String[] { "loading" }, ex, "init");
		}
		
	}

	
private	List<InvoiceLineVO> loadInvoiceDetails(Long invoiceDocId) throws MalBusinessException{
		
		List<Docl>	invoiveLineItems = doclDAO.findByDocId(invoiceDocId);
		List<InvoiceLineVO> lineItems	= new ArrayList<InvoiceLineVO>();
		if(invoiveLineItems != null && !invoiveLineItems.isEmpty()){
			List<Docl> sortedLineList =  prepareSortedList(invoiveLineItems) ;
			lineItems	= new ArrayList<InvoiceLineVO>();
			InvoiceLineVO invoiceLineVO = null;
			for (Docl invDocl : sortedLineList) {
				invoiceLineVO	= new InvoiceLineVO();
				invoiceLineVO.setEnableEdit(true);
				invoiceLineVO.setDocId(invDocl.getId().getDocId());
				invoiceLineVO.setLineId(invDocl.getId().getLineId());
				invoiceLineVO.setLineDescription(invDocl.getLineDescription());
				invoiceLineVO.setLineCost(invDocl.getTotalPrice());
				lineItems.add(invoiceLineVO);
			}
		}
		
		 for (InvoiceLineVO	lineItemVO : lineItems) {
			// In case of stock we will always have doc and docl id populated in reclaim line table
			 List<ReclaimLines>  reclaimLinesList = reclaimLineDAO.findReclaimbleLines(lineItemVO.getDocId(), lineItemVO.getLineId());
			 if(reclaimLinesList.size() == 1){// here we are always expecting at most one reclaim line
				 lineItemVO.setLineCost(reclaimLinesList.get(0).getReclaimAmount().negate());
				 lineItemVO.setReclaimable(true);
				 lineItemVO.setReclaimLineId(reclaimLinesList.get(0).getRclId());
			 }
		}
		return lineItems;
		
		
	}

	Comparator<Docl> doclComparator = new Comparator<Docl>() {
		public int compare(Docl r1, Docl r2) {
			
			String desc1 = r1.getLineDescription();
			String desc2 = r2.getLineDescription();
			if(desc1 == null && desc2 == null){
				return 0;
			}
			if(desc1 == null ){
				return -1;
			}
			if(desc2 == null ){
				return 1;
			}
			return desc1.compareTo(desc2);
		}
	};
	
	private List<Docl> prepareSortedList(List<Docl> list) {
		List<Docl> modelList = new ArrayList<Docl>();
		List<Docl> factoryList = new ArrayList<Docl>();
		List<Docl> dealerList = new ArrayList<Docl>();
		List<Docl> capitalList = new ArrayList<Docl>();
		List<Docl> finalList = new ArrayList<Docl>();
		for (Docl docl : list) {
			if ("MODEL".equals(docl.getUserDef4())) {
				modelList.add(docl);
			} else if ("FACTORY".equals(docl.getUserDef4())) {
				factoryList.add(docl);
			} else if ("DEALER".equals(docl.getUserDef4())) {
				dealerList.add(docl);
			} else if ("CAPITAL".equals(docl.getUserDef4())) {
				capitalList.add(docl);
			}
		}
		// do sorting
		Collections.sort(modelList, doclComparator);
		Collections.sort(factoryList, doclComparator);
		Collections.sort(dealerList, doclComparator);
		Collections.sort(capitalList, doclComparator);

		finalList.addAll(modelList);
		finalList.addAll(factoryList);
		finalList.addAll(dealerList);
		finalList.addAll(capitalList);
		return finalList;

	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Post Invoice");
		thisPage.setPageUrl(ViewConstants.POST_INVOICE);
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_ID) != null) {
			poDocId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_ID);
		}
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_INVOICE_HEADER_DOC_ID) != null) {
			invoiceDocId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_INVOICE_HEADER_DOC_ID);
			
		}
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID) != null) {
			Long	targetQmdId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);
			quotationModel = quotationModelDAO.findById(targetQmdId).orElse(null);
			
			quoteInfo = Long.toString(quotationModel.getQuotation().getQuoId()) + "/"
					+ Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo());	
		}else{
			quoteInfo = "N/A";
		}
		
		if(invoiceDocId == null){
			String docId = getRequestParameter("docNo");
			if (docId != null) {
				invoiceDocId	= Long.parseLong(docId);
			}
		}
	}


	@Override
	protected void restoreOldPage() {
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_ID) != null) {
			poDocId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_RELEASED_PO_DOC_ID);
		}
		
	}

	private BigDecimal getCalculatedTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (InvoiceLineVO line : lineItems) {
				if (line.getLineCost() != null) {
					total = total.add(line.getLineCost(), CommonCalculations.MC);
			}
		}
		return total;
	}
		
	
	public String	cancel(){
		return super.cancelPage();
	}
	
	public boolean	save(){
		try{
			showWarning = false;
			warningMessage = null;
			invoiveAmountMismatch = false;
			userConfirmation	= false;
			//validation is pending
			invoiceEntryService.updateInvoiceLineItems(lineItems);

			return true;
		}catch(Exception ex){
			handleException("generic.error", new String[] { "loading" }, ex, "init");
			logger.error(ex);
			return false;
		}
		
	}
	
	private List<InvoiceLineVO> getThirdPartyInvoiceLinesForMainInvoice() throws MalBusinessException {
		List<InvoiceLineVO> list =  new ArrayList<InvoiceLineVO>();
		List<DocLink> thirdPartyDocLinks = docLinkDAO.findByParentDocId(poDocId);
		if (thirdPartyDocLinks != null) {
			for (DocLink docLinkChild : thirdPartyDocLinks) {
				Doc tempDoc = docDAO.findById(docLinkChild.getId().getChildDocId()).orElse(null);
				if ("T".equals(tempDoc.getOrderType())) {
					List<DocLink> thirdPartyDocLinksInv = docLinkDAO.findByParentDocId(tempDoc.getDocId());
					for (DocLink docLink : thirdPartyDocLinksInv) {
						Doc tempDoc1 = docDAO.findById(docLink.getId().getChildDocId()).orElse(null);
						list = loadInvoiceDetails(tempDoc1.getDocId());
						for (InvoiceLineVO invoiceLineVO : list) {
							invoiceLineVO.setEnableEdit(false);
						}
					}
					
				}

			}
		}
		
		return list;
	}
	
	private void loadMainInvoiceLinesForThirdParty() throws MalBusinessException {
		DocLink	mainDocLink	= docLinkDAO.findByChildDocId(poDocId);
		List<DocLink> docLinks = docLinkDAO.findByParentDocId(mainDocLink.getId().getParentDocId());
		if (docLinks != null) {
			for (DocLink docLinkChild : docLinks) {
				Doc tempDoc = docDAO.findById(docLinkChild.getId().getChildDocId()).orElse(null);
				if ("M".equals(tempDoc.getOrderType())) {
					List<InvoiceLineVO> list = loadInvoiceDetails(tempDoc.getDocId());
					for (InvoiceLineVO invoiceLineVO : list) {
						invoiceLineVO.setEnableEdit(false);
					}
					lineItems.addAll(list);
				}

			}
		}
	}
	
	public	String post(){
		
		boolean success = true;
		
		try{
			totalInvoice	= getCalculatedTotal();
			success =  save();
		}catch(Exception ex){
			success = false;
			handleException("generic.error", new String[] { "saving" }, ex, "init");
			logger.error(ex);
		}
		
		try{
			if(success) 
				postInvoice();
		}catch(Exception ex){
			success = false;
			handleException("generic.error", new String[] { "post invoice" }, ex, "post");
			logger.error(ex);
		}
		invDoc	= docDAO.findById(invoiceDocId).orElse(null);
		disablePost =  hasInvoiceBeenPosted(invDoc);
		return null;
	}
	
	public	boolean  postInvoice() throws MalBusinessException{
		
			if (invoiceDocId== null) {
				return false;
			}
			Doc invoiceDoc = docDAO.findById(invoiceDocId).orElse(null);
			DocLink docLink = docLinkDAO.findByChildDocId(invoiceDoc.getDocId());
			Doc poOrderdoc = null;
			if (!MALUtilities.isEmpty(docLink)) {
				Long parentDocId = docLink.getId().getParentDocId();			
				poOrderdoc = docDAO.findById(parentDocId).orElse(null);
			}
			
			showWarning = false;
			warningMessage = null;
			invoiveAmountMismatch = false;
			if (!userConfirmation) {
				Map<String, Object> validationResultMap = invoiceService.prePostInvoiceValidations(invoiceDoc.getDocId());
				if (validationResultMap != null && !validationResultMap.isEmpty()) {
					boolean isFail = MALUtilities.isEmpty(validationResultMap.get(InvoiceEntryService.ERROR_TYPE_BLOCKER)) ? false
							: (Boolean) validationResultMap.get(InvoiceEntryService.ERROR_TYPE_BLOCKER);
					boolean isWarning = MALUtilities.isEmpty(validationResultMap.get(InvoiceEntryService.ERROR_TYPE_WARNING)) ? false
							: (Boolean) validationResultMap.get(InvoiceEntryService.ERROR_TYPE_WARNING);
					String errorMessage = MALUtilities.isEmpty(validationResultMap.get(InvoiceEntryService.MESSAGE)) ? null
							: (String) validationResultMap.get(InvoiceEntryService.MESSAGE);
					System.out.println("Error Message:" + errorMessage);
					if (isFail) {
						addSimplErrorMessage(errorMessage);
						return false;
					}
					if (isWarning) {
						warningMessage = errorMessage;
						showWarning = true;
						invoiceHeaderTotal	= (BigDecimal)validationResultMap.get(InvoiceEntryService.INVOICE_HEADER_TOTAL);
						invoiceDetailTotal= (BigDecimal)validationResultMap.get(InvoiceEntryService.INVOICE_DETAIL_TOTAL);
						if(invoiceDetailTotal.compareTo(invoiceHeaderTotal) != 0){
							invoiveAmountMismatch	= true;
						}
						return false;
					}
				}
			}
			boolean success = invoiceService.postInvoice(poOrderdoc, invoiceDoc , getLoggedInUser().getEmployeeNo(), getLoggedInUser().getCorporateEntity().getCorpId());
			
			// Update CDFEE in case posted after unit on contract
			// Quotation Model check is for stock where there is no quote associated with the unit. 
			if(success && quotationModel != null){
				BigDecimal origCdFeeCost = docDAO.getCdFeeUnitCost(poDocId);
				if(quotationModel.getQuoteStatus() == 6 && origCdFeeCost != null ){
					try{
						quotationElementService.updateCdfeeDifferenceInCapitalCost(quotationModel, origCdFeeCost);
					}catch (MalBusinessException e) {
						handleException("generic.error.occured.while", new String[] { "updating quotation elements" },e,null);
						logger.error(e, "Error in updating quotation elements.");
					}
				}
			}
			
			addSuccessMessage("process.success", "Post Invoice");
			String orderType = poOrderdoc.getOrderType() != null ? poOrderdoc.getOrderType() : String.valueOf("X");
			if( success && ! orderType.equalsIgnoreCase("T")){	
				invoiceService.postInvoiceTALNotification(poOrderdoc , getLoggedInUser().getCorporateEntity().getCorpId());	
			}
			//	addSuccessMessage("custom.message", "TAL transaction created successfully");
			
			return success;
	}
	
	public String postOnConfirm() throws MalBusinessException {
		
		boolean success = true;
		try{
			userConfirmation = true;			 
			success = postInvoice();
		}catch(Exception ex){
			success = false;
			handleException("generic.error", new String[] { "post invoice" }, ex, "init");
			logger.error(ex);
		}
		
		if(success)
			return ViewConstants.UNIT_RECONCILIATION;
		else
			return null;
		
	}
	
	public	void	holdPosting(){
		userConfirmation	= false;
		showWarning = false;
		warningMessage = null;
		invoiveAmountMismatch = false;
	}
	
	

	public String getPoDocNo() {
		return poDocNo;
	}

	public void setPoDocNo(String poDocNo) {
		this.poDocNo = poDocNo;
	}

	public boolean isTotalCost100k() {
		return totalCost100k;
	}

	public void setTotalCost100k(boolean totalCost100k) {
		this.totalCost100k = totalCost100k;
	}

	public List<InvoiceLineVO> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<InvoiceLineVO> lineItems) {
		this.lineItems = lineItems;
	}

	

	public BigDecimal getTotalInvoice() {
		return totalInvoice;
	}

	public void setTotalInvoice(BigDecimal totalInvoice) {
		this.totalInvoice = totalInvoice;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public String getOrderingDealerCode() {
		return orderingDealerCode;
	}

	public void setOrderingDealerCode(String orderingDealerCode) {
		this.orderingDealerCode = orderingDealerCode;
	}

	public String getOrderingDealerName() {
		return orderingDealerName;
	}

	public void setOrderingDealerName(String orderingDealerName) {
		this.orderingDealerName = orderingDealerName;
	}

	public String getDeliveringDealerCode() {
		return deliveringDealerCode;
	}

	public void setDeliveringDealerCode(String deliveringDealerCode) {
		this.deliveringDealerCode = deliveringDealerCode;
	}

	public String getDeliveringDealerName() {
		return deliveringDealerName;
	}

	public void setDeliveringDealerName(String deliveringDealerName) {
		this.deliveringDealerName = deliveringDealerName;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}
	public boolean isShowWarning() {
		return showWarning;
	}
	public void setShowWarning(boolean showWarning) {
		this.showWarning = showWarning;
	}
	public String getWarningMessage() {
		return warningMessage;
	}
	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}
	public BigDecimal getInvoiceHeaderTotal() {
		return invoiceHeaderTotal;
	}
	public void setInvoiceHeaderTotal(BigDecimal invoiceHeaderTotal) {
		this.invoiceHeaderTotal = invoiceHeaderTotal;
	}
	public BigDecimal getInvoiceDetailTotal() {
		return invoiceDetailTotal;
	}
	public void setInvoiceDetailTotal(BigDecimal invoiceDetailTotal) {
		this.invoiceDetailTotal = invoiceDetailTotal;
	}
	public boolean isInvoiveAmountMismatch() {
		return invoiveAmountMismatch;
	}
	public void setInvoiveAmountMismatch(boolean invoiveAmountMismatch) {
		this.invoiveAmountMismatch = invoiveAmountMismatch;
	}
	public boolean isDisableSave() {
		return disableSave;
	}
	public void setDisableSave(boolean disableSave) {
		this.disableSave = disableSave;
	}
	public boolean isDisablePost() {
		return disablePost;
	}
	public void setDisablePost(boolean disablePost) {
		this.disablePost = disablePost;
	}
	
	public String getQuoteInfo() {
		return quoteInfo;
	}


	public void setQuoteInfo(String quoteInfo) {
		this.quoteInfo = quoteInfo;
	}

	private boolean hasInvoiceBeenPosted(Doc invoice) {
	
		if(invoice != null && invoice.getDocStatus().equalsIgnoreCase("O")) {
			return false;
		} else {
			return true;
		}

	}

}