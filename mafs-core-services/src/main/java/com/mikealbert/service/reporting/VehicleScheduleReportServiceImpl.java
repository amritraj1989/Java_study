package com.mikealbert.service.reporting;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

import org.springframework.stereotype.Service;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.ContractReportLanguageDAO;
import com.mikealbert.data.dao.MaintenancePreferencesDAO;
import com.mikealbert.data.entity.MaintSchedulesProcessed;
import com.mikealbert.data.entity.MasterSchedule;
import com.mikealbert.data.entity.VehicleSchedule;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.VehicleScheduleService;
import com.mikealbert.service.util.reporting.JasperReportGenUtil;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;
import com.mikealbert.util.MALUtilities;
@Service("vehicleScheduleReportService")
public class VehicleScheduleReportServiceImpl implements  VehicleScheduleReportService{

	@Resource
	private VehicleScheduleService vehicleScheduleService;
	@Resource
	private MaintenanceScheduleService maintenanceScheduleService;
	@Resource
	private QuotationService quoteService;
	@Resource
	private ContractService contractService;

	@Resource
	private ContractReportLanguageDAO contractReportLanguageDAO;
	@Resource
	private MaintenancePreferencesDAO maintenancePreferencesDAO;
	
	@Resource
	private 	VehicleScheduleDataHelper	vehicleScheduleDataHelper;

	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	public ByteArrayOutputStream getVehicleScheduleReportData(MaintSchedulesProcessed maintSchedulesProcessed) throws MalException {
		ByteArrayOutputStream retVal = null;
		try {
			if (maintSchedulesProcessed == null) {
				return null;
			}
			logger.info("*** Start of Generating vehicle Schedule Report Data for VSCH ID:" + maintSchedulesProcessed.getVehicleSchedule().getVschId());
			List<MaintenanceScheduleIntervalPrintVO> maintScheduleVOs = getFinalScheduleListWithHeader(maintSchedulesProcessed);
			List<JRTableModelDataSource> dataSources = new ArrayList<JRTableModelDataSource>();
			dataSources.add(new JRTableModelDataSource(vehicleScheduleDataHelper.getHdrTableModelData(maintScheduleVOs)));
			dataSources.add(new JRTableModelDataSource(vehicleScheduleDataHelper.getDtlTableModelData(maintScheduleVOs)));
			HashMap<String, String> reportParams = vehicleScheduleDataHelper.getVehicleSchedReportParams(maintSchedulesProcessed);
			retVal = JasperReportGenUtil.exportAsPdf("PurchaseInstrument", dataSources, reportParams);
			logger.info("*** End of Generating vehicle Schedule Report Data for VSCH ID:" + maintSchedulesProcessed.getVehicleSchedule().getVschId());
			return retVal;
		} catch (JRException jre) {
			logger.error(jre);
			throw new MalException("custom.message", new String[] {"Error occured while generating report" }, jre);
		} catch (FileNotFoundException fnf) {
			logger.error(fnf);
			throw new MalException("custom.message", new String[] {"Error occured while generating report"  }, fnf);
		} catch (Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", new String[] { "getting Vehicle schedule report data" }, ex);
		}
		
	}

	
	public List<MaintenanceScheduleIntervalPrintVO> getFinalScheduleListWithHeader(MaintSchedulesProcessed maintSchedulesProcessed) {
		Long sequence = maintSchedulesProcessed.getVehicleSchedule().getVehSchSeq();
		MasterSchedule masterSchedule = maintSchedulesProcessed.getVehicleSchedule().getMasterSchedule();
		VehicleSchedule vehicleSchedule = maintSchedulesProcessed.getVehicleSchedule();
		int startingOdo = maintSchedulesProcessed.getVehicleSchedule().getStartOdoReading() != null ? maintSchedulesProcessed.getVehicleSchedule()
				.getStartOdoReading().intValue() : 0;
		Integer lastCompletedInterval = vehicleScheduleService.getLastCompletedInterval(vehicleSchedule);
		lastCompletedInterval = lastCompletedInterval != null ? lastCompletedInterval: 0; 
		
		boolean intervalCompleted = false;
		if(lastCompletedInterval > 0) {
			intervalCompleted = true;
		}
		
		int lastCompletedIntervalToCheck = lastCompletedInterval > 0 ? lastCompletedInterval-1 : 0;  // have to convert from 1 based to 0 based
		int lastCompletedIntervalMileage = 0;
		if(intervalCompleted) {
			lastCompletedIntervalMileage = maintenanceScheduleService.getIntervalIndexMileage(lastCompletedIntervalToCheck, masterSchedule, startingOdo); 
		}
		
		int startingMileage = lastCompletedIntervalMileage > startingOdo ? lastCompletedIntervalMileage+1 : startingOdo;  // +1 to move past last completed miles
		
		BigDecimal conversionFactor = new BigDecimal(1);
		if(maintSchedulesProcessed != null) {
			conversionFactor = maintenanceScheduleService.getConversionFactorForSchedule(maintSchedulesProcessed);
		}
		List<MaintenanceScheduleIntervalPrintVO> voList = new ArrayList<MaintenanceScheduleIntervalPrintVO>();
		MaintenanceScheduleIntervalPrintVO hdrPrintVO = maintenanceScheduleService.getMasterSchedulePrintHeader(masterSchedule.getMschId());
		voList.add(hdrPrintVO);
		
		List<MaintenanceScheduleIntervalPrintVO> maintScheduleVOList = maintenanceScheduleService
				.getMasterScheduleIntervalList(masterSchedule.getMschId(), startingMileage, conversionFactor);

		for (int i = 0; i < maintScheduleVOList.size(); i++) {
			MaintenanceScheduleIntervalPrintVO newVO = maintScheduleVOList.get(i);
			if(maintScheduleVOList.size() ==1 || MALUtilities.isEmpty(newVO.getIntervalDescription())) {
				newVO.setIntervalDescription(" ");
				newVO.setAuthorizationNumber("PO Required");
			} else {
				lastCompletedInterval = lastCompletedInterval+1;
				newVO.setAuthorizationNumber(vehicleScheduleService.getAuthorizationNumber(sequence, vehicleScheduleService.calculateIntervalCode(lastCompletedInterval)));
			}
			voList.add(newVO);
		}

		return voList;
	}
	
	

}
