package com.mikealbert.service; 

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.ContactDAO;
import com.mikealbert.data.dao.ExtAccConsultantDAO;
import com.mikealbert.data.dao.ExtAccFinFanDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.OnbaseUploadedDocsDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.QuoteRequestActivityDAO;
import com.mikealbert.data.dao.QuoteRequestActivityTypeDAO;
import com.mikealbert.data.dao.QuoteRequestCloseReasonDAO;
import com.mikealbert.data.dao.QuoteRequestConfigurationDAO;
import com.mikealbert.data.dao.QuoteRequestDAO;
import com.mikealbert.data.dao.QuoteRequestStatusDAO;
import com.mikealbert.data.dao.QuoteRequestTypeDAO;
import com.mikealbert.data.dao.SupplierProgressHistoryDAO;
import com.mikealbert.data.dao.VehicleAcquisitionTypeDAO;
import com.mikealbert.data.dao.VehicleConfigurationDAO;
import com.mikealbert.data.dao.VehicleDeliveryChargeTypeDAO;
import com.mikealbert.data.dao.WillowUserDAO;
import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.data.entity.ClientPointAccount;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DriverGradeGroupCode;
import com.mikealbert.data.entity.ExtAccConsultant;
import com.mikealbert.data.entity.ExtAccFinFan;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.data.entity.QuoteRequestActivity;
import com.mikealbert.data.entity.QuoteRequestCloseReason;
import com.mikealbert.data.entity.QuoteRequestConfiguration;
import com.mikealbert.data.entity.QuoteRequestDepreciationMethod;
import com.mikealbert.data.entity.QuoteRequestPaymentType;
import com.mikealbert.data.entity.QuoteRequestQuote;
import com.mikealbert.data.entity.QuoteRequestStatus;
import com.mikealbert.data.entity.QuoteRequestType;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.entity.QuoteRequestVehicle;
import com.mikealbert.data.entity.VehicleAcquisitionType;
import com.mikealbert.data.entity.VehicleConfiguration;
import com.mikealbert.data.entity.VehicleDeliveryChargeType;
import com.mikealbert.data.enumeration.AcquisitionTypeEnum;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DocumentNameEnum;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.QuoteRequestActivityTypeEnum;
import com.mikealbert.data.enumeration.QuoteRequestPaymentTypeEnum;
import com.mikealbert.data.enumeration.QuoteRequestStatusEnum;
import com.mikealbert.data.enumeration.QuoteRequestTypeEnum;
import com.mikealbert.data.vo.ClientPocVO;
import com.mikealbert.data.vo.ClientQuoteRequestServiceElementVO;
import com.mikealbert.data.vo.PointOfCommunicationVO;
import com.mikealbert.data.vo.QuoteRequestQuoteModelVO;
import com.mikealbert.data.vo.QuoteRequestVO;
import com.mikealbert.data.vo.WillowUserLovVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.enumeration.OnbaseIndexEnum;
import com.mikealbert.service.vo.OnbaseKeywordVO;
import com.mikealbert.service.vo.StockUnitVO;
import com.mikealbert.util.MALUtilities;


@Service("quoteRequestService")
public class QuoteRequestServiceImpl implements QuoteRequestService {
	
	@Resource private WillowUserDAO willowUserDAO; 
	@Resource private CustomerAccountService customerAccountService;
	@Resource private VehicleConfigurationDAO vehicleConfigurationDAO;
	@Resource private ClientPOCService clientPOCService;
	@Resource private QuoteRequestDAO quoteRequestDAO;
	@Resource private QuoteRequestStatusDAO quoteRequestStatusDAO;
	@Resource private QuoteRequestTypeDAO quoteRequestTypeDAO;
	@Resource private VehicleAcquisitionTypeDAO vehicleAcquisitionTypeDAO;	
	@Resource private DriverGradeService driverGradeService;
	@Resource private ServiceElementService serviceElementService;
	@Resource private VehicleDeliveryChargeTypeDAO vehicleDeliveryChargeTypeDAO;
	@Resource private FleetMasterService fleetMasterService;
	@Resource private DocumentService documentService;
	@Resource private SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	@Resource private ExtAccFinFanDAO extAccFinFanDAO;
	@Resource private WillowConfigService willowConfigService;
	@Resource private ContactDAO contactDAO;
	@Resource private QuoteRequestCloseReasonDAO quoteRequestCloseReasonDAO;
	@Resource private OnbaseUploadedDocsDAO onbaseUploadedDocsDAO;
	@Resource private OnbaseArchivalService onbaseArchivalService;
	@Resource private QuotationModelDAO quotationModelDAO;
	@Resource private LogBookService logBookService;
	@Resource private QuoteRequestActivityTypeDAO quoteRequestActivityTypeDAO;
	@Resource private QuoteRequestActivityDAO quoteRequestActivityDAO;
	@Resource private ExtAccConsultantDAO extAccConsultantDAO;
	@Resource private QuoteRequestConfigurationDAO quoteRequestConfigurationDAO;
	@Resource private FleetMasterDAO fleetMasterDAO;
		
	private static final String ONBASE_OBJECT_TYPE_QUOTE_REQUEST_VEHICLE = "QUOTE_REQUEST_VEHICLES";
	private static final String REWORK_REASON_PREFIX = "Request Rework - ";
	
