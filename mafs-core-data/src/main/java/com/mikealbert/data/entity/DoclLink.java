package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the DOCL_LINKS database table.
 * 
 */
@Entity
@Table(name="DOCL_LINKS")
public class DoclLink implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DoclLinkPK id;

	public DoclLink() {
	}

	public DoclLinkPK getId() {
		return this.id;
	}

	public void setId(DoclLinkPK id) {
		this.id = id;
	}

}