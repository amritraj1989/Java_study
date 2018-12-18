package com.mikealbert.vision.view.bean;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.MaintenanceCategory;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.vo.ServiceProviderDiscountVO;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintCodeSearchService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component
@Scope("view")
public class MaintainSupplierDiscountsBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 1459137993844538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());


	@Resource 
	private LookupCacheService lookupService;
	@Resource 
	private MaintCodeSearchService maintCodeSearchService; 
	@Resource 
	private ServiceProviderService serviceProviderService; 
	
	
	private List<ServiceProviderDiscountVO> rowList = new ArrayList<ServiceProviderDiscountVO>();
	private List<ServiceProviderDiscountVO> saveRowList = new ArrayList<ServiceProviderDiscountVO>();
	private List<MaintenanceCategory> maintCategories;
	private ServiceProvider serviceProvider;
	private String serviceProviderName;
	private String maintCode;
	private String maintCategory;
	private String serviceProviderId;
	private int totalRecords;
	Set<String> fullPriceTasks = new HashSet<String>();
	
	private int rowsPerPage = 150;
	private	boolean	hasPermission;

	@PostConstruct
	public void init() {
		super.openPage();
    	maintCategories = lookupService.getMaintenanceCategories();
		
		initializeDataTable(525, 790, new int[] { 20, 50, 20, 10});

	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Maintain Supplier Discounts");
		thisPage.setPageUrl(ViewConstants.MAINTAIN_SUPPLIER_DISCOUNTS);
	}

	@Override
	protected void restoreOldPage() {
	}	
	
	private void loadRows() {
		rowList = maintCodeSearchService.getServiceProviderDiscountList(serviceProviderName, maintCode, maintCategory);
		recordFullPriceTasks();
		setTotalRecords(rowList.size());
	}

	public void save(){
		saveRowList.clear();
		for(ServiceProviderDiscountVO spdVO : rowList) {
			if(spdVO.isDiscount() && fullPriceTasks.contains(spdVO.getMaintCode())) {
				saveRowList.add(spdVO);
				fullPriceTasks.remove(spdVO.getMaintCode());
			} else if(!spdVO.isDiscount() && !fullPriceTasks.contains(spdVO.getMaintCode())) {
				saveRowList.add(spdVO);
				fullPriceTasks.add(spdVO.getMaintCode());
			}
		}

		try {
			serviceProviderService.updateServiceProviderDiscounts(serviceProvider.getServiceProviderId(), saveRowList);
			super.addSuccessMessage("process.success","Save MAFS/Provider Maint Code discounts ");
		} catch(Exception e) {
			addSimplErrorMessage("No codes saved - " + e.getMessage());
		}
    }
	
	
	public String cancel(){
    	return super.cancelPage();      	
    }
	
	
	public void performSearch() {
		rowList = null;
		if(isProviderResolved()) {
			loadRows();
		} else {
			addErrorMessage("providerFilter", "custom.message", "A valid service provider is required");
		}
	}

	private void recordFullPriceTasks() {
		fullPriceTasks.clear();
		for(ServiceProviderDiscountVO spdVO : rowList) {
			if(!spdVO.isDiscount()) {
				fullPriceTasks.add(spdVO.getMaintCode());
			}
		}
	}
	
	private boolean isProviderResolved() {
		boolean resolved = false;
		if(MALUtilities.isNotEmptyString(serviceProviderName)) {
			if(serviceProviderService.getServiceProviderByExactName(serviceProviderName).size() == 1) {
				serviceProvider = serviceProviderService.getServiceProviderByExactName(serviceProviderName).get(0);
				serviceProviderName = serviceProvider.getServiceProviderName();
				resolved = true;
			}
		}
		return resolved;
	}

	
	public boolean isHasPermission() {
		return hasPermission;
	}

	public void setHasPermission(boolean hasPermission) {
		this.hasPermission = hasPermission;
	}


	public List<MaintenanceCategory> getMaintCategories() {
		return maintCategories;
	}

	public void setMaintCategories(List<MaintenanceCategory> maintCategories) {
		this.maintCategories = maintCategories;
	}

	public String getServiceProviderName() {
		return serviceProviderName;
	}

	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}

	public String getMaintCode() {
		return maintCode;
	}

	public void setMaintCode(String maintCode) {
		this.maintCode = maintCode;
	}

	public String getMaintCategory() {
		return maintCategory;
	}

	public void setMaintCategory(String maintCategory) {
		this.maintCategory = maintCategory;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public List<ServiceProviderDiscountVO> getRowList() {
		return rowList;
	}

	public void setRowList(List<ServiceProviderDiscountVO> rowList) {
		this.rowList = rowList;
	}

	public String getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	
}