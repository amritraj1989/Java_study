package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="PROCESS_STAGE_OBJECTS")
public class ProcessStageObject extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 5026155282721434645L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PSO_SEQ")    
    @SequenceGenerator(name="PSO_SEQ", sequenceName="PSO_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "PSO_ID")
	private Long psoId;
	
	@Column(name = "OBJECT_ID")
	private Long objectId;
	
	@Column(name = "OBJECT_NAME")
	private String objectName;
	
	@Column(name = "ENTERED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date enteredDate;
	
	@Size(max = 1)
	@Column(name = "INCLUDE_YN")
	private String includeYN;
	
	@Column(name = "OP_USER")
	private String opUser;
	
	@Size(max = 1)
	@Column(name = "REFRESH_YN")
	private String refreshYN;
	
	@Size(max = 200)
	@Column(name = "REASON")
	private String reason;		
	
	@JoinColumn(name = "PSG_PSG_ID", referencedColumnName = "PSG_ID")
    @ManyToOne(optional = false)
    private ProcessStage processStage;
	
	@Column(name = "PROPERTY_1")
	private String property1;	
	
	@Column(name = "PROPERTY_2")
	private String property2;
	
	@Column(name = "PROPERTY_3")
	private String property3;
	
	@Column(name = "PROPERTY_4")
	private String property4;
	
	@Column(name = "PROPERTY_5")
	private String property5;
	
	@Column(name = "PROPERTY_6")
	private String property6;
	
	@Column(name = "PROPERTY_7")
	private String property7;
	
	@Column(name = "PROPERTY_8")
	private String property8;
	
	@Column(name = "PROPERTY_9")
	private String property9;
	
	@Column(name = "PROPERTY_10")
	private String property10;
	
	@Column(name = "PROPERTY_11")
	private String property11;
	
	@Column(name = "PROPERTY_12")
	private String property12;
	
	public Long getPsoId() {
		return psoId;
	}

	public void setPsoId(Long psoId) {
		this.psoId = psoId;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Date getEnteredDate() {
		return enteredDate;
	}

	public void setEnteredDate(Date enteredDate) {
		this.enteredDate = enteredDate;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getIncludeYN() {
		return includeYN;
	}

	public void setIncludeYN(String includeYN) {
		this.includeYN = includeYN;
	}

	public String getOpUser() {
		return opUser;
	}

	public void setOpUser(String opUser) {
		this.opUser = opUser;
	}

	public ProcessStage getProcessStage() {
		return processStage;
	}

	public void setProcessStage(ProcessStage processStage) {
		this.processStage = processStage;
	}

	public String getRefreshYN() {
		return refreshYN;
	}

	public void setRefreshYN(String refreshYN) {
		this.refreshYN = refreshYN;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getProperty1() {
		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public String getProperty2() {
		return property2;
	}

	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	public String getProperty3() {
		return property3;
	}

	public void setProperty3(String property3) {
		this.property3 = property3;
	}

	public String getProperty4() {
		return property4;
	}

	public void setProperty4(String property4) {
		this.property4 = property4;
	}

	public String getProperty5() {
		return property5;
	}

	public void setProperty5(String property5) {
		this.property5 = property5;
	}

	public String getProperty6() {
		return property6;
	}

	public void setProperty6(String property6) {
		this.property6 = property6;
	}

	public String getProperty7() {
		return property7;
	}

	public void setProperty7(String property7) {
		this.property7 = property7;
	}

	public String getProperty8() {
		return property8;
	}

	public void setProperty8(String property8) {
		this.property8 = property8;
	}

	public String getProperty9() {
		return property9;
	}

	public void setProperty9(String property9) {
		this.property9 = property9;
	}

	public String getProperty10() {
		return property10;
	}

	public void setProperty10(String property10) {
		this.property10 = property10;
	}

	public String getProperty11() {
		return property11;
	}

	public void setProperty11(String property11) {
		this.property11 = property11;
	}

	public String getProperty12() {
		return property12;
	}

	public void setProperty12(String property12) {
		this.property12 = property12;
	}
	
}
