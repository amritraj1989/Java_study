package com.mikealbert.vision.view.bean.lov;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
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
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.vo.CustomerContactVO;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("customerLovBean")
@Scope("view")
public class CustomerLovBean extends BaseBean  {

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	
	private LazyDataModel<CustomerContactVO> customers;
	private CustomerContactVO selectedCustomerContact;
	private String customerCode;
	private String customerName;
	private String customerType;
	private long   customerCId; 
	private boolean includeClosedAccounts;

	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	boolean isStart = true;
	List<CustomerContactVO> resultList;
	private String searchParam;
	private int pageNumber = 0;
	private int lovRecordsPerPage;
	@Autowired
	private CustomerAccountService externalAcctService;

	@PostConstruct
	public void init() {
		try{
			setLovRecordsPerPage();
			isStart = true;
			resultList = new ArrayList<CustomerContactVO>();
			customers = new LazyDataModel<CustomerContactVO>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<CustomerContactVO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
					selectedCustomerContact = null;
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}
					
					resultList = getLazyCustomersListByName(first, pageSize);
					return resultList;
				}
				
				@Override
				public CustomerContactVO getRowData(String rowKey) {
					for (CustomerContactVO customer : resultList) {						
						if (customer.getCustomer().getExternalAccountPK().toString().equals(rowKey))
							return customer;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(CustomerContactVO externalAccount) {
					return externalAccount.getCustomer().getExternalAccountPK().toString();
				}
			};
			customers.setPageSize(rowsPerPage);
			logger.info("Method name: Init, Obtained customers:", new Object[] { customers.getRowCount() });
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
	}
	
	public void fetchLOVDataByCustomerName(ActionEvent ae) {
		String paramName = (String) getRequestParameter("CUSTOMER_LOV_INPUT");
		String paramIncClosedAccounts = (String) getRequestParameter("INCLUDE_CLOSED_ACCOUNTS");
		this.customerCode = null;
		this.customerName = null;
		this.customerType = null;
		this.customerCId = 0l;
		this.pageNumber = 0;
		
		this.includeClosedAccounts = MALUtilities.isEmptyString(paramIncClosedAccounts) ? false : MALUtilities.convertYNToBoolean(paramIncClosedAccounts);
		
		if (!MALUtilities.isEmptyString(paramName)) {
			this.setSearchParam((String) getRequestParameter(paramName));
		}
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		logger.info("Searched customerCode: " + this.customerCode);
		List<CustomerContactVO> list = getLazyCustomersListByName(0, rowsPerPage);
		customers.setWrappedData(list);
		customers.setRowIndex(0);
		execCountQuery = false;
	}
	
	private List<CustomerContactVO> getLazyCustomersListByName(int first, int size) {
		try{
			if (initFlag == false) {
				// first / pageSize
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				setPageNumber(page.getPageNumber());
				resultList = externalAcctService.findCustomerContacts(getSearchParam(),getLoggedInUser().getCorporateEntity(), page, includeClosedAccounts);
				
				customers.setPageSize(rowsPerPage);
				if (execCountQuery){
					Long count;
					if(includeClosedAccounts){
						count = externalAcctService.getAllCustomerAccountsByNameOrCodeCount(getSearchParam(), getLoggedInUser().getCorporateEntity());
					}else{
						count = externalAcctService.getOpenCustomerAccountsByNameOrCodeCount(getSearchParam(), getLoggedInUser().getCorporateEntity());
					}
					customers.setRowCount(count.intValue());
					customers.setWrappedData(resultList);
				}
				
			}

			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyCustomersListByName");
		}
		return resultList;
		
	}


	public void onRowSelect(SelectEvent event) {
		CustomerContactVO selectedCustomer = (CustomerContactVO) event.getObject();
		setSelectedCustomerContact(selectedCustomer);
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

	public LazyDataModel<CustomerContactVO> getCustomers() {
		return customers;
	}

	public void setCustomers(LazyDataModel<CustomerContactVO> customers) {
		this.customers = customers;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public CustomerContactVO getSelectedCustomerContact() {
		return selectedCustomerContact;
	}

	public void setSelectedCustomerContact(CustomerContactVO selectedCustomerContact) {
		this.selectedCustomerContact = selectedCustomerContact;
		if (this.selectedCustomerContact != null) {
			this.customerCode = this.selectedCustomerContact.getCustomer().getExternalAccountPK().getAccountCode();
			this.customerName = this.selectedCustomerContact.getCustomer().getAccountName();
			this.customerType = this.selectedCustomerContact.getCustomer().getExternalAccountPK().getAccountType();
			this.customerCId  = this.selectedCustomerContact.getCustomer().getExternalAccountPK().getCId();
		}
	}

	public String getSearchParam() {
		return searchParam;
	}

	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
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

}
