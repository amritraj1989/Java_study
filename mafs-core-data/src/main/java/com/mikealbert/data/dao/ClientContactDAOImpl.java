package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.data.entity.ClientPointAccount;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.data.vo.ReportContactVO;
import com.mikealbert.util.MALUtilities;

public class ClientContactDAOImpl extends GenericDAOImpl<Contact, Long> implements ClientContactDAOCustom {
	@PersistenceContext protected EntityManager entityManager;
	@Resource
	private ClientContactDAO clientContactDAO;
	private static final long serialVersionUID = 1140707781964529298L;	
	private static final String CONTACT_TYPE_CC_MGR = "CC MGR";
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
    
	/**
	 * Retrieves a list of assignable and assigned contacts (excluding driver)
	 * @param ClientPointAccount The Client Point Account, i.e. the Client's assigned POC
	 * @param Pageable Pageable for paging the data set
	 * @param Sort Sort for sorting the data set. Not really needed as sorting can be done via the Pageable
	 */
	public List<ClientContactVO> getContactVOsByPOC(ClientPointAccount poc, CostCentreCode costCenter, Pageable pageable, Sort sort){
		List<ClientContactVO> clientContactVOs = null;
		Query query = null;

		query = generateContactVOsByAccountQuery(poc, costCenter, sort, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}		
		
		@SuppressWarnings("unchecked")
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			clientContactVOs = new ArrayList<ClientContactVO>();
			
			for(Object[] record : resultList){
				int i = 0;
				
				ClientContactVO cc = new ClientContactVO();
				cc.setContactId(((BigDecimal)record[i]).longValue());
				cc.setDefaultIndicator(MALUtilities.convertYNToBoolean((String)record[i+=1]));
				cc.setContactType((String)record[i+=1]);
				cc.setJobTitle((String)record[i+=1]);
				cc.setFirstName((String)record[i+=1]);
				cc.setLastName((String)record[i+=1]);
				cc.setCostCenterCode((String)record[i+=1]);				
				cc.setClientContactId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				cc.setParentAccountContact(MALUtilities.convertYNToBoolean((String)record[i+=1]));
				cc.setParentAccountCid(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				cc.setParentAccountType((String)record[i+=1]);
				cc.setParentAccountCode((String)record[i+=1]);
				cc.setParentAccountName((String)record[i+=1]);
				cc.setAssigned(MALUtilities.convertYNToBoolean((String)record[i+=1]));
				cc.setContactMethodMail(MALUtilities.convertYNToBoolean((String)record[i+=1]));	
				cc.setContactMethodPhone(MALUtilities.convertYNToBoolean((String)record[i+=1]));
				cc.setContactMethodEmail(MALUtilities.convertYNToBoolean((String)record[i+=1]));
				cc.setAddressAvaialble(MALUtilities.convertYNToBoolean((String)record[i+=1]));
				cc.setPhoneAvailable(MALUtilities.convertYNToBoolean((String)record[i+=1]));
				cc.setEmailAvailable(MALUtilities.convertYNToBoolean((String)record[i+=1]));				
				cc.setAddressLine1((String)record[i+=1]);
				cc.setAddressLine2((String)record[i+=1]);
				cc.setGrdAddressLine1((String)record[i+=1]);
				cc.setGrdAddressLine2((String)record[i+=1]);
				clientContactVOs.add(cc);
			}
		}
	
		
		return clientContactVOs;
	}
	
	public int getContactVOsByPOCCount(ClientPointAccount pointOfCommunication, CostCentreCode costCenter){
		int count = 0;
		Query query = null;
		query = generateContactVOsByAccountQuery(pointOfCommunication, costCenter, null, true);
		count = ((BigDecimal)query.getSingleResult()).intValue();
		return count;
	}
	
