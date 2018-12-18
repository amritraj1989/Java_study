package com.mikealbert.vision.view.bean.lov;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.SupplierServiceTypeCodes;
import com.mikealbert.data.vo.ServiceProviderLOVVO;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.service.SuppServiceTypeCodeService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("serviceProviderLovBean")
@Scope("view")
public class ServiceProviderLovBean extends BaseBean  {

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	
	
	private LazyDataModel<ServiceProviderLOVVO> serviceProviders;
	private String serviceProviderNumber;
	private String serviceProviderName;
	private String serviceProviderId;
	private String payeeName;
	private String payeeNumber;
	

	private ServiceProviderLOVVO selectedServiceProvider;

	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private int rowsPerPage = ViewConstants.RECORDS_PER_PAGE_SMALL;
	boolean isStart = true;
	List<ServiceProviderLOVVO> resultList;
	private String provider;
	private String zipCode;
	private String payee;
	private String phoneNumber;
	private	String serviceTypeCode;
	private boolean includeOnlyParents = false;
	private int pageNumber = 0;
	private int lovRecordsPerPage;
	List<SupplierServiceTypeCodes> serviceTypeCodeList = new ArrayList<SupplierServiceTypeCodes>();
	@Autowired
	private ServiceProviderService serviceProviderService;
	
	@Resource
	private	SuppServiceTypeCodeService	suppServiceTypeCodeService;

	private String dataTableMessage;
	
