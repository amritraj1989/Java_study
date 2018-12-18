package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuoteRejectCode;
import com.mikealbert.data.entity.RejectReason;

/**
* DAO for QuoteRejectCode Entity
* @author Amritraj
*/

public interface QuoteRejectCodeDAO extends JpaRepository<QuoteRejectCode, String> {
	@Query("FROM QuoteRejectCode qr ORDER BY qr.rejectDescription ASC")
	public List<QuoteRejectCode> getQuoteRejectReasons();
}
