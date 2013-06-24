package me.model.session.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Properties;

/**
 * <b>about</b>
 * 		This is wrapper  of Hibernate Connection at runtime dynamically.
 *
 *  	Its also Wrapper Class for SchemaUpdate tool of Hibernate ORM 4x.
 *  It will help to update the Schema change on the database. It'll help
 *  to add new table, new column without any constraints.
 *        x It will create separate database session with given property
 *        x It changes will be provided by fragment or full hbm xml file
 *
 *  TODO: Enriched with http://www.tutorialspoint.com/database/hibernate_quick_guide.htm
 *
 * @author graha
 * @created 6/4/13 11:43 AM
 */

public class DBConnection {
	private Configuration configuration = new Configuration();	//Default Configuration
	private Properties properties = new Properties();         	//Default Properties
	private SessionFactory sessionFactory = null;				//SessionFactory of Hibernate

	public DBConnection(String database){
		init(database);
	}

	/**
	 * Constructor with pre-built Hibernate Configuration
	 * @param configuration
	 */
	public DBConnection(Configuration configuration){
  		this.setConfiguration(configuration);
		this.build();
	}

	/**
	 * Build the Basic Database Property to create a connection
	 *
	 * @param database
	 */
	private void init(String database) {
		Properties prop = DBPropertyFactory.getProperty(database);
		if (prop != null) {
			Environment.verifyProperties(prop);
			configuration.setProperties(prop);
			this.setProperties(configuration.getProperties());
		}
	}

	/**
	 * Building database connection with update table mapping.
	 */

	public void build(){
		ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder().applySettings(configuration
				.getProperties());
		sessionFactory = configuration
				.buildSessionFactory(serviceRegistryBuilder.buildServiceRegistry());
	}

	/**
	 *
	 * Attaching database to connect
	 *
	 * @param url
	 * @param username
	 * @param password
	 */
	public void setDatabase(String url, String username, String password){
		this.properties.setProperty("hibernate.connection.url",url);
		this.properties.setProperty("hibernate.connection.username", username);
		this.properties.setProperty("hibernate.connection.password",password);
	}


	/**
	 * adding file name of HBM Schema location
	 *
	 * @param document
	 */
	public void addXMLDocument(Document document){
		configuration.addDocument(document);
	}


	/**
	 * adding file name of HBM Schema location
	 *
	 * @param document
	 */
	public void addXMLDocumentAndRebuild(Document document){
		configuration.addDocument(document);
		build();
	}


	/**
	 * adding file name of HBM Schema location
	 *
	 * @param file
	 */
	public void addHBMFile(String file){
		configuration.addFile(file);
	}

	/**
	 * adding File object of HBM Schema
	 *
	 * @param file
	 */
	public void addHBMFile(File file){
		configuration.addFile(file);
	}

	/**
	 * adding Class name mapping of Schema
	 *
	 * @param schema
	 */
	public void addPOJOClass(Class schema){
		configuration.addClass(schema);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfigurationByFile(String file) {
		configuration.configure(file);
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}


	/**
	 * Class method for updating Schema either create table or attribute
	 *
	 *
	 */
	public synchronized void  updateSchema() {
		SchemaUpdate sup = new SchemaUpdate(configuration, properties);
		sup.execute(true, true);    //it will print SQL DDL for debug purpose
	}


	//TODO: Currently supports only Table/Field Should add full visioning too

	/**
	 * Class method for updating Schema either create table or attribute
	 *
	 * @param configuration
	 */
	@Deprecated
	public static synchronized void  updateSchema(Configuration configuration) {
		updateSchema(configuration, configuration.getProperties());
	}

	/**
	 * Class method for updating Schema either create table or attribute
	 *
	 * @param configuration
	 */
	@Deprecated
	public static synchronized void updateSchema(Configuration configuration, Properties properties) {
		SchemaUpdate sup = new SchemaUpdate(configuration, properties);
		sup.execute(true, true);    //it will print SQL DDL for debug purpose
	}
}
