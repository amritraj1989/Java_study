package com.mikealbert.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.DriverDAO;
import com.mikealbert.data.dao.WebsiteUserAssociationDAO;
import com.mikealbert.data.dao.WebsiteUserDAO;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.WebsiteUser;
import com.mikealbert.data.entity.WebsiteUserAssociation;
import com.mikealbert.data.vo.WebsiteUserAssociationVO;

@Service("websiteUserService")
public class WebsiteUserServiceImpl implements WebsiteUserService{

	@Resource
	private WebsiteUserDAO websiteUserDAO;
	@Resource
	private DriverDAO driverDAO;
	@Resource
	private WebsiteUserAssociationDAO websiteUserAssociationDAO;

	
	public List<WebsiteUser> getEnabledWebsiteUsersByAccountAndType(ExternalAccountPK externalAccountPK, String websiteUserType) {
		return websiteUserDAO.findEnabledUsersByAccountAndType(externalAccountPK, websiteUserType);
	}
	
	@Transactional
	public WebsiteUser getWebsiteUser(long id) {
		WebsiteUser websiteUser = websiteUserDAO.findById(id).orElse(null);
		Hibernate.initialize(websiteUser.getWebsiteUserAssociationList());
		return websiteUser;
	}

	public List<WebsiteUserAssociationVO> getWebsiteUserAssociationVOList(WebsiteUser websiteUser) {
		List<WebsiteUserAssociationVO> websiteUserAssociationVOList = new ArrayList<WebsiteUserAssociationVO>();
		for(WebsiteUserAssociation wua : websiteUser.getWebsiteUserAssociationList()) {
			websiteUserAssociationVOList.add(getWebsiteUserAssociationVO(wua));
		}
		
		return websiteUserAssociationVOList;
	}

	public WebsiteUserAssociationVO getWebsiteUserAssociationVO(WebsiteUserAssociation websiteUserAssociation) {
		WebsiteUserAssociationVO newAssociationVO = new WebsiteUserAssociationVO();
		if(websiteUserAssociation.getUserType().equalsIgnoreCase("DRIVER")) {
			Driver driver = driverDAO.findById(websiteUserAssociation.getAssociationId()).orElse(null);
			newAssociationVO.setFirstName(driver.getDriverForename());
			newAssociationVO.setLastName(driver.getDriverSurname());
			newAssociationVO.setEmail(driver.getEmail());
			newAssociationVO.setAddress(driver.getGaragedAddress());
			newAssociationVO.setWebsiteUserAssociation(websiteUserAssociation);
		}		

		return newAssociationVO;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveWebsiteUser(WebsiteUser websiteUser) {
		websiteUserDAO.save(websiteUser);
	}

	public WebsiteUserAssociation getWebsiteUserAssociation(Long associationId, String websiteUserType) {
		return websiteUserAssociationDAO.findAssociationByIdAndType(associationId, websiteUserType);
	}
}
