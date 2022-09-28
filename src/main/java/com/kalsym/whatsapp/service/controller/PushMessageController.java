package com.kalsym.whatsapp.service.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kalsym.whatsapp.service.WhatsappServiceApplication;
import com.kalsym.whatsapp.service.model.WhatsappMessage;
import com.kalsym.whatsapp.service.model.WhatsappTemplate;
import com.kalsym.whatsapp.service.model.UserSession;
import com.kalsym.whatsapp.service.provider.messengerCloud.Interactive;
import com.kalsym.whatsapp.service.provider.messengerCloud.FacebookCloud;
import com.kalsym.whatsapp.service.utils.HttpResponse;
import com.kalsym.whatsapp.service.utils.Logger;
import com.kalsym.whatsapp.service.utils.DateTimeUtil;
import com.kalsym.whatsapp.service.repository.UserSessionRepository;
import com.kalsym.whatsapp.service.repository.UserPaymentRepository;
import com.kalsym.whatsapp.service.service.ProductService;
import com.kalsym.whatsapp.service.service.OrderService;
import com.kalsym.whatsapp.service.service.Store;
import com.kalsym.whatsapp.service.service.Cart;
import com.kalsym.whatsapp.service.service.Order;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping(path = "/message")
public class PushMessageController {

    @Autowired
    PushMessageController pushMessageController;
    
    @Autowired
    UserSessionRepository userSessionRepository;
    
    @Autowired
    UserPaymentRepository userPaymentRepository;


