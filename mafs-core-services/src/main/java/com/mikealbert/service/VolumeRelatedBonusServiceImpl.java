package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.CapEleParameterDAO;
import com.mikealbert.data.dao.CapitalElementDAO;
import com.mikealbert.data.dao.FinanceParameterDAO;
import com.mikealbert.data.dao.VrbDiscountDAO;
import com.mikealbert.data.dao.VrbDiscountTypeCodeDAO;
import com.mikealbert.data.entity.CapEleParameter;
import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.DeliveryCost;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FinanceParameter;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.VrbDiscount;
import com.mikealbert.data.entity.VrbDiscountTypeCode;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.VolumeRelatedBonusService}
 */
@Service("volumeRelatedBonusService")
public class VolumeRelatedBonusServiceImpl implements VolumeRelatedBonusService {
	
	@Resource VrbDiscountDAO vrbDiscountDAO;
	@Resource VrbDiscountTypeCodeDAO vrbDiscountTypeCodeDAO; 
	@Resource FinanceParameterDAO financeParameterDAO;
	@Resource CapEleParameterDAO capEleParameterDAO;
	@Resource CapitalElementDAO capitalElementDAO;

	@Override
	@Transactional
	public List<VrbDiscount> getClientVolumeRelatedBonuses(ExternalAccount client, Model trim, String productCode, Long term) throws Exception {
		List<VrbDiscount> discounts;
		ExternalAccountPK eaPk;
		Long vrbdId;
		
		discounts = new ArrayList<>();
		eaPk = client.getExternalAccountPK();
		
		for(VrbDiscountTypeCode code : vrbDiscountTypeCodeDAO.findAll()) {
			vrbdId = vrbDiscountDAO.getVRBDiscountId(eaPk.getcId(), eaPk.getAccountCode(), eaPk.getAccountType(), trim.getModelId(), code.getVrbTypeCode(), productCode, term);
			
			if(!MALUtilities.isEmpty(vrbdId)) {
				discounts.add(vrbDiscountDAO.findById(vrbdId).orElse(null));
			}
		}
		
		return discounts;
	}

	@Override
	@Transactional
	public VrbDiscount getClientVolumeRelatedBonus(ExternalAccount client, String vrbTypeCode, Model trim, String productCode, Long term) throws Exception {
		ExternalAccountPK eaPk;
		Long vrbdId;
		VrbDiscount discount = null;		
		
		eaPk = client.getExternalAccountPK();
		
		vrbdId = vrbDiscountDAO.getVRBDiscountId(eaPk.getcId(), eaPk.getAccountCode(), eaPk.getAccountType(), trim.getModelId(), vrbTypeCode, productCode, term);
	
		if(!MALUtilities.isEmpty(vrbdId)) {
			discount = vrbDiscountDAO.findById(vrbdId).orElse(null);
		}
		
		return discount;
	}
	
	/**
	 * Retrieves the vrb discount linked to a capital element based on the client, trim, product, and optionally term.
	 * 
	 * @param client Client matched on VRB
	 * @param trim Model matched on VRB
	 * @param productCode Product Code (this should be coming from the profile) matched on the VRB
	 * @param capitalElement Capital element used to find matching/linked VRB via finance parameter
	 * @param term (Optional) Matched on the VRB when provided
	 * @return VrbDiscount linked to the capital element and matched on the client, trim, product, and optional term
	 */
	@Override	
	public VrbDiscount getClientVolumeRelatedBonus(ExternalAccount client, Model trim, String productCode, CapitalElement capitalElement, Long term) throws Exception {
		Long vrbdId;
		ExternalAccountPK eaPk;		
		FinanceParameter financeParameter;		
		VrbDiscount discount = null;		
		List<CapEleParameter> capEleParameters;		
		
		eaPk = client.getExternalAccountPK();
		
		capEleParameters = capEleParameterDAO.findByCelIdAndParameterType(capitalElement.getCelId(), "C");
		if(!MALUtilities.isEmpty(capEleParameters) && !capEleParameters.isEmpty()) {
			financeParameter = financeParameterDAO.findByParameterKey(capEleParameters.get(0).getParameterName());
			
			if(!MALUtilities.isEmpty(financeParameter)) {
				vrbdId = vrbDiscountDAO.getVRBDiscountId(eaPk.getcId(), eaPk.getAccountCode(), eaPk.getAccountType(), trim.getModelId(), financeParameter.getCvalue(), productCode, term);
				
				if(!MALUtilities.isEmpty(vrbdId)) {
					discount = vrbDiscountDAO.findById(vrbdId).orElse(null);
				}
			}
		}
		
		return discount;
	}
	
	@Override
	public CapitalElement findCapitalElemet(VrbDiscount vrbDiscount) {
		CapitalElement element = null;
		FinanceParameter financeParameter;
		CapEleParameter capEleParameter;
		
		financeParameter = financeParameterDAO.findByVrbTypeCode(vrbDiscount.getVrbDiscountTypeCode().getVrbTypeCode());
		capEleParameter = capEleParameterDAO.findByParameterName(financeParameter.getParameterKey(), "C");
		element = capitalElementDAO.findById(capEleParameter.getCelCelId()).orElse(null);
		
		return element;		
	}

}
