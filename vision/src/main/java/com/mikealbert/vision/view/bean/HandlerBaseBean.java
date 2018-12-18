package com.mikealbert.vision.view.bean;

import javax.annotation.Resource;

public class HandlerBaseBean extends StatefulBaseBean{

	private static final long serialVersionUID = 1L;
	
	@Resource protected MenuBean menuBean;
	
	@Override	protected void loadNewPage() {}

	@Override	protected void restoreOldPage() {}

}
