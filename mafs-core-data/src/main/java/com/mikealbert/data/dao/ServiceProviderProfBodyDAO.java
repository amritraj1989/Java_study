package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ServiceProviderProfBody;

/**
 * DAO for ServiceProviderProfBody Entity
 * 
 * @author Scholle
 */
public interface ServiceProviderProfBodyDAO extends JpaRepository<ServiceProviderProfBody, Long> {
}
