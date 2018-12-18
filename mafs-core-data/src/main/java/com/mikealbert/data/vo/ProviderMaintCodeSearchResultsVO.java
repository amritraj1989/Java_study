package com.mikealbert.data.vo;

import java.util.List;

public class ProviderMaintCodeSearchResultsVO {
	private List<ProviderMaintCodeSearchResultLineVO> resultsLines;
	private int resultCount;
	
	public List<ProviderMaintCodeSearchResultLineVO> getResultsLines() {
		return resultsLines;
	}
	public void setResultsLines(
			List<ProviderMaintCodeSearchResultLineVO> resultsLines) {
		this.resultsLines = resultsLines;
	}
	public int getResultCount() {
		return resultCount;
	}
	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
}
