package com.mikealbert.vision.view.bean.components;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.RegionCode;
import com.mikealbert.data.entity.WarrantyUnitLink;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.VehicleSearchService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VehicleMaintenanceService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;
import com.mikealbert.vision.view.link.EditViewDriverLink;

@Component
@Scope("view")
public class vehicleDisplayBean extends BaseBean {	
	private static final long serialVersionUID = -7170421837551409230L;

	@Resource VehicleSearchService vehicleSearchService;
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource OdometerService odometerService;
	@Resource FleetMasterService fleetMasterService;
	@Resource LeaseElementDAO leaseElementDAO;
	private VehicleSearchResultVO vehicleVO;
	private VehicleInformationVO vehicleInfo;
	private List<RegionCode> taxExemptedRegionList;
	private String mrqId;
	private String fmsId;
	private String warrantyInformation;
	private String ExtWarrantyInd;
	private Double warrantyMileage;
	private EditViewDriverLink parentBean;
	public static final String  displayMessage ="Unit not currently on a maintenance program";

	public String getMrqId() {
		return mrqId;
	}

	public void setMrqId(String mrqId) {
		this.mrqId = mrqId;
	}

	public String getDisplayMessage() {
		return displayMessage;
	}

	public static String getDisplaymessage() {
		return displayMessage;
	}

	public String getFmsId() {
		return fmsId;
	}

	public void setFmsId(String fmsId) {
		this.fmsId = fmsId;
	}


	public EditViewDriverLink getParentBean() {
		return parentBean;
	}


	public void setParentBean(EditViewDriverLink parentBean) {
		this.parentBean = parentBean;
	}


