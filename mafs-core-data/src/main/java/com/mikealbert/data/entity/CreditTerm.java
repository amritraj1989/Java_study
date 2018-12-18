package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the CREDIT_TERMS database table.
 * 
 */
@Entity
@Table(name="CREDIT_TERMS")
public class CreditTerm implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CreditTermPK id;

	@Column(name="AGE_ON_INVOICE")
	private String ageOnInvoice;

	@Column(name="CREDIT_DAY_OF_MONTH")
	private BigDecimal creditDayOfMonth;

	@Column(name="CREDIT_MONTHS")
	private BigDecimal creditMonths;

	@Column(name="CREDIT_NO_OF_DAYS")
	private BigDecimal creditNoOfDays;

	private String description;

	@Column(name="DISC_DAYS_1")
	private BigDecimal discDays1;

	@Column(name="DISC_DAYS_2")
	private BigDecimal discDays2;

	@Column(name="DISC_DAYS_3")
	private BigDecimal discDays3;

	@Column(name="DISC_DAYS_4")
	private BigDecimal discDays4;

	@Column(name="DISC_DAYS_5")
	private BigDecimal discDays5;

	@Column(name="DISC_DAYS_6")
	private BigDecimal discDays6;

	@Column(name="DISC_RATE_1")
	private BigDecimal discRate1;

	@Column(name="DISC_RATE_2")
	private BigDecimal discRate2;

	@Column(name="DISC_RATE_3")
	private BigDecimal discRate3;

	@Column(name="DISC_RATE_4")
	private BigDecimal discRate4;

	@Column(name="DISC_RATE_5")
	private BigDecimal discRate5;

	@Column(name="DISC_RATE_6")
	private BigDecimal discRate6;

	@Column(name="PCT_LIABILITY")
	private BigDecimal pctLiability;

	@Column(name="TERM_DAY_OF_MONTH")
	private BigDecimal termDayOfMonth;

	@Column(name="TERM_MONTHS")
	private BigDecimal termMonths;

	@Column(name="TERM_NO_OF_DAYS")
	private BigDecimal termNoOfDays;

	public CreditTerm() {
	}

	public CreditTermPK getId() {
		return this.id;
	}

	public void setId(CreditTermPK id) {
		this.id = id;
	}

	public String getAgeOnInvoice() {
		return this.ageOnInvoice;
	}

	public void setAgeOnInvoice(String ageOnInvoice) {
		this.ageOnInvoice = ageOnInvoice;
	}

	public BigDecimal getCreditDayOfMonth() {
		return this.creditDayOfMonth;
	}

	public void setCreditDayOfMonth(BigDecimal creditDayOfMonth) {
		this.creditDayOfMonth = creditDayOfMonth;
	}

	public BigDecimal getCreditMonths() {
		return this.creditMonths;
	}

	public void setCreditMonths(BigDecimal creditMonths) {
		this.creditMonths = creditMonths;
	}

	public BigDecimal getCreditNoOfDays() {
		return this.creditNoOfDays;
	}

	public void setCreditNoOfDays(BigDecimal creditNoOfDays) {
		this.creditNoOfDays = creditNoOfDays;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getDiscDays1() {
		return this.discDays1;
	}

	public void setDiscDays1(BigDecimal discDays1) {
		this.discDays1 = discDays1;
	}

	public BigDecimal getDiscDays2() {
		return this.discDays2;
	}

	public void setDiscDays2(BigDecimal discDays2) {
		this.discDays2 = discDays2;
	}

	public BigDecimal getDiscDays3() {
		return this.discDays3;
	}

	public void setDiscDays3(BigDecimal discDays3) {
		this.discDays3 = discDays3;
	}

	public BigDecimal getDiscDays4() {
		return this.discDays4;
	}

	public void setDiscDays4(BigDecimal discDays4) {
		this.discDays4 = discDays4;
	}

	public BigDecimal getDiscDays5() {
		return this.discDays5;
	}

	public void setDiscDays5(BigDecimal discDays5) {
		this.discDays5 = discDays5;
	}

	public BigDecimal getDiscDays6() {
		return this.discDays6;
	}

	public void setDiscDays6(BigDecimal discDays6) {
		this.discDays6 = discDays6;
	}

	public BigDecimal getDiscRate1() {
		return this.discRate1;
	}

	public void setDiscRate1(BigDecimal discRate1) {
		this.discRate1 = discRate1;
	}

	public BigDecimal getDiscRate2() {
		return this.discRate2;
	}

	public void setDiscRate2(BigDecimal discRate2) {
		this.discRate2 = discRate2;
	}

	public BigDecimal getDiscRate3() {
		return this.discRate3;
	}

	public void setDiscRate3(BigDecimal discRate3) {
		this.discRate3 = discRate3;
	}

	public BigDecimal getDiscRate4() {
		return this.discRate4;
	}

	public void setDiscRate4(BigDecimal discRate4) {
		this.discRate4 = discRate4;
	}

	public BigDecimal getDiscRate5() {
		return this.discRate5;
	}

	public void setDiscRate5(BigDecimal discRate5) {
		this.discRate5 = discRate5;
	}

	public BigDecimal getDiscRate6() {
		return this.discRate6;
	}

	public void setDiscRate6(BigDecimal discRate6) {
		this.discRate6 = discRate6;
	}

	public BigDecimal getPctLiability() {
		return this.pctLiability;
	}

	public void setPctLiability(BigDecimal pctLiability) {
		this.pctLiability = pctLiability;
	}

	public BigDecimal getTermDayOfMonth() {
		return this.termDayOfMonth;
	}

	public void setTermDayOfMonth(BigDecimal termDayOfMonth) {
		this.termDayOfMonth = termDayOfMonth;
	}

	public BigDecimal getTermMonths() {
		return this.termMonths;
	}

	public void setTermMonths(BigDecimal termMonths) {
		this.termMonths = termMonths;
	}

	public BigDecimal getTermNoOfDays() {
		return this.termNoOfDays;
	}

	public void setTermNoOfDays(BigDecimal termNoOfDays) {
		this.termNoOfDays = termNoOfDays;
	}

}