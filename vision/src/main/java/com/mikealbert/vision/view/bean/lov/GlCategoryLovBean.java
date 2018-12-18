package com.mikealbert.vision.view.bean.lov;

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
import com.mikealbert.data.entity.CostDatabaseCategoryCodes;
import com.mikealbert.data.vo.ServiceProviderLOVVO;
import com.mikealbert.service.GlCategoryService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class GlCategoryLovBean extends BaseBean {
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private int selectionIndex = -1;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	private int pageNumber = 0;
	boolean isStart = true;
	boolean initFlag = true;
	private int totalRecord = 0;
	private boolean execCountQuery = true;
	private String glCategoryParam;
	private CostDatabaseCategoryCodes selectedGlCategory;
	private LazyDataModel<CostDatabaseCategoryCodes> glCategoryList;
	List<CostDatabaseCategoryCodes> resultList;
	private String	glCategory;
	private String	glCategoryDesc;
	private Integer	rowIndex; 	

	@Resource
	private GlCategoryService glCategoryService;
	
	@PostConstruct
	public void init() {
		try{
			isStart = true;
			resultList = new ArrayList<CostDatabaseCategoryCodes>();
			setGlCategoryList(new LazyDataModel<CostDatabaseCategoryCodes>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<CostDatabaseCategoryCodes> load(int first, int pageSize, String arg2, SortOrder arg3,
						Map<String, Object> arg4) {
					selectedGlCategory = null;
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}
					
					resultList = getLazyGlCategoryList(first, pageSize);
					return resultList;
				}
				
				@Override
				public CostDatabaseCategoryCodes getRowData(String rowKey) {
					for (CostDatabaseCategoryCodes costDatabaseCategoryCodes : glCategoryList) {
						if (String.valueOf(costDatabaseCategoryCodes.getCategory()).equals(rowKey))
							return costDatabaseCategoryCodes;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(CostDatabaseCategoryCodes glCategory) {
					return selectedGlCategory.getCategory();
				}
			});
			getGlCategoryList().setPageSize(rowsPerPage);
			logger.info("Method name: Init, Obtained GL Catogories:", new Object[] { getGlCategoryList().getRowCount() });
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
	}
	
	public void fetchLOVData() {
		this.selectionIndex = 0;
		this.pageNumber = 0;
		this.selectedGlCategory = null;
		String paramName = (String) getRequestParameter("GL_CATEGORY_LOV_CODE_INPUT");
		glCategoryParam = (String) getRequestParameter(paramName);
		rowIndex =getRequestParameter("rowIndex") != null ? Integer.parseInt((String) getRequestParameter("rowIndex")):null;
		isStart = true;
		initFlag = false;
		try {
			/*List<CostDatabaseCategoryCodes> list = getLazyGlCategoryList(0, rowsPerPage);
			getGlCategoryList().setWrappedData(list);
			getGlCategoryList().setRowIndex(0);*/
		} catch (Exception tb) {
			addErrorMessage("generic.error.occured.while", "retrieving Gl Code");
		}
	}	
	
	@SuppressWarnings("unchecked")
	private List<CostDatabaseCategoryCodes> getLazyGlCategoryList(int first, int size) {
		try{
			if (initFlag == false) {
				// first / pageSize
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				this.pageNumber = page.getPageNumber();
				resultList = glCategoryService.getGlCategories(glCategoryParam, page);
				
				getGlCategoryList().setPageSize(rowsPerPage);
				if (execCountQuery){
					totalRecord = glCategoryService.getGlCategoryCount(glCategoryParam);
					getGlCategoryList().setRowCount(totalRecord);
					getGlCategoryList().setWrappedData(resultList);
				}
				
			}

			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyGlCateogryList");
		}
		return resultList;
		
	}	
	
	public void setClickSelectedGlCategoryRow() {
		String index = (String) getRequestParameter("clickGlCategoryLovIndex");
		List<CostDatabaseCategoryCodes> wrappedList = (List<CostDatabaseCategoryCodes>)getGlCategoryList().getWrappedData();
		if (!MALUtilities.isEmpty(index)) {
			Integer intIndex = Integer.parseInt(index);
			intIndex = intIndex % rowsPerPage;
			setSelectedGlCategory((wrappedList!= null && !wrappedList.isEmpty()  ? wrappedList.get(intIndex):null));
		} else {
			if (!wrappedList.isEmpty())
				setSelectedGlCategory(wrappedList.get(0));
		}

	}	
	
	public void onRowSelect(SelectEvent event) {
		CostDatabaseCategoryCodes glCategory = (CostDatabaseCategoryCodes) event.getObject();
		
		setSelectedGlCategory(glCategory);
	}	
	
	public CostDatabaseCategoryCodes getSelectedGlCategory() {
		return selectedGlCategory;
	}

	public void setSelectedGlCategory(CostDatabaseCategoryCodes selectedGlCategory) {
		if(selectedGlCategory != null){
			this.selectedGlCategory = selectedGlCategory;
			this.selectionIndex = resultList.indexOf(this.selectedGlCategory) + (this.rowsPerPage * this.pageNumber);
			glCategory = selectedGlCategory.getCategory();
			glCategoryDesc	= selectedGlCategory.getDescription();
			
		}
	}	

	public String getGlCategory() {
		return glCategory;
	}

	public void setGlCategory(String glCategory) {
		this.glCategory = glCategory;
	}

	public String getGlCategoryDesc() {
		return glCategoryDesc;
	}

	public void setGlCategoryDesc(String glCategoryDesc) {
		this.glCategoryDesc = glCategoryDesc;
	}

	public Integer getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(Integer rowIndex) {
		this.rowIndex = rowIndex;
	}

	public LazyDataModel<CostDatabaseCategoryCodes> getGlCategoryList() {
		return glCategoryList;
	}

	public void setGlCategoryList(LazyDataModel<CostDatabaseCategoryCodes> glCategoryList) {
		this.glCategoryList = glCategoryList;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	public void setSelectionIndex(int selectionIndex) {
		this.selectionIndex = selectionIndex;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

/*	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}*/	
	
}
