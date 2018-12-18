package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.entity.WorkshopCapabilityCode;

@Service("workshopCapabilityCodeService")
public class WorkshopCapabilityCodeServiceImpl implements WorkshopCapabilityCodeService {
	@Resource LookupCacheService lookupCacheService;

	public List<WorkshopCapabilityCode> getCachedVLInternalRepairTypeCapabilityCodes() {
		return lookupCacheService.getVLInternalRepairTypeWorkshopCapabilityCodes();
	}

	public List<WorkshopCapabilityCode> getCachedVLInternalVehicleTypeCapabilityCodes() {
		return lookupCacheService.getVLInternalVehicleTypeWorkshopCapabilityCodes();
	}
	
	public List<WorkshopCapabilityCode> getCachedVLExternalVehicleTypeCapabilityCodes() {
		return lookupCacheService.getVLExternalVehicleTypeWorkshopCapabilityCodes();
	}

	public List<WorkshopCapabilityCode> getCachedVLExternalRepairTypeCapabilityCodes() {
		return lookupCacheService.getVLExternalRepairTypeWorkshopCapabilityCodes();
	}
}
