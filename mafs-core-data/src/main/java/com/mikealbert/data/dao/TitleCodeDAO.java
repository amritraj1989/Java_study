package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.TitleCode;

/**
* DAO for TitleCode Entity
* @author sibley
*/
public interface TitleCodeDAO extends JpaRepository<TitleCode, String> {}
