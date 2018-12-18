package com.mikealbert.vision.common;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.testing.BaseTest;

public class CommonCalculationsTest extends BaseTest {
	
	@Test
	public void calculatePv() {
		try {
			double rate = (0.0699 / 12);
			//double rate = 0.00D;
			double noOfPayments = 24.00D;
			double regularPaymentAmt = 15.00D;
			double futureAmt = 0.00D;
			boolean term = false;
			double actual = CommonCalculations.pv(rate, noOfPayments, regularPaymentAmt, futureAmt, term);
			
			BigDecimal expected = new BigDecimal("-335.0604147045260000");
			MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);  
			expected = expected.round(mc);
			
			
			System.out.println("Actual from new formula  "+actual +"=Expected="+expected);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void calculatePmt() {
		try {
			// PMT(A1/12,A2,A5*-1,A6,0)
			double rate = (0.0699 / 12);
			//double rate = 0.00D;
			double noOfPayments = 24.00D;
			double presentAmt = -35444.85D;
			double futureAmt = 17125.00D;
			boolean term = false;
			double actual = CommonCalculations.pmt(rate,noOfPayments,presentAmt,futureAmt, term);
			
			BigDecimal expected = new BigDecimal("919.897009327029");
			//MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);  
			
			System.out.println("Actual from new formula  "+actual +"=Expected="+expected);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
