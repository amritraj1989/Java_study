package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

import java.math.BigDecimal;


/**
 * The persistent class for the DOC_TRAN_GL_CODES database table.
 * @author Roshan K
 */
@Entity
@Table(name="DOC_TRAN_GL_CODES")
public class DocTranGlCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DocTranGlCodePK id;

	@Column(name="COMPRESS_IND")
	private String compressInd;

	@Column(name="GC_C_ID")
	private BigDecimal gcCId;

	@Column(name="GL_CODE")
	private String glCode;

	@Column(name="POSTING_SEQ_NO")
	private BigDecimal postingSeqNo;

	public DocTranGlCode() {
	}

	public DocTranGlCodePK getId() {
		return this.id;
	}

	public void setId(DocTranGlCodePK id) {
		this.id = id;
	}

	public String getCompressInd() {
		return this.compressInd;
	}

	public void setCompressInd(String compressInd) {
		this.compressInd = compressInd;
	}

	public BigDecimal getGcCId() {
		return this.gcCId;
	}

	public void setGcCId(BigDecimal gcCId) {
		this.gcCId = gcCId;
	}

	public String getGlCode() {
		return this.glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public BigDecimal getPostingSeqNo() {
		return this.postingSeqNo;
	}

	public void setPostingSeqNo(BigDecimal postingSeqNo) {
		this.postingSeqNo = postingSeqNo;
	}

	
}