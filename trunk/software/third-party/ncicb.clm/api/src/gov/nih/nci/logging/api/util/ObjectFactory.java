package gov.nih.nci.logging.api.util;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.springframework.beans.factory.config.PropertyOverrideConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * ObjectFactory class creates and returns Beans defined in the Spring Configuration file.
 * Namely, HibernateSessionFactory object is returned.
 *
 * 
 * @author Vijay Parmar (Ekagra Software Technologies Limited.)
 */
public class ObjectFactory {
	private static XmlBeanFactory factory;

	private static XmlBeanFactory fact;

	public static final String LogMessageDAO = "LogMessageDAO";

	public static final String HibernateSessionFactory = "HibernateSessionFactory";

	public static final String TestDAO = "TestDAO";

	static {

					factory = new XmlBeanFactory(new ClassPathResource("clm_spring-config.xml"));
		
		/*java.io.InputStream inputStream = FileLoader.getInstance()
				.getFileAsStream("clm.properties");
		Properties properties = new Properties();
		if (inputStream != null) {

			try {
				properties.load(inputStream);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

		try {
			validateProperties(properties);
		} catch (Exception e) {
			
			e.printStackTrace();
		}

		if (properties.size() > 0) {

			PropertyOverrideConfigurer cfg = new PropertyOverrideConfigurer();
			
			cfg.setProperties(properties);
			cfg.postProcessBeanFactory(fact);
			
		}

		factory = fact;
*/
	}

	private ObjectFactory() {
	}

	
	/**
	 * get Object instance from the class name.
	 * 
	 * @param classname
	 * @return
	 */
	public static Object getObject(String classname) {

		return factory.getBean(classname);
	}

}