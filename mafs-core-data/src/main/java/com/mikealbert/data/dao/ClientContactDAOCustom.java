package com.mikealbert.data.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.data.entity.ClientPointAccount;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.data.vo.ReportContactVO;

public interface ClientContactDAOCustom {
	
	public List<ClientContactVO> getContactVOsByAccountPOC(FleetMaster fleetMaster, ExternalAccount clientAccount, CostCentreCode costCenter, String pocName, String system, boolean execludeDrivers) throws Exception;
	
	public ClientContactVO getContactVOByClientPOC(ClientPointAccount clientPOC, Contact contact);	
	
	public List<ClientContactVO> getContactVOsByPOC(ClientPointAccount poc, CostCentreCode costCenter, Pageable pageable, Sort sort);
	public int getContactVOsByPOCCount(ClientPointAccount pointOfCommunication, CostCentreCode costCenter);
	public List<ClientContactVO> getAllClientContactVOs(FleetMaster fleetMaster, ExternalAccount clientAccount, CostCentreCode costCenter, String pocName, String system) throws Exception;
		
	public List<ReportContactVO> findReportEmailContacts(ReportNameEnum reportName, Long cId, String accountType, String accountCode, Long fmsId, Long qmdId);		
	public String getReportEmailBody(ReportNameEnum reportName);
	public String getReportEmailSubject(ReportNameEnum reportName);	
}
