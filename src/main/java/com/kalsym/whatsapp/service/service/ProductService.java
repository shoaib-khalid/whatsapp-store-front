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
import com.kalsym.whatsapp.service.model.WhatsappMessage;
import com.kalsym.whatsapp.service.provider.messengerCloud.Interactive;
import com.kalsym.whatsapp.service.provider.messengerCloud.WhatsappReq;
import com.kalsym.whatsapp.service.utils.HttpPostConn;
import com.kalsym.whatsapp.service.utils.HttpResult;
import com.kalsym.whatsapp.service.utils.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author taufik
 */
public class ProductService {
    
    static String storeId = "c9315221-a003-4830-9e28-c26c3d044dff";
    
    public static Store GetStoreDetails() {
        String logprefix = "ProductService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start GetStoreDetails");
        String targetUrl = "https://api.symplified.it/product-service/v1/stores/"+storeId;
        int connectTimeout = 10000;
        int waitTimeout = 30000;
        HashMap httpHeader = new HashMap();
        httpHeader.put("Content-Type", "application/json");
        httpHeader.put("Authorization", "Bearer accessToken");
        HttpResult result = HttpPostConn.SendHttpsRequest("GET", null, targetUrl, httpHeader, null, connectTimeout, waitTimeout);
        if (result.resultCode==0 && result.httpResponseCode==200) {
            JsonObject jsonResponse = new Gson().fromJson(result.responseString, JsonObject.class);
            Store store = new Store();
            JsonObject jsonData = jsonResponse.get("data").getAsJsonObject();
            store.name = jsonData.get("name").getAsString();
            store.description = jsonData.get("storeDescription").getAsString();
            return store;
        } else {
            return null;
        }
    }
    
    public static List<Category> GetProductCategory() {
        String logprefix = "ProductService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/product-service/v1/store-categories?page=0&pageSize=20&sortByCol=name&sortingOrder=ASC&storeId="+storeId;
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
            JsonArray jsonArray = jsonData.get("content").getAsJsonArray();
            List<Category> categoryList = new ArrayList<>();            
            for (int i=0;i<jsonArray.size();i++) {
                JsonObject jsonCategory = jsonArray.get(i).getAsJsonObject();
                Category category = new Category();
                category.id = jsonCategory.get("id").getAsString();
                category.name = jsonCategory.get("name").getAsString();
                categoryList.add(category);
            }
            return categoryList;
        } else {
            return null;
        }
    }
    
     public static List<Product> GetProductList(String categoryId) {
        String logprefix = "ProductService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/product-service/v1/stores/"+storeId+"/products?categoryId="+categoryId+"&page=0&pageSize=20&sortByCol=name&sortingOrder=ASC";
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
            JsonArray jsonArray = jsonData.get("content").getAsJsonArray();
            List<Product> productList = new ArrayList<>();            
            for (int i=0;i<jsonArray.size();i++) {
                JsonObject jsonCategory = jsonArray.get(i).getAsJsonObject();
                Product product = new Product();
                product.id = jsonCategory.get("id").getAsString();
                product.name = jsonCategory.get("name").getAsString();
                product.description = jsonCategory.get("description").getAsString();
                JsonArray inventories = jsonCategory.get("productInventories").getAsJsonArray();
                JsonObject item = inventories.get(0).getAsJsonObject();
                product.itemCode = item.get("itemCode").getAsString();
                product.price = item.get("price").getAsDouble();
                productList.add(product);
            }
            return productList;
        } else {
            return null;
        }
    }
     
     
     public static Product GetItemDetails(String itemCode, String productId) {
        String logprefix = "ProductService";
        Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Start sending message");
        String targetUrl = "https://api.symplified.it/product-service/v1/stores/"+storeId+"/products/"+productId+"/inventory/"+itemCode;
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
            JsonObject jsonProduct = jsonData.get("product").getAsJsonObject();
            Product product = new Product();
            product.name = jsonProduct.get("name").getAsString();
            product.itemCode = itemCode;
            product.id = productId;
            product.description = jsonProduct.get("description").getAsString();
            product.price = jsonData.get("price").getAsDouble();
            product.imageUrl = "https://assets.symplified.it/"+jsonProduct.get("thumbnailUrl").getAsString();
            return product;
        } else {
            return null;
        }
    }
    
}