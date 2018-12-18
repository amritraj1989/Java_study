package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the DOCL database table.
 * @author Singh
 */
@Entity
@Table(name="DOCL")
public class Docl extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DoclPK id;

	@Column(name="ACT_DUTY_COST")
	private BigDecimal actDutyCost;

	@Column(name="ACT_FOB_COST")
	private BigDecimal actFobCost;

	@Column(name="ACT_SHIP_COST")
	private BigDecimal actShipCost;

	@Column(name="ACT_SURC_COST")
	private BigDecimal actSurcCost;

	@Column(name="ASSET_ID")
	private BigDecimal assetId;

	@Column(name="BIN_LOC_ID")
	private BigDecimal binLocId;

	@Column(name="BIN_LOC_TO_ID")
	private BigDecimal binLocToId;

	@Column(name="CRITICAL_IND")
	private String criticalInd;

	@Column(name="DIM_UOM_CODE")
	private String dimUomCode;

	@Column(name="DISC_1")
	private BigDecimal disc1;

	@Column(name="DISC_2")
	private BigDecimal disc2;

	@Column(name="DISC_3")
	private BigDecimal disc3;

    @Temporal( TemporalType.DATE)
	@Column(name="DOC_DATE")
	private Date docDate;

	@Column(name="DOC_NO")
	private String docNo;

	@Column(name="DOCL_TOTAL_PRICE")
	private BigDecimal doclTotalPrice;

	@Column(name="DOCL_UNALLOC_AMOUNT")
	private BigDecimal doclUnallocAmount;

	@Column(name="EXP_DUTY_COST")
	private BigDecimal expDutyCost;

	@Column(name="EXP_FOB_COST")
	private BigDecimal expFobCost;

	@Column(name="EXP_SHIP_COST")
	private BigDecimal expShipCost;

	@Column(name="EXP_SURC_COST")
	private BigDecimal expSurcCost;

	@Column(name="FLEET_LINE_IND")
	private String fleetLineInd;

	@Column(name="GENERIC_EXT_ID")
	private Long genericExtId;

    @Temporal( TemporalType.DATE)
	@Column(name="INSP_DATE")
	private Date inspDate;

	@Column(name="INSP_REQ_IND")
	private String inspReqInd;

	@Column(name="INSP_STATUS")
	private String inspStatus;

	@Column(name="INSURANCE_AMOUNT")
	private BigDecimal insuranceAmount;

	@Column(name="INVOICE_PRICE")
	private BigDecimal invoicePrice;

	@Column(name="ITEM_DIM_1")
	private BigDecimal itemDim1;

	@Column(name="ITEM_DIM_2")
	private BigDecimal itemDim2;

	@Column(name="ITEM_DIM_3")
	private BigDecimal itemDim3;

	@Column(name="JCT_ID")
	private BigDecimal jctId;

	@Column(name="JL_ID")
	private BigDecimal jlId;

	@Column(name="LINE_DESCRIPTION")
	private String lineDescription;

	@Column(name="LINE_STATUS")
	private String lineStatus;

	@Column(name="LINE_TYPE")
	private String lineType;

	@Column(name="LIST_DISCOUNT")
	private BigDecimal listDiscount;

	@Column(name="LIST_PRICE")
	private BigDecimal listPrice;

	@Column(name="NEW_AV_COST")
	private BigDecimal newAvCost;

	@Column(name="PAYMENT_AMOUNT")
	private BigDecimal paymentAmount;

	@Column(name="PB_CID")
	private BigDecimal pbCid;

	@Column(name="PO_TYPE_IND")
	private String poTypeInd;

	@Column(name="PREF_SUPP_ACC")
	private String prefSuppAcc;

	@Column(name="PREF_SUPP_C_ID")
	private BigDecimal prefSuppCId;

	@Column(name="PREF_SUPP_TYPE")
	private String prefSuppType;

	@Column(name="PRINTED_AMOUNT")
	private BigDecimal printedAmount;

	@Column(name="PRODUCT_CATEGORY_CODE")
	private String productCategoryCode;

	@Column(name="PRODUCT_CODE")
	private String productCode;

	@Column(name="QTY_ALLOCATED")
	private BigDecimal qtyAllocated;

	@Column(name="QTY_BACKORDERED")
	private BigDecimal qtyBackordered;

	@Column(name="QTY_CHANGE")
	private BigDecimal qtyChange;

	@Column(name="QTY_INVOICE")
	private BigDecimal qtyInvoice;

	@Column(name="QTY_ORDERED")
	private BigDecimal qtyOrdered;

	@Column(name="QTY_OUTSTANDING")
	private BigDecimal qtyOutstanding;

	@Column(name="QTY_PICK")
	private BigDecimal qtyPick;

	@Column(name="QTY_REJECTED")
	private BigDecimal qtyRejected;

	@Column(name="QTY_SHIPPED")
	private BigDecimal qtyShipped;

	@Column(name="REASON_CODE")
	private String reasonCode;

    @Temporal( TemporalType.DATE)
	@Column(name="REC_DATE_ACTUAL")
	private Date recDateActual;

    @Temporal( TemporalType.DATE)
	@Column(name="REC_DATE_PROMISED")
	private Date recDatePromised;

    @Temporal( TemporalType.DATE)
	@Column(name="REC_DATE_REQUESTED")
	private Date recDateRequested;

	@Column(name="RECLAIM_TAX")
	private String reclaimTax;

    @Temporal( TemporalType.DATE)
	@Column(name="SHIP_DATE_ACTUAL")
	private Date shipDateActual;

    @Temporal( TemporalType.DATE)
	@Column(name="SHIP_DATE_PROMISED")
	private Date shipDatePromised;

    @Temporal( TemporalType.DATE)
	@Column(name="SHIP_DATE_REQUESTED")
	private Date shipDateRequested;

	@Column(name="SOURCE_CODE")
	private String sourceCode;

	@Column(name="STANDARD_COST")
	private BigDecimal standardCost;

	@Column(name="STOCKED_IND")
	private String stockedInd;

    @Temporal( TemporalType.DATE)
	@Column(name="SUP_ACK_DATE")
	private Date supAckDate;

	@Column(name="SUP_ACK_REF")
	private String supAckRef;

	@Column(name="TAX_ID")
	private Long taxId;

	@Column(name="TAX_RATE")
	private BigDecimal taxRate;

	@Column(name="TOTAL_COST")
	private BigDecimal totalCost;

	@Column(name="TOTAL_PRICE")
	private BigDecimal totalPrice;

	@Column(name="UNIT_COST")
	private BigDecimal unitCost;

	@Column(name="UNIT_DISC")
	private BigDecimal unitDisc;

	@Column(name="UNIT_PRICE")
	private BigDecimal unitPrice;

	@Column(name="UNIT_TAX")
	private BigDecimal unitTax;

	@Column(name="UOM_CODE")
	private String uomCode;

	@Column(name="USER_DEF1")
	private String userDef1;

	@Column(name="USER_DEF2")
	private String userDef2;

	@Column(name="USER_DEF3")
	private String userDef3;

	@Column(name="USER_DEF4")
	private String userDef4;

	@Column(name="USER_DEF5")
	private String userDef5;

	@Column(name="USER_DEF6")
	private String userDef6;

	@Column(name="USER_DEF7")
	private String userDef7;

	@Column(name="USER_DEF8")
	private String userDef8;

	@Column(name="USER_DEF9")
	private String userDef9;

	private BigDecimal volume;

	private BigDecimal weight;

	
	//bi-directional many-to-one association to Dist
  	@OneToMany(fetch = FetchType.LAZY,mappedBy="docl", cascade = CascadeType.ALL ,orphanRemoval = true)
  	private List<Dist> dists;

	//bi-directional many-to-one association to Doc
  	@ManyToOne
  	@JoinColumn(name="DOC_ID", insertable=false ,updatable=false)
  	private Doc doc;
  	
  
    public Docl() {
    }

	public DoclPK getId() {
		return this.id;
	}

	public void setId(DoclPK id) {
		this.id = id;
	}
	
	public BigDecimal getActDutyCost() {
		return this.actDutyCost;
	}

	public void setActDutyCost(BigDecimal actDutyCost) {
		this.actDutyCost = actDutyCost;
	}

	public BigDecimal getActFobCost() {
		return this.actFobCost;
	}

	public void setActFobCost(BigDecimal actFobCost) {
		this.actFobCost = actFobCost;
	}

	public BigDecimal getActShipCost() {
		return this.actShipCost;
	}

	public void setActShipCost(BigDecimal actShipCost) {
		this.actShipCost = actShipCost;
	}

	public BigDecimal getActSurcCost() {
		return this.actSurcCost;
	}

	public void setActSurcCost(BigDecimal actSurcCost) {
		this.actSurcCost = actSurcCost;
	}

	public BigDecimal getAssetId() {
		return this.assetId;
	}

	public void setAssetId(BigDecimal assetId) {
		this.assetId = assetId;
	}

	public BigDecimal getBinLocId() {
		return this.binLocId;
	}

	public void setBinLocId(BigDecimal binLocId) {
		this.binLocId = binLocId;
	}

	public BigDecimal getBinLocToId() {
		return this.binLocToId;
	}

	public void setBinLocToId(BigDecimal binLocToId) {
		this.binLocToId = binLocToId;
	}

	public String getCriticalInd() {
		return this.criticalInd;
	}

	public void setCriticalInd(String criticalInd) {
		this.criticalInd = criticalInd;
	}

	public String getDimUomCode() {
		return this.dimUomCode;
	}

	public void setDimUomCode(String dimUomCode) {
		this.dimUomCode = dimUomCode;
	}

	public BigDecimal getDisc1() {
		return this.disc1;
	}

	public void setDisc1(BigDecimal disc1) {
		this.disc1 = disc1;
	}

	public BigDecimal getDisc2() {
		return this.disc2;
	}

	public void setDisc2(BigDecimal disc2) {
		this.disc2 = disc2;
	}

	public BigDecimal getDisc3() {
		return this.disc3;
	}

	public void setDisc3(BigDecimal disc3) {
		this.disc3 = disc3;
	}

	public Date getDocDate() {
		return this.docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	public String getDocNo() {
		return this.docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public BigDecimal getDoclTotalPrice() {
		return this.doclTotalPrice;
	}

	public void setDoclTotalPrice(BigDecimal doclTotalPrice) {
		this.doclTotalPrice = doclTotalPrice;
	}

	public BigDecimal getDoclUnallocAmount() {
		return this.doclUnallocAmount;
	}

	public void setDoclUnallocAmount(BigDecimal doclUnallocAmount) {
		this.doclUnallocAmount = doclUnallocAmount;
	}

	public BigDecimal getExpDutyCost() {
		return this.expDutyCost;
	}

	public void setExpDutyCost(BigDecimal expDutyCost) {
		this.expDutyCost = expDutyCost;
	}

	public BigDecimal getExpFobCost() {
		return this.expFobCost;
	}

	public void setExpFobCost(BigDecimal expFobCost) {
		this.expFobCost = expFobCost;
	}

	public BigDecimal getExpShipCost() {
		return this.expShipCost;
	}

	public void setExpShipCost(BigDecimal expShipCost) {
		this.expShipCost = expShipCost;
	}

	public BigDecimal getExpSurcCost() {
		return this.expSurcCost;
	}

	public void setExpSurcCost(BigDecimal expSurcCost) {
		this.expSurcCost = expSurcCost;
	}

	public String getFleetLineInd() {
		return this.fleetLineInd;
	}

	public void setFleetLineInd(String fleetLineInd) {
		this.fleetLineInd = fleetLineInd;
	}

	public Long getGenericExtId() {
		return this.genericExtId;
	}

	public void setGenericExtId(Long genericExtId) {
		this.genericExtId = genericExtId;
	}

	public Date getInspDate() {
		return this.inspDate;
	}

	public void setInspDate(Date inspDate) {
		this.inspDate = inspDate;
	}

	public String getInspReqInd() {
		return this.inspReqInd;
	}

	public void setInspReqInd(String inspReqInd) {
		this.inspReqInd = inspReqInd;
	}

	public String getInspStatus() {
		return this.inspStatus;
	}

	public void setInspStatus(String inspStatus) {
		this.inspStatus = inspStatus;
	}

	public BigDecimal getInsuranceAmount() {
		return this.insuranceAmount;
	}

	public void setInsuranceAmount(BigDecimal insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public BigDecimal getInvoicePrice() {
		return this.invoicePrice;
	}

	public void setInvoicePrice(BigDecimal invoicePrice) {
		this.invoicePrice = invoicePrice;
	}

	public BigDecimal getItemDim1() {
		return this.itemDim1;
	}

	public void setItemDim1(BigDecimal itemDim1) {
		this.itemDim1 = itemDim1;
	}

	public BigDecimal getItemDim2() {
		return this.itemDim2;
	}

	public void setItemDim2(BigDecimal itemDim2) {
		this.itemDim2 = itemDim2;
	}

	public BigDecimal getItemDim3() {
		return this.itemDim3;
	}

	public void setItemDim3(BigDecimal itemDim3) {
		this.itemDim3 = itemDim3;
	}

	public BigDecimal getJctId() {
		return this.jctId;
	}

	public void setJctId(BigDecimal jctId) {
		this.jctId = jctId;
	}

	public BigDecimal getJlId() {
		return this.jlId;
	}

	public void setJlId(BigDecimal jlId) {
		this.jlId = jlId;
	}

	public String getLineDescription() {
		return this.lineDescription;
	}

	public void setLineDescription(String lineDescription) {
		this.lineDescription = lineDescription;
	}

	public String getLineStatus() {
		return this.lineStatus;
	}

	public void setLineStatus(String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public String getLineType() {
		return this.lineType;
	}

	public void setLineType(String lineType) {
		this.lineType = lineType;
	}

	public BigDecimal getListDiscount() {
		return this.listDiscount;
	}

	public void setListDiscount(BigDecimal listDiscount) {
		this.listDiscount = listDiscount;
	}

	public BigDecimal getListPrice() {
		return this.listPrice;
	}

	public void setListPrice(BigDecimal listPrice) {
		this.listPrice = listPrice;
	}

	public BigDecimal getNewAvCost() {
		return this.newAvCost;
	}

	public void setNewAvCost(BigDecimal newAvCost) {
		this.newAvCost = newAvCost;
	}

	public BigDecimal getPaymentAmount() {
		return this.paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public BigDecimal getPbCid() {
		return this.pbCid;
	}

	public void setPbCid(BigDecimal pbCid) {
		this.pbCid = pbCid;
	}

	public String getPoTypeInd() {
		return this.poTypeInd;
	}

	public void setPoTypeInd(String poTypeInd) {
		this.poTypeInd = poTypeInd;
	}

	public String getPrefSuppAcc() {
		return this.prefSuppAcc;
	}

	public void setPrefSuppAcc(String prefSuppAcc) {
		this.prefSuppAcc = prefSuppAcc;
	}

	public BigDecimal getPrefSuppCId() {
		return this.prefSuppCId;
	}

	public void setPrefSuppCId(BigDecimal prefSuppCId) {
		this.prefSuppCId = prefSuppCId;
	}

	public String getPrefSuppType() {
		return this.prefSuppType;
	}

	public void setPrefSuppType(String prefSuppType) {
		this.prefSuppType = prefSuppType;
	}

	public BigDecimal getPrintedAmount() {
		return this.printedAmount;
	}

	public void setPrintedAmount(BigDecimal printedAmount) {
		this.printedAmount = printedAmount;
	}

	public String getProductCategoryCode() {
		return this.productCategoryCode;
	}

	public void setProductCategoryCode(String productCategoryCode) {
		this.productCategoryCode = productCategoryCode;
	}

	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public BigDecimal getQtyAllocated() {
		return this.qtyAllocated;
	}

	public void setQtyAllocated(BigDecimal qtyAllocated) {
		this.qtyAllocated = qtyAllocated;
	}

	public BigDecimal getQtyBackordered() {
		return this.qtyBackordered;
	}

	public void setQtyBackordered(BigDecimal qtyBackordered) {
		this.qtyBackordered = qtyBackordered;
	}

	public BigDecimal getQtyChange() {
		return this.qtyChange;
	}

	public void setQtyChange(BigDecimal qtyChange) {
		this.qtyChange = qtyChange;
	}

	public BigDecimal getQtyInvoice() {
		return this.qtyInvoice;
	}

	public void setQtyInvoice(BigDecimal qtyInvoice) {
		this.qtyInvoice = qtyInvoice;
	}

	public BigDecimal getQtyOrdered() {
		return this.qtyOrdered;
	}

	public void setQtyOrdered(BigDecimal qtyOrdered) {
		this.qtyOrdered = qtyOrdered;
	}

	public BigDecimal getQtyOutstanding() {
		return this.qtyOutstanding;
	}

	public void setQtyOutstanding(BigDecimal qtyOutstanding) {
		this.qtyOutstanding = qtyOutstanding;
	}

	public BigDecimal getQtyPick() {
		return this.qtyPick;
	}

	public void setQtyPick(BigDecimal qtyPick) {
		this.qtyPick = qtyPick;
	}

	public BigDecimal getQtyRejected() {
		return this.qtyRejected;
	}

	public void setQtyRejected(BigDecimal qtyRejected) {
		this.qtyRejected = qtyRejected;
	}

	public BigDecimal getQtyShipped() {
		return this.qtyShipped;
	}

	public void setQtyShipped(BigDecimal qtyShipped) {
		this.qtyShipped = qtyShipped;
	}

	public String getReasonCode() {
		return this.reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public Date getRecDateActual() {
		return this.recDateActual;
	}

	public void setRecDateActual(Date recDateActual) {
		this.recDateActual = recDateActual;
	}

	public Date getRecDatePromised() {
		return this.recDatePromised;
	}

	public void setRecDatePromised(Date recDatePromised) {
		this.recDatePromised = recDatePromised;
	}

	public Date getRecDateRequested() {
		return this.recDateRequested;
	}

	public void setRecDateRequested(Date recDateRequested) {
		this.recDateRequested = recDateRequested;
	}

	public String getReclaimTax() {
		return this.reclaimTax;
	}

	public void setReclaimTax(String reclaimTax) {
		this.reclaimTax = reclaimTax;
	}

	public Date getShipDateActual() {
		return this.shipDateActual;
	}

	public void setShipDateActual(Date shipDateActual) {
		this.shipDateActual = shipDateActual;
	}

	public Date getShipDatePromised() {
		return this.shipDatePromised;
	}

	public void setShipDatePromised(Date shipDatePromised) {
		this.shipDatePromised = shipDatePromised;
	}

	public Date getShipDateRequested() {
		return this.shipDateRequested;
	}

	public void setShipDateRequested(Date shipDateRequested) {
		this.shipDateRequested = shipDateRequested;
	}

	public String getSourceCode() {
		return this.sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public BigDecimal getStandardCost() {
		return this.standardCost;
	}

	public void setStandardCost(BigDecimal standardCost) {
		this.standardCost = standardCost;
	}

	public String getStockedInd() {
		return this.stockedInd;
	}

	public void setStockedInd(String stockedInd) {
		this.stockedInd = stockedInd;
	}

	public Date getSupAckDate() {
		return this.supAckDate;
	}

	public void setSupAckDate(Date supAckDate) {
		this.supAckDate = supAckDate;
	}

	public String getSupAckRef() {
		return this.supAckRef;
	}

	public void setSupAckRef(String supAckRef) {
		this.supAckRef = supAckRef;
	}

	public Long getTaxId() {
		return this.taxId;
	}

	public void setTaxId(Long taxId) {
		this.taxId = taxId;
	}

	public BigDecimal getTaxRate() {
		return this.taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public BigDecimal getTotalCost() {
		return this.totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	public BigDecimal getTotalPrice() {
		return this.totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getUnitCost() {
		return this.unitCost;
	}

	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}

	public BigDecimal getUnitDisc() {
		return this.unitDisc;
	}

	public void setUnitDisc(BigDecimal unitDisc) {
		this.unitDisc = unitDisc;
	}

	public BigDecimal getUnitPrice() {
		return this.unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getUnitTax() {
		return this.unitTax;
	}

	public void setUnitTax(BigDecimal unitTax) {
		this.unitTax = unitTax;
	}

	public String getUomCode() {
		return this.uomCode;
	}

	public void setUomCode(String uomCode) {
		this.uomCode = uomCode;
	}

	public String getUserDef1() {
		return this.userDef1;
	}

	public void setUserDef1(String userDef1) {
		this.userDef1 = userDef1;
	}

	public String getUserDef2() {
		return this.userDef2;
	}

	public void setUserDef2(String userDef2) {
		this.userDef2 = userDef2;
	}

	public String getUserDef3() {
		return this.userDef3;
	}

	public void setUserDef3(String userDef3) {
		this.userDef3 = userDef3;
	}

	public String getUserDef4() {
		return this.userDef4;
	}

	public void setUserDef4(String userDef4) {
		this.userDef4 = userDef4;
	}

	public String getUserDef5() {
		return this.userDef5;
	}

	public void setUserDef5(String userDef5) {
		this.userDef5 = userDef5;
	}

	public String getUserDef6() {
		return this.userDef6;
	}

	public void setUserDef6(String userDef6) {
		this.userDef6 = userDef6;
	}

	public String getUserDef7() {
		return this.userDef7;
	}

	public void setUserDef7(String userDef7) {
		this.userDef7 = userDef7;
	}

	public String getUserDef8() {
		return this.userDef8;
	}

	public void setUserDef8(String userDef8) {
		this.userDef8 = userDef8;
	}

	public String getUserDef9() {
		return this.userDef9;
	}

	public void setUserDef9(String userDef9) {
		this.userDef9 = userDef9;
	}

	public BigDecimal getVolume() {
		return this.volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getWeight() {
		return this.weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public Doc getDoc() {
		return this.doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

  	public List<Dist> getDists() {
		return dists;
	}

	public void setDists(List<Dist> dists) {
		this.dists = dists;
	}



	@Override
	 public boolean equals(Object object) {
	
	  if (object == null || (!(object instanceof Docl))) {
	       return false;
	   }
	  	Docl other = (Docl) object;
	  	return this.getId().equals(other.getId());
	}
	 
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.DOCL[ pk=" + id + " ]";
    }
	
}