package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.enumeration.VehicleOrderType;
import com.mikealbert.data.vo.VendorLovVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.UnitProgressService;
import com.mikealbert.vision.service.VendorService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.ContactInfo;


@Component("supplierLovBean")
@Scope("view")
public class SupplierLovBean extends BaseBean {

	private static final long serialVersionUID = 1L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private LazyDataModel<VendorLovVO> vendors	= null;
	private VendorLovVO selectedVendor;
	private String vendorCode;
	private String vendorName;
	private String quoteOrderType;
	
	private String vendorWorkshopType; 
	private List<VendorLovVO> resultList = null;
	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	boolean isStart = true;
	
	private VendorLovVO lovSelectedVendor;
	
	static final String DELIVERING_WORKSHOP =  "DELIVERING";
	static final String ORDERING_WORKSHOP =  "ORDERING";	
	static final String ORDERING_DEALER_LOV_HEADER  = "Select Ordering Vendor";
	static final String DELIVERING_DEALER_LOV_HEADER  = "Select Delivering Vendor";
	
	private String dialogHeader;
	
	@Autowired
	private VendorService vendorService;
	@Resource UnitProgressService unitProgressService;

	@SuppressWarnings("serial")
	@PostConstruct
	public void init() {
		try{
			isStart = true;
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
		lovSelectedVendor = new VendorLovVO();
		String paramName = (String) getRequestParameter("VENDOR_INPUT");
		if (!MALUtilities.isEmptyString(paramName)) {
			this.vendorName = (String) getRequestParameter(paramName);//It might be name or code
		}

		
		this.vendorWorkshopType= (String) getRequestParameter("VENDOR_WORKSHOP_INPUT");
		
		String orderTypeParam = (String) getRequestParameter("QUOTE_ORDER_TYPE");
		if (!MALUtilities.isEmptyString(orderTypeParam)) {
			this.quoteOrderType = orderTypeParam; // this is order type of quote.
		}
		
		if (!MALUtilities.isEmptyString(vendorWorkshopType)) {
			if(vendorWorkshopType.equalsIgnoreCase(ORDERING_WORKSHOP)){
				dialogHeader = ORDERING_DEALER_LOV_HEADER;
			}else if(vendorWorkshopType.equalsIgnoreCase(DELIVERING_WORKSHOP)){
				dialogHeader = DELIVERING_DEALER_LOV_HEADER;   
			}
		}
		
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		logger.debug("Searched input: " + this.vendorName);
		List<VendorLovVO> list = getLazyVendorsList(0, rowsPerPage);
		if(list != null && list.size() > 0){
			vendors.setWrappedData(list);
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
				
				resultList =  vendorService.getOrderingOrDeliveringVendors(vendorName, vendorWorkshopType, page);
				if (execCountQuery) {
					totalCount =  vendorService.getOrderingOrDeliveringVendorsCount(vendorName, vendorWorkshopType);						
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
	
	public void performLovSelection(){
		setSelectedVendor(getLovSelectedVendor());
	}
	
	

	public void setSelectedVendor(VendorLovVO selectedVendor) {
		this.selectedVendor = selectedVendor;
	}

	public void onRowSelect(SelectEvent event) {
		VendorLovVO vendorLovVO = (VendorLovVO) event.getObject();
		ContactInfo contactInfo = null;
		if( !MALUtilities.isEmpty(quoteOrderType) && VehicleOrderType.LOCATE.getCode().equals(quoteOrderType) && vendorWorkshopType.equals("DELIVERING") ){
			contactInfo = unitProgressService.getDealerContactInfo(vendorLovVO.getEaAccountCode());
		}
		
		if(MALUtilities.isEmpty(contactInfo) ) {
			contactInfo = unitProgressService.getVendorSupplierContactInfo(vendorLovVO.getEaAccountCode());
		}
		if(!MALUtilities.isEmpty(contactInfo))
			vendorLovVO.setEmail(contactInfo.getEmail());
		setLovSelectedVendor(vendorLovVO);
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

	public String getDialogHeader() {
		return dialogHeader;
	}

	public void setDialogHeader(String dialogHeader) {
		this.dialogHeader = dialogHeader;
	}

	public VendorLovVO getLovSelectedVendor() {
		return lovSelectedVendor;
	}

	public void setLovSelectedVendor(VendorLovVO lovSelectedVendor) {
		this.lovSelectedVendor = lovSelectedVendor;
	}

}
