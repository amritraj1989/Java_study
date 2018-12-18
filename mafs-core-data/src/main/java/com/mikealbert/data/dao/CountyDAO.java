package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.CountyCode;
import com.mikealbert.data.entity.CountyCodePK;

/**
* DAO for TitleCode Entity
* @author sibley
*/

public interface CountyDAO extends JpaRepository<CountyCode, CountyCodePK> {}
