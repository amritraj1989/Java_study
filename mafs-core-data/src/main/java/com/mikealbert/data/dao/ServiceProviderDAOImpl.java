package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.vo.ServiceProviderDELVO;
import com.mikealbert.data.vo.ServiceProviderDILVO;
import com.mikealbert.data.vo.ServiceProviderLOVVO;
import com.mikealbert.data.vo.ServiceProviderMaintenanceGroup;
import com.mikealbert.data.vo.ServiceProviderSELVO;
import com.mikealbert.data.vo.ServiceProviderSILVO;
import com.mikealbert.util.MALUtilities;

public  class ServiceProviderDAOImpl extends GenericDAOImpl<Driver, Long> implements ServiceProviderDAOCustom {
	@Resource
	private ServiceProviderDAO serviceProviderDAO;
	
	private static final long serialVersionUID = 1L;	
	private MalLogger logger = MalLoggerFactory.getLogger(ServiceProviderDAOImpl.class);
	
	@Resource WillowConfigDAO willowConfig;
	
	public int searchLOVServiceProviderCount(String provider, String payee, String zipCode, String phoneNumber,String serviceTypeCode, boolean includeOnlyParents) {
		
		Query countQuery = generateSearchLOVServiceProviderQuery(provider, payee, zipCode, phoneNumber,serviceTypeCode, includeOnlyParents, true);
		BigDecimal count = (BigDecimal) countQuery.getSingleResult();
		
		return count.intValue();
	}

