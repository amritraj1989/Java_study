package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the CAPITAL_ELEMENT_GROUPS database table.
 * @author Singh
 */
@Entity
@Table(name="CAPITAL_ELEMENT_GROUPS")
public class CapitalElementGroup extends BaseEntity  implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="CEGID_SEQ" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CEGID_SEQ")
	@Column(name="CEG_ID")
	private long cegId;

	@Column(name="GROUP_NAME")
	private String groupName;

	@Column(name="GROUP_DISPLAY_SEQ")
	private int groupDisplaySeq;
	
    public CapitalElementGroup() {
    }

	public long getCegId() {
		return this.cegId;
	}

	public void setCegId(long cegId) {
		this.cegId = cegId;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupDisplaySeq() {
		return groupDisplaySeq;
	}

	public void setGroupDisplaySeq(int groupDisplaySeq) {
		this.groupDisplaySeq = groupDisplaySeq;
	}

	
}