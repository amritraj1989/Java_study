package com.mikealbert.vision.view.bean;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.vo.ProviderMaintCodeSearchResultLineVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.MaintenanceCodeService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.lov.ServiceProviderLovBean;


@Component
@Scope("view")
public class AddSupplierMaintCodeBean extends StatefulBaseBean{
	private static final long serialVersionUID = 5957629830511242337L;
	
	@Resource ServiceProviderService serviceProviderService;
	@Resource MaintenanceCodeService maintenanceCodeService;
	
	final static String SERVICE_PROVIDER_UI_ID = "providerAdd";
	
	private ServiceProviderMaintenanceCode providerMaintenanceCode;
	private MaintenanceCode maintenanceCode;
	private ServiceProvider serviceProvider;/*
	private ServerviceProviderLovBean serverviceProviderLovBean;*/
	private boolean isStay = false;

	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	super.openPage();
    	providerMaintenanceCode = new ServiceProviderMaintenanceCode();
    	maintenanceCode = new MaintenanceCode();
    	serviceProvider = new ServiceProvider();
    }
    
	@Override
	protected void loadNewPage() {
		super.thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_ADD_PROVIDER_MAINT_CODE);
		super.thisPage.setPageUrl(ViewConstants.ADD_PROVIDER_MAINT_CODE);	
		Map<String, Object> map = super.thisPage.getInputValues();
	}


	@Override
	protected void restoreOldPage() {
		Map<String, Object> map = super.thisPage.getRestoreStateValues();

	}
	
	public String cancel(){
		return cancelPage(); 
	}
	
	public String save() {
		String nextPage = null;
		
		if(isValid()){
			try{
				// code should always be trimmed and uppercase
				providerMaintenanceCode.setCode(providerMaintenanceCode.getCode().trim().toUpperCase());
				providerMaintenanceCode.setServiceProvider(serviceProvider);
				providerMaintenanceCode.setMaintenanceCode(maintenanceCode);
				providerMaintenanceCode.setApprovedBy(this.getLoggedInUser().getEmployeeNo());
				providerMaintenanceCode.setApprovedDate(new Date());
				providerMaintenanceCode.setDiscountFlag("Y");
				ServiceProviderMaintenanceCode savedCode = maintenanceCodeService.saveServiceProviderMaintCode(providerMaintenanceCode);
				super.addSuccessMessage("process.success"," Add New Provider Maintenance Code : " + savedCode.getCode());
				nextPage = super.cancelPage();
			} catch (MalBusinessException e) {
				addSimplErrorMessage(e.getMessage());
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
			} catch(MalException ex){
				handleException("generic.error",new String[]{ex.getMessage()}, ex, null);
				RequestContext context = RequestContext.getCurrentInstance();
				context.addCallbackParam("failure", true);
			}
		}

		return nextPage;
	}

	
	public boolean isValid(){
		boolean valid = false;
		
		boolean providerDecoded = decodeServiceProvider(serviceProvider.getServiceProviderNumber());	
		boolean maintCodeDecoded = decodeMaintCodeByNameOrCode(maintenanceCode.getCode());

		if(providerDecoded && maintCodeDecoded){
			valid = true;
		}
		
		return valid;
	}
	
	private boolean decodeServiceProvider(String nameOrCode){
		boolean usedLov = (!MALUtilities.isEmpty(serviceProvider.getServiceProviderId()));
		
		PageRequest page1;
		
		if(usedLov){
			page1 = new PageRequest(0,5000);
		}else{
			page1 = new PageRequest(0,2);
		}
		if(MALUtilities.isNotEmptyString(serviceProvider.getServiceProviderNumber())){
			List<ServiceProvider> providers = serviceProviderService.getServiceProviderByNameOrCode(serviceProvider.getServiceProviderNumber(),true, page1);

				// check for new (decode) service provider
				if(providers.size() == 1){
					serviceProvider = providers.get(0);
				// invalid
				}else if (providers.size() == 0) { 
					addErrorMessage(SERVICE_PROVIDER_UI_ID, "decode.noMatchFound.msg","Provider Name or Code " + serviceProvider.getServiceProviderNumber());
					return false;
				// multiple service providers on save
				}else { 
					if(usedLov){
						// if returned "list" and has the LOV value in it, then assume no change and reset to LOV value		
						for(ServiceProvider provider : providers){
							if(serviceProvider.getServiceProviderNumber().equalsIgnoreCase(provider.getServiceProviderNumber()) && serviceProvider.getServiceProviderName().equalsIgnoreCase(provider.getServiceProviderName())){
								serviceProvider = provider;
								return true;
							}
						}
						// otherwise raise error
						addErrorMessage(SERVICE_PROVIDER_UI_ID,"decode.multipleMatchesFound.msg", "Provider Name or Code " + serviceProvider.getServiceProviderNumber());	
						return false;			
					}else{
						addErrorMessage(SERVICE_PROVIDER_UI_ID,"decode.multipleMatchesFound.msg", "Provider Name or Code " + serviceProvider.getServiceProviderNumber());	
						return false;						
					}
					

				}
				
				
		}else{
			addErrorMessage(SERVICE_PROVIDER_UI_ID,"required.field", "Provider Name or Code " + serviceProvider.getServiceProviderNumber());
			return false;
		}
		return true;
	}
	
	private boolean decodeMaintCodeByNameOrCode(String nameOrCode){
		//lookup the maintenance code by nameOrCode
		PageRequest page1 = new PageRequest(0,2);
		List<MaintenanceCode> maintCodes = maintenanceCodeService.findMaintenanceCodesByNameOrCode(nameOrCode,page1);
		// if there is only one then set it in the list
		if(maintCodes.size() == 1){
			maintenanceCode = maintCodes.get(0);
			return true;
		}else if (maintCodes.size() == 0) {
			addErrorMessage("decode.noMatchFound.msg","Maintenance Code ");
			return false;
		}else {
			addErrorMessage("decode.multipleMatchesFound.msg", "Maintenance Code ");
			return false;
		}
	}
	


	public ServiceProviderMaintenanceCode getProviderMaintenanceCode() {
		return providerMaintenanceCode;
	}




	public void setProviderMaintenanceCode(
			ServiceProviderMaintenanceCode providerMaintenanceCode) {
		this.providerMaintenanceCode = providerMaintenanceCode;
	}




	public MaintenanceCode getMaintenanceCode() {
		return maintenanceCode;
	}




	public void setMaintenanceCode(MaintenanceCode maintenanceCode) {
		this.maintenanceCode = maintenanceCode;
	}




	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}




	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
		


	

}
