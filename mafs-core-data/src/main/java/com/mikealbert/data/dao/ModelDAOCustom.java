package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.data.vo.ModelSearchResultVO;

public interface ModelDAOCustom {
	public List<ModelSearchResultVO> findModels(ModelSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort);
	public int findModelsCount(ModelSearchCriteriaVO searchCriteria);	
}
