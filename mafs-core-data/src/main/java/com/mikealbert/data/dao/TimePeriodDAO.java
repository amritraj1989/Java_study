package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.TimePeriod;

public interface TimePeriodDAO extends JpaRepository<TimePeriod, Long>{
	@Query("select MIN(endDate) from TimePeriod where calendarYear.id.cId= ?1 " +
			"and calendarYear.id.calendar= ?2 and calendarYear.id.year = ?3 " +
			"and timePeriodSequence > ?4")
	public Date getMinEndDate(Long cId,String calendar, String year, BigDecimal tpSeqNo);
	
	@Query("SELECT tp FROM TimePeriod tp WHERE trunc(tp.startDate) <= trunc(?1) AND trunc(tp.endDate) >= trunc(?2) AND tp.calendarYear.id.calendar = ?3 AND tp.calendarYear.id.cId = ?4 ")
	public TimePeriod findByStartDateAndEndDateAndCalendarAndCorporateId(Date startDate, Date endDate, String calendarType, Long cId);
	
	@Query("SELECT tp FROM TimePeriod tp WHERE trunc(tp.startDate) = trunc(?1) AND tp.calendarYear.id.cId = ?2 ")
	public TimePeriod findByStartDateAndContextId(Date startDate, Long cId);
	
	@Query("SELECT MAX(billingDate) FROM TimePeriod tp WHERE tp.calendarYear.id.cId = ?1 AND TRUNC(tp.billingDate) < ?2")
	public Date findPreviousBillingDate(Long cId, Date revisionEffectiveDate);
	
	@Query("SELECT tp FROM TimePeriod tp WHERE tp.sequenceNo = ?1 ")
	public TimePeriod findBySequenceNo(Long sequenceNo);
	
}
