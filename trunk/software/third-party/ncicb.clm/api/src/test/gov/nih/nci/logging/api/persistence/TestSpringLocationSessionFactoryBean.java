package test.gov.nih.nci.logging.api.persistence;

import gov.nih.nci.logging.api.util.FileLoader;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

public class TestSpringLocationSessionFactoryBean {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		java.io.InputStream in = FileLoader.getInstance().getFileAsStream("commons-logging.properties");
		Resource resource = new InputStreamResource(in);
		System.out.println(resource.exists());
		
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		String mappingResources[] = 
		{"gov/nih/nci/logging/api/domain/LogMessage.hbm.xml","gov/nih/nci/logging/api/domain/ObjectAttribute.hbm.xml"};
		localSessionFactoryBean.setMappingResources(mappingResources);
		BasicDataSource ds= setDataSourceProperties();
		localSessionFactoryBean.setDataSource(ds);
		localSessionFactoryBean.setHibernateProperties(getJDBCProperties());
		
		try {
			localSessionFactoryBean.afterPropertiesSet();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		  SessionFactory sf = (SessionFactory) localSessionFactoryBean.getObject();
		  //SessionFactory sessionFactory = (SessionFactory) ObjectFactory.getObject(ObjectFactory.HibernateSessionFactory);
	}
	private static Properties getJDBCProperties() {
		Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class","com.mysql.jdbc.Driver");
        props.setProperty("hibernate.connection.url","jdbc:mysql://localhost:3306/clm");
        props.setProperty("hibernate.connection.username","root");	
        props.setProperty("hibernate.connection.password","admin");
        props.setProperty("hibernate.dialect","org.hibernate.dialect.MySQLDialect");
        props.setProperty("jdbc.batch_size","30");
        return props;
	}
	
	private static BasicDataSource setDataSourceProperties(){
		BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/clm");
        ds.setUsername("root");	
        ds.setPassword("admin");
        /*props.setProperty("hibernate.dialect","org.hibernate.dialect.MySQLDialect");
        props.setProperty("jdbc.batch_size","30");*/
        return ds;
	}
	
}
