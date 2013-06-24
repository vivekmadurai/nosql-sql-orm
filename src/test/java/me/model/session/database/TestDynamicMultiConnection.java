package me.model.session.database;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * <b>about</b>
 *
 * @author graha
 * @created 6/4/13 4:40 PM
 */
//TODO: Currently ignored full test to avoid database connectivity at Server-side

public class TestDynamicMultiConnection {
	DBConnection dbOne, dbTwo;

	@Before
	public void dbOneConnection(){
		dbOne = new DBConnection(DBPropertyFactory.H2DB);
		dbOne.setDatabase("jdbc:h2:~/db1;MVCC=TRUE", "sa", "");
		try {
			dbOne.addXMLDocument(Utility.getDefaultDocument("item"));
			dbOne.addXMLDocument(Utility.getDefaultDocument("stock"));
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		dbOne.build();
		this.dataInsert(dbOne, "item");
		this.dataInsert(dbOne, "stock");
	}

	@Before
	public void dbTwoConnection(){
		dbTwo = new DBConnection(DBPropertyFactory.H2DB);
		dbTwo.setDatabase("jdbc:h2:~/db2;MVCC=TRUE", "sa", "");
		dbTwo.build();
	}

	@Test
	public void testDBOneConnection(){
		assertNotNull(dbOne.getSessionFactory().openSession());
	}

	@Test
	public void testDBTwoConnection(){
		assertNotNull(dbTwo.getSessionFactory().openSession());
	}

	@Test
	public void testDBTwoTableCreateAndInsert() throws Exception {
		Document document = Utility.getDefaultDocument("nodex");
		//Create a table at runtime
		//assertTrue(Utility.dbTableCreate(dbTwo, document));
		//Refresh Already Created Connection
		dbTwo.addXMLDocumentAndRebuild(document);
		//Insert the data
		this.dataInsert(dbTwo, "nodex");
	}

	@Test
	public void testDBTwoTableAlter() throws Exception {
		Document document = Utility.getAlterDocument("nodex");
		try {
			dbTwo.addXMLDocumentAndRebuild(document);
			List list = dbTwo.getSessionFactory().openSession().createQuery("from nodex").list();
			if (list.size() > 0) { // If record been inserted
				HashMap hash = (HashMap) list.get(0);
				assertTrue((hash.keySet().size() - 1) == 3); // #Column + 1 = Size of Content
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
		}
	}

	public void dataInsert(DBConnection database, String table){
		Session session = database.getSessionFactory().openSession();
		assertNotNull(session);
		session.getTransaction().begin();
		session.persist(table, Utility.getDefaultTuple(Long.toString(System.nanoTime()), "Graha"));
		session.persist(table, Utility.getDefaultTuple(Long.toString(System.nanoTime()), "Gautham"));
		session.getTransaction().commit();
	}


}