	public List<WillowUserLovVO> getWillowUsers(String willowUserNo, String willowUserName, String lovName, Pageable pageable) {
		try {
			return willowUserDAO.getWillowUserList(willowUserNo, willowUserName, lovName, pageable); 
		} catch (Exception ex) {
			if (ex instanceof MalException) {
				throw (MalException) ex;
			} else {
				throw new MalException("generic.error.occured.while", new String[] { "getting Willow Users" }, ex);
			}
		}
	}
	
	public int getWillowUsersCount(String willowUserNo, String willowUserName, String lovName) {
		try {
			return willowUserDAO.getWillowUserListCount(willowUserNo, willowUserName, lovName); 
		} catch (Exception ex) {
			if (ex instanceof MalException) {
				throw (MalException) ex;
			} else {
				throw new MalException("generic.error.occured.while", new String[] { "getting Willow Users count" }, ex);
			}
		}
	}
	
	public QuoteRequestVO getClientDetails(QuoteRequestVO quoteRequestVO, ExternalAccountPK externalAccountPK) {
		
		ExternalAccount client = customerAccountService.getCustomerAccount(CorporateEntity.fromCorpId(externalAccountPK.getCId()), externalAccountPK.getAccountType(), externalAccountPK.getAccountCode());
		quoteRequestVO.setClient(client);
		
		quoteRequestVO.setChildAccounts(customerAccountService.getChildAccounts(client));
		
		List<VehicleConfiguration> vehicleConfigList = vehicleConfigurationDAO.getActiveVehicleConfigurationsByAccount(externalAccountPK);
		
		List<String> strList = new ArrayList<String>();
		
		for (VehicleConfiguration vehicleConfiguration : vehicleConfigList) {
			strList.add(vehicleConfiguration.getDescription());
		}
		
		quoteRequestVO.setClientConfigurations(strList);
		
		quoteRequestVO.setContacts(contactDAO.getContactsByAccountInfo(externalAccountPK.getcId(), externalAccountPK.getAccountType(), externalAccountPK.getAccountCode()));
		
		quoteRequestVO.setDriverGradeGroups(driverGradeService.getExternalAccountDriverGrades(client));
		
		quoteRequestVO.setClientPrefSuppliers(quoteRequestDAO.getClientPrefSupplier(client));
		
		quoteRequestVO.setClientFleetCodes(quoteRequestDAO.getClientFleetCodes(client));
		
		quoteRequestVO.setClientConsultants(quoteRequestDAO.getClientConsultants(client));
		
		quoteRequestVO.setClientServiceElements(getServiceElements(client));
		
		quoteRequestVO.setClientCreditLimits(quoteRequestDAO.getClientCreditLimits(client));
		
		quoteRequestVO.setClientProfiles(quoteRequestDAO.getClientProfiles(client));
		
		return quoteRequestVO;
	}
	
	@Override
	@Transactional
	public QuoteRequestVO getQuoteRequestVO(Long qrqId) {
		QuoteRequestVO quoteRequestVO;
		
		quoteRequestVO = new QuoteRequestVO();
		quoteRequestVO.setQuoteRequest(quoteRequestDAO.findById(qrqId).orElse(null));
		quoteRequestVO.getQuoteRequest().getQuoteRequestQuotes().size();
		quoteRequestVO.getQuoteRequest().getQuoteRequestConfigurations().size();
		Hibernate.initialize(quoteRequestVO.getQuoteRequest().getDriver());
		quoteRequestVO = getClientDetails(quoteRequestVO, quoteRequestVO.getQuoteRequest().getClientAccount().getExternalAccountPK());
		
		for(QuoteRequestVehicle qrv : quoteRequestVO.getQuoteRequest().getQuoteRequestVehicles()) {
			qrv.setOnbaseUploadedDocs(onbaseUploadedDocsDAO.getOnBaseUploadedDocsByObjectIdAndType(qrv.getQrvId().toString(), ONBASE_OBJECT_TYPE_QUOTE_REQUEST_VEHICLE));
		}
		
		return quoteRequestVO;
	}
	
	@Override
	@Transactional(readOnly = true)
	public QuoteRequestStatus getQuoteRequestStatus(QuoteRequestStatusEnum quoteRequestStatusEnum) {
		QuoteRequestStatus quoteRequestStatus;
		quoteRequestStatus = quoteRequestStatusDAO.findByCode(quoteRequestStatusEnum.getCode());
		return quoteRequestStatus;
	}

	@Override
	@Transactional(readOnly = true)
	public QuoteRequestType getQuoteRequestType(QuoteRequestTypeEnum quoteRequestTypeEnum) {
		QuoteRequestType quoteRequestType;
		quoteRequestType = quoteRequestTypeDAO.findByCode(quoteRequestTypeEnum.getCode());
		return quoteRequestType;
	}	
	@Override
	@Transactional(readOnly = true)
	public VehicleAcquisitionType getVehicleAcquisitionType(AcquisitionTypeEnum acquisitionTypeEnum) {
		VehicleAcquisitionType vehicleAcquisitionType;
		vehicleAcquisitionType = vehicleAcquisitionTypeDAO.findByCode(acquisitionTypeEnum.getCode());
		return vehicleAcquisitionType;
	}

