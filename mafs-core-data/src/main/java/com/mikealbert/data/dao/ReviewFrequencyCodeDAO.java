package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ReviewFrequencyCode;
/**
* DAO for ReviewFrequencyCode Entity
* @author Amritraj
*/

public interface ReviewFrequencyCodeDAO extends JpaRepository<ReviewFrequencyCode, String> {
	
	@Query("SELECT rfc FROM ReviewFrequencyCode rfc where rfc.reviewFrequency = ?1")
	public ReviewFrequencyCode findReviewFrequencyByCode(String reviewFrequency);
}