	public List<ServiceProviderLOVVO> searchLOVServiceProvider(String provider, String payee, String zipCode, String phoneNumber,String serviceTypeCode, boolean includeOnlyParents, PageRequest pageReq) {
		
		Query query = generateSearchLOVServiceProviderQuery(provider, payee, zipCode, phoneNumber,serviceTypeCode, includeOnlyParents, false);
		
		if(pageReq != null){
			query.setFirstResult(pageReq.getPageNumber() * pageReq.getPageSize());
			query.setMaxResults(pageReq.getPageSize());
		}
		List<ServiceProviderLOVVO>  serviceProviderVOList = new ArrayList<ServiceProviderLOVVO>();
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				ServiceProviderLOVVO serviceProviderVO = new ServiceProviderLOVVO();
				
				serviceProviderVO.setServiceProviderId(object[0]  				!= null ? (BigDecimal) object[0] : null);
				serviceProviderVO.setServiceProviderName(object[1]     			!= null ? (String) object[1]  : null);
				serviceProviderVO.setServiceProviderNumber(object[2]   			!= null ? (String) object[2]  : null);
				serviceProviderVO.setServiceProviderTelephoneNumber(object[3]  	!= null ? (String) object[3]  : null);
				serviceProviderVO.setServiceProviderAddress1(object[4] 			!= null ? (String) object[4]  : null);
				serviceProviderVO.setServiceProviderAddress2(object[5] 			!= null ? (String) object[5]  : null);
				serviceProviderVO.setServiceProviderAddress3(object[6] 			!= null ? (String) object[6]  : null);				
				serviceProviderVO.setServiceProviderAddress4(object[7] 			!= null ? (String) object[7]  : null);
				serviceProviderVO.setServiceProviderPostcode(object[8] 			!= null ? (String) object[8]  : null);
				serviceProviderVO.setServiceProviderTownCity(object[9] 			!= null ? (String) object[9]  : null);
				serviceProviderVO.setServiceProviderRegion(object[10]  			!= null ? (String) object[10]  : null);
				serviceProviderVO.setServiceProviderCountry(object[11]			!= null ? (String) object[11] : null);
				serviceProviderVO.setPayeeName(object[12]     					!= null ? (String) object[12] : null);
				serviceProviderVO.setPayeeNumber(object[13]   					!= null ? (String) object[13] : null);
				serviceProviderVO.setPayeeTelephoneNumber(object[14]  			!= null ? (String) object[14] : null);
				serviceProviderVO.setPayeeAddress1(object[15] 					!= null ? (String) object[15] : null);
				serviceProviderVO.setPayeeAddress2(object[16] 					!= null ? (String) object[16] : null);
				serviceProviderVO.setPayeeAddress3(object[17] 					!= null ? (String) object[17] : null);				
				serviceProviderVO.setPayeeAddress4(object[18] 					!= null ? (String) object[18] : null);
				serviceProviderVO.setPayeePostcode(object[19] 					!= null ? (String) object[19] : null);
				serviceProviderVO.setPayeeTownCity(object[20] 					!= null ? (String) object[20] : null);
				serviceProviderVO.setPayeeRegion(object[21]   					!= null ? (String) object[21] : null);
				serviceProviderVO.setPayeeCountry(object[22]  					!= null ? (String) object[22] : null);
				serviceProviderVO.setServiceType(object[23]  					!= null ? (String) object[23] : null);
				serviceProviderVOList.add(serviceProviderVO);
			}
		}
		
		return serviceProviderVOList;
		
	}

	public List<ServiceProviderSILVO> searchSILServiceProvider(String latitude, String longitude, String radius, String vehicleType, String repairType, String shopCategory, String parentGroupId) {
		Query query = generateSearchSILServiceProviderQuery(latitude, longitude, radius, vehicleType, repairType, shopCategory, parentGroupId);
		List<ServiceProviderSILVO> serviceProviders = new ArrayList<ServiceProviderSILVO>();
		BigDecimal radiusConverted = new BigDecimal(radius);

		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();
				ServiceProviderSILVO serviceProviderSILVO = new ServiceProviderSILVO();

				serviceProviderSILVO.setId((BigDecimal) object[0]);
				serviceProviderSILVO.setLat((String) object[1]);
				serviceProviderSILVO.setLng((String) object[2]);
				serviceProviderSILVO.setDistance((BigDecimal) object[3]);
				serviceProviderSILVO.setName((String) object[4]);
				serviceProviderSILVO.setBillThroughName((String) object[5]);
				serviceProviderSILVO.setAddress1((String) object[6]);
				serviceProviderSILVO.setAddress2((String) object[7]);
				serviceProviderSILVO.setCity((String) object[8]);
				serviceProviderSILVO.setPostCode((String) object[9]);
				serviceProviderSILVO.setState((String) object[10]);
				serviceProviderSILVO.setTelephone((String) object[11]);
				serviceProviderSILVO.setNetworkVendor((String) object[12]);
				serviceProviderSILVO.setStoreNumber((String) object[13]);				
				serviceProviderSILVO.setDiscount((BigDecimal) object[14]);
				serviceProviderSILVO.setYearlyEventCount((BigDecimal) object[15]);
				serviceProviderSILVO.setLastEventDate((String) object[16]);

				if(serviceProviderSILVO.getDistance().compareTo(radiusConverted) <= 0) {
					serviceProviders.add(serviceProviderSILVO);
				}
			}
		}

		return serviceProviders;
	}

	public List<ServiceProviderSELVO> searchSELServiceProvider(String latitude, String longitude, String radius, String vehicleType, String repairType, String parentGroupId) {
		Query query = generateSearchSELServiceProviderQuery(latitude, longitude, radius, vehicleType, repairType, parentGroupId);
		List<ServiceProviderSELVO> serviceProviders = new ArrayList<ServiceProviderSELVO>();
		BigDecimal radiusConverted = new BigDecimal(radius);

		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();
				ServiceProviderSELVO serviceProviderSELVO = new ServiceProviderSELVO();

				serviceProviderSELVO.setId((BigDecimal) object[0]);
				serviceProviderSELVO.setLat((String) object[1]);
				serviceProviderSELVO.setLng((String) object[2]);
				serviceProviderSELVO.setDistance((BigDecimal) object[3]);
				serviceProviderSELVO.setName((String) object[4]);
				serviceProviderSELVO.setAddress1((String) object[5]);
				serviceProviderSELVO.setAddress2((String) object[6]);
				serviceProviderSELVO.setCity((String) object[7]);
				serviceProviderSELVO.setPostCode((String) object[8]);
				serviceProviderSELVO.setState((String) object[9]);
				serviceProviderSELVO.setTelephone((String) object[10]);
				
				// the query will only return vendors = "Y" but somehow the comparison converts this to a CHAR which doesn't cast 
				//    to a String using the normal pattern.  To minimize the impact at this time, we are just explicitly setting the value to "Y".
				serviceProviderSELVO.setNetworkVendor("Y");   
				
				if(serviceProviderSELVO.getDistance().compareTo(radiusConverted) <= 0) {
					serviceProviders.add(serviceProviderSELVO);
				}
			}
		}

		return serviceProviders;
	}
	
	public List<ServiceProviderDELVO> searchDELServiceProvider(String latitude,String longitude, String radius, String makId) {
		Query query = generateSearchDELServiceProviderQuery(latitude, longitude, radius, makId);
		List<ServiceProviderDELVO> serviceProviders = new ArrayList<ServiceProviderDELVO>();
		BigDecimal radiusConverted = new BigDecimal(radius);

		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();
				ServiceProviderDELVO serviceProviderDELVO = new ServiceProviderDELVO();

				serviceProviderDELVO.setId((BigDecimal) object[0]);
				serviceProviderDELVO.setLat((String) object[1]);
				serviceProviderDELVO.setLng((String) object[2]);
				serviceProviderDELVO.setDistance((BigDecimal) object[3]);
				serviceProviderDELVO.setName((String) object[4]);
				serviceProviderDELVO.setAddress1((String) object[5]);
				serviceProviderDELVO.setAddress2((String) object[6]);
				serviceProviderDELVO.setCity((String) object[7]);
				serviceProviderDELVO.setPostCode((String) object[8]);
				serviceProviderDELVO.setState((String) object[9]);
				serviceProviderDELVO.setTelephone((String) object[10]);

				if(serviceProviderDELVO.getDistance().compareTo(radiusConverted) <= 0) {
					serviceProviders.add(serviceProviderDELVO);
				}
			}
		}

		return serviceProviders;
	}

	@SuppressWarnings("unchecked")
	public List<ServiceProviderDILVO> searchDILServiceProvider(String latitude,String longitude, String radius, String makId) {
		Query query = generateSearchDILServiceProviderQuery(latitude, longitude, radius, makId);
		Query potentialDDQuery = generateSearchPotentialDILServiceProviderQuery(latitude, longitude, radius, makId);
		List<ServiceProviderDILVO> serviceProviders = new ArrayList<ServiceProviderDILVO>();
		BigDecimal radiusConverted = new BigDecimal(radius);

		
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		List<Object[]> potentialDDObjectList  =  (List<Object[]>) potentialDDQuery.getResultList();
		 objectList.addAll(potentialDDObjectList);
		if (objectList != null && objectList.size() > 0) {	
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();
				ServiceProviderDILVO serviceProviderDILVO = new ServiceProviderDILVO();

				serviceProviderDILVO.setId((BigDecimal) object[0]);
				serviceProviderDILVO.setLat((String) object[1]);
				serviceProviderDILVO.setLng((String) object[2]);
				serviceProviderDILVO.setDistance((BigDecimal) object[3]);
				serviceProviderDILVO.setName((String) object[4]);
				serviceProviderDILVO.setAddress1((String) object[5]);
				serviceProviderDILVO.setAddress2((String) object[6]);
				serviceProviderDILVO.setCity((String) object[7]);
				serviceProviderDILVO.setPostCode((String) object[8]);
				serviceProviderDILVO.setState((String) object[9]);
				serviceProviderDILVO.setTelephone((String) object[10]);
				serviceProviderDILVO.setStoreNumber((String) object[11]);
				serviceProviderDILVO.setBillThroughName((String) object[12]);
				serviceProviderDILVO.setYearlyEventCount((BigDecimal) object[13]);
				serviceProviderDILVO.setLastEventDate((String) object[14]);
				serviceProviderDILVO.setPotentialDIL(Boolean.valueOf((String) object[15]));

				if(serviceProviderDILVO.getDistance().compareTo(radiusConverted) <= 0) {
					serviceProviders.add(serviceProviderDILVO);
				}
			}
		}

		return serviceProviders;
	}

	public List<ServiceProviderMaintenanceGroup> getVLServiceProviderMaintenanceGroups(String isNetworkVendor) {
		String querySlice = (MALUtilities.isEmpty(isNetworkVendor)) ? "" : " AND S.NETWORK_VENDOR = :isNetworkVendor";
		String sql = "SELECT ID, DESCRIPTION" +
					 " FROM (" +
					 "        SELECT S.SUP_ID AS ID, S.SUPPLIER_NAME AS DESCRIPTION" +
					 "        FROM WILLOW2k.SUPPLIERS S" +
					 "		    JOIN WILLOW2k.SUPPLIERS S1 ON S.SUP_ID = S1.SUP_SUP_ID AND S1.INACTIVE_IND = 'N' AND S1.SSTC_SERVICE_TYPE_CODE = 'MAINT'" +
					 "        WHERE S.SUP_SUP_ID IS NULL" +
					 querySlice +
					 "        GROUP BY S.SUP_ID, S.SUPPLIER_NAME" +
					 "        HAVING COUNT(S1.SUP_ID) > 0" +
					 " ) ORDER BY DESCRIPTION";
		Query query = entityManager.createNativeQuery(sql);
		if(!MALUtilities.isEmpty(isNetworkVendor)) { query.setParameter("isNetworkVendor", isNetworkVendor); }
		List<ServiceProviderMaintenanceGroup> serviceProviderGroups = new ArrayList<ServiceProviderMaintenanceGroup>();

		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();
				ServiceProviderMaintenanceGroup serviceProviderGroup = new ServiceProviderMaintenanceGroup();

				serviceProviderGroup.setId((BigDecimal) object[0]);
				serviceProviderGroup.setDescription((String) object[1]);

				serviceProviderGroups.add(serviceProviderGroup);
			}
		}

		return serviceProviderGroups;
	}
	
	private Query generateSearchLOVServiceProviderQuery(String provider, String payee, String zipCode, String phoneNumber,String serviceTypeCode, boolean includeOnlyParents, Boolean isCountOnly) {		
		Query query =  null;
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		StringBuilder sqlStmt = new StringBuilder("");
		
		String payeeNumber = "";
		String payeeName = "";
		String phoneNumberAltFmt = "";
		
		/*   Determines whether the pattern fits with a payee number or name
		 *   "(\\%)?    1.Optional Wildcard
		 *   +\\d*      2.Required Digit
		 *   +(\\d*)?   3.Optional Digit
		 *   In order to search by code, there must be a digit. Wildcards can occur anywhere 
		 *   between digits. Wildcards can occur before the digit string and after. Client codes are 8 digits long*/
		
		if(MALUtilities.isNotEmptyString(payee) &&
				payee.matches("(\\%)?+\\d*+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?+(\\d*)?+(\\%)?")) {
			payeeNumber = payee;
		} else {
			payeeName = payee;
		}
		
		/* Create phoneNumberAltFmt by modifying the primary (user input) format (123)456-7890 to be the alt 123-456=7890 */
		if(MALUtilities.isNotEmptyString(phoneNumber)){
			phoneNumberAltFmt = phoneNumber.replace("(", "");
			phoneNumberAltFmt = phoneNumberAltFmt.replace(")", "-");
		}
		
		if(isCountOnly){
			sqlStmt.append("SELECT COUNT(1)");
		}
		else{
			sqlStmt.append("SELECT sup.sup_id, sup.supplier_name sup_supplier_name, sup.supplier_code sup_supplier_code, sup.telephone_number sup_telephone_number," + 
                			" supadd.address_line_1 sup_address_line_1, supadd.address_line_2 sup_address_line_2, supadd.address_line_3 sup_address_line_3, supadd.address_line_4 sup_address_line_4," +
                			" supadd.postcode sup_postcode, supadd.town_city sup_town_city, supadd.region sup_region, supadd.country sup_country," +
                			" ea.account_name account_name, ea.account_code, ea.telephone_number ea_telephone_number," +
                			" eaadd.address_line_1 ea_address_line_1, eaadd.address_line_2 ea_address_line_2, eaadd.address_line_3 ea_address_line_3, eaadd.address_line_4 ea_address_line_4," +
                			" eaadd.postcode ea_postcode, eaadd.town_city ea_town_city, eaadd.region ea_region, eaadd.country ea_country,sup.SSTC_SERVICE_TYPE_CODE ");
		}
                
		sqlStmt.append( " FROM suppliers sup, supplier_addresses supadd, external_accounts ea, ext_acc_addresses eaadd" +
			            " WHERE sup.sup_id = supadd.sup_sup_id (+)" +
			            " AND sup.ea_account_code = ea.account_code" +
			            " AND sup.ea_account_type = ea.account_type" +
			            " AND sup.ea_c_id = ea.c_id" +
			            " AND ea.account_code = eaadd.account_code" +
			            " AND ea.account_type = eaadd.account_type" +
			            " AND ea.c_id = eaadd.c_id" + 
			            " AND eaadd.address_type = 'POST'" +
			            " AND supadd.address_type (+) = 'POST'" +
			            " AND eaadd.default_ind = 'Y'" +
			            " AND supadd.default_ind (+) = 'Y'"+
	            		" AND sup.supplier_category <> 'PENDING_NA'"); // Added Category Condition for Bug 16485
		
		if(includeOnlyParents){
			sqlStmt.append( " AND sup.NETWORK_VENDOR = 'Y' AND sup.SUP_SUP_ID IS NULL ");
		}
		
		if(MALUtilities.isNotEmptyString(provider)){	            
			sqlStmt.append( " AND (LOWER(sup.supplier_name) LIKE LOWER(:serviceProviderName)" +
				            " OR LOWER(sup.supplier_code) LIKE LOWER(:serviceProviderNo))");
		}
		
		if(MALUtilities.isNotEmptyString(zipCode)){	            
			sqlStmt.append(" AND supadd.POSTCODE LIKE :zipCode");
		}
		
		if(MALUtilities.isNotEmptyString(phoneNumber)){	            
			sqlStmt.append( " AND (sup.TELEPHONE_NUMBER LIKE :phoneNumber" +
		            " OR sup.TELEPHONE_NUMBER LIKE :phoneNumberAltFmt)");
		}

		if(MALUtilities.isNotEmptyString(payeeName)){	            
			sqlStmt.append( " AND LOWER(ea.account_name) LIKE LOWER(:payeeName) ");
		}
		if(MALUtilities.isNotEmptyString(payeeNumber)){	            
			sqlStmt.append(" AND ea.account_code LIKE :payeeNumber" );
		}
		if(MALUtilities.isNotEmptyString(serviceTypeCode)){	            
			sqlStmt.append(" AND sup.SSTC_SERVICE_TYPE_CODE = :serviceTypeCode" );
		}
		
		sqlStmt.append( " ORDER BY sup.supplier_name");

		logger.debug("Final Query: " + sqlStmt.toString());  
		query = entityManager.createNativeQuery(sqlStmt.toString());	

		if(MALUtilities.isNotEmptyString(provider)){	
			parameterMap.put("serviceProviderNo", DataUtilities.appendWildCardToRight(provider));
			parameterMap.put("serviceProviderName", DataUtilities.appendWildCardToRight(provider));	
		}
		if(MALUtilities.isNotEmptyString(zipCode)){	            
			parameterMap.put("zipCode", DataUtilities.appendWildCardToRight(zipCode));
		}
		if(MALUtilities.isNotEmptyString(phoneNumber)){	            
			parameterMap.put("phoneNumber", DataUtilities.appendWildCardToRight(phoneNumber));	
			parameterMap.put("phoneNumberAltFmt", DataUtilities.appendWildCardToRight(phoneNumberAltFmt));
		}
		if(MALUtilities.isNotEmptyString(payeeName)){	            
			parameterMap.put("payeeName", DataUtilities.appendWildCardToRight(payeeName));
		}
		if(MALUtilities.isNotEmptyString(payeeNumber)){	            
			parameterMap.put("payeeNumber", DataUtilities.appendWildCardToRight(payeeNumber));
		}
		if(MALUtilities.isNotEmptyString(serviceTypeCode)){	            
			parameterMap.put("serviceTypeCode", serviceTypeCode);
		}
		
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 

		return query;
	}

	private Query generateSearchSILServiceProviderQuery(String latitude, String longitude, String radius, String vehicleType, 
			String repairType, String shopCategory, String parentGroupId) {
		Query query = null;
		String vehicleTypeQuerySlice = "";
		if(!MALUtilities.isEmpty(vehicleType)) {
			vehicleTypeQuerySlice = 
				"  JOIN" +
				"  (" +
				"    SELECT S.SUP_ID" +
				"    FROM WILLOW2k.SUPPLIERS S" +
				"      JOIN WILLOW2k.SUPPLIER_WORKSHOPS SW ON S.SUP_ID = SW.SUP_SUP_ID" +
				"      AND SW.WORKSHOP_CAPABILITY = :vehicleType" +
				"  ) S1 ON S.SUP_ID = S1.SUP_ID";
		}

		String repairTypeQuerySlice = (MALUtilities.isEmpty(repairType)) ? "" : " AND SW.WORKSHOP_CAPABILITY = :repairType";
		String dealerGroupIdQuerySlice = (MALUtilities.isEmpty(parentGroupId)) ? "" : " AND S.SUP_SUP_ID = :parentGroupId";
		String shopCategoryQuerySlice = "";
		if(!MALUtilities.isEmpty(shopCategory)) {
			shopCategoryQuerySlice = " AND S.SUPPLIER_CATEGORY ";
			if(shopCategory.contains(",")) {
				shopCategoryQuerySlice += "IN (SELECT trim(regexp_substr(CV.CONFIG_VALUE, '[^,]+', 1, LEVEL)) FROM (SELECT SUBSTR(CONFIG_VALUE, 1, INSTR(CONFIG_VALUE, ';')-1) AS CONFIG_VALUE  FROM WILLOW2k.WILLOW_CONFIG WHERE CONFIG_NAME = 'VL_SC_COM_OPTIONS') CV CONNECT BY LEVEL <= regexp_count(CV.CONFIG_VALUE, ',')+1)";
			} else {
				shopCategoryQuerySlice += "= :shopCategory";
			}
		}

		String sql = 
				"SELECT *" +
				" FROM" +
				" (" +
				"   SELECT      US.SUP_ID AS id," +
				"               SAG.LATITUDE AS lat," +
				"               SAG.LONGITUDE AS lng," +
				"               ROUND(P.distance_unit_miles" +
				"                     * P.rad2deg * ACOS(ROUND(COS(ROUND(P.deg2rad * P.latitude, 30))" +
				"                                 * COS(ROUND(P.deg2rad * SAG.LATITUDE, 30))" +
				"                                 * COS(ROUND(P.deg2rad * (P.longitude - SAG.LONGITUDE), 30))" +
				"                                 + SIN(ROUND(P.deg2rad * P.latitude, 30))" +
				"                                 * SIN(ROUND(P.deg2rad * SAG.LATITUDE, 30)), 30)), 1) AS distance," +
				"               US.SUPPLIER_NAME AS name," +
				"               US.PAYEE_NAME AS billThroughName," +
				"               INITCAP(SA.ADDRESS_LINE_1) as address1," +
				"               COALESCE(INITCAP(ADDRESS_LINE_2), '') AS address2," +
				"               INITCAP(SA.TOWN_CITY) AS city," +
				"               SA.POSTCODE AS postCode,            " +
				"               SA.REGION AS state," +
				"               US.TELEPHONE_NUMBER AS telephone," +
				"               US.NETWORK_VENDOR AS networkVendor," +
				"               US.SUPPLIER_CODE AS storeNumber," +
				"               COALESCE(COALESCE((SELECT MAX(PARTS_DISC) FROM SUPPLIER_DISCOUNTS SD WHERE SD.SUP_SUP_ID = US.SUP_ID), (SELECT MAX(PARTS_DISC) FROM SUPPLIER_DISCOUNTS SD WHERE SD.SUP_SUP_ID = US.SUP_SUP_ID)), 0) AS discount," +
				"               (SELECT COUNT(*) FROM MAINTENANCE_REQUESTS MR WHERE MR.ACTUAL_END_DATE >= SYSDATE - 365 AND MR.ACTUAL_END_DATE <= SYSDATE AND MR.SUP_SUP_ID = US.SUP_ID) AS yearlyEvents," +
				"               COALESCE(TO_CHAR((SELECT MAX(MR.ACTUAL_END_DATE) FROM MAINTENANCE_REQUESTS MR WHERE MR.ACTUAL_END_DATE <= SYSDATE AND MR.SUP_SUP_ID = US.SUP_ID), 'mm/dd/yyyy'), 'N/A') AS lastEventDate" +
				"   FROM (" +
				"     SELECT S.*" +
				"     FROM" +
				"     (" +
				"       SELECT  S.SUP_ID," +
				"               S.SUP_SUP_ID," +
				"               S.SUPPLIER_NAME," +
				"               S.TELEPHONE_NUMBER," +
				"               S.NETWORK_VENDOR," +
				"               S.SUPPLIER_CODE," +
				"               EA.PAYEE_NAME" +
				"       FROM WILLOW2k.SUPPLIERS S" +
				"         JOIN WILLOW2k.SUPPLIER_WORKSHOPS SW ON S.SUP_ID = SW.SUP_SUP_ID" +
				"         JOIN EXTERNAL_ACCOUNTS EA on S.EA_ACCOUNT_CODE = EA.ACCOUNT_CODE" +
				"       WHERE" +
				"         S.INACTIVE_IND = 'N'" +
				"         AND S.SSTC_SERVICE_TYPE_CODE = 'MAINT'" +
				repairTypeQuerySlice +
				shopCategoryQuerySlice +
				dealerGroupIdQuerySlice +
				"       GROUP BY S.SUP_ID, S.SUP_SUP_ID, S.SUPPLIER_NAME, S.TELEPHONE_NUMBER, S.NETWORK_VENDOR, S.SUPPLIER_CODE, EA.PAYEE_NAME) S" +
				vehicleTypeQuerySlice +
				"   ) US" +
				"     JOIN (" +
				"       SELECT  :latitude   AS latitude," +
				"               :longitude  AS longitude," +
				"               :radius     AS radius," +
				"               69          AS distance_unit_miles," +
				"               57.2958     AS rad2deg," +
				"               0.0174533   AS deg2rad" +
				"       FROM DUAL" +
				"     ) P ON 1 = 1" +
				"     JOIN SUPPLIER_ADDRESSES SA ON US.SUP_ID = SA.SUP_SUP_ID AND SA.ADDRESS_TYPE = 'POST'" +
				"     JOIN SUPPLIER_ADDRESS_GEOLOCATIONS SAG ON SA.SUA_ID = SAG.SUA_SUA_ID AND SAG.MAFS_QUALITY_SCORE >= 80" +
				"   WHERE" +
				"     SAG.LATITUDE" +
				"      BETWEEN P.latitude  - (P.radius / P.distance_unit_miles)" +
				"          AND P.latitude  + (P.radius / P.distance_unit_miles)" +
				"     AND SAG.LONGITUDE" +
				"      BETWEEN P.longitude - (p.radius / (P.distance_unit_miles * COS(P.deg2rad * P.latitude)))" +
				"          AND P.longitude + (p.radius / (P.distance_unit_miles * COS(P.deg2rad * P.latitude)))" +
				"   ORDER By distance, discount DESC" +
				" )" +
				" WHERE ROWNUM <= 250";

		query = entityManager.createNativeQuery(sql);

		query.setParameter("latitude", latitude);
		query.setParameter("longitude", longitude);
		query.setParameter("radius", radius);
		if(!MALUtilities.isEmpty(repairType)) {
			query.setParameter("repairType", repairType);
		}
		if(!MALUtilities.isEmpty(vehicleType)) {
			query.setParameter("vehicleType", vehicleType);
		}
		if(!MALUtilities.isEmpty(shopCategory) && !shopCategory.contains(",")) {
			query.setParameter("shopCategory", shopCategory);
		}
		if(!MALUtilities.isEmpty(parentGroupId)) {
			query.setParameter("parentGroupId", parentGroupId);
		}

		return query;
	}

	private Query generateSearchSELServiceProviderQuery(String latitude, String longitude, String radius, String vehicleType, String repairType, String parentGroupId) {
		Query query = null;

		String dealerGroupIdQuerySlice = (MALUtilities.isEmpty(parentGroupId)) ? "" : " AND S.SUP_SUP_ID = :parentGroupId";
		String repairTypeQuerySlice = "";
		if(!MALUtilities.isEmpty(repairType)) {
			repairTypeQuerySlice = " AND SW.WORKSHOP_CAPABILITY ";
			if(repairType.contains(",")) {
				repairTypeQuerySlice += "IN (SELECT WCC.WORKSHOP_CAPABILITY FROM WILLOW2k.WORKSHOP_CAPABILITY_CODES WCC JOIN WILLOW2k.WORKSHOP_CAPABILITY_LINKS WCL ON WCL.WORKSHOP_CAPABILITY = WCC.WORKSHOP_CAPABILITY JOIN WILLOW2k.WORKSHOP_CAPABILITY_GROUPS WCG ON WCG.WCG_ID = WCL.WCG_WCG_ID WHERE WCG.GROUP_NAME = 'VL_EXTERNAL_REPAIR_TYPE_PREVENTIVE')";
			} else {
				repairTypeQuerySlice += "= :repairType";
			}
		}

		String vehicleTypeQuerySlice = "";
		if(MALUtilities.isEmpty(vehicleType)) {
			vehicleTypeQuerySlice = 
				    " AND SW.WORKSHOP_CAPABILITY IN ('ALL_MAKES', 'LT_TRK')";
		} else {
			vehicleTypeQuerySlice = 
					" AND SW.WORKSHOP_CAPABILITY = :vehicleType";
		}

		String sql = 
				"SELECT *" +
				" FROM" +
				" (" +
				"   SELECT      US.SUP_ID AS id," +
				"               SAG.LATITUDE AS lat," +
				"               SAG.LONGITUDE AS lng," +
				"               ROUND(P.distance_unit_miles" +
				"                     * P.rad2deg * ACOS(ROUND(COS(ROUND(P.deg2rad * P.latitude, 30))" +
				"                                 * COS(ROUND(P.deg2rad * SAG.LATITUDE, 30))" +
				"                                 * COS(ROUND(P.deg2rad * (P.longitude - SAG.LONGITUDE), 30))" +
				"                                 + SIN(ROUND(P.deg2rad * P.latitude, 30))" +
				"                                 * SIN(ROUND(P.deg2rad * SAG.LATITUDE, 30)), 30)), 1) AS distance," +
				"               US.SUPPLIER_NAME AS name," +
				"               INITCAP(SA.ADDRESS_LINE_1) as address1," +
				"               COALESCE(INITCAP(ADDRESS_LINE_2), '') AS address2," +
				"               INITCAP(SA.TOWN_CITY) AS city," +
				"               SA.POSTCODE AS postCode,            " +
				"               SA.REGION AS state," +
				"               US.TELEPHONE_NUMBER AS telephone," +
				"               US.NETWORK_VENDOR AS networkVendor" +
				"   FROM (" +
				"     SELECT S.*" +
				"     FROM" +
				"     (" +
				"       SELECT  S.SUP_ID," +
				"               S.SUPPLIER_NAME," +
				"               S.TELEPHONE_NUMBER," +
				"               S.NETWORK_VENDOR" +
				"       FROM WILLOW2k.SUPPLIERS S" +
				"         JOIN WILLOW2k.SUPPLIER_WORKSHOPS SW ON S.SUP_ID = SW.SUP_SUP_ID" +
				"       WHERE" +
				"         S.INACTIVE_IND = 'N'" +
				"         AND S.SSTC_SERVICE_TYPE_CODE = 'MAINT'" +
				"         AND S.NETWORK_VENDOR = 'Y'" +
				repairTypeQuerySlice +
				dealerGroupIdQuerySlice +
				"       GROUP BY S.SUP_ID, S.SUPPLIER_NAME, S.TELEPHONE_NUMBER, S.NETWORK_VENDOR) S" +
			    "       JOIN" +
			    "        (" +
			    "         SELECT DISTINCT S.SUP_ID" +
			    "         FROM WILLOW2k.SUPPLIERS S" +
			    "          JOIN WILLOW2k.SUPPLIER_WORKSHOPS SW ON S.SUP_ID = SW.SUP_SUP_ID" +
				vehicleTypeQuerySlice +
			    "        ) S1 ON S.SUP_ID = S1.SUP_ID" +
				"   ) US" +
				"     JOIN (" +
				"       SELECT  :latitude   AS latitude," +
				"               :longitude  AS longitude," +
				"               :radius     AS radius," +
				"               69          AS distance_unit_miles," +
				"               57.2958     AS rad2deg," +
				"               0.0174533   AS deg2rad" +
				"       FROM DUAL" +
				"     ) P ON 1 = 1" +
				"     JOIN SUPPLIER_ADDRESSES SA ON US.SUP_ID = SA.SUP_SUP_ID AND SA.ADDRESS_TYPE = 'POST'" +
				"     JOIN SUPPLIER_ADDRESS_GEOLOCATIONS SAG ON SA.SUA_ID = SAG.SUA_SUA_ID AND SAG.MAFS_QUALITY_SCORE >= 80" +
				"   WHERE" +
				"     SAG.LATITUDE" +
				"      BETWEEN P.latitude  - (P.radius / P.distance_unit_miles)" +
				"          AND P.latitude  + (P.radius / P.distance_unit_miles)" +
				"     AND SAG.LONGITUDE" +
				"      BETWEEN P.longitude - (p.radius / (P.distance_unit_miles * COS(P.deg2rad * P.latitude)))" +
				"          AND P.longitude + (p.radius / (P.distance_unit_miles * COS(P.deg2rad * P.latitude)))" +
				"   ORDER By distance" +
				" )" +
				" WHERE ROWNUM <= 250";

		query = entityManager.createNativeQuery(sql);

		query.setParameter("latitude", latitude);
		query.setParameter("longitude", longitude);
		query.setParameter("radius", radius);
		if(!MALUtilities.isEmpty(repairType) && !repairType.contains(",")) {
			query.setParameter("repairType", repairType);
		}
		if(!MALUtilities.isEmpty(vehicleType)) {
			query.setParameter("vehicleType", vehicleType);
		}
		if(!MALUtilities.isEmpty(parentGroupId)) {
			query.setParameter("parentGroupId", parentGroupId);
		}

		return query;
	}
	
	private Query generateSearchDELServiceProviderQuery(String latitude, String longitude, String radius, String makId) {
		Query query = null;

		String sql = 
				"SELECT *" +
				" FROM" +
				" (" +
				"   SELECT      US.SUP_ID AS id," +
				"               SAG.LATITUDE AS lat," +
				"               SAG.LONGITUDE AS lng," +
				"               ROUND(P.distance_unit_miles" +
				"                     * P.rad2deg * ACOS(ROUND(COS(ROUND(P.deg2rad * P.latitude, 30))" +
				"                                 * COS(ROUND(P.deg2rad * SAG.LATITUDE, 30))" +
				"                                 * COS(ROUND(P.deg2rad * (P.longitude - SAG.LONGITUDE), 30))" +
				"                                 + SIN(ROUND(P.deg2rad * P.latitude, 30))" +
				"                                 * SIN(ROUND(P.deg2rad * SAG.LATITUDE, 30)), 30)), 1) AS distance," +
				"               US.SUPPLIER_NAME AS name," +
				"               INITCAP(SA.ADDRESS_LINE_1) as address1," +
				"               COALESCE(INITCAP(ADDRESS_LINE_2), '') AS address2," +
				"               INITCAP(SA.TOWN_CITY) AS city," +
				"               SA.POSTCODE AS postCode," +
				"               SA.REGION AS state," +
				"               US.TELEPHONE_NUMBER AS telephone" +
				"   FROM (" +
				"     SELECT  S.SUP_ID," +
				"             S.SUPPLIER_NAME," +
				"             S.TELEPHONE_NUMBER" +
				"     FROM WILLOW2k.SUPPLIERS S" +
				"       JOIN WILLOW2k.SUPPLIER_WORKSHOPS SW ON S.SUP_ID = SW.SUP_SUP_ID" +
				"       JOIN WILLOW2k.SUPPLIER_FRANCHISES SF ON S.SUP_ID = SF.SUP_SUP_ID" +
				"     WHERE" +
				"       SW.WORKSHOP_CAPABILITY = 'DELIVERING'" +
				"       AND S.INACTIVE_IND = 'N'" +
				"       AND SF.MAK_ID = :makId" +
				"   ) US" +
				"     JOIN (" +
				"       SELECT  :latitude   AS latitude," +
				"               :longitude  AS longitude," +
				"               :radius     AS radius," +
				"               69          AS distance_unit_miles," +
				"               57.2958     AS rad2deg," +
				"               0.0174533   AS deg2rad" +
				"       FROM DUAL" +
				"     ) P ON 1 = 1" +
				"     JOIN SUPPLIER_ADDRESSES SA ON US.SUP_ID = SA.SUP_SUP_ID AND SA.ADDRESS_TYPE = 'POST'" +
				"     JOIN SUPPLIER_ADDRESS_GEOLOCATIONS SAG ON SA.SUA_ID = SAG.SUA_SUA_ID AND SAG.MAFS_QUALITY_SCORE >= 80" +
				"   WHERE" +
				"     SAG.LATITUDE" +
				"      BETWEEN P.latitude  - (P.radius / P.distance_unit_miles)" +
				"          AND P.latitude  + (P.radius / P.distance_unit_miles)" +
				"     AND SAG.LONGITUDE" +
				"      BETWEEN P.longitude - (p.radius / (P.distance_unit_miles * COS(P.deg2rad * P.latitude)))" +
				"          AND P.longitude + (p.radius / (P.distance_unit_miles * COS(P.deg2rad * P.latitude)))" +
				"   ORDER By distance" +
				" )" +
				" WHERE ROWNUM <= 250";

		query = entityManager.createNativeQuery(sql);

		query.setParameter("latitude", latitude);
		query.setParameter("longitude", longitude);
		query.setParameter("radius", radius);
		query.setParameter("makId", makId);

		return query;
	}

	private Query generateSearchDILServiceProviderQuery(String latitude, String longitude, String radius, String makId) {
		Query query = null;

		String sql = 
				"SELECT *" +
				" FROM" +
				" (" +
				"   SELECT      US.SUP_ID AS id," +
				"               SAG.LATITUDE AS lat," +
				"               SAG.LONGITUDE AS lng," +
				"               ROUND(P.distance_unit_miles" +
				"                     * P.rad2deg * ACOS(ROUND(COS(ROUND(P.deg2rad * P.latitude, 30))" +
				"                                 * COS(ROUND(P.deg2rad * SAG.LATITUDE, 30))" +
				"                                 * COS(ROUND(P.deg2rad * (P.longitude - SAG.LONGITUDE), 30))" +
				"                                 + SIN(ROUND(P.deg2rad * P.latitude, 30))" +
				"                                 * SIN(ROUND(P.deg2rad * SAG.LATITUDE, 30)), 30)), 1) AS distance," +
				"               US.SUPPLIER_NAME AS name," +
				"               INITCAP(SA.ADDRESS_LINE_1) as address1," +
				"               COALESCE(INITCAP(ADDRESS_LINE_2), '') AS address2," +
				"               INITCAP(SA.TOWN_CITY) AS city," +
				"               SA.POSTCODE AS postCode," +
				"               SA.REGION AS state," +
				"               US.TELEPHONE_NUMBER AS telephone," +
				"               US.SUPPLIER_CODE AS storeNumber," +
	            "               US.PAYEE_NAME AS billThroughName," +
	            "               (SELECT COUNT(*) FROM WILLOW2k.DOC D WHERE D.POSTED_DATE >= SYSDATE - 365 AND D.POSTED_DATE <= SYSDATE AND US.ACCOUNT_CODE = D.SUB_ACC_CODE) AS yearlyEvents," +
	            "               COALESCE(TO_CHAR((SELECT MAX(D.POSTED_DATE) FROM WILLOW2k.DOC D WHERE D.POSTED_DATE <= SYSDATE AND US.ACCOUNT_CODE = D.SUB_ACC_CODE), 'mm/dd/yyyy'), 'N/A') AS lastEventDate," +				
	            "				'FALSE'   AS potentialDIL "+
	            "   FROM (" +
				"     SELECT  S.SUP_ID," +
				"             S.SUPPLIER_NAME," +
				"             S.TELEPHONE_NUMBER," +
				"			  S.SUPPLIER_CODE," +
				"             EA.PAYEE_NAME," +
				"             EA.ACCOUNT_CODE" +
				"     FROM WILLOW2k.SUPPLIERS S" +
				"		JOIN WILLOW2k.EXTERNAL_ACCOUNTS EA ON S.EA_ACCOUNT_CODE = EA.ACCOUNT_CODE" +
				"       JOIN WILLOW2k.SUPPLIER_WORKSHOPS SW ON S.SUP_ID = SW.SUP_SUP_ID" +
				"       JOIN WILLOW2k.SUPPLIER_FRANCHISES SF ON S.SUP_ID = SF.SUP_SUP_ID" +
				"     WHERE" +
				"       SW.WORKSHOP_CAPABILITY = 'DELIVERING'" +
				"       AND S.INACTIVE_IND = 'N'" +
				"       AND SF.MAK_ID = :makId" +
				"   ) US" +
				"     JOIN (" +
				"       SELECT  :latitude   AS latitude," +
				"               :longitude  AS longitude," +
				"               :radius     AS radius," +
				"               69          AS distance_unit_miles," +
				"               57.2958     AS rad2deg," +
				"               0.0174533   AS deg2rad" +
				"       FROM DUAL" +
				"     ) P ON 1 = 1" +
				"     JOIN SUPPLIER_ADDRESSES SA ON US.SUP_ID = SA.SUP_SUP_ID AND SA.ADDRESS_TYPE = 'POST'" +
				"     JOIN SUPPLIER_ADDRESS_GEOLOCATIONS SAG ON SA.SUA_ID = SAG.SUA_SUA_ID AND SAG.MAFS_QUALITY_SCORE >= 80" +
				"   WHERE" +
				"     SAG.LATITUDE" +
				"      BETWEEN P.latitude  - (P.radius / P.distance_unit_miles)" +
				"          AND P.latitude  + (P.radius / P.distance_unit_miles)" +
				"     AND SAG.LONGITUDE" +
				"      BETWEEN P.longitude - (p.radius / (P.distance_unit_miles * COS(P.deg2rad * P.latitude)))" +
				"          AND P.longitude + (p.radius / (P.distance_unit_miles * COS(P.deg2rad * P.latitude)))" +
				"   ORDER By distance" +
				" )" +
				" WHERE ROWNUM <= 250";

		query = entityManager.createNativeQuery(sql);

		query.setParameter("latitude", latitude);
		query.setParameter("longitude", longitude);
		query.setParameter("radius", radius);
		query.setParameter("makId", makId);

		return query;
	}
	
	
	private Query generateSearchPotentialDILServiceProviderQuery(String latitude, String longitude, String radius, String makId) {
		Query query = null;

		String sql = 
				"SELECT *  " +
			" FROM	       " +
				" (																						    "+
				"	SELECT     ps.ps_id                                				AS id,  				"+
				"	           psg.latitude                                         AS lat,  				"+
				"	           psg.longitude                                        AS lng,  				"+
				"	           Round(P.distance_unit_miles * P.rad2deg  									"+
				"					* Acos(  																"+
				"						   Round(Cos(Round(P.deg2rad * P.latitude, 30)) *  					"+
				"								 Cos(Round(P.deg2rad * psg.latitude, 30)) *  				"+
				"								 Cos(Round(P.deg2rad *  									"+
				"										 ( P.longitude - psg.longitude ), 30))    			"+
				"								 + Sin(Round(P.deg2rad * P.latitude, 30) ) *    			"+ 
				"								 Sin(Round(P.deg2rad * psg.latitude, 30)) 					"+ 
				"							 , 30)   														"+ 
				"						),  																"+ 
				"					1)                         						 AS distance,  			"+ 
				"      		   CONCAT( ps.supplier_name , ' (Potential Supplier)')   AS name,  				"+ 
				"	           Initcap(ps.address)                            		 AS address1,  			"+ 
				"	           NULL			     									 AS address2,  			"+ 
				"	           Initcap(ps.town_city)                                 AS city,  				"+ 
				"	           ps.postcode		                                     AS postCode ,  		"+ 
				"	           ps.region                							 AS state,  			"+ 
				"	           ps.telephone_number                       			 AS telephone,  		"+ 
				"	           'Needs Verification'                              	 AS storeNumber,  		"+ 
				"	           NULL                                         		 AS billThroughName,  	"+ 
				"	           NULL         										 AS yearlyEvents,  		"+ 
				"	           NULL   												 AS lastEventDate,  	"+
				"	           'TRUE'   											 AS potentialDIL  		"+
				"	    FROM   willow2k.potential_supplier ps 		 										"+	
				" 			   JOIN willow2k.potential_supplier_geolocation psg 							"+
				"					ON ps.ps_id = psg.ps_ps_id  											"+
				"	           JOIN (SELECT :latitude  AS latitude,  										"+
				"	                        :longitude AS longitude,  										"+
				"	                        :radius    	   AS radius,  										"+
				"	                        69         AS distance_unit_miles,  							"+
				"	                        57.2958    AS rad2deg,  										"+
				"	                        0.0174533  AS deg2rad  											"+
				"						FROM   dual) P  													"+ 
				"	             ON 1 = 1    																"+
				"	    WHERE 	ps.mak_mak_id = :makId 														"+
				"	    		AND psg.mafs_quality_score >= 80 											"+				
				"				AND NVL(ps.supplier_exist_yn,'N') = 'N'										"+
				"				AND psg.latitude  															"+
				"					BETWEEN P.latitude - ( P.radius / P.distance_unit_miles )  				"+
				"	                AND P.latitude + (P.radius / P.distance_unit_miles )  					"+
				"				AND psg.longitude  															"+
				"					BETWEEN  																"+
				"						P.longitude - ( p.radius / ( P.distance_unit_miles * Cos(P.deg2rad * P.latitude) ) )  		"+
				"					AND P.longitude + ( p.radius / ( P.distance_unit_miles * Cos (P.deg2rad * P.latitude) ) ) 		"+
				"	    ORDER  BY distance 		"+
				
				" )" +
				" WHERE ROWNUM <= 250";

		query = entityManager.createNativeQuery(sql);

		query.setParameter("latitude", latitude);
		query.setParameter("longitude", longitude);
		query.setParameter("radius", radius);
		query.setParameter("makId", makId);

		return query;
	}
	
	public String getPotentialDILServiceProviderCode(Long pServiceProviderId) {
		
		Query query = entityManager.createNativeQuery("SELECT MAKE_CODE FROM POTENTIAL_SUPPLIER WHERE PS_ID = :PS_ID");	
		query.setParameter("PS_ID", pServiceProviderId);
		
		return (String) query.getSingleResult();
	}
}
