package com.mikealbert.service;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.mikealbert.data.dao.PlateTypeCodeDAO;
import com.mikealbert.data.entity.PlateTypeCode;

@Service("plateTypeCodeService")
public class PlateTypeCodeServiceImpl implements PlateTypeCodeService {
	@Resource
	PlateTypeCodeDAO plateTypeCodeDAO;

	@Override
	public List<PlateTypeCode> getPlateTypeCodeList() {
		return plateTypeCodeDAO.getAllPlateTypeCodes();
	}

}
