package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.EarlyTermQuote;

/**
* DAO for EarlyTermQuote Entity
*/

public interface EarlyTermQuoteDAO extends JpaRepository<EarlyTermQuote, Long> {
	
	@Query("from EarlyTermQuote where fmsId = ?1 and acceptFlag = 'N' and rejectFlag = 'N' and quoteDate >=trunc(SYSDATE)")
	public List<EarlyTermQuote>	findUnacceptedEtQuotesByFmsId(Long fmsId);

}
