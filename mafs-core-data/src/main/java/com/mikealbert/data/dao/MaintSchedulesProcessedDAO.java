package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MaintSchedulesProcessed;

public interface MaintSchedulesProcessedDAO extends JpaRepository<MaintSchedulesProcessed, Long> {
	
	//TODO: add a method to get all records that do not yet have a vehicle schedule assigned
	// 1) list is used to get vehicles for schedule assignment
	@Query("select msp from MaintSchedulesProcessed msp where msp.vehicleSchedule IS NULL")
	public List<MaintSchedulesProcessed>	getAllForVehSchedAssignment();
	
	@Query("select msp from MaintSchedulesProcessed msp where msp.vehicleSchedule IS NOT NULL and msp.dateGenerated IS NULL")
	public List<MaintSchedulesProcessed>	getAllForDetermineExpPrintDate();
	
	
	//TODO: add get all method to get all that have an expected print date
	// where the date passed in is between the beginning and the end of the day for the expected print date
	// 3) this will feed the printing process
	@Query("select msp from MaintSchedulesProcessed msp where msp.errorCode IS NULL and msp.expectedPrintDate IS NOT NULL and msp.dateGenerated IS NULL and TRUNC(msp.expectedPrintDate)  <= TRUNC(sysdate) order by TRUNC(msp.expectedPrintDate) asc, msp.fleetMaster.unitNo asc")
	public List<MaintSchedulesProcessed>	getAllQualifiedForSchedulePrint();

	@Query("select count(msp) from MaintSchedulesProcessed msp where msp.errorCode IS NULL and msp.expectedPrintDate IS NOT NULL and msp.dateGenerated IS NULL and TRUNC(msp.expectedPrintDate)  <= TRUNC(sysdate)")
	public long	getAllQualifiedForSchedulePrintCount();

	@Query("select msp from MaintSchedulesProcessed msp where msp.errorCode IS NOT NULL and msp.dateGenerated IS NULL order by TRUNC(msp.expectedPrintDate) asc, msp.fleetMaster.unitNo asc")
	public List<MaintSchedulesProcessed> getErrors();

	@Query("select count(msp) from MaintSchedulesProcessed msp where msp.errorCode IS NOT NULL and msp.dateGenerated IS NULL")
	public long getErrorsCount();

	@Query("select msp from MaintSchedulesProcessed msp where msp.dateGenerated IS NOT NULL and TRUNC(msp.dateGenerated)  >= TRUNC(sysdate) - ?1  order by TRUNC(msp.expectedPrintDate) asc, msp.fleetMaster.unitNo asc")
	public List<MaintSchedulesProcessed> getPreviousPrints(long daysToGoBack);

	@Query("select count(msp) from MaintSchedulesProcessed msp where msp.dateGenerated IS NOT NULL and TRUNC(msp.dateGenerated)  >= TRUNC(sysdate) - ?1")
	public long getPreviousPrintsCount(long daysToGoBack);
	
	@Query("select msp from MaintSchedulesProcessed msp where msp.fleetMaster.fmsId = ?1 and msp.versionts = (select max(msp2.versionts) from MaintSchedulesProcessed msp2 where msp2.fleetMaster.fmsId = msp.fleetMaster.fmsId)")
	public MaintSchedulesProcessed findMaintScheduleProcessRecordByFmsId(Long id);
	
	@Query("select msp from MaintSchedulesProcessed msp where msp.fleetMaster.fmsId= ?1 and msp.vehicleSchedule.vschId = ?2 and msp.dateGenerated is NOT NULL order by msp.dateGenerated  desc")
	public	List<MaintSchedulesProcessed>	findByFmsIdAndVehSchId(Long fmsId, Long vehSchId);
}
