package com.mikealbert.data;


public class TestQueryConstants {
	public static final String UNIT_STATUS_CODE_DISPOSE_OF = "5";
	public static final String UNIT_DESIGNATION_CODE_STOCK = "STOCK";
	
	/*
	 * CREATE Statements
	 */
	
	/*
	 * READ Statements
	 */
	
	public static final String MAINT_REQUEST_OPEN_UNIT_NO = 
			"SELECT b.UNIT_NO FROM MAINTENANCE_REQUESTS a INNER JOIN FLEET_MASTERS b ON a.FMS_FMS_ID = b.FMS_ID WHERE MAINT_REQUEST_STATUS ='B' AND ROWNUM = 1";
            
	public static final String READ_RES_ID = 
			"SELECT res.res_id "
			 +" FROM resources res "
			 +" WHERE ROWNUM = 1 "
			 +" ORDER BY res.resource_name ASC";
	
	public static final String READ_PENDING_LIVE_UNIT_NO = 
			"SELECT fm.unit_no "
              + "FROM fleet_masters fm "
              + "    INNER JOIN contract_lines cl ON cl.fms_fms_id = fm.fms_id "
              + "WHERE (cl.start_date > SYSDATE + 1 "
              + "    or (cl.start_date is null and cl.in_serv_date is not null)) "
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
            +"     AND from_date > (sysdate - 365 * 3) " 
            +"     AND NOT EXISTS (SELECT 1 FROM driver_allocations dal2 WHERE dal2.from_date > sysdate and dal2.fms_fms_id = fms.fms_id) ";	
	
	
	public static final String READ_UNIT_NO_CURRENT_ALLOCATION = 
			"SELECT UNIT_NO "
	       +"     FROM (SELECT b.UNIT_NO FROM DRIVER_ALLOCATIONS a " 
		   +"               INNER JOIN FLEET_MASTERS b ON a.FMS_FMS_ID = b.FMS_ID " 
		   +"               WHERE (FROM_DATE <= SYSDATE - 2 * 365) AND (NVL(TO_DATE, SYSDATE) >= SYSDATE) "
		   +"               ORDER BY b.FMS_ID DESC) " 
           +"     WHERE  ROWNUM = 1 ";
	
	
	public static final String READ_DRV_ID_CURRENT_ALLOCATION = 
			"SELECT DRV_ID "
			+"    FROM (SELECT a.DRV_DRV_ID AS DRV_ID " 
			+"              FROM DRIVER_ALLOCATIONS a " 
            +"              INNER JOIN FLEET_MASTERS b ON a.FMS_FMS_ID = b.FMS_ID " 
            +"              WHERE (FROM_DATE <= SYSDATE - 2 * 365) AND (NVL(TO_DATE, SYSDATE) >= SYSDATE) "
		    +"              ORDER BY b.FMS_ID DESC ) "
            +"    WHERE  ROWNUM = 1 ";
	
	
	public static final String READ_FMS_ID_CURRENT_ALLOCATION = "SELECT FMS_ID FROM ( " + 
			"		SELECT b.FMS_ID FROM DRIVER_ALLOCATIONS a INNER JOIN FLEET_MASTERS b ON a.FMS_FMS_ID = b.FMS_ID WHERE (TO_DATE IS NULL OR TO_DATE > SYSDATE) AND FROM_DATE <= SYSDATE " +
			"		ORDER BY b.FMS_ID DESC " +
			"		) WHERE  ROWNUM = 1 ";
		
	
	//Obtains qmdid for unit on contract (within past 3 years)
	public static final String READ_QMD_ID_FOR_ON_CONTRACT_UNIT = 
			"SELECT qm.qmd_id "
			+ " FROM contract_lines cl, quotation_models qm "
			+ " WHERE cl.qmd_qmd_id = qm.qmd_id "
			+ "  AND cl.start_date > sysdate - (365 * 3) "
			+ "  AND cl.in_serv_date IS NOT NULL "
			+ "  AND cl.actual_end_date IS NULL "
            + "  AND cl.rev_no = 1 "
			+ "  AND ROWNUM = 1 ";
	
	//Randomly selects a maintenance PO
	public static final String READ_MAINTENANCE_PO_NUMBER = 
			"SELECT  job_no "
            + "   FROM (SELECT * "
            + "             FROM maintenance_requests sample(25)) "
            + "   WHERE orig_controller != 'CONV' "
            + "       AND maint_request_status = 'C' "
            + "       AND substr(job_no, 1, 1) = 'J' "
            + "       AND rownum = 1 ";
	
