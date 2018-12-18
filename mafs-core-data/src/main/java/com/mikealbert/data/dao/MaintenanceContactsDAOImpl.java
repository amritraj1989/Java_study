package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.vo.MaintenanceContactsVO;

public class MaintenanceContactsDAOImpl extends GenericDAOImpl<Contact, Long> implements MaintenanceContactsDAOCustom {

	/**
	 * Find maintenance contacts per account
	 * There is no direct link from contact to contact_numbers without going through contact_addresses.
	 * (Same issue with driver / phone numbers until changes were implemented)    
	 * Need to discuss whether the same type of effort will be done for contact/phone numbers.
	 * Until then, using a DAOImpl to retrieve the data.  Seemed to be the easiest wrong way possible!
	 */
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")	
	public List<MaintenanceContactsVO> getMaintenanceContactsByAccount(Long cId, String accountType, String accountCode) {
		List<MaintenanceContactsVO> maintenanceContactsList	= new ArrayList<MaintenanceContactsVO>();
		Query query = generateMaintenanceContactsByAccountDataQuery(cId, accountType, accountCode);
		
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				MaintenanceContactsVO maintenanceContactsVO = new MaintenanceContactsVO();		
				BigDecimal dec = (BigDecimal)record[i];
				maintenanceContactsVO.setContactId(dec.longValue());
				maintenanceContactsVO.setContactType((String)record[i+=1]);
				maintenanceContactsVO.setFirstName((String)record[i+=1]);
				maintenanceContactsVO.setLastName((String)record[i+=1]);
				maintenanceContactsVO.setEmail((String)record[i+=1]);
				maintenanceContactsVO.setCellAreaCode((String)record[i+=1]);
				maintenanceContactsVO.setCellNumber((String)record[i+=1]);
				maintenanceContactsVO.setCellExtension((String)record[i+=1]);
				maintenanceContactsVO.setCellCncCode((String)record[i+=1]);
				maintenanceContactsVO.setWorkAreaCode((String)record[i+=1]);
				maintenanceContactsVO.setWorkNumber((String)record[i+=1]);
				maintenanceContactsVO.setWorkExtension((String)record[i+=1]);
				maintenanceContactsVO.setWorkCncCode((String)record[i+=1]);				
				
				maintenanceContactsList.add(maintenanceContactsVO);
			}				
		}

		return maintenanceContactsList;
	}

	private Query generateMaintenanceContactsByAccountDataQuery(Long cId, String accountType, String accountCode) {
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT " +
					   " c.cnt_id, c.contact_type, c.first_name, c.last_name, c.e_mail," +
					   " cell.contact_area_code cell_area_code," + 
					   " cell.contact_number cell_contact_number," + 
					   " cell.contact_extension_number cell_extension_number," + 
					   " cell.cnc_code cell_cnc_code," +
					   " work.contact_area_code work_area_code," + 
					   " work.contact_number work_contact_number," + 
					   " work.contact_extension_number work_extension_number," + 
					   " work.cnc_code work_cnc_code" +
					   " FROM contacts c," + 
						     " (SELECT" +
						        " ca.cnt_cnt_id, contact_area_code, contact_number, contact_extension_number, cnc_code" +
						        " FROM contact_addresses ca, contact_numbers cn" +
						        " WHERE cn.cad_cad_id = ca.cad_id" +
						        " AND cn.cnr_id = (SELECT MAX(cn2.cnr_id)" +
						                           " FROM contact_addresses ca2, contact_numbers cn2" +
						                           " WHERE ca2.cnt_cnt_id = ca.cnt_cnt_id" +
						                           " AND cn2.cad_cad_id = ca2.cad_id" + 
						                           " AND cnc_code = 'WORK')) work," +
						     " (SELECT" +
						        " ca.cnt_cnt_id, contact_area_code, contact_number, contact_extension_number, cnc_code" +
						        " FROM contact_addresses ca, contact_numbers cn" +
						        " WHERE cn.cad_cad_id = ca.cad_id" +
						        " AND cn.cnr_id = (SELECT MAX(cn2.cnr_id)" +
						                           " FROM contact_addresses ca2, contact_numbers cn2" +
						                           " WHERE ca2.cnt_cnt_id = ca.cnt_cnt_id" +
						                           " AND cn2.cad_cad_id = ca2.cad_id" + 
						                           " AND cnc_code = 'CELL')) cell" +     
						" WHERE c.c_id = :cId" + 
						" AND c.account_type = :accountType" + 
						" AND c.account_code = :accountCode" +
						" AND upper(c.contact_type) LIKE '%MAINT%'" +
						" AND work.cnt_cnt_id(+) = c.cnt_id" +
						" AND cell.cnt_cnt_id(+) = c.cnt_id" +
						" ORDER BY c.contact_type ASC, c.last_name ASC");
				
		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		
		return query;
	}


}
