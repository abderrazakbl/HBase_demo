package fr.univtln.aboulaghl392;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeDataGenerator {
    public static void main(String[] args) {
        Faker faker = new Faker();
        Random random = new Random();

        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            Person person = new Person();
            person.setName(faker.name().fullName());
            person.setAge(random.nextInt(100));
            person.setPersonalEmail(faker.internet().emailAddress());
            if (random.nextBoolean()) {
                person.setPersonalAddress(faker.address().fullAddress());
            }
            if (random.nextBoolean()) {
                person.setProfessionalAddress(faker.address().fullAddress());
            }
            if (random.nextBoolean()) {
                person.setProfessionalEmail(faker.internet().emailAddress());
            }
            if (random.nextBoolean()) {
                person.setCompany(faker.company().name());
            }
            if (random.nextBoolean()) {
                person.setCompany(faker.company().name());
            }
            if (random.nextBoolean()) {
                person.setProfessionalPhoneNum(faker.phoneNumber().cellPhone());
            }
            if (random.nextBoolean()) {
                person.setPersonalPhoneNum(faker.phoneNumber().cellPhone());
            }
            persons.add(person);
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("/home/bl/Bureau/HBase_demo/HBase/persons.json"), persons);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}