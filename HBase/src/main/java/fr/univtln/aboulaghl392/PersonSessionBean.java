package fr.univtln.aboulaghl392;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ejb.Stateless;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.SneakyThrows;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

@Named
@Stateless
public class PersonSessionBean {
    @Inject
    private Person person;

    private Table table;

    @PostConstruct
    public void init() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        Admin admin = connection.getAdmin();
        TableName tableName = TableName.valueOf("my-table");
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
        TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(tableName)
                .addColumnFamily(ColumnFamilyDescriptorBuilder.of("personal"))
                .addColumnFamily(ColumnFamilyDescriptorBuilder.of("professional"))
                .build();
        admin.createTable(tableDescriptor);
        table = connection.getTable(TableName.valueOf("my-table"));
    }

    @SneakyThrows
    @PreDestroy
    public void destroy() {
        table.close();
    }

    public void createPerson(Person person) throws Exception {
        Put put = new Put(Bytes.toBytes(person.getName()));
        put.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("age"), Bytes.toBytes(person.getAge()));
        put.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("address"), Bytes.toBytes(person.getPersonalAddress()));
        put.addColumn(Bytes.toBytes("professional"), Bytes.toBytes("address"), Bytes.toBytes(person.getProfessionalAddress()));
        put.addColumn(Bytes.toBytes("professional"), Bytes.toBytes("company"), Bytes.toBytes(person.getCompany()));
        table.put(put);
    }

    public Person getPerson(String name) throws Exception {
        // Get the table object
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TableName.valueOf("my-table"));

        // Get the person row from the table
        Get get = new Get(Bytes.toBytes(name));
        Result result = table.get(get);

        // Check if the row exists
        if (result.isEmpty()) {
            // Return null if the row does not exist
            return null;
        }

        // Create a new person object and set its properties
        Person person = new Person();
        person.setName(name);
        person.setAge(Bytes.toInt(result.getValue(Bytes.toBytes("personal"), Bytes.toBytes("age"))));
        person.setPersonalAddress(Bytes.toString(result.getValue(Bytes.toBytes("personal"), Bytes.toBytes("address"))));
        person.setProfessionalAddress(Bytes.toString(result.getValue(Bytes.toBytes("professional"), Bytes.toBytes("address"))));
        person.setCompany(Bytes.toString(result.getValue(Bytes.toBytes("professional"), Bytes.toBytes("company"))));

        // Close the table
        table.close();

        // Return the person object
        return person;
    }

    public void updatePerson(Person person) throws Exception {
        Put put = new Put(Bytes.toBytes(person.getName()));
        put.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("age"), Bytes.toBytes(person.getAge()));
        put.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("address"), Bytes.toBytes(person.getPersonalAddress()));
        put.addColumn(Bytes.toBytes("professional"), Bytes.toBytes("address"), Bytes.toBytes(person.getProfessionalAddress()));
        put.addColumn(Bytes.toBytes("professional"), Bytes.toBytes("company"), Bytes.toBytes(person.getCompany()));
        table.put(put);
    }

    public void deletePerson(String name) throws Exception {
        table.delete(new Delete(Bytes.toBytes(name)));
    }
}
