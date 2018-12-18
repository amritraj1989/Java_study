package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * The persistent class for the VISION.LOG_BOOKS database table.
 * @author sibley
 */
@Entity
@Table(name="LOG_BOOKS")
public class LogBook extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LBK_SEQ")    
    @SequenceGenerator(name="LBK_SEQ", sequenceName="LBK_SEQ", allocationSize=1)      
    @Column(name = "LBK_ID", nullable=false)
    private Long lbkId;
    
    @Size(min = 1, max = 60)
    @Column(name = "NAME")
    private String name;
    
    @Size(min = 1, max = 20)
    @Column(name = "TYPE")
    private String type;
    
    @Size(min = 1, max = 80)
    @Column(name = "DESCRIPTION")
    private String description;
    
    @OneToMany(mappedBy = "logBook", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ObjectLogBook> objectLogBooks;     
    

    public LogBook() {}


    public Long getLbkId() {
		return lbkId;
	}


	public void setLbkId(Long lbkId) {
		this.lbkId = lbkId;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}



	public List<ObjectLogBook> getObjectLogBooks() {
		return objectLogBooks;
	}


	public void setObjectLogBooks(List<ObjectLogBook> objectLogBooks) {
		this.objectLogBooks = objectLogBooks;
	}


	@Override
    public String toString() {
        return "com.mikealbert.data.entity.LogBook[ code=" + getLbkId() + " ]";
    }
  	
}