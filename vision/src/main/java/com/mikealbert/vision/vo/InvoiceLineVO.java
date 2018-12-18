package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class InvoiceLineVO implements Serializable{

private static final long serialVersionUID = 1L;
private Long	docId;
private	Long	lineId;
private String  lineDescription;
private BigDecimal	lineCost;
private boolean reclaimable;
private	Long	reclaimLineId ;
private boolean enableEdit = false;
public Long getDocId() {
	return docId;
}
public void setDocId(Long docId) {
	this.docId = docId;
}
public Long getLineId() {
	return lineId;
}
public void setLineId(Long lineId) {
	this.lineId = lineId;
}
public String getLineDescription() {
	return lineDescription;
}
public void setLineDescription(String lineDescription) {
	this.lineDescription = lineDescription;
}
public BigDecimal getLineCost() {
	return lineCost;
}
public void setLineCost(BigDecimal lineCost) {
	this.lineCost = lineCost;
}
public boolean isReclaimable() {
	return reclaimable;
}
public void setReclaimable(boolean reclaimable) {
	this.reclaimable = reclaimable;
}

public Long getReclaimLineId() {
	return reclaimLineId;
}
public void setReclaimLineId(Long reclaimLineId) {
	this.reclaimLineId = reclaimLineId;
}


public boolean isEnableEdit() {
	return enableEdit;
}
public void setEnableEdit(boolean enableEdit) {
	this.enableEdit = enableEdit;
}
@Override
public String toString() {
	return "InvoiceLineVO [docId=" + docId + ", lineId=" + lineId
			+ ", lineDescription=" + lineDescription + ", lineCost=" + lineCost
			+ ", reclaimable=" + reclaimable + ", reclaimLineId="
			+ reclaimLineId + "]";
}

}
