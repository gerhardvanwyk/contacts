package org.wyk.contact.repository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.wyk.contact.dto.SupplierDto;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Supplier extends AbstractEntity {

    private String taxNumber;
    private Integer orderLastTimeDays;

    private String personId;
    private String companyId;

    public Supplier(){
        super();
    }

    public Supplier(SupplierDto supplierDto){
        this.taxNumber = supplierDto.getTaxNumber();
        this.orderLastTimeDays = supplierDto.getOrderLastTimeDays();
    }
}
