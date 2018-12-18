package com.mikealbert.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.schema.kvpair.KeyValuePairType;
import com.mikealbert.schema.tal.NotificationType;
import com.mikealbert.schema.tal.TransactionType;
import com.mikealbert.service.tal.websvc.transaction.CreateNotificationRequestType;
import com.mikealbert.service.tal.websvc.transaction.CreateNotificationResponseType;
import com.mikealbert.service.tal.websvc.transaction.CreateTransactionRequestType;
import com.mikealbert.service.tal.websvc.transaction.CreateTransactionResponseType;
import com.mikealbert.service.vo.TALTransactionVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.websvc.client.TALTransactionWebsvcClient;


@Service	
public class TALTransactionServiceImpl implements TALTransactionService{	
	
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	
	@Resource private WillowConfigService willowConfigService;
	
	
	public Long createTransaction(TALTransactionVO transactionVO , boolean isSynchronous) throws MalBusinessException,MalException {
		
			logger.info("###### Entered into createTransaction  service method.######");
			CreateTransactionResponseType transactionResponseType = null;
			TALTransactionWebsvcClient transactionWebsvcClient =  null;
			Long createdTxnId =  null;
			try {
			
				if (MALUtilities.isEmpty(transactionVO.getUnitNumber())) {
					throw new MalBusinessException("field.required", new String[] { "Unit Number" });
				}
				if (MALUtilities.isEmpty(transactionVO.getTransactionCode())) {
					throw new MalBusinessException("field.required", new String[] { "Transaction Code" });
				}
				if (MALUtilities.isEmpty(transactionVO.getCountryCode())) {
					throw new MalBusinessException("field.required", new String[] { "Country Code" });
				}
				if (MALUtilities.isEmpty(transactionVO.getRegionCode())) {
					throw new MalBusinessException("field.required", new String[] { "Target State" });
				}
				
				if (MALUtilities.isEmpty(transactionVO.getReasonCode())) {
					throw new MalBusinessException("field.required", new String[] { "Reason" });
				}
				
				if (MALUtilities.isEmpty(transactionVO.getUserId())) {
					throw new MalBusinessException("field.required", new String[] { "User" });
				}
				
				String wsdlURl = willowConfigService.getConfigValue("TAL_WEBSVC_WSDL_BASE_URL");
				
				if(wsdlURl == null ){				
						throw new MalBusinessException("field.required", new String[] { "TAL transaction wsdl willow config setup" });
				}
				wsdlURl = wsdlURl + TAL_TRANSACTION_WEBSVC_NAME;
				
				transactionWebsvcClient = new TALTransactionWebsvcClient(wsdlURl);
				transactionResponseType = transactionWebsvcClient.sendForceCreateTransactionRequest(createTransactionRequest(transactionVO));
				
				if(transactionResponseType.isSuccess()){					
					List<KeyValuePairType> list  =  transactionResponseType.getTransaction().getKeyValue();
					if(list.size() > 0  && list.get(0).getValue() != null){
						createdTxnId =  Long.parseLong(list.get(0).getValue());
					}
				}else if(isSynchronous){
					throw new MalException("generic.error", new String[] { "TAL web-services return error ::"+transactionResponseType.getErrorCode() });
				}
				
			}catch(Exception ex){
				logger.error(ex, "Failed to create TAL transaction for request parameters::"+transactionVO);	
				
				if(ex instanceof MalException) throw (MalException)ex;
				
				if(isSynchronous){
					throw new MalException("generic.error", new String[] { "TAL web-services exception ::"+ex.getMessage() });
				}else{
					try {
						CreateNotificationResponseType notificationResponseType = transactionWebsvcClient.sendCreateNotificationRequest(createNotificationRequest(transactionVO));
						if(! notificationResponseType.isSuccess())
							logger.error(null ,"Failed to create TAL transaction failure notification ::");
					} catch (Exception e) {
						throw new MalException("generic.error", new String[] { "Failed to create TAL transaction failure notification ::"+ex.getMessage() });
					}
				}	
			}
			
			logger.info("###### Exit into createTransaction service method.######");
			
			return createdTxnId;
	}
	
	
	
	

