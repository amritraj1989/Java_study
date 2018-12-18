package com.mikealbert.data.vo;

import com.mikealbert.util.MALUtilities;

public class ProviderMaintCodeSearchResultLineVO {
	public static  String  FILTER_SERVICE_PROVIDER = "serviceProvideName";
	public static  String  FILTER_MAFS_CODE = "mafsMaintCode";
	public static  String  FILTER_MAFS_DESC = "mafsMaintCodeDesc";
	public static  String  FILTER_SERVICE_PROVIDER_CODE = "provideMaintCode";
	public static  String  FILTER_SERVICE_PROVIDER_DESC = "provideMaintCodeDesc";
	
	private Long provideMaintCodeId;
	private String serviceProvideName;
	private String serviceProvideMaintCategory;
	private String serviceProvideCode;
	private String provideMaintCode;
	private String provideMaintCodeDesc;
	private Long mafsMaintCodeId;
	private String mafsMaintCode;
	private String mafsMaintCodeDesc;
	private boolean approved;
	
	public String getServiceProvideName() {
		return serviceProvideName;
	}
	public void setServiceProvideName(String serviceProvideName) {
		this.serviceProvideName = serviceProvideName;
	}
	public String getServiceProvideCode() {
		return serviceProvideCode;
	}
	public void setServiceProvideCode(String serviceProvideCode) {
		this.serviceProvideCode = serviceProvideCode;
	}
	public Long getProvideMaintCodeId() {
		return provideMaintCodeId;
	}
	public void setProvideMaintCodeId(Long provideMaintCodeId) {
		this.provideMaintCodeId = provideMaintCodeId;
	}
	public String getProvideMaintCode() {
		return provideMaintCode;
	}
	
	public String getServiceProvideMaintCategory() {
		return serviceProvideMaintCategory;
	}
	public void setServiceProvideMaintCategory(String serviceProvideMaintCategory) {
		this.serviceProvideMaintCategory = serviceProvideMaintCategory;
	}
	public void setProvideMaintCode(String provideMaintCode) {
		this.provideMaintCode = provideMaintCode;
	}
	public String getProvideMaintCodeDesc() {
		return provideMaintCodeDesc;
	}
	public void setProvideMaintCodeDesc(String provideMaintCodeDesc) {
		this.provideMaintCodeDesc = provideMaintCodeDesc;
	}
	public Long getMafsMaintCodeId() {
		return mafsMaintCodeId;
	}
	public void setMafsMaintCodeId(Long mafsMaintCodeId) {
		this.mafsMaintCodeId = mafsMaintCodeId;
	}
	public String getMafsMaintCode() {
		return mafsMaintCode;
	}
	public void setMafsMaintCode(String mafsMaintCode) {
		this.mafsMaintCode = mafsMaintCode;
	}
	public String getMafsMaintCodeDesc() {
		return mafsMaintCodeDesc;
	}
	public void setMafsMaintCodeDesc(String mafsMaintCodeDesc) {
		this.mafsMaintCodeDesc = mafsMaintCodeDesc;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProviderMaintCodeSearchResultLineVO)) {
            return false;
        }
        ProviderMaintCodeSearchResultLineVO other = (ProviderMaintCodeSearchResultLineVO) object;
        if ((this.provideMaintCodeId == null && other.provideMaintCodeId != null) || (this.provideMaintCodeId != null && !this.provideMaintCodeId.equals(other.provideMaintCodeId))) {
            return false;
        }
        if ((this.serviceProvideName == null && other.serviceProvideName != null) || (this.serviceProvideName != null && !this.serviceProvideName.equals(other.serviceProvideName))) {
            return false;
        }
        if ((this.serviceProvideMaintCategory == null && other.serviceProvideMaintCategory != null) || (this.serviceProvideMaintCategory != null && !this.serviceProvideMaintCategory.equals(other.serviceProvideMaintCategory))) {
            return false;
        }
        if ((this.serviceProvideCode == null && other.serviceProvideCode != null) || (this.serviceProvideCode != null && !this.serviceProvideCode.equals(other.serviceProvideCode))) {
            return false;
        }
        if ((this.provideMaintCode == null && other.provideMaintCode != null) || (this.provideMaintCode != null && !this.provideMaintCode.equals(other.provideMaintCode))) {
            return false;
        }
        if ((this.provideMaintCodeDesc == null && other.provideMaintCodeDesc != null) || (this.provideMaintCodeDesc != null && !this.provideMaintCodeDesc.equals(other.provideMaintCodeDesc))) {
            return false;
        }
        if ((this.mafsMaintCodeId == null && other.mafsMaintCodeId != null) || (this.mafsMaintCodeId != null && !this.mafsMaintCodeId.equals(other.mafsMaintCodeId))) {
            return false;
        }
        if ((MALUtilities.isEmptyString(this.mafsMaintCode) && (!MALUtilities.isEmptyString(other.mafsMaintCode))) || (this.mafsMaintCode != null && !this.mafsMaintCode.equals(other.mafsMaintCode))) {
            return false;
        }
        if  ((MALUtilities.isEmptyString(this.mafsMaintCodeDesc) && (!MALUtilities.isEmptyString(other.mafsMaintCodeDesc))) || (this.mafsMaintCodeDesc != null && !this.mafsMaintCodeDesc.equals(other.mafsMaintCodeDesc))) {
            return false;
        }
        if (!this.approved == other.approved) {
            return false;
        }
        
        return true;
    }
	
	
	
}
