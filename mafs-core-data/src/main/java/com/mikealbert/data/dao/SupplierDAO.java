package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Supplier;

public interface SupplierDAO extends SupplierDAOCustom, JpaRepository<Supplier,Long> {
	
	@Query("Select sup from Supplier sup JOIN sup.supplierWorkShops swp where sup.supplierCode = ?1 and swp.workShopCapability = ?2")
	public Supplier getSupplierByVendorCodeAndWorkshopCapability(String vendorCode, String workShopCapability);
	
	@Query("SELECT sup from Supplier sup WHERE sup.eaCId = ?1 AND sup.eaAccountCode = ?2")
	public List<Supplier> getSuppliersByCidAndAccountCode(Long cId, String accountCode);
}
