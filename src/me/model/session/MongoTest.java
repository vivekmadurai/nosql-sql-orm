package me.model.session;

import java.net.UnknownHostException;
import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

/**
 * @author VivekMadurai
 *
 */
public class MongoTest {

	public static void main(String[] args) {

		try {

			/**** Connect to MongoDB ****/
			// Since 2.10.0, uses MongoClient
			MongoClient mongo = new MongoClient("localhost", 27017);

			/**** Get database ****/
			// if database doesn't exists, MongoDB will create it for you
			DB db = mongo.getDB("testdb");

			/**** Get collection / table from 'testdb' ****/
			// if collection doesn't exists, MongoDB will create it for you
			DBCollection table = db.getCollection("user");

			/**** Insert ****/
			// create a document to store key and value
			BasicDBObject document = new BasicDBObject();
			document.put("name", "mkyong");
			document.put("age", 133.0);
			document.put("createdDate", new Date());
			//table.save(document);
			System.out.println("######################");
			ObjectId obj = new ObjectId();

			/**** Find and display ****/
			
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("name", "mkyong");

			DBCursor cursor = table.find(searchQuery).limit(1000).skip(5);

			
			while (cursor.hasNext()) {
				DBObject instance = cursor.next();
				System.out.println(instance);
				instance.put("age", 55);
				table.save(instance);
			}
			

			/**** Update ****/
			/*
			// search document where name="mkyong" and update it with new values
			BasicDBObject query = new BasicDBObject();
			query.put("name", "mkyong");

			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("name", "mkyong-updated");

			BasicDBObject updateObj = new BasicDBObject();
			updateObj.put("$set", newDocument);

			table.update(query, updateObj);
			*/

			
			/**** Find and display ****/
			/*
			BasicDBObject searchQuery2 
				= new BasicDBObject().append("name", "mkyong-updated")
									.append("age", 33.0);

			DBCursor cursor2 = table.find(searchQuery2);

			while (cursor2.hasNext()) {
				System.out.println(cursor2.next());
			}
			*/
			
			/**** Find by id ****/
			BasicDBObject searchQuery3
				= new BasicDBObject().append("_id", "test");

			DBCursor cursor3 = table.find(searchQuery3);

			while (cursor3.hasNext()) {
				System.out.println(cursor3.next());
			}

			/**** Done ****/
			System.out.println("Done");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}

	}
	
}
