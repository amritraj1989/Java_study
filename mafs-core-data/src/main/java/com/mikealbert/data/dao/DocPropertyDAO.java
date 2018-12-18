package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DocProperty;

public interface DocPropertyDAO extends JpaRepository<DocProperty, Long>{
	
	@Query("SELECT dp FROM DocProperty dp WHERE  dp.name = ?1")
	public DocProperty findByName(String dpName);

}
