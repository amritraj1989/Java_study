package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.MaintSchedulesProcessedDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.data.entity.VehicleScheduleInterval;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.VehicleScheduleService;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.util.ObjectUtils;

@Component
@Scope("view")
public class ViewVehicleMaintenanceScheduleBean extends StatefulBaseBean {
	private static final long serialVersionUID = 1L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private String sendToContactName;
	private String sendToContactAddress;
	private	String	vin;
	private String unitNumber;
	private String fleetRefNumber;
	private Date dateGenerated;
	private String modelDesc ;
	private	BigDecimal	driverAuthorizationLimit;
	private	String	driverAuthorizationNumber;
	private int scrollHeight;
	private boolean readOnly = true;
	
	List<MaintenanceScheduleIntervalPrintVO> maintScheduleVOs = new ArrayList<MaintenanceScheduleIntervalPrintVO>();
	private List<String> headerList = new ArrayList<String>();
	private List<String[]> columnValues = new ArrayList<String[]>();

	@Resource
	private MaintenanceScheduleService maintenanceScheduleService;

	@Resource
	private VehicleScheduleService vehicleScheduleService;

	@Resource
	private FleetMasterService fleetMasterService;
	
	@Resource
	private	MaintSchedulesProcessedDAO	maintSchedulesProcessedDAO;
	
	@Resource
	private	ContractService	contractService;
	
	@Resource
	private	QuotationService	quotationService;
	
	final String POC_MAINT_SYSTEM = "MAINT";
	final String POC_NAME_MAINT_EXCEED_AUTH_LIMIT = "Exceeds Maintenance Authorization Limit";

	@PostConstruct
	public void init() {

	}

	public void fetchScheduleMaintList() {
		try {
			unitNumber = this.getRequestParameter("UNIT_NUMBER");
			if(MALUtilities.isNotEmptyString(this.getRequestParameter("READ_ONLY"))){
				readOnly = Boolean.parseBoolean(this.getRequestParameter("READ_ONLY"));
			}

			FleetMaster fleetMaster = fleetMasterService.findByUnitNo(unitNumber);
			fleetRefNumber = fleetMaster.getFleetReferenceNumber();
			vin = fleetMaster.getVin();
			driverAuthorizationNumber = "MAFS"+unitNumber;
			modelDesc =(fleetMaster.getModel().getModelMarkYear().getModelMarkYearDesc() + " " +
					fleetMaster.getModel().getMake().getMakeDesc()   + " " +
					fleetMaster.getModel().getMakeModelRange().getDescription());
			//TODO: move to a core method as time permits and log a TD
			ContractLine contractLine = null;
			Calendar calendar = Calendar.getInstance();    
			calendar.setTime(new Date());
			calendar.add(Calendar.DATE, 365);
			contractLine = contractService.getLastActiveContractLine(fleetMaster, calendar.getTime());
			
			if(contractLine != null){
				ExternalAccount externalAccount = contractLine.getContract().getExternalAccount();
				ClientContactVO clientContact = maintenanceScheduleService.getAllClientContactVOsForSchedules( externalAccount,  fleetMaster);
				if(clientContact != null){
					sendToContactName	= clientContact.formattedName();
					sendToContactAddress = clientContact.getAddressDisplay();
				}
				driverAuthorizationLimit =quotationService.getDriverAuthorizationLimit(externalAccount.getExternalAccountPK().getCId(), externalAccount.getExternalAccountPK().getAccountType(), externalAccount.getExternalAccountPK().getAccountCode(), unitNumber); 
				if(driverAuthorizationLimit == null) {
					driverAuthorizationLimit = BigDecimal.ZERO;
				}
			}
			
			scrollHeight = 50;
			headerList.clear();
			columnValues.clear();
			boolean isHeaderListPrepared = false;
			if (!MALUtilities.isEmpty(unitNumber)) {
				
				List<VehicleSchedule> vehScheduleList = vehicleScheduleService.getVehicleScheduleByUnitNumber(unitNumber);
				if (vehScheduleList == null || vehScheduleList.isEmpty()){
					addErrorMessage("generic.error", "No vehicle maintenance schedule has been created for this unit.");
					RequestContext context = RequestContext.getCurrentInstance();
					context.addCallbackParam("failure", true);
					return;
				} else {
					for (VehicleSchedule vehicleSchedule : vehScheduleList) {
						Date activeToDate = vehicleSchedule.getActiveTo();
						if (activeToDate != null && activeToDate.before(new Date())) {
							continue;
						}

						MaintSchedulesProcessed maintSchedulesProcessed = null;
						List<MaintSchedulesProcessed> maintSchedulesProcessedList = 
								maintSchedulesProcessedDAO.findByFmsIdAndVehSchId(fleetMaster.getFmsId(), vehicleSchedule.getVschId());
						if(maintSchedulesProcessedList != null && !maintSchedulesProcessedList.isEmpty()){
							maintSchedulesProcessed = maintSchedulesProcessedList.get(0);
							dateGenerated	= maintSchedulesProcessed.getDateGenerated();
						}
						MasterSchedule masterSchedule = vehicleSchedule.getMasterSchedule();
						
						int startingOdo = vehicleSchedule.getStartOdoReading() != null ? vehicleSchedule.getStartOdoReading().intValue() : 0;
						BigDecimal conversionFactor = new BigDecimal(1);
						if(maintSchedulesProcessed != null) {
							conversionFactor = maintenanceScheduleService.getConversionFactorForSchedule(maintSchedulesProcessed);
						}

						Long vehicleSchSequence = vehicleSchedule.getVehSchSeq();

						if (!isHeaderListPrepared) {
							headerList.add("PO No");
							headerList.add("Authorization Number");
							headerList.add("Perform services at mileage indicated");
							isHeaderListPrepared = true;
							MaintenanceScheduleIntervalPrintVO headerVO = maintenanceScheduleService.getMasterSchedulePrintHeader(masterSchedule.getMschId());
							for (int b = 0; b < 13; b++) {
								String headerText = (String) ObjectUtils.getProperty(headerVO, "Column" + (b + 1));
								if (!MALUtilities.isEmpty(headerText)) {
									headerList.add(headerText);
								}
							}
						}

						List<VehicleScheduleInterval> completedIntervals = vehicleScheduleService.getCompletedIntervalsWithPO(vehicleSchedule);
						int lastCompleteIntervalIndex = 0;
						if (completedIntervals != null && !completedIntervals.isEmpty()) {
							lastCompleteIntervalIndex = vehicleScheduleService.getIntervalIndex(completedIntervals.get(completedIntervals.size() - 1).getAuthorizationNo());
						}

						List<MaintenanceScheduleIntervalPrintVO> maintScheduleVOList = maintenanceScheduleService
								.getMasterScheduleIntervalList(masterSchedule.getMschId(), startingOdo, conversionFactor, lastCompleteIntervalIndex + 35);

						if(maintScheduleVOList.size() > 2){
							scrollHeight = 250;
						}

						String authorizationNumber;
						String completedPONumber;
						int realIntervalIndex = 1;
						for(int i=0; i<maintScheduleVOList.size(); i++) {
							MaintenanceScheduleIntervalPrintVO targetRow = maintScheduleVOList.get(i);
							if(MALUtilities.isEmpty(targetRow.getIntervalDescription())){
								authorizationNumber	= "PO Required";
								completedPONumber	= "";
							}else{
								authorizationNumber = vehicleScheduleService.getAuthorizationNumber(vehicleSchSequence, vehicleScheduleService.calculateIntervalCode(realIntervalIndex));
								completedPONumber = getCompletedPONumber(authorizationNumber, completedIntervals);
								realIntervalIndex++;
							}
							columnValues.add(fillRow(targetRow, completedPONumber, authorizationNumber ));
						}

					}
				}
			}
				

			
		} catch (Exception ex) {
			handleException("generic.error", new String[]{"Error while getting vehicle schedules"}, ex, null);
		}
	}

