package org.wyk.contact.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Person extends AbstractEntity {

    private String firstName;
    private String lastName;

    private String customerId;
    private String supplierId;

    public Person(){
        super();
    }
}
