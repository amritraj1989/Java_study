package com.mikealbert.service;

import java.util.List;
import com.mikealbert.data.entity.PlateTypeCode;

/**
 * @see com.mikealbert.data.entity.PlateTypeCode
 * @see com.mikealbert.vision.service.PlateTypeCodeServiceImpl
 * */
public interface PlateTypeCodeService {
	
	public List<PlateTypeCode> getPlateTypeCodeList();
}
