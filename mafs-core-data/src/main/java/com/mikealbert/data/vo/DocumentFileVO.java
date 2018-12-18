package com.mikealbert.data.vo;

import java.io.Serializable;

public class DocumentFileVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long fileId;
	private String fileName;
	private String fileExt;
	private String filePath;
	private String fileType;
	private byte fileData[];
	private boolean uploadDoc;//its valid file for upload
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public byte[] getFileData() {
		return fileData;
	}
	public void setFileData(byte fileData[]) {
		this.fileData = fileData;
	}
	public String getFileExt() {
		return fileExt;
	}
	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public boolean isUploadDoc() {
		return uploadDoc;
	}
	public void setUploadDoc(boolean uploadDoc) {
		this.uploadDoc = uploadDoc;
	}
	
}
