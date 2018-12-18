package com.mikealbert.vision.specs.admin;

import javax.annotation.Resource;

import com.mikealbert.service.LookupCacheService;
import com.mikealbert.testing.BaseSpec;

public class AdminMiscellaneousTest extends BaseSpec{
	
	@Resource LookupCacheService lookupCacheService;
	
	public boolean testRefreshCache(){
		boolean expected = true;
		lookupCacheService.refreshCache();
		return expected;
	}

}
