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
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.vo.CustomerContactVO;
import com.mikealbert.data.vo.GlCodeLOVVO;
import com.mikealbert.service.GlCodeService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("glCodeLovBean")
@Scope("view")
public class GlCodeLovBean extends BaseBean {
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	
	private int pageNumber = 0;
	boolean isStart = true;
	boolean initFlag = true;
	private int totalRecord = 0;
	private boolean execCountQuery = true;

	private GlCodeLOVVO selectedGlCode;
	private LazyDataModel<GlCodeLOVVO> glCodeList;
	List<GlCodeLOVVO> resultList;
	private String	glCode;
	private String	glCodeDesc;
	private String glCodeParam;
	private Long corpId;
	private Integer	rowIndex; 

	@Resource
	private GlCodeService glCodeService;

	@PostConstruct
	public void init() {
		try{
			isStart = true;
			resultList = new ArrayList<GlCodeLOVVO>();
			setGlCodeList(new LazyDataModel<GlCodeLOVVO>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<GlCodeLOVVO> load(int first, int pageSize, String arg2, SortOrder arg3,
						Map<String, Object> arg4) {
					selectedGlCode = null;
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}
					
					resultList = getLazyGlCodeList(first, pageSize);
					return resultList;
				}
				
				@Override
				public GlCodeLOVVO getRowData(String rowKey) {
					for (GlCodeLOVVO glCode : glCodeList) {
						if (String.valueOf(glCode.getCompositeKey()).equals(rowKey))
							return glCode;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(GlCodeLOVVO glCode) {
					return glCode.getCode();
				}
			});
			getGlCodeList().setPageSize(rowsPerPage);
			logger.info("Method name: Init, Obtained GL Codes:", new Object[] { getGlCodeList().getRowCount() });
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
	}

	public void fetchLOVData() {
		this.pageNumber = 0;
		this.selectedGlCode = null;
		String paramName = (String) getRequestParameter("GL_CODE_LOV_CODE_INPUT");
		glCodeParam = (String) getRequestParameter(paramName);
		paramName = (String) getRequestParameter("GL_CODE_LOV_CORPID_INPUT");
		corpId = Long.valueOf(getRequestParameter(paramName)).longValue();
		rowIndex =getRequestParameter("rowIndex") != null ? Integer.parseInt((String) getRequestParameter("rowIndex")):null;
		isStart = true;
		initFlag = false;
		try {
			/*List<GlCodeLOVVO> list = getLazyGlCodeList(0, rowsPerPage);
			getGlCodeList().setWrappedData(list);
			getGlCodeList().setRowIndex(0);*/
		} catch (Exception tb) {
			addErrorMessage("generic.error.occured.while", "retrieving Gl Code");
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<GlCodeLOVVO> getLazyGlCodeList(int first, int size) {
		try{
			if (initFlag == false) {
				// first / pageSize
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				this.pageNumber = page.getPageNumber();
				resultList = glCodeService.getGlCodes(glCodeParam, corpId, page);
				
				getGlCodeList().setPageSize(rowsPerPage);
				if (execCountQuery){
					totalRecord = glCodeService.getGlCodesCount(glCodeParam, corpId);
					getGlCodeList().setRowCount(totalRecord);
					getGlCodeList().setWrappedData(resultList);
				}
				
			}

			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyCustomersListByName");
		}
		return resultList;
		
	}
	
	public void onRowSelect(SelectEvent event) {
		GlCodeLOVVO glCode = (GlCodeLOVVO) event.getObject();
		
		setSelectedGlCode(glCode);
	}


	public GlCodeLOVVO getSelectedGlCode() {
		return selectedGlCode;
	}

	public void setSelectedGlCode(GlCodeLOVVO selectedGlCode) {
		if(selectedGlCode != null){
			this.selectedGlCode = selectedGlCode;
			glCode = selectedGlCode.getCode();
			glCodeDesc	= selectedGlCode.getDescription();
			
		}
		
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public String getGlCodeDesc() {
		return glCodeDesc;
	}

	public void setGlCodeDesc(String glCodeDesc) {
		this.glCodeDesc = glCodeDesc;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public Integer getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(Integer rowIndex) {
		this.rowIndex = rowIndex;
	}

	public LazyDataModel<GlCodeLOVVO> getGlCodeList() {
		return glCodeList;
	}

	public void setGlCodeList(LazyDataModel<GlCodeLOVVO> glCodeList) {
		this.glCodeList = glCodeList;
	}
}