	@Override
	@Transactional(readOnly = true)
	public QuoteRequestPaymentType getQuoteRequestPaymentTypeByCode(QuoteRequestPaymentTypeEnum quoteRequestPaymentTypeEnum) {
		QuoteRequestPaymentType quoteRequestPaymentType;
		quoteRequestPaymentType = quoteRequestDAO.getQuoteRequestPaymentTypeByCode(quoteRequestPaymentTypeEnum.getCode()); 
		return quoteRequestPaymentType;
	}
	
	@Override	
	@Transactional(rollbackFor = Exception.class )
	public QuoteRequest copyQuoteRequest(Long quoteRequestId, String username) {
		QuoteRequest sourceQuoteRequest = quoteRequestDAO.findById(quoteRequestId).orElse(null);
		QuoteRequest quoteRequest = new QuoteRequest();
		QuoteRequestConfiguration qrc = new QuoteRequestConfiguration();		
		QuoteRequest savedQuoteRequest;
		
		List<QuoteRequestVehicle> qrvList = new ArrayList<QuoteRequestVehicle>();
		List<QuoteRequestQuote> qrqList = new ArrayList<QuoteRequestQuote>();
		List<QuoteRequestConfiguration> qrcList = new ArrayList<QuoteRequestConfiguration>();
		
		BeanUtils.copyProperties(sourceQuoteRequest, quoteRequest, new String[] {"qrqId", "quoteRequestQuotes", "quoteRequestVehicles", "quoteRequestActivities", "quoteRequestConfigurations"});
		
		if (!MALUtilities.isEmpty(sourceQuoteRequest.getQuoteRequestVehicles()) && sourceQuoteRequest.getQuoteRequestVehicles().size() > 0) {
			for (QuoteRequestVehicle quoteRequestVehicle : sourceQuoteRequest.getQuoteRequestVehicles()) {
				QuoteRequestVehicle qrv = new QuoteRequestVehicle();
		    	BeanUtils.copyProperties(quoteRequestVehicle, qrv, new String[] {"qrvId", "quoteRequest"});
		    	qrv.setQuoteRequest(quoteRequest);
		    	qrvList.add(qrv);
			}
		}
		
		if ((!MALUtilities.isEmpty(sourceQuoteRequest.getQuoteRequestQuotes()) && sourceQuoteRequest.getQuoteRequestQuotes().size() > 0) ) {
			for (QuoteRequestQuote quoteRequestQuote : sourceQuoteRequest.getQuoteRequestQuotes()) {
				QuoteRequestQuote qrq = new QuoteRequestQuote();
		    	BeanUtils.copyProperties(quoteRequestQuote, qrq, new String[] {"quoteRequestQuoteId", "quoteRequest", "quoId"});
		    	qrq.setQuoteRequest(quoteRequest);
		    	qrqList.add(qrq);
			}
		}
		
		if(!MALUtilities.isEmpty(sourceQuoteRequest.getQuoteRequestConfigurations()) && sourceQuoteRequest.getQuoteRequestConfigurations().size() > 0) {
			for(QuoteRequestConfiguration sourceQrc : sourceQuoteRequest.getQuoteRequestConfigurations()) {
				qrc = new QuoteRequestConfiguration();				
		    	BeanUtils.copyProperties(sourceQrc, qrc, new String[] {"qrcId", "quoteRequest", "vehicleConfiguration"});
				qrc.setQuoteRequest(quoteRequest);	
				qrc.setVehicleConfigurationId(vehicleConfigurationDAO.findById(sourceQrc.getVehicleConfiguration().getVcfId()).orElse(null));
				qrcList.add(qrc);
			}
		}
		
		quoteRequest.setQuoteRequestVehicles(qrvList);
		quoteRequest.setQuoteRequestQuotes(qrqList);
		
		quoteRequest.setQuoteRequestStatus(quoteRequestStatusDAO.findByCode(QuoteRequestStatusEnum.SAVED.getCode()));
		quoteRequest.setSubmittedBy(username);
		quoteRequest.setSubmittedDate(null);
		quoteRequest.setDueDate(null);
		quoteRequest.setAssignedTo(null);
		quoteRequest.setCompletedDate(null);
		quoteRequest.setClosedDate(null);
		quoteRequest.setQuoteRequestCloseReason(null);
		quoteRequest.setCreatedBy(username);
		quoteRequest.setCreatedDate(Calendar.getInstance().getTime()); 
		quoteRequest.setQuoteRequestConfigurations(qrcList);
		
		savedQuoteRequest = quoteRequestDAO.saveAndFlush(quoteRequest);
		
		return savedQuoteRequest;
	}
			
