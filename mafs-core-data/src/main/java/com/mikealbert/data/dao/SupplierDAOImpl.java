package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;
import com.mikealbert.data.entity.Supplier;
import com.mikealbert.exception.MalException;

public class SupplierDAOImpl extends GenericDAOImpl<Supplier, Long> implements SupplierDAOCustom {
	@Resource
	private SupplierDAO supplierDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1127166964996701610L;

	@Override
	public List<Object[]> getSupplierAddressByType(Long cId, String accountType, String accountCode, String addressType)
			throws MalException {

		String queryString = "SELECT sup.supplier_name, sup.telephone_number, supadd.address_line_1, supadd.address_line_2, "
				+ " INITCAP(supadd.town_city), supadd.region, supadd.postcode, supadd.country"
				+ " FROM suppliers sup, supplier_addresses supadd"
				+ " WHERE sup.sup_id = supadd.sup_sup_id"
				+ " AND sup.ea_account_code = :accountCode"
				+ " AND sup.ea_account_type = :accountType"
				+ " AND sup.ea_c_id = :cId"
				+ " AND UPPER(supadd.address_type) = UPPER(:addressType)"
				+ " AND supadd.default_ind = 'Y'";

		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		query.setParameter("addressType", addressType);

		List<Object[]> resultList = (List<Object[]>) query.getResultList();

		return resultList;
	}

	@Override
	public List<Object> getSupplierByMakeCodeOrName(String makeCode, String manufacturer) throws MalException{

		String queryString = "SELECT sup_id " +
				" FROM suppliers" +
				" WHERE sup_id in" +
				"              (SELECT DISTINCT(sup_sup_id)" +
				"                 FROM supplier_franchises" +
				"                WHERE ltrim(trim(make_code) , '0') = ltrim(trim(:makeCode) , '0')  " +
				"                  AND MAK_id in (select mak_id from makes where lower(make_desc) = lower(:manufacturer) )) " +
				" AND supplier_category = 'PUR_DLR' " + 
				" AND sstc_service_type_code = 'DEALER' " +
				" AND inactive_ind = 'N' ";

		
		Query query = entityManager.createNativeQuery(queryString);
		query.setParameter("makeCode", makeCode);
		query.setParameter("manufacturer", manufacturer);

		List<Object> resultList = (List<Object>) query.getResultList();

		if(resultList.isEmpty()){
			return null;
		}else{
			return resultList;
		}
	}
	
}