	@PostConstruct
	public void init() {
		try{
			logger.info("-- Method name: Init end, Obtained Service Providers");
			
			setLovRecordsPerPage();
			serviceTypeCodeList	= suppServiceTypeCodeService.getSuppServiceTypeCodes(null);
			isStart = true;			
			resultList = new ArrayList<ServiceProviderLOVVO>();
			serviceProviders = new LazyDataModel<ServiceProviderLOVVO>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<ServiceProviderLOVVO> load(int first, int pageSize, String arg2, SortOrder arg3, Map<String, Object> arg4) {
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}

					if (isStart == false) {
						resultList = getLazyServiceProvidersListByName(first, pageSize);
					}
					
					return resultList;
				}
				
				@Override
				public ServiceProviderLOVVO getRowData(String rowKey) {
					for (ServiceProviderLOVVO serviceProvider : resultList) {
						if (String.valueOf(serviceProvider.getServiceProviderId()).equals(rowKey))
							return serviceProvider;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(ServiceProviderLOVVO serviceProvider) {
					return serviceProvider.getServiceProviderId();
				}
			};
			serviceProviders.setPageSize(ViewConstants.RECORDS_PER_PAGE);
			logger.info("Method name: Init end, Obtained Service Providers:", new Object[] { serviceProviders.getRowCount() });
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
	}
	
	public void fetchLOVDataByServiceProviderName(ActionEvent ae) {
		
		String serviceProviderParam = (String) getRequestParameter("SERVICEPROVIDER_LOV_INPUT");
		String serviceTypeCodeParam = (String) getRequestParameter("SERVICETYPECODE_LOV_INPUT");
		
		String includeParentsOnlyParam = (String) getRequestParameter("INCLUDEPARENTSONLY_LOV_INPUT");
		if (!MALUtilities.isEmptyString(includeParentsOnlyParam)) {
			this.includeOnlyParents =  true; // default is false
		}	
		
		this.serviceProviderName = null;
		this.serviceProviderNumber = null;
		this.serviceProviderId = null;
		this.payeeName = null;
		this.payeeNumber = null;
		this.provider = null;
		this.zipCode = null;
		this.payee = null;
		this.phoneNumber = null;
		this.pageNumber = 0;
		
		if (!MALUtilities.isEmptyString(serviceProviderParam)) {
			this.setProvider((String) getRequestParameter(serviceProviderParam));
		}
		
		if (!MALUtilities.isEmptyString(serviceTypeCodeParam)) {
			this.setServiceTypeCode((String) getRequestParameter(serviceTypeCodeParam));
		}
		
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		logger.info("Searched serviceProvider: " + this.provider);
		List<ServiceProviderLOVVO> list = getLazyServiceProvidersListByName(0, rowsPerPage);
		serviceProviders.setWrappedData(list);
		
		if (list.size() == 0) {
			this.setDataTableMessage(talMessage.getMessage("no.records.found.for", " Consider changing your Service Type."));
		}
		
		serviceProviders.setRowIndex(0);
		execCountQuery = false;
	}
	
	public void refresh() {
		
		this.pageNumber = 0;
		
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		List<ServiceProviderLOVVO> list = getLazyServiceProvidersListByName(0, rowsPerPage);
		serviceProviders.setWrappedData(list);
		
		if (list.size() == 0) {
			this.setDataTableMessage(talMessage.getMessage("no.records.found.for", "the criteria entered, Consider changing your Service Type"));
		}
		
		serviceProviders.setRowIndex(0);
		execCountQuery = false;
	}
	
	
	private List<ServiceProviderLOVVO> getLazyServiceProvidersListByName(int first, int size) {
		try{
			if (initFlag == false) {
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				setPageNumber(page.getPageNumber());
				resultList = serviceProviderService.getAllServiceProviders(provider,payee,zipCode,phoneNumber,serviceTypeCode,includeOnlyParents,page);
				
				serviceProviders.setPageSize(rowsPerPage);
				if (execCountQuery){
					int count = serviceProviderService.getAllServiceProvidersCount(provider,payee,zipCode,phoneNumber,serviceTypeCode,includeOnlyParents);
					serviceProviders.setRowCount(count);					
				}
			}

			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyCustomersListByName");
		}
		return resultList;
		
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getServiceProviderName() {
		return serviceProviderName;
	}

	public void setServiceProviderName(String serviceProviderName) {
		this.serviceProviderName = serviceProviderName;
	}

	public LazyDataModel<ServiceProviderLOVVO> getServiceProviders() {
		return serviceProviders;
	}

	public void setServiceProviders(
			LazyDataModel<ServiceProviderLOVVO> serviceProviders) {
		this.serviceProviders = serviceProviders;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public ServiceProviderLOVVO getSelectedServiceProvider() {
		return selectedServiceProvider;
	}
	
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	
	public void setSelectedServiceProvider(ServiceProviderLOVVO selectedServiceProvider) {
		logger.info("-- Method name: setSelectedServiceProvider start, Service Providers lov");
		
		this.selectedServiceProvider = selectedServiceProvider;
		
		if(selectedServiceProvider != null){
			serviceProviderName = selectedServiceProvider.getServiceProviderName();
			serviceProviderNumber = selectedServiceProvider.getServiceProviderNumber();
			serviceProviderId = selectedServiceProvider.getServiceProviderId().toString();
			payeeName = selectedServiceProvider.getPayeeName();
			payeeNumber = selectedServiceProvider.getPayeeNumber();
		}
		logger.info("-- Method name: setSelectedServiceProvider start, Service Providers lov");
		
	}

	public void onRowSelect(SelectEvent event) {
		ServiceProviderLOVVO selectedServiceProvider = (ServiceProviderLOVVO) event.getObject();
		setSelectedServiceProvider(selectedServiceProvider);
	}
	
	public int getLovRecordsPerPage() {
		return lovRecordsPerPage;
	}

	public void setLovRecordsPerPage() {
		this.lovRecordsPerPage = ViewConstants.RECORDS_PER_PAGE_SMALL;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getServiceProviderNumber() {
		return serviceProviderNumber;
	}

	public void setServiceProviderNumber(String serviceProviderNumber) {
		this.serviceProviderNumber = serviceProviderNumber;
	}

	public String getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(String serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}
	
	public String getPayeeName() {
		return payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	
	public String getPayeeNumber() {
		return payeeNumber;
	}

	public void setPayeeNumber(String payeeNumber) {
		this.payeeNumber = payeeNumber;
	}

	public String getServiceTypeCode() {
		return serviceTypeCode;
	}

	public void setServiceTypeCode(String serviceTypeCode) {
		this.serviceTypeCode = serviceTypeCode;
	}
	
	public boolean isIncludeOnlyParents() {
		return includeOnlyParents;
	}

	public void setIncludeOnlyParents(boolean includeOnlyParents) {
		this.includeOnlyParents = includeOnlyParents;
	}

	public List<SupplierServiceTypeCodes> getServiceTypeCodelist() {
		return serviceTypeCodeList;
	}

	public void setServiceTypeCodeList(List<SupplierServiceTypeCodes> serviceTypeCodeList) {
		this.serviceTypeCodeList = serviceTypeCodeList;
	}
	
	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}


	
}
