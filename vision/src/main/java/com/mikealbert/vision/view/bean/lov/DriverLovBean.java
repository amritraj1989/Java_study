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
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.vo.DriverLOVVO;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("driverLovBean")
@Scope("view")
public class DriverLovBean extends BaseBean {
	
	private static final long serialVersionUID = 1L;
	private MalLogger logger = MalLoggerFactory.getLogger(DriverLovBean.class);
	
	private int selectionIndex = -1;
	
	private DriverLOVVO selectedDriver;
	private String driverId;
	private String driverSurname;
	private String driverFirstName;
	private String driverFullName;
	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private List<String> customerCodeList;
	private String customerType = null;
	private String customerContext = null;	 
	private String customerName;
	private String dataTableMessage;	
	private String fleetMasterId;
	private String unitNumber;	
	private String pageTitle = "Select driver";;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	boolean isStart = true;
	private String driverName;
	private int pageNumber = 0;
	private List<DriverLOVVO> resultList;	
	private LazyDataModel<DriverLOVVO> drivers;
	private int lovRecordsPerPage;
	
	private boolean fetchRelatedDriver = true;	
	private boolean fetchOpenCustomerOnly = false;
	private boolean fetchActiveDriverOnly = false;
	
	@Resource private DriverService driversService;
	@Resource private CustomerAccountService customerAccountService;

