package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.PreCollectionCheck;

public interface PreCollectionCheckDAO extends JpaRepository<PreCollectionCheck, Long>{
	
	
}
