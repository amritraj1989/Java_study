package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.ServiceProviderAddress;
import com.mikealbert.data.vo.ServiceProviderAddressVO;

public  class ServiceProviderAddressDAOImpl extends GenericDAOImpl<ServiceProviderAddressVO, Long> implements ServiceProviderAddressDAOCustom {
	private static final long serialVersionUID = 1L;	
	private MalLogger logger = MalLoggerFactory.getLogger(ServiceProviderAddressDAOImpl.class);
	
	@Resource WillowConfigDAO willowConfig;

	public List<ServiceProviderAddressVO> findServiceProviderAddressesByListOfIds(List<BigDecimal> ids) {
		
		Query query = generateFindServiceProviderAddressByIdQuery(ids);
		
		List<ServiceProviderAddressVO>  serviceProviderAddressList = new ArrayList<ServiceProviderAddressVO>();;
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			
			for (Object[] record : objectList) {
				int i = 0;			
				ServiceProviderAddressVO serviceProviderAddressVO = new ServiceProviderAddressVO();
				ServiceProviderAddress serviceProviderAddress = new ServiceProviderAddress();
				serviceProviderAddressVO.setServiceProviderId(((BigDecimal)record[i]));
				serviceProviderAddress.setAddressLine1((String)record[i+=1]);
				serviceProviderAddress.setAddressLine2((String)record[i+=1]);
				serviceProviderAddress.setAddressLine3((String)record[i+=1]);
				serviceProviderAddress.setAddressLine4((String)record[i+=1]);
				serviceProviderAddress.setTownCity((String)record[i+=1]);
				serviceProviderAddress.setRegion((String)record[i+=1]);
				serviceProviderAddress.setPostcode((String)record[i+=1]);
				
				serviceProviderAddressVO.setServiceProviderAddress(serviceProviderAddress);
				serviceProviderAddressList.add(serviceProviderAddressVO);
			}
		}
		
		return serviceProviderAddressList;
		
	}
		
	private Query generateFindServiceProviderAddressByIdQuery(List<BigDecimal> ids) {
		Query query =  null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		sqlStmt.append("SELECT sa.sup_sup_id, sa.address_line_1, sa.address_line_2, sa.address_line_3, sa.address_line_4, sa.town_city, sa.region, sa.postcode"
					   +"  FROM supplier_addresses sa "
					   +"   WHERE sa.sup_sup_id IN (:ids) AND "
					   +"         sa.default_ind = 'Y'");

		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("ids", ids);

		return query;
	}
		
}
