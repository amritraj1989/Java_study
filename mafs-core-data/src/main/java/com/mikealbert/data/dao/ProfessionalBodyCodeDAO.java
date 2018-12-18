package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ProfessionalBodyCode;

/**
 * DAO for professionalBodyCode Entity
 * 
 * @author Scholle
 */
public interface ProfessionalBodyCodeDAO extends JpaRepository<ProfessionalBodyCode, Long> {
}
