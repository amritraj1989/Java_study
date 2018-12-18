package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.TelematicsMileage;

public interface TelematicsMileageService {

	public List<TelematicsMileage> findByFmsId(Long fmsId);	
}
