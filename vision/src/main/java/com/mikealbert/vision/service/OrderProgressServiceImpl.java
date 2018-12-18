package com.mikealbert.vision.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.DataConstants;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MakeDAO;
import com.mikealbert.data.dao.ModelMarkYearDAO;
import com.mikealbert.data.dao.OrderProgressDAO;
import com.mikealbert.data.dao.ProgressTypeCodeDAO;
import com.mikealbert.data.dao.SupplierProgressHistoryDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.vo.OrderProgressSearchCriteriaVO;
import com.mikealbert.data.vo.OrderProgressSearchResultVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.ModelService;
import com.mikealbert.service.PurchaseOrderService;

@Service("orderProgressService")
@Transactional
public class OrderProgressServiceImpl implements OrderProgressService {
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource CustomerAccountService customerAccountService;
	@Resource OrderProgressDAO orderProgressDAO;
	@Resource ModelMarkYearDAO modelMarkYearDAO;
	@Resource MakeDAO makeDAO;
	@Resource ProgressTypeCodeDAO progressTypeCodeDAO;
	@Resource SupplierProgressHistoryDAO supplierProgressHistoryDAO;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource ModelService modelService;
	@Resource DocDAO docDAO;
	@Resource LogBookService logBookService;
	@Resource PurchaseOrderService purchaseOrderService;
	
	private static final Map<String, String> SORT_BY_MAPPING = new HashMap<String, String>();
	static{
		SORT_BY_MAPPING.put(SORT_BY_UNIT_NO, DataConstants.SEARCH_SORT_FIELD_UNIT_NO);
		SORT_BY_MAPPING.put(SORT_BY_CLIENT, DataConstants.SEARCH_SORT_FIELD_ACCOUNT);
		SORT_BY_MAPPING.put(SORT_BY_CSS, DataConstants.SEARCH_SORT_FIELD_CSS);
		SORT_BY_MAPPING.put(SORT_BY_YEAR, DataConstants.MODEL_SEARCH_SORT_YEAR);
		SORT_BY_MAPPING.put(SORT_BY_MAKE, DataConstants.MODEL_SEARCH_SORT_MAKE);
		SORT_BY_MAPPING.put(SORT_BY_MODEL, DataConstants.MODEL_SEARCH_SORT_MODEL);
		SORT_BY_MAPPING.put(SORT_BY_TRIM, DataConstants.MODEL_SEARCH_SORT_TRIM);
		SORT_BY_MAPPING.put(SORT_BY_ETA, DataConstants.SEARCH_SORT_FIELD_ETA_DATE);
	}	
	
	@Override
	public List<OrderProgressSearchResultVO> findUnits(OrderProgressSearchCriteriaVO criteria, PageRequest page, Sort sort) {
		return orderProgressDAO.findUnits(criteria, page, sort);
	}

	@Override
	public int findUnitsCount(OrderProgressSearchCriteriaVO criteria) {
		return orderProgressDAO.findUnitsCount(criteria);
	}
	
	public String resolveSortByName(String columnName){
		return SORT_BY_MAPPING.get(columnName);		
	}
	
	public List<String> findDistinctYears(OrderProgressSearchCriteriaVO searchCriteria) {
		List<String> distinctYears = modelMarkYearDAO.findDistinctYearByCriteria(DataUtilities.appendWildCardToRight(searchCriteria.getMfgCode()).toLowerCase());
		
		return distinctYears;
	}
	
	public List<String> findDistinctMakes(OrderProgressSearchCriteriaVO searchCriteria) {
		List<String> distinctMakes = makeDAO.findByCriteria(DataUtilities.appendWildCardToRight(searchCriteria.getYear()), 
				DataUtilities.appendWildCardToRight(searchCriteria.getModelType()).toLowerCase(), 
				DataUtilities.appendWildCardToRight(searchCriteria.getMfgCode()).toLowerCase());			
		
		return distinctMakes;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveETAandNotes(SupplierProgressHistory supplierProgressHistory, Long fmsId, String loggedInUserName, String adjustmentNote) throws MalException {
        supplierProgressHistory = supplierProgressHistoryDAO.save(supplierProgressHistory);
        
        FleetMaster fleetMaster = fleetMasterDAO.findById(fmsId).orElse(null);
        ObjectLogBook olb = logBookService.createObjectLogBook(fleetMaster, LogBookTypeEnum.TYPE_BASE_VEH_ORDER_NOTES);			
		logBookService.addEntry(olb, loggedInUserName,  adjustmentNote, null, false);
        
	}

	@Override
	public String getOptionalAccessories(Long docId) {
		return docDAO.getOptionalAccessories(docId);
	}

	@Override
	public Long getManufacturerLeadTime(Long fmsId) {
		return orderProgressDAO.getManufacturerLeadTime(fmsId);
	}

	@Override
	public Long getPDILeadTime(Long fmsId) {
		return orderProgressDAO.getPDILeadTime(fmsId);
		 
	}

	@Override
	public List<String> getPossibleStandardAccessoriesByDocId(Long docId) {
		return orderProgressDAO.getPossibleStandardAccessoriesByDocId(docId);
	}
	
}
