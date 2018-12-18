package com.mikealbert.vision.view.bean.lov;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.SupplierProgressHistoryDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.vo.ActiveQuoteVO;
import com.mikealbert.data.vo.StockUnitsLovVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.DocumentService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.QuoteRequestService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.bean.BaseBean;

@Component("stockUnitsLovBean")
@Scope("view")
public class StockUnitsLovBean extends BaseBean  {

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	
	@Resource 
	FleetMasterService fleetMasterService;
	@Resource 
	QuoteRequestService quoteRequestService;
	@Resource 
	DocumentService documentService;
	@Resource
	SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	@Resource
	QuotationService quotationService;
	
	private LazyDataModel<StockUnitsLovVO> stockUnits;
	

	private StockUnitsLovVO selectedStockUnit;

	private boolean execCountQuery = false;
	private boolean initFlag = true;
	private int rowsPerPage = ViewConstants.RECORDS_PER_PAGE;
	private static final String  DATA_TABLE_UI_ID ="stockUnitDataTableId";
	boolean isStart = true;
	List<StockUnitsLovVO> resultList;
	private String provider;

	private int pageNumber = 0;
	private int lovRecordsPerPage;
	private Long stockUnitFmsId;
	private List<String> standardEquipment;
	private List<String> modelEquipment;
	private List<String> dealerEquipment;
	private List<ActiveQuoteVO> activeQuoteList;  
	private Date etaDate;
	private String vehicleDescSearch;
	private String unitNoSearch;
	
	
	@PostConstruct
	public void init() {
		try{
			setLovRecordsPerPage();
			isStart = true;			
			resultList = new ArrayList<StockUnitsLovVO>();
			stockUnits = new LazyDataModel<StockUnitsLovVO>() {
				@Override
				public List<StockUnitsLovVO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> arg4) {
					setPageSize(rowsPerPage);
					if (isStart) {
						first = 0;
						isStart = false;
					}

					if (isStart == false) {
						resultList = getLazyStockUnits(first, pageSize, sortField, sortOrder);
					}
					
					return resultList;
				}
				
				@Override
				public StockUnitsLovVO getRowData(String rowKey) {
					for (StockUnitsLovVO stockUnit : resultList) {
						if (String.valueOf(stockUnit.getFmsId()).equals(rowKey))
							return stockUnit;
					}
					return null;
				}
				
				@Override
				public Object getRowKey(StockUnitsLovVO stockUnit) {
					return stockUnit.getFmsId();
				}
			};
			stockUnits.setPageSize(ViewConstants.RECORDS_PER_PAGE);
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "init");
		}
		
	}
	
	public void performSearch() {

		this.pageNumber = 0;
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		this.selectedStockUnit = null;		
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null)
			pfDataTable.setFirst(0);
	}
	
	public void fetchStockUnits() {
		
		this.pageNumber = 0;
		unitNoSearch = null;
		vehicleDescSearch = null;
		isStart = true;
		execCountQuery = true;
		initFlag = false;
		this.selectedStockUnit = null;
		DataTable pfDataTable = ((DataTable) getComponent(DATA_TABLE_UI_ID));
		if(pfDataTable != null){
			RequestContext.getCurrentInstance().update(DATA_TABLE_UI_ID);
			RequestContext.getCurrentInstance().update("stockUnitsSelectPanel");
			pfDataTable.setFirst(0);
		}
	}
	
	
	private List<StockUnitsLovVO> getLazyStockUnits(int first, int size, String sortField, SortOrder sortOrder) {
		try{
			if (initFlag == false) {
				Sort sort = null;
				int pageIdx = (first == 0) ? 0 : (first / size);
				PageRequest page = new PageRequest(pageIdx,size);
				setPageNumber(page.getPageNumber());
				
				if(MALUtilities.isNotEmptyString(sortField)){
					if (sortOrder.name().equalsIgnoreCase(SortOrder.DESCENDING.toString())) {
						 sort = new Sort(Sort.Direction.DESC, fleetMasterService.resolveStockUnitLovSortByName(sortField));
					}else{
						 sort = new Sort(Sort.Direction.ASC, fleetMasterService.resolveStockUnitLovSortByName(sortField));
					}
				}

				resultList = fleetMasterService.findStockUnitsList(unitNoSearch, vehicleDescSearch, page, sort);
				
				stockUnits.setPageSize(rowsPerPage);
				if (execCountQuery){
					int count = fleetMasterService.findStockUnitsListCount(unitNoSearch, vehicleDescSearch);
					stockUnits.setRowCount(count);					
				}
			}

			
		}catch(Exception ex){
			handleException("generic.error.occured.while", new String[] { "loading LOV" }, ex, "getLazyStockUnits");
		}
		return resultList;
		
	}
	
	public void populateETADate() {
		Doc doc;
		try {
			doc = documentService.getMainPODocOfStockUnit(selectedStockUnit.getFmsId());
		} catch (Exception ex) {
			if (ex instanceof MalException) {
				throw (MalException) ex;
			} else {
				throw new MalException("generic.error.occured.while", new String[] { "getting Stock Unit Main PO" }, ex);
			}
		}

		if(doc != null) {
			SupplierProgressHistory sph = supplierProgressHistoryDAO.findSupplierProgressHistoryForDocAndTypeById(doc.getDocId(), "14_ETA");
			if(sph != null) {
				etaDate = sph.getActionDate();
			}
		}
	}
	

	public void populateStockUnitEquipment() {
		standardEquipment = fleetMasterService.getStandardEquipmentForFmsId(selectedStockUnit.getFmsId());
		modelEquipment = fleetMasterService.getModelEquipmentForFmsId(selectedStockUnit.getFmsId());
		dealerEquipment = fleetMasterService.getDealerEquipmentForFmsId(selectedStockUnit.getFmsId());
	}

	public void populateActiveQuotes() {
		activeQuoteList = quotationService.getAllActiveQuotesByFmsId(selectedStockUnit.getFmsId());
		
	}
	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public LazyDataModel<StockUnitsLovVO> getStockUnits() {
		return stockUnits;
	}

	public void setStockUnits(LazyDataModel<StockUnitsLovVO> stockUnits) {
		this.stockUnits = stockUnits;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public StockUnitsLovVO getSelectedStockUnit() {
		return selectedStockUnit;
	}
	
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	
	public void setSelectedStockUnit(StockUnitsLovVO selectedStockUnit) {
		
		this.selectedStockUnit = selectedStockUnit;
		
		if(selectedStockUnit != null){
			setStockUnitFmsId(selectedStockUnit.getFmsId());
		}
		
	}

	public void onRowSelect(SelectEvent event) {
		StockUnitsLovVO selectedStockUnit = (StockUnitsLovVO) event.getObject();
		setSelectedStockUnit(selectedStockUnit);
	}
	
	public void onSortOperation(SortEvent event) {
		setSelectedStockUnit(null);
	 }
	
	public int getLovRecordsPerPage() {
		return lovRecordsPerPage;
	}

	public void setLovRecordsPerPage() {
		this.lovRecordsPerPage = ViewConstants.RECORDS_PER_PAGE;
	}

	public Long getStockUnitFmsId() {
		return stockUnitFmsId;
	}

	public void setStockUnitFmsId(Long stockUnitFmsId) {
		this.stockUnitFmsId = stockUnitFmsId;
	}


	public List<String> getStandardEquipment() {
		return standardEquipment;
	}


	public void setStandardEquipment(List<String> standardEquipment) {
		this.standardEquipment = standardEquipment;
	}


	public List<String> getModelEquipment() {
		return modelEquipment;
	}


	public void setModelEquipment(List<String> modelEquipment) {
		this.modelEquipment = modelEquipment;
	}


	public List<String> getDealerEquipment() {
		return dealerEquipment;
	}


	public void setDealerEquipment(List<String> dealerEquipment) {
		this.dealerEquipment = dealerEquipment;
	}


	public List<ActiveQuoteVO> getActiveQuoteList() {
		return activeQuoteList;
	}


	public void setActiveQuoteList(List<ActiveQuoteVO> activeQuoteList) {
		this.activeQuoteList = activeQuoteList;
	}


	public Date getEtaDate() {
		return etaDate;
	}


	public void setEtaDate(Date etaDate) {
		this.etaDate = etaDate;
	}


	public String getVehicleDescSearch() {
		return vehicleDescSearch;
	}


	public void setVehicleDescSearch(String vehicleDescSearch) {
		this.vehicleDescSearch = vehicleDescSearch;
	}


	public String getUnitNoSearch() {
		return unitNoSearch;
	}


	public void setUnitNoSearch(String unitNoSearch) {
		this.unitNoSearch = unitNoSearch;
	}

	
}
