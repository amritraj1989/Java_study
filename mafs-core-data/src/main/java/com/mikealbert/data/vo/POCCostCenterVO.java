package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.List;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.entity.ClientContact;
import com.mikealbert.util.MALUtilities;

public class POCCostCenterVO implements Serializable {
	private static final long serialVersionUID = -2400721099734693703L;
	
	private Long clientPointAccountId;
	private String code;
	private String description;
	private Long level;
	private String parentCode;
	private List<ClientContact> clientContacts;
	
	public POCCostCenterVO(){}

	public Long getClientPointAccountId() {
		return clientPointAccountId;
	}

	public void setClientPointAccountId(Long clientPointAccountId) {
		this.clientPointAccountId = clientPointAccountId;
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

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public List<ClientContact> getClientContacts() {
		return clientContacts;
	}

	public void setClientContacts(List<ClientContact> clientContacts) {
		this.clientContacts = clientContacts;
	}
	
	public boolean isDriverAssigned(){
		boolean isDriverAssigned = false;
		
		if(!MALUtilities.isEmpty(this.clientContacts)){
			for(ClientContact cc : this.clientContacts){
				if(!MALUtilities.isEmpty(cc.getDrvInd()) && cc.getDrvInd().equals(MalConstants.FLAG_Y)){
					isDriverAssigned = true;
				}
			}			
		}

		return isDriverAssigned;
	}

}
