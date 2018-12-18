package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.DriverAllocCode;

/**
* DAO for DriverAllocCode Entity
* @author sibley
*/
public interface DriverAllocCodeDAO extends JpaRepository<DriverAllocCode, String> {

}