	@PostConstruct
	@SuppressWarnings("serial")
	public void init() {
		try{
			setLovRecordsPerPage();
			isStart = true;
			resultList = new ArrayList<DriverLOVVO>();
			drivers = new LazyDataModel<DriverLOVVO>() {
				@Override
				public List<DriverLOVVO> load(int first, int pageSize, String arg2, SortOrder arg3, Map<String, Object> arg4) {
					selectedDriver = null;
					if (isStart) {
						first = 0;
						isStart = false;
					}
					if(! initFlag){
						setPageSize(ViewConstants.LOV_RECORD_SIZE);
						resultList = getLazyDriversList(first, pageSize);
					}
					return resultList;

				}
				
				@Override
				public DriverLOVVO getRowData(String rowKey) {
					for (DriverLOVVO driver : resultList) {
						if (String.valueOf(driver.getDrvId()).equals(rowKey))
							return driver;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(DriverLOVVO driver) {
					return driver.getDrvId();
				}
			};
			drivers.setPageSize(ViewConstants.RECORDS_PER_PAGE);
			logger.info("Method:init, Obtained drivers: {}", new Object[] { drivers.getRowCount() });

		}
		catch(Exception ex){
					handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
				}
	}

	private List<DriverLOVVO> getLazyDriversList(int first, int size) {
		try {
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				setPageNumber(page.getPageNumber());
				
				resultList = driversService.searchLOVDriver(driverName, customerCodeList, customerType, customerContext, fetchOpenCustomerOnly, fetchRelatedDriver, fetchActiveDriverOnly, page);	
				drivers.setPageSize(rowsPerPage);
				
				if (execCountQuery){
					int count = driversService.searchLOVDriverCount (driverName, customerCodeList, customerType, customerContext, fetchOpenCustomerOnly,fetchRelatedDriver, fetchActiveDriverOnly);
					drivers.setRowCount(count);
					drivers.setWrappedData(resultList);
					execCountQuery = false;
				}				
				
				if (resultList == null || resultList.size() == 0) {	
					this.setDataTableMessage(talMessage.getMessage("no.records.found"));
				}			

		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyCustomersListByName");
		}
		return resultList;
	}

	public void fetchLOVData(ActionEvent actionEvent) {
		
		isStart = true;
		this.selectionIndex = 0;
		this.pageNumber = 0;
		
		String driverNameParam = (String) getRequestParameter("DRIVER_NAME_INPUT");
		if (!MALUtilities.isEmptyString(driverNameParam)) {
			this.driverName = ( (String)  getRequestParameter(driverNameParam));
		}

		String customerCodeParam = (String) getRequestParameter("CUSTOMER_CODE_INPUT");
		if (!MALUtilities.isEmptyString(customerCodeParam)) {
			
			String customerCode = (String) getRequestParameter(customerCodeParam);
			this.pageTitle = "Select driver for customer " + customerCode;
			this.customerType = ExternalAccountDAO.ACCOUNT_TYPE_CUSTOMER;
			this.customerContext = ExternalAccountDAO.ACCOUNT_CONTEXT_MAL;
			customerCodeList = new ArrayList<String>();
			
			String fetchRelatedCustomerParam = getRequestParameter("FETCH_RELATED_CUSTOMER_ACCOUNT");
			if (!MALUtilities.isEmptyString(fetchRelatedCustomerParam)) {
				if (fetchRelatedCustomerParam.equals("Y")) {
					customerCodeList = customerAccountService.getReleatedAccountCodes(customerCode);
				} else if (fetchRelatedCustomerParam.equals("N")) {
					customerCodeList.add(customerCode);
				}
			}
		}
		
		String fetchRelatedDriverParam = (String) getRequestParameter("DONOT_FETCH_RELATED_DRIVERS");
		if (!MALUtilities.isEmptyString(fetchRelatedDriverParam)) {
			this.fetchRelatedDriver =  false; // default is true
		}		
		
		String fetchOpenCutomerOnlyParam = (String) getRequestParameter("FETCH_OPEN_CUSTOMER_ONLY");
		if (!MALUtilities.isEmptyString(fetchOpenCutomerOnlyParam)) {
			this.fetchOpenCustomerOnly = true;// default is false
		}
		
		String activeIndParam = getRequestParameter("FETCH_ACTIVE_DRIVER_ONLY");
		if (!MALUtilities.isEmptyString(activeIndParam)) {
			this.fetchActiveDriverOnly  =  true; // default is false
		}	
		
		
		
		execCountQuery = true;
		initFlag = false;

	}

	public void setSelectedDriver(DriverLOVVO selectedDriver) {
		this.selectedDriver = selectedDriver;
		selectionIndex = resultList.indexOf(this.selectedDriver) + (this.rowsPerPage * this.pageNumber);
		if (selectedDriver != null) {
			driverSurname = selectedDriver.getDriverSurname();
			driverFirstName = selectedDriver.getDriverForename();
			driverFullName = MALUtilities.getPersonFormattedName(driverSurname, driverFirstName);
			driverId = (selectedDriver.getDrvId() != null ? selectedDriver.getDrvId().toString() : null);
			customerName = selectedDriver.getAccountName();
		}

	}

	@SuppressWarnings("unchecked")
	public void setSelectedDriver() {

		String index = (String) getRequestParameter("selectedDrvIndex");

		if (!MALUtilities.isEmpty(index)) {
			Integer intIndex = Integer.parseInt(index);
			intIndex = intIndex % rowsPerPage;
			setSelectedDriver(resultList!= null && !resultList.isEmpty()  ? resultList.get(intIndex):null);
		} else {
			if (!resultList.isEmpty())
				setSelectedDriver(resultList.get(0));
		}

	}

	public void onRowSelect(SelectEvent event) {
		DriverLOVVO Driver = (DriverLOVVO) event.getObject();
		this.setSelectedDriver(Driver);
	}

	public DriverLOVVO getSelectedDriver() {
		return selectedDriver;
	}
	public LazyDataModel<DriverLOVVO> getDrivers() {
		return drivers;
	}

	public void setDrivers(LazyDataModel<DriverLOVVO> drivers) {
		this.drivers = drivers;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getFleetMasterId() {
		return fleetMasterId;
	}

	public void setFleetMasterId(String fleetMasterId) {
		this.fleetMasterId = fleetMasterId;
	}
	
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getDriverFirstName() {
		return driverFirstName;
	}

	public void setDriverFirstName(String driverFirstName) {
		this.driverFirstName = driverFirstName;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getDriverFullName() {
		return driverFullName;
	}

	public void setDriverFullName(String driverFullName) {
		this.driverFullName = driverFullName;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public String getDriverSurname() {
		return driverSurname;
	}

	public void setDriverSurname(String driverSurname) {
		this.driverSurname = driverSurname;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public String getDataTableMessage() {
		return dataTableMessage;
	}

	public void setDataTableMessage(String dataTableMessage) {
		this.dataTableMessage = dataTableMessage;
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}	
	
	public int getLovRecordsPerPage() {
		return lovRecordsPerPage;
	}

	public void setLovRecordsPerPage() {
		this.lovRecordsPerPage = ViewConstants.LOV_RECORD_SIZE;
	}



}
