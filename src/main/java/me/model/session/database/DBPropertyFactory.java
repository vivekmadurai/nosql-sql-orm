package me.model.session.database;

import java.util.HashMap;
import java.util.Properties;

/**
 * <b>about</b>
 *
 * @author graha
 * @created 6/4/13 3:07 PM
 */
public class DBPropertyFactory {
	private DBPropertyFactory(){}

	public static Properties getProperty(String database){
		//TODO: generalized with resource type
		Properties prop = getDefaultProperties();
		switch (database){
			case MYSQL:
				prop.putAll(getMySQLProperties());
				break;
			case H2DB:
				prop.putAll(getH2DBProperties());
				break;
		}

		prop.putAll(new HashMap<String, String>());
		return prop;
	}


	private static Properties getDefaultProperties(){
		Properties prop = new Properties();
		prop.setProperty("hibernate.default_entity_mode","dynamic-map");
		prop.setProperty("hibernate.hbm2ddl.auto","update");
		prop.setProperty("hibernate.show_sql","true");
		prop.setProperty("hibernate.jdbc.batch_size","30");
		return prop;
	}

	private static HashMap<String, String> getMySQLProperties(){
		HashMap<String, String> prop = new HashMap<String, String>();
		prop.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		prop.put("hibernate.connection.driver_class","com.mysql.jdbc.Driver");
		return prop;
	}

	private static HashMap<String, String> getH2DBProperties(){
		HashMap<String, String> prop = new HashMap<String, String>();
		prop.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		prop.put("hibernate.connection.driver_class","org.h2.Driver");
		return prop;
	}


	public static final String MYSQL = "MySQL";
	public static final String H2DB = "H2DB";
}
