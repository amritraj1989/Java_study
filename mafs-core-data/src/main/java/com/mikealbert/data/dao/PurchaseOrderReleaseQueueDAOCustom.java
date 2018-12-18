package com.mikealbert.data.dao;

import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.PurchaseOrderReleaseQueueV;
import com.mikealbert.data.entity.Supplier;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;


public interface PurchaseOrderReleaseQueueDAOCustom  {	
	
	public DbProcParamsVO createPurchaseOrder(Long qmdId, Long cId, String userId ) throws MalBusinessException;
	
	public DbProcParamsVO releaseMainPurchaseOrder(Long docId, Long cId, String userId) throws MalBusinessException, MalException;
	
	public DbProcParamsVO confirmPurchaseOrder(Long docId, String userId, Date confirmDate) throws MalBusinessException, MalException;	
	
	public DbProcParamsVO releaseThirdPartyPurchaseOrders(Long docId, String userId) throws MalBusinessException, MalException;	
	
	public List<PurchaseOrderReleaseQueueV> findAllPurchaseOrderReleaseQueueList();
	
	public DbProcParamsVO peformPostOrderingDealerChangeUpdates(Long docId, Long orderingSupId) throws MalBusinessException;
	
	public int getNonAutoDataDealerAccWithoutVendorCount(Long qmdId, String autodataAccCode);
	
	public List<ExternalAccount> getPreferredVendorByQmdId(Long qmdId);
	
	public List<Supplier> getPreferredSupplierByQmdId(Long qmdId);
}
