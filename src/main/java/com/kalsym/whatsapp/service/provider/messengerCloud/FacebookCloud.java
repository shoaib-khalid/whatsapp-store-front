package com.kalsym.whatsapp.service.provider.messengerCloud;

import com.kalsym.whatsapp.service.WhatsappServiceApplication;
import com.kalsym.whatsapp.service.model.WhatsappMessage;
import com.kalsym.whatsapp.service.utils.Logger;
import com.kalsym.whatsapp.service.utils.HttpPostConn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.kalsym.whatsapp.service.utils.HttpResult;

/**
 * @author taufik
 */
public class FacebookCloud {

    public static void sendInteractiveMessage(WhatsappMessage requestBody, Interactive interactive) {
        String logprefix = "FacebookCloud";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        
        WhatsappReq req = new WhatsappReq();
        req.setMessaging_product("whatsapp");
        req.setRecipient_type("individual");
        req.setTo(requestBody.getRecipientIds().get(0));
        req.setType("interactive");
        req.setInteractive(interactive);
        
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(req);
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Request Json:" + jsonRequest);

        String targetUrl = "https://graph.facebook.com/v14.0/101552592607485/messages";
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer EAAH0cEAnrjABAOW3CoEfSvedZAWlFZChcU68HhxZB2IOP467VO1zpzJmRLm3vwyY0LnbcMIUyZAtUKaGDE958OVeC8DId2RH3aM6xBZBMVxxkN39nGsLwLUem2we01hy9cbyb4lqKjKZBmBFnl7775r3XrLaYhbeZBTc68hAZA7G8YgCXHByuSt8vGBV7ZBp8GiFWIQgOwmTlygZDZD");
        HttpPostConn.SendHttpsRequest("POST", requestBody.getRecipientIds().get(0), targetUrl, httpHeader, jsonRequest, connectTimeout, waitTimeout);
    }
    
    
    public static void sendNotificationMessage(WhatsappMessage requestBody) {
        String logprefix = "FacebookCloud";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        
        WhatsappReqNotification req = new WhatsappReqNotification();
        req.setMessaging_product("whatsapp");
        req.setRecipient_type("individual");
        req.setTo(requestBody.getRecipientIds().get(0));        
        req.setText(new Text(requestBody.getText()));
        
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(req);
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Request Json:" + jsonRequest);

        String targetUrl = "https://graph.facebook.com/v14.0/101552592607485/messages";
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer EAAH0cEAnrjABAOW3CoEfSvedZAWlFZChcU68HhxZB2IOP467VO1zpzJmRLm3vwyY0LnbcMIUyZAtUKaGDE958OVeC8DId2RH3aM6xBZBMVxxkN39nGsLwLUem2we01hy9cbyb4lqKjKZBmBFnl7775r3XrLaYhbeZBTc68hAZA7G8YgCXHByuSt8vGBV7ZBp8GiFWIQgOwmTlygZDZD");
        HttpPostConn.SendHttpsRequest("POST", requestBody.getRecipientIds().get(0), targetUrl, httpHeader, jsonRequest, connectTimeout, waitTimeout);
    }
    
    
    public static HttpResult sendTemplateMessage(WhatsappMessage requestBody) {
        String logprefix = "FacebookCloud";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String receiverMsisdn = requestBody.getRecipientIds().get(0);
        if (receiverMsisdn.startsWith("01")) {
            receiverMsisdn = "6" + receiverMsisdn;
        } else if (receiverMsisdn.startsWith("0")) {
            receiverMsisdn = "92" + receiverMsisdn.substring(1);
        }
        FbRequest req = new FbRequest();
        req.setMessaging_product("whatsapp");
        req.setTo(receiverMsisdn);
        req.setType("template");
        
        Template template = new Template();
        template.setName(requestBody.getTemplate().getName());
        Language lang = new Language();
        lang.setCode("en");
        template.setLanguage(lang);
        
        Component[] componentList = new Component[3];
            
        if (requestBody.getTemplate().getParameters()!=null) {
            Parameter[] paramList = new Parameter[requestBody.getTemplate().getParameters().length];
            for (int i=0;i<requestBody.getTemplate().getParameters().length;i++) {
                String param = requestBody.getTemplate().getParameters()[i];
                Parameter parameter = new Parameter();
                parameter.setText(param);
                parameter.setType("text");
                paramList[i] = parameter;
            }                
            Component component = new Component();
            component.setType("body");
            component.setParameters(paramList);
            componentList[0] = component;
            template.setComponents(componentList);
        }
        
        if (requestBody.getTemplate().getParametersButton()!=null) {
            Parameter[] paramButtonList = new Parameter[requestBody.getTemplate().getParametersButton().length];
            for (int i=0;i<requestBody.getTemplate().getParametersButton().length;i++) {
                String param = requestBody.getTemplate().getParametersButton()[i];
                Parameter parameter = new Parameter();
                parameter.setText(param);
                parameter.setType("text");            
                paramButtonList[i] = parameter;
            }

            Component componentButton = new Component();
            componentButton.setType("button");
            componentButton.setSub_type("url");
            componentButton.setIndex(0);
            componentButton.setParameters(paramButtonList);
            componentList[1] = componentButton;                    
        }        
        
        template.setComponents(componentList);
        
        req.setTemplate(template);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(req);
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Request Json:"+jsonRequest);
        
        String targetUrl = "https://graph.facebook.com/v14.0/101552592607485/messages";
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Authorization", "Bearer EAAH0cEAnrjABAOW3CoEfSvedZAWlFZChcU68HhxZB2IOP467VO1zpzJmRLm3vwyY0LnbcMIUyZAtUKaGDE958OVeC8DId2RH3aM6xBZBMVxxkN39nGsLwLUem2we01hy9cbyb4lqKjKZBmBFnl7775r3XrLaYhbeZBTc68hAZA7G8YgCXHByuSt8vGBV7ZBp8GiFWIQgOwmTlygZDZD");
        httpHeader.put("Content-Type","application/json");
        HttpResult result = HttpPostConn.SendHttpsRequest("POST", receiverMsisdn, targetUrl, httpHeader, jsonRequest, connectTimeout, waitTimeout);
        return result;
    }
}
