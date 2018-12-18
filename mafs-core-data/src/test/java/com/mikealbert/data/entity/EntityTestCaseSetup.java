package com.mikealbert.data.entity;

import java.text.MessageFormat;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;

import com.mikealbert.testing.BaseTest;


public abstract class EntityTestCaseSetup extends BaseTest {
	
	protected static String DEFAULT_NULL_MESSAGE  = "{0} is required.";
	protected static String DEFAULT_LENGTH_KEY  =  "{0} must be between {1} and {2} characters.";
	protected static String DEFAULT_EMAIL_MESSAGE = "Invalid Email Address.";
	
	protected static  MessageFormat lengthMessage = new MessageFormat(DEFAULT_LENGTH_KEY);
	protected static  MessageFormat notNullMessage = new MessageFormat(DEFAULT_NULL_MESSAGE);
	protected static  MessageFormat emailMessage = new MessageFormat(DEFAULT_EMAIL_MESSAGE);
	
	protected static Validator validator;

	@BeforeClass
	public static void setUp() {
	    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    validator = factory.getValidator();
	} 

}