	private CreateTransactionRequestType createTransactionRequest(TALTransactionVO transactionVO) {

		CreateTransactionRequestType createTransactionRequest = new CreateTransactionRequestType();
		TransactionType transactionType = new TransactionType();
		List<KeyValuePairType> listKeyValuePairType = populateTransactionKeyValuepairList(transactionVO);
		transactionType.getKeyValue().addAll(listKeyValuePairType);
		createTransactionRequest.setTransaction(transactionType);
		createTransactionRequest.setUserId(transactionVO.getUserId());
		return createTransactionRequest;

	}
	
	protected List<KeyValuePairType> populateTransactionKeyValuepairList(TALTransactionVO transactionVO) {
		
			List<KeyValuePairType> kvList = new ArrayList<KeyValuePairType>();
			
			if(transactionVO.getUnitNumber() != null){
				KeyValuePairType keyValuePairType1 = new KeyValuePairType();
				keyValuePairType1.setKey(TXN_UNIT_NO);
				keyValuePairType1.setValue(transactionVO.getUnitNumber());
				kvList.add(keyValuePairType1);
			}
			if(transactionVO.getTransactionCode() != null){
				KeyValuePairType keyValuePairType2 = new KeyValuePairType();
				keyValuePairType2.setKey(TRX_CODE);
				keyValuePairType2.setValue(transactionVO.getTransactionCode());
				kvList.add(keyValuePairType2);
			}
			if(transactionVO.getCountryCode() != null){
				KeyValuePairType keyValuePairType3 = new KeyValuePairType();
				keyValuePairType3.setKey(TXN_COUNTRY_CODE);
				keyValuePairType3.setValue(transactionVO.getCountryCode());
				kvList.add(keyValuePairType3);
			}
			if(transactionVO.getRegionCode() != null){
				KeyValuePairType keyValuePairType4 = new KeyValuePairType();
				keyValuePairType4.setKey(TXN_REGION_CODE);
				keyValuePairType4.setValue(transactionVO.getRegionCode());		
				kvList.add(keyValuePairType4);
			}
			if(transactionVO.getReasonCode() != null){
				KeyValuePairType keyValuePairType5 = new KeyValuePairType();
				keyValuePairType5.setKey(TXN_TAL_REASON);
				keyValuePairType5.setValue(transactionVO.getReasonCode());	
				kvList.add(keyValuePairType5);
			}
			if(transactionVO.getUserId() != null){
				KeyValuePairType keyValuePairType6 = new KeyValuePairType();
				keyValuePairType6.setKey(USER_ID);
				keyValuePairType6.setValue(String.valueOf(transactionVO.getUserId()));
				kvList.add(keyValuePairType6);
			}
			if(transactionVO.getDriverId() != null){
				KeyValuePairType keyValuePairType7 = new KeyValuePairType();
				keyValuePairType7.setKey(TXN_DRIVER_ID);
				keyValuePairType7.setValue(String.valueOf(transactionVO.getDriverId()));
				kvList.add(keyValuePairType7);
			}
			
			KeyValuePairType keyValuePairType8 = new KeyValuePairType();
			keyValuePairType8.setKey(TXN_FETCH_DRIVER_ADDRESS_VALUE);
			keyValuePairType8.setValue(String.valueOf(transactionVO.isFetchDriverAddress()));
			kvList.add(keyValuePairType8);
			
			if(transactionVO.getContextId() != null){
				KeyValuePairType keyValuePairType9 = new KeyValuePairType();
				keyValuePairType9.setKey(TXN_ACCOUNT_CONTEXT_ID);
				keyValuePairType9.setValue(String.valueOf(transactionVO.getContextId()));
				kvList.add(keyValuePairType9);
			}
			if(transactionVO.getAccountType() != null){			
				KeyValuePairType keyValuePairType10 = new KeyValuePairType();
				keyValuePairType10.setKey(TXN_ACCOUNT_TYPE);
				keyValuePairType10.setValue(transactionVO.getAccountType());
				kvList.add(keyValuePairType10);
			
			}
			if(transactionVO.getAccountCode() != null){
				KeyValuePairType keyValuePairType11 = new KeyValuePairType();
				keyValuePairType11.setKey(TXN_ACCOUNT_CODE);
				keyValuePairType11.setValue(transactionVO.getAccountCode());
				kvList.add(keyValuePairType11);	
			}
			if(transactionVO.getPlateTypeCode() != null){
				KeyValuePairType keyValuePairType12 = new KeyValuePairType();
				keyValuePairType12.setKey(TXN_PLATE_TYPE_CODE);
				keyValuePairType12.setValue(transactionVO.getPlateTypeCode());
				kvList.add(keyValuePairType12);			
			}
			if(transactionVO.getInventoryCode() != null){
				KeyValuePairType keyValuePairType13 = new KeyValuePairType();
				keyValuePairType13.setKey(TXN_INVENTORY_CODE);
				keyValuePairType13.setValue(transactionVO.getInventoryCode());
				kvList.add(keyValuePairType13);			
			}
		
			KeyValuePairType keyValuePairType14 = new KeyValuePairType();
			keyValuePairType14.setKey(TXN_CHECK_IF_EXIST);
			keyValuePairType14.setValue(String.valueOf(transactionVO.isCheckIfExist()));
			kvList.add(keyValuePairType14);
			
			
			if(transactionVO.getTxnOriginPGM() != null){
				KeyValuePairType keyValuePairType15 = new KeyValuePairType();
				keyValuePairType15.setKey(TRX_ORIGIN_PGM);
				keyValuePairType15.setValue(String.valueOf(transactionVO.getTxnOriginPGM()));
				kvList.add(keyValuePairType15);
			}

		return kvList;
	}
	
