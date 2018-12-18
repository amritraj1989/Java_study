package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;


public  class VehicleTimelineEventVO implements Serializable, Comparable<VehicleTimelineEventVO> {
		
		private static final long serialVersionUID = 1L;

		private String code;
		private String description;
		private Date date;
		

		public VehicleTimelineEventVO(){}

		public VehicleTimelineEventVO(String code, String description, Date date){
			this.code = code;
			this.description = description;
			this.date = date;
		}


		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		@Override
		public int compareTo(VehicleTimelineEventVO other) {
	    	final Date BASELINE_DATE = new GregorianCalendar(2100, 0, 1).getTime();

	    	Date thisDate = this.getDate() == null ? BASELINE_DATE : this.getDate();
	    	Date otherDate = other.getDate() == null ? BASELINE_DATE : other.getDate();
	    	
	        if (thisDate.after(otherDate)){
	            return 1;
	        }else if (thisDate.before(otherDate)){
	            return -1;
	        }else{
	            return 0;
	        }
 
		}
		
		

}