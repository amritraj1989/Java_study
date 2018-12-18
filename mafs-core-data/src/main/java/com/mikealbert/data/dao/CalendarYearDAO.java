package com.mikealbert.data.dao;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CalendarYear;
import com.mikealbert.data.entity.CalendarYearPK;

public interface CalendarYearDAO extends	JpaRepository<CalendarYear, CalendarYearPK>{
		@Query("select MIN(cy.yearSequence) " +
				"from CalendarYear cy where cy.id.cId = ?1 " +
				"and cy.id.calendar = ?2 and cy.yearSequence > " +
				"(select ccy.yearSequence  from CalendarYear ccy where ccy.id.cId= ?1 " +
				"and ccy.id.calendar = ?2 and ccy.id.year = ?3)")
		public BigDecimal	getMinYearSequence(Long cId,String calendar, String year);
		
}