	@Override	
	@Transactional(rollbackFor = Exception.class )
	public QuoteRequest save(QuoteRequest quoteRequest, String username) {
		QuoteRequest savedQuoteRequest;
		List<OnbaseUploadedDocs> newOnBaseDocs = new ArrayList<OnbaseUploadedDocs>();
		
		if(MALUtilities.isEmpty(quoteRequest.getCreatedBy())) {
			quoteRequest.setCreatedBy(username);
			quoteRequest.setCreatedDate(Calendar.getInstance().getTime());
		}
		
		//Only when submitted by has not been set, the created by user will temporarily be the submitter 
		//until the QR has been formally submitted.
		if(MALUtilities.isEmpty(quoteRequest.getSubmittedBy())) {
			quoteRequest.setSubmittedBy(username);			
		}
		
		if(MALUtilities.isEmpty(quoteRequest.getQuoteRequestStatus())){
			quoteRequest.setQuoteRequestStatus(quoteRequestStatusDAO.findByCode(QuoteRequestStatusEnum.SAVED.getCode()));
		}
		
		//TODO This check is temporary. It can be removed once the
		// is being set by caller.
		if(MALUtilities.isEmpty(quoteRequest.getReturningUnitYN())) {
			quoteRequest.setReturningUnitYN("N");
		}
		
		switch(QuoteRequestTypeEnum.valueOf(quoteRequest.getQuoteRequestType().getCode())) {
		    case IMM_NEED_STOCK:
		    case IMM_NEED_UPFIT_STOCK:
			    quoteRequest.setVehicleAcquisitionType(vehicleAcquisitionTypeDAO.findByCode(AcquisitionTypeEnum.STOCK.getCode()));	
			    break;
		    case IMM_NEED_LOCATE:
		    case IMM_NEED_UPFIT_MAFS_LOCATE:
			    quoteRequest.setVehicleAcquisitionType(vehicleAcquisitionTypeDAO.findByCode(AcquisitionTypeEnum.LOCATE.getCode()));		
			    break;
		    case IMM_NEED_CLIENT:
		    case IMM_NEED_UPFIT_CLIENT_LOCATE:
			    quoteRequest.setVehicleAcquisitionType(vehicleAcquisitionTypeDAO.findByCode(AcquisitionTypeEnum.LOCATE.getCode()));
			    break;
		    case UPFIT_ASSESSMENT:
			    quoteRequest.setVehicleAcquisitionType(vehicleAcquisitionTypeDAO.findByCode(AcquisitionTypeEnum.FACTORY.getCode()));
			    break;
		    default:
			    throw new MalException("generic.error", new String[]{"Unspported Quote Request Type"});
		}	
		
		savedQuoteRequest = quoteRequestDAO.saveAndFlush(quoteRequest);
		
		//Initializing the transient onbase doc property of the saved vehicle
		for(int i = 0; i < savedQuoteRequest.getQuoteRequestVehicles().size(); i++) {
			savedQuoteRequest.getQuoteRequestVehicles().get(i).setOnbaseUploadedDocs(quoteRequest.getQuoteRequestVehicles().get(i).getOnbaseUploadedDocs());
		}
		
		// Extract new documents to upload to OnBase that should now be linked to the PK
		// of the saved vehicle
		for(QuoteRequestVehicle qrv : savedQuoteRequest.getQuoteRequestVehicles()) {
			if(!MALUtilities.isEmpty(qrv.getOnbaseUploadedDocs())) {
				for(OnbaseUploadedDocs obd : qrv.getOnbaseUploadedDocs()) {
					if(MALUtilities.isEmpty(obd.getObdId())) {
						obd.setObdId(onbaseUploadedDocsDAO.getNextPK());
						obd.setObjectId(String.valueOf(qrv.getQrvId()));
						obd.setObjectType(ONBASE_OBJECT_TYPE_QUOTE_REQUEST_VEHICLE);
						obd.setDocType(OnbaseDocTypeEnum.TYPE_QUOTE_REQUEST.getValue());
						obd.setIndexKey(getOnBaseIndexKey(obd));
						obd.setObsoleteYn("N");
						obd.setFileType(obd.getFileType().toUpperCase());
						newOnBaseDocs.add(obd);
					}
				}
			}
		}
		
		if (!newOnBaseDocs.isEmpty() && newOnBaseDocs.size() > 0) {
			onbaseUploadedDocsDAO.saveAll(newOnBaseDocs);		
			onbaseArchivalService.archiveDocumentInOnBase(newOnBaseDocs);
		}
						
		return savedQuoteRequest;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class )
	public QuoteRequest submit(QuoteRequest quoteRequest, String username) throws Exception {
		QuoteRequest submittedQuoteRequest;
		Calendar cal = Calendar.getInstance();
		int slaDays = 0;
			
		submittedQuoteRequest = quoteRequest;
		
		validateForSubmit(submittedQuoteRequest);
		
		slaDays = Integer.parseInt(willowConfigService.getConfigValue(WillowConfigService.SLA_DAYS));
		submittedQuoteRequest.setSubmittedBy(username);		
		submittedQuoteRequest.setSubmittedDate(cal.getTime());
		cal.add(Calendar.DATE, slaDays);
		submittedQuoteRequest.setDueDate(cal.getTime());
		submittedQuoteRequest.setQuoteRequestStatus(quoteRequestStatusDAO.findByCode(QuoteRequestStatusEnum.SUBMITTED.getCode()));
		
		submittedQuoteRequest = quoteRequestDAO.saveAndFlush(quoteRequest);
		
		return submittedQuoteRequest;		
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class )
	public QuoteRequest complete(QuoteRequest quoteRequest, String username) throws Exception{
		QuoteRequest completedQuoteRequest;
		
		completedQuoteRequest = coreComplete(quoteRequest, username);	
				
		return completedQuoteRequest;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class )	
	public List<QuoteRequest> complete(Long qmdId, String username) throws Exception {
		QuotationModel quotationModel;
		List<QuoteRequest> quoteRequests;
		List<QuoteRequest> completedQuoteRequests;		
		
		//TODO Retrieve all quote requests assigned to the given quote no
		quotationModel = quotationModelDAO.findById(qmdId).orElse(null);
		quoteRequests = quoteRequestDAO.findByQuoId(quotationModel.getQuotation().getQuoId());
		
		completedQuoteRequests = new ArrayList<QuoteRequest>();
		for(QuoteRequest qrq : quoteRequests){
			if(!qrq.getQuoteRequestStatus().getCode().equals(QuoteRequestStatusEnum.COMPLETED.getCode()))
			completedQuoteRequests.add(coreComplete(qrq, username));
		}
		
		return completedQuoteRequests;		
	}
		
