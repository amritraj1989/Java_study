package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ExtAccTranTerm;
import com.mikealbert.data.entity.ExtAccTranTermPK;

public interface ExtAccTranTermDAO extends JpaRepository<ExtAccTranTerm, ExtAccTranTermPK>{
	
}
