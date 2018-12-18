package com.mikealbert.ws;

import javax.jws.WebService;

import com.mikealbert.data.vo.StoreLocationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

@WebService
public interface VendorLocationReprocessing {
	public void reprocessLocation(StoreLocationVO location, long serviceProviderId, String interfaceType) throws MalException, MalBusinessException;
}
