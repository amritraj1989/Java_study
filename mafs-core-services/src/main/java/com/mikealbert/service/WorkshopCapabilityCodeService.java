package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.WorkshopCapabilityCode;

public interface WorkshopCapabilityCodeService {
	List<WorkshopCapabilityCode> getCachedVLInternalRepairTypeCapabilityCodes();
	List<WorkshopCapabilityCode> getCachedVLInternalVehicleTypeCapabilityCodes();
	List<WorkshopCapabilityCode> getCachedVLExternalVehicleTypeCapabilityCodes();
	List<WorkshopCapabilityCode> getCachedVLExternalRepairTypeCapabilityCodes();
}
