package com.mikealbert.ws;

import javax.jws.WebService;

import com.mikealbert.data.vo.VendorMaintCodeVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

@WebService
public interface VendorCodeReprocessing {
	public void reprocessVendorCode(VendorMaintCodeVO code, long serviceProviderId, String interfaceType) throws MalException, MalBusinessException;
}
