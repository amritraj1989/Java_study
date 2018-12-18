package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.mikealbert.data.beanvalidation.MASize;

/**
 * Base class for accessory
 * @author sibley
 */

@Entity
@Table(name="UPFITTER_QUOTES")
public class UpfitterQuote extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="UFQ_SEQ")    
    @SequenceGenerator(name="UFQ_SEQ", sequenceName="UFQ_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "UFQ_ID")
    private Long ufqId;
    
	@MASize(min=1, max=40, label="Quote Number")
    @Column(name = "QUOTE_NUMBER")
    private String quoteNumber;
	
	@Column(name="QUOTE_DATE")
	private Date quoteDate;
	
	@Column(name="EXPIRATION_DATE")
	private Date expirationDate;
	
	@MASize(min=1, max=250, label="Quote Description")		
    @Column(name = "DESCRIPTION")
    private String description;		
	
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional=false)
    private ExternalAccount externalAccount;
    
    @OneToMany(mappedBy = "upfitterQuote", orphanRemoval=true)
    private List<DealerAccessoryPrice> dealerAccessoryPrices;
    
    @OneToMany(mappedBy = "upfitterQuote")
    private List<VehicleConfigUpfitQuote> vehicleConfigUpfitQuotes;
    
    @Transient
    private List<OnbaseUploadedDocs> onbaseUploadedDocs;

    

	public UpfitterQuote(){}
    
	
	public Long getUfqId() {
		return ufqId;
	}

	public void setUfqId(Long ufqId) {
		this.ufqId = ufqId;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}
	
	public Date getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public List<DealerAccessoryPrice> getDealerAccessoryPrices() {
		return dealerAccessoryPrices;
	}

	public void setDealerAccessoryPrices(List<DealerAccessoryPrice> dealerAccessoryPrices) {
		this.dealerAccessoryPrices = dealerAccessoryPrices;
	}

	public List<VehicleConfigUpfitQuote> getVehicleConfigUpfitQuotes() {
		return vehicleConfigUpfitQuotes;
	}

	public void setVehicleConfigUpfitQuotes(List<VehicleConfigUpfitQuote> vehicleConfigUpfitQuotes) {
		this.vehicleConfigUpfitQuotes = vehicleConfigUpfitQuotes;
	}	
	
	public List<OnbaseUploadedDocs> getOnbaseUploadedDocs() {
		return onbaseUploadedDocs;
	}

	public void setOnbaseUploadedDocs(List<OnbaseUploadedDocs> onbaseUploadedDocs) {
		this.onbaseUploadedDocs = onbaseUploadedDocs;
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.ufqId != null ? this.ufqId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UpfitterQuote)) {
            return false;
        }
        UpfitterQuote other = (UpfitterQuote) object;
        if ((this.getUfqId() == null && other.getUfqId() != null) || (this.getUfqId() != null && !this.getUfqId().equals(other.getUfqId()))) {
            return false;
        }
        return true;
    }    
    
}