	//Randomly selects a service provider/invoice
    public static final String READ_MAINTENANCE_SERVICE_PROVIDER_INVOICE =
    		"SELECT account_code, doc_no"    
            +"    FROM doc sample(25)"
            +"    WHERE doc_type = 'INVOICEAP'"
            +"        AND update_control_code = 'FLMAINT'"
            +"        AND source_code = 'FLMAINT'"
            +"        AND doc_status IN ('O', 'P')"
            +"        AND op_code != 'CONV'"
            +"        AND  rownum = 1";
    
	//Randomly selects an internal (Mike Albert) invoice
    public static final String READ_MAINTENANCE_INTERNAL_INVOICE =
    		"SELECT doc_no"    
            +"    FROM doc sample(25)"
            +"    WHERE doc_type = 'INVOICEAR'"
            +"        AND update_control_code = 'FLMAINT'"
            +"        AND source_code = 'FLMAINT'"
            +"        AND doc_status IN ('O', 'P')"
            +"        AND op_code != 'CONV'"
            +"        AND  rownum = 1";
    
	//Randomly selects client accounts that have active units
    public static final String READ_CLIENT_ACCOUNT_WITH_ACTIVE_UNITS =
    		"SELECT account_code, account_name"
           +"    FROM (SELECT distinct account_code, account_name"
           +"              FROM external_accounts sample(25)"
           +"              WHERE account_type = 'C' AND c_id = 1) rea"
           +"    INNER JOIN contracts con ON con.ea_account_code = rea.account_code"
           +"    INNER JOIN contract_lines cln ON cln.con_con_id = con.con_id"
           +"    INNER JOIN driver_allocations dal ON dal.fms_fms_id = cln.fms_fms_id"
           +"    WHERE (dal.to_date IS NULL" 
           +"               OR  dal.to_date >= sysdate)"
           +"        AND rownum = 1";
    
    //Randomly selects a client's fleet reference number
    public static final String READ_CLIENT_FLEET_REFERENCE_NUMBER =
    		"SELECT vehicle_cost_centre"
           +"    FROM (SELECT *" 
           +"              FROM fleet_masters sample(25)" 
           +"              WHERE vehicle_cost_centre IS NOT NULL)"
           +"    WHERE rownum = 1";
    
    //Randomly selects a unit that has a replacement unit
    public static final String READ_UNIT_NO_WITH_REPLACEMENT_UNIT = 
    		"SELECT fms.unit_no, quo.c_id, quo.account_code, quo.account_type" 
           +"    FROM (SELECT qmd.quo_quo_id, qmd.replacement_for_fms_id"
           +"              FROM quotation_models sample(25) qmd"
           +"              WHERE qmd.vlo_printed_date < TRUNC(SYSDATE)"
           +"                  AND qmd.quote_status IN (3, 16, 17)"
           +"                  AND qmd.replacement_for_fms_id IS NOT NULL"
           +"                  AND fl_status.fleet_status(qmd.replacement_for_fms_id) = 2) qmd_sample"
           +"    INNER JOIN fleet_masters fms ON qmd_sample.replacement_for_fms_id = fms.fms_id"
           +"    INNER JOIN quotations quo ON qmd_sample.quo_quo_id = quo.quo_id"           
           +"  WHERE rownum = 1";
           
    //Randomly selects a maintenance request job no based on status    
    public static final String READ_MAINTENANCE_REQUEST_JOB_NO = 
    		"SELECT job_no"
           +"    FROM (SELECT *" 
           +"              FROM maintenance_requests sample(20)"
           +"              WHERE maint_request_status = ?"
           +"                  AND substr(job_no, 1, 1) = 'J'"
           +"                  AND length(job_no) > 8"
           +"                  AND job_no != 'J00217329'"
           +"                  AND MAINT_REQ_DATE < SYSDATE)" 
           +"    WHERE ROWNUM = 1";
    
    //Randomly selects a client point id
    public static final String READ_CLIENT_POINT_ID = 
    		"SELECT cpnt_id "
           +"    FROM (SELECT cpnt_id from CLIENT_POINTS sample(75) )"
           +"    WHERE ROWNUM = 1";    
    
    //Randomly selects a client point account id
    public static final String READ_CLIENT_POINT_ACCOUNT_ID = 
    		"SELECT cpnta_id "
           +"    FROM (SELECT cpnta_id FROM client_point_accounts sample(75) )"
           +"    WHERE ROWNUM = 1";  
    
    //Randomly selects a client contact that is assigned to a cost center POC
    public static final String READ_CLIENT_CONTACT_AS_COST_CENTER_DRIVER = 
    		"SELECT * "
           +"    FROM (SELECT * FROM client_contacts sample(50) WHERE drv_ind = 'Y' AND cost_centre_code IS NOT NULL)"
           +"    WHERE ROWNUM = 1"; 
    
