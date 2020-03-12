package org.wyk.contact.repository;

import lombok.Getter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter
@MappedSuperclass
public class AbstractEntity {

    @Id
    private String id;

    public AbstractEntity(){
        id = UUID.randomUUID().toString();
    }
}
