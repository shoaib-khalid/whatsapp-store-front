package com.kalsym.whatsapp.service.provider.messengerCloud;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WhatsappReqNotification implements Serializable {
    private String recipient_type;
    private String messaging_product;
    private String to;
    private Text text;    
}
