package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.QuotationModel;

public interface OrderProgressDAO extends JpaRepository<QuotationModel, Long>, OrderProgressDAOCustom {

}
