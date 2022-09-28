package com.kalsym.whatsapp.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class WhatsappTemplate { 
    private String[] parameters;
    private String name;  
    private String[] parametersButton;
    
}