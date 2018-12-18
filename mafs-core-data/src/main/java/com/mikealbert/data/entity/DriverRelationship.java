package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Mapped to RELATED_DRIVERS table
 * @author sibley
 */
@Entity
@Table(name = "RELATED_DRIVERS")
public class DriverRelationship extends BaseEntity implements Serializable {
   
   private static final long serialVersionUID = 1L;
   
   @Id
   @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="RDV_SEQ")    
   @SequenceGenerator(name="RDV_SEQ", sequenceName="RDV_SEQ", allocationSize=1)   
   @Basic(optional = false)
   @Column(name = "RDV_ID")
   private Long rdvId;
   
   @Basic(optional = false)
   @Size(max = 10)   
   @Column(name = "DRV_RELATIONSHIP_TYPE_CODE")
   private String relationshipType;
   
   @JoinColumn(name = "PARENT_DRV_ID", referencedColumnName = "DRV_ID")
   //@JoinColumn(name = "CHILD_DRV_ID", referencedColumnName = "DRV_ID")
   @ManyToOne(optional = false)  
   private Driver primaryDriver;
   
   @JoinColumn(name = "CHILD_DRV_ID", referencedColumnName = "DRV_ID")
   //@JoinColumn(name = "PARENT_DRV_ID", referencedColumnName = "DRV_ID")
   @ManyToOne(optional = false)
   private Driver secondaryDriver;

   public DriverRelationship() {}

   public DriverRelationship(Driver primaryDriver, Driver secondaryDriver, String relationshipType) {
       this.setPrimaryDriver(primaryDriver);
       this.setSecondaryDriver(secondaryDriver);
       this.setRelationshipType(relationshipType);
   }
   
   public Long getRdvId() {
       return rdvId;
   }

   public void setRdvId(Long rdvId) {
       this.rdvId = rdvId;
   }

   public String getRelationshipType() {
	return relationshipType;
}

public void setRelationshipType(String relationshipType) {
	this.relationshipType = relationshipType;
}

public Driver getPrimaryDriver() {
	   return primaryDriver;
   }

   public void setPrimaryDriver(Driver primaryDriver) {
	   this.primaryDriver = primaryDriver;
   }

   public Driver getSecondaryDriver() {
	return secondaryDriver;
}

public void setSecondaryDriver(Driver secondaryDriver) {
	this.secondaryDriver = secondaryDriver;
}

@Override
   public int hashCode() {
	   int hash = 0;
	   hash += (rdvId != null ? rdvId.hashCode() : 0);
	   return hash;
   }

   @Override
   public boolean equals(Object object) {
       // TODO: Warning - this method won't work in the case the id fields are not set
       if (!(object instanceof DriverRelationship)) {
           return false;
       }
       DriverRelationship other = (DriverRelationship) object;
       if ((this.rdvId == null && other.rdvId != null) || (this.rdvId != null && !this.rdvId.equals(other.rdvId))) {
           return false;
       }
       return true;
   }

   @Override
   public String toString() {
       return "com.mikealbert.entity.DriverRelationship[ rdvId=" + rdvId + " ]";
   }
    
}
