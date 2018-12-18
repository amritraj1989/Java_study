package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * The persistent class for the QUOTE_REQUEST_CONFIGURATION database table.
 * 
 */
@Entity
@Table(name="QUOTE_REQUEST_CONFIGURATIONS")
public class QuoteRequestConfiguration extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRC_SEQ")    
    @SequenceGenerator(name="QRC_SEQ", sequenceName="QRC_SEQ", allocationSize=1)
    @Column(name = "QRC_ID")
    private Long qrcId;
    
	@JoinColumn(name = "QRQ_QRQ_ID", referencedColumnName = "QRQ_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    private QuoteRequest quoteRequest;

	@JoinColumn(name = "VCF_VCF_ID", referencedColumnName = "VCF_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    private VehicleConfiguration vehicleConfiguration;

	
	public QuoteRequestConfiguration() {}
    
	public Long getQrcId() {
		return qrcId;
	}

	public void setQrcId(Long qrcId) {
		this.qrcId = qrcId;
	}

    public QuoteRequest getQuoteRequest() {
		return quoteRequest;
	}

	public void setQuoteRequest(QuoteRequest quoteRequest) {
		this.quoteRequest = quoteRequest;
	}

	public VehicleConfiguration getVehicleConfiguration() {
		return vehicleConfiguration;
	}

	public void setVehicleConfigurationId(VehicleConfiguration vehicleConfiguration) {
		this.vehicleConfiguration = vehicleConfiguration;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getQrcId() == null) ? 0 : getQrcId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QuoteRequestConfiguration))
			return false;
		QuoteRequestConfiguration other = (QuoteRequestConfiguration) obj;
		if (getQrcId() == null) {
			if (other.getQrcId() != null)
				return false;
		} else if (!getQrcId().equals(other.getQrcId()))
			return false;
		return true;
	}

	
}