package org.wyk.contact.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyk.contact.dto.CompanyDto;
import org.wyk.contact.exception.CompanyException;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.UUID;


@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Company extends AbstractEntity {

    @Transient
    private final Logger log = LoggerFactory.getLogger(Company.class);

    private String name;
    private String registrationNumber;
    private String customerId;
    private String supplierId;

    public Company(){
        super();
    }

    /**
     * Creates a new Company. If a customer or supplier id is given, we assume it is an
     * exiting (customer or supplier).
     * If the (customer or supplier) object is given we create a new one.
     * @param company
     * @param customerId
     * @param supplierId
     */
    public Company(CompanyDto company, String customerId, String supplierId){
        super();
        //Test if we received valid UUIDs

        if((customerId == null && company.isCustomer()) && (supplierId == null && company.isSupplier()))
            throw new CompanyException("Company must be a supplier or customer (or both)");

        try {
            if (customerId != null) {
                UUID.fromString(customerId);
                this.customerId = customerId;
            }
            if (supplierId != null) {
                UUID.fromString(supplierId);
                this.supplierId = supplierId;
            }
        }catch (IllegalArgumentException e){
            log.error("Not a valid UUID", e);
            throw  new CompanyException("Must supply valid UUID for customer or supplier fields");
        }
        name = company.getName();
        registrationNumber = company.getRegistrationNumber();
    }
}
