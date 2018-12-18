package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.WebsiteUser;
import com.mikealbert.data.entity.WebsiteUserAssociation;
import com.mikealbert.data.vo.WebsiteUserAssociationVO;

public interface WebsiteUserService {
	
	public List<WebsiteUser> getEnabledWebsiteUsersByAccountAndType(ExternalAccountPK externalAccountPK, String websiteUserType);
	public WebsiteUser getWebsiteUser(long id);
	public List<WebsiteUserAssociationVO> getWebsiteUserAssociationVOList(WebsiteUser websiteUser);
	public WebsiteUserAssociationVO getWebsiteUserAssociationVO(WebsiteUserAssociation websiteUserAssociation);
	public void saveWebsiteUser(WebsiteUser websiteUser);
	public WebsiteUserAssociation getWebsiteUserAssociation(Long associationId, String websiteUserType);
}
