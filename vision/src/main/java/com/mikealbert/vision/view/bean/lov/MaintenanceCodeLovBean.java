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
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.service.MaintenanceCodeService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("maintenanceCodeLovBean")
@Scope("view")
public class MaintenanceCodeLovBean extends BaseBean {
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	
	private int selectionIndex = -1;

	private LazyDataModel<MaintenanceCode> maintenanceCodes;
	private MaintenanceCode selectedMaintenanceCodeContact;

	private String maintCodeId;
	private String maintCode;
	private String maintenanceCodeDescription;

	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	boolean isStart = true;
	List<MaintenanceCode> resultList;
	private String searchParam;
	private int pageNumber = 0;
	private int lovRecordsPerPage;
	private String msCode;
	@Autowired
	private MaintenanceCodeService maintCodeService;
	
	@Resource MaintenanceCodeService mainCodeService;

	@PostConstruct
	public void init() {
		try{
			setLovRecordsPerPage();
			isStart = true;
			resultList = new ArrayList<MaintenanceCode>();
			maintenanceCodes = new LazyDataModel<MaintenanceCode>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<MaintenanceCode> load(int first, int pageSize, String arg2, SortOrder arg3,
						Map<String, Object> arg4) {
					selectedMaintenanceCodeContact = null;
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}
					
					resultList = getLazyMaintenanceCodeListByDescription(first, pageSize);
					return resultList;
				}
				
				@Override
				public MaintenanceCode getRowData(String rowKey) {
					for (MaintenanceCode maintenanceCode : resultList) {
						if (String.valueOf(maintenanceCode.getMcoId()).equals(rowKey))
							return maintenanceCode;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(MaintenanceCode maintCode) {
					return maintCode.getMcoId();
				}
			};
			maintenanceCodes.setPageSize(rowsPerPage);
			logger.info("Method name: Init, Obtained maintenanceCode:", new Object[] { maintenanceCodes.getRowCount() });
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
	}
	
	public void fetchLOVDataByMaintenanceCodeDescription(ActionEvent ae) {
		String paramName = (String) getRequestParameter("MAINTENANCE_CODE_LOV_INPUT");
		this.maintCodeId = null;
		this.maintCode = null;
		this.maintenanceCodeDescription = null;
		this.selectionIndex = 0;
		this.pageNumber = 0;
		
		if (!MALUtilities.isEmptyString(paramName)) {
			this.setSearchParam((String) getRequestParameter(paramName));
		}
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		logger.info("Searched maintenanceCode: " + paramName);
		List<MaintenanceCode> list = getLazyMaintenanceCodeListByDescription(0, rowsPerPage);
		maintenanceCodes.setWrappedData(list);
		maintenanceCodes.setRowIndex(0);
	}
	
	@SuppressWarnings("unchecked")
	private List<MaintenanceCode> getLazyMaintenanceCodeListByDescription(int first, int size) {
		try{
			if (initFlag == false) {
				// first / pageSize
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				setPageNumber(page.getPageNumber());

				resultList = maintCodeService.findMaintenanceCodesByNameOrCode(getSearchParam(), page);
				maintenanceCodes.setWrappedData(resultList);
				maintenanceCodes.setPageSize(rowsPerPage);
								
				maintenanceCodes.setRowCount(resultList.size());
				if (execCountQuery){
					Long count = maintCodeService.getMaintenanceCodeCountByNameOrCode(getSearchParam());
					maintenanceCodes.setRowCount(count.intValue());
				}
				
			}

			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyMaintenanceCodeListByDescription");
		}
		return resultList;
		
	}

	@SuppressWarnings("unchecked")
	public void setClickSelectedMaintenanceCodeRow() {
		//TODO: this never gets called, and is not needed!!
	}

	public void onRowSelect(SelectEvent event) {
		MaintenanceCode maintenanceCode = (MaintenanceCode) event.getObject();
		setSelectedMaintenanceContact(maintenanceCode);
	}

	public String getMaintCode() {
		return maintCode;
	}

	public void setMaintCode(String maintCode) {
		this.maintCode = maintCode;
	}

	public String getMaintCodeId() {
		return maintCodeId;
	}

	public void setMaintCodeId(String maintCodeId) {
		this.maintCodeId = maintCodeId;
	}
	
	public String getmaintenanceCodeDescription() {
		return maintenanceCodeDescription;
	}

	public void setmaintenanceCodeDescription(String maintenanceCodeDescription) {
		this.maintenanceCodeDescription = maintenanceCodeDescription;
	}

	public LazyDataModel<MaintenanceCode> getMaintenanceCode() {
		return maintenanceCodes;
	}

	public void setMaintenanceCode(LazyDataModel<MaintenanceCode> maintenanceCode) {
		this.maintenanceCodes = maintenanceCode;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public MaintenanceCode getSelectedMaintenanceContact() {
		return selectedMaintenanceCodeContact;
	}

	public void setSelectedMaintenanceContact(MaintenanceCode selectedMaintenanceCodeContact) {
		this.selectedMaintenanceCodeContact = selectedMaintenanceCodeContact;
		this.selectionIndex = resultList.indexOf(this.selectedMaintenanceCodeContact) + (this.rowsPerPage * this.pageNumber);
		if (this.selectedMaintenanceCodeContact != null) {
			this.maintCodeId = this.selectedMaintenanceCodeContact.getMcoId().toString();
			this.maintCode = this.selectedMaintenanceCodeContact.getCode();
			this.maintenanceCodeDescription = this.selectedMaintenanceCodeContact.getDescription();
		}
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
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
	

	
	public MaintenanceCode getSelectedMaintenanceCodeContact() {
		return selectedMaintenanceCodeContact;
	}

	public void setSelectedMaintenanceCodeContact(
			MaintenanceCode selectedMaintenanceCodeContact) {
		this.selectedMaintenanceCodeContact = selectedMaintenanceCodeContact;
	}

}