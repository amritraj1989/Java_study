package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.LogBookEntry;

/**
* DAO for LogBook Entity
* @author sibley
*/

public interface LogBookEntryDAO extends JpaRepository<LogBookEntry, Long> {
	
	@Query("SELECT lbe FROM LogBookEntry lbe WHERE lbe.lbeId IN (?1)")
	public List<LogBookEntry> getLogBookEntries(List<Long> lbeIds);
}
