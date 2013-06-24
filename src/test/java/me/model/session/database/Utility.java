package me.model.session.database;

import me.model.session.database.dom.HibernateDOMBuilder;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Table;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <b>about</b>
 *
 * @author graha
 * @created 6/5/13 2:03 PM
 */
public class Utility {

	@Deprecated
	public static synchronized boolean dbTableCreate(DBConnection db, Document document){
		Configuration c = new Configuration();
		c.setProperties(db.getProperties());
		try {
			c.addDocument(document);
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		DBConnection.updateSchema(c);
		Iterator<Table> i = c.getTableMappings();
		do {
			Table tbl = i.next();
			System.out.println(tbl.getName());
		}  while(i.hasNext());
		return true;
	}

	@Deprecated
	public static synchronized boolean dbFieldCreate(DBConnection db, Document document){
		Configuration c = new Configuration();
		c.setProperties(db.getProperties());
		try {
			c.addDocument(document);
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		DBConnection.updateSchema(c);
		return true;
	}



	public static synchronized Document getDefaultDocument(String table) throws Exception{
		HibernateDOMBuilder hdom = new HibernateDOMBuilder();
		hdom.addTable(table);
		hdom.addAttributeAsKey("Id", "ATTRIBUTE_101", "string");
		hdom.addAttributeAsField("name", "ATTRIBUTE_102", "string");
		return hdom.toDocument();
	}

	public static synchronized Document getAlterDocument(String table) throws Exception{
		HibernateDOMBuilder hdom = new HibernateDOMBuilder();
		hdom.addTable(table);
		hdom.addAttributeAsKey("id", "ATTRIBUTE_101", "string");
		hdom.addAttributeAsField("name", "ATTRIBUTE_102", "string");
		hdom.addAttributeAsField("size", "ATTRIBUTE_103", "long");
		return hdom.toDocument();
	}


	public static synchronized Map<String, Object> getDefaultTuple(String Id, String name){
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("ATTRIBUTE_101", Id);
		hm.put("ATTRIBUTE_102", name);
		return hm;
	}

	public static synchronized void printTables(Configuration configuration) {
		Iterator<Table> i = configuration.getTableMappings();
		if (i != null && i.hasNext()) {
			do {
				Table tbl = i.next();
				System.out.printf(">>>>>>>>>>>>> Tables  %s.%s\n", tbl.getCatalog(), tbl.getName());
			} while (i.hasNext());
		} else {
			System.out.println(">>>>>>>>>>>>> Empty Table <<<<<<<<<<<<<<<");
		}
	}


}
