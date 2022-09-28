package com.kalsym.whatsapp.service.provider.messengerCloud;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Body {
    private String text;
    
    public Body(String text) {
        this.text = text;
    }
            
}
