package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.vo.VendorLovVO;

public class VendorSearchDAOImpl extends GenericDAOImpl<ServiceProvider, Long> implements VendorSearchDAOCustom {

	private static final long serialVersionUID = 1L;
	
	@Resource private ExternalAccountDAO externalAccountDAO;
	
	public  List<VendorLovVO> getVendorList(Long contextId, String vendorType, String vendorCodeOrName,Pageable pageable) {		
				
		Query query = generateQueryToGetVendorList(contextId, vendorType, vendorCodeOrName  , false);
			
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		
		return populateVendorResultList(resultList);
	}
	
	public int getVendorListCount(Long contextId, String vendorType, String vendorCodeOrName) {
		
		int recordCount = 0;		
		Query query = generateQueryToGetVendorList(contextId, vendorType, vendorCodeOrName  , true);
		Object result = (Object)query.getSingleResult();		
		if (result != null) {
			recordCount = Integer.parseInt(String.valueOf(result));
		}
		
		return recordCount;
	}
	
	private List<VendorLovVO> populateVendorResultList(List<Object[]> resultList ){
		
		List<VendorLovVO> vendorList = new ArrayList<VendorLovVO>();
		
		for (Object[] record : resultList) {
			int i = 0;
			VendorLovVO vendorLovVO = new VendorLovVO();
		
			vendorLovVO.setVendorCode(record[i] != null ? (String) record[i] : null);		
			vendorLovVO.setVendorName(record[i += 1] != null ? (String) record[i] : null);
			vendorLovVO.setAddressLine1(record[i += 1] != null ? (String) record[i] : null);		
			vendorLovVO.setAddressLine2(record[i += 1] != null ? (String) record[i] : null);			
			vendorLovVO.setAddressLine3(record[i += 1] != null ? (String) record[i] : null);		
			vendorLovVO.setAddressLine4(record[i += 1] != null ? (String) record[i] : null);			
			vendorLovVO.setPostCode(record[i += 1] != null ? (String) record[i] : null);			
			vendorLovVO.setTownCity(record[i += 1] != null ? (String) record[i] : null);			
			vendorLovVO.setRegion(record[i += 1] != null ? (String) record[i] : null);			
			vendorLovVO.setPhoneNumber(record[i += 1] != null ? (String) record[i] : null);
			
			vendorList.add(vendorLovVO);
		}
		
		return vendorList;
	}
	
	private List<VendorLovVO> populateOrderingOrDeliveringResultList(List<Object[]> resultList){
		
		List<VendorLovVO> vendorList = new ArrayList<VendorLovVO>();
		for (Object[] record : resultList) {
			int i = 0;
			VendorLovVO vendorLovVO = new VendorLovVO();
		
			vendorLovVO.setRowId(((BigDecimal)record[i]).longValue());
			vendorLovVO.setVendorCode(record[i += 1] != null ? (String) record[i] : null);		
			vendorLovVO.setVendorName(record[i += 1] != null ? (String) record[i] : null);
			vendorLovVO.setAddressLine1(record[i += 1] != null ? (String) record[i] : null);		
			vendorLovVO.setAddressLine2(record[i += 1] != null ? (String) record[i] : null);			
			vendorLovVO.setPostCode(record[i += 1] != null ? (String) record[i] : null);			
			vendorLovVO.setTownCity(record[i += 1] != null ? (String) record[i] : null);			
			vendorLovVO.setRegion(record[i += 1] != null ? (String) record[i] : null);			
			vendorLovVO.setPhoneNumber(record[i += 1] != null ? (String) record[i] : null);
			vendorLovVO.setEaAccountCode(record[i += 1] != null ? (String) record[i] : null);
			
			vendorList.add(vendorLovVO);
		}
		
		return vendorList;
	}

	
	private  Query  generateQueryToGetVendorList(Long contextId, String vendorType, String vendorCodeOrName , boolean isCount){
		
		String selectClause = "SELECT ea.account_code, ea.account_name, eaa.address_line_1,eaa.address_line_2,eaa.address_line_3,eaa.address_line_4,eaa.postcode, eaa.town_city, eaa.region, ea.telephone_number " ;
		if(isCount){
			selectClause = "SELECT count(1) ";
		}
		String stmt = selectClause
					
						+ " FROM external_accounts ea, ext_acc_addresses eaa "
						+ " WHERE ea.c_id = :cId AND ea.account_type =  :vendorType AND eaa.address_type = 'POST' "
						+ " AND ea.account_code != 'CASHBOOK' AND ea.c_id = eaa.c_id "
						+ " AND ea.account_type = eaa.account_type	AND ea.account_code = eaa.account_code "
						+ " AND ( UPPER(ea.account_code) like UPPER(:vendorCodeOrName)  OR  UPPER(ea.account_name) like UPPER(:vendorCodeOrName)  )"
						+ " order by ea.account_name asc ";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("cId", contextId);
		query.setParameter("vendorType", vendorType);
		query.setParameter("vendorCodeOrName", vendorCodeOrName);
		
		return query;
		
	}
	
