package fr.univtln.aboulaghl392;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        PersonSessionBean bean = new PersonSessionBean();
        bean.init();

        /*ObjectMapper mapper = new ObjectMapper();
        Person[] persons = mapper.readValue(new File("./src/main/resources/persons.json"), Person[].class);
        long startTime=System.nanoTime();
        for (Person person : persons) {
            bean.createPerson(person);
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        System.out.println(elapsedTime / 1000000000 +" second to populate the data base");
         */
        long startTime=System.nanoTime();
        bean.createPersonsFromJson("./src/main/resources/persons.json");
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        System.out.println(elapsedTime / 1000000000 +" second to populate the data base");

        Person retrievedPerson = bean.getPerson("Ms. Myong Paucek");

        System.out.println(retrievedPerson.getName()); // John
        System.out.println(retrievedPerson.getAge()); // 30
        System.out.println(retrievedPerson.getPersonalAddress()); // 123 Main St
        System.out.println(retrievedPerson.getProfessionalAddress()); // 456 Market St
        System.out.println(retrievedPerson.getCompany()); // Acme Inc

        retrievedPerson.setAge(31);
        retrievedPerson.setPersonalAddress("124 Main St");
        bean.updatePerson(retrievedPerson);

        retrievedPerson = bean.getPerson("Ms. Myong Paucek");
        System.out.println(retrievedPerson.getAge()); // 31
        System.out.println(retrievedPerson.getPersonalAddress()); // 124 Main St

        bean.deletePerson("Ms. Myong Paucek");
        retrievedPerson = bean.getPerson("Ms. Myong Paucek");
        System.out.println(retrievedPerson); // null
        List<Person> personslist =new ArrayList<>();
        startTime = System.nanoTime();
        personslist=bean.getPersonsByAge(30);
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        System.out.println(elapsedTime / 1000000000 +" second to retrieve all persons with age equals to 30");

        bean.destroy();
    }
}
