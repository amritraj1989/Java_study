
package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.CurrencyUnit;

public interface CurrencyUnitDAO extends JpaRepository<CurrencyUnit, String> {

}
