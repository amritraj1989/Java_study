package com.mikealbert.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mikealbert.data.DataConstants;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.dao.MakeDAO;
import com.mikealbert.data.dao.MakeModelRangeDAO;
import com.mikealbert.data.dao.ModelDAO;
import com.mikealbert.data.dao.ModelMarkYearDAO;
import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.vo.MakeModelRangeVO;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.data.vo.ModelSearchResultVO;


/**
 * A mfg code search will yield the models with that exact mfg code. </br>
 * 
 * A driver name search (all or partial, i.e. implicit wild card to the right will yield 
 * all units currently in service or on contract assigned to that driver </br>
 * 
 * A VIN search (last six digits), will yield only units that contain all of the last 
 * six digits of the VIN should be displayed.</br>
 * 
 * A license plate number search will yield the most current unit in TAL with that 
 * license plate number. If TAL does not have the plate number, Fleet Masters Reg No is searched.</br>
 * 
 * A client's fleet reference number will yield only units with that fleet reference -- same or different clients </br>
 * 
 * A client name or number search will yield only units that are currently in service or on contract with that client</br>
 * 
 * A PO number search will yield units with purchase orders containing all of the PO number. </br>
 * 
 * A service provider (maintenance supplier partial or full) search in conjunction with the payee invoice number will yield units 
 * that were serviced by that service provider and payee invoice number</br>
 * 
 * A MAFS invoice number search will yield all units that appears on that MAFS invoice</br>
 * 
 * Searching by PO number, vendor & vendor invoice number and MAFS invoice number, returned data for client and driver will be what is reflected on the PO/invoice as of: </br>
 *     
 *     The actual start date, if null </br>
 *     The planned start date; if null </br>
 *     The most recent date.</br>
 *     
 * Implementation of {@link com.mikealbert.vision.service.VehicleSearchService}
 */
@Service("modelSearchService")
public class ModelSearchServiceImpl implements ModelSearchService {
	@Resource LookupCacheService lookupCacheService;
	@Resource MakeDAO makeDAO;
	@Resource ModelMarkYearDAO modelMarkYearDAO;
	@Resource ModelDAO modelDAO;
	@Resource MakeModelRangeDAO makeModelRangeDAO;
		
	private static final Map<String, String> SORT_BY_MAPPING = new HashMap<String, String>();
	static{
		SORT_BY_MAPPING.put(SORT_BY_YEAR, DataConstants.MODEL_SEARCH_SORT_YEAR);
		SORT_BY_MAPPING.put(SORT_BY_MAKE, DataConstants.MODEL_SEARCH_SORT_MAKE);
		SORT_BY_MAPPING.put(SORT_BY_MODEL, DataConstants.MODEL_SEARCH_SORT_MODEL);
		SORT_BY_MAPPING.put(SORT_BY_TRIM, DataConstants.MODEL_SEARCH_SORT_TRIM);
		SORT_BY_MAPPING.put(SORT_BY_MFG_CODE, DataConstants.MODEL_SEARCH_SORT_MFG_CODE);
	}	
	
	public List<String> findDistinctYears(ModelSearchCriteriaVO searchCriteria){
		List<String> distinctYears;
		
		distinctYears = modelMarkYearDAO.findDistinctYearByCriteria(DataUtilities.decodeNullString(searchCriteria.getMfgCode(), "%").toLowerCase());
		
		return distinctYears;
	}
	
	/**
	 * Retrieves a context sensitive list of vehicle makes based on the search 
	 * criteria. 
	 * @param ModelSearchCriteriaVO The view object that represents the model search criteria
	 * @return A distinct list of makes' description
	 */
	public List<String> findDistinctMakes(ModelSearchCriteriaVO searchCriteria){
		List<String> distinctMakes;			
		
		distinctMakes = makeDAO.findByCriteria(DataUtilities.decodeNullString(searchCriteria.getYear(), "%"), 
				DataUtilities.decodeNullString(searchCriteria.getModelType(), "%").toLowerCase(), 
				DataUtilities.decodeNullString(searchCriteria.getMfgCode(), "%").toLowerCase());			
		
		return distinctMakes;
	}
	
	
	public List<String> findDistinctMfgCodes(String term, Pageable page) {
		List<String> distinctMfgCodes = modelDAO.findMfgCodes(DataUtilities.appendWildCardToRight(term).toLowerCase(), page);
		return distinctMfgCodes;
	}
	
	
	public List<MakeModelRangeVO> findModelRanges(ModelSearchCriteriaVO searchCriteria, Pageable page){
		List<MakeModelRangeVO> modelRanges;
	
		modelRanges = makeModelRangeDAO.findModelRanges(searchCriteria, page, null);

		return modelRanges;
	}
	
	public int findModelRangesCount(ModelSearchCriteriaVO searchCriteria){
		int count;
	
		count = makeModelRangeDAO.findModelRangesCount(searchCriteria);

		return count;
	}

	public List<ModelType> getModelTypes(){
		return lookupCacheService.getModelTypes();
	}
	
	public List<ModelSearchResultVO> findModels(ModelSearchCriteriaVO searchCriteria, Pageable page, Sort sort){
		List<ModelSearchResultVO> models;
	
		models = modelDAO.findModels(searchCriteria, page, sort);

		return models;
	}
	
	public int findModelsCount(ModelSearchCriteriaVO searchCriteria){
		int count;
	
		count = modelDAO.findModelsCount(searchCriteria);

		return count;
	}	
	
	public String resolveSortByName(String columnName){
		return SORT_BY_MAPPING.get(columnName);		
	}
	

}
