package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.ActiveVehicleStatus;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.DocumentType;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.util.MALUtilities;


public  class VehicleSearchDAOImpl extends GenericDAOImpl<FleetMaster, Long> implements VehicleSearchDAOCustom {

	private static final long serialVersionUID = 1L;
	private static MalLogger logger = MalLoggerFactory.getLogger(VehicleSearchDAOImpl.class);
	
	/**
	 * Searches for vehicles based on the passed in search criteria. This is the main method for performing vehicle 
	 * search.
	 */
	public List<VehicleSearchResultVO> searchVehicles(VehicleSearchCriteriaVO vehicleSearchCriteriaVO, Pageable pageable, Sort sort){
		Query query = null;
		List<VehicleSearchResultVO> vehicleSearchResults = new ArrayList<VehicleSearchResultVO>();
		//Per Business Rules - return an empty list (]do not search) if criteria is empty
		if(isSearchCriteriaEmpty(vehicleSearchCriteriaVO)){
			return vehicleSearchResults;		
		}
		//Split queries to improve performance. PO and Invoice search use similar associations.
		//All other searches, don't need the document tables.
		if(isPOInvoiceSearch(vehicleSearchCriteriaVO)){			
			query = this.generateVehicleSearchByPOInvoiceQuery(vehicleSearchCriteriaVO, false, sort);			
		} else {			
			query = generateVehicleSearchQuery(vehicleSearchCriteriaVO, false, sort);			
		}	
		
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				VehicleSearchResultVO vehicleSearchResultVO = new VehicleSearchResultVO();				
				vehicleSearchResultVO.setFmsId(((BigDecimal)record[i]).longValue());
				vehicleSearchResultVO.setUnitStatus((String)record[i+=1]);				
				vehicleSearchResultVO.setUnitNo((String)record[i+=1]);
				vehicleSearchResultVO.setClientFleetReferenceNumber((String)record[i+=1]);
				vehicleSearchResultVO.setVIN((String)record[i+=1]);
				vehicleSearchResultVO.setUnitDescription((String)record[i+=1]);
				vehicleSearchResultVO.setClientCorpEntity(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				vehicleSearchResultVO.setClientAccountNumber((String)record[i+=1]);
				vehicleSearchResultVO.setClientAccountName((String)record[i+=1]);
				vehicleSearchResultVO.setClientAccountType((String)record[i+=1]);
				vehicleSearchResultVO.setDrvId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				vehicleSearchResultVO.setDriverStatus((String)record[i+=1]);
				vehicleSearchResultVO.setDriverActive(MALUtilities.convertYNToBoolean((String)record[i+=1]));				
				vehicleSearchResultVO.setDriverPoolManager(MALUtilities.convertYNToBoolean((String)record[i+=1]));
				vehicleSearchResultVO.setDriverSurname((String)record[i+=1]);
				vehicleSearchResultVO.setDriverForeName((String)record[i+=1]);
				vehicleSearchResultVO.setDriverAddressBusinessIndicator(MALUtilities.convertYNToBoolean((String)record[i+=1]));				
				vehicleSearchResultVO.setDriverBusinessAddressLine((String)record[i+=1]);
				vehicleSearchResultVO.setDriverAddress1((String)record[i+=1]);
				vehicleSearchResultVO.setDriverAddress2((String)record[i+=1]);
				vehicleSearchResultVO.setDriverCity((String)record[i+=1]);
				vehicleSearchResultVO.setDriverState((String)record[i+=1]);
				vehicleSearchResultVO.setDriverZip((String)record[i+=1]);
				vehicleSearchResultVO.setDriverEmail((String)record[i+=1]);
				vehicleSearchResultVO.setDriverAreaCode((String)record[i+=1]);
				vehicleSearchResultVO.setDriverPhoneNumber((String)record[i+=1]);
				vehicleSearchResultVO.setDriverPhoneExtension((String)record[i+=1]);
				
				vehicleSearchResultVO.setContractStartDate((Date)record[i+=1]);
				vehicleSearchResultVO.setContractEndDate((Date)record[i+=1]);
				vehicleSearchResultVO.setContractActualEndDate((Date)record[i+=1]);
				vehicleSearchResultVO.setInServiceDate((Date)record[i+=1]);				
				vehicleSearchResultVO.setContractOutOfServiceDate((Date)record[i+=1]);	
				
				vehicleSearchResultVO.setMaintenanceRequestId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				vehicleSearchResultVO.setPurchaseOrderNumber((String)record[i+=1]);
                vehicleSearchResultVO.setInternalInvoiceNumber((String)record[i+=1]);                
				vehicleSearchResultVO.setserviceProviderNumber((String)record[i+=1]);				
				vehicleSearchResultVO.setServiceProviderName((String)record[i+=1]);
				vehicleSearchResultVO.setServiceProviderInvoiceNumber((String)record[i+=1]);
				vehicleSearchResultVO.setLicensePlateNo((String)record[i+=1]);				
				vehicleSearchResultVO.setNumOfOpenMaintPOs(((BigDecimal)record[i+=1]).intValue());
				vehicleSearchResultVO.setProductName((String)record[i+=1]);
				vehicleSearchResultVO.setQmdId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				vehicleSearchResultVO.setContractTerm(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				vehicleSearchResultVO.setContractDistance(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				
				vehicleSearchResults.add(vehicleSearchResultVO);
			}
			
			// *Note: As part of performance tuning it was decided to return the list them "pair it down"
			// this is because the use of the function to get vehicle status was causing very poor execution
			// and it proved to be much faster to move the filter into Java code; this is not typically 
			// the case and is an exception to our pre-established patterns of filtering and paging in the DB
			vehicleSearchResults = filterByVehicleStatus(vehicleSearchCriteriaVO, vehicleSearchResults);
			
			if(pageable != null){
				vehicleSearchResults = this.applypPagingToResults(vehicleSearchResults, pageable);
			}		
		}	
							
		return vehicleSearchResults;
	}
	
	/**
	 * Determines the number of records the will be in the search result. Used 
	 * primarily for paging.
	 * 
	 * @param VehicleSearchCriteriaVO Vehicle search criteria
	 */
	public int searchVehiclesCount(VehicleSearchCriteriaVO vehicleSearchCriteriaVO){
		Query query = null;
		List<VehicleSearchResultVO> vehicleSearchResults = new ArrayList<VehicleSearchResultVO>();
		VehicleSearchResultVO vehicleSearchResultVO = null;		
		//Per Business Rules - return a '0' count (do not search) if criteria is empty
		if(isSearchCriteriaEmpty(vehicleSearchCriteriaVO)){
			return 0;
		}		
		if(isPOInvoiceSearch(vehicleSearchCriteriaVO)){			
			query = generateVehicleSearchByPOInvoiceQuery(vehicleSearchCriteriaVO, true, null);			
		} else {			
			query = generateVehicleSearchQuery(vehicleSearchCriteriaVO, true, null);			
		}
		
		List<String>resultList = (List<String>)query.getResultList();
		if(resultList != null){
			for(String record : resultList){
				vehicleSearchResultVO = new VehicleSearchResultVO();				
				vehicleSearchResultVO.setUnitStatus(record);
				vehicleSearchResults.add(vehicleSearchResultVO);				
			}
		}		
		
		// *Note: As part of performance tuning it was decided to return the list them "pair it down"
		// this is because the use of the function to get vehicle status was causing very poor execution
		// and it proved to be much faster to move the filter into Java code; this is not typically 
		// the case and is an exception to our pre-established patterns of filtering and paging in the DB
		vehicleSearchResults = filterByVehicleStatus(vehicleSearchCriteriaVO, vehicleSearchResults);
		return vehicleSearchResults.size();		
	}	
	
	private boolean isSearchCriteriaEmpty(VehicleSearchCriteriaVO vehicleSearchCriteria){
		boolean empty = true;
		if( !MALUtilities.isEmpty(vehicleSearchCriteria.getClientAccountName())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getClientAccountNumber())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getClientFleetReferenceNumber())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getDriverForeName())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getDriverSurName())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getInternalnvoiceNumber())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getLicensePlateNo())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getPurchaseOrderNumber())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getServiceProviderInvoiceNumber())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getServiceProviderName())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getUnitNo())
			|| !MALUtilities.isEmpty(vehicleSearchCriteria.getVIN()) 
			||	!MALUtilities.isEmpty(vehicleSearchCriteria.getVehSchSeq())){
			return false;
		}
		
		return empty;
	}
	
	/**
	 * Generates a query that will search on driver and any non PO/Invoice criteria. 
	 * 
	 * @param vehicleSearchCriteriaVO Criteria to search on.
	 * @param isCount flag used to count or return query results 
	 * @return String query
	 */
	private Query generateVehicleSearchQuery(VehicleSearchCriteriaVO vehicleSearchCriteriaVO, boolean isCount, Sort sort){
		Query query = null;
		
		StringBuilder sqlStmt = new StringBuilder("");
		//Added Optimizer setting after Oracle 12c upgrade. After 12c Oracle database upgrade performance issues were reported in MM071. To fix those performance issues we have enabled 11g optimizer. Saket 06/14/2016
		sqlStmt.append("SELECT ");
		
		if(isCount){
			sqlStmt.append(
					"       fl_status.fleet_status_desc(fl_status.fleet_status(fms.fms_id))");				
		} else {
			sqlStmt.append(
					"       fms.fms_id, fl_status.fleet_status_desc(fl_status.fleet_status(fms.fms_id)), fms.unit_no, fms.vehicle_cost_centre, fms.vin, mdl.model_desc," 
				   +"       ea.c_id, ea.account_code, ea.account_name, ea.account_type," 
                   +"       drv.drv_id, fl_status.driver_status_desc(fl_status.driver_status(drv.drv_id)), drv.active_ind, drv.pool_mgr, drv.driver_surname, drv.driver_forename,"  
                   +"       dra.business_ind, dra.business_address_line, dra.address_line_1, dra.address_line_2," 
                   +"       dra.town_city, dra.region, dra.postcode, drv.email, "
                   +"       cnr.contact_area_code, cnr.contact_number, cnr.contact_extension_number," 
                   +"       ccln.start_date, ccln.end_date, ccln.actual_end_date, ccln.in_serv_date, ccln.out_of_service_date,"
                   +"       null AS c1, null AS C2, null AS C3, null AS C4, null AS C5, null AS C6,");	
            if(vehicleSearchCriteriaVO.isContractVehicleSearch()){
            	sqlStmt.append(
            	    "		null AS C7,	"
            	   +"		0 AS C8,"
            	   +"		p.product_name, ccln.qmd_qmd_id, qm.contract_period, qm.contract_distance");
            } else {
            	sqlStmt.append(
            		"       decode(urv.lic_plate_no, null, fms.reg_no, lic_plate_no), "
                   +"		(SELECT count(mr.mrq_id) "
                   +"			FROM maintenance_requests mr, maint_request_status_codes mrsc " 
                   +"			WHERE mr.fms_fms_id = fms.fms_id "
                   +"				AND mrsc.maint_request_status = mr.maint_request_status "
                   +"				AND mrsc.sort_order < 6), "
                   +"		null AS C9, null AS C10, null AS C11, null AS C12");	
            }
		}
		
		//Optimizes driver search
		if(this.isDriverSearch(vehicleSearchCriteriaVO)){
			sqlStmt.append(
					"     FROM drivers drv");				
		} else {
			sqlStmt.append(
					"     FROM fleet_masters fms");			
		}
		
	
		//Optimizes driver search
		if(this.isDriverSearch(vehicleSearchCriteriaVO)){
			sqlStmt.append(
					 "    INNER JOIN driver_allocations dal ON drv.drv_id = dal.drv_drv_id" 
					+"    INNER JOIN fleet_masters fms ON dal.fms_fms_id = fms.fms_id");			
		} else {
			sqlStmt.append(
					 "    LEFT JOIN driver_allocations dal ON fms.fms_id = dal.fms_fms_id" 
					+"    LEFT JOIN drivers drv ON dal.drv_drv_id = drv.drv_id");			
		}
		// conditionally join to VEHICLE_SCHEDULES
		if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getVehSchSeq())){
			sqlStmt.append(" INNER JOIN VEHICLE_SCHEDULES vsch ON fms.fms_id = vsch.fms_fms_id ");
		}
		
		if(!vehicleSearchCriteriaVO.isContractVehicleSearch()){
			sqlStmt.append("  LEFT JOIN unit_registrations urv ON (fms.fms_id = urv.fms_fms_id and urv.urg_id ="
									+ " (SELECT MAX(URG_ID) FROM unit_registrations WHERE fms_fms_id = urv.fms_fms_id"
									+ " AND issued_date = (SELECT MAX(issued_date) FROM unit_registrations"
									+ " WHERE fms_fms_id = urv.fms_fms_id)))");
		}

		sqlStmt.append(
                       "  LEFT JOIN driver_addresses dra ON (dra.drv_drv_id = drv.drv_id AND dra.address_type = 'GARAGED')"
                      +"  LEFT JOIN contact_numbers cnr ON (cnr.drv_drv_id = drv.drv_id AND cnr.preferred_ind = 'Y')"
                      +"  LEFT JOIN contract_lines ccln ON fms.fms_id = ccln.fms_fms_id "
                      +"  LEFT JOIN contracts con ON ccln.con_con_id = con.con_id");
		
		if(vehicleSearchCriteriaVO.isContractVehicleSearch()){
			sqlStmt.append(
					 " LEFT JOIN quotation_models qm ON ccln.qmd_qmd_id = qm.qmd_id"
					+" LEFT JOIN quotations q ON qm.quo_quo_id = q.quo_id"
					+" LEFT JOIN quotation_profiles qp ON q.qpr_qpr_id = qp.qpr_id"
			        +" LEFT JOIN products p ON qp.prd_product_code = p.product_code");
					
		}
                     
		//Optimizes client search
		if(this.isClientSearch(vehicleSearchCriteriaVO)){
			sqlStmt.append(
					   "  INNER JOIN external_accounts ea ON (con.ea_account_code = ea.account_code AND con.ea_account_type = ea.account_type AND con.ea_c_id = :corporateEntityId)");			
		} else {
			sqlStmt.append(
					   "  LEFT JOIN external_accounts ea ON (con.ea_account_code = ea.account_code AND con.ea_account_type = ea.account_type AND con.ea_c_id = :corporateEntityId)");
		}        

        sqlStmt.append("  INNER JOIN models mdl ON fms.mdl_mdl_id = mdl.mdl_id");
        
        //if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientFleetReferenceNumber()))			
		sqlStmt.append("  WHERE 1 = 1 ");

		// conditionally search VEHICLE_COST_CENTRE
		if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getClientFleetReferenceNumber())){
	       	 sqlStmt.append(" AND upper(fms.vehicle_cost_centre) LIKE :clientFleetReferenceNumber");
		}
		
		// conditionally search VEHICLE_SCHEDULES
		if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getVehSchSeq())){
	       	 sqlStmt.append(" AND vsch.veh_sch_seq LIKE :vehSchSeq");
		}
		
		//Based on the number of characters in the VIN compare from right-to-left the last number of characters
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getVIN()) && vehicleSearchCriteriaVO.getVIN().length() > 0) {
			sqlStmt.append( 
					   "      AND fms.vin LIKE upper(:VIN)");				
		} /*else {
			sqlStmt.append( 
					   "      AND nvl(trim(fms.vin), '%') LIKE upper(:VIN)");			
		}*/
		

		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getUnitNo()))
			sqlStmt.append("      AND fms.unit_no LIKE :unitNumber " );
                     // +"      AND mdl.mdl_id = fms.mdl_mdl_id"        
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getDriverSurName()))
			sqlStmt.append("      AND upper(drv.driver_surname) LIKE upper(:driverSurname)");
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getDriverForeName()))
			sqlStmt.append("      AND upper(drv.driver_forename) LIKE upper(:driverForename)");
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getLicensePlateNo()))
			sqlStmt.append("      AND (fms.reg_no LIKE upper(:licensePlateNumber) OR urv.lic_plate_no LIKE upper(:licensePlateNumber))");
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountNumber()))
			sqlStmt.append("      AND ea.account_code LIKE :clientAccountNumber");
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountName()))
			sqlStmt.append("      AND upper(trim(ea.account_name)) LIKE upper(:clientAccountName)");                      
		sqlStmt.append("      AND (ccln.cln_id IS NULL"
                      +"               OR (ccln.cln_id = (SELECT max(cln2.cln_id)" 
                      +"                                       FROM contract_lines cln2"
                      +"                                       WHERE  cln2.fms_fms_id = fms.fms_id"
                      +"                                           AND ( (cln2.out_of_service_date IS NOT NULL) " 
                      +"                                                      OR cln2.in_serv_date IS NOT NULL " 
                      +"                                                      OR NVL(cln2.start_date, trunc(SYSDATE)) <= trunc(SYSDATE)  ))))");
		sqlStmt.append(
				       "      AND (dal.dal_id IS NULL"
                      +"               OR dal.from_date = (SELECT max(dal2.from_date)" 
                      +"                                       FROM driver_allocations dal2"
                      +"                                       WHERE dal2.fms_fms_id = fms.fms_id))");		
		
		//Defaults order by unless otherwise specified by the passed in sort object
		if(!MALUtilities.isEmpty(sort)){
			sqlStmt.append(
					"     ORDER BY ");
			for ( Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext(); ) {
				Order order = orderIterator.next();
				
				if(DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO.equals(order.getProperty())){	
					sqlStmt.append(" fms.unit_no " + order.getDirection());
				}
				if(DataConstants.VEHICLE_SEARCH_SORT_FIELD_DRIVER_NAME.equals(order.getProperty())){	
					sqlStmt.append(" drv.driver_surname " + order.getDirection() + ", drv.driver_forename " + order.getDirection());
				}
				if(DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME.equals(order.getProperty())){	
					sqlStmt.append(" ea.account_name " + order.getDirection());
				}				
				
				if(orderIterator.hasNext()){
					sqlStmt.append(", ");
				}
	
			}			
		} else {
			sqlStmt.append("  ORDER BY drv.driver_surname asc, drv.driver_forename asc, dal.from_date desc");
		}
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientFleetReferenceNumber()))
			parameterMap.put("clientFleetReferenceNumber",vehicleSearchCriteriaVO.getClientFleetReferenceNumber().toUpperCase());
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getLicensePlateNo()))
			parameterMap.put("licensePlateNumber", vehicleSearchCriteriaVO.getLicensePlateNo());
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getUnitNo()))
			parameterMap.put("unitNumber", vehicleSearchCriteriaVO.getUnitNo());		
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getVIN()))
			parameterMap.put("VIN", DataUtilities.prependWildCardToLeft(vehicleSearchCriteriaVO.getVIN()));
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getDriverSurName()))
			parameterMap.put("driverSurname", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getDriverSurName()));
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getDriverForeName()))
			parameterMap.put("driverForename", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getDriverForeName()));
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountNumber()))
			parameterMap.put("clientAccountNumber", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getClientAccountNumber()));
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountName()))
			parameterMap.put("clientAccountName", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getClientAccountName()));
		parameterMap.put("corporateEntityId", vehicleSearchCriteriaVO.getCorporateEntity().getCorpId());
		if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getVehSchSeq())){
			parameterMap.put("vehSchSeq", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getVehSchSeq()));
		}
		
		logger.debug("Final Status Query: " + sqlStmt.toString());
		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 
		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 		
		
		return query;
	}	
	
	/**
	 *  Generates a query that will search on the PO and/or Service Provider or MAFS internal invoice to find the unit.
	 * 
	 * @param vehicleSearchCriteriaVO Criteria to search on.
	 * @param isCount flag used to count or return query results 
	 * @return String query
	 */
	private Query generateVehicleSearchByPOInvoiceQuery(VehicleSearchCriteriaVO vehicleSearchCriteriaVO, boolean isCount, Sort sort){			
		Query query = null;		
		StringBuilder sqlStmt = new StringBuilder("");
		
		//Added Optimizer setting after Oracle 12c upgrade. After 12c Oracle database upgrade performance issues were reported in MM071. To fix those performance issues we have enabled 11g optimizer. Saket 06/14/2016
		sqlStmt.append("SELECT ");	

		if(isCount){
			sqlStmt.append(
					  "     fl_status.fleet_status_desc(fl_status.fleet_status(fms.fms_id))");			
		} else {		
			sqlStmt.append(
					   "    fms.fms_id, fl_status.fleet_status_desc(fl_status.fleet_status(fms.fms_id)), fms.unit_no, fms.vehicle_cost_centre, fms.vin, mdl.model_desc,"
				      +"    ea.c_id, ea.account_code, ea.account_name, ea.account_type," 
                      +"    drv.drv_id, fl_status.driver_status_desc(fl_status.driver_status(drv.drv_id)), drv.active_ind, drv.pool_mgr, drv.driver_surname, drv.driver_forename,"  
                      +"    dra.business_ind, dra.business_address_line, dra.address_line_1, dra.address_line_2," 
                      +"    dra.town_city, dra.region, dra.postcode, drv.email, cnr.contact_area_code, cnr.contact_number, cnr.contact_extension_number,"
                      +"    ccln.start_date, ccln.end_date, ccln.actual_end_date, ccln.in_serv_date, ccln.out_of_service_date,"
                      +"    mrq.mrq_id, mrq.job_no, invdoc.doc_no, sup.ea_account_code, sup.supplier_name, srvinvdoc.doc_no AS \"Service Provider Invoice\","
                      +"	decode(urv.lic_plate_no, null, fms.reg_no, lic_plate_no)," 
                      +"		(SELECT count(mr.mrq_id) "
                      +"			FROM maintenance_requests mr, maint_request_status_codes mrsc " 
                      +"			WHERE mr.fms_fms_id = fms.fms_id "
                      +"				AND mrsc.maint_request_status = mr.maint_request_status "
                      +"				AND mrsc.sort_order < 6),"	
					  +"	null AS C9, null AS C10, null AS C11, null AS C12");			
		}
		
        sqlStmt.append("  FROM fleet_masters fms ");
        
		if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getVehSchSeq())){
	       	 sqlStmt.append(" INNER JOIN VEHICLE_SCHEDULES vsch ON fms.fms_id = vsch.fms_fms_id ");
		}
        
        sqlStmt.append("    INNER JOIN maintenance_requests mrq ON fms.fms_id = mrq.fms_fms_id"  
                      +"    INNER JOIN suppliers sup ON mrq.sup_sup_id = sup.sup_id"
        		      +"    INNER JOIN models mdl ON fms.mdl_mdl_id = mdl.mdl_id"
        			  +"    LEFT JOIN doc podoc ON (mrq.mrq_id = podoc.generic_ext_id AND podoc.c_id = :corporateEntityId AND podoc.doc_type = :purchaseOrderDocumentType AND podoc.doc_status = :purchaseOrderDocumentStatus)"
                      +"    LEFT JOIN  doc_links dlk ON podoc.doc_id = dlk.parent_doc_id"
                      +"    LEFT JOIN doc srvinvdoc ON dlk.child_doc_id = srvinvdoc.doc_id"
                      +"    LEFT JOIN docl_links dllk ON srvinvdoc.doc_id = dllk.parent_doc_id"
                      +"    LEFT JOIN doc invdoc ON dllk.child_doc_id = invdoc.doc_id"
                      +"    LEFT JOIN unit_registrations urv ON fms.fms_id = urv.fms_fms_id "
                      +"			  AND urv.urg_id = (SELECT MAX(URG_ID) "
                      +"								  FROM unit_registrations "
                      +"								 WHERE fms_fms_id = urv.fms_fms_id "
                      +"								   AND issued_date = (SELECT MAX(issued_date) "
                      +"														FROM unit_registrations "
                      +"													   WHERE fms_fms_id = urv.fms_fms_id))"
                      +"    LEFT JOIN driver_allocations dal ON dal.fms_fms_id = fms.fms_id" 
                      +"    LEFT JOIN drivers drv ON dal.drv_drv_id = drv.drv_id"
                      +"    LEFT JOIN driver_addresses dra ON (dra.drv_drv_id = drv.drv_id AND dra.address_type = 'GARAGED')"
                      +"    LEFT JOIN contact_numbers cnr ON (cnr.drv_drv_id = drv.drv_id AND cnr.preferred_ind = 'Y')"
                      +"    LEFT JOIN contract_lines ccln ON fms.fms_id = ccln.fms_fms_id " 
                      +"    LEFT JOIN contracts con ON ccln.con_con_id = con.con_id" 
                      +"    LEFT JOIN external_accounts ea ON (con.ea_account_code = ea.account_code AND con.ea_account_type = ea.account_type AND con.ea_c_id = :corporateEntityId)"); 

        //if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientFleetReferenceNumber()))
        sqlStmt.append("  WHERE 1 = 1 ");

		// conditionally search VEHICLE_COST_CENTRE
		if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getClientFleetReferenceNumber())){        
			sqlStmt.append(" AND upper(fms.vehicle_cost_centre) LIKE :clientFleetReferenceNumber");
		}
		//Based on the number of characters in the VIN compare from right-to-left the last number of characters
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getVIN()) && vehicleSearchCriteriaVO.getVIN().length() > 0) {
			sqlStmt.append( 
					   "      AND fms.vin LIKE upper(:VIN)");				
		} /*else {
			sqlStmt.append( 
					   "      AND nvl(trim(fms.vin), '%') LIKE upper(:VIN)");			
		}*/
		
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getUnitNo()))
			sqlStmt.append("      AND fms.unit_no LIKE :unitNumber " );       
                      //+"      AND mdl.mdl_id = fms.mdl_mdl_id"        
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getDriverSurName()))
			sqlStmt.append("      AND upper(drv.driver_surname) LIKE upper(:driverSurname)"); 
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getDriverForeName()))
			sqlStmt.append("      AND upper(drv.driver_forename) LIKE upper(:driverForename)" );              
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getLicensePlateNo()))
			sqlStmt.append("      AND (upper(urv.lic_plate_no) LIKE upper(:licensePlateNumber) OR upper(trim(fms.reg_no)) LIKE upper(:licensePlateNumber))");                       
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountNumber()))
			sqlStmt.append("      AND ea.account_code LIKE :clientAccountNumber"); 
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountName()))
			sqlStmt.append("      AND upper(trim(ea.account_name)) LIKE upper(:clientAccountName)");
        //Purchase Order: has a type outside of AP and AR, but route is same as AP
        if(isPOSearch(vehicleSearchCriteriaVO)){
        	sqlStmt.append("      AND mrq.job_no LIKE :purchaseOrderNumber");         	
        }
        
        //Service Provider/Payee invoice: are handled through accounts payable
        if (isServiceProviderInvoiceSearch(vehicleSearchCriteriaVO)) {
        	sqlStmt.append("      AND nvl(upper(trim(sup.supplier_name)), '%') LIKE upper(:serviceProviderAccountName)");
        	sqlStmt.append("      AND nvl(srvinvdoc.doc_no, '%') LIKE :serviceProviderInvoiceNumber");
            sqlStmt.append("      AND srvinvdoc.c_id = :corporateEntityId");
            sqlStmt.append("      AND srvinvdoc.update_control_code = :updateControlCode");
            sqlStmt.append("      AND srvinvdoc.doc_status = :invoiceAPDocumentStatus");
            sqlStmt.append("      AND srvinvdoc.doc_type = :serviceProviderDocumentType");          	
        }
        //Internal invoice: are handled through accounts receivable
        if(isInternalInvoiceSearch(vehicleSearchCriteriaVO)) {
        	sqlStmt.append("      AND invdoc.doc_no LIKE :internalInvoiceNumber");
            sqlStmt.append("      AND invdoc.c_id = :corporateEntityId");
            sqlStmt.append("      AND invdoc.update_control_code = :updateControlCode");  
            sqlStmt.append("      AND invdoc.doc_status = :invoiceARDocumentStatus");       	   
            sqlStmt.append("      AND invdoc.doc_type = :internalDocumentType");        	
        }
        
		// conditionally search VEHICLE_SCHEDULES
		if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getVehSchSeq())){
	       	 sqlStmt.append(" AND vsch.veh_sch_seq LIKE :vehSchSeq");
		}

        sqlStmt.append(
                      "	  	  AND (invdoc.doc_type IS NULL"
                      +"	  		  OR (invdoc.doc_type = 'INVOICEAR' AND rownum = 1) )"  
                      +"      AND (dllk.parent_line_id IS NULL" 
                      +"               OR dllk.parent_line_id IN (SELECT MIN(dllk2.parent_line_id)" 
                      +"                                              FROM docl_links dllk2" 
                      +"                                              WHERE dllk2.parent_doc_id = srvinvdoc.doc_id" 
                      +"                                                  AND dllk2.child_doc_id = invdoc.doc_id) )"
                      +"      AND (dal.dal_id IS NULL"
                      +"              OR dal.dal_id IN (SELECT min(dal2.dal_id)"                                    
                      +"                                    FROM driver_allocations dal2"                                    
                      +"                                    WHERE dal2.fms_fms_id = fms.fms_id"                                        
                      +"                                        AND ( (trunc(mrq.actual_start_date) BETWEEN trunc(dal2.from_date) AND trunc(nvl(dal2.to_date, sysdate)))"
                      +"                                                   OR (trunc(mrq.planned_start_date) BETWEEN trunc(dal2.from_date) AND trunc(nvl(dal2.to_date, sysdate)))"
                      +"                                                   OR dal2.dal_id IN (SELECT MAX(dal3.dal_id) FROM driver_allocations dal3 WHERE dal3.fms_fms_id = fms.fms_id))))"
                      +"      AND (ccln.cln_id IS NULL"
                      +"               OR ccln.cln_id IN (SELECT max(cln2.cln_id)" 
                      +"                       FROM contract_lines cln2"
                      +"                       WHERE  cln2.fms_fms_id = fms.fms_id"
                      
                      
                      +"                           AND ( (mrq.actual_start_date IS NULL AND mrq.planned_start_date IS NULL)"                                
                      +"                                  OR ( NOT(cln2.start_date is NULL and cln2.in_serv_date is NULL)"                                    
                      +"                                       AND (( mrq.actual_start_date IS NULL AND mrq.planned_start_date IS NULL )"                                       
                      +"                                              OR ( (cln2.end_date IS NULL AND cln2.actual_end_date IS NULL)"                                              
                      +"                                                    OR (trunc(mrq.actual_start_date) <= trunc(cln2.end_date)" 
                      +"                                                    OR trunc(mrq.actual_start_date) <= trunc(nvl(cln2.actual_end_date, sysdate)))" 
                      +"                                                    OR (trunc(mrq.planned_start_date) <= trunc(cln2.end_date)" 
                      +"                                                    OR trunc(mrq.planned_start_date) <= trunc(nvl(cln2.actual_end_date, sysdate))) )) )"
                      +"                                              OR (cln2.start_date IS NOT NULL AND trunc(mrq.actual_start_date) > trunc(cln2.start_date) ) ))) ");
        
		//Defaults order by unless otherwise specified by the passed in sort object
		if(!MALUtilities.isEmpty(sort)){
			sqlStmt.append(
					"     ORDER BY ");
			for ( Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext(); ) {
				Order order = orderIterator.next();
				
				if(DataConstants.VEHICLE_SEARCH_SORT_FIELD_UNIT_NO.equals(order.getProperty())){	
					sqlStmt.append(" fms.unit_no " + order.getDirection());
				}
				if(DataConstants.VEHICLE_SEARCH_SORT_FIELD_DRIVER_NAME.equals(order.getProperty())){	
					sqlStmt.append(" drv.driver_surname " + order.getDirection() + ", drv.driver_forename " + order.getDirection());
				}
				if(DataConstants.VEHICLE_SEARCH_SORT_FIELD_ACCOUNT_NAME.equals(order.getProperty())){	
					sqlStmt.append(" ea.account_name " + order.getDirection());
				}				
				
				if(orderIterator.hasNext()){
					sqlStmt.append(", ");
				}
	
			}			
		} else {
			sqlStmt.append("  ORDER BY drv.driver_surname asc, drv.driver_forename asc, dal.from_date desc");
		}        
        
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientFleetReferenceNumber()))
			parameterMap.put("clientFleetReferenceNumber",vehicleSearchCriteriaVO.getClientFleetReferenceNumber().toUpperCase());
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getLicensePlateNo()))
			parameterMap.put("licensePlateNumber", vehicleSearchCriteriaVO.getLicensePlateNo());
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getUnitNo()))
			parameterMap.put("unitNumber",vehicleSearchCriteriaVO.getUnitNo());		
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getVIN()))
			parameterMap.put("VIN", DataUtilities.prependWildCardToLeft(vehicleSearchCriteriaVO.getVIN()));
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getDriverSurName()))
			parameterMap.put("driverSurname", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getDriverSurName()));
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getDriverForeName()))
			parameterMap.put("driverForename", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getDriverForeName()));
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountNumber()))
			parameterMap.put("clientAccountNumber", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getClientAccountNumber()));
		if(!MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountName()))
			parameterMap.put("clientAccountName", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getClientAccountName()));		
		parameterMap.put("corporateEntityId",vehicleSearchCriteriaVO.getCorporateEntity().getCorpId());	
		parameterMap.put("purchaseOrderDocumentType", DocumentType.PURCHASE_ORDER.getdocumentType());
		parameterMap.put("purchaseOrderDocumentStatus", DocumentStatus.PURCHASE_ORDER_STATUS_RELEASED.getCode());
		
        if(this.isPOSearch(vehicleSearchCriteriaVO)){
        	parameterMap.put("purchaseOrderNumber", vehicleSearchCriteriaVO.getPurchaseOrderNumber().toUpperCase());
        }
        if (this.isServiceProviderInvoiceSearch(vehicleSearchCriteriaVO)) {
        	parameterMap.put("serviceProviderAccountName", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getServiceProviderName()));
        	parameterMap.put("serviceProviderInvoiceNumber", vehicleSearchCriteriaVO.getServiceProviderInvoiceNumber().toUpperCase());
        	parameterMap.put("invoiceAPDocumentStatus", DocumentStatus.INVOICE_ACCOUNTS_PAYABLE_STATUS_POSTED.getCode());     		
    		parameterMap.put("serviceProviderDocumentType", DocumentType.ACCOUNTS_PAYABLE.getdocumentType()); 
    		parameterMap.put("updateControlCode", "FLMAINT");	    		
        }
        if(this.isInternalInvoiceSearch(vehicleSearchCriteriaVO)) {
        	parameterMap.put("internalInvoiceNumber", vehicleSearchCriteriaVO.getInternalnvoiceNumber().toUpperCase());
    		parameterMap.put("internalDocumentType", DocumentType.ACCOUNTS_RECEIVABLE.getdocumentType());   
    		parameterMap.put("invoiceARDocumentStatus", DocumentStatus.INVOICE_ACCOUNTS_RECEIVABLE_STATUS_POSTED.getCode()); 
    		parameterMap.put("updateControlCode", "FLMAINT");	    		
        }
        if(!MALUtilities.isEmpty(vehicleSearchCriteriaVO.getVehSchSeq())){
			parameterMap.put("vehSchSeq", DataUtilities.appendWildCardToRight(vehicleSearchCriteriaVO.getVehSchSeq()));
		}

		query = entityManager.createNativeQuery(sqlStmt.toString());		 
		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 			
		
		return query;		
	}
		
	
	private boolean isPOInvoiceSearch(VehicleSearchCriteriaVO vehicleSearchCriteriaVO){
		boolean ret = false;
		if( MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getServiceProviderName()) 
				&& MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getServiceProviderInvoiceNumber())
				&& MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getInternalnvoiceNumber()) 
				&& MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getPurchaseOrderNumber()) ){	
			ret = false;
		} else {
			ret = true;
		}		
		return ret;
	}
	
	private boolean isPOSearch(VehicleSearchCriteriaVO vehicleSearchCriteriaVO){
		boolean ret = false;
		if(MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getPurchaseOrderNumber())){	
			ret = false;
		} else {
			ret = true;
		}		
		return ret;		
	}
	
	private boolean isDriverSearch(VehicleSearchCriteriaVO vehicleSearchCriteriaVO){
		boolean ret = false;
		if(MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getDriverName())){	
			ret = false;
		} else {
			ret = true;
		}		
		return ret;			
	}
	
	private boolean isClientSearch(VehicleSearchCriteriaVO vehicleSearchCriteriaVO){
		boolean ret = false;
		if(MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountNumber()) 
				&& MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getClientAccountName())){	
			ret = false;
		} else {
			ret = true;
		}		
		return ret;			
	}	
	
	private boolean isServiceProviderInvoiceSearch(VehicleSearchCriteriaVO vehicleSearchCriteriaVO){
		boolean ret = false;
		if( MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getServiceProviderInvoiceNumber()) 
				&& MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getServiceProviderName()) ){	
			ret = false;
		} else {
			ret = true;
		}		
		return ret;		
	}	
	
	private boolean isInternalInvoiceSearch(VehicleSearchCriteriaVO vehicleSearchCriteriaVO){
		boolean ret = false;
		if(MALUtilities.isEmptyString(vehicleSearchCriteriaVO.getInternalnvoiceNumber())){	
			ret = false;
		} else {
			ret = true;
		}		
		return ret;		
	}
	
	/**
	 * Filters the result list down to those vehicles that match the unit status criteria: Active, Inactive, or Both
	 * @param vehicleSearchResults
	 * @return List Containing filtered search results based on the unit status search criteria.
	 */
	private List<VehicleSearchResultVO> filterByVehicleStatus(VehicleSearchCriteriaVO searchCriteria, List<VehicleSearchResultVO> searchResults){
		List<VehicleSearchResultVO> filteredResults = null;
		VehicleSearchResultVO searchResult = null;
		
		if(!searchCriteria.getUnitStatus().equals(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH)){
			filteredResults = new ArrayList<VehicleSearchResultVO>();
			
			for(Iterator<VehicleSearchResultVO> iter = searchResults.iterator(); iter.hasNext();){
				searchResult = iter.next();
				
				if(searchCriteria.getUnitStatus().equals(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE)){
					if(searchResult.getUnitStatus().equals(ActiveVehicleStatus.STATUS_ON_ORDER.getDescription())
							|| searchResult.getUnitStatus().equals(ActiveVehicleStatus.STATUS_PENDING_LIVE.getDescription())
							|| searchResult.getUnitStatus().equals(ActiveVehicleStatus.STATUS_ON_CONTRACT.getDescription())
							|| searchResult.getUnitStatus().equals(ActiveVehicleStatus.STATUS_ON_STOCK.getDescription())){
						filteredResults.add(searchResult);
					}
				} else {
					if(!(searchResult.getUnitStatus().equals(ActiveVehicleStatus.STATUS_ON_ORDER.getDescription())
							|| searchResult.getUnitStatus().equals(ActiveVehicleStatus.STATUS_PENDING_LIVE.getDescription())
							|| searchResult.getUnitStatus().equals(ActiveVehicleStatus.STATUS_ON_CONTRACT.getDescription())
							|| searchResult.getUnitStatus().equals(ActiveVehicleStatus.STATUS_ON_STOCK.getDescription()))){
						filteredResults.add(searchResult);	
					}
				}
				
			}
		} else {
			filteredResults = searchResults;
		}			
		
		return filteredResults;
	}
	
	/**
	 * Created this method to support pageable on a result list that needs to be modified, in java, after the execution of a query.
	 * @param searchResults Result list from un-pageable query execution
	 * @param pageable Page object
	 * @return Result list after paging has been applied
	 */
	private  List<VehicleSearchResultVO> applypPagingToResults(List<VehicleSearchResultVO> searchResults, Pageable pageable){
		List<VehicleSearchResultVO> filteredResults = searchResults;		
		
		if(pageable != null){
			int startIndex = pageable.getPageNumber() * pageable.getPageSize();
			int endIndex = startIndex + pageable.getPageSize();

			if(startIndex > 0){
				for(int i = startIndex ; i >= 0; i--){
					try {
						filteredResults.remove(i);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("***** " + i + " --- " + e.getMessage());
					}

				}
			}

			if(filteredResults.size() > endIndex){ 
				for(int i = filteredResults.size() - 1; i > endIndex; i--)
					filteredResults.remove(i);
			}
		}	
		
		return filteredResults;		
	}
		
}
