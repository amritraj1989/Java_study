package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.LogBookTypeVO;


@Component
@Scope("view")
public class LaunchUnitUpfitNotesBean extends StatefulBaseBean {
	private static final long serialVersionUID = 7702705961310899474L;
	
	@Resource FleetMasterDAO fleetMasterDAO;
	
	private String fmsId;
	private FleetMaster selctedFleetMaster ;	
	private List<LogBookTypeVO> logBookType;	

	@PostConstruct
	public void init() {
		openPage();		
		
		try {
			setLogBookType(new ArrayList<LogBookTypeVO>());
			getLogBookType().add(
					new LogBookTypeVO(
							LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES, LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES, ViewConstants.LABEL_PROGRESS_CHASING, true));
			getLogBookType().add(
					new LogBookTypeVO(
							LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES, true));			
			selctedFleetMaster = fleetMasterDAO.findById(Long.parseLong(getFmsId())).orElse(null);
		} catch (Exception e) {
			logger.error(e);
			if(e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while", " loading upfit notes screen.");
			}
		}
	}

	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_UNIT_UPFIT_NOTES);
		thisPage.setPageUrl(ViewConstants.UNIT_UPFIT_NOTES);
		
		Map<String, Object> map = super.thisPage.getInputValues();		
		setFmsId((String)map.get(ViewConstants.VIEW_PARAM_FMS_ID));
	}
	
	protected void restoreOldPage() {

	}
	
	public String getFmsId() {
		return fmsId;
	}

	public void setFmsId(String fmsId) {
		this.fmsId = fmsId;
	}

	public FleetMaster getSelctedFleetMaster() {
		return selctedFleetMaster;
	}
	
	public void setSelctedFleetMaster(FleetMaster selctedFleetMaster) {
		this.selctedFleetMaster = selctedFleetMaster;
	}

	public List<LogBookTypeVO> getLogBookType() {
		return logBookType;
	}

	public void setLogBookType(List<LogBookTypeVO> logBookType) {
		this.logBookType = logBookType;
	}


}
