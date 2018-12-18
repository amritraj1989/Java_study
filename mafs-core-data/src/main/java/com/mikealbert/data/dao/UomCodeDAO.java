package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.UomCode;

/**
* DAO for UomCode Entity
* @author sibley
*/
public interface UomCodeDAO extends JpaRepository<UomCode, String> {

}
