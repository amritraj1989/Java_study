package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.DriverGradeServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.DriverGrade}(s).
 *
 * @see com.mikealbert.data.entity.DriverGrade
 * @see com.mikealbert.vision.service.DriverGradeServiceImpl
 **/
public interface DriverGradeService {
	public DriverGrade getDriverGrade(String driverGradeCode);	
	public List<DriverGrade> getAllDriverGrades();
	public List<DriverGrade> getExternalAccountDriverGrades(ExternalAccount externalAccount);
}
