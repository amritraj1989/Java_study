package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.SerializedConfig;

public interface SerializedConfigDAO extends JpaRepository<SerializedConfig, Long> {
	
}
