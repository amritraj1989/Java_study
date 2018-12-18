package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Date;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.PurchaseOrderReleaseQueueV;
import com.mikealbert.data.entity.Supplier;
import com.mikealbert.data.vo.DbProcParamsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.util.MALUtilities;

/**
* This class will be used to perform database transactions via the EntityManager.
* @author Amritraj
*/
public class PurchaseOrderReleaseQueueDAOImpl extends GenericDAOImpl<PurchaseOrderReleaseQueueV, Long> implements PurchaseOrderReleaseQueueDAOCustom {
	@Resource DataSource dataSource;
	
	private static final long serialVersionUID = 1L;


	public DbProcParamsVO createPurchaseOrder(Long qmdId, Long cId, String userId ) throws MalBusinessException {
		
		Connection connection = null;
		CallableStatement callableStatement = null;
		DbProcParamsVO parameterVO = new DbProcParamsVO();
			
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ call po_mgr.create_po(?,?,?,?,?) }");
			
			callableStatement.setLong(1, qmdId.longValue());
			callableStatement.setLong(2, cId.longValue());
			callableStatement.setString(3, userId);
			callableStatement.registerOutParameter(4, Types.NUMERIC);
			callableStatement.registerOutParameter(5, Types.VARCHAR);
			
			callableStatement.execute();
			BigDecimal mainPoDocId = callableStatement.getBigDecimal(4);
			String errorMsg = callableStatement.getString(5);
			if(MALUtilities.isNotEmptyString(errorMsg)){
				parameterVO.setMessage(errorMsg);
				parameterVO.setSuccessTrueFalse("false");
			}else{
				parameterVO.setSuccessTrueFalse("true");
				parameterVO.setReturnId(mainPoDocId);
			}
			
