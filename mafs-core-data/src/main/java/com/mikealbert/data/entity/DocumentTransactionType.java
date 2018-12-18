package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;


/**
 * The persistent class for the DOC_TRANS database table.
 * @author sibley
 */
@Entity
@Table(name="DOC_TRANS")
public class DocumentTransactionType extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
	private DocumentTransactionTypePK documentTransactionTypePK;
    
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
    

    public DocumentTransactionType() {}

 
	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


	public DocumentTransactionTypePK getDocumentTransactionTypePK() {
		return documentTransactionTypePK;
	}


	public void setDocumentTransactionTypePK(DocumentTransactionTypePK documentTransactionTypePK) {
		this.documentTransactionTypePK = documentTransactionTypePK;
	}

    	
}