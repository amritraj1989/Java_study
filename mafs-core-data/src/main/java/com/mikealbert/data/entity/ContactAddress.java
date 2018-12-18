package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.util.MALUtilities;

@Entity
@Table(name="CONTACT_ADDRESSES")
public class ContactAddress extends Address implements Serializable
{	
	private static final long serialVersionUID = -7496131983014833223L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CAD_ID_SEQ")    
    @SequenceGenerator(name="CAD_ID_SEQ", sequenceName="CAD_ID_SEQ", allocationSize=1)    
    @Basic(optional = false)
    @NotNull
    @Column(name = "CAD_ID")
    private Long id;
	  
	@ManyToOne
    @JoinColumn(name="CNT_CNT_ID")
    private Contact contact;  
	
	@OneToMany(fetch=FetchType.EAGER)
    @MapKeyJoinColumn(name = "cnc_code")
    @JoinColumn(name = "CAD_CAD_ID")
    private Map<PhoneNumberType, PhoneNumber> phoneNumbers = new HashMap<PhoneNumberType,PhoneNumber>();
    
    @Size(max = 80)
    @Column(name = "ADDRESS_LINE_3")
    private String addressLine3;
    
    @Size(max = 80)
    @Column(name = "ADDRESS_LINE_4")
    private String addressLine4;  

    public void setId(Long id)
    {
        this.id = id;
    }
    
    public Long getId(){
    	return id;
    }    
       
    public Contact getContact()
    {
        return contact;
    }

    public void setContact(Contact contact)
    {
        this.contact = contact;
    }    
    
    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }
    
    public String getAddressLine4() {
        return addressLine4;
    }

    public void setAddressLine4(String addressLine4) {
        this.addressLine4 = addressLine4;
    }
    
    public Map<PhoneNumberType, PhoneNumber> getPhoneNumbers()
    {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Map<PhoneNumberType, PhoneNumber> phoneNumbers)
    {
        this.phoneNumbers = phoneNumbers;
    }

    @Transient
    public String getAddressDisplay() {
		return DisplayFormatHelper.formatAddressForTable(null, getAddressLine1(), 
				getAddressLine2(), getAddressLine3(), getAddressLine4(), getCityDescription(), this.getRegionDescription(), this.getPostcode(), "<br/>");
	}
	
    @Transient
	public String getPhoneWorkDisplay() {
		StringBuilder display = new StringBuilder();
		
		if(!MALUtilities.isEmpty(getWorkNumber())){
			display.append(DisplayFormatHelper.formatPhoneNumberForTable(getWorkNumber().getAreaCode(), getWorkNumber().getNumber(), getWorkNumber().getExtensionNumber(), "<br/>"));
		}
		return display.toString();
	}
	
    @Transient
	public String getPhoneCellDisplay() {
		StringBuilder display = new StringBuilder();

		if(!MALUtilities.isEmpty(getCellNumber())){
			display.append(DisplayFormatHelper.formatPhoneNumberForTable(getCellNumber().getAreaCode(), getCellNumber().getNumber(), null, "<br/>"));
		}		
		
		return display.toString();
	}    
    
    @Transient
    public PhoneNumber getWorkNumber()
    {
    	return getPhoneNumbers().get(new PhoneNumberType("WORK"));
    }
    
    @Transient
    public PhoneNumber getCellNumber()
    {
    	return getPhoneNumbers().get(new PhoneNumberType("CELL"));
    }
}
