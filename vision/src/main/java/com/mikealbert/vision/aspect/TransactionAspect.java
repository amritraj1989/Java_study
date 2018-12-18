package com.mikealbert.vision.aspect;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mikealbert.vision.common.AppConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.vo.TransactionSessionParameter;

@Aspect
@Component
public class TransactionAspect {
	@PersistenceContext(unitName = "CorePU") 
	private EntityManager em;
	
	private JoinPoint joinPoint;
	
	final String PARAM_NAME_USER = "USER";
	final String PARAM_NAME_SOURCE = "AUDITSOURCE";
	final String PARAM_VAL_DEFAULT_USER = "UKNOWN";
	final String PARAM_VAL_DEFAULT_VIEW = "UKNOWN";
	final String NO_TRANSACTION_MESSAGE = "Unable to initialize DB session with audit data. A transaction was not properly created for a request coming from {0}";
	
	final String PREPARE_POINTCUT = "execution(public * org.springframework.data.jpa.repository.JpaRepository+.save*(..)) " 
			+ "|| execution(public * org.springframework.data.jpa.repository.JpaRepository+.delete*(..)) "
			+ "|| execution(public * org.springframework.data.jpa.repository.JpaRepository+.merge*(..)) "
			+ "|| execution(public * org.springframework.data.jpa.repository.JpaRepository+.flush(..)) "
			+ "|| execution(* com.mikealbert.data.dao.*DAOImpl.*(..)) ";
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Pointcut(PREPARE_POINTCUT)	
	public void saveMethods(){}	
	
	/**
	 * Prior to performing an insert or update transaction, parameters are set in the 
	 * database to support the audit trigger logging mechanism. 
	 * @param jp JoinPoint 
	 */
	@Before("saveMethods()")
	public void setDBSessionParameters(JoinPoint jp){
		List<TransactionSessionParameter> parameters;
		
		this.joinPoint = jp;
		
		parameters = new ArrayList<TransactionSessionParameter>();
		parameters.add(new TransactionSessionParameter(PARAM_NAME_USER, this.getLoggedInUser() != null ? this.getLoggedInUser().getEmployeeNo() : PARAM_VAL_DEFAULT_USER));
		parameters.add(new TransactionSessionParameter(PARAM_NAME_SOURCE,  AppConstants.APPLICATION_NAME + "-" + this.getViewName()));
					
        this.setDBParameters(parameters);            	
	}
			
	/**
	 * Sends transient name/value pair parameters down to the database
	 * @param TransactionSessionParameter list of name/value pairs 
	 */
	private void setDBParameters(List<TransactionSessionParameter>  parameters){
		try {	
            Query query = em.createNativeQuery("BEGIN SESSION_INFO.SET_SESSION_PARAM(?, ?); END;");
            
            for(TransactionSessionParameter parameter : parameters){           	
            	query.setParameter(1, parameter.getName());
            	query.setParameter(2, parameter.getValue());
            	query.executeUpdate();
            }
            		
		} catch (Exception ex) {
			logger.error(ex.getCause(), MessageFormat.format(NO_TRANSACTION_MESSAGE, AppConstants.APPLICATION_NAME + "-" + this.getViewName()));			
		}
	}
		
	/**
	 * Retrieves the User object from the security context
	 * @return User The object representing the logged in user.
	 */
    private User getLoggedInUser()
    {
    	User user = null;    	   	

    	try {
    		if(SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getName() != null){
    			if(SecurityContextHolder.getContext().getAuthentication().getPrincipal()  != null 
    					&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User){
    				user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    			}
    		}
    	} catch(Exception e) {
			logger.error(e);    		
    	}
    	
    	return user;
    }
    
    
    /**
     * Retrieves the view id of the view that initiated the request 
     * @return viewId The name of the view
     */
    private String getViewName(){
    	StringBuilder viewName = new StringBuilder("");
		FacesContext ctx = FacesContext.getCurrentInstance(); 
		
		try {
			if(ctx != null){
				viewName.append(ctx.getViewRoot().getViewId());
				viewName.replace(0, viewName.length(), viewName.substring(viewName.lastIndexOf("/") + 1));			
				viewName.replace(0, viewName.length(), viewName.substring(0, viewName.lastIndexOf(".")));						
			} else {
				viewName.append(PARAM_VAL_DEFAULT_VIEW);
			}

		} catch(Exception e) {
			logger.error(e);
			return PARAM_VAL_DEFAULT_VIEW;
		}
		
    	return viewName.toString();		
    }
}
