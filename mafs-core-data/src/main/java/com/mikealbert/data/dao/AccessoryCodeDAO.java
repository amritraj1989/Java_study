package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.AccessoryCode;

/**
* DAO for AccessoryCode Entity
* @author Singh
*/

public interface AccessoryCodeDAO extends JpaRepository<AccessoryCode, Long> {}
