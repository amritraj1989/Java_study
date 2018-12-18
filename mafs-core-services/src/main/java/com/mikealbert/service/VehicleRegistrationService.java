package com.mikealbert.service;

import com.mikealbert.data.entity.VehicleRegistrationV;

public interface VehicleRegistrationService {
	public VehicleRegistrationV getVehicleRegistration(Long fmsId);
	
	public String getVehicleRegistrationPDF(Long fmsId);

}
