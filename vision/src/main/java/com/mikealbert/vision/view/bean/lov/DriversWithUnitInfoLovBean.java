package com.mikealbert.vision.view.bean.lov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.vo.DriverInfoVO;
import com.mikealbert.data.vo.VehicleOrderStatusSearchCriteriaVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.QuoteRequestService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.OrderProgressService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("driversWithUnitInfoLovBean")
@Scope("view")
public class DriversWithUnitInfoLovBean extends BaseBean  {

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	
	private DriverInfoVO selectedDriver;
	private List<DriverInfoVO> resultList;	
	private LazyDataModel<DriverInfoVO> drivers;
	
	private String driverNameInput;
	private String customerCode;
	private String customerName;
	private String customerType;
	private long   customerCId; 
	private String selectedUnitFactoryOptionalEquipments;
	private List<String> selectedUnitStandardEquipments;
	private List<String> selectedUnitDealerAccessories;

	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	boolean isStart = true;
	private int pageNumber = 0;
	private int lovRecordsPerPage;
	@Autowired
	private DriverService driverService;
	@Resource private OrderProgressService orderProgressService;
	@Resource QuotationService quotationService;
	@Resource DocDAO docDAO;
	@Resource QuoteRequestService quoteRequestService;

	@PostConstruct
	public void init() {
		try{
			setLovRecordsPerPage();
			isStart = true;
			resultList = new ArrayList<DriverInfoVO>();
			drivers = new LazyDataModel<DriverInfoVO>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<DriverInfoVO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
					selectedDriver = null;
					if (isStart) {
						first = 0;
						isStart = false;	
					}
					if(! initFlag){
						setPageSize(ViewConstants.LOV_RECORD_SIZE);
						resultList = getLazyDriversInfoList(first, getPageSize());
					}
					return resultList;

				}
				
				@Override
				public DriverInfoVO getRowData(String rowKey) {
					for (DriverInfoVO driver : resultList) {
						if ((String.valueOf(driver.getDrvId()).concat("-").concat(MALUtilities.getNullSafeString(driver.getAllocatedUnit()))).equals(rowKey))
							return driver;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(DriverInfoVO driver) {
					return driver.getDrvId();
				}
			};
			drivers.setPageSize(ViewConstants.RECORDS_PER_PAGE);
			logger.info("Method:init, Obtained drivers: {}", new Object[] { drivers.getRowCount() });
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
	}
	
	public void fetchLOVDataByDriverName(ActionEvent ae) {
		String paramName = (String) getRequestParameter("DRIVER_NAME_INPUT");
		this.customerCode = null;
		this.customerName = null;
		this.customerType = null;
		this.customerCId = 0l;
		this.pageNumber = 0;
		
		this.driverNameInput = (String) getRequestParameter(paramName);
		
		this.customerCode = (String) getRequestParameter("CLIENT_ACC_CODE");
		this.customerType = (String) getRequestParameter("CLIENT_ACC_TYPE");
		this.customerCId = (!MALUtilities.isEmpty(getRequestParameter("CLIENT_ACC_C_ID"))) ? Long.valueOf(getRequestParameter("CLIENT_ACC_C_ID")) : 0l;
		
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		logger.info("Searched driver info: " + this.driverNameInput);
		List<DriverInfoVO> list = getLazyDriversInfoList(0, rowsPerPage);
		drivers.setWrappedData(list);
		drivers.setRowIndex(0);
		execCountQuery = false;
	}
	
	private List<DriverInfoVO> getLazyDriversInfoList(int first, int size) {
		try {
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				setPageNumber(page.getPageNumber());
				
				resultList = driverService.searchDriverInfo(driverNameInput, null, null, new ExternalAccountPK(customerCId, customerType, customerCode), page, null);
				drivers.setPageSize(rowsPerPage);
				
				if (execCountQuery){
					int count = driverService.searchDriverInfoCount(driverNameInput, new ExternalAccountPK(customerCId, customerType, customerCode));
					drivers.setRowCount(count);
					drivers.setWrappedData(resultList);
					execCountQuery = false;
				}				

		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyDriversInfoList");
		}
		return resultList;
	}

	public void onRowSelect(SelectEvent event) {
		DriverInfoVO selectedDriverInfoVO = (DriverInfoVO) event.getObject();
		setSelectedDriver(selectedDriverInfoVO);
	}
	
	public void populateSelectedUnitEquipments(DriverInfoVO driverInfoVO) {
	    setSelectedUnitStandardEquipments(new ArrayList<String>());
	    setSelectedUnitFactoryOptionalEquipments(null);
	    setSelectedUnitDealerAccessories(new ArrayList<String>());
	    
	    if(!MALUtilities.isEmpty(driverInfoVO.getQmdId())){
	    	setSelectedUnitStandardEquipments(quotationService.getStandardAccessories(driverInfoVO.getQmdId())); // Standard equipments
	    	try {
				QuotationModel qmd = quoteRequestService.getQuotationModelWithDealerAccessories(driverInfoVO.getQmdId()); // dealer accessories
				if(!MALUtilities.isEmpty(qmd) && qmd.getQuotationDealerAccessories() != null && qmd.getQuotationDealerAccessories().size() > 0){
					for(QuotationDealerAccessory qda: qmd.getQuotationDealerAccessories()){
						selectedUnitDealerAccessories.add(qda.getDealerAccessory().getDealerAccessoryCode().getDescription());
					}
				}
			} catch (MalBusinessException e1) {
				e1.printStackTrace();
			}
	    	
	    	if(selectedUnitDealerAccessories != null && selectedUnitDealerAccessories.size() > 0){
		    	Collections.sort(selectedUnitDealerAccessories, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						if(MALUtilities.isEmptyString(o1) || MALUtilities.isEmptyString(o2)) {
							return MALUtilities.isEmptyString(o1) ? 1 : -1;
						} else {
							return o1.compareTo(o2);
						}
					}
				});
	    	}
	    	
	    	VehicleOrderStatusSearchCriteriaVO searchCriteria = new VehicleOrderStatusSearchCriteriaVO();
			searchCriteria.setUnitNo(driverInfoVO.getAllocatedUnit());
			try {
				Doc doc = docDAO.searchMainPurchaseOrder(searchCriteria);
				if(doc != null){
					setSelectedUnitFactoryOptionalEquipments(orderProgressService.getOptionalAccessories(doc.getDocId())); // optional equipments
				}
			} catch (MalBusinessException e) {
				e.printStackTrace();
			}
	    }
	}

	public DriverInfoVO getSelectedDriver() {
		return selectedDriver;
	}

	public void setSelectedDriver(DriverInfoVO selectedDriver) {
		this.selectedDriver = selectedDriver;
	}
	
	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
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

	public long getCustomerCId() {
		return customerCId;
	}

	public void setCustomerCId(long customerCId) {
		this.customerCId = customerCId;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getDriverNameInput() {
		return driverNameInput;
	}

	public void setDriverNameInput(String driverNameInput) {
		this.driverNameInput = driverNameInput;
	}

	public List<DriverInfoVO> getResultList() {
		return resultList;
	}

	public void setResultList(List<DriverInfoVO> resultList) {
		this.resultList = resultList;
	}

	public LazyDataModel<DriverInfoVO> getDrivers() {
		return drivers;
	}

	public void setDrivers(LazyDataModel<DriverInfoVO> drivers) {
		this.drivers = drivers;
	}

	public String getSelectedUnitFactoryOptionalEquipments() {
		return selectedUnitFactoryOptionalEquipments;
	}

	public void setSelectedUnitFactoryOptionalEquipments(
			String selectedUnitFactoryOptionalEquipments) {
		this.selectedUnitFactoryOptionalEquipments = selectedUnitFactoryOptionalEquipments;
	}

	public List<String> getSelectedUnitStandardEquipments() {
		return selectedUnitStandardEquipments;
	}

	public void setSelectedUnitStandardEquipments(
			List<String> selectedUnitStandardEquipments) {
		this.selectedUnitStandardEquipments = selectedUnitStandardEquipments;
	}

	public List<String> getSelectedUnitDealerAccessories() {
		return selectedUnitDealerAccessories;
	}

	public void setSelectedUnitDealerAccessories(
			List<String> selectedUnitDealerAccessories) {
		this.selectedUnitDealerAccessories = selectedUnitDealerAccessories;
	}


}
