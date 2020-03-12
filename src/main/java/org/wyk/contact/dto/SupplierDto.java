package org.wyk.contact.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.wyk.contact.repository.Supplier;

@Getter
@Setter
@EqualsAndHashCode
public class SupplierDto extends MessageDto {

    private String taxNumber;
    private Integer orderLastTimeDays;

    public SupplierDto(){
        super();
    }

    public SupplierDto(Supplier supplier) {
        this.setId(supplier.getId());
        this.taxNumber = supplier.getTaxNumber();
        this.orderLastTimeDays = supplier.getOrderLastTimeDays();
    }
}
