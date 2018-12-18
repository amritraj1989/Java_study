package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.PhoneNumber;

/**
* DAO for PhoneNumber Entity
* @author sibley
*/
public interface PhoneNumberDAO extends JpaRepository<PhoneNumber, Long>{    
}

