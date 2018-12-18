package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;


/**
 * The persistent class for the BUYER_LIMITS database table.
 * 
 */
@Entity
@Table(name="BUYER_LIMITS")
public class BuyerLimit extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
    protected BuyerLimitPK buyerLimitPK;
	
	@Column(name="AUTHORISE_AMOUNT")
	private BigDecimal authorizeAmount;

	@Column(name="CHANGE_PERCENT")
	private BigDecimal changePercent;

	@Column(name="ORIGINATE_AMOUNT")
	private BigDecimal originateAmount;
	
	@Column(name="RELEASE_AMOUNT")
	private BigDecimal releaseAmount;

    public BuyerLimit() {
    }
    
    public BuyerLimit(BuyerLimitPK buyerLimitPK) {
        this.buyerLimitPK = buyerLimitPK;
    }
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.BuyerLimit[ authorizeAmount=" + getAuthorizeAmount() +" , changePercent "+ getChangePercent()+ " , originateAmount "+ getOriginateAmount()+ " , releaseAmount "+ getReleaseAmount()+" ]";
    }

	public BigDecimal getAuthorizeAmount() {
		return authorizeAmount;
	}

	public void setAuthorizeAmount(BigDecimal authorizeAmount) {
		this.authorizeAmount = authorizeAmount;
	}

	public BigDecimal getChangePercent() {
		return changePercent;
	}

	public void setChangePercent(BigDecimal changePercent) {
		this.changePercent = changePercent;
	}

	public BigDecimal getOriginateAmount() {
		return originateAmount;
	}

	public void setOriginateAmount(BigDecimal originateAmount) {
		this.originateAmount = originateAmount;
	}

	public BigDecimal getReleaseAmount() {
		return releaseAmount;
	}

	public void setReleaseAmount(BigDecimal releaseAmount) {
		this.releaseAmount = releaseAmount;
	}

}