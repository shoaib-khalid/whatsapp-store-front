package com.kalsym.whatsapp.service.service;

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
public class Payment {
    private String detail;    
    private Boolean isSuccess;   
    private String paymentLink;
    private String hash;
    private Double amount;   
}
