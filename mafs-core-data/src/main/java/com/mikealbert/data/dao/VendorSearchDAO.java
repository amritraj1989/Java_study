package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ServiceProvider;

public interface VendorSearchDAO  extends JpaRepository<ServiceProvider, Long>, VendorSearchDAOCustom {

	
}
