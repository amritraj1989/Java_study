package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.vo.VendorLovVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.VendorService;
import com.mikealbert.vision.view.ViewConstants;


@Component("vendorLovBean")
@Scope("view")
public class VendorLovBean extends BaseBean {

	private static final long serialVersionUID = 1L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private LazyDataModel<VendorLovVO> vendors	= null;
	private VendorLovVO selectedVendor;
	private String vendorCode;
	private String vendorName;
	private String vendorType;
	private String vendorNameOrCode;
	private Long cId;	
	private List<VendorLovVO> resultList = null;
	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	boolean isStart = true;
	
	
	@Autowired
	private VendorService vendorService;

	@SuppressWarnings("serial")
	@PostConstruct
	public void init() {
		try{	
			
			isStart = true;
			this.vendorType = "S";//if needed we can make it parametrize
			this.cId = getLoggedInUser().getCorporateEntity().getCorpId();
			
			resultList = new ArrayList<VendorLovVO>();
			vendors = new LazyDataModel<VendorLovVO>() {
				@Override
				public List<VendorLovVO> load(int first, int pageSize, String arg2, SortOrder arg3,
						Map<String, Object> arg4) {
					selectedVendor = null;
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}
					
					resultList = getLazyVendorsList(first, pageSize);
					return resultList;
				}
				
				@Override
				public VendorLovVO getRowData(String rowKey) {
					for (VendorLovVO vendorList : resultList) {
						if (String.valueOf(vendorList.getVendorCode()).equals(rowKey))
							return vendorList;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(VendorLovVO vendorLovVO) {
					return vendorLovVO.getVendorCode();
				}
			};
			vendors.setPageSize(rowsPerPage);
			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
	}

	public void fetchLOVData() {		
		this.vendorCode = null;
		this.vendorName = null;
		
		
		
		String paramName = (String) getRequestParameter("VENDOR_INPUT");
		if (!MALUtilities.isEmptyString(paramName)) {
			this.vendorNameOrCode = (String) getRequestParameter(paramName);//It might be name or code
		}
		
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		logger.debug("Searched input: " + this.vendorNameOrCode);
		resultList = getLazyVendorsList(0, rowsPerPage);
		if(resultList != null && resultList.size() > 0){
			vendors.setWrappedData(resultList);
			vendors.setRowIndex(0);
			
			
		}
		
		execCountQuery = false;		
	}

	private List<VendorLovVO> getLazyVendorsList(int first, int size) {
		try {
			if (initFlag == false) {
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				int  totalCount = 0;			
				resultList =  vendorService.getVendors(cId, vendorType, vendorNameOrCode,page);
				if (execCountQuery) {
					totalCount =  vendorService.getVendorListCount(cId, vendorType, vendorNameOrCode);						
					}
				
				if (execCountQuery) {
						vendors.setRowCount(totalCount);
				}
				
				vendors.setWrappedData(resultList);
			}
			
			execCountQuery = false;

		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyVendorsListByName");
		}
		return resultList;
	}
	
	

	public void setSelectedVendor(VendorLovVO selectedVendor) {
		this.selectedVendor = selectedVendor;
		if (this.selectedVendor != null) {
			this.vendorCode = this.selectedVendor.getVendorCode();
			this.vendorName = this.selectedVendor.getVendorName();
		}
	}

	
	public void onRowSelect(SelectEvent event) {
		VendorLovVO vendorLovVO = (VendorLovVO) event.getObject();
		setSelectedVendor(vendorLovVO);
	}

	public LazyDataModel<VendorLovVO> getVendors() {
		return vendors;
	}

	public void setVendors(LazyDataModel<VendorLovVO> vendors) {
		this.vendors = vendors;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public VendorLovVO getSelectedVendor() {
		return selectedVendor;
	}

}
