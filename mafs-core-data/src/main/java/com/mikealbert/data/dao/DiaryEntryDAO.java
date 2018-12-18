package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.DiaryEntry;

public interface DiaryEntryDAO extends JpaRepository<DiaryEntry, Long> {}
