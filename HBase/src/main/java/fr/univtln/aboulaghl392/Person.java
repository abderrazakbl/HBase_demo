package fr.univtln.aboulaghl392;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class Person {
    @Size(min = 1, max = 20)
    private String name;

    private int age;
    private String personalAddress;
    private String professionalAddress;
    private String personalEmail;
    private String professionalEmail;
    private String company;
    private String personalPhoneNum;
    private String professionalPhoneNum;
}