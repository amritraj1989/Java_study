package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.DocumentTransactionType;
import com.mikealbert.data.entity.DocumentTransactionTypePK;

/**
* DAO for DocumentTransactionType Entity
* @author sibley
*/

public interface DocumentTransactionTypeDAO extends JpaRepository<DocumentTransactionType, DocumentTransactionTypePK> {}
