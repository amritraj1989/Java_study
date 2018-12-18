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
import com.mikealbert.data.vo.WillowUserLovVO;
import com.mikealbert.service.QuoteRequestService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;


@Component("willowUserLovBean")
@Scope("view")
public class WillowUserLovBean extends BaseBean {

	private static final long serialVersionUID = 1L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private LazyDataModel<WillowUserLovVO> willowUsers = null;
	private WillowUserLovVO selectedWillowUser;
	private WillowUserLovVO lovSelectedWillowUser;
	private String willowUserNo;
	private String willowUserName;
	private String dialogHeader;
	private String lovName;
	private int rowsPerPage = ViewConstants.LOV_RECORD_SIZE;
	 
	private List<WillowUserLovVO> resultList = null;
	private boolean execCountQuery = false;
	private boolean initFlag = true;
	boolean isStart = true;
	
	@Resource QuoteRequestService quoteRequestService;

	@SuppressWarnings("serial")
	@PostConstruct
	public void init() {
		try{
			isStart = true;
			resultList = new ArrayList<WillowUserLovVO>();
			willowUsers = new LazyDataModel<WillowUserLovVO>() {
				@Override
				public List<WillowUserLovVO> load(int first, int pageSize, String arg2, SortOrder arg3,
						Map<String, Object> arg4) {
					selectedWillowUser = null;
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}
					
					resultList = getLazyWillowUsersList(first, pageSize);
					return resultList;
				}
				
				@Override
				public WillowUserLovVO getRowData(String rowKey) {
					for (WillowUserLovVO willowUserList : willowUsers) {
						if (String.valueOf(willowUserList.getEmployeeNo()).equals(rowKey))
							return willowUserList;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(WillowUserLovVO willowUserLovVO) {
					return willowUserLovVO.getEmployeeNo();
				}
			};
			willowUsers.setPageSize(rowsPerPage);
			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
	}

	public void fetchLOVData() {
		this.dialogHeader = (String) getRequestParameter("WILLOW_USER_LOV_INPUT_HEADER");
		this.lovName = (String) getRequestParameter("WILLOW_USER_LOV_NAME");
		
		String willowUserNameLovParam = (String) getRequestParameter("WILLOW_USER_LOV_INPUT_NAME");
		if (!MALUtilities.isEmptyString(willowUserNameLovParam)) {
			this.willowUserName = (String) getRequestParameter(willowUserNameLovParam);
		}
		
		String willowUserNoLovParam = (String) getRequestParameter("WILLOW_USER_LOV_INPUT_NO");
		if (!MALUtilities.isEmptyString(willowUserNoLovParam)) {
			this.willowUserNo = (String) getRequestParameter(willowUserNoLovParam);
		}
		
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		setLovSelectedWillowUser(null);
		logger.debug("Searched input: " + this.willowUserName);
		List<WillowUserLovVO> list = getLazyWillowUsersList(0, rowsPerPage);
		if(list != null && list.size() > 0){
			willowUsers.setWrappedData(list);
			willowUsers.setRowIndex(0);
			
		}
		
		execCountQuery = false;		
	}

	private List<WillowUserLovVO> getLazyWillowUsersList(int first, int size) {
		try {
			if (initFlag == false) {
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				int  totalCount = 0;
				
				resultList =  quoteRequestService.getWillowUsers(willowUserNo, willowUserName, this.lovName, page);
				if (execCountQuery) {
					totalCount =  quoteRequestService.getWillowUsersCount(willowUserNo, willowUserName, this.lovName);						
				}								
				if (execCountQuery) {
						willowUsers.setRowCount(totalCount);
					}
				
				willowUsers.setWrappedData(resultList);
			}
			
			execCountQuery = false;

		} catch (Exception ex) {
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyWillowUsersList");
		}
		return resultList;
	}
	
	public void onRowSelect(SelectEvent event) {
		WillowUserLovVO willowUserLovVO = (WillowUserLovVO) event.getObject();
		setLovSelectedWillowUser(willowUserLovVO);
	}
	
	public void performLovSelection(){
		setSelectedWillowUser(getLovSelectedWillowUser());
	}

	public LazyDataModel<WillowUserLovVO> getWillowUsers() {
		return willowUsers;
	}

	public void setWillowUsers(LazyDataModel<WillowUserLovVO> willowUsers) {
		this.willowUsers = willowUsers;
	}

	public WillowUserLovVO getSelectedWillowUser() {
		return selectedWillowUser;
	}

	public void setSelectedWillowUser(WillowUserLovVO selectedWillowUser) {
		this.selectedWillowUser = selectedWillowUser;
	}

	public WillowUserLovVO getLovSelectedWillowUser() {
		return lovSelectedWillowUser;
	}

	public void setLovSelectedWillowUser(WillowUserLovVO lovSelectedWillowUser) {
		this.lovSelectedWillowUser = lovSelectedWillowUser;
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

	public String getWillowUserName() {
		return willowUserName;
	}

	public void setWillowUserName(String willowUserName) {
		this.willowUserName = willowUserName;
	}

	public String getWillowUserNo() {
		return willowUserNo;
	}

	public void setWillowUserNo(String willowUserNo) {
		this.willowUserNo = willowUserNo;
	}

	public String getLovName() {
		return lovName;
	}

	public void setLovName(String lovName) {
		this.lovName = lovName;
	}
	
}