	@Override
	@Transactional(rollbackFor = Exception.class )
	public QuoteRequest close(QuoteRequest quoteRequest, QuoteRequestCloseReason quoteRequestCloseReason, String username) throws Exception{
		QuoteRequest closedQuoteRequest;
		
		closedQuoteRequest = quoteRequest;	
		
		validateForClose(closedQuoteRequest);
		
		closedQuoteRequest.setClosedDate(Calendar.getInstance().getTime());
		closedQuoteRequest.setQuoteRequestStatus(quoteRequestStatusDAO.findByCode(QuoteRequestStatusEnum.CLOSED.getCode()));
		closedQuoteRequest.setQuoteRequestCloseReason(quoteRequestCloseReason);
		closedQuoteRequest = quoteRequestDAO.saveAndFlush(quoteRequest);				
		
		return closedQuoteRequest;
	}

	@Override
	@Transactional(rollbackFor = Exception.class )
	public QuoteRequest assignToAccountConsultant(QuoteRequest quoteRequest, String consultantRole) throws Exception {			
		ExtAccConsultant extAccConsultant;
		
		validateForAssign(quoteRequest);
		
		extAccConsultant = extAccConsultantDAO.findByExtAccountAndRoleType(quoteRequest.getClientAccount().getExternalAccountPK().getcId(), 
				quoteRequest.getClientAccount().getExternalAccountPK().getAccountType(), 
				quoteRequest.getClientAccount().getExternalAccountPK().getAccountCode(),
				consultantRole);

		if(MALUtilities.isEmpty(extAccConsultant)) {
			throw new MalBusinessException("service.validation", new String[]{ "Quote Request was not assigned. An assignee has not been specified/setup for account " 
					+ quoteRequest.getClientAccount().getExternalAccountPK().getAccountCode() + " for qrqId " + quoteRequest.getQrqId()}); 
		}
		
		return assign(quoteRequest, extAccConsultant.getEmployeeNo());		
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class )
	public QuoteRequest assignToEmployee(QuoteRequest quoteRequest, String employeeNo) throws Exception{							
		return assign(quoteRequest, employeeNo);
	}
	
	public List<ClientQuoteRequestServiceElementVO> getServiceElements(ExternalAccount ea) {
		List<ClientQuoteRequestServiceElementVO> serviceElementList = new ArrayList<ClientQuoteRequestServiceElementVO>();
		List<ClientQuoteRequestServiceElementVO> seList = new ArrayList<ClientQuoteRequestServiceElementVO>();
		
		serviceElementList = quoteRequestDAO.getClientServiceElements(ea);
		
		ClientQuoteRequestServiceElementVO prevCse = new ClientQuoteRequestServiceElementVO();
		
		for (ClientQuoteRequestServiceElementVO cse : serviceElementList) {
			if (!cse.getElementName().equals(prevCse.getElementName())) {
				if (!MALUtilities.isEmpty(cse.getElementStatus())) {
					if (cse.getElementStatus().equals("ACC PRD_EXCLUSIONS")) {
						cse.setProductExclusions(cse.getProductCode());
					} else if (cse.getElementStatus().equals("GG_EXCLUSION")) {
						cse.setGradeGroupExclusions(cse.getGradeGroupCode());
					} else if (cse.getElementStatus().equals("GG_PRD_EXCLUSION")) {
						cse.setProductExclusions(cse.getProductCode() + "(" + cse.getGradeGroupCode() + ")");
					}
				}
				seList.add(cse);
				prevCse = cse;
			} else {
				int i = seList.size()-1;
				if (!MALUtilities.isEmpty(cse.getElementStatus())) {
					if (cse.getElementStatus().equals("ACC PRD_EXCLUSIONS")) {
						seList.get(i).setProductExclusions((!MALUtilities.isEmpty(seList.get(i).getProductExclusions())) ? seList.get(i).getProductExclusions() + ", " + cse.getProductCode() : cse.getProductCode());
					} else if (cse.getElementStatus().equals("GG_EXCLUSION")) {
						seList.get(i).setGradeGroupExclusions((!MALUtilities.isEmpty(seList.get(i).getGradeGroupExclusions())) ? seList.get(i).getGradeGroupExclusions() + ", " + cse.getGradeGroupCode() : cse.getGradeGroupCode());
					} else if (cse.getElementStatus().equals("GG_PRD_EXCLUSION")) {
						seList.get(i).setProductExclusions((!MALUtilities.isEmpty(seList.get(i).getProductExclusions())) ? seList.get(i).getProductExclusions() + ", " + cse.getProductCode() + "(" + cse.getGradeGroupCode() + ")" : cse.getProductCode() + "(" + cse.getGradeGroupCode() + ")");
					}
				}
				prevCse = cse;
			}
		}
		
		return seList;
	}
	
