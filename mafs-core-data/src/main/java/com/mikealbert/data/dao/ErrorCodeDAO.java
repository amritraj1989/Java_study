package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ErrorCode;

public interface ErrorCodeDAO extends JpaRepository<ErrorCode, Long> {

}
