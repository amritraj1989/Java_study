package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.AppResource;

public interface ResourceDAO extends JpaRepository<AppResource, Long>, ResourceDAOCustom {			
	
}
