package com.mikealbert.vision.view.bean;

import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class VehicleOrderStatusSearchBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8852517240727853199L;
	
	private boolean enquiryMode;
	private Long mainPODocId;
	private boolean addNotes = false;

	@PostConstruct
	public void init() {
		openPage();

	}

	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_VEHICLE_ORDER_STATUS);
		thisPage.setPageUrl(ViewConstants.VEHICLE_ORDER_STATUS_SEARCH);
		
		Map<String, Object> map = super.thisPage.getInputValues();		
		String readMode = (String)map.get(ViewConstants.VIEW_MODE_READ);
		setEnquiryMode(Boolean.valueOf(readMode));
		if(isEnquiryMode()) {
			String docIdValue = (String)map.get(ViewConstants.VIEW_PARAM_DOC_ID);
			if(!MALUtilities.isEmptyString(docIdValue)) {
				setMainPODocId(Long.parseLong(docIdValue));
			}
		}else{
			boolean hasPermission = hasPermission("manage_veh_order_status");
			if(hasPermission)
				setAddNotes(true);
			else
				setAddNotes(false);
		}
		
	}

	protected void restoreOldPage() {

	}

	public boolean isEnquiryMode() {
		return enquiryMode;
	}

	public void setEnquiryMode(boolean enquiryMode) {
		this.enquiryMode = enquiryMode;
	}

	public Long getMainPODocId() {
		return mainPODocId;
	}

	public void setMainPODocId(Long mainPODocId) {
		this.mainPODocId = mainPODocId;
	}

	public boolean isAddNotes() {
		return addNotes;
	}

	public void setAddNotes(boolean addNotes) {
		this.addNotes = addNotes;
	}
}
