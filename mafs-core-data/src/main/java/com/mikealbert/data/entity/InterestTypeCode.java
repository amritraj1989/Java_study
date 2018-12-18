package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the INTEREST_TYPE_CODES database table.
 * 
 */
@Entity
@Table(name="INTEREST_TYPE_CODES")
public class InterestTypeCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="INTEREST_TYPE")
	private String interestType;

	@Column(name="CONCEALED_IND")
	private String concealedInd;

	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="INCL_QPR")
	private String inclQpr;

	@Column(name="RATE_FREQUENCY")
	private String rateFrequency;

	//bi-directional many-to-one association to InterestRate
	@OneToMany(mappedBy="interestTypeCode")
	private List<InterestRate> interestRates;

	//bi-directional many-to-one association to Product
	@OneToMany(mappedBy="interestTypeCode")
	private List<Product> products;

    public InterestTypeCode() {
    }

	public String getInterestType() {
		return this.interestType;
	}

	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

	public String getConcealedInd() {
		return this.concealedInd;
	}

	public void setConcealedInd(String concealedInd) {
		this.concealedInd = concealedInd;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInclQpr() {
		return this.inclQpr;
	}

	public void setInclQpr(String inclQpr) {
		this.inclQpr = inclQpr;
	}

	public String getRateFrequency() {
		return this.rateFrequency;
	}

	public void setRateFrequency(String rateFrequency) {
		this.rateFrequency = rateFrequency;
	}

	public List<InterestRate> getInterestRates() {
		return this.interestRates;
	}

	public void setInterestRates(List<InterestRate> interestRates) {
		this.interestRates = interestRates;
	}
	
	public List<Product> getProducts() {
		return this.products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
}