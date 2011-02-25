package gov.nih.nci.logging.api.logger.util;

import gov.nih.nci.logging.api.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * ApplicationPropertyHelper class to parse the ApplicationPropertyFile and return its properties.
 * 
 * Refer the ObjectStateLogger.dtd DTD in api/resources folder for reference.
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 *
 */
public class ApplicationPropertiesHelper implements ApplicationConstants {

	private static Document dom;
	private static Element documentRootElement;
	
	private List documentObjectList=null;
	private Map identifierAttributes=null;

	
	ApplicationPropertiesHelper(String propertyFile){
	
		
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			// Using factory get an instance of document builder
			PropertyFileLoader propertyFileLoader = new PropertyFileLoader();
			InputStream propertyFileStream = propertyFileLoader.loadPropertyFile(propertyFile); 
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(propertyFileStream);

			// get the root elememt
			documentRootElement = dom.getDocumentElement();

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	ApplicationPropertiesHelper() {

		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			
			PropertyFileLoader propertyFileLoader = new PropertyFileLoader();
			InputStream propertyFileStream = propertyFileLoader.loadPropertyFile(ApplicationPropertyFile);

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(propertyFileStream);

			// get the root elememt
			documentRootElement = dom.getDocumentElement();

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	/**
	 *  Parse LoggerConfigFile from ApplicationPropertyFile
	 * @return LoggerConfigFile
	 */
	public String getLoggerConfigFile() {
		if (documentRootElement == null) return null;

		NodeList nl = documentRootElement
				.getElementsByTagName(LOGGER_CONFIGURATION_FILE);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				if (LOGGER_CONFIGURATION_FILE.equalsIgnoreCase(el.getTagName())) {
					return el.getFirstChild().getNodeValue();
				}

			}
		}
		return null;
	}
	/**
	 *   Parse LoggerName from ApplicationPropertyFile
	 * @return  LoggerName
	 */
	public String getLoggerName() {
		if (documentRootElement == null) return null;

		NodeList nl = documentRootElement
				.getElementsByTagName(OBJECT_STATE_LOGGER_NAME);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				if (OBJECT_STATE_LOGGER_NAME.equalsIgnoreCase(el.getTagName())) {
					return el.getFirstChild().getNodeValue();
				}
			}
		}
		return null;
	}

	/**
	 *  Parse LoggingEnabledfrom ApplicationPropertyFile
	 * @return boolean LoggingEnabled
	 */
	public boolean getLoggingEnabled() {
		if (documentRootElement == null) return false;

		NodeList nl = documentRootElement.getElementsByTagName(LOGGING_ENABLED);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				if (LOGGING_ENABLED.equalsIgnoreCase(el.getTagName())) {
					String value = el.getAttribute("value");
					if (LOGGING_ENABLED_TRUE.equalsIgnoreCase(value)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Parse LogLevel from ApplicationPropertyFile
	 * @return LogLevel
	 */
	public String getLogLevel() {
		if (documentRootElement == null) return null;
		
		NodeList nl = documentRootElement.getElementsByTagName(LOG_LEVEL);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				if (LOG_LEVEL.equalsIgnoreCase(el.getTagName())) {
					return el.getFirstChild().getNodeValue();
				}
			}
		}
		return null;
	}

	/**
	 * Parse MessageLoggingFormat from ApplicationPropertyFile
	 * @return MessageLoggingFormat
	 */
	public String getMessageLoggingFormat() {
		if (documentRootElement == null) return null;

		NodeList nl = documentRootElement
				.getElementsByTagName(OBJECT_STATE_LOGGER_MESSAGE_FORMAT);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				if (OBJECT_STATE_LOGGER_MESSAGE_FORMAT.equalsIgnoreCase(el.getTagName())) {
					String value = el.getAttribute("type");
					if (OBJECT_STATE_LOGGER_MESSAGE_FORMAT_STRING.equalsIgnoreCase(value)) {
						return OBJECT_STATE_LOGGER_MESSAGE_FORMAT_STRING;
					}
					if (OBJECT_STATE_LOGGER_MESSAGE_FORMAT_XML.equalsIgnoreCase(value)) {
						return OBJECT_STATE_LOGGER_MESSAGE_FORMAT_XML;
					}
				}
			}
		}
		return null;
	}
	
	/** 
	 * 
	 *  Parse DomainObjectsList from ApplicationPropertyFile
	 * @return DomainObjectsList. List of Domain Object Names
	 */
	public List getDomainObjectsList() {
		if(this.documentObjectList==null){
			parseDomainObjects();
		}
		return documentObjectList;
	}
	/**
	 * Parse IdentifierAttribute from ApplicationPropertyFile
	 * @return Identifier Attributes Map <String,String> =<objectName,identifierAttribute>
	 */
	public Map getIdentifierAttributes() {
		if(this.identifierAttributes==null){
			parseDomainObjects();
		}
		return identifierAttributes;

	}
	

	/**
	 * 
	 * private method to parse DomainObjectsList node for DomainObject ( ObjectName, IdentifierAttribute elements)
	 */
	private void parseDomainObjects() {
		if (documentRootElement == null) return ;
		
		documentObjectList = new ArrayList();
		identifierAttributes = new HashMap();
		
		Element domainObjectsList = null;
		NodeList nl = documentRootElement.getElementsByTagName(DOMAIN_OBJECT_LIST);
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				if (DOMAIN_OBJECT_LIST.equalsIgnoreCase(el.getTagName())) {
					domainObjectsList = el;
				}
			}
		}
		if(domainObjectsList!=null){
			NodeList nll = domainObjectsList.getElementsByTagName(DOMAIN_OBJECT);
			if (nll != null && nll.getLength() > 0) {
				for (int i = 0; i < nll.getLength(); i++) {
					Element el = (Element) nll.item(i);
					if (DOMAIN_OBJECT.equalsIgnoreCase(el.getTagName())) {
						String objectName = getObjectName(el);
						String identifierAttribute = getIdentifierAttributeValue(el);
						if(!StringUtils.isBlank(objectName)){
							documentObjectList.add(objectName);
							if(!StringUtils.isBlank(identifierAttribute)){
								identifierAttributes.put(objectName,identifierAttribute);
							}
						}
					}
				}
			}
		}
		return;
	}


	/**
	 * Parse IdentifierAttribute value from element
	 * @param domainObjectElement
	 * @return
	 */
	private String getIdentifierAttributeValue(Element domainObjectElement) {
		
		NodeList nodelist = domainObjectElement.getElementsByTagName(IDENTIFIER_ATTRIBUTE);
		if (nodelist != null && nodelist.getLength() > 0) {
			for (int i = 0; i < nodelist.getLength(); i++) {
				Element el = (Element) nodelist.item(i);
				if (IDENTIFIER_ATTRIBUTE.equalsIgnoreCase(el.getTagName())) {
					return el.getFirstChild().getNodeValue();
				}
			}
		}
		return null;
	}

	/**
	 * Parse ObjectName value from element
	 * @param domainObjectElement
	 * @return
	 */
	private String getObjectName(Element domainObjectElement) {
		NodeList nodelist = domainObjectElement.getElementsByTagName(OBJECT_NAME);
		if (nodelist != null && nodelist.getLength() > 0) {
			for (int i = 0; i < nodelist.getLength(); i++) {
				Element el = (Element) nodelist.item(i);
				if (OBJECT_NAME.equalsIgnoreCase(el.getTagName())) {
					return el.getFirstChild().getNodeValue();
				}
			}
		}
		return null;
	}





}