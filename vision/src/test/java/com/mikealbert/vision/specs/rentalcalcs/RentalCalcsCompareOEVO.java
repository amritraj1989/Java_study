package com.mikealbert.vision.specs.rentalcalcs;

import java.math.BigDecimal;

public class RentalCalcsCompareOEVO {

    private BigDecimal step1Rental;
    private BigDecimal step1NBV;
    private BigDecimal step2Rental;
    private BigDecimal step2NBV;
    private BigDecimal step3Rental;
    private BigDecimal step3NBV;
    private BigDecimal step4Rental;
    private BigDecimal step4NBV;
    private BigDecimal step5Rental;
    private BigDecimal step5NBV;
    private BigDecimal irr;

    public String getStep1Rental() {
	if (step1Rental != null) {
	    step1Rental = step1Rental.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step1Rental.toString();
	} else {
	    return "";
	}
    }

    public void setStep1Rental(BigDecimal step1Rental) {
	this.step1Rental = step1Rental;
    }

    public String getStep1NBV() {
	if (step1NBV != null) {
	    step1NBV = step1NBV.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step1NBV.toString();
	} else {
	    return "";
	}
    }

    public void setStep1NBV(BigDecimal step1nbv) {
	step1NBV = step1nbv;
    }

    public String getStep2Rental() {
	if (step2Rental != null) {
	    step2Rental = step2Rental.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step2Rental.toString();
	} else {
	    return "";
	}
    }

    public void setStep2Rental(BigDecimal step2Rental) {
	this.step2Rental = step2Rental;
    }

    public String getStep2NBV() {
	if (step2NBV != null) {
	    step2NBV = step2NBV.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step2NBV.toString();
	} else {
	    return "";
	}
    }

    public void setStep2NBV(BigDecimal step2nbv) {
	step2NBV = step2nbv;
    }

    public String getStep3Rental() {
	if (step3Rental != null) {
	    step3Rental = step3Rental.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step3Rental.toString();
	} else {
	    return "";
	}
    }

    public void setStep3Rental(BigDecimal step3Rental) {
	this.step3Rental = step3Rental;
    }

    public String getStep3NBV() {
	if (step3NBV != null) {
	    step3NBV = step3NBV.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step3NBV.toString();
	} else {
	    return "";
	}
    }

    public void setStep3NBV(BigDecimal step3nbv) {
	step3NBV = step3nbv;
    }

    public String getStep4Rental() {
	if (step4Rental != null) {
	    step4Rental = step4Rental.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step4Rental.toString();
	} else {
	    return "";
	}
    }

    public void setStep4Rental(BigDecimal step4Rental) {
	this.step4Rental = step4Rental;
    }

    public String getStep4NBV() {
	if (step4NBV != null) {
	    step4NBV = step4NBV.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step4NBV.toString();
	} else {
	    return "";
	}
    }

    public void setStep4NBV(BigDecimal step4nbv) {
	step4NBV = step4nbv;
    }

    public String getStep5Rental() {
	if (step5Rental != null) {
	    step5Rental = step5Rental.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step5Rental.toString();
	} else {
	    return "";
	}
    }

    public void setStep5Rental(BigDecimal step5Rental) {
	this.step5Rental = step5Rental;
    }

    public String getStep5NBV() {
	if (step5NBV != null) {
	    step5NBV = step5NBV.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return step5NBV.toString();
	} else {
	    return "";
	}
    }

    public void setStep5NBV(BigDecimal step5nbv) {
	step5NBV = step5nbv;
    }

    public String getIrr() {
	if (irr != null) {
	    irr = irr.setScale(2, BigDecimal.ROUND_HALF_UP);
	    return irr.toString();
	} else {
	    return "";
	}
    }

    public void setIrr(BigDecimal irr) {
	this.irr = irr;
    }

    @Override
    public String toString() {
	return "RentalCalcsCompareVO [step1Rental=" + step1Rental + ", step1NBV=" + step1NBV + ", step2Rental=" + step2Rental
		+ ", step2NBV=" + step2NBV + ", step3Rental=" + step3Rental + ", step3NBV=" + step3NBV + ", step4Rental=" + step4Rental
		+ ", step4NBV=" + step4NBV + ", step5Rental=" + step5Rental + ", step5NBV=" + step5NBV + ", irr=" + irr + "]";
    }

}
