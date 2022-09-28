/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalsym.whatsapp.service.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.kalsym.whatsapp.service.WhatsappServiceApplication;
import static com.kalsym.whatsapp.service.service.ProductService.storeId;
import com.kalsym.whatsapp.service.utils.HttpPostConn;
import com.kalsym.whatsapp.service.utils.HttpResult;
import com.kalsym.whatsapp.service.utils.Logger;
import com.kalsym.whatsapp.service.utils.DateTimeUtil;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author taufik
 */
public class OrderService {
    
    static String storeId = "c9315221-a003-4830-9e28-c26c3d044dff";
    static String storeName = "ABC Test";
    
    public static Cart CreateNewCart(String customerId) {
        String logprefix = "OrderService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/order-service/v1/carts";
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer accessToken");
        /*JsonObject requestJson = new JsonObject();
        requestJson.addProperty("customerId", null);
        requestJson.addProperty("storeId", storeId);*/
        String requestJson = "{\"customerId\":null, \"storeId\":\""+storeId+"\"}";
        HttpResult result = HttpPostConn.SendHttpsRequest("POST", null, targetUrl, httpHeader, requestJson, connectTimeout, waitTimeout);
        if (result.resultCode==0 && result.httpResponseCode==201) {
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "response:"+result.responseString);
            JsonObject jsonResponse = new Gson().fromJson(result.responseString, JsonObject.class);            
            JsonObject jsonData = jsonResponse.get("data").getAsJsonObject();
            Cart cart = new Cart();            
            cart.id = jsonData.get("id").getAsString();
            cart.storeId = jsonData.get("storeId").getAsString();
            return cart;
        } else {
            return null;
        }
    }
    
    
    public static Cart AddItemToCart(String cartId, String productId, String itemCode, int quantity) {
        String logprefix = "OrderService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/order-service/v1/carts/"+cartId+"/items";
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer accessToken");
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("cartId", cartId);
        requestJson.addProperty("itemCode", itemCode);
        requestJson.addProperty("productId", productId);
        //requestJson.addProperty("productPrice", productId);
        //requestJson.addProperty("price", productId);
        requestJson.addProperty("quantity", quantity);
        HttpResult result = HttpPostConn.SendHttpsRequest("POST", null, targetUrl, httpHeader, requestJson.toString(), connectTimeout, waitTimeout);
        if (result.resultCode==0 && result.httpResponseCode==201) {
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "response:"+result.responseString);
            JsonObject jsonResponse = new Gson().fromJson(result.responseString, JsonObject.class);            
            JsonObject jsonData = jsonResponse.get("data").getAsJsonObject();
            Cart cart = new Cart();            
            cart.id = jsonData.get("cartId").getAsString();
            return cart;
        } else {
            return null;
        }
    }
    
    public static Cart RemoveItemFromCart(String cartId, String itemId) {
        String logprefix = "OrderService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/order-service/v1/carts/"+cartId+"/items/"+itemId;
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer accessToken");
        HttpResult result = HttpPostConn.SendHttpsRequest("DELETE", null, targetUrl, httpHeader, null, connectTimeout, waitTimeout);
        if (result.resultCode==0 && result.httpResponseCode==201) {
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "response:"+result.responseString);
            Cart cart = new Cart();            
            cart.id = cartId;
            return cart;
        } else {
            return null;
        }
    }
    
    
    public static List<CartItem> GetItemInCart(String cartId) {
        String logprefix = "OrderService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/order-service/v1/carts/details?cartIdList="+cartId+"&page=0&pageSize=5&includeEmptyCart=false";
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer accessToken");
        HttpResult result = HttpPostConn.SendHttpsRequest("GET", null, targetUrl, httpHeader, null, connectTimeout, waitTimeout);
        if (result.resultCode==0 && result.httpResponseCode==200) {
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "response:"+result.responseString);
            JsonObject jsonResponse = new Gson().fromJson(result.responseString, JsonObject.class);            
            JsonObject jsonData = jsonResponse.get("data").getAsJsonObject();
            JsonArray jsonContent = jsonData.get("content").getAsJsonArray();
            List<CartItem> cartItems = new ArrayList<>();                                        
            if (jsonContent.size()>0) {
                JsonArray jsonItems = jsonContent.get(0).getAsJsonObject().get("cartItems").getAsJsonArray();
                for (int i=0;i<jsonItems.size();i++) {
                    JsonObject jsonItem = jsonItems.get(i).getAsJsonObject();
                    String itemId = jsonItem.get("id").getAsString();
                    String itemCode = jsonItem.get("itemCode").getAsString();
                    Double price = jsonItem.get("price").getAsDouble();
                    Double productPrice = jsonItem.get("productPrice").getAsDouble();
                    int quantity = jsonItem.get("quantity").getAsInt();
                    String productName = jsonItem.get("productName").getAsString();
                    CartItem cartItem = new CartItem();
                    cartItem.cartId = cartId;
                    cartItem.id = itemId;
                    cartItem.itemCode = itemCode;
                    cartItem.price = price;
                    cartItem.productPrice = productPrice;
                    cartItem.quantity = quantity;
                    cartItem.productName = productName;
                    cartItems.add(cartItem);
                }
            }
            return cartItems;
        } else {
            return null;
        }
    }
    
    public static Cart GetTotalDiscount(String cartId) {
        String logprefix = "OrderService";
        String customerId = null;
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/order-service/v1/carts/"+cartId+"/discount?deliveryType=PICKUP&customerId="+customerId;
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer accessToken");
        HttpResult result = HttpPostConn.SendHttpsRequest("GET", null, targetUrl, httpHeader, null, connectTimeout, waitTimeout);
        if (result.resultCode==0 && result.httpResponseCode==200) {
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "response:"+result.responseString);
            JsonObject jsonResponse = new Gson().fromJson(result.responseString, JsonObject.class);            
            JsonObject jsonData = jsonResponse.get("data").getAsJsonObject();
            Cart cart = new Cart();
            cart.subTotal = jsonData.get("cartSubTotal").getAsDouble();
            cart.serviceCharge = jsonData.get("storeServiceCharge").getAsDouble();
            cart.grandTotal = jsonData.get("cartGrandTotal").getAsDouble();
            cart.discount = jsonData.get("subTotalDiscount").getAsDouble();
            return cart;
        } else {
            return null;
        }
    }
    
    public static Order PlaceOrder(String cartId, String customerId, String customerName, String customerEmail, String customerPhoneNumber) {
        String logprefix = "OrderService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/order-service/v1/orders/placeOrder?cartId="+cartId+"&saveCustomerInformation=false";
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer accessToken");
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("cartId", cartId);
        requestJson.addProperty("customerId", customerId);
        JsonObject orderPaymentDetails = new JsonObject();
        orderPaymentDetails.addProperty("accountName", customerName);
        requestJson.add("orderPaymentDetails", orderPaymentDetails);
        JsonObject orderShipmentDetails = new JsonObject();
        orderShipmentDetails.addProperty("receiverName",customerName);
        orderShipmentDetails.addProperty("deliveryType", "PICKUP");
        orderShipmentDetails.addProperty("email", customerEmail);
        orderShipmentDetails.addProperty("phoneNumber", customerPhoneNumber);
        orderShipmentDetails.addProperty("storePickup", true);
        requestJson.add("orderShipmentDetails", orderShipmentDetails);
        HttpResult result = HttpPostConn.SendHttpsRequest("POST", null, targetUrl, httpHeader, requestJson.toString(), connectTimeout, waitTimeout);
        if (result.resultCode==0 && result.httpResponseCode==201) {
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "response:"+result.responseString);
            JsonObject jsonResponse = new Gson().fromJson(result.responseString, JsonObject.class);            
            JsonObject jsonData = jsonResponse.get("data").getAsJsonObject();
            Order order = new Order();            
            order.id = jsonData.get("id").getAsString();
            order.total = jsonData.get("total").getAsDouble();
            return order;
        } else {
            return null;
        }
    }
    
    
    public static Payment MakePayment(String customerId, String customerName, String orderId, Double amount) {
        String logprefix = "OrderService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/payment-service/v1/payments/makePayment";
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer accessToken");
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("customerId", customerId);
        requestJson.addProperty("customerName", customerName);
        requestJson.addProperty("productCode","parcel");
        requestJson.addProperty("storeName",storeName);
        requestJson.addProperty("systemTransactionId", DateTimeUtil.GenerateTxId());
        requestJson.addProperty("transactionId", orderId);
        HttpResult result = HttpPostConn.SendHttpsRequest("POST", null, targetUrl, httpHeader, requestJson.toString(), connectTimeout, waitTimeout);
        if (result.resultCode==0 && result.httpResponseCode==200) {
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "response:"+result.responseString);
            JsonObject jsonResponse = new Gson().fromJson(result.responseString, JsonObject.class);            
            JsonObject jsonData = jsonResponse.get("data").getAsJsonObject();
            Payment payment = new Payment();            
            payment.setDetail(jsonData.get("sysTransactionId").getAsString());
            payment.setAmount(amount);
            payment.setHash(jsonData.get("hash").getAsString());
            payment.setIsSuccess(jsonData.get("isSuccess").getAsBoolean());
            payment.setPaymentLink(jsonData.get("paymentLink").getAsString());
            return payment;
        } else {
            return null;
        }
    }

}
