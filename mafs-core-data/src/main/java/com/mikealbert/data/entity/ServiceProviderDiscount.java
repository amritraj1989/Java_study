package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;


/**
 * Mapped to SUPPLIER_DISCOUNTS table
 * @author Raj
 */
@Entity
@Table(name="SUPPLIER_DISCOUNTS")
public class ServiceProviderDiscount implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SDIS_SEQ")
	@SequenceGenerator(name="SDIS_SEQ", sequenceName="SDIS_SEQ", allocationSize=1)
	@Column(name="SDIS_ID", unique=true, nullable=false, precision=12)
	private Long serviceProviderDiscountId;
	
	@Column(name="DISC_APPL")
	private String discAppl;

	@Temporal(TemporalType.DATE)
	@Column(name="EFFECTIVE_DATE")
	private Date effectiveDate;

	@Column(name="LABOUR_DISC")
	private BigDecimal labourDisc;

	@Column(name="PARTS_DISC")
	private BigDecimal partsDisc;

	@Temporal(TemporalType.DATE)
	@Column(name="REVIEW_DATE")
	private Date reviewDate;

	@Column(name="TYRE_DISC")
	private BigDecimal tyreDisc;
	
	//bi-directional many-to-one association to Supplier
	//@JoinColumn(name="SUP_SUP_ID", referencedColumnName = "SUP_ID", nullable=false)
	//@ManyToOne(optional = true)
	@ManyToOne
	@JoinColumn(name="SUP_SUP_ID")
	private ServiceProvider serviceProvider;
	
	//bi-directional many-to-one association to Make
	@ManyToOne
	@JoinColumn(name="MAK_MAK_ID")
	private Make make;

	//bi-directional many-to-one association to MakeModelRange
	@ManyToOne
	@JoinColumn(name="MRG_MRG_ID")
	private MakeModelRange makeModelRange;

	//bi-directional many-to-one association to ModelType
	@ManyToOne
	@JoinColumn(name="MTP_MTP_ID")
	private ModelType modelType;

	public String getDiscAppl() {
		return discAppl;
	}

	public void setDiscAppl(String discAppl) {
		this.discAppl = discAppl;
	}

	public long getServiceProviderDiscountId() {
		return serviceProviderDiscountId;
	}

	public void setServiceProviderDiscountId(long serviceProviderDiscountId) {
		this.serviceProviderDiscountId = serviceProviderDiscountId;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public Make getMake() {
		return make;
	}

	public void setMake(Make make) {
		this.make = make;
	}

	public MakeModelRange getMakeModelRange() {
		return makeModelRange;
	}

	public void setMakeModelRange(MakeModelRange makeModelRange) {
		this.makeModelRange = makeModelRange;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public BigDecimal getLabourDisc() {
		return labourDisc;
	}

	public void setLabourDisc(BigDecimal labourDisc) {
		this.labourDisc = labourDisc;
	}

	public BigDecimal getPartsDisc() {
		return partsDisc;
	}

	public void setPartsDisc(BigDecimal partsDisc) {
		this.partsDisc = partsDisc;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public BigDecimal getTyreDisc() {
		return tyreDisc;
	}

	public void setTyreDisc(BigDecimal tyreDisc) {
		this.tyreDisc = tyreDisc;
	}
}