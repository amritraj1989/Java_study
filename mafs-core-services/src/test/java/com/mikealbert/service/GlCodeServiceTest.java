package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.vo.GlCodeLOVVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.testing.BaseTest;

public class GlCodeServiceTest extends BaseTest {
	@Resource
	private GlCodeService	glCodeService;
	
	@Test
	public void testGetGlCodes(){
		Pageable page = null;
		String code = "01100001000";
		List<GlCodeLOVVO> list;
		try {
			list = glCodeService.getGlCodes(code, 1L, page);
			Assert.assertNotNull(list);
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
