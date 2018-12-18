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
import javax.sql.DataSource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.AccessoryVO;
import com.mikealbert.data.vo.DealerAccessoryVO;
import com.mikealbert.data.vo.StockUnitsLovVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.util.MALUtilities;



public  class FleetMasterDAOImpl extends GenericDAOImpl<FleetMaster, Long> implements FleetMasterDAOCustom {

	private static final long serialVersionUID = 1L;	
	@Resource DataSource dataSource;
	@Resource FleetMasterDAO fleetMasterDAO;

	public String getFleetStatus(Long fmsId){
		String stmt = "SELECT fl_status.fleet_status(?) FROM DUAL";
		String status = null;		

		status = (String)entityManager.createNativeQuery(stmt)
				.setParameter(1, fmsId)
				.getSingleResult();

		return status;
	}

	@Override
	public List<Long> findOnContractOrOnOrderFmsIdsByVinOrVehicleCostCenter(String vin, String vehicleCostCentre) {
		String sql = 
			" SELECT fms.FMS_ID" +
			" FROM willow2k.contract_lines cln, willow2k.drivers drv, willow2k.fleet_masters fms" +
			" WHERE cln.fms_fms_id = fms.fms_id" +
			"	AND cln.drv_drv_id = drv.drv_id(+)" +
			"	AND cln.rev_no = (SELECT MAX (cln1.rev_no)" +
			"					  FROM willow2k.contract_lines cln1" +
			"					  WHERE cln1.con_con_id = cln.con_con_id" +
			"						AND cln1.fms_fms_id = cln.fms_fms_id AND rev_date <= SYSDATE" +
			"						AND actual_end_date IS NULL AND start_date IS NOT NULL)" +
			"	AND LOWER(fms.SEARCH_COLUMN) = LOWER(?)" +
			" UNION" +
			" Select fms.FMS_ID" +
			" FROM willow2k.fleet_masters fms, willow2k.quotation_models qm" +
			" WHERE fms.unit_no = qm.unit_no" +
			"	AND qm.quote_status IN ('3', '16', '17')" +
			"	AND LOWER(fms.SEARCH_COLUMN) = LOWER(?)";
		
		@SuppressWarnings("unchecked")
		List<BigDecimal> vinRows =
			entityManager.createNativeQuery(sql.toString().replaceAll("SEARCH_COLUMN", "VIN"))
				.setParameter(1, vin)
				.setParameter(2, vin)
				.getResultList();

		List<Long> fmsIds = new ArrayList<Long>();
		for(BigDecimal fmsId: vinRows) {
			fmsIds.add(fmsId.longValue());
		}

		if(fmsIds.size() > 0) {
			return fmsIds;
		}

		@SuppressWarnings("unchecked")
		List<BigDecimal> vehicleCostCenterRows =
			entityManager.createNativeQuery(sql.toString().replaceAll("SEARCH_COLUMN", "VEHICLE_COST_CENTRE"))
				.setParameter(1, vehicleCostCentre)
				.setParameter(2, vehicleCostCentre)
				.getResultList();

		fmsIds = new ArrayList<Long>();
		for(BigDecimal fmsId: vehicleCostCenterRows) {
			fmsIds.add(fmsId.longValue());
		}

		return fmsIds;
	}

