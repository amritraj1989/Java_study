package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuoteRequestConfiguration;


public interface QuoteRequestConfigurationDAO extends JpaRepository<QuoteRequestConfiguration, Long> {

	@Query("SELECT qrc FROM QuoteRequestConfiguration qrc "
			+ "INNER JOIN FETCH qrc.vehicleConfiguration vc "
			+ "INNER JOIN FETCH qrc.quoteRequest qr "
			+ "WHERE qrc.quoteRequest.qrqId = ?1 ")
	public List<QuoteRequestConfiguration> getQuoteRequestConfigurationsByQuoteRequest(long qrcId);

}
