package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the VEHICLE_CONFIGURATIONS database table.
 * 
 */
@Entity
@Table(name="VEHICLE_CONFIGURATIONS")
public class VehicleConfiguration extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="VCFID_GENERATOR", sequenceName="VCF_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VCFID_GENERATOR")
	@Column(name="VCF_ID")
	private Long vcfId;

	@Column(name="COLOR")
	private String color;

	@Column(name="DESCRIPTION")
	private String description;
	
	@Temporal(TemporalType.DATE)
	@Column(name="ENTERED_DATE", nullable=false)
	private Date enteredDate;

	@Column(name="ENTERED_USER", nullable=false, length=80)
	private String enteredUser;

	@JoinColumns({
	        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
	        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
	        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private ExternalAccount externalAccount;

	//bi-directional many-to-one association to VehicleConfigGrouping
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "vehicleConfiguration", fetch = FetchType.EAGER)
	private List<VehicleConfigGrouping> vehicleConfigGroupings;
	
	//bi-directional many-to-one association to VehicleConfigurationModel
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "vehicleConfiguration")
	private List<VehicleConfigModel> vehicleConfigModels;
	
	@OneToMany(mappedBy="vehicleConfiguration", cascade = CascadeType.ALL, fetch = FetchType.LAZY) 
	private List<QuoteRequestConfiguration> quoteRequestConfigurations;

	//bi-directional many-to-one association to OrderType
	@ManyToOne
	@JoinColumn(name="ORDER_TYPE_CODE", nullable=false)
	private OrderType orderType;
	
	@Column(name = "OBSOLETE_IND", length = 1)
	private String obsoleteYN;

	public VehicleConfiguration() {
	}

	public Long getVcfId() {
		return vcfId;
	}

	public void setVcfId(Long vcfId) {
		this.vcfId = vcfId;
	}

	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
	}
		
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public Date getEnteredDate() {
		return this.enteredDate;
	}

	public void setEnteredDate(Date enteredDate) {
		this.enteredDate = enteredDate;
	}

	public String getEnteredUser() {
		return this.enteredUser;
	}

	public void setEnteredUser(String enteredUser) {
		this.enteredUser = enteredUser;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public List<VehicleConfigGrouping> getVehicleConfigGroupings() {
		return this.vehicleConfigGroupings;
	}

	public void setVehicleConfigGroupings(List<VehicleConfigGrouping> vehicleConfigGroupings) {
		this.vehicleConfigGroupings = vehicleConfigGroupings;
	}

	public List<VehicleConfigModel> getVehicleConfigModels() {
		return vehicleConfigModels;
	}

	public void setVehicleConfigModels(List<VehicleConfigModel> vehicleConfigModels) {
		this.vehicleConfigModels = vehicleConfigModels;
	}

	public OrderType getOrderType() {
		return this.orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public String getObsoleteYN() {
		return obsoleteYN;
	}

	public void setObsoleteYN(String obsoleteYN) {
		this.obsoleteYN = obsoleteYN;
	}

	public List<QuoteRequestConfiguration> getQuoteRequestConfigurations() {
		return quoteRequestConfigurations;
	}

	public void setQuoteRequestConfiguration(
			List<QuoteRequestConfiguration> quoteRequestConfigurations) {
		this.quoteRequestConfigurations = quoteRequestConfigurations;
	}

	
	
}