	private  Query  generateQueryToGetOrderingOrDeliveringrList(String vendorCodeOrName,  String vendorWorkshopType , boolean isCount){
		
		String selectClause = "SELECT sup.sup_id, sup.supplier_code, sup.supplier_name, supadd.address_line_1, supadd.address_line_2 , supadd.postcode, supadd.town_city, supadd.region, sup.telephone_number, "
								+ " sup.ea_account_code ";
		if(isCount){
			selectClause = "SELECT count(1) ";
		}
		StringBuilder stmt = new StringBuilder(selectClause) 
							.append("  FROM  suppliers sup , external_accounts ea , supplier_addresses supadd , supplier_workshops suppws    ")
							.append("  WHERE sup.ea_c_id = ea.c_id AND sup.ea_account_type = ea.account_type AND sup.ea_account_code = ea.account_code ")
							.append("  AND sup.sup_id = supadd.sup_sup_id(+)   ")
							.append("  AND sup.sup_id =  suppws.sup_sup_id   ")
							.append("  AND sup.ea_account_type = 'S'   ")
							.append("  AND ea.acc_status ='O'   ")
							.append("  AND supadd.address_type (+) = 'POST'   ")
							.append("  AND supadd.default_ind (+) = 'Y'   ")
							.append("  AND NVL(sup.inactive_ind, 'N') = 'N' ")
							.append( " AND suppws.workshop_capability = :vendorWorkshopType ")
							.append( " AND ( UPPER(sup.supplier_code) like UPPER(:vendorCodeOrName) OR  UPPER(sup.supplier_name) like UPPER(:vendorCodeOrName)  )")
							.append( " ORDER BY sup.supplier_name ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		
		query.setParameter("vendorWorkshopType", vendorWorkshopType);
		query.setParameter("vendorCodeOrName", vendorCodeOrName);
		
		
		return query;
	}
	
	public List<VendorLovVO> getOrderingOrDeliveringVendors(String vendorCodeOrName, String vendorWorkshopType, Pageable pageable) {		
				
		Query query = generateQueryToGetOrderingOrDeliveringrList( vendorCodeOrName,  vendorWorkshopType, false);		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}	
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		
		return populateOrderingOrDeliveringResultList(resultList);
	}
	
	public int getOrderingOrDeliveringVendorsCount(String vendorCodeOrName, String vendorWorkshopType) {
		
		int recordCount = 0;
		Query query = generateQueryToGetOrderingOrDeliveringrList(vendorCodeOrName, vendorWorkshopType, true);	
		Object result = (Object)query.getSingleResult();
		if (result != null) {
			recordCount = Integer.parseInt(String.valueOf(result));
		}
		
		return recordCount;
	}



}
