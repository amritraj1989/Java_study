package com.mikealbert.data.dao;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.VehicleConfiguration;

public interface VehicleConfigurationDAO extends JpaRepository<VehicleConfiguration, Long>, VehicleConfigurationDAOCustom {
	
	@Query("SELECT distinct (ea) FROM VehicleConfiguration vcf JOIN vcf.externalAccount ea WHERE ea.externalAccountPK.cId = ?1 and ea.externalAccountPK.accountType = ?2 and ea.externalAccountPK.accountCode LIKE ?3 order by ea.accountName, ea.externalAccountPK.accountCode")
	public List<ExternalAccount> getVehicleConfigurationClientsByCode(Long cId, String accounType, String accountCode, Pageable page);
	
	@Query("SELECT distinct (ea) FROM VehicleConfiguration vcf JOIN vcf.externalAccount ea WHERE ea.externalAccountPK.cId = ?1 and ea.externalAccountPK.accountType = ?2 and LOWER(ea.accountName) LIKE ?3 order by ea.accountName, ea.externalAccountPK.accountCode")
	public List<ExternalAccount> getVehicleConfigurationClientsByName(Long cId, String accounType, String accountName, Pageable page);
	
	@Query("SELECT DISTINCT (eas) " +
	          "  FROM UpfitterQuote ufq JOIN ufq.externalAccount eas  " +
	          " WHERE ufq.ufqId IN " +
	          "          (SELECT vuq.upfitterQuote.ufqId " +
	          "             FROM VehicleConfigUpfitQuote vuq  " +
	          "            WHERE vuq.obsoleteYn = 'N'  " + 
	          "              AND vuq.vehicleConfigGrouping.vcgId IN  " +
	          "                     (SELECT vcg.vcgId  " +
	          "                        FROM VehicleConfigGrouping vcg  " +
	          "                       WHERE vcg.vehicleConfiguration.vcfId IN " +
	          "                                (SELECT vcf.vcfId " +
	          "                                   FROM VehicleConfiguration vcf) " +
	          "  AND eas.externalAccountPK.cId = ?1 " +
	          "  AND eas.externalAccountPK.accountType = ?2 " +
	          "  AND eas.externalAccountPK.accountCode LIKE ?3))")
   public List<ExternalAccount> getVehicleConfigurationVendorsByCode(Long cId, String accounType, String accountCode, Pageable page);
	
	@Query("SELECT DISTINCT (eas) " +
	          "  FROM UpfitterQuote ufq JOIN ufq.externalAccount eas  " +
	          " WHERE ufq.ufqId IN " +
	          "          (SELECT vuq.upfitterQuote.ufqId " +
	          "             FROM VehicleConfigUpfitQuote vuq  " +
	          "            WHERE vuq.obsoleteYn = 'N'  " + 
	          "              AND vuq.vehicleConfigGrouping.vcgId IN  " +
	          "                     (SELECT vcg.vcgId  " +
	          "                        FROM VehicleConfigGrouping vcg  " +
	          "                       WHERE vcg.vehicleConfiguration.vcfId IN " +
	          "                                (SELECT vcf.vcfId " +
	          "                                   FROM VehicleConfiguration vcf) " +
	          "  AND eas.externalAccountPK.cId = ?1 " +
	          "	 AND eas.externalAccountPK.accountType = ?2 " +
	          "	 AND LOWER(eas.accountName) LIKE ?3))")
	public List<ExternalAccount> getVehicleConfigurationVendorsByName(Long cId, String accounType, String accountName, Pageable page);
	
	@Query("SELECT DISTINCT (vcm.mfgCode) FROM VehicleConfigModel vcm WHERE LOWER(vcm.mfgCode) LIKE ?1 AND vcm.obsoleteYn = 'N'")
	public List<String> getVehicleConfigurationMfgCodes(String mfgCode, Pageable page);
	
	@Query("SELECT DISTINCT (mdl.modelDescription) FROM VehicleConfigModel vcm JOIN vcm.model mdl WHERE LOWER(mdl.modelDescription) LIKE ?1 AND vcm.obsoleteYn = 'N'")
	public List<String> getVehicleConfigurationTrims(String trim, Pageable page);
	
	@Query("FROM VehicleConfiguration vc WHERE vc.externalAccount.externalAccountPK = ?1 ORDER BY vc.description ASC")
	public List<VehicleConfiguration> getVehicleConfigurationsByAccount(ExternalAccountPK externalAccountPK);

	@Query("FROM VehicleConfiguration vc WHERE vc.externalAccount.externalAccountPK = ?1 AND vc.obsoleteYN = 'N' ORDER BY vc.description ASC")
	public List<VehicleConfiguration> getActiveVehicleConfigurationsByAccount(ExternalAccountPK externalAccountPK);

}
