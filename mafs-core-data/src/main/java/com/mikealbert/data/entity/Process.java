package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="PROCESSES")
public class Process extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 4849095396920253005L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PRS_SEQ")    
    @SequenceGenerator(name="PRS_SEQ", sequenceName="PRS_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "PRS_ID")
    private Long prs_id;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "NAME")
    private String processName;
    
}
