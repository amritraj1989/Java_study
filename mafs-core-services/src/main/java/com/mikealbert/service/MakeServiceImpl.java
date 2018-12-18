package com.mikealbert.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.dao.MakeDAO;
import com.mikealbert.data.entity.Make;
import com.mikealbert.data.vo.MakeVO;

/**
 * Implementation of {@link com.mikealbert.vision.service.AddressService}
 */
@Service("makeService")
public class MakeServiceImpl implements MakeService {
	@Resource LookupCacheService lookupCacheService;
	@Resource MakeDAO makeDAO;

	public List<MakeVO> getMakeVOsByDescription(String description, Pageable pageable){
		List<Make> makes;
		List<MakeVO> makeVOs;
		MakeVO makeVO;

		makeVOs = new ArrayList<MakeVO>();
		makes = makeDAO.findByDescription(description, pageable);
		for(Make make : makes){
			makeVO = new MakeVO();
			makeVO.setMake(make);
			makeVOs.add(makeVO);
		}

		return makeVOs;
	}

	public Long getMakeVOsByDescriptionCount(String description){		
		return makeDAO.findByDescriptionCount(DataUtilities.appendWildCardToRight(description));
	}	

	public List<Make> getMakesByCode(String code) {
		return makeDAO.findByMakeCode(code);
	}
	
	public List<Make> getCachedCarModelTypeMakes() {
		return lookupCacheService.getCarModelTypeMakes();
	}

}
