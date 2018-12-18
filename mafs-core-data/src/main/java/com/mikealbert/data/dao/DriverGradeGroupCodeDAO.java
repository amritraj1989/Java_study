package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DriverGradeGroupCode;

/**
* DAO for DriverGrade Entity
* @author sibley
*/
public interface DriverGradeGroupCodeDAO extends JpaRepository<DriverGradeGroupCode, String>{

@Query("select dgc.description from DriverGradeGroupCode dgc where dgc.driverGradeGroup = ?1")
public String getDriverGradeGroupDesc(String driverGradeGroup);
}