package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DelayReason;

/**
* DAO for DelayReasonDAO Entity
* @author Raj
*/

public interface DelayReasonDAO extends JpaRepository<DelayReason, String> {
	@Query("FROM DelayReason dr ORDER BY dr.delayReasonDescription ASC")
	public List<DelayReason> getDelayReasons();
	
	@Query("FROM DelayReason dr where dr.delayReasonCode = ?1")
	public DelayReason getDelayReasonByCode(String code);
}
