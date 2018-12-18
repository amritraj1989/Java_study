package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.PhoneNumberType;

/**
* DAO for PhoneNumber Entity
* @author sibley
*/
public interface PhoneNumberTypeDAO extends JpaRepository<PhoneNumberType, Long>{    
}

