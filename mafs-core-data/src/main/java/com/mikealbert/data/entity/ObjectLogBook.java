package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * The persistent class for the VISION.OBJECT_LOG_BOOKS database table.
 * @author sibley
 */
@Entity
@Table(name="OBJECT_LOG_BOOKS")
public class ObjectLogBook extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OLB_SEQ")    
    @SequenceGenerator(name="OLB_SEQ", sequenceName="OLB_SEQ", allocationSize=1)      
    @Column(name = "OLB_ID", nullable = false)
    private Long olbId;
    
    @Column(name = "OBJECT_ID", nullable = false)
    private Long objectId;
    
    @Size(min = 1, max = 40)
    @Column(name = "OBJECT_TYPE")
    private String objectType;
    
    @Size(min = 1, max = 40)
    @Column(name = "OBJECT_NAME")
    private String objectName;
        
    @JoinColumn(name = "LBK_LBK_ID", referencedColumnName = "LBK_ID")
    @ManyToOne(optional = false)
    private LogBook logBook;  
    
    @OrderBy("entryDate DESC")
    @OneToMany(mappedBy = "objectLogBook",cascade=CascadeType.MERGE, orphanRemoval=true)
    private List<LogBookEntry> logBookEntries;     

    public ObjectLogBook() {}


	public Long getOlbId() {
		return olbId;
	}


	public void setOlbId(Long olbId) {
		this.olbId = olbId;
	}


	public Long getObjectId() {
		return objectId;
	}


	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}


	public String getObjectType() {
		return objectType;
	}


	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}


	public String getObjectName() {
		return objectName;
	}


	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}


	public LogBook getLogBook() {
		return logBook;
	}


	public void setLogBook(LogBook logBook) {
		this.logBook = logBook;
	}


	public List<LogBookEntry> getLogBookEntries() {
		return logBookEntries;
	}


	public void setLogBookEntries(List<LogBookEntry> logBookEntries) {
		this.logBookEntries = logBookEntries;
	}


	@Override
    public String toString() {
        return "com.mikealbert.data.entity.LogBook[ code=" + getOlbId() + " ]";
    }
  	
}