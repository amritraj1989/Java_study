package com.mikealbert.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.vo.MakeModelRangeVO;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.data.vo.ModelSearchResultVO;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.ModelSearchServiceImpl} 
 *
 * @see com.mikealbert.data.entity.Model
 * @see com.mikealbert.vision.service.ModelSearchServiceImpl
 **/
public interface ModelSearchService {	
	static final String SORT_BY_YEAR = "year";
	static final String SORT_BY_MAKE = "make";
	static final String SORT_BY_MODEL = "model";
	static final String SORT_BY_TRIM = "trim";
	static final String SORT_BY_MFG_CODE = "mfgCode";	
			
	public List<String> findDistinctYears(ModelSearchCriteriaVO searchCriteria);
	
	public List<String> findDistinctMakes(ModelSearchCriteriaVO searchCriteria);
	public List<String> findDistinctMfgCodes(String term, Pageable page);
	
	public List<MakeModelRangeVO> findModelRanges(ModelSearchCriteriaVO searchCriteria, Pageable page);
	public int findModelRangesCount(ModelSearchCriteriaVO searchCriteria);
	
	public List<ModelType> getModelTypes();
	
	public List<ModelSearchResultVO> findModels(ModelSearchCriteriaVO searchCriteria, Pageable page, Sort sort);
	public int findModelsCount(ModelSearchCriteriaVO searchCriteria);
	
	public String resolveSortByName(String columnName);	
	
}
