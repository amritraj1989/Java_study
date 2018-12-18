package com.mikealbert.data.enumeration;

public enum CorporateEntity {
	MAL ("MCB", 1,"Mike Albert Leasing - MAL"),
	LTD ("LTD", 2, "Mike Albert Limited - LTD");
	
	private final String displayName;
	private final String corpPrefix;   // USER_PREFIX in CONTROL_CONTEXT
	private final long corpId; // c_Id in CONTROL_CONTEXT
	CorporateEntity(String corpPrefix, long corpId, String displayName) {
	    this.corpPrefix = corpPrefix;
	    this.corpId = corpId;
	    this.displayName = displayName;
	}
	
	public String getCorpPrefix() {
		return corpPrefix;
	}

	public long getCorpId() {
		return corpId;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public static CorporateEntity fromCorpId(Long corpId) {
    if (corpId != null) {
	      for (CorporateEntity a : CorporateEntity.values()) {
	        if (a.corpId == corpId.longValue() ) {
	          return a;
	        }
	      }
	    }
	    return null;
	}
}

