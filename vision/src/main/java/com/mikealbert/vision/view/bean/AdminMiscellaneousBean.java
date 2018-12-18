package com.mikealbert.vision.view.bean;

import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.UserService;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class AdminMiscellaneousBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952043784558L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	@Resource
	UserService userService;
	@Resource
	LookupCacheService lookupCacheService;

	private int DEFAULT_DATATABLE_HEIGHT = 175;
	private String selectedLevel;

	/**
	 * Initializes the bean
	 */
	@PostConstruct
	public void init() {
		// set the height and width of the datatables based upon the screen
		// resolution
		initializeDataTable(600, 280, new int[] { 50 }).setHeight(DEFAULT_DATATABLE_HEIGHT);
		super.openPage();
		selectedLevel = Logger.getRootLogger().getLevel().toString();
		logger.info("System Logger Info");
		logger.debug("System Logger Debug");
		logger.warning("System Logger Warning");
	}

	/**
	 * Handles the refresh of cache
	 * 
	 * @param There
	 *            are no parameters
	 * @return Nothing is returned
	 */
	public String refreshCache() {
		try {
			lookupCacheService.refreshCache();
			super.addSuccessMessage("process.success", "Refresh cache");
		} catch (Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		return null;
	}

	/**
	 * Handles the Logging Levels
	 * 
	 * @param There
	 *            are no parameters
	 * @return Nothing is returned
	 */
	@SuppressWarnings("rawtypes")
	public String updateLogLevel() {
		try{
			Logger root = Logger.getRootLogger();
			Enumeration allLoggers = root.getLoggerRepository().getCurrentCategories();
			// set logging level of root and all logging instances in the system
			if ("FATAL".equalsIgnoreCase(getSelectedLevel())) {
				root.setLevel(Level.FATAL);
				while (allLoggers.hasMoreElements()) {
					Category tmpLogger = (Category) allLoggers.nextElement();
					tmpLogger.setLevel(Level.FATAL);
				}
			} else if ("ERROR".equalsIgnoreCase(getSelectedLevel())) {
				root.setLevel(Level.ERROR);
				while (allLoggers.hasMoreElements()) {
					Category tmpLogger = (Category) allLoggers.nextElement();
					tmpLogger.setLevel(Level.ERROR);
				}

			} else if ("INFO".equalsIgnoreCase(getSelectedLevel())) {
				root.setLevel(Level.INFO);
				while (allLoggers.hasMoreElements()) {
					Category tmpLogger = (Category) allLoggers.nextElement();
					tmpLogger.setLevel(Level.INFO);
				}

			} else if ("WARN".equalsIgnoreCase(getSelectedLevel())) {
				root.setLevel(Level.WARN);
				while (allLoggers.hasMoreElements()) {
					Category tmpLogger = (Category) allLoggers.nextElement();
					tmpLogger.setLevel(Level.WARN);
				}

			} else if ("TRACE".equalsIgnoreCase(getSelectedLevel())) {
				root.setLevel(Level.TRACE);
				while (allLoggers.hasMoreElements()) {
					Category tmpLogger = (Category) allLoggers.nextElement();
					tmpLogger.setLevel(Level.TRACE);
				}

			} else if ("DEBUG".equalsIgnoreCase(getSelectedLevel())) {
				root.setLevel(Level.DEBUG);
				while (allLoggers.hasMoreElements()) {
					Category tmpLogger = (Category) allLoggers.nextElement();
					tmpLogger.setLevel(Level.DEBUG);
				}

			} else if ("OFF".equalsIgnoreCase(getSelectedLevel())) {
				root.setLevel(Level.OFF);
				while (allLoggers.hasMoreElements()) {
					Category tmpLogger = (Category) allLoggers.nextElement();
					tmpLogger.setLevel(Level.OFF);
				}

			}
		}catch (Exception ex) {
			logger.error(ex);
			handleException("generic.error.occured.while", new String[] { "changing log level." }, ex,
					null);
		}
		

		// This is another way to do change log level at runtime.
		// It will not do any change in actual log4j file unless store is
		// called.
		/*
		 * try{ Properties props = new Properties(); props.load(new
		 * InputStreamReader(Thread.currentThread().
		 * getContextClassLoader().getResourceAsStream("log4j.properties"))); if
		 * (getSelectedLevel().equals("INFO")) {
		 * props.setProperty("log4j.appender.applogfile.threshold", "INFO"); }
		 * else { props.setProperty("log4j.appender.applogfile.threshold",
		 * "WARN"); } PropertyConfigurator.configure(props); }catch(Exception
		 * ex){ ex.printStackTrace(); }
		 */
		return null;
	}

	/**
	 * Handles page cancel button click event
	 * 
	 * @return The calling view
	 */
	public String cancel() {
		return super.cancelPage();
	}

	/**
	 * Navigation code
	 */
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Miscellaneous Administration");
		thisPage.setPageUrl("adminMiscellaneous");

		if (thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_WORK_CLASS) != null) {

		}
	}

	// new navigation code
	protected void restoreOldPage() {
	}

	public String getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(String selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

}
