package com.mikealbert.vision.view.bean;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.VehicleConfiguration;
import com.mikealbert.data.vo.UpfitAssessmentVO;
import com.mikealbert.service.UpfitAssessmentService;
import com.mikealbert.service.VehicleConfigurationService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component
@Scope("view")
public class UpfitAssessmentBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 5459137773854538998L;

	@Resource UpfitAssessmentService upfitAssessmentService;
	@Resource VehicleConfigurationService vehicleConfigurationService;
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	List<UpfitAssessmentVO> upfitAssessmentList = null;
	
	List<UpfitAssessmentVO> upfitAssessmentListFleet = new ArrayList<UpfitAssessmentVO>();
	List<UpfitAssessmentVO> upfitAssessmentListConfig = new ArrayList<UpfitAssessmentVO>();

	private Long vehicleConfigId;
	private VehicleConfiguration selectedVehicleConfiguration;
	private boolean hasUpfitAssessment = false;
	private Date lastUpdatedDate;
	private String lastUpdatedBy;
	private boolean readOnly = false;
	
	@PostConstruct
	public void init() {
		logger.debug("init is called");
		super.openPage();
		
		if(!MALUtilities.isEmpty(vehicleConfigId)){
			setSelectedVehicleConfiguration(vehicleConfigurationService.getVehicleConfiguration(vehicleConfigId));
		}
		if(hasUpfitAssessment){
			initEditUpfitAssessment();
		}else{
			initAddUpfitAssessment();
		}
		
		for(UpfitAssessmentVO vo : upfitAssessmentList){
			if(vo.getUpfitAssessmentQuestion().getGroupPosition() == 1){
				upfitAssessmentListFleet.add(vo);
			}else{
				upfitAssessmentListConfig.add(vo);
			}
		}
		
		if(super.hasPermission("quoteRequestAddEdit")){
			readOnly = true;
			if(super.hasPermission("quoteRequestAddEdit_manage")){
				readOnly = false;
			}
		}
	}

	private void initAddUpfitAssessment() {
		upfitAssessmentList = upfitAssessmentService.prepareUpfitAssessmentVoForVehicleConfigId(vehicleConfigId);
	}

	private void initEditUpfitAssessment() {
		upfitAssessmentList = upfitAssessmentService.getUpfitAssessmentVosByVehicleConfigId(vehicleConfigId);
		if(!MALUtilities.isEmpty(upfitAssessmentList) && upfitAssessmentList.size() > 0){ 
			lastUpdatedDate = upfitAssessmentList.get(0).getUpfitAssessmentAnswer().getLastUpdatedDate();
			lastUpdatedBy = upfitAssessmentList.get(0).getUpfitAssessmentAnswer().getLastUpdatedBy();
		}
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Upfit Assessment");
		thisPage.setPageUrl(ViewConstants.UPFIT_ASSESSMENT);
		Long vehicleConfigId = (Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_VEHICLE_CONFIG_ID);
		if(vehicleConfigId != null) {
			this.vehicleConfigId = vehicleConfigId;
		}
		boolean hasUpfitAssessment = (boolean) thisPage.getInputValues().get("UPFIT_ASSESSMENT");
		this.hasUpfitAssessment = hasUpfitAssessment;
	}

	@Override
	protected void restoreOldPage() {}	
	
	
	public String cancel(){
    	return super.cancelPage();      	
    }

	public String save(){		
		try {
			upfitAssessmentService.saveOrUpdateUpfitAssessmentAnswers(upfitAssessmentList, super.getLoggedInUser().getEmployeeNo());
			addSuccessMessage("custom.message", "Upfit Assessment saved successsfully for Application " + selectedVehicleConfiguration.getDescription());
		} catch (Exception e) {
			logger.error(e ,"Error occured while saving Upfit Assessment Questionnaire");
			addErrorMessage("generic.error.occured.while", "saving Upfit Assessment Questionnaire");
		}
		
    	return super.cancelPage();      	
			
	}

	public List<UpfitAssessmentVO> getUpfitAssessmentList() {
		return upfitAssessmentList;
	}

	public void setUpfitAssessmentList(List<UpfitAssessmentVO> upfitAssessmentList) {
		this.upfitAssessmentList = upfitAssessmentList;
	}

	public List<UpfitAssessmentVO> getUpfitAssessmentListFleet() {
		return upfitAssessmentListFleet;
	}

	public void setUpfitAssessmentListFleet(
			List<UpfitAssessmentVO> upfitAssessmentListFleet) {
		this.upfitAssessmentListFleet = upfitAssessmentListFleet;
	}

	public List<UpfitAssessmentVO> getUpfitAssessmentListConfig() {
		return upfitAssessmentListConfig;
	}

	public void setUpfitAssessmentListConfig(
			List<UpfitAssessmentVO> upfitAssessmentListConfig) {
		this.upfitAssessmentListConfig = upfitAssessmentListConfig;
	}

	public VehicleConfiguration getSelectedVehicleConfiguration() {
		return selectedVehicleConfiguration;
	}

	public void setSelectedVehicleConfiguration(
			VehicleConfiguration selectedVehicleConfiguration) {
		this.selectedVehicleConfiguration = selectedVehicleConfiguration;
	}

	public boolean isHasUpfitAssessment() {
		return hasUpfitAssessment;
	}

	public void setHasUpfitAssessment(boolean hasUpfitAssessment) {
		this.hasUpfitAssessment = hasUpfitAssessment;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}


}