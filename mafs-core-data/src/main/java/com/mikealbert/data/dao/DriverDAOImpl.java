package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.vo.DriverInfoVO;
import com.mikealbert.data.vo.DriverLOVVO;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.util.MALUtilities;


public  class DriverDAOImpl extends GenericDAOImpl<Driver, Long> implements DriverDAOCustom {

	@Resource
	private DriverDAO driverDAO;
	
	private static final long serialVersionUID = 1L;	

	public int searchLOVDriverCount(String driverName, List<String> acctCodes, String acctType, String accContextId, boolean fetchOpenAccontOnly , boolean fetchRelatedDriversAlso, boolean fetchActiveDriverOnly) {
		
		Query countQuery = generateSearchLOVDriverQuery( driverName, acctCodes ,acctType ,accContextId , fetchOpenAccontOnly , fetchRelatedDriversAlso,fetchActiveDriverOnly,  null ,  true );
		BigDecimal count = (BigDecimal) countQuery.getSingleResult();
		
		return count.intValue();
	}

	public List<DriverLOVVO> searchLOVDriver(String driverName, List<String> acctCodes, String acctType, String accContextId, boolean fetchOpenAccontOnly , boolean fetchRelatedDriversAlso, boolean fetchActiveDriverOnly, PageRequest pageReq) {
		
		Query query = generateSearchLOVDriverQuery( driverName, acctCodes ,acctType ,accContextId , fetchOpenAccontOnly ,  fetchRelatedDriversAlso,fetchActiveDriverOnly,  pageReq ,  false );
		
		if(pageReq != null){
			query.setFirstResult(pageReq.getPageNumber() * pageReq.getPageSize());
			query.setMaxResults(pageReq.getPageSize());
		}
		List<DriverLOVVO>  driverVOList = new ArrayList<DriverLOVVO>();;
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();				
				DriverLOVVO driverVO = new DriverLOVVO();
				
				driverVO.setDrvId(object[0] != null ? ((BigDecimal) object[0]).longValue() : null);
				driverVO.setDriverForename(object[1] != null ? (String) object[1] : null);
				driverVO.setDriverSurname(object[2] != null ? (String) object[2] : null);
				driverVO.setEmail(object[3] != null ? (String) object[3] : null);
				driverVO.setPoolManager(object[4] != null ? (String) object[4] : null);				
				driverVO.setActiveInd(object[5] != null ? (String) object[5] : null);
				
				driverVO.setAccountName(object[6] != null ? (String) object[6] : null);
				driverVO.setAccountId(object[7] != null ? (String) object[7] : null);
				driverVO.setAccountStatus(object[8] != null ? (String) object[8] : null);
				
				driverVO.setBusinessAddressLine(object[9] != null ? (String) object[9] : null);
				driverVO.setAddressLine1(object[10] != null ? (String) object[10] : null);
				driverVO.setAddressLine2(object[11] != null ? (String) object[11] : null);
				driverVO.setTownCity(object[12] != null ? (String) object[12] : null);
				driverVO.setRegion(object[13] != null ? (String) object[13] : null);
				driverVO.setPostcode(object[14] != null ? (String) object[14] : null);
				
				driverVOList.add(driverVO);
			}
		}
		
		return driverVOList;
		
	}
	public Query generateSearchLOVDriverQuery(String driverName, List<String> acctCodes,String acctType, String accContextId,boolean fetchOpenAccontOnly , boolean fetchRelatedDriversAlso,boolean fetchActiveDriverOnly, PageRequest pageable , boolean isCountOnly) {
		
		Query query =  null;
		
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		String selectData = "SELECT  dr.DRV_ID, dr.DRIVER_FORENAME, dr.DRIVER_SURNAME, dr.EMAIL , dr.POOL_MGR, dr.ACTIVE_IND,"+
							" exAcc.ACCOUNT_NAME , exAcc.ACCOUNT_CODE , exAcc.ACC_STATUS ,"+
							" dadd.BUSINESS_ADDRESS_LINE, dadd.ADDRESS_LINE_1, dadd.ADDRESS_LINE_2, tcc.TOWN_DESCRIPTION, dadd.REGION, dadd.POSTCODE ";
		String selectCount = "SELECT COUNT(1) ";
		String baseQuery = " FROM DRIVERS  dr "+														
							 " INNER JOIN  DRIVER_ADDRESSES  dadd "+
							           " ON dr.drv_id=dadd.drv_drv_id "+
							  " INNER JOIN  TOWN_CITY_CODES  tcc "+
							           " ON tcc.COUNTRY_CODE=dadd.COUNTRY AND tcc.REGION_CODE=dadd.REGION AND tcc.TOWN_NAME=dadd.TOWN_CITY  AND tcc.COUNTY_CODE=dadd.COUNTY_CODE "+
							 " INNER JOIN	EXTERNAL_ACCOUNTS exAcc "+
							           " ON dr.EA_ACCOUNT_CODE = exAcc.ACCOUNT_CODE "+ 
							           " AND dr.Ea_Account_Type = exAcc.Account_Type "+
							           " AND dr.Ea_C_Id = exAcc.C_Id "; 
							

		String selectClause =  isCountOnly == true ? selectCount : selectData;
		
		StringBuilder queryBuilder = new StringBuilder(selectClause).append(baseQuery);
	
		if (MALUtilities.isEmptyString(driverName)){	
			driverName = "%,%";
		}		
		//generate SQL for last name ,first name 
		String lastName =  null;
		String firstName =  null;
		
		String[] nameArray= driverName.split(",");			
		lastName = driverName.split(",")[0];
		if(nameArray.length > 1){			
			firstName =  driverName.split(",")[1];
		}
		queryBuilder.append(" WHERE dadd.ADDRESS_TYPE = 'GARAGED' and dadd.DEFAULT_IND = 'Y'");
		
		if(isNotNull(lastName)){
			
			queryBuilder.append("AND UPPER(dr.DRIVER_SURNAME) LIKE :lastName ");
			
			if(isNotNull(firstName)){				
				parameterMap.put("lastName", lastName.toUpperCase());
			}else {
				parameterMap.put("lastName", setupWildCardAtRight(lastName.toUpperCase()));
			}		
		}
		
		if(isNotNull(firstName)){
			queryBuilder.append("AND UPPER(dr.DRIVER_FORENAME) LIKE :firstName ");			
			parameterMap.put("firstName", setupWildCardAtRight(firstName.trim().toUpperCase()));
		}
			
		
		//generate SQL for last account code and id
		if(acctCodes != null && acctCodes.size() > 0){
			StringBuilder accountCodeBuilder = new StringBuilder(); 
			int index = 0;
			for (String accountCode : acctCodes) {
				accountCodeBuilder.append("'").append(accountCode).append("'");index++;
				if(index  < acctCodes.size())
					accountCodeBuilder.append(",");
			}
			String accountCodes = accountCodeBuilder.toString();
		
			queryBuilder.append(  "AND  exAcc.ACCOUNT_CODE IN ( ").append(accountCodes).append(" )  " +
					" AND  exAcc.ACCOUNT_TYPE = :acctType " +
					" AND  exAcc.C_ID = :accContextId  "  ); 
			parameterMap.put("acctType", acctType);
			parameterMap.put("accContextId", accContextId);
						
		}
		
		//generate SQL for last account code and id
		if(fetchOpenAccontOnly == true){
			queryBuilder.append(  "AND  exAcc.ACC_STATUS  = :accountStatus "  ); 
			parameterMap.put("accountStatus", "O");
		}
		
		if(fetchRelatedDriversAlso == false){
			
			queryBuilder.append(" AND dr.DRV_ID NOT IN  ( "+
	                " SELECT DISTINCT relDr.CHILD_DRV_ID " +
	                " FROM  RELATED_DRIVERS relDr, DRIVERS dr0 " +
	                " WHERE relDr.CHILD_DRV_ID = dr0.DRV_ID ) ");
		}
		
		if(fetchActiveDriverOnly == true){
			queryBuilder.append(" AND dr.ACTIVE_IND = :activeInd ");
			parameterMap.put("activeInd", "Y");
		}
		
		queryBuilder.append(" ORDER BY  dr.DRIVER_SURNAME ASC,  dr.DRIVER_FORENAME ASC,  exAcc.ACCOUNT_NAME ");
		 
		  
		query = entityManager.createNativeQuery(queryBuilder.toString());		 
		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 
		
		return query;
	}
		
	public int searchDriverCount(String driverName, Long driverId, String customerName, String customerNo,String unitNo,String vin, String regNo, String licPlate,boolean showRecentUnits , String driverActiveInd) {

		Query countQuery = generateSearchDriverQuery(driverName,driverId, customerName, customerNo,unitNo,vin,regNo ,licPlate, showRecentUnits ,driverActiveInd , null ,true );
		BigDecimal count = (BigDecimal) countQuery.getSingleResult();
		
		return count.intValue();
	}
	
	
	public List<DriverSearchVO> searchDriver(String driverName, Long driverId, String customerName, String customerNo,String unitNo,String vin, String regNo, String licPlate,boolean showRecentUnits, String driverActiveInd, Pageable pageable, Sort sort) {
		
		
		List<DriverSearchVO> list = new ArrayList<DriverSearchVO>();
		
		Query query = generateSearchDriverQuery(driverName,driverId, customerName, customerNo,unitNo,  vin, regNo,licPlate,showRecentUnits,driverActiveInd ,sort,false );
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}

		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null) {
				
			
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();
				DriverSearchVO driverSearchVO = new DriverSearchVO();

				driverSearchVO.setDrvId(object[0] != null ? ((BigDecimal) object[0]).longValue() : null);
				driverSearchVO.setDriverForename(object[1] != null ? (String) object[1] : null);
				driverSearchVO.setDriverSurname(object[2] != null ? (String) object[2] : null);
				driverSearchVO.setDriverEmail(object[3] != null ? (String) object[3] : null);
				driverSearchVO.setActiveInd(object[4] != null ? (String) object[4] : null);
				
				driverSearchVO.setContractLineStartDate(object[5] != null ? (Date) object[5] : null);
				driverSearchVO.setContractLineEndDate(object[6] != null ? (Date) object[6] : null);
				
				driverSearchVO.setUnitNo(object[7] != null ? (String) object[7] : null);
				driverSearchVO.setFmsId(object[8] != null ? ((BigDecimal) object[8]).longValue() : null);
				driverSearchVO.setVin(object[9] != null ? (String) object[9] : null);
				driverSearchVO.setModelDesc(object[10] != null ? (String) object[10] : null);
				
				driverSearchVO.setFleetRefNo(object[11] != null ? (String) object[11] : null);
				driverSearchVO.setLicensePlate(object[12] != null ? (String) object[12] : null);
				
				driverSearchVO.setAccountCode(object[13] != null ? (String) object[13] : null);
				driverSearchVO.setAccountName(object[14] != null ? (String) object[14] : null);
				driverSearchVO.setTownCity(object[15] != null ? (String) object[15] : null);
				driverSearchVO.setRegion(object[16] != null ? (String) object[16] : null);
				driverSearchVO.setPostcode(object[17] != null ? (String) object[17] : null);
				driverSearchVO.setBusinessaddressLine(object[18] != null ? (String) object[18] : null);
				driverSearchVO.setAddressLine1(object[19] != null ? (String) object[19] : null);
				driverSearchVO.setAddressLine2(object[20] != null ? (String) object[20] : null);	
				String unitRegistrationLicPlate  = (String)object[21] ;				
				if(isNotNull(unitRegistrationLicPlate))
						driverSearchVO.setLicensePlate(unitRegistrationLicPlate);
				driverSearchVO.setDriverPhone(object[22] != null ? (String) object[22] : "");
				driverSearchVO.setPoolManager(object[23] != null ? (String) object[23] : null);
				driverSearchVO.setCostCentre(object[24] != null ? (String) object[24] : null);
				
				list.add(driverSearchVO);

			}
		}

		return list;
	}
	

	public Query generateSearchDriverQuery(String driverName, Long driverId, String customerName, String customerNo,String unitNo,String vin, String regNo,String licPlate, boolean showRecentUnits, String driverActiveInd , Sort sort, boolean isCountOnly) {
		
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
				
		Query query =  null;
		String selectData = "SELECT dr.DRV_ID, dr.DRIVER_FORENAME, dr.DRIVER_SURNAME , dr.EMAIL ,dr.ACTIVE_IND , acln.START_DATE , acln.ACTUAL_END_DATE, "
				+ " fm.UNIT_NO,fm.FMS_ID, fm.VIN, md.MODEL_DESC ,  fm.VEHICLE_COST_CENTRE , fm.REG_NO , ea.ACCOUNT_CODE , "
				+ " ea.ACCOUNT_NAME,  daddr.TOWN_CITY , daddr.REGION ,daddr.POSTCODE, daddr.BUSINESS_ADDRESS_LINE, daddr.ADDRESS_LINE_1 ,daddr.ADDRESS_LINE_2 , "
				+ " urv.LIC_PLATE_NO, fl_contact.get_driver_phone_no(dr.DRV_ID, null).phone_number AS PHONE_NUMER, dr.POOL_MGR ,  "
				+ " act_unt_man_drv.get_driver_cost_centre(dr.DRV_ID).COST_CENTRE_CODE AS COST_CENTRE ";																
		String selectCount = "SELECT COUNT(1) ";
		String selectClause =  isCountOnly == true ? selectCount : selectData;
		
		StringBuilder queryBuilder = new StringBuilder(selectClause).append(
						" FROM DRIVERS dr");
		queryBuilder.append(
				" left join (" +
				"SELECT cln.DRV_DRV_ID, cln.START_DATE, cln.ACTUAL_END_DATE, cln.FMS_FMS_ID " +
				" FROM CONTRACT_LINES cln " +
				
                " WHERE cln.CLN_ID =  (SELECT MAX(cln1.CLN_ID) FROM contract_lines cln1 WHERE cln1.fms_fms_id = cln.fms_fms_id ");
				
		if(showRecentUnits){
			queryBuilder.append(" AND ((cln1.start_date IS NOT NULL) OR (cln1.start_date is null AND cln1.in_serv_date is not null)) ");
		}else{
			queryBuilder.append(" AND actual_end_date IS NULL AND ((cln1.start_date IS NOT NULL) OR (cln1.start_date is null AND cln1.in_serv_date is not null)) ");
		}
		queryBuilder.append(")) acln ON dr.DRV_ID = acln.DRV_DRV_ID ");
		
		queryBuilder.append(" left join  FLEET_MASTERS fm on acln.FMS_FMS_ID=fm.FMS_ID "
						+ " join  EXTERNAL_ACCOUNTS ea on dr.EA_ACCOUNT_CODE=ea.ACCOUNT_CODE and dr.EA_ACCOUNT_TYPE=ea.ACCOUNT_TYPE  and dr.EA_C_ID=ea.C_ID "
						+ " join DRIVER_ADDRESSES daddr ON dr.drv_id = daddr.drv_drv_id "
						+ " left join MODELS md on md.MDL_ID = fm.MDL_MDL_ID " );
			
		if(isCountOnly == false || isNotNull(licPlate)){
			queryBuilder
			.append("left join (" +
                    "SELECT u.fms_fms_id, u.LIC_PLATE_NO FROM unit_registrations u " +
                            "WHERE u.urg_id = " +
                             "(SELECT MAX(URG_ID) " +
                                                           " FROM   unit_registrations u1 " +
                                                           " WHERE  u1.fms_fms_id = u.fms_fms_id " +
                                                            
                            "AND issued_date = (SELECT MAX(issued_date) "+ 
                                                            " FROM unit_registrations u2 " +
                                                            " WHERE u2.fms_fms_id = u.fms_fms_id)) " +
                ") urv ON  fm.FMS_ID  = urv.FMS_FMS_ID ");
			
		}	
		
		queryBuilder.append(" WHERE daddr.ADDRESS_TYPE = 'GARAGED' and daddr.DEFAULT_IND = 'Y'");
				
		if( driverId != null){
			queryBuilder.append("AND dr.DRV_ID =:driverId  ");
			parameterMap.put("driverId", driverId);
		}else if (isNotNull(driverName)){			
			String lastName =  null;
			String firstName =  null;
			
			String[] nameArray= driverName.split(",");			
			lastName = driverName.split(",")[0];
			if(nameArray.length > 1){			
				firstName =  driverName.split(",")[1];
			}
			
			if(isNotNull(lastName)){
				if(firstName != null && driverName.indexOf(",") >= 0 ){
					queryBuilder.append("AND UPPER(dr.DRIVER_SURNAME) = :lastName ");
					parameterMap.put("lastName", lastName.toUpperCase());
				}
				else {
					queryBuilder.append("AND UPPER(dr.DRIVER_SURNAME) like :lastName ");
					parameterMap.put("lastName", setupWildCardAtRight(lastName.toUpperCase()));
				}
			}
			if(isNotNull(firstName)){
				queryBuilder.append("AND UPPER(dr.DRIVER_FORENAME) like :firstName ");
				parameterMap.put("firstName", setupWildCardAtRight(firstName.trim().toUpperCase()));
			}
			
		}
		if ( isNotNull(driverActiveInd)) {
			queryBuilder.append("AND UPPER(dr.ACTIVE_IND) = :driverActiveInd ");
			parameterMap.put("driverActiveInd", driverActiveInd);
		}
		
		if (isNotNull(customerName) || isNotNull(customerNo)) {
			
		  if (isNotNull(customerName)) {
				queryBuilder.append("AND UPPER(ea.ACCOUNT_NAME) like :customerName ");
				parameterMap.put("customerName", setupWildCardAtRight(customerName.toUpperCase()));
			} else if (isNotNull(customerNo)) {
				queryBuilder.append("AND UPPER(ea.ACCOUNT_CODE) like :customerNo ");
				parameterMap.put("customerNo", setupWildCardAtRight(customerNo.toUpperCase()));
			}
			queryBuilder.append(" AND ea.ACC_STATUS = 'O'  AND ea.ACCOUNT_TYPE = 'C' AND ea.C_ID = 1 "); 
		}
		
		if(isNotNull(unitNo)){			
			queryBuilder.append(" AND FM.UNIT_NO =:unitNo ");
			parameterMap.put("unitNo", unitNo.toUpperCase());
		}
		if(isNotNull(vin)){			
			queryBuilder.append(" AND UPPER(SUBSTR(FM.VIN,-6)) like :vin ");
			parameterMap.put("vin", setupWildCardAtRight(vin.toUpperCase()));
		}
		
		if(isNotNull(regNo)){		
			if(regNo.contains("%")){
				queryBuilder.append(" AND UPPER(fm.VEHICLE_COST_CENTRE) like :regNo ");
			}else{
				queryBuilder.append(" AND UPPER(fm.VEHICLE_COST_CENTRE) =:regNo ");
			}
			parameterMap.put("regNo", regNo.toUpperCase());
		}
		if(isNotNull(licPlate)){
			queryBuilder.append(" AND ( fm.REG_NO =:licPlate  OR  urv.LIC_PLATE_NO =:licPlate ) ");
			parameterMap.put("licPlate", licPlate.toUpperCase());
		}
	
		if(isCountOnly == false &&  sort!= null){
			Order order = sort.iterator().next();
			String sortOrder = "DESC";
			if(order.isAscending()){
				sortOrder = "ASC";
			}
		
			if(DataConstants.DRIVER_SEARCH_SORT_FIELD_DRIVER_NAME.equalsIgnoreCase(order.getProperty())){
				queryBuilder.append(" ORDER BY dr.DRIVER_SURNAME ").append(sortOrder).append("  , ").append(" dr.DRIVER_FORENAME ").append(sortOrder);	
			}else if(DataConstants.DRIVER_SEARCH_SORT_FIELD_ACCOUNT.equalsIgnoreCase(order.getProperty())){
				queryBuilder.append(" ORDER BY ea.ACCOUNT_NAME, act_unt_man_drv.get_driver_cost_centre(dr.DRV_ID).COST_CENTRE_CODE, dr.DRIVER_SURNAME, dr.DRIVER_FORENAME "+sortOrder);	
			}else if(DataConstants.DRIVER_SEARCH_SORT_FIELD_UNIT_NO.equalsIgnoreCase(order.getProperty())){
				queryBuilder.append(" ORDER BY fm.UNIT_NO "+sortOrder);	
			}else if(DataConstants.DRIVER_SEARCH_SORT_FIELD_VEHICLE_STATUS.equalsIgnoreCase(order.getProperty())){
				queryBuilder.append(" ORDER BY vs.VEHICLE_STATUS "+sortOrder);	
			}else{
				queryBuilder.append(" ORDER BY dr.DRIVER_SURNAME ").append(sortOrder).append("  , ").
				append(" dr.DRIVER_FORENAME ").append(sortOrder).append("  , ").append(" ea.ACCOUNT_NAME ").append(sortOrder);	
			}
		}
		
		query = entityManager.createNativeQuery(queryBuilder.toString());		 
				 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 
		
		return query;
		
	}

	/**
	 * Makes use of a standard DB package to retrieve a specified driver's status.
	 * @param driverId Uniquely identifies the driver
	 * @return The status description
	 */
	public String getDriverStatus(Long driverId){
		String stmt = "SELECT fl_status.driver_status_desc(fl_status.driver_status(?)) FROM DUAL";
		String status = null;		
		
		status = (String)entityManager.createNativeQuery(stmt)
				.setParameter(1, driverId)
				.getSingleResult();
		
		return status;
	}
	
	/**
	 * Makes use of a native query to return unit numbers for on order vehicles for further query
	 * (This is a performance optimization as this is used in a notification routine while saving the driver and needs to be fast)
	 * 
	 * It select a distinct list of all unit numbers present on a quotation model for a quote that have a quote status of 3 (Accepted)
	 * and optionally that are in the GRD processes (with only pertains to Stock "In Inventory" vehicles) in which case we also need to look for a quote status of 
	 * 16 (Allocated to GRD) and/or 17 (GRD Complete) that also either do not have a contract line or have a contract line 
	 * with a null/empty in service date
	 * filtered by the driver (driver id) that is present on the quote. 
	 * 
	 * @param driverId Uniquely identifies the driver
	 * @return a list of unit numbers (or an empty list) for all of the on order units for that driver
	 */
	public List<String> getOnOrderUnitNumbersByDriverId(Long driverId, boolean includeStock) {
		String stmt = "SELECT distinct qmd.unit_no FROM quotations quo " +
				" INNER JOIN quotation_models qmd ON quo.quo_id = qmd.quo_quo_id " + 
				" LEFT JOIN contract_lines cli ON qmd.qmd_id = cli.qmd_qmd_id ";
				if(includeStock){
					stmt = stmt + " WHERE qmd.quote_status in (3,16,17) ";
				} else {
					stmt = stmt + " WHERE qmd.quote_status in (3) ";
				}
				stmt = stmt + " AND cli.in_serv_date IS NULL " +
				" AND quo.DRV_DRV_ID = ? ";

		Query query = entityManager.createNativeQuery(stmt).setParameter(1, driverId);
		
		@SuppressWarnings("unchecked")
		List<String> unitNumberList = (List<String>)query.getResultList();
		
		return unitNumberList;
	}
	
	private boolean isNotNull(String str){		
		return MALUtilities.isNotEmptyString(str);
	}

	private String setupWildCardAtRight(String searchTerm){		
		if( !searchTerm.endsWith("%")){
			searchTerm = searchTerm + "%";
		}
		return searchTerm ;
	}	
	
	@Override
	public List<DriverInfoVO> searchDriverInfo(String driverName, Long driverId, Long fmsId, ExternalAccountPK externalAccountPK, Pageable pageable, Sort sort) {
		List<DriverInfoVO> list = new ArrayList<DriverInfoVO>();
		
		Query query = generateSearchDriverInfoQuery(driverName, driverId, fmsId, externalAccountPK, sort, false );
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}

		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null) {
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				int i = 0;
				Object[] object = iterator.next();
				DriverInfoVO driverInfoVO = new DriverInfoVO();
				driverInfoVO.setDrvId(				object[i] 		!= null ? ((BigDecimal) object[i]).longValue() : null);
				driverInfoVO.setFmsId(				object[i+=1] 	!= null ? ((BigDecimal) object[1]).longValue() : null);
				driverInfoVO.setDriverForeName(		object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setDriverSurName(		object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setAllocatedUnit(		object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setVin(				object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setEolDate(			object[i+=1] 	!= null ? (Date) object[i] : null);
				driverInfoVO.setQmdId(				object[i+=1] 	!= null ? ((BigDecimal) object[i]).longValue() : null);
				driverInfoVO.setDriverPhone(		object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setBusinessAddressLine(object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setAddressLine1(		object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setAddressLine2(		object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setTown(				object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setRegion(				object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setPostCode(			object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setTerm(				object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setQuoteProfileDesc(	object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setProductType(		object[i+=1] 	!= null ? (String) object[i] : null);
				driverInfoVO.setTrim(				object[i+=1] 	!= null ? (String) object[i] : null);
				list.add(driverInfoVO);
			}
		}
		return list;
	}

	@Override
	public int searchDriverInfoCount(String driverName,  ExternalAccountPK externalAccountPK) {
		Query countQuery = generateSearchDriverInfoQuery(driverName,null, null, externalAccountPK, null ,true );
		BigDecimal count = (BigDecimal) countQuery.getSingleResult();
		
		return count.intValue();
	}
	
	public Query generateSearchDriverInfoQuery(String driverName, Long driverId, Long fmsId, ExternalAccountPK externalAccountPK, Sort sort, boolean isCountOnly) {
		
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
				
		Query query =  null;
		String selectCount = " SELECT COUNT(1) ";
		
		String selectData = " SELECT tmp.*, fl_contact.get_driver_phone_no (tmp.DRV_ID, NULL).phone_number AS PHONE_NUMER, dadd.BUSINESS_ADDRESS_LINE, dadd.ADDRESS_LINE_1, dadd.ADDRESS_LINE_2,"
				+ " tcc.TOWN_DESCRIPTION, dadd.REGION, dadd.POSTCODE,"
				+ " (SELECT contract_period || '/' || contract_distance FROM quotation_models WHERE qmd_id = tmp.qmd_id) term,"
				+ " (SELECT description FROM quotation_profiles WHERE qpr_id = (SELECT qpr_qpr_id FROM quotations WHERE quo_id = (SELECT quo_quo_id FROM quotation_models WHERE qmd_id = tmp.qmd_id))) AS quote_proile_description,"
				+ " (SELECT description FROM product_type_codes WHERE product_type = (SELECT product_type FROM products WHERE product_code ="
				+ " (SELECT prd_product_code FROM quotation_profiles WHERE qpr_id = (SELECT qpr_qpr_id FROM quotations WHERE quo_id = (SELECT quo_quo_id FROM quotation_models WHERE qmd_id = tmp.qmd_id))))) AS product_type, "
				+ " (SELECT model_desc from models where mdl_id = (select mdl_mdl_id FROM quotation_models WHERE qmd_id = tmp.qmd_id)) trim ";
		
		String selectClause =  isCountOnly == true ? selectCount : selectData;
		
		StringBuilder queryBuilder = new StringBuilder(" WITH driver_query as ( ").append(selectClause);
		
		
		
		queryBuilder.append(" FROM (SELECT drv.drv_id, dra.fms_fms_id, drv.driver_forename, drv.driver_surname,"
				+ " (SELECT unit_no FROM fleet_masters WHERE fms_id = dra.fms_fms_id) unit_no,"
				+ " (SELECT vin FROM fleet_masters WHERE fms_id = dra.fms_fms_id) vin, "
				+ " (SELECT end_date FROM contract_lines WHERE cln_id IN"
				+ " (SELECT MAX (cln1.CLN_ID) FROM contract_lines cln1 WHERE cln1.fms_fms_id = dra.fms_fms_id"
				+ " AND actual_end_date IS NULL AND ( (cln1.start_date IS NOT NULL)"
				+ " OR (    cln1.start_date IS NULL AND cln1.in_serv_date IS NOT NULL)))) end_date,"
				+ " ( (SELECT qmd_qmd_id FROM contract_lines WHERE cln_id IN (SELECT MAX (cln1.CLN_ID) FROM contract_lines cln1 WHERE cln1.fms_fms_id = dra.fms_fms_id"
				+ " AND actual_end_date IS NULL AND (   (cln1.start_date IS NOT NULL) OR ( cln1.start_date IS NULL AND cln1.in_serv_date IS NOT NULL))))) qmd_id"
				+ " FROM drivers drv, driver_allocations dra"
				+ " WHERE 1 = 1 ");
		
		if( driverId != null){
			queryBuilder.append(" AND drv.drv_id = :driverId");
			parameterMap.put("driverId", driverId);
		}else if(!MALUtilities.isEmptyString(driverName)){
			String lastName =  null;
			String firstName =  null;
		
			String[] nameArray= driverName.split(",");			
			lastName = driverName.split(",")[0];
			if(nameArray.length > 1){			
				firstName =  driverName.split(",")[1];
			}
			if(!MALUtilities.isEmptyString(lastName)){
				if(firstName != null && driverName.indexOf(",") >= 0 ){
					queryBuilder.append(" AND UPPER(drv.DRIVER_SURNAME) = :lastName ");
					parameterMap.put("lastName", lastName.toUpperCase());
				}
				else {
					queryBuilder.append(" AND UPPER(drv.DRIVER_SURNAME) like :lastName ");
					parameterMap.put("lastName", DataUtilities.appendWildCardToRight((lastName.toUpperCase())));
				}
			}
			if(!MALUtilities.isEmptyString(firstName)){
				queryBuilder.append(" AND UPPER(drv.DRIVER_FORENAME) like :firstName ");
				parameterMap.put("firstName", DataUtilities.appendWildCardToRight(firstName.trim().toUpperCase()));
			}
		}
		
		if(!MALUtilities.isEmpty(externalAccountPK) && !MALUtilities.isEmpty(externalAccountPK.getAccountCode())){
			queryBuilder.append(" AND (drv.ea_c_id,"
					+ " drv.ea_account_type, drv.ea_account_code) IN (SELECT c_id, account_type, account_code"
					+ " FROM external_accounts"
					+ " START WITH (    c_id = :cId"
					+ " AND account_type = :acc_type"
					+ " AND account_code = :account_code)"
					+ " CONNECT BY (    parent_account_entity = PRIOR c_id"
					+ " AND parent_account_type = PRIOR account_type"
					+ " AND parent_account = PRIOR account_code)) ");
			
			parameterMap.put("cId", externalAccountPK.getcId());
			parameterMap.put("acc_type", externalAccountPK.getAccountType());
			parameterMap.put("account_code", externalAccountPK.getAccountCode());
		}
		
		queryBuilder.append(" AND drv.drv_id = dra.drv_drv_id(+) ");
		
		queryBuilder.append(" AND NVL(drv.active_ind, 'N') = 'Y' ");

		// if fms id is passed in, we want to join against the allocations table for the latest match even if it's no longer active,  
		//     otherwise we want to only get unit info for an active allocation
		if(!MALUtilities.isEmpty(fmsId)){
			queryBuilder.append(" AND dra.fms_fms_id = :fmsId " 
					+ " AND (NVL(dra.TO_DATE,TRUNC(SYSDATE)) = (SELECT MAX (NVL(dra1.TO_DATE, TRUNC(SYSDATE))) FROM driver_allocations dra1 WHERE dra1.fms_fms_id = dra.fms_fms_id))) tmp, ");//-HD-182(PR-103)
			parameterMap.put("fmsId", fmsId);
		} else {
			queryBuilder.append(" AND dra.TO_DATE(+) IS NULL) tmp, ");
		}
		
		queryBuilder.append(" DRIVER_ADDRESSES dadd, TOWN_CITY_CODES tcc"
				+ " WHERE     tmp.drv_id = dadd.drv_drv_id"
				+ " AND tcc.COUNTRY_CODE = dadd.COUNTRY"
				+ " AND tcc.REGION_CODE = dadd.REGION"
				+ " AND tcc.TOWN_NAME = dadd.TOWN_CITY"
				+ " AND tcc.COUNTY_CODE = dadd.COUNTY_CODE "
				+ " AND dadd.ADDRESS_TYPE = 'GARAGED' "
				+ " AND dadd.DEFAULT_IND = 'Y')"
				+ " SELECT * FROM driver_query ");
				
		if(isCountOnly == false){
			queryBuilder.append(" ORDER BY DRIVER_SURNAME ASC, DRIVER_FORENAME ASC ");
		}
		
		query = entityManager.createNativeQuery(queryBuilder.toString());		 
				 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 
		
		return query;
		
	}
}
