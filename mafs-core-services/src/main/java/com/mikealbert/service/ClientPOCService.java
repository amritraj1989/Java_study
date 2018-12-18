package com.mikealbert.service;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mikealbert.data.entity.ClientPoint;
import com.mikealbert.data.entity.ClientPointAccount;
import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.data.vo.POCCostCenterVO;
import com.mikealbert.data.vo.PointOfCommunicationVO;
import com.mikealbert.exception.MalException;
/**
* Public Interface implemented by {@link com.mikealbert.ClientPOCServiceImpl.service.PointOfContactServiceImpl}.
* 
*  @see com.mikealbert.data.vo.PointOfContactVO
 * @see com.mikealbert.data.entity.ClientPoint
 * @see com.mikealbert.data.entity.ClientPointAccount
 * @see com.mikealbert.data.entity.ClientAccount
* */
public interface ClientPOCService {
	
	static final int SAVE_MODE_ADD = 1;
	static final int SAVE_MODE_DELETE = 2;	
	static final int SAVE_MODE_UPDATE = 3;
	
	static final String SORT_BY_NAME = "NAME";
	static final String SORT_BY_ACCOUNT = "ACCOUNT";
	static final String SORT_BY_PARENT_ACCOUNT = "PARENT_ACCOUNT";
	
	static final String POC_NAME_DEFAULT_CONTACT = "DEFAULT_CONTACT";	
	static final String POC_NAME_MAINT_EXCEED_AUTH_LIMIT = "Exceeds Maintenance Authorization Limit";
	static final String POC_NAME_VEHICLE_RECALL = "Vehicle Recall";	
	static final String POC_NAME_TAL = "Title and License";	
	static final String	POC_NAME_TAL_MULTI = "Title and License - Multi";
	static final String	POC_NAME_VEHICLE_SCHEDULE = "Vehicle Schedule";
	
	
	public List<PointOfCommunicationVO> getPointOfCommunicationVOs(ExternalAccount clientAccount);
	
	public List<PointOfCommunicationVO> getPointOfCommunicationVOs(ExternalAccount clientAccount, Long clientSystemId);
	
	public PointOfCommunicationVO getPointOfCommunicationVO(Long clientPointAccountId);	
	
	public ClientPoint getClientPoint(Long clientPointId);
	
	public ClientPointAccount getClientPOC(Long clientPointAccountId);
	
	public Contact getContact(Long contactId);
	
	public ClientContactVO getClientContactVO(PointOfCommunicationVO pocVO, Contact contact);
	
	public List<ClientContactVO> getSetupClientContactVOs(ClientPointAccount clientPOC, CostCentreCode costCenter, Pageable page, Sort sort);
	public int getSetupClientContactVOsCount(ClientPointAccount clientPOC, CostCentreCode costCenter);
	
	public List<ClientContactVO> getClientContactVOs(FleetMaster fleetMaster, ExternalAccount account, CostCentreCode costCenter, String pocName, String system, boolean orderByContactType);		
	
	public ClientContactVO getDriverClientContactVO(ClientPointAccount clientPOC, CostCentreCode costCenter);
	
	public boolean isDriverAssigned(ClientPointAccount clientPOC, CostCentreCode costCenter);
	
	public List<POCCostCenterVO> getSetupPOCCostCenterVOs(ClientPointAccount clientPOC, Pageable page);	
	public int getSetupPOCCostCenterVOCount(ClientPointAccount clientPOC);
	
	public Date getDateOfLastUpdate(ClientPointAccount clientPOC);
	
	public Date getDateOfLastUpdate(ClientPointAccount clientPOC, String costCentercode); 	
	
	public ClientPointAccount saveOrUpdateClientPOC(ClientPointAccount clientPOC, int mode) throws MalException;
	
	public ClientPointAccount saveOrUpdateClientPOC(ExternalAccount account, ClientPoint clientPoint) throws MalException;
	
	public ClientContactVO saveOrUpdateClientPOCContact(PointOfCommunicationVO pocVO, CostCentreCode costCenterCode, ClientContactVO contactVO, int mode) throws MalException;
	
	public String resolveSortByName(String columnName);	
	public ClientContactVO getDefaultPOCContact(ExternalAccount externalAccount);
	
}
