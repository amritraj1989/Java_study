package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.entity.GlCodePK;

public interface GlCodeDAO extends JpaRepository<GlCode, GlCodePK>, GlCodeDAOCustom {

	
}