	@Transactional(rollbackFor = Exception.class )
	public QuoteRequest rework(QuoteRequest quoteRequest, String reason, String username) {	
		QuoteRequest reworkQuoteRequest = quoteRequest;		
		Date completedDate, submittedDate;
		
		submittedDate = reworkQuoteRequest.getSubmittedDate();
		completedDate = reworkQuoteRequest.getCompletedDate();
		
		reworkQuoteRequest.setQuoteRequestStatus(quoteRequestStatusDAO.findByCode(QuoteRequestStatusEnum.SAVED.getCode()));
		reworkQuoteRequest.setSubmittedDate(null);
		reworkQuoteRequest.setCompletedDate(null);
		reworkQuoteRequest.setAssignedTo(null);
		reworkQuoteRequest.setDueDate(null);
		reworkQuoteRequest.setClosedDate(null);
		reworkQuoteRequest.setSubmittedBy(username);		
		for(QuoteRequestQuote qrq : reworkQuoteRequest.getQuoteRequestQuotes()) {
			qrq.setQuoId(null);
		}
		
		reworkQuoteRequest = quoteRequestDAO.saveAndFlush(reworkQuoteRequest);
		
		logBookService.addEntry(logBookService.createObjectLogBook(reworkQuoteRequest, LogBookTypeEnum.TYPE_QUOTE_REQEUST_NOTES),
				 username, REWORK_REASON_PREFIX + reason, null, false);
		
		QuoteRequestActivity qra = new QuoteRequestActivity();
		qra.setActivityDate(new Date());
		qra.setQuoteRequest(reworkQuoteRequest);
		qra.setQuoteRequestActivityType(quoteRequestActivityTypeDAO.findByCode(QuoteRequestActivityTypeEnum.REWORK.getCode()));
		qra.setSubmittedDate(submittedDate);
		qra.setCompletedDate(completedDate);
		quoteRequestActivityDAO.saveAndFlush(qra);
		
		return reworkQuoteRequest;
	}
	
	@Transactional(readOnly = true)
	public boolean isRework(QuoteRequest quoteRequest) {
		boolean isRework = false;
		QuoteRequest qrq = quoteRequestDAO.findById(quoteRequest.getQrqId()).orElse(null);
		
		for(QuoteRequestActivity qra : qrq.getQuoteRequestActivities()) {
			if(qra.getQuoteRequestActivityType().getCode().equals(QuoteRequestActivityTypeEnum.REWORK.getCode())){
				isRework = true;
				break;
			}
		}
		
		return isRework;
	}
	
	private List<ClientPocVO> getClientPocDetails(ExternalAccount ea) {
		List<ClientPocVO> quoteRequestPocList = new ArrayList<ClientPocVO>();
		List<PointOfCommunicationVO> pocVOList = clientPOCService.getPointOfCommunicationVOs(ea);
		ClientPocVO poc;
		
		for (PointOfCommunicationVO pocVo : pocVOList) {
			if (!pocVo.getPoc().getName().contains("DEFAULT")) {
				poc = new ClientPocVO();
				poc.setPocCategory(pocVo.getPoc().getClientSystem().getDescription());
				poc.setPocName(pocVo.getPoc().getName());
				poc.setPocDescription(pocVo.getPoc().getDescription());
				if (!MALUtilities.isEmpty(pocVo.getClientPOC())) {
					poc.setContactName(getContacts(pocVo.getClientPOC()));
					poc.setSendToDriver(getSendToDriver(pocVo.getClientPOC()));
				}
				
				quoteRequestPocList.add(poc);
			}
		}
		
		return quoteRequestPocList;
	}
	
	private String getContacts(ClientPointAccount cpa) {
		String delim = "||";
		
		String contacts = "";
		
		if (!MALUtilities.isEmpty(cpa.getClientContacts())) {
			for (ClientContact cc : cpa.getClientContacts()) {
				if((MALUtilities.isEmpty(cc.getDrvInd()) || cc.getDrvInd().equals(MalConstants.FLAG_N)) 
						&& MALUtilities.isEmpty(cc.getCostCentreCode())) {
					if (MALUtilities.isEmpty(contacts)) {
						contacts = cc.getContact().getLastName() + ", " + cc.getContact().getFirstName();
					} else {
						contacts = contacts + delim + cc.getContact().getLastName() + ", " + cc.getContact().getFirstName();
					}
				}
			}
		}
		return contacts;
	}
	
