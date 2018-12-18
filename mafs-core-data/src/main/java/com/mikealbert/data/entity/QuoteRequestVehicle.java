package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;
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
import javax.persistence.Transient;


/**
 * The persistent class for the QUOTE_REQUEST_VEHICLES database table.
 * 
 */
@Entity
@Table(name="QUOTE_REQUEST_VEHICLES")
public class QuoteRequestVehicle extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRV_SEQ")    
    @SequenceGenerator(name="QRV_SEQ", sequenceName="QRV_SEQ", allocationSize=1)
    @Column(name = "QRV_ID")
    private Long qrvId;
	
	@JoinColumn(name = "QRQ_QRQ_ID", referencedColumnName = "QRQ_ID")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private QuoteRequest quoteRequest;
	
    @JoinColumn(name = "FMS_FMS_ID", referencedColumnName = "FMS_ID")
    @ManyToOne(optional = true)
    private FleetMaster fleetmaster;		
	
	@ManyToOne
	@JoinColumn(name="VDCT_VDCT_ID")
	private VehicleDeliveryChargeType vehicleDeliveryChargeType;
	
	@ManyToOne
	@JoinColumn(name="PLATE_TYPE_CODE", referencedColumnName = "PLATE_TYPE_CODE")
	private PlateTypeCode plateTypeCode;
	
	@Column(name="DEALERSHIP_CODE")
	private String dealershipCode;

	@Column(name="DEALERSHIP_NAME")
	private String dealershipName;

	@Column(name="DEALERSHIP_CONTACT")
	private String dealershipContact;

	public String getDealershipContact() {
		return dealershipContact;
	}

	public void setDealershipContact(String dealershipContact) {
		this.dealershipContact = dealershipContact;
	}

	public String getDealershipContactPhone() {
		return dealershipContactPhone;
	}

	public void setDealershipContactPhone(String dealershipContactPhone) {
		this.dealershipContactPhone = dealershipContactPhone;
	}

	@Column(name="DEALERSHIP_CONTACT_PHONE")
	private String dealershipContactPhone;
	
	@Column(name="DESCRIPTION")
	private String vehicleDescription;
	
    @Column(name="FIRST_INTERIOR_COLOR")
	private String firstInteriorColor;

    @Column(name="SECOND_INTERIOR_COLOR")
	private String secondInteriorColor;
    
    @Column(name="THIRD_INTERIOR_COLOR")
	private String thirdInteriorColor;
    
    @Column(name="FIRST_EXTERIOR_COLOR")
	private String firstExteriorColor;

    @Column(name="SECOND_EXTERIOR_COLOR")
	private String secondExteriorColor;
    
    @Column(name="THIRD_EXTERIOR_COLOR")
	private String thirdExteriorColor;
    
    @Column(name="REFUSED_COLOR_DESCRIPTION")
	private String refusedColorDescription;
    
	@Column(name="FLEET_REFERENCE_NUMBER")
	private String fleetReferenceNumber;
	
	@Column(name="REQUIRED_ACCESSORIES")
	private String requiredAccessories;
	
    @Transient
    private List<OnbaseUploadedDocs> onbaseUploadedDocs;	

	public String getRequiredAccessories() {
		return requiredAccessories;
	}

	public void setRequiredAccessories(String requiredAccessories) {
		this.requiredAccessories = requiredAccessories;
	}

	public Long getQrvId() {
		return qrvId;
	}

	public void setQrvId(Long qrvId) {
		this.qrvId = qrvId;
	}

	public QuoteRequest getQuoteRequest() {
		return quoteRequest;
	}

	public void setQuoteRequest(QuoteRequest quoteRequest) {
		this.quoteRequest = quoteRequest;
	}

	public FleetMaster getFleetmaster() {
		return fleetmaster;
	}

	public void setFleetmaster(FleetMaster fleetmaster) {
		this.fleetmaster = fleetmaster;
	}

	public VehicleDeliveryChargeType getVehicleDeliveryChargeType() {
		return vehicleDeliveryChargeType;
	}

	public void setVehicleDeliveryChargeType(
			VehicleDeliveryChargeType vehicleDeliveryChargeType) {
		this.vehicleDeliveryChargeType = vehicleDeliveryChargeType;
	}

	public PlateTypeCode getPlateTypeCode() {
		return plateTypeCode;
	}

	public void setPlateTypeCode(PlateTypeCode plateTypeCode) {
		this.plateTypeCode = plateTypeCode;
	}

	public String getVehicleDescription() {
		return vehicleDescription;
	}

	public void setVehicleDescription(String vehicleDescription) {
		this.vehicleDescription = vehicleDescription;
	}

	public String getFirstInteriorColor() {
		return firstInteriorColor;
	}

	public void setFirstInteriorColor(String firstInteriorColor) {
		this.firstInteriorColor = firstInteriorColor;
	}

	public String getSecondInteriorColor() {
		return secondInteriorColor;
	}

	public void setSecondInteriorColor(String secondInteriorColor) {
		this.secondInteriorColor = secondInteriorColor;
	}

	public String getThirdInteriorColor() {
		return thirdInteriorColor;
	}

	public void setThirdInteriorColor(String thirdInteriorColor) {
		this.thirdInteriorColor = thirdInteriorColor;
	}

	public String getFirstExteriorColor() {
		return firstExteriorColor;
	}

	public void setFirstExteriorColor(String firstExteriorColor) {
		this.firstExteriorColor = firstExteriorColor;
	}

	public String getSecondExteriorColor() {
		return secondExteriorColor;
	}

	public void setSecondExteriorColor(String secondExteriorColor) {
		this.secondExteriorColor = secondExteriorColor;
	}

	public String getThirdExteriorColor() {
		return thirdExteriorColor;
	}

	public void setThirdExteriorColor(String thirdExteriorColor) {
		this.thirdExteriorColor = thirdExteriorColor;
	}

	public String getRefusedColorDescription() {
		return refusedColorDescription;
	}

	public void setRefusedColorDescription(String refusedColorDescription) {
		this.refusedColorDescription = refusedColorDescription;
	}

	public String getFleetReferenceNumber() {
		return fleetReferenceNumber;
	}

	public void setFleetReferenceNumber(String fleetReferenceNumber) {
		this.fleetReferenceNumber = fleetReferenceNumber;
	}

	public List<OnbaseUploadedDocs> getOnbaseUploadedDocs() {
		return onbaseUploadedDocs;
	}

	public void setOnbaseUploadedDocs(List<OnbaseUploadedDocs> onbaseUploadedDocs) {
		this.onbaseUploadedDocs = onbaseUploadedDocs;
	}
	
	public String getDealershipName() {
		return dealershipName;
	}

	public void setDealershipName(String dealershipName) {
		this.dealershipName = dealershipName;
	}


	public String getDealershipCode() {
		return dealershipCode;
	}

	public void setDealershipCode(String dealershipCode) {
		this.dealershipCode = dealershipCode;
	}

}