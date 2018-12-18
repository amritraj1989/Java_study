package com.mikealbert.data.dao;

import com.mikealbert.exception.MalBusinessException;

public interface FuelUploadDAOCustom {
	public Long	getNextUploadId();
	
	boolean	callValidateFuelData(Long loadId, Long cId) throws MalBusinessException;
	public boolean callFmInterfaceControl(Long loadId) throws MalBusinessException;
}
