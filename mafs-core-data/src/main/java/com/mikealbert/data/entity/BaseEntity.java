package com.mikealbert.data.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
/**
 * Super class composed of common entity variables
 */

@MappedSuperclass
public class BaseEntity {
    @Version
    @Column(name = "VERSIONTS")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date versionts;

	public Date getVersionts() {
		return versionts;
	}
}
