package com.mikealbert.vision.view.bean.components;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.MaintenanceCategoryPropertyValue;
import com.mikealbert.data.entity.MaintenanceCategoryUOM;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class maintenanceCategoryInfoBean extends BaseBean {
	private static final long serialVersionUID = -774371402692590593L;
	
	@Resource MaintenanceRequestService maintRequestService;

	final static String BRAKES_DIALOG_HEADER = "Brake Measurements - Additional Information";
	final static String BRAKES_DIALOG_HEIGHT = "430";
	final static String BRAKES_DIALOG_WIDTH = "425";
	final static String GLASS_DIALOG_HEADER = "Glass - Additional Information";	
	final static String GLASS_DIALOG_HEIGHT = "500";
	final static String GLASS_DIALOG_WIDTH = "800";
	final static String TIRES_DIALOG_HEADER = "Tires - Additional Information";	
	final static String TIRES_DIALOG_HEIGHT = "300";
	final static String TIRES_DIALOG_WIDTH = "500";

	private String dialogHeight;
	private String dialogWidth;
	private String dialogHeader;
	
	private MaintenanceRequestTask maintReqTask;
	private List<MaintenanceCategoryUOM> maintCatUOM;
	private String maintCatCode = "";
	private boolean saveFlag = false;
	
    public void initDialog(MaintenanceRequestTask mrt){
    	String maintenanceCategoryCode = mrt.getMaintCatCode();

		try{
	    	if(maintenanceCategoryCode.equals("GLASS")){
	    		setDialogHeader(GLASS_DIALOG_HEADER);
	    		setDialogHeight(GLASS_DIALOG_HEIGHT);
	    		setDialogWidth(GLASS_DIALOG_WIDTH);
	    	}
	    	else if(maintenanceCategoryCode.equals("BRKS_BRGS")){
	    		setDialogHeader(BRAKES_DIALOG_HEADER);
	    		setDialogHeight(BRAKES_DIALOG_HEIGHT);
	    		setDialogWidth(BRAKES_DIALOG_WIDTH);
	    	}  
	    	else { //TIRES
	    		setDialogHeader(TIRES_DIALOG_HEADER);
	    		setDialogHeight(TIRES_DIALOG_HEIGHT);
	    		setDialogWidth(TIRES_DIALOG_WIDTH);    		
	    	}
	    	
	    	loadMaintenanceRequestTask(mrt);
	    	loadMaintenanceCategoryUnitOfMeasure(mrt);
	    	saveFlag = false;
		} catch(Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}    	
    }
    	
    private void loadMaintenanceRequestTask(MaintenanceRequestTask mrt){
    	if(!MALUtilities.isEmpty(mrt)){
    		//check to see if user has clicked on additional info icon already
    		if(!MALUtilities.isEmpty(maintCatCode)){
    			//check to see if user changed category code on task or this is a new task
    			if((!maintCatCode.equals(mrt.getMaintCatCode())) || (MALUtilities.isEmpty(mrt.getMaintenanceCategoryPropertyValues())) || (mrt.getMaintenanceCategoryPropertyValues().size() == 0)){
        			maintReqTask = maintRequestService.intializeMaintenanceCateogryProperties(mrt);
	    		}
    			//existing additional info
        		else{
        			maintReqTask = mrt;
        		}
    		}
    		//new task
    		else if(MALUtilities.isEmpty(mrt.getMaintenanceCategoryPropertyValues()) || mrt.getMaintenanceCategoryPropertyValues().size() == 0){
    			maintReqTask = maintRequestService.intializeMaintenanceCateogryProperties(mrt);
    		}
    		//existing additional info
    		else{
    			maintReqTask = mrt;
    		}
  
    		//there is a known issue with the primefaces datatable sortby attribute not working, 
    		//so doing a sort of the maintenance category property values here as a workaround
			Collections.sort(maintReqTask.getMaintenanceCategoryPropertyValues(), new Comparator<MaintenanceCategoryPropertyValue>() { 
				public int compare(MaintenanceCategoryPropertyValue c1, MaintenanceCategoryPropertyValue c2) { 
					return c1.getMaintenanceCategoryProperty().getSortOrder().compareTo(c2.getMaintenanceCategoryProperty().getSortOrder());
				}
			});	
			
    		setMaintCatCode(maintReqTask.getMaintCatCode());
    	}
    }
    
    public void done(){
    	saveFlag = true;
    }
    
    public void resetMaintCateogryPropertyValues(){
    	if(saveFlag == false){
    		if(!MALUtilities.isEmpty(maintReqTask)){
    			maintRequestService.resetMaintenanceCateogryPropertyValues(maintReqTask);
    		}
    	}
    }
    
    public void loadMaintenanceCategoryUnitOfMeasure(MaintenanceRequestTask mrt){
    	maintCatUOM = maintRequestService.getMaintenanceCategoryUOM(mrt);
    }

	public String getDialogHeight() {
		return dialogHeight;
	}

	public void setDialogHeight(String dialogHeight) {
		this.dialogHeight = dialogHeight;
	}

	public String getDialogWidth() {
		return dialogWidth;
	}

	public void setDialogWidth(String dialogWidth) {
		this.dialogWidth = dialogWidth;
	}

	public String getDialogHeader() {
		return dialogHeader;
	}

	public void setDialogHeader(String dialogHeader) {
		this.dialogHeader = dialogHeader;
	}

	public MaintenanceRequestTask getMaintReqTask() {
		return maintReqTask;
	}

	public void setMaintReqTask(MaintenanceRequestTask maintReqTask) {
		this.maintReqTask = maintReqTask;
	}

	public List<MaintenanceCategoryUOM> getMaintCatUOM() {
		return maintCatUOM;
	}

	public void setMaintCatUOM(List<MaintenanceCategoryUOM> maintCatUOM) {
		this.maintCatUOM = maintCatUOM;
	}

	public String getMaintCatCode() {
		return maintCatCode;
	}

	public void setMaintCatCode(String maintCatCode) {
		this.maintCatCode = maintCatCode;
	}	

}
