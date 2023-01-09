package fr.univtln.aboulaghl392;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ejb.Stateless;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Optional;

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
        addColumnIfPresent(put, "personal", "address", person.getPersonalAddress());
        addColumnIfPresent(put, "personal", "mail", person.getPersonalEmail());
        addColumnIfPresent(put, "personal", "phone", person.getPersonalPhoneNum());
        addColumnIfPresent(put, "professional", "address", person.getProfessionalAddress());
        addColumnIfPresent(put, "professional", "mail", person.getProfessionalEmail());
        addColumnIfPresent(put, "professional", "company", person.getCompany());
        addColumnIfPresent(put, "professional", "phone", person.getProfessionalPhoneNum());
        table.put(put);
    }

    public void createPersonsFromJson(String jsonFilePath) throws Exception {
        // Read the JSON file and parse it into a list of Person objects
        ObjectMapper mapper = new ObjectMapper();
        List<Person> persons = mapper.readValue(new File(jsonFilePath), new TypeReference<List<Person>>() {});

        // Get the table object
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        TableName tableName = TableName.valueOf("my-table");
        BufferedMutator table = connection.getBufferedMutator(tableName);

        // Iterate over the list of persons
        for (Person person : persons) {
            // Create the Put object
            Put put = new Put(Bytes.toBytes(person.getName()));
            put.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("age"), Bytes.toBytes(person.getAge()));
            addColumnIfPresent(put, "personal", "address", person.getPersonalAddress());
            addColumnIfPresent(put, "personal", "mail", person.getPersonalEmail());
            addColumnIfPresent(put, "personal", "phone", person.getPersonalPhoneNum());
            addColumnIfPresent(put, "professional", "address", person.getProfessionalAddress());
            addColumnIfPresent(put, "professional", "mail", person.getProfessionalEmail());
            addColumnIfPresent(put, "professional", "company", person.getCompany());
            addColumnIfPresent(put, "professional", "phone", person.getProfessionalPhoneNum());

            // Add the Put object to the table
            table.mutate(put);
        }

        // Flush the table to send the puts to the server
        table.flush();

        // Close the table
        table.close();
    }
    private void addColumnIfPresent(Put put, String family, String column, String value) {
        Optional.ofNullable(value)
                .ifPresent(v -> put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(v)));
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

    public List<Person> getPersonsByAge(int age) throws Exception {
        // Get the table object
        Configuration conf = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(conf);
        Table table = connection.getTable(TableName.valueOf("my-table"));

        // Create the scan object and set the age filter
        Scan scan = new Scan();
        scan.setFilter(new SingleColumnValueFilter(Bytes.toBytes("personal"), Bytes.toBytes("age"), CompareFilter.CompareOp.EQUAL, Bytes.toBytes(age)));

        // Get the results of the scan
        ResultScanner scanner = table.getScanner(scan);

        // Create a list to store the persons
        List<Person> persons = new ArrayList<>();

        // Iterate over the results
        for (Result result : scanner) {
            // Create a new person object and set its properties
            Person person = new Person();
            person.setName(Bytes.toString(result.getRow()));
            person.setAge(age);
            person.setPersonalAddress(Bytes.toString(result.getValue(Bytes.toBytes("personal"), Bytes.toBytes("address"))));
            person.setProfessionalAddress(Bytes.toString(result.getValue(Bytes.toBytes("professional"), Bytes.toBytes("address"))));
            person.setCompany(Bytes.toString(result.getValue(Bytes.toBytes("professional"), Bytes.toBytes("company"))));

            // Add the person to the list
            persons.add(person);
        }

        // Close the scanner and the table
        scanner.close();
        table.close();

        // Return the list of persons
        return persons;
    }
    public void updatePerson(Person person) throws Exception {
        Put put = new Put(Bytes.toBytes(person.getName()));
        put.addColumn(Bytes.toBytes("personal"), Bytes.toBytes("age"), Bytes.toBytes(person.getAge()));
        addColumnIfPresent(put, "personal", "address", person.getPersonalAddress());
        addColumnIfPresent(put, "personal", "mail", person.getPersonalEmail());
        addColumnIfPresent(put, "personal", "phone", person.getPersonalPhoneNum());
        addColumnIfPresent(put, "professional", "address", person.getProfessionalAddress());
        addColumnIfPresent(put, "professional", "mail", person.getProfessionalEmail());
        addColumnIfPresent(put, "professional", "company", person.getCompany());
        addColumnIfPresent(put, "professional", "phone", person.getProfessionalPhoneNum());
        table.put(put);
    }

    public void deletePerson(String name) throws Exception {
        table.delete(new Delete(Bytes.toBytes(name)));
    }
}
