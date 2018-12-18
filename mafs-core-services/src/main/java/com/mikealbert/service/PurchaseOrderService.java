package com.mikealbert.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.DocSupplier;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Supplier;
import com.mikealbert.data.entity.ThirdPartyPoQueueV;
import com.mikealbert.data.enumeration.DocumentNameEnum;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.task.POArchieveTask;
import com.mikealbert.service.vo.VehiclePurchaseOrderVO;

public interface PurchaseOrderService {
	public static final String WORKSHOP_CAPABILITY_ORDERING = "ORDERING";
	public static final String WORKSHOP_CAPABILITY_DELIVERING = "DELIVERING";
	

	public DbProcParamsVO createPurchaseOrder(Long qmdId, Long cId,String userId) throws MalBusinessException, MalException;
	
	public VehiclePurchaseOrderVO getMainPurchaseOrderDetails(Long qmdId, Long mainDocId);
	
	public DbProcParamsVO releaseMainPurchaseOrder(Long docId, Long cId, String userId) throws MalBusinessException, MalException;
	
	public DbProcParamsVO confirmPurchaseOrder(Long docId, String userId , Date confirmDate) throws MalBusinessException, MalException;
	
	public DbProcParamsVO releaseThirdPartyPurchaseOrders(Long docId, String userId) throws MalBusinessException, MalException;
	
	public void renderPurchaseOrderReleaseDocuments(Long doc_id, boolean isStock, boolean isEmail);	
	
	public DbProcParamsVO saveOrUpdatePO(VehiclePurchaseOrderVO maintainPoVO) throws MalBusinessException, MalException;

	public static final String GARAGED = "GARAGED";

	public String getBailmentDealerCode(VehiclePurchaseOrderVO maintainPoVO);
	
	public List<Doc> getThirdPartyPurchaseOrders(Long docId);
	
	public List<Doc> getOpenThirdPartyPurchaseOrderWithAccessories(Long docId);
	
	public List<Doc> getThirdPartyPurchaseOrderWithAccessories(Long docId);
	
	public int getNonAutoDataDealerAccWithoutVendorCount(Long qmdId);
	
	public boolean isOrdeDelDealerExistInDocSupplier(Long docId);
	
	public boolean isCapitalElementOnlyPurchaseOrder(Long docId);
	
	public List<ExternalAccount> getPreferredVendorByQmdId(Long qmdId);
	
	public List<Supplier> getPreferredSupplierListByQmdId(Long qmdId);
	
	public Supplier getDefaultSupplier(Long qmdId, Long docId);
	
	public DbProcParamsVO updatePreferredVendor(Long docId, Long orderingSupId) throws MalBusinessException, MalException;
	
	public DocSupplier getOrderingDealerByDocId(Long docId);
	
	public List<ThirdPartyPoQueueV> findThirdPartyPoQueueList() throws Exception;
	
	public Map<String, List<String>> getDealerAccessoriesWithVendorQuoteNumber(Long qmdId, Long thpyDocId, String accountCode);
	
	
	public void archiveVehicleOrderSummary(Long mainPODocId) throws MalBusinessException, MalException;	
	
	public void archiveClientOrderConfirmation(Long mainPODocId) throws MalBusinessException, MalException;
	
	public void archiveMainPurchaseOrderDoc(Long mainPODocId,  String stockYn) throws MalBusinessException, MalException;
	
	public void archiveCourtesyDeliveryInstructionDoc(Long mainPODocId) throws MalBusinessException, MalException;	
	
	public void archiveThirdPartyPurchaseOrder(Long tpDocId, String stockYn) throws MalBusinessException, MalException;
	
	public void  archivePurchaseOrderDoc(Long docId, byte[] archivalData, DocumentNameEnum documentNameEnum) throws MalBusinessException, MalException;
	
	public byte[] getPurchaseOrderDocument(Long docId, DocumentNameEnum documentNameEnum) throws MalBusinessException, MalException;	
	
	public byte[] getPurchaseOrderDocument(String docNo, DocumentNameEnum documentNameEnum) throws MalBusinessException, MalException;
	
	public Long getPurchaseOrderLeadTimeByDocId(Long docId);
	public Long getUnitLeadTimeByDocId(Long docId);	
	public List<Doc> getMultipleMainPO(Long qmdId);	
	
	public List<String> getPowertrainByQmdorDocId(Long qmdId, Long docId);
	public void process(POArchieveTask task) throws MalException, MalBusinessException;
}
