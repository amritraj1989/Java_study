package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the DOC_LINKS database table.
 * 
 */
@Entity
@Table(name="DOC_LINKS")
public class DocLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DocLinkPK id;
	
    @JoinColumn(name = "CHILD_DOC_ID", referencedColumnName = "DOC_ID", insertable=false, updatable=false)	
    @ManyToOne
	private Doc childDoc;

	public DocLink() {
	}

	public DocLinkPK getId() {
		return this.id;
	}

	public void setId(DocLinkPK id) {
		this.id = id;
	}

	public Doc getChildDoc() {
		return childDoc;
	}

	public void setChildDoc(Doc childDoc) {
		this.childDoc = childDoc;
	}

}