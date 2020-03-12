package org.wyk.contact.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.wyk.contact.repository.Company;
import org.wyk.contact.repository.Customer;
import org.wyk.contact.repository.Supplier;

@Getter
@Setter
@EqualsAndHashCode
/**
 * Company can be a customer or a supplier
 */
public class CompanyDto extends MessageDto {

    @JsonProperty
    private String name;

    @JsonProperty
    private String registrationNumber;

    @JsonProperty
    private CustomerDto customer;

    @JsonProperty
    private SupplierDto supplier;

    public CompanyDto(){
        super();
    }

    public CompanyDto(Company company) {
        this.name = company.getName();
        this.registrationNumber = company.getRegistrationNumber();
        this.setId(company.getId());
        if(company.getCustomerId() != null) {
            this.customer = new CustomerDto();
            this.customer.setId(company.getCustomerId());
        }
        if(company.getSupplierId() != null) {
            this.supplier = new SupplierDto();
            this.supplier.setId(company.getSupplierId());
        }
    }

    public CompanyDto(String error) {
        super(error);
    }

    @JsonIgnore
    public boolean isCustomer(){
        return customer != null;
    }

    @JsonIgnore
    public boolean isSupplier(){
        return supplier != null;
    }

}
