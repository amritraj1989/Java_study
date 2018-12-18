package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.DriverGradeGroupCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.data.entity.QuoteRequestCloseReason;
import com.mikealbert.data.entity.QuoteRequestDepreciationMethod;
import com.mikealbert.data.entity.QuoteRequestPaymentType;
import com.mikealbert.data.entity.QuoteRequestStatus;
import com.mikealbert.data.entity.QuoteRequestType;
import com.mikealbert.data.entity.VehicleAcquisitionType;
import com.mikealbert.data.vo.ClientQuoteRequestServiceElementVO;
import com.mikealbert.data.vo.QuoteRequestQuoteModelVO;
import com.mikealbert.data.entity.VehicleDeliveryChargeType;
import com.mikealbert.data.enumeration.AcquisitionTypeEnum;
import com.mikealbert.data.enumeration.DocumentNameEnum;
import com.mikealbert.data.enumeration.QuoteRequestPaymentTypeEnum;
import com.mikealbert.data.enumeration.QuoteRequestStatusEnum;
import com.mikealbert.data.enumeration.QuoteRequestTypeEnum;
import com.mikealbert.data.vo.QuoteRequestVO;
import com.mikealbert.data.vo.WillowUserLovVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.StockUnitVO;

public interface QuoteRequestService {	
	
	public List<WillowUserLovVO> getWillowUsers(String willowUserNo, String willowUserName, String lovName, Pageable pageable);
	public int getWillowUsersCount(String willowUserNo, String willowUserName, String lovName);
	
	public QuoteRequestVO getClientDetails(QuoteRequestVO quoteRequestVO, ExternalAccountPK externalAccountPK);
	public QuoteRequestVO getQuoteRequestVO(Long qrqId);
	
	public QuoteRequestStatus getQuoteRequestStatus(QuoteRequestStatusEnum quoteRequestStatusEnum);
	public QuoteRequestType getQuoteRequestType(QuoteRequestTypeEnum quoteRequestTypeEnum);
	public VehicleAcquisitionType getVehicleAcquisitionType(AcquisitionTypeEnum acquisitionTypeEnum);
	public QuoteRequestPaymentType getQuoteRequestPaymentTypeByCode(QuoteRequestPaymentTypeEnum quoteRequestPaymentTypeEnum);	
	
	public QuoteRequest save(QuoteRequest quoteRequest, String username);
	public QuoteRequest submit(QuoteRequest quoteRequest, String username) throws Exception;	
	public QuoteRequest complete(QuoteRequest quoteRequest, String username) throws Exception;
	public List<QuoteRequest> complete(Long qmdId, String username) throws Exception;	
	public QuoteRequest close(QuoteRequest quoteRequest, QuoteRequestCloseReason quoteRequestCloseReason, String username) throws Exception;	
	
	public List<ClientQuoteRequestServiceElementVO> getServiceElements(ExternalAccount ea);
	
	public List<VehicleDeliveryChargeType> getAllVehicleDeliveryChargeTypes();
	
	public List<QuoteRequestDepreciationMethod> getAllQuoteRequestDepreciationMethods();
	
	public QuoteRequestDepreciationMethod getQuoteRequestDepreciationMethodByCode(String code);
	
	public List<QuoteRequestPaymentType> getAllQuoteRequestPaymentTypes();
	
	public List<DriverGradeGroupCode> getGradeGroupsByAccount(Long cId, String accountType, String accountCode);
	public StockUnitVO getStockUnitInfo(long fmsId);
	
	public List<QuoteRequestCloseReason> getQuoteRequestCloseReasons();
	
	public QuoteRequest assignToAccountConsultant(QuoteRequest quoteRequest, String consultantRole) throws Exception;
	public QuoteRequest assignToEmployee(QuoteRequest quoteRequest, String employeeNo) throws Exception;	
	
	public void uploadDocument(Long qrqId, byte[] fileData, DocumentNameEnum docNameEnum) throws MalBusinessException, MalException;
	
	public List<QuoteRequestQuoteModelVO> getQuoteRequestQuoteModels(ExternalAccountPK ea, Long quoId);
	
	public List<QuotationProfile> getQuotationProfiles(long corpId, String accountType, String accountCode);
	
	public QuotationModel getQuotationModelWithDealerAccessories(Long qmdId)throws MalBusinessException;
	
	public QuoteRequest copyQuoteRequest(Long quoteRequestId, String username);
	
	public void deleteQuoteRequest(QuoteRequest quoteRequest) throws MalException;
	
	public QuoteRequest rework(QuoteRequest quoteRequest, String reason, String username);
	
	public boolean isRework(QuoteRequest quoteRequest);
}
