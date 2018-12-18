package com.mikealbert.service;

import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.TALTransactionVO;

public interface TALTransactionService {
	
	public Long createTransaction(TALTransactionVO transactionVO , boolean isSynchronous) throws MalBusinessException,MalException ;
	
	public static final String TAL_TRANSACTION_WEBSVC_NAME=	"Transaction?WSDL";
	
	public static final String TXN_UNIT_NO = "txnUnitNo";
	public static final String TRX_CODE = "txnTrxCode";
	public static final String TXN_COUNTRY_CODE = "txnCountryCode";
	public static final String TXN_REGION_CODE = "txnRegionCode";
	public static final String TXN_TAL_REASON = "txnTalReason";
	public static final String USER_ID = "userId";
	public static final String TXN_DRIVER_ID = "txnDrvDrvId";
	public static final String TXN_FETCH_DRIVER_ADDRESS_VALUE = "txnFetchDriverAddress";	
	public static final String TXN_ACCOUNT_CONTEXT_ID = "txnCompContextId";
	public static final String TXN_ACCOUNT_TYPE = "txnCompType";
	public static final String TXN_ACCOUNT_CODE = "txnCompCode";
	public static final String TXN_PLATE_TYPE_CODE = "txnPlateTypeCode";
	public static final String TXN_INVENTORY_CODE = "txnInventoryCode";
	public static final String TXN_ID =	"txnId";
	public static final String TXN_CHECK_IF_EXIST =	"txnCheckIfExist";
	
	
	public static final String 	TXN_FMS_ID =	"txnFmsId";
	public static final String NOTIFICATION_MSG =	"notificationMSG";
	
	public static final String TRX_ORIGIN_PGM =	"txnOriginPGM";
	
	
	
}