	/**
	 * OTD-5264 Creating this method to minimize the risk of altering the way fleet maintenance module
	 * utilizes the method to obtains the list in a certain sort order. The query that sorts on contact type 
	 * was implemented via Bugzilla Bug 16420. The implementation broke future uses such as OTD-5264.
	 * The second query is the original one, i.e. the query prior to BZ 16420 changes. 
	 * @param sortOnContactType Flag used to indicate to sort by contact type or not.
	 * @return Query
	 */
	private Query generateContactVOsByAccountPOCQuery(boolean orderByContactType) {
		StringBuilder sqlStmt;		
		Query query = null;
		sqlStmt = new StringBuilder("");
		
		if(orderByContactType){
			sqlStmt.append("SELECT  cnt_id, drv_id, first_name, last_name, job_title, "
					+ "             email, work_area_code, work_number, work_number_extension, cell_area_code, "
					+ "             cell_number, street_no, address_line_1, address_line_2, address_line_3, "
					+ "             address_line_4, business_address_line, postcode, town_city, region, "
					+ "             county_name, country, method_used, cost_centre_code, dao.contact_type, "
					+"              home_area_code, home_number, home_number_extension, preferred_contact_number "
					+ "         FROM TABLE(vision.client_contact_wrapper.getContacts(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)) dao,"
					+ "			contact_type_Codes ctc where dao.contact_type = ctc.contact_type"
					+ "			order by sort_order asc,last_name,first_name");/*Added Sorting in Query for Bug 16420*/			
		} else {
			sqlStmt.append("SELECT  cnt_id, drv_id, first_name, last_name, job_title, "
					+ "             email, work_area_code, work_number, work_number_extension, cell_area_code, "
					+ "             cell_number, street_no, address_line_1, address_line_2, address_line_3, "
					+ "             address_line_4, business_address_line, postcode, town_city, region, "
					+ "             county_name, country, method_used, cost_centre_code, dao.contact_type, "
					+ "             home_area_code, home_number, home_number_extension, preferred_contact_number "
					+ "         FROM TABLE(vision.client_contact_wrapper.getContacts(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)) dao");			
		}
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		
		return query;
	}
		
