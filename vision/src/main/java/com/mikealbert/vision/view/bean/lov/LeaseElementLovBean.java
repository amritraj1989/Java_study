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
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.vo.CustomerContactVO;
import com.mikealbert.data.vo.GlCodeLOVVO;
import com.mikealbert.service.GlCodeService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class LeaseElementLovBean extends BaseBean {
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	/*private int selectionIndex = -1;*/
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	private int pageNumber = 0;
	boolean isStart = true;
	boolean initFlag = true;
	private int totalRecord = 0;
	private boolean execCountQuery = true;

	private LeaseElement selectedLeaseElement;
	private LazyDataModel<LeaseElement> leaseElementList;
	List<LeaseElement> resultList;
	private List<Long> listOfExcludedElements;
	private String	leaseElementName;
	private String	leaseElementDescription;
	private String leaseElementParam;
	//private Integer	rowIndex; 

	@Resource
	private ServiceElementService serviceElementService;

	@PostConstruct
	public void init() {
		try{
			isStart = true;
			resultList = new ArrayList<LeaseElement>();
			setLeaseElementList(new LazyDataModel<LeaseElement>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<LeaseElement> load(int first, int pageSize, String arg2, SortOrder arg3,
						Map<String, Object> arg4) {
					selectedLeaseElement = null;
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}
					
					resultList = getLazyLeaseElementList(first, pageSize);
					return resultList;
				}
				
				@Override
				public LeaseElement getRowData(String rowKey) {
					for (LeaseElement leaseElement : leaseElementList) {						
						if (leaseElement.getElementName().equals(rowKey))
							return leaseElement;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(LeaseElement leaseElement) {
					return leaseElement.getElementName();
				}
			});
			getLeaseElementList().setPageSize(rowsPerPage);
			logger.info("Method name: Init, Obtained Lease Elements:", new Object[] { getLeaseElementList().getRowCount() });
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
	}

	public void fetchLOVData(List<Long> listOfExcludedElements) {
		this.pageNumber = 0;
		this.selectedLeaseElement = null;
		String paramName = (String) getRequestParameter("LEASE_ELEMENT_NAME_INPUT");
		setLeaseElementParam((String) getRequestParameter(paramName));
		setListOfExcludedElements(listOfExcludedElements);
		//rowIndex =getRequestParameter("rowIndex") != null ? Integer.parseInt((String) getRequestParameter("rowIndex")):null;
		isStart = true;
		initFlag = false;
	}
	
	@SuppressWarnings("unchecked")
	private List<LeaseElement> getLazyLeaseElementList(int first, int size) {
		try{
			if (initFlag == false) {
				// first / pageSize
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				this.pageNumber = page.getPageNumber();
				resultList = serviceElementService.findAllFilterByFinanceTypeAndElementList(leaseElementParam, getListOfExcludedElements(), page);
				
				getLeaseElementList().setPageSize(rowsPerPage);
				if (execCountQuery){
					totalRecord = serviceElementService.findAllFilterByFinanceTypeAndElementListCount(leaseElementParam, listOfExcludedElements);
					getLeaseElementList().setRowCount(totalRecord);
					getLeaseElementList().setWrappedData(resultList);
				}
				
			}

			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyLeaseElementsList");
		}
		return resultList;
		
	}
	

	public void onRowSelect(SelectEvent event) {
		LeaseElement leaseElement = (LeaseElement) event.getObject();
		
		setSelectedLeaseElement(leaseElement);
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	/*public Integer getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(Integer rowIndex) {
		this.rowIndex = rowIndex;
	}*/

	public LeaseElement getSelectedLeaseElement() {
		return selectedLeaseElement;
	}

	public void setSelectedLeaseElement(LeaseElement selectedLeaseElement) {
		if(selectedLeaseElement != null){
			this.selectedLeaseElement = selectedLeaseElement;
			leaseElementName = selectedLeaseElement.getElementName();
			leaseElementDescription	= selectedLeaseElement.getDescription();
			
		}
	}

	public LazyDataModel<LeaseElement> getLeaseElementList() {
		return leaseElementList;
	}

	public void setLeaseElementList(LazyDataModel<LeaseElement> leaseElementList) {
		this.leaseElementList = leaseElementList;
	}

	public String getLeaseElementName() {
		return leaseElementName;
	}

	public void setLeaseElementName(String leaseElementName) {
		this.leaseElementName = leaseElementName;
	}

	public String getLeaseElementDescription() {
		return leaseElementDescription;
	}

	public void setLeaseElementDescription(String leaseElementDescription) {
		this.leaseElementDescription = leaseElementDescription;
	}

	public String getLeaseElementParam() {
		return leaseElementParam;
	}

	public void setLeaseElementParam(String leaseElementParam) {
		this.leaseElementParam = leaseElementParam;
	}

	public List<Long> getListOfExcludedElements() {
		return listOfExcludedElements;
	}

	public void setListOfExcludedElements(List<Long> listOfExcludedElements) {
		this.listOfExcludedElements = listOfExcludedElements;
	}
	
}