    //Randomly selects a client contact that is directly assigned to a POC
    public static final String READ_CLIENT_CONTACT_AS_POC_DRIVER = 
    		"SELECT * "
           +"    FROM (SELECT * FROM client_contacts sample(50) WHERE drv_ind = 'Y' AND cost_centre_code IS NULL)"
           +"    WHERE ROWNUM = 1";      
    
    //Selects a client's POC that has a cost center
    public static final String READ_CLIENT_POINT_ACCOUNT_WITH_COST_CENTER =    
    		"SELECT DISTINCT cpnta.* "
    	   +"    FROM client_point_accounts cpnta, cost_centre_codes ccc"
    	   +"    WHERE cpnta.c_id = ccc.ea_c_id"
    	   +"        AND cpnta.account_type = ccc.ea_account_type"
    	   +"        AND cpnta.account_code = ccc.ea_account_code"
    	   +"        AND ROWNUM = 1";
    
    //Selects a fleet master that is on contract along with its client account information
    //The contract in service data will be one year prior to the current date
    //this is to minimize the risk of pulling in development data
    public static final String READ_FLEET_MASTER_AND_ACCOUNT_ON_CONTRACT =
    		"SELECT fms.fms_id, ea.c_id, ea.account_type, ea.account_code "
            + "   FROM fleet_masters fms " 
            + "       INNER JOIN contract_lines cln ON cln.fms_fms_id = fms.fms_id "
            + "       INNER JOIN contracts con ON con.con_id = cln.con_con_id "
            + "       INNER JOIN external_accounts ea ON ea.c_id = con.ea_c_id AND ea.account_type = con.ea_account_type AND ea.account_code = con.ea_account_code "
            + "   WHERE cln.in_serv_date BETWEEN SYSDATE - 365 * 2 AND SYSDATE - 365 "
            + "       AND cln.start_date <= cln.in_serv_date "
            + "       AND cln.actual_end_date is null "
            + "       AND ROWNUM = 1";
    
	public static final String READ_ACCOUNT_WITH_NO_POC =
			"SELECT ea.account_code "
            + "    FROM external_accounts sample(75) ea "
            + "    WHERE NOT EXISTS (SELECT 1 " 
            + "                          FROM client_point_accounts " 
            + "                          WHERE c_id = ea.c_id "
            + "                              AND account_type = ea.account_type "
            + "                              AND account_code = ea.account_code) "
            + "        AND ea.c_id = 1 "
            + "        AND ea.account_type = 'C' "
            + "        AND ROWNUM = 1";
	
	public static final String READ_UPFITTER_QUOTE_ID =
			"SELECT ufq.ufq_id "
            + "    FROM upfitter_quotes sample(75) ufq "
            + "    WHERE ROWNUM = 1 ";	
	
	public static final String READ_UPFITTER_QUOTE_BY_CLIENT_QUOTE_REFERENCE =
			"SELECT ufq.ufq_id "
            + "    FROM upfitter_quotes ufq "
			+ "    INNER JOIN quotation_dealer_accessories qda ON ufq.quote_number = qda.external_reference_no "
            + "    WHERE ROWNUM = 1 ";	
	
	public static final String READ_OBJECT_LOG_BOOK_FROM_OBJECT_WITH_MANY_LOG_BOOKS =
			"SELECT olb.olb_id "
            + "    FROM object_log_books sample(50) olb  "
			+ "    WHERE EXISTS ( SELECT 1  "
			+ "                       FROM object_log_books olb2 "
			+ "                       WHERE olb2.olb_id <> olb.olb_id  "
			+ "                           AND olb2.object_id = olb.object_id "
			+ "                           AND olb2.object_name = olb.object_name) "			
            + "    AND ROWNUM = 1 ";	

	public static final String READ_UPFIT_LOG_BOOK_WITH_FOLLOW_UP_DATE =
			"SELECT olb.object_id "
            + "    FROM vision.object_log_books sample(50) olb "
            + "        WHERE EXISTS ( SELECT 1 "
            + "                           FROM vision.log_book_entries lbe "
            + "                               WHERE lbe.olb_olb_id = olb.olb_id "
            + "                                   AND lbe.follow_up_date IS NOT NULL ) "
            + "            AND EXISTS ( SELECT 1 "
            + "                            FROM vision.log_books lbk "
            + "                            WHERE lbk.lbk_id = olb.lbk_lbk_id "
            + "                                AND lbk.type = 'UPFIT_PRG_NOTES' )"            
            + "            AND ROWNUM = 1";
	/*
	 * UPDATE Statements
	 */	
	public static final String UPDATE_COST_CENTER_MAKE_CURRENT = "UPDATE DRIVER_COST_CENTRES SET DATE_ALLOCATED = TRUNC(SYSDATE) WHERE EFFECTIVE_TO_DATE IS NULL AND DRV_DRV_ID = ? ";
	
	
	/*
	 * DELETE Statements
	 */	
}
