package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ReportsSpool;

public interface ReportsSpoolDAO extends JpaRepository<ReportsSpool, Long>, ReportsSpoolDAOCustom{
	
		
}
