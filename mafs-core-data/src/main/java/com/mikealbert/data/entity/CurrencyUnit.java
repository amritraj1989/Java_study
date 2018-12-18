package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;


/**
 * The persistent class for the CURRENCY_UNITS database table.
 * 
 */
@Entity
@Table(name="CURRENCY_UNITS")
public class CurrencyUnit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CURRENCY_CODE")
	private String currencyCode;

	@Column(name="\"ALIAS\"")
	private String alias;

	@Column(name="CURRENCY_NAME")
	private String currencyName;

	private String description;

	@Column(name="EMU_INDICATOR")
	private String emuIndicator;

	private Integer exponent;

	@Column(name="ISSUING_COUNTRY")
	private String issuingCountry;

	@Column(name="MINIMUM_ACCOUNTABLE_UNIT")
	private BigDecimal minimumAccountableUnit;

	private String restrictions;

	private String symbol;

	@Column(name="UNIT_TYPE")
	private String unitType;

	//bi-directional many-to-one association to WillowEntityDefault
	@OneToMany(mappedBy="currencyUnit",fetch = FetchType.LAZY)
	private Set<WillowEntityDefault> willowEntityDefaults;

    public CurrencyUnit() {
    }

	public String getCurrencyCode() {
		return this.currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getCurrencyName() {
		return this.currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmuIndicator() {
		return this.emuIndicator;
	}

	public void setEmuIndicator(String emuIndicator) {
		this.emuIndicator = emuIndicator;
	}

	public Integer getExponent() {
		return this.exponent;
	}

	public void setExponent(Integer exponent) {
		this.exponent = exponent;
	}

	public String getIssuingCountry() {
		return this.issuingCountry;
	}

	public void setIssuingCountry(String issuingCountry) {
		this.issuingCountry = issuingCountry;
	}

	public BigDecimal getMinimumAccountableUnit() {
		return this.minimumAccountableUnit;
	}

	public void setMinimumAccountableUnit(BigDecimal minimumAccountableUnit) {
		this.minimumAccountableUnit = minimumAccountableUnit;
	}

	public String getRestrictions() {
		return this.restrictions;
	}

	public void setRestrictions(String restrictions) {
		this.restrictions = restrictions;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getUnitType() {
		return this.unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public Set<WillowEntityDefault> getWillowEntityDefaults() {
		return this.willowEntityDefaults;
	}

	public void setWillowEntityDefaults(Set<WillowEntityDefault> willowEntityDefaults) {
		this.willowEntityDefaults = willowEntityDefaults;
	}
	
}