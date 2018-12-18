package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Mapped to DRIVER_MANUAL_STATUS_CODES
 * @author sibley
 */
@Entity
@Table(name = "DRIVER_MANUAL_STATUS_CODES")
public class DriverManualStatusCode extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "STATUS_CODE")
    private String statusCode;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
        
    public DriverManualStatusCode() {
    }

    public DriverManualStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public DriverManualStatusCode(String statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "com.mikealbert.entity.DriverManualStatusCodes[ statusCode=" + statusCode + " ]";
    }
    
}
