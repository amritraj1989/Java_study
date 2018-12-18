package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Mapped to QMF_NO_SHOW_REPORTS table
 */
@Entity
@Table(name = "QMF_NO_SHOW_REPORTS")
public class QmfNoShowReports implements Serializable {

	private static final long serialVersionUID = -4574316118841140902L;

	@EmbeddedId
	private QmfNoShowReportsPK id;

	public QmfNoShowReportsPK getId() {
		return id;
	}

	public void setId(QmfNoShowReportsPK id) {
		this.id = id;
	}
	    
}