	private CreateNotificationRequestType  createNotificationRequest(TALTransactionVO transactionVO) {

		CreateNotificationRequestType createNotificationRequestType = new CreateNotificationRequestType();
		NotificationType notificationType = new NotificationType();
		List<KeyValuePairType> listKeyValuePairType = populateNotificationKeyValuepairList(transactionVO);
		notificationType.getKeyValue().addAll(listKeyValuePairType);
		createNotificationRequestType.setNotification(notificationType);
		createNotificationRequestType.setUserId(transactionVO.getUserId());
		return createNotificationRequestType;

	}
	
	protected List<KeyValuePairType> populateNotificationKeyValuepairList(TALTransactionVO transactionVO) {
		
		List<KeyValuePairType> kvList = new ArrayList<KeyValuePairType>();
		
		KeyValuePairType keyValuePairType1 = new KeyValuePairType();
		keyValuePairType1.setKey(TXN_FMS_ID);
		keyValuePairType1.setValue(String.valueOf(transactionVO.getFmsId()));
		
		KeyValuePairType keyValuePairType2 = new KeyValuePairType();
		keyValuePairType2.setKey(TRX_CODE);
		keyValuePairType2.setValue(transactionVO.getTransactionCode());
		
		KeyValuePairType keyValuePairType3 = new KeyValuePairType();
		keyValuePairType3.setKey(NOTIFICATION_MSG);
		keyValuePairType3.setValue("Failed to create "+transactionVO.getTransactionCode()+" transaction for unit "+transactionVO.getUnitNumber());
		
		KeyValuePairType keyValuePairType4 = new KeyValuePairType();
		keyValuePairType4.setKey(USER_ID);
		keyValuePairType4.setValue(String.valueOf(transactionVO.getUserId()));
		
		
		kvList.add(keyValuePairType1);
		kvList.add(keyValuePairType2);
		kvList.add(keyValuePairType3);
		kvList.add(keyValuePairType4);	
		
		return kvList;
	}
		
	
	
}
	
