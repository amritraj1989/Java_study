package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Mapped to MODELS table
 */
@Entity
@Table(name = "MODELS")
public class Model extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -1726992924572540629L;
	
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "MDL_ID")
    private Long modelId;
    
    @Column(name="MODEL_DESC", nullable=false, insertable=false, updatable=false, length=240)
    private String modelDescription;
            
    @Column(name="GROSS_VEH_WEIGHT")
    private BigDecimal grossVehWeight;    
    
    @Column(name="STANDARD_CODE")
    @Size(max=60)
    private String standardCode;     
        
    @Column(name="QUOTE_PERMITTED_FLAG")
    private String quotePermittedInd;
    
    @Column(name="STANDARD_EDI_NO")
    private String standardEDINo;    
    
    @JoinColumn(name = "MAK_MAK_ID", referencedColumnName = "MAK_ID")
    @ManyToOne
    private Make make;     

    @JoinColumn(name = "MMY_MMY_ID", referencedColumnName = "MMY_ID")
    @ManyToOne
    private ModelMarkYear modelMarkYear;
    
    @JoinColumn(name = "MTP_MTP_ID", referencedColumnName = "MTP_ID")
    @ManyToOne
    private ModelType modelType;     

    @JoinColumn(name = "MRG_MRG_ID", referencedColumnName = "MRG_ID")
    @ManyToOne
    private MakeModelRange makeModelRange;
    
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)    
    private List<ModelPrice> modelPrices;
    
    @OneToMany(mappedBy = "model")    
    private List<OptionPackHeader> optionPackHeaders; 
    
    @OneToMany(mappedBy = "model")    
    private List<OptionalAccessory> optionalAccessories; 
    
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)    
    private List<DealerAccessory> dealerAccessories;  
    
    @OneToMany(mappedBy = "model") 
    private List<StandardAccessory> standardAccessories;
    
    //bi-directional many-to-one association to VehicleConfigurationModel
  	@OneToMany(mappedBy="model")
  	private List<VehicleConfigModel> vehicleConfigModels;
  	
  	@Column(name="STANDARD_DELIVERY_LEAD_TIME")
    private Long stdDeliveryLeadTime;

    @OneToMany(mappedBy = "model")    
    private List<DeliveryCost> deliveryCosts;
    
	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public String getModelDescription() {
		return modelDescription;
	}

	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}

	public BigDecimal getGrossVehWeight() {
		return grossVehWeight;
	}

	public void setGrossVehWeight(BigDecimal grossVehWeight) {
		this.grossVehWeight = grossVehWeight;
	}
	
	public String getStandardCode() {
		return standardCode;
	}

	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}

	public String getQuotePermittedInd() {
		return quotePermittedInd;
	}

	public void setQuotePermittedInd(String quotePermittedInd) {
		this.quotePermittedInd = quotePermittedInd;
	}

	public String getStandardEDINo() {
		return standardEDINo;
	}

	public void setStandardEDINo(String standardEDINo) {
		this.standardEDINo = standardEDINo;
	}

	public Make getMake() {
		return make;
	}

	public void setMake(Make make) {
		this.make = make;
	}

	public ModelMarkYear getModelMarkYear() {
		return modelMarkYear;
	}

	public void setModelMarkYear(ModelMarkYear modelMarkYear) {
		this.modelMarkYear = modelMarkYear;
	}

	public ModelType getModelType() {
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	public MakeModelRange getMakeModelRange() {
		return makeModelRange;
	}

	public void setMakeModelRange(MakeModelRange makeModelRange) {
		this.makeModelRange = makeModelRange;
	}

	public List<ModelPrice> getModelPrices() {
		return modelPrices;
	}

	public void setModelPrices(List<ModelPrice> modelPrices) {
		this.modelPrices = modelPrices;
	}

	public List<OptionPackHeader> getOptionPackHeaders() {
		return optionPackHeaders;
	}

	public void setOptionPackHeaders(List<OptionPackHeader> optionPackHeaders) {
		this.optionPackHeaders = optionPackHeaders;
	}

	public List<OptionalAccessory> getOptionalAccessories() {
		return optionalAccessories;
	}

	public void setOptionalAccessories(List<OptionalAccessory> optionalAccessories) {
		this.optionalAccessories = optionalAccessories;
	}

	public List<DealerAccessory> getDealerAccessories() {
		return dealerAccessories;
	}

	public void setDealerAccessories(List<DealerAccessory> dealerAccessories) {
		this.dealerAccessories = dealerAccessories;
	}

	public List<StandardAccessory> getStandardAccessories() {
		return standardAccessories;
	}

	public void setStandardAccessories(List<StandardAccessory> standardAccessories) {
		this.standardAccessories = standardAccessories;
	}

	public List<VehicleConfigModel> getVehicleConfigModels() {
		return vehicleConfigModels;
	}

	public void setVehicleConfigModels(List<VehicleConfigModel> vehicleConfigModels) {
		this.vehicleConfigModels = vehicleConfigModels;
	}

	public Long getStdDeliveryLeadTime() {
		return stdDeliveryLeadTime;
	}

	public void setStdDeliveryLeadTime(Long stdDeliveryLeadTime) {
		this.stdDeliveryLeadTime = stdDeliveryLeadTime;
	}

	public List<DeliveryCost> getDeliveryCosts() {
		return deliveryCosts;
	}

	public void setDeliveryCosts(List<DeliveryCost> deliveryCosts) {
		this.deliveryCosts = deliveryCosts;
	}

}
