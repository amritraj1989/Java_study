package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;


/**
 * The persistent class for the SERIALIZED_CONFIG database table.
 * @author sibley
 */
@Entity
@Table(name="SERIALIZED_CONFIG")
public class SerializedConfig implements Serializable {
	private static final long serialVersionUID = 953952305976970583L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SERIALIZED_CONFIG_SEQ")    
    @SequenceGenerator(name="SERIALIZED_CONFIG_SEQ", sequenceName="SERIALIZED_CONFIG_SEQ", allocationSize=1)	
    @NotNull
    @Column(name = "SC_ID")
    private Long id;
    
	@NotNull
    @Column(name = "SC_ACODE")
    private String acode;

	@NotNull
    @Column(name = "SC_XML")
    private String xml;
    
	@NotNull	
    @Column(name = "SC_LEVEL")
    private String level;
    
	
	public SerializedConfig() {}
	
    public SerializedConfig(String acode, String xml, String level) {
		super();
		setAcode(acode);
		setXml(xml);
		setLevel(level);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAcode() {
		return acode;
	}

	public void setAcode(String acode) {
		this.acode = acode;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}


	
}