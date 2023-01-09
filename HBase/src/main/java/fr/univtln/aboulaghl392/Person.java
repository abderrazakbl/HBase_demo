package fr.univtln.aboulaghl392;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class Person {
    @Size(min = 1, max = 20)
    private String name;

    private int age;
    @Nullable
    private String personalAddress;
    @Nullable
    private String professionalAddress;
    @Nullable
    private String personalEmail;
    @Nullable
    private String professionalEmail;
    @Nullable
    private String company;
    @Nullable
    private String personalPhoneNum;
    @Nullable
    private String professionalPhoneNum;
}