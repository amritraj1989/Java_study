package com.mikealbert.data.vo;

import java.util.List;

public class OeConRevAmortizationScheduleVO {
	
	private QuoteOEVO currentOEQuoteVO;
	private List<QuoteOEVO> revisionOEQuoteVOList;		

	public QuoteOEVO getCurrentOEQuoteVO() {
		return currentOEQuoteVO;
	}
	public void setCurrentOEQuoteVO(QuoteOEVO currentOEQuoteVO) {
		this.currentOEQuoteVO = currentOEQuoteVO;
	}
	public List<QuoteOEVO> getRevisionOEQuoteVOList() {
		return revisionOEQuoteVOList;
	}
	public void setRevisionOEQuoteVOList(List<QuoteOEVO> revisionOEQuoteVOList) {
		this.revisionOEQuoteVOList = revisionOEQuoteVOList;
	}
}