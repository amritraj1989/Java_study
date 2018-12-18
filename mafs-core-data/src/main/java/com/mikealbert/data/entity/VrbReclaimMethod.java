package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;


/**
 * The persistent class for the VRB_RECLAIM_METHODS database table.
 * 
 */
@Entity
@Table(name="VRB_RECLAIM_METHODS")
public class VrbReclaimMethod extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RECLAIM_METHOD")
	private String reclaimMethod;

	private String description;

	//bi-directional many-to-one association to VrbDiscount
	@OneToMany(mappedBy="vrbReclaimMethod")
	private Set<VrbDiscount> vrbDiscounts;

    public VrbReclaimMethod() {
    }

	public String getReclaimMethod() {
		return this.reclaimMethod;
	}

	public void setReclaimMethod(String reclaimMethod) {
		this.reclaimMethod = reclaimMethod;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<VrbDiscount> getVrbDiscounts() {
		return this.vrbDiscounts;
	}

	public void setVrbDiscounts(Set<VrbDiscount> vrbDiscounts) {
		this.vrbDiscounts = vrbDiscounts;
	}
	
}