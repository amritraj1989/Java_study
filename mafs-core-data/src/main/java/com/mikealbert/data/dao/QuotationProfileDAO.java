package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuotationProfile;

public interface QuotationProfileDAO extends JpaRepository<QuotationProfile, Long>, QuotationProfileDAOCustom{
	@Query("SELECT qpr "
			+ "FROM QuotationProfile qpr "
			+ "WHERE qpr.profileStatus = ?1 "
			+ "    AND qpr.effectiveFrom <= CURRENT_DATE "
			+ "    AND ( qpr.effectiveTo IS NULL OR qpr.effectiveTo >= CURRENT_DATE) "
			+ "    AND NOT EXISTS (SELECT 1 FROM qpr.quoteProfileCusts qpc) ")	
	public List<QuotationProfile> fetchByStatus(String status);
	
	
	@Query("SELECT qpr "
			+ "FROM QuotationProfile qpr "
			+ "WHERE qpr.profileCode = ?1 ")	
	public QuotationProfile getQuotationProfileByProfileCode(String profileCode);
	
}
