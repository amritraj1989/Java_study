/**
 * OpenEndProcessorInput.java
 * rental_calcs
 * Jun 18, 2013
 * 2:57:24 PM
 */
package com.mikealbert.rental.processors.inputoutput;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.exception.MalBusinessException;

/**
 * @author anand.mohan
 * 
 */
@Component("openEndProcessorInput")
@Scope(value = "prototype")
public class OpenEndProcessorInput extends BaseRentalProcessorInput {

	@Override
	public void bindFinanceParamer(int index, BigDecimal value) throws MalBusinessException {
	}

}
