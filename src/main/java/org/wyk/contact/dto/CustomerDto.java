package org.wyk.contact.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.wyk.contact.repository.Company;
import org.wyk.contact.repository.Customer;
import org.wyk.contact.repository.Person;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class CustomerDto extends MessageDto {

    @JsonProperty
    private CompanyDto company;

    @JsonProperty
    private  PersonDto person;

    @JsonProperty
    private String companyId;

    @JsonProperty
    private String personId;

    @JsonProperty
    private String customerNumber;

    @JsonProperty
    private Date lastOrderDate;

    public CustomerDto(){
        super();
    }

    public CustomerDto(Customer customer, Person person) {
        this.customerNumber = customer.getCustomerNumber();
        this.lastOrderDate = customer.getLastOrderDate();
        this.setId(customer.getId());
        this.person = new PersonDto(person);
    }

    public CustomerDto(Customer customer, Company company) {
        this.customerNumber = customer.getCustomerNumber();
        this.lastOrderDate = customer.getLastOrderDate();
        this.setId(customer.getId());
        this.company = new CompanyDto(company);
    }

    public CustomerDto(Customer customer) {
        this.customerNumber = customer.getCustomerNumber();
        this.lastOrderDate = customer.getLastOrderDate();
        this.setId(customer.getId());
    }

}
