package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.List;

/**
 * Mapped to WARRANTY_DETAILS table
 * @author Raj
 */
@Entity
@Table(name="WARRANTY_DETAILS")
public class WarrantyDetail extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="WD_SEQ")
	@SequenceGenerator(name="WD_SEQ", sequenceName="WD_SEQ", allocationSize=1)
	@NotNull
	@Column(name="WD_ID")
	private long wdId;

	//Bi-directional many-to-one association to WarrantyUnitLink
	@OneToMany(mappedBy="warrantyDetail")
	private List<WarrantyUnitLink> warrantyUnitLinks;

	public long getWdId() {
		return wdId;
	}

	public void setWdId(long wdId) {
		this.wdId = wdId;
	}

	public List<WarrantyUnitLink> getWarrantyUnitLinks() {
		return warrantyUnitLinks;
	}

	public void setWarrantyUnitLinks(List<WarrantyUnitLink> warrantyUnitLinks) {
		this.warrantyUnitLinks = warrantyUnitLinks;
	}
}