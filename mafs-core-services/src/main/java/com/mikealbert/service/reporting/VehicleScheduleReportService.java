package com.mikealbert.service.reporting;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;

public interface VehicleScheduleReportService {
	public ByteArrayOutputStream getVehicleScheduleReportData(MaintSchedulesProcessed maintSchedulesProcessed) throws MalException;
	public List<MaintenanceScheduleIntervalPrintVO> getFinalScheduleListWithHeader(MaintSchedulesProcessed maintSchedulesProcessed);
}
