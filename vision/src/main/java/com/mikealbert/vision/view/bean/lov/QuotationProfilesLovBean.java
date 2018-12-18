package com.mikealbert.vision.view.bean.lov;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.event.SelectEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.service.QuotationProfileService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class QuotationProfilesLovBean extends BaseBean {

	private static final long serialVersionUID = 2437933046906999010L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource
	private QuotationProfileService quotationProfileService;
	private QuotationProfile selectedProfile;
	private List<QuotationProfile> profileList  = new ArrayList<QuotationProfile>();
	
	private Long cId;
	private String productCode;
	private String accountType;
	private String accountCode;
	private String accountName;
	private Long profileId;
	private Long selectedProfileId;
	
	@PostConstruct
	public void init() {
		try {
			logger.debug("init is called");
			

			String cIdParam = (String) getRequestParameter("cId");
			if (!MALUtilities.isEmptyString(cIdParam)) {
				cId = Long.parseLong(cIdParam);
			}
			accountType = (String) getRequestParameter("accountType");
			accountCode = (String) getRequestParameter("accountCode");
			accountName = (String) getRequestParameter("accountName");
			productCode = (String) getRequestParameter("productCode");
			String qprIdParam = (String) getRequestParameter("profileId");
			if (!MALUtilities.isEmptyString(qprIdParam)) {
				setProfileId(Long.parseLong(qprIdParam));
			}
			
		} catch (Exception ex) {
			handleException("generic.error", new String[]{"loading"}, ex, "init");
			logger.error(ex);
		}
	}

	public void loadRows(){
		profileList  = quotationProfileService.fetchCustomerQuotationProfilesByCriteria(cId, accountType, accountCode, productCode);

	}

	public void onRowSelect(SelectEvent event) {
		QuotationProfile selectedProfile = (QuotationProfile) event.getObject();
		setSelectedProfile(selectedProfile);
	}

	public List<QuotationProfile> getProfileList() {
		return profileList;
	}

	public void setProfileList(List<QuotationProfile> profileList) {
		this.profileList = profileList;
	}

	public QuotationProfile getSelectedProfile() {
		return selectedProfile;
	}

	public void setSelectedProfile(QuotationProfile selectedProfile) {
		if(selectedProfile != null) {
			selectedProfileId = selectedProfile.getQprId();
		}
		this.selectedProfile = selectedProfile;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Long getSelectedProfileId() {
		return selectedProfileId;
	}

	public void setSelectedProfileId(Long selectedProfileId) {
		this.selectedProfileId = selectedProfileId;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}



}