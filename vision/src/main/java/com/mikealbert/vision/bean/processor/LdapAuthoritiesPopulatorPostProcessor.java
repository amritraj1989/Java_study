package com.mikealbert.vision.bean.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

/**
 * Primarily implemented this bean post processor solely to set the LDAP authorities 
 * populator's ignorePartialResultException property to true. The namespance config 
 * does not provide a away to set the property w/o explicitly defining a chain of 
 * beans. Doing so, kind of defeats the purpose/simplicity of namespace configuration.
 * 
 * @author sibley
 *
 */
public class LdapAuthoritiesPopulatorPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		if(bean instanceof DefaultLdapAuthoritiesPopulator){
			((DefaultLdapAuthoritiesPopulator)bean).setIgnorePartialResultException(true);
		}
		
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}

}
