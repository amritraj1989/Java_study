package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "EXT_ACC_CONSULTANTS")
public class ExtAccConsultant extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
	@Column(name="CONSUL_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONSUL_ID_SEQ")
    @SequenceGenerator(name = "CONSUL_ID_SEQ", sequenceName = "CONSUL_ID_SEQ", allocationSize = 1)
    private Long consulId;
    
    @Column(name="EMPLOYEE_NO")
    private String employeeNo;
    
    @Column(name="ROLE_TYPE")
    private String roleType;
    
    @Column(name="DEFAULT_IND")
    private String defaultInd;
    
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExternalAccount externalAccounts;
    
    public ExtAccConsultant() {
    }
    
    public Long getConsulId() {
		return consulId;
	}

	public void setConsulId(Long consulId) {
		this.consulId = consulId;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getDefaultInd() {
		return defaultInd;
	}

	public void setDefaultInd(String defaultInd) {
		this.defaultInd = defaultInd;
	}

	public ExternalAccount getExternalAccounts() {
		return externalAccounts;
	}

	public void setExternalAccounts(ExternalAccount externalAccounts) {
		this.externalAccounts = externalAccounts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((consulId == null) ? 0 : consulId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ExtAccConsultant other = (ExtAccConsultant) obj;
		if(consulId == null) {
			if(other.consulId != null)
				return false;
		} else if(!consulId.equals(other.consulId))
			return false;
		return true;
	}

}
