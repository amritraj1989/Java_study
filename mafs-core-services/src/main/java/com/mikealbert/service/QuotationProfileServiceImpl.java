/**
 * 
 */
package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.InterestTypeCodeDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.QuotationProfileDAO;
import com.mikealbert.data.dao.ReviewFrequencyCodeDAO;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.InterestTypeCode;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.ReviewFrequencyCode;
import com.mikealbert.util.MALUtilities;


@Service("quotationProfileService")
public class QuotationProfileServiceImpl implements QuotationProfileService {

	private static final long serialVersionUID = 1L;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource
	private QuotationProfileDAO quotationProfileDAO;
	
	@Resource InterestTypeCodeDAO interestTypeCodeDAO;
	@Resource ReviewFrequencyCodeDAO reviewFrequencyCodeDAO;
	
	public List<QuotationProfile> fetchCustomerQuotationProfiles(long corpId, String accountType, String accountCode){
		List<QuotationProfile> quoteProfiles = new ArrayList<QuotationProfile>();
		quoteProfiles = quotationProfileDAO.fetchCustomerQuotationProfiles(corpId, accountType, accountCode);
		
		if(quoteProfiles != null && quoteProfiles.size() > 0) {
			Collections.sort(quoteProfiles, new Comparator<QuotationProfile>() { 
				public int compare(QuotationProfile qp1, QuotationProfile qp2) { 
					return qp1.getProfileCode().toLowerCase().compareTo(qp2.getProfileCode().toLowerCase()); 
				}
			});
		}
		
		return quoteProfiles;
	}
	
	public List<QuotationProfile> getQuotationProfilesByLeaseElement(long leaseElementId, ExternalAccount parentAccount){
		return quotationProfileDAO.getQuotationProfilesByLeaseElement(leaseElementId, parentAccount);
	}
	
	/**
	 * Retrieves a list of Generic Quotation Profiles.
	 * 
	 * Generic profiles are those that do not have a client link,
	 * are approved, and have not expired.
	 * 
	 * @return List of Quotation Profiles that are approved, not linked to a client, have not expired.
	 */
	public List<QuotationProfile> getGenericQuotationProfiles() {
		return getGenericQuotationProfilesCore();
	}
	
	public Long countQuotationProfilesByLeaseElement(long leaseElementId, ExternalAccount parentAccount){
		return quotationProfileDAO.countQuotationProfilesByLeaseElement(leaseElementId, parentAccount);
	}
	
	@Override
	public String getQuotationProfileStatus(Long qmdId) {
		QuotationModel quotationModel = quotationModelDAO.findById(qmdId).orElse(null);
		String profileStatus = null;
		if (!MALUtilities.isEmpty(quotationModel)) {
			profileStatus = quotationModel.getQuotation().getQuotationProfile().getProfileStatus();
		}

		return profileStatus;
	}	

	@Override
	public InterestTypeCode getInterestTypeByCode(String interestType) {
		return interestTypeCodeDAO.findInterestTypeByCode(interestType);
	}

	@Override
	public ReviewFrequencyCode getReviewFrequencyByCode(String reviewFrequencyCode) {
		return reviewFrequencyCodeDAO.findReviewFrequencyByCode(reviewFrequencyCode);
	}

	@Override
	public String getProductTypeByProfile(Long qprId) {

		return quotationProfileDAO.getProductTypeByProfile(qprId);
	}

	@Override
	public QuotationProfile getQuotationProfileById(Long qprId) {
		return quotationProfileDAO.findById(qprId).orElse(null);
	}

	@Override
	public QuotationProfile getQuotationProfileByProfileCode(String profileCode) {
		return quotationProfileDAO.getQuotationProfileByProfileCode(profileCode);
	}

	@Override
	public boolean isProfileValidForAccount(Long corporateId, String accountCode, Long profileId) {
		boolean isValid = false;
		List<QuotationProfile> quotationProfiles;
		
		quotationProfiles = quotationProfileDAO.fetchCustomerQuotationProfiles(corporateId, "C", accountCode);
		quotationProfiles.addAll(getGenericQuotationProfilesCore());
		for(QuotationProfile profile : quotationProfiles) {
			if(profileId.equals(profile.getQprId())) {
				isValid = true;
				break;
			}
		}
		
		return isValid;
	}
	
	/**
	 * Core call that retrieves a list of Generic Quotation Profiles.
	 * 
	 * Generic profiles are those that do not have a client link,
	 * are approved, and have not expired.
	 * 
	 * @return List of Quotation Profiles that are approved, not linked to a client, have not expired.
	 */
	private List<QuotationProfile> getGenericQuotationProfilesCore() {
		return quotationProfileDAO.fetchByStatus("APPROVED");
	}
	
	public List<QuotationProfile> fetchCustomerQuotationProfilesByCriteria(Long cId, String accountType, String accountCode, String productCode){
		return quotationProfileDAO.fetchCustomerQuotationProfilesByCriteria(cId, accountType, accountCode, productCode);
	}

}
