package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.DriverGradeDAO;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.exception.MalException;

/**
 * Implementation of {@link com.mikealbert.vision.service.DriverGradeService}
 */
@Service("driverGradeService")
public class DriverGradeServiceImpl implements DriverGradeService {
	@Resource
	DriverGradeDAO driverGradeDAO;
	
	/**
	 * Finds the driver grade for the provided driver grade code; Used to view driver grade in Driver Add Edit
	 * @param  driverGradeCode Value used to find driver grade
	 * @return Driver grade for the provided driver grade code
	 */
	public DriverGrade getDriverGrade(String driverGradeCode){
		try{
			return driverGradeDAO.findById(driverGradeCode).orElse(null);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding a Driver Grade by code" }, ex);				
		}
	}
	
	/**
	 * Retrieves a list of all driver grades
	 * @return List of all driver grades
	 */
	public List<DriverGrade> getAllDriverGrades() {
		try{
			return driverGradeDAO.findAll();
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding All Driver Grades" }, ex);				
		}
	}
	
	/**
	 * Finds driver grades for the provided account.
	 * @param  externalAccount Account used to find diver grades
	 * @return List of driver grades for the provided account
	 */
	public List<DriverGrade> getExternalAccountDriverGrades(ExternalAccount externalAccount){
		try{
			return driverGradeDAO.getByExternalAccountCodeAndType(externalAccount.getExternalAccountPK().getAccountCode(), externalAccount.getExternalAccountPK().getAccountType());
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding All Driver Grades by ExternalAccout Code and Type" }, ex);				
		}		
	}
}
