package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.FuelGroupingDAO;
import com.mikealbert.data.dao.FuelTypeValuesDAO;
import com.mikealbert.data.dao.ModelDAO;
import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.entity.ClientScheduleType;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.data.entity.FuelGrouping;
import com.mikealbert.data.entity.FuelGroupingPK;
import com.mikealbert.data.entity.FuelTypeValues;
import com.mikealbert.data.entity.MaintScheduleRule;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.entity.WillowConfig;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.MakeService;
import com.mikealbert.service.ModelSearchService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component
@Scope("view")
public class ScheduleRuleAddEditBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 5459137983854538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private ModelDAO modelDAO;
	@Resource
	private ModelSearchService modelSearchService;	
	@Resource
	private MaintenanceScheduleService maintenanceScheduleService;
	@Resource 
	private CustomerAccountService externalAccountService;
	@Resource
	private	MakeService	makeService;
	@Resource
	private	ExternalAccountDAO	externalAccountDAO;
	@Resource
	private	FuelGroupingDAO	fuelGroupingDAO;
	
	@Resource
	private	WillowConfigDAO	willowConfigDAO;
	@Resource
	private	FuelTypeValuesDAO	fuelTypeValuesDAO;
	
	

	private List<SelectItem> availableYears;
	private List<SelectItem> availableModels;
	private List<SelectItem> availableMakes;
	private List<SelectItem> availableModelTypes;

	private List<SelectItem> availableScheduleTypes;
	private List<SelectItem> availableScheduleNames;
	private List<SelectItem> availableScheduleMiles;

	private List<SelectItem> availableFuelTypes;
	
	private String selectedYear;
	private String selectedMake;
	private String selectedModel;
	private String selectedModelType;
	private String selectedFuelType;

	private String selectedClientScheduleType;
	private String selectedScheduleMiles;
	private String selectedScheduleNames;
	
	private Long scheduleId;
	private Long scheduleRuleId;
	private MaintScheduleRule scheduleRule;	
	boolean baseScheduleIndicator;	
	private MasterSchedule masterSchedule;
	private ExternalAccount selectedExternalAccount = new ExternalAccount(new ExternalAccountPK());
	
	boolean activeFlag;
	boolean currentFlag;
	private static final String CUSTOMER_ACCOUNT_UI_ID = "customerAccount";
	private static final BigDecimal YEAR_LIMIT = new BigDecimal(1950);  // some years are halves such as 2015.5
	@MANotNull(label = "Client")
	private String customerLOVParam;
	
	private boolean showHiddenSchedules = false; 
	private boolean	hasEditPermission;
	private	List<ExternalAccount> clientAccounts ;
	private boolean	enableClientAccounts= false;
	private	final	String TYPE_CLIENT = "CLIENT";
	private	String	clientAccountCode ;
	private List<SelectItem> availableClients;
	private	boolean	enableInterval;
	private	boolean	enableScheduleName;
	private Map<String, Object> nextPageValues;
	
	private boolean	highMileageFlag;
	private boolean disableMake;
	private boolean	disableModel;
	private boolean	disableModelType;
	private	String	highMilegaeThreshold;
	
	@PostConstruct
	public void init() {
		logger.debug("init is called");     
		super.openPage();
		hasEditPermission = hasPermission();
		enableInterval	=	true;
		enableScheduleName	=	true;;
		setCustomerLOVParam(null);
		setupMaintScheduleRule();	
		clientAccounts = externalAccountDAO.findAccountsForClientScheduleType();
		availableClients = new ArrayList<SelectItem>();
		for (ExternalAccount externalAccount : clientAccounts) {
			SelectItem	item	= new SelectItem();
			String accountName = externalAccount.getAccountName() != null ? externalAccount.getAccountName(): "";
			item.setValue((String)externalAccount.getExternalAccountPK().getAccountCode());
			item.setLabel((String)externalAccount.getExternalAccountPK().getAccountCode()+"-"+accountName);
			availableClients.add(item);
		}
		setValuesIfHighMileage();
		WillowConfig	thresholdConfig = willowConfigDAO.findById("HIGH_MILEAGE_THRESHOLD").orElse(null);
		if(thresholdConfig != null){
			highMilegaeThreshold = NumberFormat.getInstance(Locale.US).format(new Long(thresholdConfig.getConfigValue())) ;
		}
		
	}
	private void setEnableDisableClient( String type){
		if(MALUtilities.isEmpty(type)){
			enableClientAccounts	= false;
			return;
		}
		if(TYPE_CLIENT.equals(type)){
			enableClientAccounts	= true;
			if(MALUtilities.isEmpty(getClientAccountCode())){
				enableInterval	= false;
				enableScheduleName	=	false;
			}
			
		}else{
			enableClientAccounts	= false;
			enableInterval	= true;
			enableScheduleName	=	true;
			setClientAccountCode(null);
			selectedExternalAccount = new ExternalAccount(new ExternalAccountPK());
		}
	}
	
	
	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Add/Edit Schedule Rule");
		thisPage.setPageUrl(ViewConstants.SCHEDULE_RULE_ADD_EDIT);
		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_SCHEDULE_RULE_ID) != null) {
			setScheduleRuleId((Long) thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_SCHEDULE_RULE_ID));
		}
	}	
	
	private void setupMaintScheduleRule() {
		if(scheduleRuleId != null) {
			
			//get rules information
			scheduleRule = maintenanceScheduleService.getMaintScheduleRule(scheduleRuleId);
			clientAccountCode	= scheduleRule.getScheduleAccount() != null ?  scheduleRule.getScheduleAccount().getExternalAccountPK().getAccountCode(): null;
			setSelectedExternalAccount(scheduleRule.getScheduleAccount());
			selectedYear = scheduleRule.getYear();			
			selectedModelType = scheduleRule.getModelTypeDesc();			
			if(!MALUtilities.isEmpty(selectedYear)){
				loadMakes(selectedYear);	
			}
			if(scheduleRule.getMakeCode() != null) {
				selectedMake = makeService.getMakesByCode(scheduleRule.getMakeCode()).get(0).getMakeDesc();
			}
			if(scheduleRule.getMakeModelDesc() != null) {
				
				selectedModel = scheduleRule.getMakeModelDesc();			
			}
			if(!MALUtilities.isEmpty(selectedYear) 
					&& !MALUtilities.isEmpty(selectedMake)
					&& !MALUtilities.isEmpty(selectedModel)){
				loadModels(selectedModelType, selectedYear, selectedMake);
			}
			selectedFuelType = scheduleRule.getFuelTypeGroup();
			
			//get Master Schedule information
			masterSchedule = scheduleRule.getMasterSchedule();
			showHiddenSchedules=  "Y".equals(scheduleRule.getMasterSchedule().getHiddenFlag())? true:false;
			selectedClientScheduleType = masterSchedule.getClientScheduleType().getScheduleType();
			selectedScheduleMiles = String.valueOf(masterSchedule.getDistanceFrequency());
			
			selectedExternalAccount = masterSchedule.getClientAccount();
			if(selectedExternalAccount == null)
				selectedExternalAccount = new ExternalAccount(new ExternalAccountPK());
			else
				selectedExternalAccount = masterSchedule.getClientAccount();
			
			loadIntervals(selectedClientScheduleType);
			selectedScheduleNames = masterSchedule.getMasterCode();
			loadScheduleNames(selectedClientScheduleType, selectedScheduleMiles);
			activeFlag = scheduleRule.getActiveFlag().equalsIgnoreCase("Y") ? true : false;
			baseScheduleIndicator = scheduleRule.getBaseSchedule().equalsIgnoreCase("Y") ? true : false;
			setEnableDisableClient(selectedClientScheduleType);
			scheduleId = scheduleRule.getMasterSchedule().getMschId();
			highMileageFlag = "Y".equalsIgnoreCase(scheduleRule.getHighMileage())? true:false;
			
			
		} else {
			scheduleRule = new MaintScheduleRule();
			activeFlag = true;
			selectedExternalAccount = new ExternalAccount(new ExternalAccountPK());
			baseScheduleIndicator = false;
			selectedYear = null;
			selectedMake = null;
			selectedModel = null;
			selectedModelType = null;
			selectedFuelType = null;
			selectedClientScheduleType = null;
			selectedScheduleMiles = null;
			selectedScheduleNames = null;
			clientAccountCode = null;
			highMileageFlag	= false;
		}
		
		loadYears();
		loadScheduleTypes();
		loadModelTypes();
		loadFuelTypes();
	
	}
	private void	setValuesIfHighMileage(){
		if(highMileageFlag){
			disableMake	= true;
			disableModel	= true;
			disableModelType	= true;
			selectedMake = null;
			selectedModel = null;
			selectedModelType = null;	
			selectedYear 	= "1977";
			
		}
	}
	public void  handleHighMileageSelect(){
		if(highMileageFlag){
			setValuesIfHighMileage();
			selectedFuelType = null;
			activeFlag	= true;
			baseScheduleIndicator	= true;
			
		}else{
			disableMake	= false;
			disableModel	= false;
			disableModelType	= false;
			selectedYear 	= null;
			
		}
	}
	
	private void loadYears() {
		availableYears = new ArrayList<SelectItem>();
		ModelSearchCriteriaVO searchCriteria = new ModelSearchCriteriaVO();
		for (String year : modelSearchService.findDistinctYears(searchCriteria)) {
			if(new BigDecimal(year).compareTo(YEAR_LIMIT) >= 0) {
				availableYears.add(new SelectItem(year));
			}
		}
	}


	private void loadScheduleTypes() {
		availableScheduleTypes = new ArrayList<SelectItem>();
		for (ClientScheduleType cst : maintenanceScheduleService.getClientScheduleTypes()) {
			availableScheduleTypes.add(new SelectItem(cst.getScheduleType()));
		}
	}

	
	private void loadModelTypes() {
		availableModelTypes = new ArrayList<SelectItem>();
		for (ModelType modelType : modelSearchService.getModelTypes()) {
			availableModelTypes.add(new SelectItem(modelType.getDescription()));
		}
	}

	private void loadFuelTypes() {
		availableFuelTypes = new ArrayList<SelectItem>();
		for (FuelGroupCode fuelGroupCode : maintenanceScheduleService.getFuelTypeGroupList()) {
			availableFuelTypes.add(new SelectItem(fuelGroupCode.getFuelGroupCode(), fuelGroupCode.getFuelGroupDescription()));
		}
	}

	private void loadMakes(String year) {
		availableMakes = new ArrayList<SelectItem>();
		ModelSearchCriteriaVO searchCriteria = new ModelSearchCriteriaVO();
		searchCriteria.setYear(year);
		for (String make : modelSearchService.findDistinctMakes(searchCriteria)) {
			availableMakes.add(new SelectItem(make));
		}
	}


	private void loadModels(String modelType, String year, String make) {
		Set<String> models = new HashSet<String>();
		List<Model> modelsList = modelDAO.findByModelTypeYearAndMake(modelType, year, make);
		for (Model model : modelsList) {
			models.add(model.getMakeModelRange().getDescription());
		}
		if (!models.isEmpty()) {
			availableModels = buildFilterList(models);
		}
	}

	
	private void populateModels() {
		if (availableModels != null) {
			availableModels.clear();
			setSelectedModel(null);
		}
		if (!MALUtilities.isEmpty(getSelectedModelType()) && !MALUtilities.isEmpty(getSelectedYear()) && !MALUtilities.isEmpty(getSelectedMake())) {
			loadModels(getSelectedModelType(), getSelectedYear(), getSelectedMake());
		}
	}

	
	private List<SelectItem> buildFilterList(Set<String> set) {
		ArrayList<String> sorted = new ArrayList<String>(set);
		Collections.sort(sorted);
		List<SelectItem> list = new ArrayList<SelectItem>();
		for (String s : sorted) {
			list.add(new SelectItem(s));
		}
		return list;
	}
	
	
	public void handleClientSelect(){
		if(!MALUtilities.isEmpty(getClientAccountCode())){
			for (ExternalAccount externalAccount : clientAccounts) {
				if (externalAccount.getExternalAccountPK().getAccountCode().equals(getClientAccountCode())) {
					setSelectedExternalAccount(externalAccountDAO.findById(externalAccount.getExternalAccountPK()).orElse(null));
					break;
				}
			}
			enableInterval	=	true;
			enableScheduleName	=	true;
		}else{
			selectedExternalAccount = new ExternalAccount(new ExternalAccountPK());
			enableInterval	=	false;
			enableScheduleName	=	false;
		}
		handleScheduleTypesSelect();
	}
	
	public void handleScheduleTypesSelect() {
		if (availableScheduleMiles != null)
			availableScheduleMiles.clear();
		setSelectedScheduleMiles(null);
		setEnableDisableClient(getSelectedClientScheduleType());
		if (!MALUtilities.isEmpty(getSelectedClientScheduleType())) {
			loadIntervals(getSelectedClientScheduleType());
		}
		
		handleIntervalSelect();
	}

	public void handleIntervalSelect() {
		if (availableScheduleNames != null)
			availableScheduleNames.clear();
		setSelectedScheduleNames(null);
		if (getSelectedScheduleMiles() != null) {
			loadScheduleNames(selectedClientScheduleType, selectedScheduleMiles);
		}
	}
	
	
	public void handleYearSelect() {
		if (availableMakes != null) {
			availableMakes.clear();
			setSelectedMake(null);
		}
		loadMakes(getSelectedYear());
		handleMakeSelect();
	}

	public void handleMakeSelect() {
		populateModels();
	}

	public void handleModelTypeSelect() {
		populateModels();
	}

	

	private void loadIntervals(String clientScheduleType) {
		Set<String> intervals = new HashSet<String>();
		if( TYPE_CLIENT.equals(getSelectedClientScheduleType())&&  MALUtilities.isEmpty(getClientAccountCode())){
			return;
		}
		Long	cId = null;
		String accountCode = null;
		String	accountType = null;
		if(selectedExternalAccount != null){
			cId = selectedExternalAccount.getExternalAccountPK() != null? selectedExternalAccount.getExternalAccountPK().getCId(): null;
			accountCode = selectedExternalAccount.getExternalAccountPK() != null? selectedExternalAccount.getExternalAccountPK().getAccountCode(): null;
			accountType = selectedExternalAccount.getExternalAccountPK() != null? selectedExternalAccount.getExternalAccountPK().getAccountType(): null;
		}
		List<MasterSchedule> list = maintenanceScheduleService.getMasterSchedulesByScheduleType(selectedClientScheduleType, cId, accountCode,
				accountType, showHiddenSchedules);
		for (MasterSchedule masterSchedule : list) {
			intervals.add(String.valueOf(masterSchedule.getDistanceFrequency()));
		}
		if (!intervals.isEmpty()) {
			availableScheduleMiles = buildFilterList(intervals);
		}
	}
	

	private void loadScheduleNames(String clientScheduleType, String miles ) {
		Set<String> scheduleNames = new HashSet<String>();
		if( TYPE_CLIENT.equals(getSelectedClientScheduleType())&&  MALUtilities.isEmpty(getClientAccountCode())){
			return;
		}
		Long	cId = null;
		String accountCode = null;
		String	accountType = null;
		if(selectedExternalAccount != null){
			cId = selectedExternalAccount.getExternalAccountPK() != null? selectedExternalAccount.getExternalAccountPK().getCId(): null;
			accountCode = selectedExternalAccount.getExternalAccountPK() != null? selectedExternalAccount.getExternalAccountPK().getAccountCode(): null;
			accountType = selectedExternalAccount.getExternalAccountPK() != null? selectedExternalAccount.getExternalAccountPK().getAccountType(): null;
		}
		List<MasterSchedule> list = maintenanceScheduleService.getMasterSchedulesByScheduleTypeAndInterval(selectedClientScheduleType, cId,
				accountCode, accountType, Integer.parseInt(miles), showHiddenSchedules);
		List<SelectItem> listSelectItem = new ArrayList<SelectItem>();
		for (MasterSchedule masterSchedule : list) {
			scheduleNames.add(masterSchedule.getMasterCode());
			SelectItem item = new SelectItem(masterSchedule.getMasterCode(), masterSchedule.getMasterCode() + " "+masterSchedule.getDescription());
			listSelectItem.add(item);
		}
		availableScheduleNames = listSelectItem;
	}
	
	public Long getSchedulePreviewBeforeSave() {
		if(selectedScheduleNames != null)
			return maintenanceScheduleService.getMasterScheduleByCode(selectedScheduleNames).get(0).getMschId();	
		else return null;
	}
	
	public String cancel(){
    	return super.cancelPage();      	
    }
	

	public String save(){
		String url = null;
		try {
			if(validToSave()) {
				if(saveRule()) {
					url=cancel();
				} 
			}
		} catch (Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		return url;
	}
		
	/*
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		if(scheduleRule != null) {
			restoreStateValues.put("SELECTED_CLIENT_SCHEDULE_TYPE", scheduleRule.getMasterSchedule().getClientScheduleType());			
		}
		
		return restoreStateValues;
	}
	*/
	
	private void setNextPageValues() {
		nextPageValues = new HashMap<String, Object>();
		if(scheduleRule != null) {				
			if(scheduleRule.getMasterSchedule() != null)
				nextPageValues.put(ViewConstants.VIEW_PARAM_SCHEDULE_ID, scheduleRule.getMasterSchedule().getMschId());
			if(scheduleRule.getMasterSchedule() == null && selectedScheduleNames != null)
				scheduleRule.setMasterSchedule(maintenanceScheduleService.getMasterScheduleByCode(selectedScheduleNames).get(0));
		}
		saveNextPageInitStateValues(nextPageValues);
	}
	
	private boolean validToSave() {
		boolean valid = true;
		if(selectedYear == null) {
			String yearId ="year"; 
			if(highMileageFlag){
				yearId = "year1977";
			}
			addErrorMessage(yearId, "custom.message", "Model Year is required");
			valid = false;
		}
		
		if(selectedClientScheduleType == null) {
			addErrorMessage("schedule", "custom.message", "Schedule Client Type is required");
			valid = false;
		}else{
			if(TYPE_CLIENT.equals(selectedClientScheduleType)){
				if(MALUtilities.isEmpty(getClientAccountCode())){
					addErrorMessage("client", "custom.message", "Client is required");
					valid = false;
				}
			}
		}
		if(selectedScheduleMiles == null) {
			addErrorMessage("scheduleMiles", "custom.message", "Schedule Interval is required");
			valid = false;
		}
		if(selectedScheduleNames == null) {
			addErrorMessage("scheduleNames", "custom.message", "Schedule Name is required");
			valid = false;
		}
		
		return valid;
	}	
	

	private void prepFields() {
		scheduleRule.setActiveFlag(activeFlag ? "Y" : "N");
		scheduleRule.setBaseSchedule(baseScheduleIndicator ? "Y" : "N");
		scheduleRule.setHighMileage(highMileageFlag ? "Y":"N");
		scheduleRule.setMasterSchedule(maintenanceScheduleService.getMasterScheduleByCode(selectedScheduleNames).get(0));
				
		scheduleRule.setYear(selectedYear);
		scheduleRule.setMakeModelDesc(selectedModel);
		scheduleRule.setFuelTypeGroup(selectedFuelType);
		/*if (!MALUtilities.isEmpty(selectedFuelType)) {

			scheduleRule.setFuelTypeGroup(selectedFuelType);

		} else {
			scheduleRule.setFuelTypeGroup(null);
		}*/
		scheduleRule.setModelTypeDesc(selectedModelType);
		
		if(selectedMake != null) {
			scheduleRule.setMakeCode(makeService.getMakeVOsByDescription(selectedMake, null).get(0).getMake().getMakeCode());
		}
		else {
			scheduleRule.setMakeCode(null);
		}
		
		if (MALUtilities.isEmpty(getClientAccountCode())) {
			scheduleRule.setScheduleAccount(null);
		} else {
			/*for (ExternalAccount externalAccount : clientAccounts) {
				if (externalAccount.getExternalAccountPK().getAccountCode().equals(getClientAccountCode())) {
					scheduleRule.setScheduleAccount(externalAccountDAO.findById(externalAccount.getExternalAccountPK()).orElse(null));
					break;
				}
			}*/
			scheduleRule.setScheduleAccount(getSelectedExternalAccount());
		}
	}
		
	private boolean saveRule() {
		try {
			prepFields();
			List<MaintScheduleRule>	 existingRules =  maintenanceScheduleService.getMatchingRules(scheduleRule,getSelectedClientScheduleType());
			if (existingRules != null && !existingRules.isEmpty()) {
				// check for duplicate rule if adding
				if (scheduleRule.getMscId() == null || scheduleRule.getMscId() == 0) {
					addErrorMessage("error.duplicate.schedulerule");
					return false;
				} else {
					// update
					boolean isDuplicate = false;
					for (MaintScheduleRule maintScheduleRule : existingRules) {
						if (maintScheduleRule.getMscId().longValue() != scheduleRule.getMscId().longValue()) {
							isDuplicate = true;
							break;
						}
					}
					if (isDuplicate) {
						addErrorMessage("error.duplicate.schedulerule");
						return false;
					}

				}
			}
			
			maintenanceScheduleService.saveRule(scheduleRule);
			addSuccessMessage("saved.success", "Maintenance Schedule Rule");
			return true;
		} catch (Exception ex) {
    		super.addErrorMessage("generic.error", ex.getMessage());
    		return false;
		}
	}
	
	public String getSelectedScheduleMiles() {
		return selectedScheduleMiles;
	}

	public void setSelectedScheduleMiles(String selectedScheduleMiles) {
		this.selectedScheduleMiles = selectedScheduleMiles;
	}

	public String getSelectedScheduleNames() {
		return selectedScheduleNames;
	}

	public void setSelectedScheduleNames(String selectedScheduleNames) {
		this.selectedScheduleNames = selectedScheduleNames;
	}
	
	public List<SelectItem> getAvailableScheduleNames() {
		return availableScheduleNames;
	}

	public void setAvailableScheduleNames(List<SelectItem> availableScheduleNames) {
		this.availableScheduleNames = availableScheduleNames;
	}

	public List<SelectItem> getAvailableScheduleMiles() {
		return availableScheduleMiles;
	}

	public void setAvailableScheduleMiles(List<SelectItem> availableScheduleMiles) {
		this.availableScheduleMiles = availableScheduleMiles;
	}
	
	public List<SelectItem> getAvailableScheduleTypes() {
		return availableScheduleTypes;
	}

	public void setAvailableScheduleTypes(List<SelectItem> availableScheduleTypes) {
		this.availableScheduleTypes = availableScheduleTypes;
	}
	
	public List<SelectItem> getAvailableYears() {
		return availableYears;
	}

	public void setAvailableYears(List<SelectItem> availableYears) {
		this.availableYears = availableYears;
	}

	public List<SelectItem> getAvailableModels() {
		return availableModels;
	}

	public void setAvailableModels(List<SelectItem> availableModels) {
		this.availableModels = availableModels;
	}

	public List<SelectItem> getAvailableMakes() {
		return availableMakes;
	}

	public void setAvailableMakes(List<SelectItem> availableMakes) {
		this.availableMakes = availableMakes;
	}

	public List<SelectItem> getAvailableModelTypes() {
		return availableModelTypes;
	}

	public void setAvailableModelTypes(List<SelectItem> availableModelTypes) {
		this.availableModelTypes = availableModelTypes;
	}

	public List<SelectItem> getAvailableFuelTypes() {
		return availableFuelTypes;
	}

	public void setAvailableFuelTypes(List<SelectItem> availableFuelTypes) {
		this.availableFuelTypes = availableFuelTypes;
	}
	
	public String getSelectedYear() {
		return selectedYear;
	}

	public void setSelectedYear(String selectedYear) {
		this.selectedYear = selectedYear;
	}

	public String getSelectedMake() {
		return selectedMake;
	}

	public void setSelectedMake(String selectedMake) {
		this.selectedMake = selectedMake;
	}

	public String getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(String selectedModel) {
		this.selectedModel = selectedModel;
	}

	public String getSelectedModelType() {
		return selectedModelType;
	}

	public void setSelectedModelType(String selectedModelType) {
		this.selectedModelType = selectedModelType;
	}

	public String getSelectedFuelType() {
		return selectedFuelType;
	}

	public void setSelectedFuelType(String selectedFuelType) {
		this.selectedFuelType = selectedFuelType;
	}

	public MaintenanceScheduleService getMaintenanceScheduleService() {
		return maintenanceScheduleService;
	}

	public void setMaintenanceScheduleService(
			MaintenanceScheduleService maintenanceScheduleService) {
		this.maintenanceScheduleService = maintenanceScheduleService;
	}
	
	public String getSelectedClientScheduleType() {
		return selectedClientScheduleType;
	}

	public void setSelectedClientScheduleType(String selectedClientScheduleType) {
		this.selectedClientScheduleType = selectedClientScheduleType;
	}
	
	public ExternalAccount getSelectedExternalAccount() {
		return selectedExternalAccount;
	}

	public void setSelectedExternalAccount(ExternalAccount externalAccount) {
		this.selectedExternalAccount = externalAccount;
	}

	@Override
	protected void restoreOldPage() {
	}	
		
	public Long getScheduleRuleId() {
		return scheduleRuleId;
	}

	public void setScheduleRuleId(Long scheduleRuleId) {
		this.scheduleRuleId = scheduleRuleId;
	}

	public MaintScheduleRule getScheduleRule() {
		return scheduleRule;
	}

	public void setScheduleRule(MaintScheduleRule scheduleRule) {
		this.scheduleRule = scheduleRule;
	}
	
	public boolean isActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}
	
	public boolean isBaseScheduleIndicator() {
		return baseScheduleIndicator;
	}

	public void setBaseScheduleIndicator(boolean baseScheduleIndicator) {
		this.baseScheduleIndicator = baseScheduleIndicator;
	}
	
	public MasterSchedule getMasterSchedule() {
		return masterSchedule;
	}

	public void setMasterSchedule(MasterSchedule baseSchedule) {
		this.masterSchedule = baseSchedule;
	}
	
	public String getCustomerLOVParam() {
		return customerLOVParam;
	}

	public void setCustomerLOVParam(String customerLOVParam) {
		this.customerLOVParam = customerLOVParam;
	}
	
	public ModelSearchService getModelSearchService() {
		return modelSearchService;
	}

	public void setModelSearchService(ModelSearchService modelSearchService) {
		this.modelSearchService = modelSearchService;
	}


	public boolean isShowHiddenSchedules() {
		return showHiddenSchedules;
	}


	public void setShowHiddenSchedules(boolean showHiddenSchedules) {
		this.showHiddenSchedules = showHiddenSchedules;
	}


	public boolean isHasEditPermission() {
		return hasEditPermission;
	}


	public void setHasEditPermission(boolean hasEditPermission) {
		this.hasEditPermission = hasEditPermission;
	}


	public boolean isEnableClientAccounts() {
		return enableClientAccounts;
	}


	public void setEnableClientAccounts(boolean enableClientAccounts) {
		this.enableClientAccounts = enableClientAccounts;
	}


	public String getClientAccountCode() {
		return clientAccountCode;
	}


	public void setClientAccountCode(String clientAccountCode) {
		this.clientAccountCode = clientAccountCode;
	}


	public List<SelectItem> getAvailableClients() {
		return availableClients;
	}


	public void setAvailableClients(List<SelectItem> availableClients) {
		this.availableClients = availableClients;
	}
	public boolean isEnableInterval() {
		return enableInterval;
	}
	public void setEnableInterval(boolean enableInterval) {
		this.enableInterval = enableInterval;
	}
	public boolean isEnableScheduleName() {
		return enableScheduleName;
	}
	public void setEnableScheduleName(boolean enableScheduleName) {
		this.enableScheduleName = enableScheduleName;
	}
	public Long getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public boolean isHighMileageFlag() {
		return highMileageFlag;
	}
	public void setHighMileageFlag(boolean highMileageFlag) {
		this.highMileageFlag = highMileageFlag;
	}
	public boolean isDisableMake() {
		return disableMake;
	}
	public void setDisableMake(boolean disableMake) {
		this.disableMake = disableMake;
	}
	public boolean isDisableModel() {
		return disableModel;
	}
	public void setDisableModel(boolean disableModel) {
		this.disableModel = disableModel;
	}
	public boolean isDisableModelType() {
		return disableModelType;
	}
	public void setDisableModelType(boolean disableModelType) {
		this.disableModelType = disableModelType;
	}
	public String getHighMilegaeThreshold() {
		return highMilegaeThreshold;
	}
	public void setHighMilegaeThreshold(String highMilegaeThreshold) {
		this.highMilegaeThreshold = highMilegaeThreshold;
	}
	
	
	
}