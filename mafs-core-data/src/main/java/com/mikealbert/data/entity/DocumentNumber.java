package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
* Mapped to DOC_NOS Table
* @author sibley
*/
@Entity
@Table(name = "DOC_NOS")
public class DocumentNumber extends BaseEntity implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @EmbeddedId
   protected DocumentNumberPK docNosPK;
   
   @Size(max = 3)
   @Column(name = "PRE_FIX")
   private String preFix;
   
   @Column(name = "NEXT_NO")
   private Integer nextNo;

   public DocumentNumber() {}

   public DocumentNumber(DocumentNumberPK docNosPK) {
       this.docNosPK = docNosPK;
   }

   public DocumentNumber(long cId, String domain) {
       this.docNosPK = new DocumentNumberPK(cId, domain);
   }

   public DocumentNumberPK getDocNosPK() {
       return docNosPK;
   }

   public void setDocNosPK(DocumentNumberPK docNosPK) {
       this.docNosPK = docNosPK;
   }

   public String getPreFix() {
       return preFix;
   }

   public void setPreFix(String preFix) {
       this.preFix = preFix;
   }

   public Integer getNextNo() {
       return nextNo;
   }

   public void setNextNo(Integer nextNo) {
       this.nextNo = nextNo;
   }

   @Override
   public int hashCode() {
       int hash = 0;
       hash += (docNosPK != null ? docNosPK.hashCode() : 0);
       return hash;
   }

   @Override
   public boolean equals(Object object) {
       // TODO: Warning - this method won't work in the case the id fields are not set
       if (!(object instanceof DocumentNumber)) {
           return false;
       }
       DocumentNumber other = (DocumentNumber) object;
       if ((this.docNosPK == null && other.docNosPK != null) || (this.docNosPK != null && !this.docNosPK.equals(other.docNosPK))) {
           return false;
       }
       return true;
   }

   @Override
   public String toString() {
       return "com.mikealbert.data.entity.DocNos[ docNosPK=" + docNosPK + " ]";
   }    
}