	public List<StockUnitsLovVO> findStockUnits(String unitNo, String vehicleDesc, Pageable pageable, Sort sort) {
		List<StockUnitsLovVO> results = null;
		Query query = null;	
		
		query = generateStockUnitQuery(unitNo, vehicleDesc, sort, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}		
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<StockUnitsLovVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				StockUnitsLovVO result = new StockUnitsLovVO();
				result.setFmsId(((BigDecimal)record[i]).longValue());	
				result.setUnitNo((String)record[i+=1]);					
				result.setVehicleDescription(((String)record[i+=1]));
				Object obj = record[i+=1]; 
				if(obj != null) {
					result.setLastOdoReading(((BigDecimal)obj).intValue());	
				}
				result.setExteriorColor(((String)record[i+=1]));
				result.setTrimColor(((String)record[i+=1]));
				result.setQuoteCount(((BigDecimal)record[i+=1]).intValue());
				result.setReceivedDate((Date) record[i+=1]);
				result.setMdlId(((BigDecimal)record[i+=1]).longValue());
				result.setVin(((String)record[i+=1]));
				result.setStandardEDINo(((String)record[i+=1]));				
				results.add(result);
			}
		}		
		
		return results;				
	}

	@Override
	public int findStockUnitCount(String unitNo, String vehicleDesc){
		int count = 0;
		Query query = null;
		query = generateStockUnitQuery(unitNo, vehicleDesc, null, true);
		count = ((BigDecimal)query.getSingleResult()).intValue();				
		return count;
	}

	private Query generateStockUnitQuery(String unitNo, String vehicleDesc, Sort sort, boolean isCountQuery){
		StringBuilder sqlStmt;	    
		Query query = null;
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		sqlStmt = new StringBuilder("");
		sqlStmt.append("WITH fms1 AS ( ");
			
		sqlStmt.append("    SELECT /*+ MATERIALIZE */ FMS_ID,UNIT_NO,colour_code,trim_colour,mdl_mdl_id, confirmed_delivery_date, vin ");
		sqlStmt.append("        FROM FLEET_MASTERS FMS "); 
		sqlStmt.append("        WHERE adjustment_value IS NOT NULL ");
		sqlStmt.append("            AND designation_code = 'STOCK' ");			
		
		if(!MALUtilities.isEmpty(unitNo)){
			sqlStmt.append("        AND UNIT_NO = :unitNo ");
			parameterMap.put("unitNo", unitNo);
		}
			
		sqlStmt.append("            AND NOT EXISTS (SELECT 1 ");
		sqlStmt.append("                                FROM disposal_requests ");
		sqlStmt.append("                                WHERE fl_ams_status.disposal_status(fms.fms_id,drq_id) IN ('A','Z','C','3','4','8') ");
		sqlStmt.append("                                    AND fms_id = fms.fms_id) "); 
		sqlStmt.append("            AND NOT EXISTS (SELECT 1 ");
		sqlStmt.append("                                FROM current_allocation_view "); 
		sqlStmt.append("                                WHERE fms_fms_id = fms_id)  "); 
		sqlStmt.append("            AND NOT EXISTS (SELECT 1 "); 
		sqlStmt.append("                                FROM doc, docl, contract_lines cln "); 
		sqlStmt.append("                                WHERE doc.c_id = 1 ");
		sqlStmt.append("                                    AND doc.doc_type = 'PORDER' ");
		sqlStmt.append("                                    AND doc.doc_no = doc.doc_no ");
		sqlStmt.append("                                    AND doc.source_code = 'FLQUOTE' ");
		sqlStmt.append("                                    AND doc.doc_status = 'R' ");
		sqlStmt.append("                                    AND docl.doc_id = doc.doc_id ");
		sqlStmt.append("                                    AND docl.qty_outstanding > 0 ");
		sqlStmt.append("                                    AND docl.user_def4 = 'MODEL' ");
		sqlStmt.append("                                    AND doc.generic_ext_id = cln.qmd_qmd_id ");
		sqlStmt.append("                                    AND cln.fms_fms_id = fms_id)  "); 
		sqlStmt.append("                                    AND (NOT EXISTS (SELECT 1 ");
		sqlStmt.append("                                                         FROM quotation_models ");
		sqlStmt.append("                                                         WHERE fms_fms_id IS NOT NULL ");
		sqlStmt.append("                                                             AND fms_fms_id = fms_id ) ");
		sqlStmt.append("                                         OR NOT EXISTS (SELECT 1 ");
		sqlStmt.append("                                                            FROM quotation_models ");
		sqlStmt.append("                                                            WHERE quote_status NOT IN ('1','4','7','8') ");
		sqlStmt.append("                                                                AND fms_fms_id IS NOT NULL ");
		sqlStmt.append("                                                                AND fms_fms_id = fms_id )) "); 
			
		sqlStmt.append(") ");
			
		if(isCountQuery){
			sqlStmt.append("SELECT count(1) ");				
		} else {
			sqlStmt.append("SELECT fms1.fms_id, fms1.unit_no, m.model_desc, fl_odo.last_odo(fms1.fms_id), cc.description, tc.trim_desc, ");
			sqlStmt.append("        (SELECT count(rowid) from quotation_models qm WHERE qm.fms_fms_id = fms1.fms_id AND quote_status NOT IN ('7','8')), ");
			sqlStmt.append("        fms1.confirmed_delivery_date, fms1.mdl_mdl_id, fms1.vin, m.standard_edi_no  ");				
		}
			
		sqlStmt.append("    FROM colour_codes cc, ");
        sqlStmt.append("         trim_codes tc,  "); 
        sqlStmt.append("         models m,  ");
        sqlStmt.append("         fms1 ");
        sqlStmt.append("    WHERE fms1.colour_code = cc.colour_code  "); 
        sqlStmt.append("        AND fms1.trim_colour = tc.trim_code "); 
        sqlStmt.append( "       AND fms1.mdl_mdl_id = m.mdl_id  ");			
			
    	if(!MALUtilities.isEmpty(vehicleDesc)){
    		sqlStmt.append("    AND LOWER(m.model_desc) like LOWER(:vehicleDesc) ");
    		parameterMap.put("vehicleDesc", DataUtilities.appendWildCardToRight(DataUtilities.prependWildCardToLeft(vehicleDesc)));
    	}
    				
    	//Defaults order by unless otherwise specified by the passed in sort object		
    	if(!isCountQuery){
    		if(!MALUtilities.isEmpty(sort)){
    			sqlStmt.append(
    					"   ORDER BY ");
    			for ( Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext(); ) {
    				Order order = orderIterator.next();
    						
    				if(DataConstants.STOCK_UNIT_LOV_SORT_UNIT_NO.equals(order.getProperty())){	
    					sqlStmt.append(" fms1.unit_no " + order.getDirection());
    				}									
    				if(DataConstants.STOCK_UNIT_LOV_SORT_VEH_DESC.equals(order.getProperty())){	
    					sqlStmt.append(" m.model_desc " + order.getDirection());
    				}	
    			}
    		}else{
    			sqlStmt.append(
    					"   ORDER BY fms1.unit_no ASC ");
    		}
    	}	


    	query = entityManager.createNativeQuery(sqlStmt.toString());
    	for (String paramName : parameterMap.keySet()) {
    		query.setParameter(paramName, parameterMap.get(paramName));
    	} 

    	return query;    			
		
	}
	
	public List<String> getStandardEquipmentForFmsId(long fmsId) {
		
		List<String> results = null;
		Query query = null;	
		StringBuilder sqlStmt;
		
		sqlStmt = new StringBuilder("");
		sqlStmt.append(" select sacc.description "
				+ " from fleet_masters fm, fleet_master_accessories fma, standard_accessories sa, standard_accessory_codes sacc"
				+ " where  fm.fms_id = :fmsId " 
				+ " and fm.fms_id = fma.fms_fms_id "
				+ " and fma.sac_sac_id = sa.sac_id "
				+ " and sa.sacc_sacc_id = sacc.sacc_id "
				+ " and fm.mdl_mdl_id = sa.mdl_mdl_id " );			
		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		query.setParameter("fmsId", fmsId);
		
		@SuppressWarnings("unchecked")
		List<Object>resultList = (List<Object>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<String>();
			
			for(Object record : resultList){
				results.add((String)record);
			}
		}		
		
		return results;				

		
	}

	public List<String> getModelEquipmentForFmsId(long fmsId) {
		
		List<String> results = null;
		Query query = null;	
		StringBuilder sqlStmt;
		
		sqlStmt = new StringBuilder("");
		sqlStmt.append(" select ac.description "
				+ " from fleet_masters fm, fleet_master_accessories fma, optional_accessories oa, models m, accessory_codes ac "
				+ " where  fm.fms_id = :fmsId " 
				+ " and fm.fms_id = fma.fms_fms_id "
				+ " and fm.mdl_mdl_id = m.mdl_id "
				+ " and fma.oac_oac_id = oa.oac_id "
				+ " and oa.assc_assc_id = assc_Id " 
				+ " and m.mtp_mtp_id = ac.mtp_mtp_id ");			
		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		query.setParameter("fmsId", fmsId);
		
		@SuppressWarnings("unchecked")
		List<Object>resultList = (List<Object>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<String>();
			
			for(Object record : resultList){
				results.add((String)record);
			}
		}		
		
		return results;				
	}
	
	public List<String> getDealerEquipmentForFmsId(long fmsId) {
		
		List<String> results = null;
		Query query = null;	
		StringBuilder sqlStmt;
		
		sqlStmt = new StringBuilder("");
		sqlStmt.append(" select dac.description "
				+ " from fleet_masters fm, fleet_master_accessories fma, dealer_accessories da, models m, dealer_accessory_codes dac "
				+ " where  fm.fms_id = :fmsId " 
				+ " and fm.fms_id = fma.fms_fms_id "
				+ " and fm.mdl_mdl_id = m.mdl_id "
				+ " and fma.dac_dac_id = da.dac_id "
				+ " and da.accessory_code = dac.accessory_code " 
				+ " and fm.mdl_mdl_id = da.mdl_mdl_id ");			

		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		query.setParameter("fmsId", fmsId);
		
		@SuppressWarnings("unchecked")
		List<Object>resultList = (List<Object>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<String>();
			
			for(Object record : resultList){
				results.add((String)record);
			}
		}		
		
		return results;				
	}
	
	
	public List<AccessoryVO> getStandardAccessoriesByFmsId(long fmsId) {
		List<AccessoryVO> results = null;
		AccessoryVO result;
		Query query = null;	
		StringBuilder stmt;	
	
		stmt = new StringBuilder("");
		stmt.append(" SELECT sac.sac_id, sacc.new_accessory_code, sacc.description ");
		stmt.append("     FROM fleet_masters fms, fleet_master_accessories fma, standard_accessories sac, standard_accessory_codes sacc ");
		stmt.append("     WHERE fms.fms_id = :fmsId "); 
		stmt.append("         AND fms.fms_id = fma.fms_fms_id ");
		stmt.append("         AND fma.sac_sac_id = sac.sac_id ");
		stmt.append("         AND sac.sacc_sacc_id = sacc.sacc_id  "); 
		stmt.append("         AND fms.mdl_mdl_id = sac.mdl_mdl_id ");			
		
		query = entityManager.createNativeQuery(stmt.toString());		 		 
		query.setParameter("fmsId", fmsId);
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				result = new DealerAccessoryVO();
				result.setId(((BigDecimal)record[i]).longValue());
				result.setCode((String)record[i+=1]);
				result.setDescription((String)record[i+=1]);				

				results.add(result);
			}
		}
		
		return results;			
	}
	
	public List<AccessoryVO> getOptionalAccessoriesByFmsId(long fmsId) {
		List<AccessoryVO> results = null;
		AccessoryVO result;
		Query query = null;	
		StringBuilder stmt;	
		
		stmt = new StringBuilder("");
		stmt.append(" SELECT oac.oac_id, assc.new_accessory_code, assc.description ");
		stmt.append("     FROM fleet_masters fms, fleet_master_accessories fma, optional_accessories oac, models mdl, accessory_codes assc ");
		stmt.append("     WHERE  fms.fms_id = :fmsId "); 
		stmt.append("         AND fms.fms_id = fma.fms_fms_id ");
		stmt.append("         AND fms.mdl_mdl_id = mdl.mdl_id ");
		stmt.append("         AND fma.oac_oac_id = oac.oac_id ");
		stmt.append("         AND oac.assc_assc_id = assc.assc_Id "); 
		stmt.append("         AND mdl.mtp_mtp_id = assc.mtp_mtp_id ");		
		
		query = entityManager.createNativeQuery(stmt.toString());		 		 
		query.setParameter("fmsId", fmsId);
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				result = new DealerAccessoryVO();
				result.setId(((BigDecimal)record[i]).longValue());
				result.setCode((String)record[i+=1]);
				result.setDescription((String)record[i+=1]);				

				results.add(result);
			}
		}	
		
		return results;			
	}
	
	public List<AccessoryVO> getDealerAccessoriesByFmsId(long fmsId) {
		List<AccessoryVO> results = null;
		AccessoryVO result;
		Query query = null;	
		StringBuilder stmt;
		
		stmt = new StringBuilder("");
		stmt.append(" SELECT dac.dac_id, dacc.new_accessory_code, dacc.description ");
		stmt.append("     FROM fleet_masters fms, fleet_master_accessories fma, dealer_accessories dac, models mdl, dealer_accessory_codes dacc ");
		stmt.append("     WHERE  fms.fms_id = :fmsId "); 
		stmt.append("         AND fms.fms_id = fma.fms_fms_id ");
		stmt.append("         AND fms.mdl_mdl_id = mdl.mdl_id ");
		stmt.append("         AND fma.dac_dac_id = dac.dac_id ");
		stmt.append("         AND dac.accessory_code = dacc.accessory_code "); 
		stmt.append("         AND fms.mdl_mdl_id = dac.mdl_mdl_id ");	
		
		query = entityManager.createNativeQuery(stmt.toString());		 		 
		query.setParameter("fmsId", fmsId);
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				result = new DealerAccessoryVO();
				result.setId(((BigDecimal)record[i]).longValue());
				result.setCode((String)record[i+=1]);
				result.setDescription((String)record[i+=1]);				

				results.add(result);
			}
		}		
		
		return results;			
	}

	@Override
	public BigDecimal getStockUnitCost(String unitNo, Long cId) throws MalBusinessException {
		
		String stmt = "select quotation_wrapper.get_stock_unit_cost(?, ?) from dual";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, unitNo);
		query.setParameter(2, cId);

		@SuppressWarnings("rawtypes")
		List results = query.getResultList();
        if (results.isEmpty()){
        	return null;
        }
        else{
        	return (BigDecimal) query.getSingleResult();
        }
	}

	@Override
	public BigDecimal getLastOdoMeterReadingByFmsId(Long fmsId) {
		
		String stmt = "SELECT fl_odo.last_odo(?) FROM DUAL";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, fmsId);

		BigDecimal lastOdoReading = (BigDecimal) query.getSingleResult();
		
		return lastOdoReading;
	}

	@Override
	public boolean isActivePOUnit(String unitNo, Long qprId, Long cId) throws MalBusinessException {
		
		String stmt = "SELECT 1"
				+ " FROM (SELECT unit_no,"
				+ " quotation_mgr.is_vehicle_paid(fms_id) paid,"
				+ " quotation_mgr.is_vehicle_received(fms_id) received,"
				+ " (SELECT product_type FROM quotation_profiles qpr, products prd WHERE qpr.prd_product_code = prd.product_code AND qpr.qpr_id = :qpr_qpr_id) product_type,"
				+ " ut.get_willow_config(:c_id, 'AM_PRODUCT_TYPE') am_product_type"
				+ " FROM (Select a.unit_no, a.fms_id, a.mdl_mdl_id, b.mrg_mrg_id, b.mak_mak_id, b.mtp_mtp_id, h.doc_id"
				+ " FROM fleet_masters a, models b, make_model_ranges c, makes d, model_types e, dist f, docl g, doc h"
				+ " WHERE a.fms_id = f.cdb_code_1"
				+ " AND a.adjustment_value is not null"
				+ " AND a.mdl_mdl_id = b.mdl_id"
				+ " AND b.mrg_mrg_id = c.mrg_id"
				+ " AND b.mak_mak_id = d.mak_id"
				+ " AND c.mtp_mtp_id = e.mtp_id"
				+ " AND f.cdb_code_8 = 'PORDER'"
				+ " AND f.docl_doc_id = g.doc_id"
				+ " AND f.docl_line_id = g.line_id"
				+ " AND f.amount > 0"
				+ " AND g.user_def4 = 'MODEL'"
				+ " AND g.qty_outstanding <> 0"
				+ " AND g.doc_id = h.doc_id"
				+ " AND h.doc_type = 'PORDER'"
				+ " AND h.doc_status = 'R'"
				+ " AND h.source_code = 'FLORDER'"
				+ " AND EXISTS (SELECT 'Y'"
				+ "              FROM quote_model_types qmt"
				+ "             WHERE qmt.qpr_qpr_id = :qpr_qpr_id"
				+ "               AND qmt.mtp_mtp_id = e.mtp_id)"
				+ " AND a.confirmed_delivery_date IS NULL"
				+ " AND a.fms_id NOT IN(SELECT jc.fms_fms_id"
				+ "                       FROM job_cards jc"
				+ "                      WHERE jc.job_status = 'O'))) active_po_vehicle"
				+ " WHERE (product_type = am_product_type AND paid = 'FALSE' AND received = 'FALSE')"
				+ " OR product_type <> am_product_type"
				+ " AND UNIT_NO = :unitNo";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("unitNo", unitNo);
		query.setParameter("qpr_qpr_id", qprId);
		query.setParameter("c_id", cId);
		
		@SuppressWarnings("rawtypes")
		List results = query.getResultList();
        if (results.isEmpty()) 	
        	return false;
        else 
        	return true;
	}

	@Override
	public BigDecimal getUnpaidVehicleCapitalCostFromDocsByFmsId(Long fmsId) throws MalBusinessException {
		
		String stmt = "select quotation_wrapper.get_capital_cost_by_fms_id(?) from dual";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, fmsId);

		@SuppressWarnings("rawtypes")
		List results = query.getResultList();
        if (results.isEmpty()){
        	return null;
        }
        else{
        	return (BigDecimal) query.getSingleResult();
        }
	}

	@Override
	public boolean isActivePOUnit(Long fmsId) throws MalBusinessException {
		
		String stmt = "SELECT 'Y'" +
				"  FROM fleet_masters a,dist b,docl c,doc d" +
				" WHERE b.cdb_code_1        = TO_CHAR(a.fms_id)" +
				"   AND a.adjustment_value IS NOT NULL" +
				"   AND b.cdb_code_8        = 'PORDER'" +
				"   AND b.docl_doc_id       = c.doc_id" +
				"   AND b.docl_line_id      = c.line_id" +
				"   AND b.amount            > 0" +
				"   AND c.user_def4         = 'MODEL'" +
				"   AND c.qty_outstanding  <> 0" +
				"   AND c.doc_id            = d.doc_id" +
				"   AND d.doc_type          = 'PORDER'" +
				"   AND d.doc_status        = 'R'" +
				"   AND d.source_code       = 'FLORDER'" +
				"   AND a.fms_id            = :fmsId" +
				"   AND NOT EXISTS (SELECT 1 " +
				"                     FROM job_cards jc " +
				"                    WHERE jc.fms_fms_id = a.fms_id " +
				"                      AND jc.job_status = 'O')";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter("fmsId", fmsId);
		
		@SuppressWarnings("rawtypes")
		List results = query.getResultList();
        if (results.isEmpty()) 	
        	return false;
        else 
        	return true;
	}
	
	@Override
	public List<Long> findOnContractOrInServiceByVin(String vin) {
		List<Long> results = null;
		Query query = null;	
		StringBuilder sqlStmt;
		
		sqlStmt = new StringBuilder("");
		sqlStmt.append("select distinct fms_id "
						+" from "
						+" (select fms_id "
						+" from contract_lines cln "
						+" join quotation_models qmd on(qmd.qmd_id = cln.qmd_qmd_id) "
						+" join fleet_masters fms on (fms.fms_id = cln.fms_fms_id) "
						+" where qmd.quote_status = '6' "
						+" and NVL(cln.ACTUAL_END_DATE,SYSDATE+1) > SYSDATE "
						+" and fms.vin = :vin "
						+" union all "
						+" select fms_id "
						+" from contract_lines cln "
						+" join fleet_masters fms on (fms.fms_id = cln.fms_fms_id) "
						+" where NVL(cln.ACTUAL_END_DATE,SYSDATE+1) > SYSDATE "
						+" and NVL(cln.in_serv_date,SYSDATE-1) > SYSDATE "
						+" and fms.vin = :vin)");	
		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		query.setParameter("vin", vin);
		
		@SuppressWarnings("unchecked")
		List<Object>resultList = (List<Object>)query.getResultList();
		if(resultList != null){
			results = new ArrayList<Long>();
			
			for(Object record : resultList){
				results.add(((BigDecimal)record).longValue());
			}
		}		
		
		return results;		
	}
	@Override
	public BigDecimal getUnpaidDealerAccessoryCostByFmsId(Long fmsId) throws MalBusinessException {
		
		String stmt = "select quotation_wrapper.get_unpaid_dlr_acc_cost_by_fms(?) from dual";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, fmsId);

		@SuppressWarnings("rawtypes")
		List results = query.getResultList();
        if (results.isEmpty()){
        	return null;
        }
        else{
        	return (BigDecimal) query.getSingleResult();
        }
	}

}