    @PostMapping(path = {"/test/webhook"}, name = "push-template-message-post")
    public ResponseEntity<HttpResponse> webhook(HttpServletRequest request, @RequestBody JsonObject json) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "callback-message-get, URL:  " + request.getRequestURI());
        JsonObject jsonResp = new Gson().fromJson(String.valueOf(json), JsonObject.class);
        JsonObject entry = jsonResp.get("entry").getAsJsonArray().get(0).getAsJsonObject();
        JsonObject changes = entry.get("changes").getAsJsonArray().get(0).getAsJsonObject();
        
        //user input : {"from":"60133731869","id":"wamid.HBgLNjAxMzM3MzE4NjkVAgASGBQzRUIwQkI3Q0FCRjIwQjNEMjg4OQA=","timestamp":"1660023849","text":{"body":"hello"},"type":"text"}
        //user select from list : {"context":{"from":"60125063299","id":"wamid.HBgLNjAxMzM3MzE4NjkVAgARGBJGQkVGQURCODBDRDQ0OUQ4M0IA"},"from":"60133731869","id":"wamid.HBgLNjAxMzM3MzE4NjkVAgASGBQzRUIwMDAxOURCMDk1RDhGNjk4QwA=","timestamp":"1660024660","type":"interactive","interactive":{"type":"list_reply","list_reply":{"id":"b4b3fac1-f593-4dff-ad64-2ad532cf4724","title":"Brew Coffee"}}}
        
        JsonObject messages = null;
        try {
            messages = changes.get("value").getAsJsonObject().get("messages").getAsJsonArray().get(0).getAsJsonObject();
        } catch (Exception ex) {
            //not a message
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "callback-message-get, not a message");            
            JsonObject statuses = changes.get("value").getAsJsonObject().get("statuses").getAsJsonArray().get(0).getAsJsonObject();
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "callback-message-get, receive a status : "+statuses.toString());            
            response.setSuccessStatus(HttpStatus.OK);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "callback-message-get, MessageBody: " + messages);
        
        JsonObject context = null;
        try {
            context = messages.get("context").getAsJsonObject();            
        } catch (Exception ex) {}
        
        WhatsappMessage messageBody = new WhatsappMessage();
        String phone = null;
        String userInput = null;
        String type = null;
        String replyTitle=null;
        String replyId=null;
        
        if (context!=null) {
            //user reply
            phone = messages.get("from").getAsString();
            type = messages.get("type").getAsString();
            if (type.equals("interactive")) {
                JsonObject interactive = messages.get("interactive").getAsJsonObject();
                String interactiveType = interactive.get("type").getAsString();
                if (interactiveType.equals("list_reply")) {
                    JsonObject listReply = interactive.get("list_reply").getAsJsonObject();
                    replyId = listReply.get("id").getAsString();
                    replyTitle = listReply.get("title").getAsString();
                } else if (interactiveType.equals("button_reply")) {
                    JsonObject listReply = interactive.get("button_reply").getAsJsonObject();
                    replyId = listReply.get("id").getAsString();
                    replyTitle = listReply.get("title").getAsString();
                }
            }
            List<String> mp = new ArrayList<>();
            mp.add(phone);
            messageBody.setRecipientIds(mp);
        } else {
            //user input
            type = "input";
            phone = messages.get("from").getAsString();
            userInput = messages.get("text").getAsJsonObject().get("body").getAsString();
            List<String> mp = new ArrayList<>();
            mp.add(phone);
            messageBody.setRecipientIds(mp);
        }
        
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Incoming message. Msisdn:"+phone+" UserInput:" + userInput);

        List<Object[]> userSession = userSessionRepository.findActiveSession(phone);
        Interactive interactiveMsg = null;
        if (!userSession.isEmpty()) {
            //get stage
            Optional<UserSession> sessionOpt = userSessionRepository.findById(phone);
            UserSession session = sessionOpt.get();
                
            if (userInput!=null && userInput.equals("00")) {
                //main menu
                Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Show main menu");
                session.setStage(0);
                session.setExpiry(DateTimeUtil.expiryTimestamp(120));            
                userSessionRepository.save(session);
                interactiveMsg = SessionController.GenerateResponseMessage(phone, 0, userInput); 
            } else if (userInput!=null && userInput.equals("99")) {
                //view cart
                Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "View cart");
                interactiveMsg = SessionController.UserViewCart(session.getCartId());
                session.setExpiry(DateTimeUtil.expiryTimestamp(120));            
                userSessionRepository.save(session);
            } else if (userInput!=null && userInput.equals("88")) {
                //checkout
                Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Checkout");
                interactiveMsg = SessionController.Checkout(session.getCartId());
                session.setExpiry(DateTimeUtil.expiryTimestamp(120));            
                userSessionRepository.save(session);
            } else {
                int stage = session.getStage();
                if (type.equals("input")) {
                    if (stage==0) {
                        interactiveMsg = SessionController.GenerateResponseMessage(phone, stage, userInput); 
                    } else if (stage==1) {
                        session.setStage(2);
                        session.setName(userInput);
                        interactiveMsg = SessionController.EnterEmail(session.getCartId(), phone, stage);
                    } else if (stage==2) { 
                        //generate payment form
                        session.setStage(3);
                        session.setEmail(userInput);
                        session.setExpiry(DateTimeUtil.expiryTimestamp(120));            
                        userSessionRepository.save(session);
                        
                        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Place Order for name:"+session.getName()+" email:"+session.getEmail());
                        Order order = SessionController.PlaceOrder(session.getCartId(), phone, stage, session.getName(), session.getEmail(), session, userSessionRepository, userPaymentRepository);
                         
                        try {
                            if (order!=null) {
                                //success create order
                                WhatsappTemplate template = new WhatsappTemplate();
                                template.setName("deliverin_payment_link2"); 
                                String[] params = {order.id};
                                template.setParametersButton(params);
                                messageBody.setTemplate(template);
                                FacebookCloud.sendTemplateMessage(messageBody);
                                response.setSuccessStatus(HttpStatus.CREATED);       
                            } else {
                                //fail
                                String responseMsg = "Fail to create order. Please try again later";
                                messageBody.setText(responseMsg);
                                FacebookCloud.sendNotificationMessage(messageBody);
                                response.setSuccessStatus(HttpStatus.CREATED);
                            }                            
                        } catch (Exception exp) {
                            Logger.application.error(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Error sending message : ", exp);
                            response.setMessage(exp.getMessage());
                            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
                        }

                        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Send message completed");
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                } else {
                    if (replyId!=null && replyId.startsWith("C")) {
                        interactiveMsg = SessionController.UserSelectCategory(phone, stage, replyId, replyTitle); 
                    } else if (replyId!=null && replyId.startsWith("P")) {
                        interactiveMsg = SessionController.UserSelectProduct(phone, stage, replyId, replyTitle); 
                        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Interactive body:"+interactiveMsg.getBody().toString());
                    } else if (replyId!=null && replyId.startsWith("ADD")) {
                        interactiveMsg = SessionController.UserAddToCart(session.getCartId(), phone, stage, replyId, replyTitle); 
                    } else if (replyId!=null && replyId.startsWith("REM")) {
                        interactiveMsg = SessionController.UserRemoveFromCart(session.getCartId(), phone, stage, replyId); 
                    } else if (replyId!=null && replyId.startsWith("BAY")) {
                        //enter name
                        session.setStage(1);
                        interactiveMsg = SessionController.EnterName(session.getCartId(), phone, stage); 
                    }
                }
                session.setExpiry(DateTimeUtil.expiryTimestamp(120));            
                userSessionRepository.save(session);
            }
        } else {
            //generate new cart
            Cart cart = OrderService.CreateNewCart(phone);
            //generate new session
            UserSession session = new UserSession();
            session.setMsisdn(phone);
            session.setStage(0);
            session.setExpiry(DateTimeUtil.expiryTimestamp(120)); 
            session.setCartId(cart.id);
            userSessionRepository.save(session);
            interactiveMsg = SessionController.GenerateResponseMessage(phone, 0, userInput);       
        }
                 
        try {
            FacebookCloud.sendInteractiveMessage(messageBody, interactiveMsg);
            response.setSuccessStatus(HttpStatus.CREATED);
        } catch (Exception exp) {
            Logger.application.error(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Error sending message : ", exp);
            response.setMessage(exp.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
        }

        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Send message completed");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/test/webhook"}, name = "push-template-message-post")
    public ResponseEntity<HttpResponse> verifyWebHook(HttpServletRequest request, @RequestParam(name = "hub.mode") String mode, @RequestParam(name = "hub.challenge") String challenge, @RequestParam(name = "hub.verify_token") String token) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        HttpResponse response = new HttpResponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "callback-message-get, URL:  " + request.getRequestURI());

        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "callback-message-get, messageBody: " + mode);
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "callback-message-get, Message Text : " + token);

        JsonObject object = new JsonObject();
        object.addProperty("hub.challenge", challenge);
        response.setData(object);

        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Send message completed");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