	/**
	 * Initializes the bean
	 */
	@PostConstruct
	public void init(){ 
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			Long mrqId = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.mrqId}", Long.class);
			Long fmsId = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.fmsId}", Long.class);
			VehicleInformationVO vehicleInfoObj = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.vehInfo}", VehicleInformationVO.class);
			parentBean = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.parentBean}", EditViewDriverLink.class);

			//If all of the base input data is null, this init it probably being inadvertently called from a commandLink
			if((!MALUtilities.isEmpty(fmsId) && fmsId.intValue() != 0) || (!MALUtilities.isEmpty(mrqId) && mrqId.intValue() != 0) ||
					vehicleInfoObj != null){
				Date actualStartDate = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.actualStartDate}", Date.class);

				if(!MALUtilities.isEmpty(vehicleInfoObj)) {
					setVehicleInfo(vehicleInfoObj);
				}else if(!MALUtilities.isEmpty(fmsId) && fmsId.intValue() != 0) {
					setVehicleInfo(vehicleMaintenanceService.getVehicleInformationByFmsId(fmsId));      		
				} else if(!MALUtilities.isEmpty(mrqId) && mrqId.intValue() != 0) {
					setVehicleInfo(vehicleMaintenanceService.getVehicleInformationByMrqId(mrqId));      		
				}
				if(!MALUtilities.isEmpty(getVehicleInfo())){
					loadTaxExemptedRegions();
					loadWarrantyInformation();
					loadExtWarrantyInfo(actualStartDate);
				}
			}

			//HD-419
			if(fmsId==0 && fmsId!=null){
				Long Fms_Id=vehicleInfoObj.getFmsId();
				isUnitOnMaintenance(Fms_Id);

			}
			else{
				isUnitOnMaintenance(fmsId);

			}
		}


		catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
		}    	

	}

	private void loadTaxExemptedRegions(){
		if(getVehicleInfo().getClientTaxIndicator() != null && getVehicleInfo().getClientTaxIndicator().equals("E")){
			taxExemptedRegionList = vehicleMaintenanceService.getTaxExemptedRegions(getVehicleInfo().getClientCorporateId(), getVehicleInfo().getClientAccountType(), getVehicleInfo().getClientAccountNumber());
		}
	}

	private void loadWarrantyInformation() {
		String uomCode;
		String uomDescription;
		DecimalFormat decimalFormat = new DecimalFormat("############.#");

		if (getVehicleInfo().getVehicleTechInfo() != null && getVehicleInfo().getVehicleTechInfo().getVehicleWarrantyMileage() != null && getVehicleInfo().getVehicleTechInfo().getVehicleWarrantyMonths() != null) {
			uomCode = odometerService.getCurrentOdometer(fleetMasterService.getFleetMasterByFmsId(getVehicleInfo().getFmsId())).getUomCode();
			uomDescription = (odometerService.convertOdoUOMCode(uomCode)).getDescription();

			if (uomCode.equals("KM")) {
				setWarrantyMileage(getVehicleInfo().getVehicleTechInfo().getVehicleWarrantyMileage() * ViewConstants.MILE_TO_KM_CONVERSION_FACTOR);
			} else {
				setWarrantyMileage(getVehicleInfo().getVehicleTechInfo().getVehicleWarrantyMileage().doubleValue());
			}

			if (getVehicleInfo().getVehicleTechInfo().getVehicleWarrantyMonths() == 999) {
				if (getVehicleInfo().getVehicleTechInfo().getVehicleWarrantyMileage() == 99999999) {
					setWarrantyInformation("Unlimited Months / Unlimited " + uomDescription);
				} else {
					setWarrantyInformation("Unlimited Months / " + decimalFormat.format(getWarrantyMileage()) + " " + uomDescription);
				}
			} else {
				if (getVehicleInfo().getVehicleTechInfo().getVehicleWarrantyMileage() == 99999999) {
					setWarrantyInformation(getVehicleInfo().getVehicleTechInfo().getVehicleWarrantyMonths() + " Months / Unlimited " + uomDescription);
				} else {
					setWarrantyInformation(getVehicleInfo().getVehicleTechInfo().getVehicleWarrantyMonths() + " Months / " + decimalFormat.format(getWarrantyMileage()) + " " + uomDescription);
				}
			}
		}
	}

	private void loadExtWarrantyInfo (Date startDate) {
		Date currentDate = Calendar.getInstance().getTime();
		setExtWarrantyInd(null);

		if (startDate != null) {
			currentDate = MALUtilities.clearTimeFromDate(startDate);
		}

		for (WarrantyUnitLink extWar : getVehicleInfo().getWarrantyUnitLinks()) {
			Date extWarStartDate = MALUtilities.clearTimeFromDate(extWar.getStartDate());
			Date extWarEndDate = MALUtilities.clearTimeFromDate(extWar.getEndDate());
			if ((MALUtilities.compateDates(currentDate, extWarStartDate) == 1 || MALUtilities.compateDates(currentDate, extWarStartDate) == 0)
					&& (MALUtilities.compateDates(currentDate, extWarEndDate) == -1 || MALUtilities.compateDates(currentDate, extWarEndDate) == 0)) {
				setExtWarrantyInd("(Ext. Warranty)");
				break;
			}
		}

	}

	public void refreshExtWarrantyInfo(Date startDate) {
		if (startDate != null) {
			loadExtWarrantyInfo(startDate);
		}
	}

	public VehicleSearchResultVO getVehicleVO() {
		return vehicleVO;
	}

	public void setVehicleVO(VehicleSearchResultVO vehicleVO) {
		this.vehicleVO = vehicleVO;
	}

	public VehicleInformationVO getVehicleInfo() {
		return vehicleInfo;
	}

	public void setVehicleInfo(VehicleInformationVO vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	}


	public List<RegionCode> getTaxExemptedRegionList() {
		return taxExemptedRegionList;
	}


	public void setTaxExemptedRegionList(List<RegionCode> taxExemptedRegionList) {
		this.taxExemptedRegionList = taxExemptedRegionList;
	}


	public String getWarrantyInformation() {
		return warrantyInformation;
	}


	public void setWarrantyInformation(String warrantyInformation) {
		this.warrantyInformation = warrantyInformation;
	}


	public String getExtWarrantyInd() {
		return ExtWarrantyInd;
	}


	public void setExtWarrantyInd(String extWarrantyInd) {
		ExtWarrantyInd = extWarrantyInd;
	}


	public Double getWarrantyMileage() {
		return warrantyMileage;
	}


	public void setWarrantyMileage(Double warrantyMileage) {
		this.warrantyMileage = warrantyMileage;
	}

	public void editViewDriver(){
		if(!MALUtilities.isEmpty(parentBean)){
			parentBean.editViewDriver(getVehicleInfo().getDrvId().toString());
		}
	}
	private void isUnitOnMaintenance(Long fmsId){
		List<LeaseElement> leaseElementList = leaseElementDAO.findAllMaintenanceLeaseElements();
		Long clnIdforReleaseUnit=vehicleMaintenanceService.getClnIdforReleaseUnit(fmsId);
		if(!MALUtilities.isEmpty(clnIdforReleaseUnit)){
			VehicleSearchResultVO vehicleVO = new VehicleSearchResultVO();
			Long qmdIdfromClnId=vehicleMaintenanceService.getQmdIdfromClnId(clnIdforReleaseUnit);
			if(!MALUtilities.isEmpty(qmdIdfromClnId)|| qmdIdfromClnId !=null){
				//For checking Informal Unit
				boolean informalUnit=vehicleMaintenanceService.validationCheckForInformalUnit(qmdIdfromClnId);
				if(informalUnit==true){
					for(LeaseElement leaseElement : leaseElementList){
						Long lelID;
						lelID = leaseElement.getLelId();
						String unitOnMaintenance =  vehicleMaintenanceService.findElementOnQuote(qmdIdfromClnId, lelID);
						if("Y".equalsIgnoreCase(unitOnMaintenance)){
							vehicleVO.setVehicleUnderMaintenanceFlag(false);
							setVehicleVO(vehicleVO);
							break;
						}
						if("N".equalsIgnoreCase(unitOnMaintenance)){
							vehicleVO.setVehicleUnderMaintenanceFlag(true);
							setVehicleVO(vehicleVO);
						}
					} 
				}
				else if(informalUnit==false){
					int leaseElementCount =0;
					if(!MALUtilities.isEmpty(fmsId)){
						leaseElementCount =  vehicleMaintenanceService.getleaseElementbyFmdId(qmdIdfromClnId);
						if(leaseElementCount==0){
							vehicleVO.setVehicleUnderMaintenanceFlag(true);
							setVehicleVO(vehicleVO);
						}
					}
				}
			}
		}

		else{
			Long clnId=vehicleMaintenanceService.getContractLinesfromfmsId(fmsId);
			VehicleSearchResultVO vehicleVO = new VehicleSearchResultVO();
			Long qmdIdforDisposedUnit;
			Long qmdIdfromClnId;
			if(!MALUtilities.isEmpty(clnId)){
				qmdIdfromClnId=vehicleMaintenanceService.getQmdIdfromClnId(clnId);
				if(!MALUtilities.isEmpty(qmdIdfromClnId)|| qmdIdfromClnId !=null){
					boolean informalUnit=vehicleMaintenanceService.validationCheckForInformalUnit(qmdIdfromClnId);
					if(informalUnit==true){
						for(LeaseElement leaseElement : leaseElementList){
							Long lelID;
							lelID = leaseElement.getLelId();
							String unitOnMaintenance =  vehicleMaintenanceService.findElementOnQuote(qmdIdfromClnId, lelID);
							if("Y".equalsIgnoreCase(unitOnMaintenance)){
								vehicleVO.setVehicleUnderMaintenanceFlag(false);
								setVehicleVO(vehicleVO);
								break;
							}
							else if(unitOnMaintenance.equals("N")){
								vehicleVO.setVehicleUnderMaintenanceFlag(true);
								setVehicleVO(vehicleVO);
							}
						}
					}
					else if(informalUnit==false){
						int leaseElementCount =0;
						if(!MALUtilities.isEmpty(fmsId)){
							leaseElementCount =  vehicleMaintenanceService.getleaseElementbyFmdId(qmdIdfromClnId);
							if(leaseElementCount==0){
								vehicleVO.setVehicleUnderMaintenanceFlag(true);
								setVehicleVO(vehicleVO);
							}
						}
					}
				} 
			}

			//VEHICLE_ON_CONTRACT AND PENDING_LIVE
			else if(MALUtilities.isEmpty(clnId)){
				qmdIdforDisposedUnit = null;
				qmdIdforDisposedUnit=vehicleMaintenanceService.getClnIdforDisposedUnit(fmsId);
				if(!MALUtilities.isEmpty(qmdIdforDisposedUnit) && (qmdIdforDisposedUnit !=null) && (qmdIdforDisposedUnit != 0)){
					boolean Unit=vehicleMaintenanceService.validationCheckForInformalUnit(qmdIdforDisposedUnit);
					if(Unit==true){
						for(LeaseElement leaseElement : leaseElementList){
							Long lelID;
							lelID = leaseElement.getLelId();
							String vehicleUnderMaintenanceFlag =  vehicleMaintenanceService.findElementOnQuote(qmdIdforDisposedUnit, lelID);
							if("Y".equalsIgnoreCase(vehicleUnderMaintenanceFlag)){
								vehicleVO.setVehicleUnderMaintenanceFlag(false);
								setVehicleVO(vehicleVO);
								break;
							}
							else if(vehicleUnderMaintenanceFlag.equals("N")){
								vehicleVO.setVehicleUnderMaintenanceFlag(true);
								setVehicleVO(vehicleVO);
							}
						}
					}
					else if(Unit==false){
						int leaseElementCount =0;
						if(!MALUtilities.isEmpty(fmsId)){
							leaseElementCount =  vehicleMaintenanceService.getleaseElementbyFmdId(qmdIdforDisposedUnit);
							if(leaseElementCount==0){
								vehicleVO.setVehicleUnderMaintenanceFlag(true);
								setVehicleVO(vehicleVO);
							}
						}
					}
				} 
			}
		}
	}

}
