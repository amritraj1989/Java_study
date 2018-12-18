package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.ModelType;

/**
* DAO for Model Entity
* @author sibley
*/
public interface ModelTypeDAO extends JpaRepository<ModelType, Long>{}
