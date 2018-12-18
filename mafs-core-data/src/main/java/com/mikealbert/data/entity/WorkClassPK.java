package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite Key on WorkClass
 */
@Embeddable
public class WorkClassPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6586125483295771472L;

	@Column(name="C_ID", unique=true, nullable=false, precision=12)
	private long cId;
	
	@Column(name="WORK_CLASS", nullable=false, length=15)
	private String workClass;

	public long getcId() {
		return cId;
	}

	public void setcId(long cId) {
		this.cId = cId;
	}

	public String getWorkClass() {
		return workClass;
	}

	public void setWorkClass(String workClass) {
		this.workClass = workClass;
	}
	
	@Override
	public String toString(){
        return "com.mikealbert.data.entity.WorkClassPK[ cId=" + getcId() + " workClass=" + getWorkClass() + "]";		
	}
	
}
