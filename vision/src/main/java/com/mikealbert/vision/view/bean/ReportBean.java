package com.mikealbert.vision.view.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.VehicleScheduleService;
import com.mikealbert.service.reporting.VehicleScheduleDataHelper;
import com.mikealbert.service.util.reporting.JasperReportGenUtil;
import com.mikealbert.service.vo.MaintenanceScheduleIntervalPrintVO;
import com.mikealbert.vision.view.ViewConstants;


@Component
public class ReportBean extends BaseBean {
	
	private static final long serialVersionUID = 5459137983854538998L;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private	MaintenanceScheduleService	maintenanceScheduleService;
	
	@Resource
	private	VehicleScheduleService	vehicleScheduleService;
	
	
	@Resource
	private 	VehicleScheduleDataHelper	vehicleScheduleDataHelper;

	private long masterScheduleId;

	public long getMasterScheduleId() {
		return masterScheduleId;
	}

	public void setMasterScheduleId(long masterScheduleId) {
		this.masterScheduleId = masterScheduleId;
	}

	@PostConstruct
	public void init() {
		logger.debug("init is called");
		
	}

	public void runStaticReport() throws IOException, JRException{
		String schedId = this.getRequestParameter(ViewConstants.VIEW_PARAM_SCHEDULE_ID);
		if(schedId.endsWith("?")){
			schedId = schedId.substring(0, schedId.length() -1);
		}

		this.masterScheduleId = Long.parseLong(schedId, 10);
		
		List<MaintenanceScheduleIntervalPrintVO> maintScheduleVOs = maintenanceScheduleService.getMasterSchedulePrint(masterScheduleId);

		// check for special interval and if found format the interval description
		if(maintScheduleVOs.size() > 1 && maintScheduleVOs.get(1).getIntervalDescription().equalsIgnoreCase(" ")) {
			maintScheduleVOs.get(1).setAuthorizationNumber("PO Required");
		}
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		ServletOutputStream outputStream = response.getOutputStream();
	
		List<JRTableModelDataSource> dataSources = new ArrayList<JRTableModelDataSource>();
		/*dataSources.add(new JRTableModelDataSource(this.getHdrTableModelData(maintScheduleVOs)));
		dataSources.add(new JRTableModelDataSource(this.getDtlTableModelData(maintScheduleVOs)));*/
		
		dataSources.add(new JRTableModelDataSource(vehicleScheduleDataHelper.getHdrTableModelData(maintScheduleVOs)));
		dataSources.add(new JRTableModelDataSource(vehicleScheduleDataHelper.getDtlTableModelData(maintScheduleVOs)));
		
		ByteArrayOutputStream reportBytesOut = JasperReportGenUtil.exportAsPdf("maintenanceSchedule",dataSources);
		reportBytesOut.writeTo(outputStream);
		
		FacesContext.getCurrentInstance().responseComplete();
	}
		

}