			return parameterVO;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
		
    }

	public DbProcParamsVO releaseMainPurchaseOrder(Long docId, Long cId, String userId) throws MalBusinessException {
		
		Connection connection = null;
		CallableStatement callableStatement = null;
		DbProcParamsVO parameterVO = new DbProcParamsVO();
			
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ call po_mgr.release_main_po(?,?,?,?) }");
			
			callableStatement.setLong(1, docId.longValue());
			callableStatement.setLong(2, cId.longValue());
			callableStatement.setString(3, userId);
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			
			callableStatement.execute();
			String errorMsg = callableStatement.getString(4);
			if(MALUtilities.isNotEmptyString(errorMsg)){
				parameterVO.setMessage(errorMsg);
				parameterVO.setSuccessTrueFalse("false");
			}else{
				parameterVO.setSuccessTrueFalse("true");
			}
			
			return parameterVO;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
		
    }

	public DbProcParamsVO confirmPurchaseOrder(Long docId, String userId , Date confirmDate) throws MalBusinessException {
		
		Connection connection = null;
		CallableStatement callableStatement = null;
		DbProcParamsVO parameterVO = new DbProcParamsVO();
			
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ call po_mgr.confirm_po(?,?,?,?) }");
			
			callableStatement.setLong(1, docId);
			callableStatement.setString(2, userId);
			callableStatement.setTimestamp(3, new java.sql.Timestamp(confirmDate.getTime()));
			callableStatement.registerOutParameter(4, Types.VARCHAR);
			
			callableStatement.execute();
			String errorMsg = callableStatement.getString(4);
			if(MALUtilities.isNotEmptyString(errorMsg)){
				parameterVO.setMessage(errorMsg);
				parameterVO.setSuccessTrueFalse("false");
			}else{
				parameterVO.setSuccessTrueFalse("true");
			}
			
			return parameterVO;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
		
    }
	
	public DbProcParamsVO releaseThirdPartyPurchaseOrders(Long docId, String userId) throws MalBusinessException {
		
		Connection connection = null;
		CallableStatement callableStatement = null;
		DbProcParamsVO parameterVO = new DbProcParamsVO();
			
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ call po_mgr.release_third_party_pos(?,?,?) }");
			
			callableStatement.setLong(1, docId);
			callableStatement.setString(2, userId);
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			
			callableStatement.execute();
			String errorMsg = callableStatement.getString(3);
			if(MALUtilities.isNotEmptyString(errorMsg)){
				parameterVO.setMessage(errorMsg);
				parameterVO.setSuccessTrueFalse("false");
			}else{
				parameterVO.setSuccessTrueFalse("true");
			}
			
			return parameterVO;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
		
    }

	@Override
	public List<PurchaseOrderReleaseQueueV> findAllPurchaseOrderReleaseQueueList() {
		List<PurchaseOrderReleaseQueueV> poReleaseQueueList = new ArrayList<PurchaseOrderReleaseQueueV>();
		
		String queryString = "select por.* from purchase_order_release_queue_v por";
		
		Query query = entityManager.createNativeQuery(queryString);
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				PurchaseOrderReleaseQueueV purchaseOrderReleaseQueueV = new PurchaseOrderReleaseQueueV();
				int i = 0;
				
				purchaseOrderReleaseQueueV.setPsoId(object[i]					!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				purchaseOrderReleaseQueueV.setPsgId(object[i+=1]				!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				purchaseOrderReleaseQueueV.setDocId(object[i+=1]				!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				purchaseOrderReleaseQueueV.setMake(object[i+=1]					!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setOrderType(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setUnitNo(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setTrim(object[i+=1]					!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setQmdId(object[i+=1]				!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				purchaseOrderReleaseQueueV.setQuoId(object[i+=1]				!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				purchaseOrderReleaseQueueV.setQuoteNumber(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setClientAccountCode(object[i+=1]	!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setClientAccountName(object[i+=1]	!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setClientAccountCId(object[i+=1]		!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				purchaseOrderReleaseQueueV.setClientAccountType(object[i+=1]	!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setQuoteCreator(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setQuoteRequiredDate(object[i+=1]	!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setPoStatus(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setPoNumber(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setPoReleaseDate(object[i+=1]		!= null ? (Date) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setVendorName(object[i+=1]			!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setToleranceYN(object[i+=2]			!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setToleranceMessage(object[i+=1]		!= null ? (String) 		object[i]			 	: null);
				purchaseOrderReleaseQueueV.setAcquisitionType(object[i+=2]		!= null ? (String) 		object[i]			 	: null);
				
				poReleaseQueueList.add(purchaseOrderReleaseQueueV);
			}
		}
		
		return poReleaseQueueList;
	}
	
public DbProcParamsVO peformPostOrderingDealerChangeUpdates(Long docId, Long orderingSupId) throws MalBusinessException {
		
		Connection connection = null;
		CallableStatement callableStatement = null;
		DbProcParamsVO parameterVO = new DbProcParamsVO();
			
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ call po_mgr.ord_dlr_change_updates(?,?,?) }");
			
			callableStatement.setLong(1, docId.longValue());
			callableStatement.setLong(2, orderingSupId.longValue());			
			callableStatement.registerOutParameter(3, Types.VARCHAR);
			
			callableStatement.execute();
			String errorMsg = callableStatement.getString(3);
			
			if(MALUtilities.isNotEmptyString(errorMsg)){
				parameterVO.setMessage(errorMsg);
				parameterVO.setSuccessTrueFalse("false");
			}else{
				parameterVO.setSuccessTrueFalse("true");
			}
			
			return parameterVO;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
		
    }

	@Override
	public int getNonAutoDataDealerAccWithoutVendorCount(Long qmdId, String autodataAccCode) {
		
		StringBuilder sqlStmt = new StringBuilder("");
		sqlStmt.append("select count(1) "
				+ "from quotation_dealer_accessories qda, dealer_accessories dac, dealer_accessory_codes ac "
				+ "where qda.qmd_qmd_id=:qmdId "
				+ "and qda.c_id is null "
				+ "and qda.account_type is null "
				+ "and qda.account_code is null "
				+ "and qda.dac_dac_id = dac.dac_id "
				+ "and dac.accessory_code = ac.accessory_code "
				+ "and ac.category_code not in (").append(autodataAccCode).append(")");
		
		Query query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("qmdId", qmdId);
		
		int count = ((BigDecimal) query.getSingleResult()).intValue();
		return count;
	}
	
	@Override
	public List<Supplier> getPreferredSupplierByQmdId(Long qmdId) {
		
		List<Supplier> preferredSupplierList = new ArrayList<Supplier>();
	
		String queryString = "SELECT s.sup_id, s.ea_c_id, s.ea_account_type, s.ea_account_code, s.supplier_name, s.supplier_code, " 
				+ "(select 'Y' from SUPPLIER_WORKSHOPS where WORKSHOP_CAPABILITY = 'ORDERING' and SUP_SUP_ID = S.SUP_ID) as ORDERING_WORKSHOP_CAP "
				+ "FROM ext_acc_pref_suppliers eaps, external_accounts ea, suppliers s, quotation_models qmd "
				+ "WHERE eaps.account_code = ea.account_code "
				+ "AND eaps.account_type = ea.account_type "
				+ "AND eaps.c_id = ea.c_id "
				+ "AND eaps.account_code = (select account_code from quotations where quo_id=qmd.quo_quo_id) "
				+ "AND eaps.account_type_supp = s.ea_account_type "
				+ "AND eaps.c_id_supp = s.ea_c_id " 
				+ "AND eaps.account_code_supp = s.ea_account_code "
				+ "AND eaps.account_type_supp = 'S' "
				+ "AND s.inactive_ind = 'N' "
				+ "AND qmd.qmd_id = :qmdId";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);
		
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				Supplier sup = new Supplier();
				int i = 0;
				sup.setSupId(object[i]							!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				sup.setEaCId(object[i+=1]						!= null ? ((BigDecimal) object[i]).longValue() 	: null);
				sup.setEaAccountType(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				sup.setEaAccountCode(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				sup.setSupplierName(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				sup.setSupplierCode(object[i+=1]				!= null ? (String) 		object[i]			 	: null);
				sup.setOrderingWorkShopCapability(object[i+=1]	!= null ? MALUtilities.convertYNToBoolean(object[i].toString()) : false);
				
				preferredSupplierList.add(sup);
			}
		}
		
		return preferredSupplierList;
	}

	@Override
	public List<ExternalAccount> getPreferredVendorByQmdId(Long qmdId) {
		
		List<ExternalAccount> resultAccount = new ArrayList<ExternalAccount>();
		String queryString = "select ea.c_id, ea.account_type, ea.account_code, ea.account_name "
				+ "from ext_acc_pref_suppliers eaps, external_accounts ea, quotation_models qmd "
				+ "where eaps.account_code = (select account_code from quotations where quo_id=qmd.quo_quo_id) "
				+ "and ea.c_id = eaps.c_id_supp "
				+ "and ea.account_type = eaps.account_type_supp "
				+ "and ea.account_code = eaps.account_code_supp "
				+ "and eaps.account_type_supp = 'S' "
				+ "and qmd.qmd_id = :qmdId ";
		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("qmdId", qmdId);
		
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		
		if (objectList != null && objectList.size() > 0) {	
			
			for (Object[] objects : objectList) {
				ExternalAccount account = new ExternalAccount();
				ExternalAccountPK id = new ExternalAccountPK();
				id.setCId(((BigDecimal)objects[0]).longValue());
				id.setAccountType((String)objects[1]);
				id.setAccountCode((String)objects[2]);
				account.setExternalAccountPK(id);
				account.setAccountName((String)objects[3]);
				resultAccount.add(account);
			}
		}
		return resultAccount;
	}		
}
