package me.model.session;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.prettyprint.cassandra.dao.SimpleCassandraDao;
import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.model.IndexedSlicesQuery;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnIndexType;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

/**
 * @author VivekMadurai
 *
 */
public class CassandraTest {
	private static final String keyspaceName = "MyKeyspace";
	private static final String columnFamilyName = "CFN1";
	private Cluster cluster;
	
	private Keyspace getKeyspace() {
		this.cluster = HFactory.getOrCreateCluster("test-cluster","localhost:9160");
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(keyspaceName);

		// If keyspace does not exist, the CFs don't exist either. => create them.
		if (keyspaceDef == null) {
			keyspaceDef = createSchema(cluster);
		}
		
		ConfigurableConsistencyLevel ccl = new ConfigurableConsistencyLevel();
		ccl.setDefaultReadConsistencyLevel(HConsistencyLevel.ONE);
		
		Keyspace ksp = HFactory.createKeyspace(keyspaceName, cluster, ccl);
		return ksp;
	}
	
	private KeyspaceDefinition createSchema(Cluster cluster) {
		ColumnFamilyDefinition cfDef = HFactory.createColumnFamilyDefinition(keyspaceName,
				columnFamilyName,
                ComparatorType.BYTESTYPE);
		

		// replication factor of 1 means that there is only one copy of each row on one node
		int replicationFactor = 1;
		KeyspaceDefinition keyspaceDef = HFactory.createKeyspaceDefinition(keyspaceName,
		              ThriftKsDef.DEF_STRATEGY_CLASS,
		              replicationFactor,
		              Arrays.asList(cfDef));
		//Add the schema to the cluster.  
		//"true" as the second param means that Hector will block until all nodes see the change.
		cluster.addKeyspace(keyspaceDef, true);
		return keyspaceDef;

	}
	
	private void simpleInsert(Keyspace ksp) {
		// TODO Auto-generated method stub
		SimpleCassandraDao dao = new SimpleCassandraDao();
		dao.setKeyspace(ksp);
		dao.setColumnFamilyName(columnFamilyName);
		dao.insert("key002", "First", "Vivek");
		dao.insert("key002", "Last", "Madurai");
		Map<String, String> map = dao.getMulti("First", "Key001", "Key002");
		System.out.println(map);
		System.out.println(dao.get("key002", "First"));
	}
	