	private String getCompletedPONumber(String intervalAuthorizationNumber, List<VehicleScheduleInterval> completedIntervals) {
		
		for(VehicleScheduleInterval vsi : completedIntervals) {
			if(vsi.getAuthorizationNo().equalsIgnoreCase(intervalAuthorizationNumber)) {
				return vsi.getDocNo();
			}
		}
		
		return null;
	}
	
	private String[] fillRow(MaintenanceScheduleIntervalPrintVO msipVO, String completedPONumber, String authorizationNumber) {
		String[] columnValueArray = new String[15];
		for (int b = 0; b < 15; b++) {
			if(b ==0){
				columnValueArray[b] = completedPONumber;
			} else if (b == 1) {
				columnValueArray[b] = authorizationNumber;
			} else if (b == 2) {
					columnValueArray[b] = msipVO.getIntervalDescription();
			} else {
				String value = (String) ObjectUtils.getProperty(msipVO, "Column" + (b - 2));
				if (!MALUtilities.isEmpty(value)) {
					columnValueArray[b] = value;
				}
			}
		}
		return columnValueArray;
	}


	@Override
	protected void loadNewPage() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void restoreOldPage() {
		// TODO Auto-generated method stub

	}

	public String getSendToContactName() {
		return sendToContactName;
	}

	public void setSendToContactName(String sendToContactName) {
		this.sendToContactName = sendToContactName;
	}

	public String getSendToContactAddress() {
		return sendToContactAddress;
	}

	public void setSendToContactAddress(String sendToContactAddress) {
		this.sendToContactAddress = sendToContactAddress;
	}


	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public String getFleetRefNumber() {
		return fleetRefNumber;
	}

	public void setFleetRefNumber(String fleetRefNumber) {
		this.fleetRefNumber = fleetRefNumber;
	}

	public List<MaintenanceScheduleIntervalPrintVO> getMaintScheduleVOs() {
		return maintScheduleVOs;
	}

	public void setMaintScheduleVOs(List<MaintenanceScheduleIntervalPrintVO> maintScheduleVOs) {
		this.maintScheduleVOs = maintScheduleVOs;
	}

	

	public Date getDateGenerated() {
		return dateGenerated;
	}

	public void setDateGenerated(Date dateGenerated) {
		this.dateGenerated = dateGenerated;
	}

	public List<String> getHeaderList() {
		return headerList;
	}

	public void setHeaderList(List<String> headerList) {
		this.headerList = headerList;
	}

	public List<String[]> getColumnValues() {
		return columnValues;
	}

	public void setColumnValues(List<String[]> columnValues) {
		this.columnValues = columnValues;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public BigDecimal getDriverAuthorizationLimit() {
		return driverAuthorizationLimit;
	}

	public void setDriverAuthorizationLimit(BigDecimal driverAuthorizationLimit) {
		this.driverAuthorizationLimit = driverAuthorizationLimit;
	}

	public String getDriverAuthorizationNumber() {
		return driverAuthorizationNumber;
	}

	public void setDriverAuthorizationNumber(String driverAuthorizationNumber) {
		this.driverAuthorizationNumber = driverAuthorizationNumber;
	}

	public int getScrollHeight() {
		return scrollHeight;
	}

	public void setScrollHeight(int scrollHeight) {
		this.scrollHeight = scrollHeight;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	

	
	

}
