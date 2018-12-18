package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.PersonnelBase;

public interface WillowUserDAO extends JpaRepository<PersonnelBase, String>, WillowUserDAOCustom {
		
	@Query("from PersonnelBase pb where pb.employeeNo = ?1 ")
	public PersonnelBase findByEmployeeNo(String employeeNo);
}
