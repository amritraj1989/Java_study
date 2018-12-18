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
import com.mikealbert.data.vo.MakeModelRangeVO;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.service.ModelSearchService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class ModelLOVBean extends BaseBean  {

	@Resource ModelSearchService modelSearchService;
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	
	private int selectionIndex = -1;

	private LazyDataModel<MakeModelRangeVO> modelRanges;
	private MakeModelRangeVO selectedModelRange;
	private List<MakeModelRangeVO> resultList = new ArrayList<MakeModelRangeVO>();	
	
	
	private String modelDescription;
	private ModelSearchCriteriaVO modelSearchCriteriaVO;
	private boolean showModelType = false;

	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private int rowsPerPage = ViewConstants.RECORDS_PER_PAGE_SMALL;
	boolean isStart = true;
	//private String searchParam;
	private int pageNumber = 0;
	private int lovRecordsPerPage;

	@PostConstruct
	public void init() {
		try{
			setLovRecordsPerPage(ViewConstants.RECORDS_PER_PAGE_SMALL);
			
			isStart = true; //TODO Needed?
						
			this.modelRanges = new LazyDataModel<MakeModelRangeVO>() {

				private static final long serialVersionUID = 3234994868777224492L;						

				@Override
				public List<MakeModelRangeVO> load(int pageNumber, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {				
					
					setSelectedModelRange(null);
					setPageSize(getRowsPerPage());
					
					//TODO Investigate why this is needed
					if (isStart) {
						pageNumber = 0;
						isStart = false;
					}
					
					return getLazyMakes(pageNumber, pageSize);					
				}
				
				@Override
				public MakeModelRangeVO getRowData(String rowKey) {
					for (MakeModelRangeVO modelRange : resultList) {
						if (modelRange.getMrgId().toString().equals(rowKey))
							return modelRange;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(MakeModelRangeVO modelRange) {
					return modelRange.getMrgId();
				}
			};
			
			modelRanges.setPageSize(ViewConstants.RECORDS_PER_PAGE_SMALL);
			
			logger.info("Method name: Init, Obtained model ranges:", new Object[] { modelRanges.getRowCount() });
			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
	}
	
	public void fetchLOVDataByModelDescription(ActionEvent ae) {
		setModelSearchCriteriaVO(new ModelSearchCriteriaVO());
		
		if (!MALUtilities.isEmpty(getRequestParameter(getRequestParameter("MODEL_LOV_INPUT_MFG_CODE")))) {
			getModelSearchCriteriaVO().setMfgCode((String) getRequestParameter(getRequestParameter("MODEL_LOV_INPUT_MFG_CODE")));
		}
		
		if (!MALUtilities.isEmpty(getRequestParameter(getRequestParameter("MODEL_LOV_INPUT_YEAR")))) {
			getModelSearchCriteriaVO().setYear((String) getRequestParameter(getRequestParameter("MODEL_LOV_INPUT_YEAR")));
		}
		
		if (!MALUtilities.isEmpty(getRequestParameter(getRequestParameter("MODEL_LOV_INPUT_MAKE")))) {
			getModelSearchCriteriaVO().setMake((String) getRequestParameter(getRequestParameter("MODEL_LOV_INPUT_MAKE")));
		}
		
		if (!MALUtilities.isEmpty(getRequestParameter(getRequestParameter("MODEL_LOV_INPUT_MODEL")))) {
			getModelSearchCriteriaVO().setModel((String) getRequestParameter(getRequestParameter("MODEL_LOV_INPUT_MODEL")));
		}
		
		if (!MALUtilities.isEmpty(getRequestParameter("MODEL_LOV_SOURCE"))) {
			getModelSearchCriteriaVO().setLovSource((String) getRequestParameter("MODEL_LOV_SOURCE"));
		}
		
		if (MALUtilities.isNotEmptyString(getRequestParameter("MODEL_LOV_SHOW_TYPE"))) {
			 setShowModelType(Boolean.parseBoolean((String) getRequestParameter("MODEL_LOV_SHOW_TYPE"))); 
		}
		
		this.selectionIndex = 0;
		this.pageNumber = 0;
		
//		if (!MALUtilities.isEmptyString(paramName)) {
//			this.setSearchParam((String) getRequestParameter(paramName));
//		}
//		
		this.setModelDescription(getModelSearchCriteriaVO().getModel());
		
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		logger.info("Searched makeDescription: " + getModelDescription());
		List<MakeModelRangeVO> list = getLazyMakes(0, rowsPerPage);
		modelRanges.setWrappedData(list);
		modelRanges.setRowIndex(-1);
		execCountQuery = true;
	}
	
	private List<MakeModelRangeVO> getLazyMakes(int pageNumber, int pageSize) {
		
		try{
			if (initFlag == false) {
				int pageIdx = (pageNumber == 0) ? 0 : (pageNumber / pageSize);
				
				PageRequest page = new PageRequest(pageIdx, pageSize);
				setPageNumber(page.getPageNumber());
				
				resultList = modelSearchService.findModelRanges(modelSearchCriteriaVO, page);
				
				modelRanges.setPageSize(rowsPerPage);
				if (execCountQuery){
					int count =modelSearchService.findModelRangesCount(modelSearchCriteriaVO);
					modelRanges.setRowCount(count);
					modelRanges.setWrappedData(resultList);
				}
				
			}

			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyMakes");
		}
		return resultList;
		
	}
	
	@SuppressWarnings("unchecked")
	public void setClickSelectedModelRow() {
		String index = (String)getRequestParameter("clickModelLovIndex");

		if (!MALUtilities.isEmpty(index)) {
			Integer intIndex = Integer.parseInt(index);
			intIndex = intIndex % rowsPerPage;
			setSelectedModelRange((resultList != null && !resultList.isEmpty()  ? resultList.get(intIndex) : null));
		} else {
			if (!resultList.isEmpty()) {
				setSelectedModelRange(resultList.get(0));
			}
		}
	}

	public void onRowSelect(SelectEvent event) {
		setSelectedModelRange(((MakeModelRangeVO)event.getObject()));
	}
	
	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}

	public LazyDataModel<MakeModelRangeVO> getModelRanges() {
		return modelRanges;
	}

	public void setModelRanges(LazyDataModel<MakeModelRangeVO> modelRanges) {
		this.modelRanges = modelRanges;
	}

	public MakeModelRangeVO getSelectedModelRange() {
		return selectedModelRange;
	}

	public void setSelectedModelRange(MakeModelRangeVO selectedModelRange) {
		this.selectedModelRange = selectedModelRange;
	}

	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	public ModelSearchCriteriaVO getModelSearchCriteriaVO() {
		return modelSearchCriteriaVO;
	}

	public void setModelSearchCriteriaVO(ModelSearchCriteriaVO modelSearchCriteriaVO) {
		this.modelSearchCriteriaVO = modelSearchCriteriaVO;
	}

//	public String getSearchParam() {
//		return searchParam;
//	}
//
//	public void setSearchParam(String searchParam) {
//		this.searchParam = searchParam;
//	}

	public boolean isShowModelType() {
		return showModelType;
	}

	public void setShowModelType(boolean showModelType) {
		this.showModelType = showModelType;
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

	public void setLovRecordsPerPage(int lovRecordsPerPage) {
		this.lovRecordsPerPage = lovRecordsPerPage;
	}


}
