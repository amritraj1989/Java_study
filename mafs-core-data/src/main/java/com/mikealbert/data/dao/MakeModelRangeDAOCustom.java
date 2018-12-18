package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mikealbert.data.vo.MakeModelRangeVO;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;

public interface MakeModelRangeDAOCustom {
	public List<MakeModelRangeVO> findModelRanges(ModelSearchCriteriaVO searchCriteria, Pageable pageable, Sort sort);
	public int findModelRangesCount(ModelSearchCriteriaVO searchCriteria);	
}
