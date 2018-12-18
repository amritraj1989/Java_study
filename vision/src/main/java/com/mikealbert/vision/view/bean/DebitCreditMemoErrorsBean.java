package com.mikealbert.vision.view.bean;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.service.DebitCreditMemoService;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class DebitCreditMemoErrorsBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952042784558L;
	
	@Resource DebitCreditMemoService debitCreditMemoService;
	
	@Value("${url.debit.credit.processing}")
	private String debitCreditProcessingBaseURL;	
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private int formatErrorCount;
	private int businessErrorCount;
	private Map<String, Object> nextPageValues;
	private boolean serviceAvailable;

	@PostConstruct
	public void init() {
		super.openPage();
		loadCounts();
	}

	public String cancel() {
		return super.cancelPage();
	}

	private void loadCounts() {
		try {
			formatErrorCount = debitCreditMemoService.requestDebitCreditUploadErrors(debitCreditProcessingBaseURL + "/formatErrors").size();
			businessErrorCount = debitCreditMemoService.requestDebitCreditUploadErrors(debitCreditProcessingBaseURL + "/businessErrors").size();
			serviceAvailable = true;
		} catch (Exception e) {
			serviceAvailable = false;
		}
		
	}

	public String navigateToFormatErrors() {
    	saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
    	setNextPageValues();
   		return ViewConstants.DEBIT_CREDIT_MEMO_FORMAT_ERRORS;
	}

	
	public String navigateToBusinessErrors() {
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		setNextPageValues();
		return ViewConstants.ERROR_SEARCH_DEBIT_CREDIT_MEMO;
	}

	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		return restoreStateValues;		
	}

	private void setNextPageValues() {
		nextPageValues = new HashMap<String, Object>();
		saveNextPageInitStateValues(nextPageValues);
	}

	
	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_DEBIT_CREDIT_MEMO_ERRORS);
		thisPage.setPageUrl(ViewConstants.DEBIT_CREDIT_MEMO_ERRORS);

	}

	@Override
	protected void restoreOldPage() {
	}

	public int getBusinessErrorCount() {
		return businessErrorCount;
	}

	public void setBusinessErrorCount(int businessErrorCount) {
		this.businessErrorCount = businessErrorCount;
	}

	public int getFormatErrorCount() {
		return formatErrorCount;
	}

	public void setFormatErrorCount(int formatErrorCount) {
		this.formatErrorCount = formatErrorCount;
	}

	public boolean isServiceAvailable() {
		return serviceAvailable;
	}

	public void setServiceAvailable(boolean serviceAvailable) {
		this.serviceAvailable = serviceAvailable;
	}

}
