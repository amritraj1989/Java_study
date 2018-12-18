package com.mikealbert.vision.common;

public class TestQueryConstants {
	public static final String UNIT_STATUS_CODE_DISPOSE_OF = "5";
	public static final String UNIT_DESIGNATION_CODE_STOCK = "STOCK";
	
	/*
	 * CREATE Statements
	 */
	
	/*
	 * READ Statements
	 */
	public static final String READ_PENDING_LIVE_UNIT_NO = 
			"SELECT fm.unit_no "
              + "FROM fleet_masters fm "
              + "    INNER JOIN contract_lines cl ON cl.fms_fms_id = fm.fms_id "
              + "WHERE cl.start_date > SYSDATE + 1 "
              + "    AND ROWNUM = 1";
		
	
	public static final String READ_DRIVER_ID_WITH_CURR_COST_CENTER = 
			"SELECT d.drv_id "
              + "FROM drivers d"
			  + "    INNER JOIN DRIVER_COST_CENTRES cs ON cs.drv_drv_id = d.drv_id "
              + "WHERE act_unt_man_drv.get_driver_cost_centre(d.drv_id).cost_centre_code IS NOT NULL "
			  + "    AND cs.cost_centre_code != 'NONE'"
              + "    AND d.active_ind = 'Y'"
              + "    AND ROWNUM = 1";
		
	
	public static final String READ_UNIT_NO_UNALLOCATED = 
			"SELECT drv_id "
             + " FROM drivers d "
             + " WHERE NOT EXISTS (SELECT drv_drv_id "
             + "                       FROM driver_allocations da "
             + "                       WHERE da.drv_drv_id = d.drv_id "
             + "                           AND (da.to_date IS NULL OR da.to_date >= SYSDATE)) " 
             + "    AND customer_start_date > to_date('2006', 'RRRR') "                              
             + "    AND rownum = 1";
		
		
	public static final String READ_DRIVER_ID_UNALLOCATED = 
			"SELECT drv_id "
             + " FROM drivers d "
             + " WHERE NOT EXISTS (SELECT drv_drv_id "
             + "                       FROM driver_allocations da "
             + "                       WHERE da.drv_drv_id = d.drv_id "
             + "                           AND (da.to_date IS NULL OR da.to_date >= SYSDATE)) " 
             + "    AND customer_start_date > to_date('2006', 'RRRR') "                              
             + "    AND rownum = 1";
	
	
	public static final String READ_DRIVER_ID_HAVING_VEHICLE_ON_ORDER =
			"SELECT quo.DRV_DRV_ID FROM quotations quo "
			+ " INNER JOIN quotation_models qmd ON quo.quo_id = qmd.quo_quo_id "
			+ " LEFT JOIN contract_lines cli ON qmd.qmd_id = cli.qmd_qmd_id "
			+ " Where Qmd.Quote_Status In (3) "
			+ " AND cli.in_serv_date IS NULL ";
	
	
	public static final String READ_MOST_RECENT_DIARY_DATE_FOR_DRV_ID = 
			"SELECT DIARY_DATE FROM DIARIES WHERE DRV_DRV_ID = ? order by DRY_ID DESC";
	

	public static final String READ_UNIT_NO_CURRENT_OR_FUTURE_ALLOCATION = 
			"SELECT fms.unit_no "
			+" FROM driver_allocations dal "
		    +" INNER JOIN fleet_masters fms ON fms.fms_id = dal.fms_fms_id "
			+" WHERE (dal.to_date IS NULL OR (dal.to_date > SYSDATE AND dal.from_date <= SYSDATE)) "
            +"     AND ROWNUM = 1 "
            +"     AND from_date < (sysdate - 365 * 3) " 
            +"     AND NOT EXISTS (SELECT 1 FROM driver_allocations dal2 WHERE dal2.from_date > sysdate and dal2.fms_fms_id = fms.fms_id) ";	
	
	
	public static final String READ_UNIT_NO_CURRENT_ALLOCATION = "SELECT UNIT_NO FROM ( " + 
			"		SELECT b.UNIT_NO FROM DRIVER_ALLOCATIONS a INNER JOIN FLEET_MASTERS b ON a.FMS_FMS_ID = b.FMS_ID WHERE TO_DATE IS NULL OR (TO_DATE > SYSDATE AND FROM_DATE <= SYSDATE) " +
			"		ORDER BY b.FMS_ID DESC " +
			"		) WHERE  ROWNUM = 1 ";
	
	
	public static final String READ_DRV_ID_CURRENT_ALLOCATION = "SELECT DRV_ID FROM ( " + 
			"		SELECT a.DRV_DRV_ID AS DRV_ID FROM DRIVER_ALLOCATIONS a INNER JOIN FLEET_MASTERS b ON a.FMS_FMS_ID = b.FMS_ID WHERE TO_DATE IS NULL OR (TO_DATE > SYSDATE AND FROM_DATE <= SYSDATE) " +
			"		ORDER BY b.FMS_ID DESC " +
			"		) WHERE  ROWNUM = 1 ";
	
	
	public static final String READ_FMS_ID_CURRENT_ALLOCATION = "SELECT FMS_ID FROM ( " + 
			"		SELECT b.FMS_ID FROM DRIVER_ALLOCATIONS a INNER JOIN FLEET_MASTERS b ON a.FMS_FMS_ID = b.FMS_ID WHERE TO_DATE IS NULL OR (TO_DATE > SYSDATE AND FROM_DATE <= SYSDATE) " +
			"		ORDER BY b.FMS_ID DESC " +
			"		) WHERE  ROWNUM = 1 ";
	
	
	public static final String READ_RES_ID = 
			"SELECT res.res_id "
			 +" FROM resources res "
			 +" WHERE ROWNUM = 1 "
			 +" ORDER BY res.resource_name ASC";
	
	//Obtains qmdid for unit on contract (within past 3 years)
	public static final String READ_QMD_ID_FOR_ON_CONTRACT = 
			"SELECT qm.qmd_id "
			+ " FROM contract_lines cl, quotation_models qm "
			+ " WHERE cl.qmd_qmd_id = qm.qmd_id "
			+ "  AND cl.start_date > sysdate - (365 * 3) "
			+ "  AND cl.in_serv_date IS NOT NULL "
            + "  AND cl.rev_no = 1 "
			+ "  AND ROWNUM = 1 ";

	
	//Obtains unit no for unit on contract (within past 3 years) that was ordered from standard quote
	public static final String READ_UNIT_NO_ON_CONTRACT_FROM_STD_ORDER = 
			"SELECT qm.unit_no "
			+ " FROM contract_lines cl, quotation_models qm "
			+ " WHERE cl.qmd_qmd_id = qm.qmd_id "
			+ "  AND cl.start_date > sysdate - (365 * 3) "
			+ "  AND cl.in_serv_date IS NOT NULL "
            + "  AND cl.rev_no = 1 "
			+ "  AND ROWNUM = 1 "
            + "  AND EXISTS (SELECT 1 "
            + "                FROM quotation_models qm2 " 
            + "                WHERE qm2.qmd_id = nvl(quotation_wrapper.getOriginalQuoteModelId(cl.qmd_qmd_id), 0) "
            + "                  AND qm2.quote_status in (15))";
	
	
	/*
	 * UPDATE Statements
	 */	
	public static final String UPDATE_COST_CENTER_MAKE_CURRENT = "UPDATE DRIVER_COST_CENTRES SET DATE_ALLOCATED = TRUNC(SYSDATE) WHERE EFFECTIVE_TO_DATE IS NULL AND DRV_DRV_ID = ? ";
	
	
	/*
	 * DELETE Statements
	 */	
}
