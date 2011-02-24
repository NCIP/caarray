package gov.nih.nci.logging.api.logger.util;

/**
 * <!-- LICENSE_TEXT_START -->
 * 
 * <!-- LICENSE_TEXT_END -->
 */

import gov.nih.nci.logging.api.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Ekagra Software Technologes Limited ('Ekagra')
 * 
 * This class is a singleton to get Object State Logging Configuration
 * properties.
 * 
 */

public class ApplicationProperties implements ApplicationConstants {
	private static Logger logger = Logger
			.getLogger(ApplicationProperties.class);
	private static ApplicationProperties myInstance;
	private static List domainObjectList = new ArrayList();
	private static Map identifierAttributes = new HashMap();
	private static String loggerName = null;
	private static boolean isLoggingEnabled = false;
	private static String messageLoggingFormat = null;
	private static String loggingConfigFile = null;
	private static String logLevel = null;

	ApplicationProperties() {
		try {
			ApplicationPropertiesHelper aph = new ApplicationPropertiesHelper();
			// Get Logging Config File Name.
			loggingConfigFile = aph.getLoggerConfigFile();
			// Get Logger Name/
			loggerName = aph.getLoggerName();
			// Get Log Level
			logLevel = aph.getLogLevel();
			// Get Message Type/
			messageLoggingFormat = aph.getMessageLoggingFormat();
			// Get Logging Enabled.
			isLoggingEnabled = aph.getLoggingEnabled();
			// Get Domain Objects List.
			domainObjectList = aph.getDomainObjectsList();
			// Get HashMap of Identifier Attributes <ObjectName,Attribute>
			identifierAttributes = aph.getIdentifierAttributes();

		} catch (Exception ex) {
			ex.printStackTrace();
			if (logger.isDebugEnabled())
				logger
						.debug("Error reading the Config File "
								+ ex.getMessage());
		}

	}

	ApplicationProperties(String propertyFile) {
		try {
			ApplicationPropertiesHelper aph;
			if (StringUtils.isBlank(propertyFile)) {
				aph = new ApplicationPropertiesHelper();
			} else {
				aph = new ApplicationPropertiesHelper(propertyFile);
			}
			// Get Logging Config File Name.
			loggingConfigFile = aph.getLoggerConfigFile();
			// Get Logger Name/
			loggerName = aph.getLoggerName();
			// Get Log Level
			logLevel = aph.getLogLevel();
			// Get Message Type/
			messageLoggingFormat = aph.getMessageLoggingFormat();
			// Get Logging Enabled.
			isLoggingEnabled = aph.getLoggingEnabled();
			// Get Domain Objects List.
			domainObjectList = aph.getDomainObjectsList();
			// Get HashMap of Identifier Attributes <ObjectName,Attribute>
			identifierAttributes = aph.getIdentifierAttributes();

		} catch (Exception ex) {
			ex.printStackTrace();
			if (logger.isDebugEnabled())
				logger
						.debug("Error reading the Config File "
								+ ex.getMessage());
		}

	}

	/**
	 * Determines of ObjectState Logging is enabled for this particular
	 * domainObject
	 * 
	 * @param obj
	 *            to be determined if audited or not
	 * @return boolean value
	 */
	public boolean isObjectStateLoggingEnabled(Object domainObject) {
		if (domainObjectList.contains(domainObject.getClass().getName())) {
			return true;
		} else
			return false;
	}

	/**
	 * Method checks the ObjectStateLogger Configuration for IdentifierAttribute
	 * for particular Domain Object.
	 * 
	 * @param domainObject
	 *            The domainObject for which identifierAttributes are requested.
	 * @return String If available returns the identifier attribute value from
	 *         the configuration file else returns null.
	 */
	public String getIdentifierAttribute(Object domainObject) {

		if (identifierAttributes.containsKey(domainObject.getClass().getName())) {
			String ia = (String) identifierAttributes.get(domainObject
					.getClass().getName());
			if (!StringUtils.isBlank(ia))
				return ia;
		}
		return null;
	}

	/**
	 * @return -- Returns the logging configuration file
	 */
	public String getConfigFile() {
		return loggingConfigFile;
	}

	/**
	 * @return -- Returns the log level
	 */
	public String getLogLevel() {
		return logLevel;
	}

	/**
	 * @return -- Returns the logging configuration file
	 */
	public String getLoggerName() {
		return loggerName;
	}
	/**
	 * @return returns message logging format
	 */
	public String getMessageLoggingFormat() {
		return messageLoggingFormat;
	}

	/**
	 * @return -- Returns the boolean value indicating if the logging is enabled
	 *         or disabled
	 */
	public boolean isLoggingEnabled() {
		return isLoggingEnabled;
	}

	/**
	 * @return Returns the single instance of this class
	 */
	public static ApplicationProperties getInstance() {

		if (myInstance == null) {
				myInstance = new ApplicationProperties();
		}
		//logger.info("Inside ApplicationProperties " + myInstance);
		return myInstance;

	}
	
	/**
	 * @return Returns the single instance of this class
	 */
	public static ApplicationProperties getInstance(String propertyFile) {

		if (myInstance == null) {
			if (StringUtils.isBlank(propertyFile)) {
				myInstance = new ApplicationProperties();
			} else {
				myInstance = new ApplicationProperties(propertyFile);
			}
		}
		//logger.info("Inside ApplicationProperties " + myInstance);
		return myInstance;

	}

	
	
}
