package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="PROCESS_STAGES")
public class ProcessStage extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -5852920913338852927L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PSG_SEQ")    
    @SequenceGenerator(name="PSG_SEQ", sequenceName="PSG_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "PSG_ID")
    private Long psgId;
    
	@Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "NAME")
    private String processStageName;

    @JoinColumn(name = "PRS_PRS_ID", referencedColumnName = "PRS_ID", insertable=false, updatable=false)
    @OneToOne(fetch = FetchType.EAGER)
    private Process process;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "processStage")
    private List<ProcessStageObject> processStageObjects;
    
	public Long getPsgId() {
		return psgId;
	}

	public void setPsgId(Long psgId) {
		this.psgId = psgId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProcessStageName() {
		return processStageName;
	}

	public void setProcessStageName(String processStageName) {
		this.processStageName = processStageName;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public List<ProcessStageObject> getProcessStageObjects() {
		return processStageObjects;
	}

	public void setProcessStageObjects(List<ProcessStageObject> processStageObjects) {
		this.processStageObjects = processStageObjects;
	}
	
}
