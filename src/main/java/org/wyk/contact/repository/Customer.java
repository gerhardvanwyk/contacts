package org.wyk.contact.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.wyk.contact.dto.CustomerDto;

import javax.persistence.Entity;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Customer extends AbstractEntity {

    private String customerNumber;

    private Date lastOrderDate;

    private String personId;

    private String companyId;

    public Customer(){
        super();
    }

    public Customer(CustomerDto customerDto){
        this.customerNumber = customerDto.getCustomerNumber();
        this.lastOrderDate = customerDto.getLastOrderDate();
        this.companyId = customerDto.getCompanyId();
        this.personId = customerDto.getPersonId();
    }

}
