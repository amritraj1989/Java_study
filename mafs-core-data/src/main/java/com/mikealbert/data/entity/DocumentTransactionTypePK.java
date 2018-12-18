package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Primary key on DOC_TRANS table
 * @author sibley
 */
@Embeddable
public class DocumentTransactionTypePK implements Serializable {
   
    @NotNull
    @Column(name = "C_ID")
    private Long cId;

    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "DOC_TYPE")
    private String documentType;
    
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "TRAN_TYPE")
    private String transactionType;    

    public DocumentTransactionTypePK() {}

    public Long getCId() {
        return cId;
    }

    public void setCId(Long cId) {
        this.cId = cId;
    }

    public String getDocumentType() {
		return documentType;
	}


	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.DocumentTransactionTypePK[ cId=" + cId + ", documentType=" + getDocumentType() +", transactionType=" + getTransactionType()+ " ]";
    }    
}
