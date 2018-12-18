package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.TelematicsMileageDAO;
import com.mikealbert.data.entity.TelematicsMileage;

@Service("telematicsMileageService")
public class TelematicsMileageServiceImpl implements TelematicsMileageService{

	@Resource TelematicsMileageDAO 	telematicsMileageDAO;
	
	@Override
	public List<TelematicsMileage> findByFmsId(Long fmsId) {
		return telematicsMileageDAO.findByFmsId(fmsId);
	}

}
