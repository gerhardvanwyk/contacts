package org.wyk.contact.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class MessageDto {

    private String message;
    private String id;

    public MessageDto(){

    }

    public MessageDto(String message) {
        this.message = message;
    }
}
