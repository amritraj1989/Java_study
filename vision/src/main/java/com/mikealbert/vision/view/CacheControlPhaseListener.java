package com.mikealbert.vision.view;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

public class CacheControlPhaseListener implements PhaseListener {
	 public PhaseId getPhaseId() {
		 return PhaseId.RENDER_RESPONSE;
	 }
	 
	 public void afterPhase(PhaseEvent event) {}
	 public void beforePhase(PhaseEvent event) {
		 FacesContext facesContext = event.getFacesContext();
	 
		 HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
	 
		 // Set to expire far in the past.
		 response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");

		 // Set standard HTTP/1.1 no-cache headers.
		 response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

		 // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		 response.addHeader("Cache-Control", "post-check=0, pre-check=0");

		 // Set standard HTTP/1.0 no-cache header.
		 response.setHeader("Pragma", "no-cache");
	 }
}

