package com.mikealbert.service; 

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ClientContactDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.vo.ReportContactVO;
import com.mikealbert.util.MALUtilities;


@Service("reportContactService")
public class ReportContactServiceImpl implements ReportContactService {	
	@Resource ClientContactDAO clientContactDAO;
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
		
	@Override
	@Transactional(readOnly = true)
	public List<ReportContactVO> getReportContacts(ReportNameEnum reportName, FleetMaster fms, QuotationModel qmd) {
		
		return clientContactDAO.findReportEmailContacts(reportName, 
				qmd.getQuotation().getExternalAccount().getExternalAccountPK().getCId(), 
				qmd.getQuotation().getExternalAccount().getExternalAccountPK().getAccountType(), 
				qmd.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode(), 
				MALUtilities.isEmpty(fms) ? 0L : fms.getFmsId(), 
				qmd.getQmdId());
	}

	@Override
	public String getReportContactsEmailSubject(ReportNameEnum reportName) {
		return clientContactDAO.getReportEmailBody(reportName);
	}

	@Override
	public String getReportContactsEmailBody(ReportNameEnum reportName) {
		return clientContactDAO.getReportEmailSubject(reportName);
	}
	


}
