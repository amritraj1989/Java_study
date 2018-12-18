package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
* Maps to the VISION.VEHICLE_REPLACEMENT_V view
* @author sibley
*/
@Entity
@Table(name = "VEHICLE_REPLACEMENT_V")
public class VehicleReplacementV implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @EmbeddedId
private VehicleReplacementVPK vehicleReplacementVPK;   
      
   @Column(name = "REPLACEMENT_UNIT_NO", insertable = false, updatable = false)
   private String replacementUnitNo;
   
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "ETA_DATE", insertable = false, updatable = false)
   private Date etaDate;
   
   @Temporal(TemporalType.TIMESTAMP)   
   @Column(name = "DEALER_DELIVER_DATE", insertable = false, updatable = false)
   private Date dealerDeliverDate;
   
   @Temporal(TemporalType.TIMESTAMP)
   @Column(name = "IN_SERVICE_DATE", insertable = false, updatable = false)
   private Date inServiceDate;

   public VehicleReplacementV() {}

   public VehicleReplacementVPK getVehicleReplacementVPK() {
	return vehicleReplacementVPK;
}

public void setVehicleReplacementVPK(VehicleReplacementVPK vehicleReplacementVPK) {
	this.vehicleReplacementVPK = vehicleReplacementVPK;
}

public String getReplacementUnitNo() {
	return replacementUnitNo;
}

public void setReplacementUnitNo(String replacementUnitNo) {
	this.replacementUnitNo = replacementUnitNo;
}

public Date getEtaDate() {
       return etaDate;
   }

   public void setEtaDate(Date etaDate) {
       this.etaDate = etaDate;
   }

   public Date getDealerDeliverDate() {
       return dealerDeliverDate;
   }

   public void setDealerDeliverDate(Date dealerDeliverDate) {
       this.dealerDeliverDate = dealerDeliverDate;
   }

   public Date getInServiceDate() {
       return inServiceDate;
   }

   public void setInServiceDate(Date inServiceDate) {
       this.inServiceDate = inServiceDate;
   }

   @Override
   public String toString() {
       return "com.mikealbert.entity.VehicleReplacementV[ vehicleReplacementVPK=" + getVehicleReplacementVPK() + " ]";
   }


}