	private String getSendToDriver(ClientPointAccount cpa) {
		String sendToDrv = "No";
		
		if (!MALUtilities.isEmpty(cpa.getClientContacts())) {
			for (ClientContact cc : cpa.getClientContacts()) {
				if(!MALUtilities.isEmpty(cc.getDrvInd()) && cc.getDrvInd().equals(MalConstants.FLAG_Y)) {
					sendToDrv = "Yes";
					break;
				}
			}
		}
		return sendToDrv;
	}

	@Override
	public List<VehicleDeliveryChargeType> getAllVehicleDeliveryChargeTypes() {
		return vehicleDeliveryChargeTypeDAO.findAll();
	}
	
	public List<QuoteRequestDepreciationMethod> getAllQuoteRequestDepreciationMethods() {
		return quoteRequestDAO.getAllQuoteRequestDepreciationMethods();
	}
	
	public QuoteRequestDepreciationMethod getQuoteRequestDepreciationMethodByCode(String code) {
		return quoteRequestDAO.getQuoteRequestDepreciationMethodByCode(code);
	}
	
	public List<QuoteRequestPaymentType> getAllQuoteRequestPaymentTypes() {
		return quoteRequestDAO.getAllQuoteRequestPaymentTypes();
	}
	
	public List<DriverGradeGroupCode> getGradeGroupsByAccount(Long cId, String accountType, String accountCode) {
		return quoteRequestDAO.getGradeGroupsByAccount(cId, accountType, accountCode);
	}

	@Override
	@Transactional
	public StockUnitVO getStockUnitInfo(long fmsId) {
		Doc doc;
		StockUnitVO stockUnitVO = new StockUnitVO(); 
		FleetMaster fleetMaster = fleetMasterDAO.findById(fmsId).orElse(null);
		if(fleetMaster.getColourCode() != null) {
			stockUnitVO.setExteriorColor(fleetMaster.getColourCode().getDescription());
		}
		if(fleetMaster.getTrimColour() != null) {
			stockUnitVO.setInteriorColor(fleetMaster.getTrimColour().getTrimDescription());
		}
		if(fleetMaster.getVehicleOdometerReadings() != null && fleetMaster.getVehicleOdometerReadings().size()>0) {
			stockUnitVO.setOdoReading(fleetMaster.getVehicleOdometerReadings().get(0).getOdoReading());
		}
		stockUnitVO.setFleetMaster(fleetMaster);
		try {
			doc = documentService.getMainPODocOfStockUnit(fmsId);
		} catch (Exception ex) {
			if (ex instanceof MalException) {
				throw (MalException) ex;
			} else {
				throw new MalException("generic.error.occured.while", new String[] { "getting Stock Unit Main PO" }, ex);
			}
		}

		if(doc != null) {
			SupplierProgressHistory sph = supplierProgressHistoryDAO.findSupplierProgressHistoryForDocAndTypeById(doc.getDocId(), "15_RECEIVD");
			if(sph == null) {
				sph = supplierProgressHistoryDAO.findSupplierProgressHistoryForDocAndTypeById(doc.getDocId(), "09_INT_DLR");
			} 
			if(sph != null) {
				stockUnitVO.setReceivedDate(sph.getActionDate());
			}
			sph = supplierProgressHistoryDAO.findSupplierProgressHistoryForDocAndTypeById(doc.getDocId(), "14_ETA");
			if(sph != null) {
				stockUnitVO.setEtaDate(sph.getActionDate());
			}
		}

		return stockUnitVO;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)	
	public void uploadDocument(Long qrqId, byte[] fileData, DocumentNameEnum docNameEnum) throws MalBusinessException, MalException {
		
	}

	@Override
	@Transactional(readOnly=true)
	public List<QuoteRequestCloseReason> getQuoteRequestCloseReasons(){
		return quoteRequestCloseReasonDAO.findAll();
	}
	
	private void validateForSubmit(QuoteRequest quoteRequest) throws MalBusinessException {
		Long makId;
		ExternalAccount externalAccount;
		ExternalAccountPK externalAccountPK;
        ExtAccFinFan extAccFinFan;
		ArrayList<String> messages;
        
		externalAccount = quoteRequest.getClientAccount();
		externalAccountPK = externalAccount.getExternalAccountPK();
		messages = new ArrayList<String>();
		
		//Stock Fin/Fan Code check
		if(!MALUtilities.isEmpty(quoteRequest.getQuoteRequestVehicles()) 
				&& !quoteRequest.getQuoteRequestVehicles().isEmpty()
				&& quoteRequest.getQuoteRequestType().getCode().equals(QuoteRequestTypeEnum.IMM_NEED_STOCK.getCode())) {
			for(QuoteRequestVehicle qrv : quoteRequest.getQuoteRequestVehicles()) {
				if(!MALUtilities.isEmpty(qrv.getFleetmaster())) {
					makId = qrv.getFleetmaster().getModel().getMake().getMakId();
					extAccFinFan = extAccFinFanDAO.findByAccountAndMake(externalAccountPK.getcId(), externalAccountPK.getAccountType(), externalAccountPK.getAccountCode(), makId);
					
					if(MALUtilities.isEmpty(extAccFinFan)) {
						messages.add("Client does not have a fleet code for the selected Make. Request cannot be submitted until client has a fleet code. Fin/Fan Number is not setup for make: " + qrv.getFleetmaster().getModel().getMake().getMakeDesc());
					}
				}
			}
		}
		
		if(messages.size() > 0)
			throw new MalBusinessException("service.validation", messages.toArray(new String[messages.size()]));
		
	}
	
