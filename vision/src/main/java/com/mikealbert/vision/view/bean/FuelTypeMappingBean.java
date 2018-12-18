package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.FuelGrouping;
import com.mikealbert.data.entity.FuelGroupingPK;
import com.mikealbert.data.entity.FuelTypeValues;
import com.mikealbert.service.FuelService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.util.ObjectUtils;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.FuelMappingVO;


@Component
@Scope("view")
public class FuelTypeMappingBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 5459137773854538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private	FuelService	fuelService;

	private	boolean	hasEditPermission;
	private List<FuelMappingVO> fuelMappings;
	private List<String> header = new ArrayList<String>();
	private List<String> fuelGroupKeys;
	private List<FuelTypeValues> fuelTypeValues;
	private List<FuelGroupCode> availableGroups;
	                       
	private String tableWidth;
	private boolean savePossible;
	
	private String groupCode;
	private String groupDescription;
	
	@PostConstruct
	public void init() {
		logger.debug("init is called");
		super.openPage();
		
		
		buildLists();
		buildAvailableGroupsList();
		hasEditPermission = hasPermission();
		checkSavePossible();
		
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Fuel Type Mapping");
		thisPage.setPageUrl(ViewConstants.FUEL_TYPE_MAPPING);
	}

	@Override
	protected void restoreOldPage() {}	
	
	
	public String cancel(){
    	return super.cancelPage();      	
    }

	public void save(){
		try {
			boolean saved = false;
			for(FuelMappingVO fmVO : fuelMappings) {
				for(int i=0; i<fuelGroupKeys.size(); i++) {
					String s = fuelGroupKeys.get(i);
					if(s != null) {
						FuelGroupCode fuelGroupCode = (FuelGroupCode)ObjectUtils.getProperty(fmVO, "FuelGroupCode" + i);
						if(fuelGroupCode != null) {
							FuelGrouping fg = fuelService.getFuelGrouping(s, fmVO.getFuelTypeValue());
							if(fg == null) {
								FuelGrouping fuelGrouping = new FuelGrouping();
								FuelGroupingPK fuelGroupingPK = new FuelGroupingPK();
								fuelGroupingPK.setFuelType(fmVO.getFuelTypeValue());
								fuelGroupingPK.setGroupKey(s);
								fuelGrouping.setFuelGroupingPK(fuelGroupingPK);
								fuelGrouping.setFuelGroupCode(fuelGroupCode);
								fuelService.saveFuelGrouping(fuelGrouping);
								saved = true;
							}
						}
					}
				}
			}
			
			checkSavePossible();
			if(saved) {
				addSuccessMessage("saved.success", "List ");
			} else {
				super.addErrorMessage("generic.error", "No changes to save");
			}
			
		} catch (Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
		}		
	}
		
	
	private void buildLists() {
		fuelMappings = new ArrayList<FuelMappingVO>();
		fuelGroupKeys = fuelService.getDistinctFuelGroupKeys();
		if(fuelGroupKeys.size() < 2) {
			tableWidth = "60%";
		} else {
			tableWidth = "95%";
		}
		for(String s: fuelGroupKeys) {
			header.add(s);
		}
		
		fuelTypeValues = fuelService.getAllFuelTypeValues();
		for(FuelTypeValues ftv : fuelTypeValues) {
			FuelMappingVO fmVO = new FuelMappingVO();
			fmVO.setFuelTypeValue(ftv);

			for(int i=0; i<fuelGroupKeys.size(); i++) {
				String s = fuelGroupKeys.get(i);
				if(s != null) {
					FuelGrouping fg = fuelService.getFuelGrouping(s, ftv);
					if(fg != null) {
						ObjectUtils.setProperty(fmVO, "FuelGroupCode" + i, fg.getFuelGroupCode());
					} else {
						ObjectUtils.setProperty(fmVO, "FuelGroupCode" + i, null);
					}
				}
			}
			fuelMappings.add(fmVO);
		}			
	}

	
	private void buildAvailableGroupsList() {
		availableGroups = new ArrayList<FuelGroupCode>();
		for(FuelGroupCode fgc : fuelService.getAllFuelGroupCodes()) {
			availableGroups.add(fgc);
		}
	}
	
	
	private void checkSavePossible() {
		savePossible = false;
		for(FuelMappingVO fmVO: fuelMappings) {
			for(int i=0; i<fuelGroupKeys.size(); i++) {
				if(ObjectUtils.getProperty(fmVO, "FuelGroupCode" + i) == null) {
					savePossible = true;
					break;
				}
			}
		}
	}
	
	public void addGroup() {

		try {
			if(validGroup()) {
				FuelGroupCode fuelGroupCode = new FuelGroupCode();
				fuelGroupCode.setFuelGroupCode(groupCode);
				fuelGroupCode.setFuelGroupDescription(groupDescription);
				fuelService.saveFuelGroupCode(fuelGroupCode);
				buildAvailableGroupsList();
				groupCode = null;
				groupDescription = null;
	    		super.addSuccessMessage("process.success",  "Group Added - ");
			}
		} catch(Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
		}

	}
	
	private boolean validGroup() {
		boolean valid = true;
		
		if(MALUtilities.isEmptyString(groupCode)) {
			valid = false;
			addErrorMessageSummary("groupCodeId", "custom.message", "Group Code is required");
		}
		if(MALUtilities.isEmptyString(groupDescription)) {
			valid = false;
			addErrorMessageSummary("groupDescId", "custom.message", "Group Description is required" );
		}
		if(fuelService.getFuelGroupCodeByCode(groupCode) != null) {
			valid = false;
			addErrorMessageSummary("groupCodeId", "duplicate.record", groupCode);
		}
		if(fuelService.getFuelGroupCodeByDescription(groupDescription) != null) {
			valid = false;
			addErrorMessageSummary("groupDescId", "duplicate.record", groupDescription);
		}

		if(!valid) {
	    	RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
		return valid;
	}
	
	public void clearDialog() {
		UIComponent comp = getComponent("groupCodeId");
   	 	if(comp!= null) ((UIInput) comp).setValid(true); 

   	 	comp = getComponent("groupDescId");
   	 	if(comp!= null) ((UIInput) comp).setValid(true); 

   	 	groupCode = null;
   	 	groupDescription = null;
	}
	
	public boolean isHasEditPermission() {
		return hasEditPermission;
	}

	public void setHasEditPermission(boolean hasEditPermission) {
		this.hasEditPermission = hasEditPermission;
	}


	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public List<FuelMappingVO> getFuelMappings() {
		return fuelMappings;
	}

	public void setFuelMappings(List<FuelMappingVO> fuelMappings) {
		this.fuelMappings = fuelMappings;
	}

	public List<String> getFuelGroupKeys() {
		return fuelGroupKeys;
	}

	public void setFuelGroupKeys(List<String> fuelGroupKeys) {
		this.fuelGroupKeys = fuelGroupKeys;
	}

	public List<FuelTypeValues> getFuelTypeValues() {
		return fuelTypeValues;
	}

	public void setFuelTypeValues(List<FuelTypeValues> fuelTypeValues) {
		this.fuelTypeValues = fuelTypeValues;
	}

	public List<FuelGroupCode> getAvailableGroups() {
		return availableGroups;
	}

	public void setAvailableGroups(List<FuelGroupCode> availableGroups) {
		this.availableGroups = availableGroups;
	}

	public String getTableWidth() {
		return tableWidth;
	}

	public void setTableWidth(String tableWidth) {
		this.tableWidth = tableWidth;
	}

	public boolean isSavePossible() {
		return savePossible;
	}

	public void setSavePossible(boolean savePossible) {
		this.savePossible = savePossible;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}



}