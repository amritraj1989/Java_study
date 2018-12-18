package com.mikealbert.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "SUPPLIER_INDICATORS")

public class ServiceProviderIndicators extends BaseEntity{
	
	@Id
	@SequenceGenerator(name = "SI_SEQ", sequenceName = "SI_SEQ", allocationSize = 1)
	@GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "SI_SEQ")
	@Column(name = "SI_ID")
	private Long siId;
	
	@Column(name = "PO_AUTO_COMPLETE_FLAG")
	private String poAutoCompleteFlag;
	
    @JoinColumn(name="SUP_SUP_ID", referencedColumnName = "SUP_ID")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ServiceProvider serviceProvider;

    public ServiceProviderIndicators(){}
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getSiId() != null ? getSiId().hashCode() : 0);
        hash += (getPoAutoCompleteFlag() != null ? getPoAutoCompleteFlag().hashCode() : 0);
        hash += (getServiceProvider() != null ? getServiceProvider().hashCode() : 0);
        
        return hash;
    }

    @Override
    public boolean equals(Object object) {
       
        if (!(object instanceof ServiceProvider)) {
            return false;
        }
        ServiceProviderIndicators other = (ServiceProviderIndicators) object;
       
        if ((this.getServiceProvider() == null && other.getServiceProvider() != null) || (this.getServiceProvider() != null && other.getServiceProvider() == null) ) {
            return false;
        }

        if ((this.getServiceProvider() != null && other.getServiceProvider() != null) ) {
        	if( this.getServiceProvider().equals(other.getServiceProvider()) == false) return false;
        }
        if (this.siId != null && other.siId != null){
        	if(!this.siId.equals(other.siId)) return false;
        }else{
        	return false;
        }
        if(this.getPoAutoCompleteFlag() != null && other.getPoAutoCompleteFlag() != null){
        	if(!this.getPoAutoCompleteFlag().equals(other.getPoAutoCompleteFlag())) return false;
        }else{
        	return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.ServiceProviderIndicators[ siId=" + siId + " ]";
    }
    
    
	public Long getSiId() {
		return siId;
	}

	public void setSiId(Long siId) {
		this.siId = siId;
	}

	public String getPoAutoCompleteFlag() {
		return poAutoCompleteFlag;
	}

	public void setPoAutoCompleteFlag(String poAutoCompleteFlag) {
		this.poAutoCompleteFlag = poAutoCompleteFlag;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
    
    
}
