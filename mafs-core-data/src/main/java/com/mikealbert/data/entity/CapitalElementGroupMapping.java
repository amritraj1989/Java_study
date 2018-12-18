package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the CAPITAL_ELEMENT_GROUP_MAPPING database table.
 * @author Singh
 */
@Entity
@Table(name="CAPITAL_ELEMENT_GROUP_MAPPING")
public class CapitalElementGroupMapping extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CAPITAL_ELEMENT_GROUP_MAPPING_CEGMID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CAPITAL_ELEMENT_GROUP_MAPPING_CEGMID_GENERATOR")
	@Column(name="CEGM_ID")
	private long cegmId;

	@Column(name="GROUP_ELEMENT_DISPLAY_SEQ")
	private int groupElementDisplaySeq;

	//bi-directional many-to-one association to CapitalElement
    @ManyToOne
	@JoinColumn(name="CEL_CEL_ID")
	private CapitalElement capitalElement;

	//bi-directional many-to-one association to CapitalElementGroup
    @ManyToOne
	@JoinColumn(name="CEG_CEG_ID")
	private CapitalElementGroup capitalElementGroup;

    public CapitalElementGroupMapping() {
    }

	public long getCegmId() {
		return this.cegmId;
	}

	public void setCegmId(int cegmId) {
		this.cegmId = cegmId;
	}

	public int getGroupElementDisplaySeq() {
		return this.groupElementDisplaySeq;
	}

	public void setGroupElementDisplaySeq(int groupElementDisplaySeq) {
		this.groupElementDisplaySeq = groupElementDisplaySeq;
	}

	public CapitalElement getCapitalElement() {
		return this.capitalElement;
	}

	public void setCapitalElement(CapitalElement capitalElement) {
		this.capitalElement = capitalElement;
	}
	
	public CapitalElementGroup getCapitalElementGroup() {
		return this.capitalElementGroup;
	}

	public void setCapitalElementGroup(CapitalElementGroup capitalElementGroup) {
		this.capitalElementGroup = capitalElementGroup;
	}
	
}