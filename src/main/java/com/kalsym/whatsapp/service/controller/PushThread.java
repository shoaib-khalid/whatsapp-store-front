package com.kalsym.whatsapp.service.controller;

import com.kalsym.whatsapp.service.WhatsappServiceApplication;
import com.kalsym.whatsapp.service.model.WhatsappMessage;
import com.kalsym.whatsapp.service.utils.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

public class PushThread extends Thread implements Runnable {

    private final JSONObject jsonObject;

    HttpServletRequest request;
    @Autowired
    PushMessageController pushMessageController;

    public PushThread(JSONObject object, PushMessageController pushMessageController, HttpServletRequest request) {
        this.jsonObject = object;
        this.pushMessageController = pushMessageController;
        this.request = request;
    }

    public void run() {
        super.run();
        String logprefix = "PUSH THREAD ";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "callback-message-get, test: " + this.jsonObject);
        WhatsappMessage messageBody = new WhatsappMessage();
        try {
            //pushMessageController.pushWhatsappResponse(this.jsonObject);
        } catch (Exception e) {
            Logger.application.error(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Exception: ", e.getMessage());
        }

    }
}