	private void validateForComplete(QuoteRequest quoteRequest) throws MalBusinessException {}	
	
	private void validateForClose(QuoteRequest quoteRequest) throws MalBusinessException {}
	
	private void validateForAssign(QuoteRequest quoteRequest) throws MalBusinessException {}	
	
	public List<QuoteRequestQuoteModelVO> getQuoteRequestQuoteModels(ExternalAccountPK ea, Long quoId) {
		return quoteRequestDAO.getQuoteRequestQuoteModels(ea, quoId);
	}
	
	/**
	 * Assembles the index key for the passed in ...
	 * @param onbaseUploadedDocs
	 * @return
	 */
	private String getOnBaseIndexKey(OnbaseUploadedDocs onbaseUploadedDocs) {
		List<OnbaseKeywordVO>  keywordVOList  =  null;
		String index;				
		
		keywordVOList =  onbaseArchivalService.getOnbaseKeywords(OnbaseDocTypeEnum.TYPE_QUOTE_REQUEST);
		
		for (OnbaseKeywordVO onbaseKeywordVO :keywordVOList) {				
			if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.UPLOAD_ID.getName())){
				onbaseKeywordVO.setKeywordValue(String.valueOf(onbaseUploadedDocs.getObdId())) ;
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.FILE_EXT.getName())){
				onbaseKeywordVO.setKeywordValue(onbaseUploadedDocs.getFileType());
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.QUOTE_REQUEST_TYPE.getName())){
				onbaseKeywordVO.setKeywordValue(onbaseUploadedDocs.getQuoteRequest().getQuoteRequestType().getCode());				
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CUSTOMER_NO.getName())){
				onbaseKeywordVO.setKeywordValue(onbaseUploadedDocs.getQuoteRequest().getClientAccount().getExternalAccountPK().getAccountCode());
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CUSTOMER_NAME.getName())){
				onbaseKeywordVO.setKeywordValue(onbaseUploadedDocs.getQuoteRequest().getClientAccount().getAccountName());
			}	
		}
		
		index = onbaseArchivalService.generateOnbaseKeywordsIndex(keywordVOList);
		
		return index;
	}
	
	@Override
	public List<QuotationProfile> getQuotationProfiles(long corpId, String accountType, String accountCode) {
		return quoteRequestDAO.getQuotationProfiles(corpId, accountType, accountCode);
	}	
	
	/**
	 * Core logic for completing a Quote Request.
	 * @param quoteRequest QuoteRequest to be completed
	 * @param username The employee No of the user that is completing the requests
	 * @return QuoteRequest Flushed version of the QuoteRequest
	 * @throws Exception
	 */
	private QuoteRequest coreComplete(QuoteRequest quoteRequest, String username) throws Exception{
		QuoteRequest completedQuoteRequest;
		
		completedQuoteRequest = quoteRequest;	
		
		validateForComplete(completedQuoteRequest);
		
		completedQuoteRequest.setQuoteRequestStatus(quoteRequestStatusDAO.findByCode(QuoteRequestStatusEnum.COMPLETED.getCode()));	
		completedQuoteRequest.setCompletedDate(Calendar.getInstance().getTime());
		completedQuoteRequest = quoteRequestDAO.saveAndFlush(quoteRequest);		
				
		return completedQuoteRequest;		
	}

	@Override
	@Transactional
	public QuotationModel getQuotationModelWithDealerAccessories(Long qmdId) throws MalBusinessException {
		QuotationModel quotationModel = null;
		if(qmdId != null){
			quotationModel =  quotationModelDAO.findById(qmdId).orElse(null);
			Hibernate.initialize(quotationModel.getQuotationDealerAccessories());
		}else{
			throw new MalBusinessException("service.validation", new String[]{ "Quotation model id must be not null."});
		}
		return quotationModel;
	}

	@Override
	@Transactional(rollbackFor = Exception.class )
	public void deleteQuoteRequest(QuoteRequest quoteRequest) throws MalException {
		logBookService.deleteLogBook(quoteRequest, LogBookTypeEnum.TYPE_QUOTE_REQEUST_NOTES);
		quoteRequestDAO.delete(quoteRequest);
	}
	
	/**
	 * Assigns QuoteRequest to an employee and then changes the state of the quote request to In Progress
	 * @param quoteRequest The QuoteRequest to assign and place In Progress
	 * @param employeeNo THe MAFS employee to assign the Quote Request to.
	 * @return The assigned QuoteRequest
	 */
	private QuoteRequest assign(QuoteRequest quoteRequest, String employeeNo) {
		quoteRequest.setAssignedTo(employeeNo);
		quoteRequest.setQuoteRequestStatus(getQuoteRequestStatus(QuoteRequestStatusEnum.IN_PROGRESS));
		
		quoteRequest = quoteRequestDAO.saveAndFlush(quoteRequest);				
		
		return quoteRequest;
	}	
}
