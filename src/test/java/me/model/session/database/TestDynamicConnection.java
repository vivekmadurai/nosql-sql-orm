package me.model.session.database;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.google.appengine.repackaged.com.google.common.base.X.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * <b>about</b>
 *
 * @author graha
 * @created 6/4/13 4:40 PM
 */
//TODO: Currently ignored full test to avoid database connectivity at Server-side

public class TestDynamicConnection {
	DBConnection mysql, h2db;

	//@Before //enable this to test with mysql
	public void mySQLConnection(){
		mysql = new DBConnection(DBPropertyFactory.MYSQL);
		mysql.setDatabase("jdbc:mysql://localhost:3306/guestbook","root","admin");
		try {
			mysql.addXMLDocument(Utility.getDefaultDocument("item"));
			mysql.addXMLDocument(Utility.getDefaultDocument("stock"));
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		mysql.build();
	}

	@Before
	public void H2DBConnection(){
		h2db = new DBConnection(DBPropertyFactory.H2DB);
		h2db.setDatabase("jdbc:h2:~/test;MVCC=TRUE", "sa", "");
		try {
			h2db.addXMLDocument(Utility.getDefaultDocument("item"));
			h2db.addXMLDocument(Utility.getDefaultDocument("stock"));
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		h2db.build();
	}

	@Ignore @Test
	public void testMySQLConnection(){
		assertNotNull(mysql.getSessionFactory().openSession());
	}

	@Test
	public void testH2DBConnection(){
		assertNotNull(h2db.getSessionFactory().openSession());
	}


	@Ignore @Test
	public void mySQLTableCreate() throws Exception{
		assertTrue(Utility.dbTableCreate(mysql, Utility.getDefaultDocument("nodex")));
	}

	@Ignore @Test
	public void mySQLFieldCreate() throws Exception{
		assertTrue(Utility.dbFieldCreate(mysql, Utility.getAlterDocument("item")));
	}

	@Test
	public void h2dbTableCreate() throws Exception {
			assertTrue(Utility.dbTableCreate(h2db, Utility.getDefaultDocument("nodex")));
	}

	@Test
	public void h2dbFieldCreate() throws Exception{
			assertTrue(Utility.dbFieldCreate(h2db, Utility.getAlterDocument("item")));
	}

}
