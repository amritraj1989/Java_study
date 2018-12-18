package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the OPTION_PACK_TYPE_CODES database table.
 * @author sibley
 */
@Entity
@Table(name="OPTION_PACK_TYPE_CODES")
public class OptionPackTypeCode extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6254874864214801901L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OPTC_SEQ")    
    @SequenceGenerator(name="OPTC_SEQ", sequenceName="OPTC_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "OPTC_ID")
    private Long optcId;
    
    @Size(min =1, max = 10)
    @Column(name = "TYPE_CODE")
    private String code;

    @Size(min =1, max = 10)
    @Column(name = "DESCRIPTION")
    private String description; 
    
    @OneToMany(mappedBy = "optionPackTypeCode")    
    private List<OptionPackHeader> optionPackHeaders;     

    public OptionPackTypeCode() {}

    public Long getOptcId() {
		return optcId;
	}

	public void setOptcId(Long optcId) {
		this.optcId = optcId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<OptionPackHeader> getOptionPackHeaders() {
		return optionPackHeaders;
	}

	public void setOptionPackHeaders(List<OptionPackHeader> optionPackHeaders) {
		this.optionPackHeaders = optionPackHeaders;
	}

	@Override
    public String toString() {
        return "com.mikealbert.data.entity.OptionPackTypeCode[ optcId=" + this.getOptcId() + " ]";
    }
	
}