package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the ONBASE_UPLOADED_DOCS database table.
 * 
 */
@Entity
@Table(name = "ONBASE_UPLOADED_DOCS")
public class OnbaseUploadedDocs extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 955129842755388342L;
	/*
	 *
	 * We have added getNextPK() method in custom DAO of onbaseUploadedDocsDAO to get PK based on OBD_SEQ sequence .
	 * We pass pk while inserting the record because pk is needed to store in index data 
	 *
	 */
	@Id 
	@Column(name = "OBD_ID")
	private Long obdId; // unique index key helpful to get document from onbase

	@Column(name = "OBJECT_ID")
	private String objectId; // willow/vision entity id

	@Column(name = "OBJECT_TYPE")
	private String objectType;  //willow/vision entity type

	@Column(name = "FILE_NAME")
	private String fileName;//display name in any UI(Label)

	@Column(name = "DOC_TYPE")
	private String docType;// onbase doc type 

	@Column(name = "DOC_SUB_TYPE")
	private String docSubType;//  sub category within a doc type 

	@Column(name = "INDEX_KEY")
	private String indexKey;// onbase stored index 

	@Column(name = "OBSOLETE_YN")
	private String obsoleteYn;

	@Column(name = "FILE_TYPE")
	private String fileType;//file ext type

	// bi-directional many-to-one association to VehicleConfiguration
	@Transient
	private UpfitterQuote upfitterQuote;
	
	// bi-directional many-to-one association to QuoteRequest
	@Transient
	private QuoteRequest quoteRequest;	
	
	@Transient
	private byte fileData[];

	@Transient
	private boolean needToUpload;

	@Transient
	private String fileNameWithExt;

	public String getFileNameWithExt() {

		String value = "";
		if (fileName != null) {
			value = fileName;
		}
		if (fileType != null) {
			value = value + "." + fileType;
		}

		return value;
	}

	public OnbaseUploadedDocs() {
	}

	public Long getObdId() {
		return obdId;
	}

	public void setObdId(Long obdId) {
		this.obdId = obdId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getDocSubType() {
		return docSubType;
	}

	public void setDocSubType(String docSubType) {
		this.docSubType = docSubType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getIndexKey() {
		return indexKey;
	}

	public void setIndexKey(String indexKey) {
		this.indexKey = indexKey;
	}

	public String getObsoleteYn() {
		return obsoleteYn;
	}

	public void setObsoleteYn(String obsoleteYn) {
		this.obsoleteYn = obsoleteYn;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public UpfitterQuote getUpfitterQuote() {
		return upfitterQuote;
	}

	public void setUpfitterQuote(UpfitterQuote upfitterQuote) {
		this.upfitterQuote = upfitterQuote;
	}
	
	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public boolean isNeedToUpload() {
		return needToUpload;
	}

	public void setNeedToUpload(boolean needToUpload) {
		this.needToUpload = needToUpload;
	}

	public QuoteRequest getQuoteRequest() {
		return quoteRequest;
	}

	public void setQuoteRequest(QuoteRequest quoteRequest) {
		this.quoteRequest = quoteRequest;
	}

}