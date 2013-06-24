package me.model.session.database.dom;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DOMWriter;

/**
 * <b>about</b>
 *
 * @author graha
 * @created 6/4/13 11:18 PM
 */
public class HibernateDOMBuilder {
	Document document = DocumentHelper.createDocument();
	Element hibernateRoot, hibernateClass;

	public HibernateDOMBuilder(){
		document.addDocType("hibernate-mapping",
				"-//Hibernate/Hibernate Mapping DTD 3.0//EN",
				"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd");
		hibernateRoot = document.addElement("hibernate-mapping");
	}

	public Element addTable(String table){
		return this.addTable(table, table);
	}


	public Element addTable(String table, String entity){
		//Default dynamic-insert="true" dynamic-update="true"
		hibernateClass = hibernateRoot.addElement("class");
		hibernateClass
				.addAttribute("dynamic-insert", "true")
				.addAttribute("dynamic-update", "true")
				.addAttribute("entity-name", entity)
				.addAttribute("table", table);
		return hibernateClass;
	}

	public Element addAttributeAsKey(String column, String name, String type){
		return addAttribute("id", column, name, type);
	}

	public Element addAttributeAsField(String column, String name, String type){
		return addAttribute("property", column, name, type);
	}


	public Element addAttribute(String attribute, String column, String name, String type){
		Element element = hibernateClass.addElement(attribute);
		element
				.addAttribute("column", column)
				.addAttribute("name", name)
				.addAttribute("type", type);
		return element;
	}

	public String toString(){
		return document.asXML();
	}

	public org.w3c.dom.Document toDocument() throws Exception{
		return (new DOMWriter()).write(document);
	}

	public static void main (String argv[]){
		HibernateDOMBuilder hdom = new HibernateDOMBuilder();
		hdom.addTable("Item", "Item");
		hdom.addAttributeAsKey("Id", "ATTRIBUTE_101", "string");
		hdom.addAttributeAsField("name", "ATTRIBUTE_102", "string");
		System.out.println(hdom.toString());
	}
}
