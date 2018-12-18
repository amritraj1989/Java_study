package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.VrbDiscount;

/**
* Public Interface implemented by {@link com.mikealbert.vision.service.VolumeRelatedBonusServiceImpl} 
* for interacting with business service methods concerning: 
* 
*  @see com.mikealbert.data.entity.VrbDiscount
* */
public interface VolumeRelatedBonusService {
	public List<VrbDiscount> getClientVolumeRelatedBonuses(ExternalAccount client, Model trim, String productCode, Long term) throws Exception;
	public VrbDiscount getClientVolumeRelatedBonus(ExternalAccount client, String vrbTypeCode, Model trim, String productCode, Long term) throws Exception;
	public VrbDiscount getClientVolumeRelatedBonus(ExternalAccount client, Model trim, String productCode, CapitalElement capitalElement, Long term) throws Exception;	
	public CapitalElement findCapitalElemet(VrbDiscount vrbDiscount);
}
