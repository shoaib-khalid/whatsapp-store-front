package com.kalsym.whatsapp.service.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.kalsym.whatsapp.service.provider.messengerCloud.WhatsappReq;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author taufik
 */

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class WhatsappMessage {
    private List<String> recipientIds;
    private String title;
    private String subTitle;
    private String url;
    private String urlType;
    private String menuItems;
    private WhatsappReq whatsappReq;
    private String refId;
    private String referenceId;
    private Boolean guest;
    private String orderId;
    private String merchantToken;
    private String text;
    private WhatsappTemplate template;

}