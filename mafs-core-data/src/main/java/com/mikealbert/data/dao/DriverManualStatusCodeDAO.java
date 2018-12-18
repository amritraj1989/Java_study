package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.DriverManualStatusCode;

/**
* DAO for ExternalAccont Entity
* @author sibley
*/
public interface DriverManualStatusCodeDAO extends JpaRepository<DriverManualStatusCode, String> {

}
