package com.mikealbert.service;

import java.util.List;
import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.Make;
import com.mikealbert.data.vo.MakeVO;
/**
* Public Interface implemented by {@link com.mikealbert.vision.service.AddressServiceImpl} 
* for interacting with business service methods concerning {@link com.mikealbert.data.entity.Make}(es). 
* 
*  @see com.mikealbert.data.entity.Make
 * @see com.mikealbert.vision.service.MakeServiceImpl
* */
public interface MakeService {
	public List<MakeVO> getMakeVOsByDescription(String description, Pageable pageable);

	public Long getMakeVOsByDescriptionCount(String description);

	public List<Make> getMakesByCode(String code);
	
	public List<Make> getCachedCarModelTypeMakes();
}
