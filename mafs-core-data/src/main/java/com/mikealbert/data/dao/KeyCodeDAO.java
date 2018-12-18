package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.KeyCode;

public interface KeyCodeDAO extends JpaRepository<KeyCode, Long> {
	
	@Query("select kc from KeyCode kc where kc.fmsFmsId = ?1 order by kc.kcdId asc")
	List<KeyCode>	findByFmsId(Long fmsId);
}