	/**
	 * Retrieves a list of contact (include driver) based on the fleet master (optional),
	 * account, POC, driver's cost center (optional). The fleet master and 
	 * driver's cost center is needed in order to retrieve an assigned driver's contact info.
	 * @param fleetMaster Fleet Master (Optional) 
	 * @param clientAccount The client's account
	 * @param driverCostCenter Driver cost center (Optional)
	 * @param pocName The name of the Point of Communication
	 * @param system The name of the system the Point of Communication is assigned to
	 * @param Flag used as an indicator to determine whether to sort on contact type
	 * @return ClientContactVO List of VOs representing the client'contacts
	 * @throws Exception
	 */
	public List<ClientContactVO> getContactVOsByAccountPOC(FleetMaster fleetMaster, ExternalAccount clientAccount, CostCentreCode costCenter, String pocName, String system, boolean orderByContactType) throws Exception{
		ClientContactVO clientContactVO;		
		List<ClientContactVO> clientContactVOs;		
		Query query;
		
		query = generateContactVOsByAccountPOCQuery(orderByContactType);		
	
		if(MALUtilities.isEmpty(fleetMaster)){
			query.setParameter(1, "");				
		} else {
			query.setParameter(1, fleetMaster.getFmsId());	
		}
		
		query.setParameter(2, clientAccount.getExternalAccountPK().getAccountCode());
		query.setParameter(3, clientAccount.getExternalAccountPK().getAccountType());
		query.setParameter(4, clientAccount.getExternalAccountPK().getCId());
				
		if(MALUtilities.isEmpty(costCenter)){
			query.setParameter(5, "");
			query.setParameter(6, "");
			query.setParameter(7, "");
			query.setParameter(8, "");			
		} else {
			query.setParameter(5, costCenter.getCostCentreCodesPK().getCostCentreCode());
			query.setParameter(6, costCenter.getCostCentreCodesPK().getEaCId());
			query.setParameter(7, costCenter.getCostCentreCodesPK().getEaAccountType());
			query.setParameter(8, costCenter.getCostCentreCodesPK().getEaAccountCode());			
		}
		
		query.setParameter(9, pocName);
		query.setParameter(10, system);				
		
		clientContactVOs = new ArrayList<ClientContactVO>();
		
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				clientContactVO = new ClientContactVO();
				clientContactVO.setContactId(record[i] != null ? ((BigDecimal)record[i]).longValue() : null);				
				clientContactVO.setDriverId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);				
				//clientContactVO.setContactType((String)record[i+=1]);				
				clientContactVO.setFirstName((String)record[i+=1]);
				clientContactVO.setLastName((String)record[i+=1]);	
				clientContactVO.setJobTitle((String)record[i+=1]);
				clientContactVO.setEmail((String)record[i+=1]);
				clientContactVO.setWorkAreaCode((String)record[i+=1]);
				clientContactVO.setWorkNumber((String)record[i+=1]);
				clientContactVO.setWorkNumberExtension((String)record[i+=1]);
				clientContactVO.setCellAreaCode((String)record[i+=1]);
				clientContactVO.setCellNumber((String)record[i+=1]);
				clientContactVO.setStreetNo((String)record[i+=1]);
				clientContactVO.setAddressLine1((String)record[i+=1]);
				clientContactVO.setAddressLine2((String)record[i+=1]);
				clientContactVO.setAddressLine3((String)record[i+=1]);
				clientContactVO.setAddressLine4((String)record[i+=1]);				
				clientContactVO.setBusinessAddressLine((String)record[i+=1]);
				clientContactVO.setPostCode((String)record[i+=1]);	
				clientContactVO.setCity((String)record[i+=1]);	
				clientContactVO.setState((String)record[i+=1]);	
				clientContactVO.setCounty((String)record[i+=1]);
				clientContactVO.setCountry((String)record[i+=1]);	
				clientContactVO.setMethodToUse((String)record[i+=1]);
				clientContactVO.setCostCenterCode((String)record[i+=1]);	
				clientContactVO.setContactType((String)record[i+=1]);	
				clientContactVO.setHomeAreaCode((String)record[i+=1]);
				clientContactVO.setHomeNumber((String)record[i+=1]);
				clientContactVO.setHomeNumberExtension((String)record[i+=1]);
				clientContactVO.setPreferredNumber((String)record[i+=1]);
				clientContactVOs.add(clientContactVO);
			}

		}
		
		return clientContactVOs;
	}	
	
	public ClientContactVO getContactVOByClientPOC(ClientPointAccount clientPOC, Contact contact){
		ClientContactVO clientContactVO;		
		StringBuilder sqlStmt;		
		Query query;
		Object[] record;
		
		try {
			sqlStmt = new StringBuilder("");
			sqlStmt.append("SELECT  cnt_id, drv_id, first_name, last_name, job_title, "
					+ "             email, work_area_code, work_number, work_number_extension, cell_area_code, "
					+ "             cell_number, street_no, address_line_1, address_line_2, address_line_3, "
					+ "             address_line_4, business_address_line, postcode, town_city, region, "
					+ "             county_name, country, method_used, cost_centre_code, contact_type "
					+ "         FROM TABLE(vision.client_contact_wrapper.getContact(?, ?))");

			query = entityManager.createNativeQuery(sqlStmt.toString());		
			query.setParameter(1, clientPOC.getClientPointAccountId());				
			query.setParameter(2, contact.getContactId());
			
			List<Object[]>resultList = (List<Object[]>)query.getResultList();
			if(resultList != null){
				int i = 0;
				record = resultList.get(0);
				clientContactVO = new ClientContactVO();
				clientContactVO.setContactId(record[i] != null ? ((BigDecimal)record[i]).longValue() : null);	
				clientContactVO.setDriverId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);	
				clientContactVO.setFirstName((String)record[i+=1]);
				clientContactVO.setLastName((String)record[i+=1]);
				clientContactVO.setJobTitle((String)record[i+=1]);
				clientContactVO.setEmail((String)record[i+=1]);
				clientContactVO.setWorkAreaCode((String)record[i+=1]);
				clientContactVO.setWorkNumber((String)record[i+=1]);
				clientContactVO.setWorkNumberExtension((String)record[i+=1]);
				clientContactVO.setCellAreaCode((String)record[i+=1]);
				clientContactVO.setCellNumber((String)record[i+=1]);							
				clientContactVO.setStreetNo((String)record[i+=1]);
				clientContactVO.setAddressLine1((String)record[i+=1]);
				clientContactVO.setAddressLine2((String)record[i+=1]);
				clientContactVO.setAddressLine3((String)record[i+=1]);
				clientContactVO.setAddressLine4((String)record[i+=1]);				
				clientContactVO.setBusinessAddressLine((String)record[i+=1]);
				clientContactVO.setPostCode((String)record[i+=1]);	
				clientContactVO.setCity((String)record[i+=1]);	
				clientContactVO.setState((String)record[i+=1]);
				clientContactVO.setCounty((String)record[i+=1]);
				clientContactVO.setCountry((String)record[i+=1]);
				clientContactVO.setMethodToUse((String)record[i+=1]);					
				clientContactVO.setCostCenterCode((String)record[i+=1]);	
				clientContactVO.setContactType((String)record[i+=1]);				
			} else {
				clientContactVO = null;
			}
		} catch (Exception e) {
			clientContactVO = null;
		}
		
		
		return clientContactVO;
	}
	
	@Override
	public List<ReportContactVO> findReportEmailContacts(ReportNameEnum reportName, Long cId, String accountType, String accountCode,
			Long fmsId, Long qmdId) {
		String sql;
		Query query;
		List<ReportContactVO> reportContacts = new ArrayList<ReportContactVO>();		

		logger.info("reportName.getModuleName() = " + reportName.getModuleName() + ", cId=" + cId + ", accountType=" + accountType + ", accountCode=" + accountCode + ", fmsId=" + fmsId + ", qmdId=" + qmdId);
		
		sql = "SELECT * from table(vision.report_contact_wrapper.getEmailReportContacts(:reportName, :cId, :accountType, :accountCode, :callFrom, :fmsId, :qmdId))";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("reportName", reportName.getModuleName());
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);
		query.setParameter("callFrom", "MAL_EMAIL");
		query.setParameter("fmsId", fmsId);
		query.setParameter("qmdId", qmdId);

		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				
				ReportContactVO reportContact = new ReportContactVO();
				reportContact.setCntId(MALUtilities.isEmpty(record[i]) ? null : ((BigDecimal)record[i]).longValue());
				reportContact.setDrvId(MALUtilities.isEmpty(record[i+=1]) ? null : ((BigDecimal)record[i]).longValue());
				reportContact.setFirstName((String)record[i+=1]);
				reportContact.setLastName((String)record[i+=1]);
				reportContact.setContactType((String)record[i+=1]);
				reportContact.setCorporateContactType((String)record[i+=1]);
				reportContact.setDeliveryMethod((String)record[i+=1]);
				reportContact.setEmailAddres((String)record[i+=1]);
				reportContact.setFaxNumber((String)record[i+=1]);	
				
				reportContacts.add(reportContact);								
			}
		}

		return reportContacts;
	}
	
	@Override
	public String getReportEmailSubject(ReportNameEnum reportName) {
		String emailSubject;
		String sql;
		Query query;

		sql = "SELECT vision.report_contact_wrapper.getEmailSubject(:reportName) FROM DUAL";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("reportName", reportName.getModuleName());

		emailSubject = (String) query.getSingleResult();

		return emailSubject;
	}

	@Override
	public String getReportEmailBody(ReportNameEnum reportName) {
		String emailBody;
		String sql;
		Query query;

		sql = "SELECT vision.report_contact_wrapper.getEmailBody(:reportName) FROM DUAL";

		query = entityManager.createNativeQuery(sql);
		query.setParameter("reportName", reportName.getModuleName());

		emailBody = (String) query.getSingleResult();

		return emailBody;
	}
	
	private Query generateContactVOsByAccountQuery(ClientPointAccount poc, CostCentreCode costCenter, Sort sort, boolean isCountQuery) {
		StringBuilder sqlStmt;		
		Query query = null;
		
		sqlStmt = new StringBuilder("");
		if(isCountQuery){
			sqlStmt.append("select count(cnt.cnt_id) ");
		} else {
			sqlStmt.append("select cnt.cnt_id, cnt.default_ind, cnt.contact_type, cnt.job_title, cnt.first_name, cnt.last_name, ccc.cost_centre_code, "
					+ "             (select ccon2.ccon_id "
					+ "                  from client_contacts ccon2 "
					+ "                  where ccon2.cnt_cnt_id = cnt.cnt_id "
					+ "                      and ccon2.cpnta_cpnta_id = cpnta.cpnta_id " 
					+ "                      and nvl(ccon2.cost_centre_code, '-1') like :costCenterCode " 
					+ "                      and nvl(ccon2.cc_c_id, -1) like :cId " 
					+ "                      and nvl(ccon2.cc_account_type, '-1') like :accountType "
					+ "                      and nvl(ccon2.cc_account_code, '-1') like :accountCode ) as \"CCON_ID\", "
					+ "             decode(cnt.account_code, cpnta.account_code, 'N', 'Y') AS \"PARENT_ACCOUNT_CONTACT\", "
					+ "             (select ea2.parent_account_entity " 
                    + "                 from external_accounts ea2 "
                    + "                 where ea2.c_id = cnt.c_id "
                    + "                     and ea2.account_type = cnt.account_type "
                    + "                     and ea2.account_code = cnt.account_code) AS \"PARENT_ACCOUNT_C_ID\", "
                    + "             (select ea2.parent_account_type " 
                    + "                 from external_accounts ea2 " 
                    + "                 where ea2.c_id = cnt.c_id "
                    + "                     and ea2.account_type = cnt.account_type "
                    + "                     and ea2.account_code = cnt.account_code) AS \"PARENT_ACCOUNT_TYPE\", "
                    + "             (select ea2.parent_account "
                    + "                 from external_accounts ea2 " 
                    + "                 where ea2.c_id = cnt.c_id "
                    + "                     and ea2.account_type = cnt.account_type "
                    + "                     and ea2.account_code = cnt.account_code) AS \"PARENT_ACCOUNT_CODE\", "
                    + "             (select (select ea3.account_name " 
                    + "                          from external_accounts ea3 "
                    + "                          where ea3.c_id = ea2.parent_account_entity "
                    + "                              and ea3.account_type = ea2.parent_account_type "
                    + "                              and ea3.account_code = ea2.parent_account) "
                    + "                 from external_accounts ea2 " 
                    + "                 where ea2.c_id = cnt.c_id "
                    + "                     and ea2.account_type = cnt.account_type "
                    + "                     and ea2.account_code = cnt.account_code) AS \"PARENT_ACCOUNT_NAME\", "
					+ "             decode((select 1 " 
					+ "                         from client_contacts ccon "
					+ "                         where ccon.cnt_cnt_id = cnt.cnt_id "
					+ "                             and ccon.cpnta_cpnta_id = cpnta.cpnta_id "
					+ "                             and nvl(ccon.cost_centre_code, '-1') like :costCenterCode " 
					+ "                             and nvl(ccon.cc_c_id, -1) like :cId " 
					+ "                             and nvl(ccon.cc_account_type, '-1') like :accountType "
					+ "                             and nvl(ccon.cc_account_code, '-1') like :accountCode "					
					+ "                             and rownum = 1), 1, 'Y', 'N') AS \"ASSIGNED\", " 
					+ "             decode((select 1 " 
					+ "                         from client_contacts ccon, "
					+ "                              client_contact_methods cccm, "
					+ "                              client_methods cmet "
					+ "                         where ccon.cnt_cnt_id = cnt.cnt_id "
					+ "                             and ccon.cpnta_cpnta_id = cpnta.cpnta_id "
					+ "                             and nvl(ccon.cost_centre_code, '-1') like :costCenterCode " 
					+ "                             and nvl(ccon.cc_c_id, -1) like :cId " 
					+ "                             and nvl(ccon.cc_account_type, '-1') like :accountType "
					+ "                             and nvl(ccon.cc_account_code, '-1') like :accountCode "						
					+ "                             and cccm.ccon_ccon_id = ccon.ccon_id "
					+ "                             and cmet.cmet_id = cccm.cmet_cmet_id "
					+ "                             and cmet.method_name = 'MAIL' "
					+ "                             and rownum = 1), 1, 'Y', 'N') AS \"CONTACT_METHOD_MAIL_\", "                        
					+ "             decode((select 1 " 
					+ "                         from client_contacts ccon, "
					+ "                              client_contact_methods cccm, "
					+ "                              client_methods cmet "
					+ "                         where ccon.cnt_cnt_id = cnt.cnt_id "
					+ "                             and ccon.cpnta_cpnta_id = cpnta.cpnta_id " 
					+ "                             and nvl(ccon.cost_centre_code, '-1') like :costCenterCode " 
					+ "                             and nvl(ccon.cc_c_id, -1) like :cId " 
					+ "                             and nvl(ccon.cc_account_type, '-1') like :accountType "
					+ "                             and nvl(ccon.cc_account_code, '-1') like :accountCode "						
					+ "                             and cccm.ccon_ccon_id = ccon.ccon_id "
					+ "                             and cmet.cmet_id = cccm.cmet_cmet_id "
					+ "                             and cmet.method_name = 'PHONE' "
					+ "                             and rownum = 1), 1, 'Y', 'N') AS \"CONTACT_METHOD_PHONE\", "
					+ "             decode((select 1 " 
					+ "                         from client_contacts ccon, "
					+ "                              client_contact_methods cccm, "
					+ "                              client_methods cmet "
					+ "                         where ccon.cnt_cnt_id = cnt.cnt_id "
					+ "                             and ccon.cpnta_cpnta_id = cpnta.cpnta_id " 
					+ "                             and nvl(ccon.cost_centre_code, '-1') like :costCenterCode " 
					+ "                             and nvl(ccon.cc_c_id, -1) like :cId " 
					+ "                             and nvl(ccon.cc_account_type, '-1') like :accountType "
					+ "                             and nvl(ccon.cc_account_code, '-1') like :accountCode "						
					+ "                             and cccm.ccon_ccon_id = ccon.ccon_id "
					+ "                             and cmet.cmet_id = cccm.cmet_cmet_id "
					+ "                             and cmet.method_name = 'EMAIL' "
					+ "                             and rownum = 1), 1, 'Y', 'N') AS \"CONTACT_METHOD_EMAIL\", "
					+ "             decode((select count(1) "
					+ "                         from contact_addresses cad "
					+ "                         where cad.cnt_cnt_id = cnt.cnt_id "
					+ "                             and cad.address_type in ('POST', 'GARAGED') "
					+ "                             and rownum = 1), 0, 'N', 'Y') AS \"ADDRESS_AVAILABLE\", "
					+ "              decode((select count(cnr.cnr_id) "
					+ "                         from contact_addresses cad, contact_numbers cnr "
					+ "                         where cad.cnt_cnt_id = cnt.cnt_id "
					+ "                             and cnr.cad_cad_id = cad.cad_id "
					+ "                             and rownum = 1), 0, 'N', 'Y') AS \"PHONE_AVAILABLE\", "
					+ "              decode(cnt.e_mail, null, 'N', 'Y') AS \"EMAIL_AVAILABLE\", "
					+ "              (select cad.address_line_1 from contact_addresses cad where cnt.cnt_id = cad.cnt_cnt_id and cad.address_type = 'POST') AS \"ADDRESS_LINE_1\", "
					+ "              (select cad.address_line_2 from contact_addresses cad where cnt.cnt_id = cad.cnt_cnt_id and cad.address_type = 'POST') AS \"ADDRESS_LINE_2\", "					
					+ "              (select cad.address_line_1 from contact_addresses cad where cnt.cnt_id = cad.cnt_cnt_id and cad.address_type = 'GARAGED') AS \"GR_ADDRESS_LINE_1\", "
					+ "              (select cad.address_line_2 from contact_addresses cad where cnt.cnt_id = cad.cnt_cnt_id and cad.address_type = 'GARAGED') AS \"GR_ADDRESS_LINE_2\" ");
		}
		
		sqlStmt.append("    FROM contacts cnt "
				+ "             INNER JOIN external_accounts ea ON (cnt.c_id = ea.c_id AND cnt.account_type = ea.account_type AND cnt.account_code = ea.account_code) " 
                + "                 OR (cnt.c_id = ea.parent_account_entity AND cnt.account_type = ea.parent_account_type AND cnt.account_code = ea.parent_account) "        
                + "             INNER JOIN client_point_accounts cpnta ON cpnta.c_id = ea.c_id AND cpnta.account_type = ea.account_type AND cpnta.account_code = ea.account_code "
                + "             LEFT JOIN web_user wu ON cnt.cnt_id = wu.cnt_id "
                + "             LEFT JOIN cost_centre_codes ccc ON wu.ea_account_code = ccc.ea_account_code AND wu.cost_centre_code = ccc.cost_centre_code "
                + "         WHERE cpnta.cpnta_id = :cpntaId "
                + "             AND (cnt.contact_type != :contactType "
                + "                     OR ccc.cost_centre_code || ccc.ea_account_code IN ( "
                + "                             SELECT ccc1.cost_centre_code || ccc1.ea_account_code "
                + "                                 FROM cost_centre_codes ccc1 "
                + "                                 WHERE ccc1.ea_account_code = cpnta.account_code "
                + "                                 START WITH ccc1.cost_centre_code = :costCenterCode "
                + "                                     AND ccc.ea_c_id = cpnta.c_id "
                + "                                     AND ccc.ea_account_type = cpnta.account_type "
                + "                                     AND ccc.ea_account_code = cpnta.account_code "
                + "                                 CONNECT BY PRIOR  ccc1.parent_cc_code = ccc1.cost_centre_code ) )");        
		
		//ORDER BY	
		if(!isCountQuery){
			if(!MALUtilities.isEmpty(sort)){
				sqlStmt.append(
						"   ORDER BY ");
				for ( Iterator<Order> orderIterator = sort.iterator(); orderIterator.hasNext(); ) {
					Order order = orderIterator.next();
					
					if(DataConstants.CLIENT_CONTACTS_SORT_ACCOUNT.equals(order.getProperty())){	
						sqlStmt.append(" ea.account_code " + order.getDirection());
					}				
					if(DataConstants.CLIENT_CONTACTS_SORT_PARENT_ACCOUNT.equals(order.getProperty())){	
						sqlStmt.append(" parent_account_code " + order.getDirection());
					}				
					if(DataConstants.CLIENT_CONTACTS_SORT_NAME.equals(order.getProperty())){	
						sqlStmt.append(" cnt.last_name " + order.getDirection() + ", cnt.first_name " + order.getDirection());
					}					

					if(orderIterator.hasNext()){
						sqlStmt.append(", ");
					}
				}			
			} else {
				sqlStmt.append(" ORDER BY decode(assigned, 'Y', '1', '2'), "
						+ "         decode(nvl(ccc.cost_centre_code, '-1'), :costCenterCode, '3', '-1', '5', '4'), " 
						+ "         decode(cnt.contact_type , :contactType, 4, 7), "
						+ "         decode(nvl(parent_account_code, '-1'), '-1', '7', '6'), " 
						+ "         last_name");				
			}				
		}
		
		//Query parameter assignments
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		parameterMap.put("cpntaId", poc.getClientPointAccountId());
		parameterMap.put("contactType", CONTACT_TYPE_CC_MGR);
		

		if(MALUtilities.isEmpty(costCenter)){
			parameterMap.put("costCenterCode", "-1");
			if(!isCountQuery){			
				parameterMap.put("cId", -1L);
				parameterMap.put("accountType", "-1");
				parameterMap.put("accountCode", "-1");	
			}
		} else {
			parameterMap.put("costCenterCode", costCenter.getCostCentreCodesPK().getCostCentreCode());
			if(!isCountQuery){			
				parameterMap.put("cId", costCenter.getCostCentreCodesPK().getEaCId());
				parameterMap.put("accountType", costCenter.getCostCentreCodesPK().getEaAccountType());
				parameterMap.put("accountCode", costCenter.getCostCentreCodesPK().getEaAccountCode());
			}
		}
		
		query = entityManager.createNativeQuery(sqlStmt.toString());		 		 
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 							
				
		return query;
	}
	/**
	 * Retrieves a list of contact (include driver) based on the fleet master (optional),
	 * account, POC, driver's cost center (optional). The fleet master and 
	 * driver's cost center is needed in order to retrieve an assigned driver's contact info.
	 * @param fleetMaster Fleet Master (Optional) 
	 * @param clientAccount The client's account
	 * @param driverCostCenter Driver cost center (Optional)
	 * @param pocName The name of the Point of Communication
	 * @param system The name of the system the Point of Communication is assigned to
	 * @return ClientContactVO List of VOs representing the client'contacts
	 * @throws Exception
	 */
	public List<ClientContactVO> getAllClientContactVOs(FleetMaster fleetMaster, ExternalAccount clientAccount, CostCentreCode costCenter, String pocName, String system) throws Exception{
		ClientContactVO clientContactVO;		
		List<ClientContactVO> clientContactVOs;
		StringBuilder sqlStmt;		
		Query query;
		
		sqlStmt = new StringBuilder("");
		sqlStmt.append("SELECT  cnt_id, drv_id, first_name, last_name, job_title, "
				+ "             email, work_area_code, work_number, work_number_extension, cell_area_code, "
				+ "             cell_number, street_no, address_line_1, address_line_2, address_line_3, "
				+ "             address_line_4, business_address_line, postcode, town_city, region, "
				+ "             county_name, country, method_used, cost_centre_code, dao.contact_type "
				+",home_area_code, home_number, home_number_extension, preferred_contact_number,drv_ind "
				+ "         FROM TABLE(vision.client_contact_wrapper.getContacts(?, ?, ?, ?, ?, ?, ?, ?, ?, ?))dao");
				
		query = entityManager.createNativeQuery(sqlStmt.toString());		
	
		if(MALUtilities.isEmpty(fleetMaster)){
			query.setParameter(1, "");				
		} else {
			query.setParameter(1, fleetMaster.getFmsId());	
		}
		
		query.setParameter(2, clientAccount.getExternalAccountPK().getAccountCode());
		query.setParameter(3, clientAccount.getExternalAccountPK().getAccountType());
		query.setParameter(4, clientAccount.getExternalAccountPK().getCId());
				
		if(MALUtilities.isEmpty(costCenter)){
			query.setParameter(5, "");
			query.setParameter(6, "");
			query.setParameter(7, "");
			query.setParameter(8, "");			
		} else {
			query.setParameter(5, costCenter.getCostCentreCodesPK().getCostCentreCode());
			query.setParameter(6, costCenter.getCostCentreCodesPK().getEaCId());
			query.setParameter(7, costCenter.getCostCentreCodesPK().getEaAccountType());
			query.setParameter(8, costCenter.getCostCentreCodesPK().getEaAccountCode());			
		}
		
		query.setParameter(9, pocName);
		query.setParameter(10, system);				
		
		clientContactVOs = new ArrayList<ClientContactVO>();
		
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				clientContactVO = new ClientContactVO();
				clientContactVO.setContactId(record[i] != null ? ((BigDecimal)record[i]).longValue() : null);				
				clientContactVO.setDriverId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);				
				//clientContactVO.setContactType((String)record[i+=1]);				
				clientContactVO.setFirstName((String)record[i+=1]);
				clientContactVO.setLastName((String)record[i+=1]);	
				clientContactVO.setJobTitle((String)record[i+=1]);
				clientContactVO.setEmail((String)record[i+=1]);
				clientContactVO.setWorkAreaCode((String)record[i+=1]);
				clientContactVO.setWorkNumber((String)record[i+=1]);
				clientContactVO.setWorkNumberExtension((String)record[i+=1]);
				clientContactVO.setCellAreaCode((String)record[i+=1]);
				clientContactVO.setCellNumber((String)record[i+=1]);
				clientContactVO.setStreetNo((String)record[i+=1]);
				clientContactVO.setAddressLine1((String)record[i+=1]);
				clientContactVO.setAddressLine2((String)record[i+=1]);
				clientContactVO.setAddressLine3((String)record[i+=1]);
				clientContactVO.setAddressLine4((String)record[i+=1]);				
				clientContactVO.setBusinessAddressLine((String)record[i+=1]);
				clientContactVO.setPostCode((String)record[i+=1]);	
				clientContactVO.setCity((String)record[i+=1]);	
				clientContactVO.setState((String)record[i+=1]);	
				clientContactVO.setCounty((String)record[i+=1]);
				clientContactVO.setCountry((String)record[i+=1]);	
				clientContactVO.setMethodToUse((String)record[i+=1]);
				clientContactVO.setCostCenterCode((String)record[i+=1]);	
				clientContactVO.setContactType((String)record[i+=1]);	
				clientContactVO.setHomeAreaCode((String)record[i+=1]);
				clientContactVO.setHomeNumber((String)record[i+=1]);
				clientContactVO.setHomeNumberExtension((String)record[i+=1]);
				clientContactVO.setPreferredNumber((String)record[i+=1]);
				clientContactVO.setDriver("Y".equals((String)record[i+=1])? true:false);
				clientContactVOs.add(clientContactVO);
			}

		}
		
		return clientContactVOs;
	}	
	
}
