package fr.univtln.aboulaghl392;
/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        PersonSessionBean bean = new PersonSessionBean();
        bean.init();

        Person person = new Person();
        person.setName("John");
        person.setAge(30);
        person.setPersonalAddress("123 Main St");
        person.setProfessionalAddress("456 Market St");
        person.setCompany("Acme Inc");

        bean.createPerson(person);
        Person retrievedPerson = bean.getPerson("John");

        System.out.println(retrievedPerson.getName()); // John
        System.out.println(retrievedPerson.getAge()); // 30
        System.out.println(retrievedPerson.getPersonalAddress()); // 123 Main St
        System.out.println(retrievedPerson.getProfessionalAddress()); // 456 Market St
        System.out.println(retrievedPerson.getCompany()); // Acme Inc

        person.setAge(31);
        person.setPersonalAddress("124 Main St");
        bean.updatePerson(person);

        retrievedPerson = bean.getPerson("John");
        System.out.println(retrievedPerson.getAge()); // 31
        System.out.println(retrievedPerson.getPersonalAddress()); // 124 Main St

        bean.deletePerson("John");
        retrievedPerson = bean.getPerson("John");
        System.out.println(retrievedPerson); // null

        bean.destroy();
    }
}
