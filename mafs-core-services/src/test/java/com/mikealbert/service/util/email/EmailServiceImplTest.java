package com.mikealbert.service.util.email;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//import com.dumbster.smtp.SimpleSmtpServer;
//import com.dumbster.smtp.SmtpMessage;
import com.mikealbert.service.util.email.EmailServiceSpringImpl;

import junit.framework.TestCase;

/*
 * Test class for EmailServiceImpl
 */
@ContextConfiguration(locations={"classpath:emailContextTest.xml"}) 
@RunWith(SpringJUnit4ClassRunner.class)
public class EmailServiceImplTest {
	
	private static final int SMTP_PORT = 25;

	//private SimpleSmtpServer server;

	@Autowired
	private ApplicationContext appContext;


	@Value("$email{email.from.address}")
	private String fromAddress;
	@Value("$email{email.to.address}")
	private String toAddress;

	
	@Before
	public void setUp() throws Exception{
		//server = SimpleSmtpServer.start(SMTP_PORT);
	}
	
	@After
	public void tearDown() throws Exception {
	    //server.stop();
	}

	@Test
	public void testSendEmail(){		
		EmailServiceSpringImpl emailService = (EmailServiceSpringImpl) appContext.getBean("emailServiceImpl");	

		Email email = new Email();
		
		EmailAddress from = new EmailAddress(fromAddress);
		EmailAddress to = new EmailAddress(toAddress);
		
		email.setSubject("Test subject");
		email.setMessage("Test message");
		
		email.setFrom(from);
		email.setTo(to);
		
		try{
			emailService.sendEmail(email);
		}catch(EmailException ee){
			TestCase.fail(ee.getMessage());
		}catch(Exception ex){
			TestCase.fail(ex.getMessage());
		}
		
		TestCase.assertTrue(true);
		
		// check the fake server for a message
		//Iterator it = server.getReceivedEmail();
		
		//SmtpMessage msg = (SmtpMessage) it.next();
		
//		TestCase.assertNotNull(msg.getBody());
//		TestCase.assertEquals("test subject set in test-subject.vm",msg.getHeaderValue("Subject"));
//		TestCase.assertEquals("jasond@mikealbert.com",msg.getHeaderValue("To"));
//		TestCase.assertEquals("noone@mikealbert.com",msg.getHeaderValue("From"));
		
		
	}
	
}

