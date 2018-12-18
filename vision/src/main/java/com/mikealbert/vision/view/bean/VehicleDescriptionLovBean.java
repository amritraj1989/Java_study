package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.data.vo.ModelSearchResultVO;
import com.mikealbert.service.ModelSearchService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component("vehicleDescriptionLovBean")
@Scope("view")
public class VehicleDescriptionLovBean extends BaseBean {

	private static final long serialVersionUID = 1L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private LazyDataModel<ModelSearchResultVO> vehicleDescriptions = null;
	private ModelSearchResultVO selectedVehicleDescription;
	private ModelSearchResultVO lovSelectedVehicleDescription;
	private ModelSearchCriteriaVO criteria;
	
	private String vehicleDescInput;
	
	private String dialogHeader;
	private String lovName;
	
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	 
	private List<ModelSearchResultVO> resultList = null;
	private boolean execCountQuery = false;
	private boolean initFlag = true;
	boolean isStart = true;
	
	@Resource ModelSearchService modelSearchService;

	@SuppressWarnings("serial")
	@PostConstruct
	public void init() {
		try{
			isStart = true;
			resultList = new ArrayList<ModelSearchResultVO>();
			criteria = new ModelSearchCriteriaVO();
			vehicleDescriptions = new LazyDataModel<ModelSearchResultVO>() {
				@Override
				public List<ModelSearchResultVO> load(int first, int pageSize, String arg2, SortOrder arg3,
						Map<String, Object> arg4) {
					selectedVehicleDescription = null;
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}
					
					resultList = getLazyVehicleDescriptionList(first, pageSize);
					return resultList;
				}
				
				@Override
				public ModelSearchResultVO getRowData(String rowKey) {
					for (ModelSearchResultVO vehicleDescriptionList : vehicleDescriptions) {
						if (String.valueOf(vehicleDescriptionList.getMdlId()).equals(rowKey))
							return vehicleDescriptionList;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(ModelSearchResultVO modelSearchResultVO) {
					return modelSearchResultVO.getMdlId();
				}
			};
			vehicleDescriptions.setPageSize(rowsPerPage);
			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
	}

	public void fetchLOVData() {
		this.dialogHeader = (String) getRequestParameter("VEH_DESC_LOV_INPUT_HEADER");
		this.lovName = (String) getRequestParameter("VEH_DESC_LOV_NAME");
		
		String vehDescLovParam = (String) getRequestParameter("VEH_DESC_INPUT");
		if (!MALUtilities.isEmptyString(vehDescLovParam)) {
			this.vehicleDescInput = (String) getRequestParameter(vehDescLovParam);
			criteria.setTrim(DataUtilities.prependWildCardToLeft(this.vehicleDescInput));
		}
		
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		setLovSelectedVehicleDescription(null);
		logger.debug("Searched input: " + this.vehicleDescInput);
		List<ModelSearchResultVO> list = getLazyVehicleDescriptionList(0, rowsPerPage);
		if(list != null && list.size() > 0){
			vehicleDescriptions.setWrappedData(list);
			vehicleDescriptions.setRowIndex(0);
			
		}
		
		execCountQuery = false;		
	}

	private List<ModelSearchResultVO> getLazyVehicleDescriptionList(int first, int size) {
		try {
			if (initFlag == false) {
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				int  totalCount = 0;
				
				resultList =  modelSearchService.findModels(criteria, page, null);
				if (execCountQuery) {
					totalCount =  modelSearchService.findModelsCount(criteria);						
				}								
				if (execCountQuery) {
					vehicleDescriptions.setRowCount(totalCount);
					}
				
				vehicleDescriptions.setWrappedData(resultList);
			}
			
			execCountQuery = false;

		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyVehicleDescriptionList");
		}
		return resultList;
	}
	
	public void onRowSelect(SelectEvent event) {
		ModelSearchResultVO modelSearchResultVO = (ModelSearchResultVO) event.getObject();
		setLovSelectedVehicleDescription(modelSearchResultVO);
	}
	
	public void performLovSelection(){
		setSelectedVehicleDescription(getLovSelectedVehicleDescription());
	}

	public String getDialogHeader() {
		return dialogHeader;
	}

	public void setDialogHeader(String dialogHeader) {
		this.dialogHeader = dialogHeader;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public String getLovName() {
		return lovName;
	}

	public void setLovName(String lovName) {
		this.lovName = lovName;
	}

	public LazyDataModel<ModelSearchResultVO> getVehicleDescriptions() {
		return vehicleDescriptions;
	}

	public void setVehicleDescriptions(
			LazyDataModel<ModelSearchResultVO> vehicleDescriptions) {
		this.vehicleDescriptions = vehicleDescriptions;
	}

	public ModelSearchResultVO getLovSelectedVehicleDescription() {
		return lovSelectedVehicleDescription;
	}

	public void setLovSelectedVehicleDescription(
			ModelSearchResultVO lovSelectedVehicleDescription) {
		this.lovSelectedVehicleDescription = lovSelectedVehicleDescription;
	}

	public ModelSearchResultVO getSelectedVehicleDescription() {
		return selectedVehicleDescription;
	}

	public void setSelectedVehicleDescription(
			ModelSearchResultVO selectedVehicleDescription) {
		this.selectedVehicleDescription = selectedVehicleDescription;
	}

	public String getVehicleDescInput() {
		return vehicleDescInput;
	}

	public void setVehicleDescInput(String vehicleDescInput) {
		this.vehicleDescInput = vehicleDescInput;
	}
	
}
