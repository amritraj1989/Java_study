package com.mikealbert.service;

import com.mikealbert.data.entity.FuelUpload;
import com.mikealbert.exception.MalBusinessException;

public interface FuelUploadService {
	 void	saveFuelUploadData( FuelUpload fuelUpload);
	 Long getNextFuelUploadLoadId();
	 boolean	validateFuelData(Long loadId, Long cId) throws MalBusinessException;
	 public boolean saveFmInterfaceControl(Long loadId) throws MalBusinessException;
}