	private void templateInsert(Keyspace ksp) {
		ColumnFamilyTemplate<String, String> template =   new ThriftColumnFamilyTemplate<String, String>(ksp, 
				columnFamilyName,
				StringSerializer.get(),
				StringSerializer.get());
		
		ColumnFamilyUpdater<String, String> updater = template.createUpdater("key001");
		
		updater.setString("first", new  String("test"));
		updater.setDouble("age", new Double(32.0));
		
		Map<String, String> jsonObj = new HashMap<>();
		jsonObj.put("Vivek",  "Madurai");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(jsonObj);
			updater.setByteArray("meta", bos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		template.update(updater);
		
		query(template);
		
	}
	
	private void query(ColumnFamilyTemplate template) {
		ColumnFamilyResult<String, String> res = template.queryColumns("key002");
		System.out.println("########################");
		System.out.println(res.getColumnNames());
		System.out.println(StringSerializer.get().fromByteBuffer(res.getColumn("first").getValue()));
		System.out.println(res.getString("First"));
		System.out.println(res.getDouble("age"));
		
		
		System.out.println(ObjectSerializer.get().fromByteBuffer(res.getColumn("meta").getValue()));
		/*
		byte[] bytes = res.getByteArray("meta");
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bis);
			System.out.println((Map)ois.readObject());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		*/
	}
	
	private void rangeQuery(Keyspace ksp) {
		StringSerializer stringSerializer = StringSerializer.get();
		RangeSlicesQuery<String, String, String> rangeSlicesQuery =
				HFactory.createRangeSlicesQuery(ksp, stringSerializer, stringSerializer,
				    stringSerializer);
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.setRange(null, null, false, 1000);
		rangeSlicesQuery.setRowCount(1000);
		//rangeSlicesQuery.setReturnKeysOnly();
		rangeSlicesQuery.setRowCount(5);
		QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
		
		OrderedRows<String, String, String> orderedRows = result.get();
		Row<String,String,String> lastRow = orderedRows.peekLast();
		System.out.println(orderedRows.getCount());
		System.out.println(lastRow.getColumnSlice().getColumns());
		HColumn<String, String> column = lastRow.getColumnSlice().getColumns().get(0);
		System.out.println(column.getName());
		System.out.println(column.getValue());
	}
	
	private void addIndex() {
		BasicColumnFamilyDefinition cfDef = new BasicColumnFamilyDefinition();
	    cfDef.setName(columnFamilyName);
	    cfDef.setKeyspaceName(keyspaceName);
		BasicColumnDefinition columnDefinition = new BasicColumnDefinition();
	    System.out.println("################");
	    System.out.println(StringSerializer.get().toByteBuffer("first"));
	    columnDefinition.setName(StringSerializer.get().toByteBuffer("first"));
	    columnDefinition.setIndexName("first_idx"); 
	    columnDefinition.setIndexType(ColumnIndexType.KEYS);
	    columnDefinition.setValidationClass(ComparatorType.UTF8TYPE.getClassName());
	    cfDef.addColumnDefinition(columnDefinition);
	    
	    System.out.println(cfDef.getColumnMetadata());
	    cluster.updateColumnFamily(cfDef);
	}
	private void indexQuery(Keyspace ksp) {
		StringSerializer stringSerializer = StringSerializer.get();
		ObjectSerializer objSerializer = ObjectSerializer.get();
		
		IndexedSlicesQuery<String, String, String> indexedSlicesQuery = 
	            HFactory.createIndexedSlicesQuery(ksp, stringSerializer, stringSerializer, stringSerializer);
		
		indexedSlicesQuery.setColumnFamily(columnFamilyName);
		indexedSlicesQuery.setRange(null, null, false, 1000);
		indexedSlicesQuery.setRowCount(1000);
		indexedSlicesQuery.addEqualsExpression("first", new String("vivek"));
		
		QueryResult<OrderedRows<String, String, String>> result = indexedSlicesQuery.execute();
		
		System.out.println("#######"+result.get().getCount());
		for(Row<String,String,String> row: result.get().getList()) {
			System.out.println(row.getColumnSlice().getColumns().get(0).getValue());
		}
	}
	
	public static void main(String[] args) {
		
		//ApplicationContext factory =new ClassPathXmlApplicationContext("spring-cassandra.cfg.xml");
		//SimpleCassandraDao dao = (SimpleCassandraDao) factory.getBean("simpleCassandraDao");
		
		CassandraTest cs = new CassandraTest();
		Keyspace ksp = cs.getKeyspace();
		
		//cs.simpleInsert(ksp);
		//cs.templateInsert(ksp);
		//cs.rangeQuery(ksp);
		
		//cs.addIndex();
		cs.indexQuery(ksp);
		
		
		/*
		KeyspaceDefinition keyspaceDef = cs.cluster.describeKeyspace(keyspaceName);
	    List<ColumnFamilyDefinition> cfDefs = keyspaceDef.getCfDefs();
	    for(ColumnFamilyDefinition cfDef: cfDefs) {
	    	System.out.println(cfDef.getName());
	    	System.out.println(cfDef.getColumnMetadata());
	    }
	    
	    */
		
		/*
		BasicColumnFamilyDefinition columnFamilyDefinition = new BasicColumnFamilyDefinition();
	    columnFamilyDefinition.setKeyspaceName(keyspaceName);
	    columnFamilyDefinition.setName("CFN1");   
	    
	    BasicColumnDefinition columnDefinition = new BasicColumnDefinition();
	    columnDefinition.setName(StringSerializer.get().toByteBuffer("birthdate"));
	    columnDefinition.setIndexType(ColumnIndexType.KEYS);
	    columnDefinition.setValidationClass(ComparatorType.LONGTYPE.getClassName());
	    columnFamilyDefinition.addColumnDefinition(columnDefinition);
	    
	    columnDefinition = new BasicColumnDefinition();
	    columnDefinition.setName(StringSerializer.get().toByteBuffer("nonindexed_field"));    
	    columnDefinition.setValidationClass(ComparatorType.LONGTYPE.getClassName());
	    columnFamilyDefinition.addColumnDefinition(columnDefinition);  
	    
	    cs.cluster.addColumnFamily(new ThriftCfDef(columnFamilyDefinition));
	   */ 
	    
	}
}
