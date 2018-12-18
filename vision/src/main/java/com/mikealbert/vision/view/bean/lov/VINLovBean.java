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
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

/**
 * @author anand.mohan
 *
 */
@Component("vinLovBean")
@Scope("view")
public class VINLovBean extends BaseBean{
	private static final long serialVersionUID = 1L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private FleetMaster selectedFleetMaster;
	private LazyDataModel<FleetMaster> fleetMaster;
	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private String isDetermined;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	boolean isStart = true;
	private String VIN;
	private String unitNumber;
	private int selectionIndex;
	List<FleetMaster> resultList;
	private String searchParam;
	private int pageNumber = 0;
	private int lovRecordsPerPage;
	@Resource
	private FleetMasterService fleetMasterService;

	@PostConstruct
	public void init() {
		try {
			setLovRecordsPerPage();
			resultList = new ArrayList<FleetMaster>();
			fleetMaster = new LazyDataModel<FleetMaster>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<FleetMaster> load(int first, int pageSize, String arg2, SortOrder arg3,
						Map<String, Object> arg4) {
					selectedFleetMaster = null;
					if (isStart) {
						first = 0;
						isStart = false;
					}
					setPageSize(ViewConstants.LOV_RECORD_SIZE);
					resultList = getLazyFleetList(first, pageSize);
					return resultList;
				}

				@Override
				public FleetMaster getRowData(String rowKey) {
					for (FleetMaster fleet : resultList) {
						if (String.valueOf(fleet.getFmsId()).equals(rowKey))
							return fleet;
					}

					return null;
				}

				@Override
				public Object getRowKey(FleetMaster fleet) {
					return fleet.getFmsId();
				}
			};
			fleetMaster.setPageSize(ViewConstants.RECORDS_PER_PAGE);
		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}

	}

	public void fetchLOVData(ActionEvent ae) {
		isDetermined = "";
		isStart = true;
		this.selectionIndex = 0;
		this.pageNumber = 0;
		
		String paramName = (String) getRequestParameter("VIN_PARAM_NAME");
		if (!MALUtilities.isEmptyString(paramName)) {
			this.setSearchParam((String) getRequestParameter(paramName));
		}
		execCountQuery = true;
		initFlag = false;
		List<FleetMaster> list = getLazyFleetList(0, ViewConstants.LOV_RECORD_SIZE);
		fleetMaster.setWrappedData(list);
		fleetMaster.setRowIndex(0);
		execCountQuery = false;
	}

	@SuppressWarnings({ "unchecked" })
	private List<FleetMaster> getLazyFleetList(int first, int size) {
		try {
			if (initFlag == false) {
				// first / pageSize
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				setPageNumber(page.getPageNumber());
				resultList = fleetMasterService.findFleetMasterByVinLastSix(getSearchParam(), execCountQuery, page);
				fleetMaster.setPageSize(rowsPerPage);
				if (execCountQuery) {
					Long count = fleetMasterService.getFleetMasterCountByVINLastSix(getSearchParam());
					fleetMaster.setRowCount(count.intValue());
					fleetMaster.setWrappedData(resultList);
				}
			}
		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyFleetList");
		}

		return resultList;
	}

	public void setSelectedFleetMaster(FleetMaster selectedFleetMaster) {
		this.selectedFleetMaster = selectedFleetMaster;
		this.setSelectionIndex(resultList.indexOf(this.selectedFleetMaster) + (this.rowsPerPage * this.pageNumber));
		if (this.selectedFleetMaster != null) {
			this.VIN = this.selectedFleetMaster.getVin();
			this.unitNumber = this.selectedFleetMaster.getUnitNo();
			isDetermined = "";
		}
	}

	@SuppressWarnings("unchecked")
	public void setClickSelectedVinRow() {
		String index = (String) getRequestParameter("clickVinLovIndex");
	
		if (!MALUtilities.isEmpty(index)) {
			Integer intIndex = Integer.parseInt(index);
			intIndex = intIndex % rowsPerPage;
			setSelectedFleetMaster((resultList != null && !resultList.isEmpty() ? resultList.get(intIndex) : null));
		} else {
			if (!resultList.isEmpty())
				setSelectedFleetMaster(resultList.get(0));
		}

	}

	public void onRowSelect(SelectEvent event) {
		FleetMaster fleet = (FleetMaster) event.getObject();
		setSelectedFleetMaster(fleet);
	}

	public LazyDataModel<FleetMaster> getFleetMasters() {
		return fleetMaster;
	}

	public void setFleetMasters(LazyDataModel<FleetMaster> fleetMasters) {
		this.fleetMaster = fleetMasters;
	}

	public String getIsDetermined() {
		return isDetermined;
	}

	public void setIsDetermined(String isDetermined) {
		this.isDetermined = isDetermined;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public String getVIN() {
		return VIN;
	}

	public void setVIN(String vIN) {
		VIN = vIN;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public FleetMaster getSelectedFleetMaster() {
		return selectedFleetMaster;
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getSearchParam() {
		return searchParam;
	}

	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}
	
	public int getLovRecordsPerPage() {
		return lovRecordsPerPage;
	}

	public void setLovRecordsPerPage() {
		this.lovRecordsPerPage = ViewConstants.LOV_RECORD_SIZE;
	}

}
