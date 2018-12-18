package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 * Composite Primary Key for REPORTS_SPOOL table
 * @author Amritraj
 */
@Embeddable
public class ReportsSpoolPK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "RUN_ID")
    private long runId;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "SEQUENCE_NO")
    private long sequenceNo;
    

    public ReportsSpoolPK() {}

    public ReportsSpoolPK(long runId, long sequenceNo) {
        this.runId = runId;
        this.sequenceNo = sequenceNo;
    }

}
