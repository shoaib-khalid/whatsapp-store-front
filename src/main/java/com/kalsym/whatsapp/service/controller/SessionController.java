/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalsym.whatsapp.service.controller;

import com.kalsym.whatsapp.service.service.Payment;
import com.kalsym.whatsapp.service.WhatsappServiceApplication;
import com.kalsym.whatsapp.service.utils.Utilities;
import java.util.ArrayList;
import java.util.List;
import com.kalsym.whatsapp.service.model.UserSession;
import com.kalsym.whatsapp.service.model.UserPayment;
import com.kalsym.whatsapp.service.provider.messengerCloud.*;
import com.kalsym.whatsapp.service.repository.UserSessionRepository;
import com.kalsym.whatsapp.service.repository.UserPaymentRepository;
import com.kalsym.whatsapp.service.utils.Logger;
import com.kalsym.whatsapp.service.service.*;
/**
 *
 * @author taufik
 */
public class SessionController {
    
    public static Interactive GenerateResponseMessage(String msisdn, int stage, String userInput) {
        
        String logprefix = msisdn;
        //check user session
        //List<Row> rowList=null;
        String headerText = null;
        String bodyText = null;
        List<Button> buttonList = new ArrayList<>();
        List<Section> sectionList = new ArrayList<>();
                         
        Interactive interactiveMsg = new Interactive();
        
        if (stage==0) {
            Store store = ProductService.GetStoreDetails();
            Logger.application.info(Logger.pattern, WhatsappServiceApplication.VERSION, logprefix, "Store details name:"+store.name+" ");
            List<Category> categoryList = ProductService.GetProductCategory();
           
            //main menu
            headerText = store.name;
            String storeDescription = "";
            if (store.description!=null) {
                storeDescription = store.description.replaceAll("<[^>]*>", "");
                storeDescription = storeDescription.replaceAll("amp;","");
            }
            bodyText = storeDescription;    
            Action action = new Action();
            action.setButton("Category List");
            
            Section section1 = new Section();
            section1.setTitle("Select Product Category");
            List<Row> rowList=new ArrayList<>();
            for (int i=0;i<categoryList.size();i++) {
                Category category = categoryList.get(i);
                String description = "";
                if (category.description!=null) {
                    description = category.description.replaceAll("<[^>]*>", "");
                }
                Row row1 = new Row("C"+category.id, category.name, description);
                rowList.add(row1);
            }            
            section1.setRows(rowList);
            sectionList.add(section1);                       
            action.setSections(sectionList);
            
            interactiveMsg.setAction(action);
            
            interactiveMsg.setType("list");
            
            Header header = new Header();
            header.setType("text");
            header.setText(headerText);
            interactiveMsg.setHeader(header);                   
        }
               
        Body body = new Body();
        body.setText(bodyText);
        Footer footer = new Footer();
        footer.setText("Quick access:\n00 for main menu\n99 for view cart");
        
        interactiveMsg.setBody(body);
        interactiveMsg.setFooter(footer);
                
        return interactiveMsg;
    }
    
    public static Interactive UserSelectCategory(String msisdn, int stage, String categoryId, String categoryName) {
        
        //remove prefix
        categoryId = categoryId.substring(1);
        String logprefix = msisdn;
        //check user session
        //List<Row> rowList=null;
        String headerText = null;
        if (categoryName.length()>20) {
            categoryName = categoryName.substring(0,20);
        }
        String bodyText = categoryName;
        List<Button> buttonList = new ArrayList<>();
        List<Section> sectionList = new ArrayList<>();
                         
        Interactive interactiveMsg = new Interactive();
        
        List<Product> productList = ProductService.GetProductList(categoryId);

        Section section1 = new Section();
        section1.setTitle("Select Product");
        List<Row> rowList=new ArrayList<>();
        for (int i=0;i<productList.size();i++) {
            Product product = productList.get(i);
            String description = "";
            if (product.description!=null) {
                description = product.description.replaceAll("<[^>]*>", "");
            }
            Row row1 = new Row("P"+product.id+","+product.itemCode, product.name, description, product.price);
            rowList.add(row1);
        }            
        section1.setRows(rowList);
        sectionList.add(section1);  
        Action action = new Action();
        action.setButton("Product List");
        action.setSections(sectionList);

        interactiveMsg.setAction(action);

        interactiveMsg.setType("list");
                       
        Body body = new Body();
        body.setText(bodyText);
        Footer footer = new Footer();
        footer.setText("Quick access:\n00 for main menu\n99 for view cart");
        
        interactiveMsg.setBody(body);
        interactiveMsg.setFooter(footer);
                
        return interactiveMsg;
    }
    
    
    public static Interactive UserSelectProduct(String msisdn, int stage, String productDetails, String productName) {
        
        //remove prefix
        productDetails = productDetails.substring(1);
        String[] temp = productDetails.split(",");
        String productId = temp[0];
        String itemCode = temp[1];
        String logprefix = msisdn;
        
        List<Button> buttonList = new ArrayList<>();
                         
        Interactive interactiveMsg = new Interactive();
        
        Button button = new Button(new Reply("ADD"+itemCode+","+productId, "Add to cart"));
        buttonList.add(button);
        
        Product itemDetails = ProductService.GetItemDetails(itemCode, productId);
        
        Action action = new Action();
        action.setButtons(buttonList);
        interactiveMsg.setAction(action);

        interactiveMsg.setType("button");
        
        Header header = new Header();
        header.setType("image");
        header.setText(itemDetails.name);
        Image image = new Image();
        image.setLink(itemDetails.imageUrl);
        header.setImage(image);
        interactiveMsg.setHeader(header); 
            
        Body body = new Body();
        /*if (description!=null && description.length()>72) {
            description = description.substring(0, 72);
        }*/
        String description = "";
        if (itemDetails.description!=null) {
            description = itemDetails.description.replaceAll("<[^>]*>", "");
        }
            
        body.setText(description);
        Footer footer = new Footer();
        footer.setText("Quick access:\n00 for main menu\n99 for view cart");
        
        interactiveMsg.setBody(body);
        interactiveMsg.setFooter(footer);
                
        return interactiveMsg;
    }
        
    public static Interactive UserAddToCart(String cartId, String msisdn, int stage, String productDetails, String productName) {
        
        //remove prefix
        String[] temp = productDetails.substring(3).split(",");
        String itemCode = temp[0];
        String productId = temp[1];
        String logprefix = msisdn;
       
        Cart cart = OrderService.AddItemToCart(cartId, productId, itemCode, 1);
        
        Interactive interactiveMsg = UserViewCart(cart.id);
        return interactiveMsg;
    }
    
    public static Interactive UserRemoveFromCart(String cartId, String msisdn, int stage, String productDetails) {
        
        //remove prefix
        String[] temp = productDetails.substring(3).split(",");
        String itemId = temp[0];
        String productId = temp[1];
           
        Cart cart = OrderService.RemoveItemFromCart(cartId, itemId);        
        
        Interactive interactiveMsg = UserViewCart(cartId);
        return interactiveMsg;
    }
    
    
    
    public static Interactive UserViewCart(String cartId) {
        
        
        String bodyText = "Shopping Cart";
        List<Section> sectionList = new ArrayList<>();
                         
        Interactive interactiveMsg = new Interactive();
        
        List<CartItem> cartItems = OrderService.GetItemInCart(cartId);

        Section section1 = new Section();
        section1.setTitle("Select item to remove");
        List<Row> rowList=new ArrayList<>();
        Double totalPrice = 0.00;
        for (int i=0;i<cartItems.size();i++) {
            CartItem cartItem = cartItems.get(i);
            String itemName = cartItem.productName;
            if (itemName.length()>10) {
                itemName = itemName.substring(0,10);
            }
            totalPrice = totalPrice + cartItem.price;
            
            String description = cartItem.quantity+" RM"+cartItem.price;
            Row row1 = new Row("REM"+cartItem.id+","+cartId, itemName, description);
            rowList.add(row1);
        }            
        section1.setRows(rowList);
        sectionList.add(section1);  
        Action action = new Action();
        action.setButton("Cart Item");
        action.setSections(sectionList);

        interactiveMsg.setAction(action);

        interactiveMsg.setType("list");

        bodyText = bodyText+"\n\nSubtotal: RM"+Utilities.Round2DecimalPoint(totalPrice);
        Body body = new Body();
        body.setText(bodyText);
        Footer footer = new Footer();
        footer.setText("Quick access:\n00 for main menu\n88 for checkout");
        
        interactiveMsg.setBody(body);
        interactiveMsg.setFooter(footer);
                
        return interactiveMsg;
    }
    
    
    public static Interactive Checkout(String cartId) {
                
        Cart cart = OrderService.GetTotalDiscount(cartId);
        
        List<Button> buttonList = new ArrayList<>();                         
        Interactive interactiveMsg = new Interactive();
        
        Button button = new Button(new Reply("BAY"+cartId, "Pay Now"));
        buttonList.add(button);
        
        Action action = new Action();
        action.setButtons(buttonList);
        interactiveMsg.setAction(action);

        interactiveMsg.setType("button");
        
        Header header = new Header();
        header.setType("text");
        header.setText("Shopping Cart");
        interactiveMsg.setHeader(header); 
            
        Body body = new Body();
        String cartItemDetails="";
        Double totalPrice = 0.00;
        List<CartItem> cartItems = OrderService.GetItemInCart(cartId);
        for (int i=0;i<cartItems.size();i++) {
            int index=i+1;
            CartItem item = cartItems.get(i);
            String itemName = item.productName;
            if (itemName.length()>10) {
                itemName = itemName.substring(0,10);
            }
            totalPrice = totalPrice + item.price;
            cartItemDetails = cartItemDetails + itemName+" ["+item.quantity+"] RM"+item.price+"\n";
        }
        cartItemDetails = cartItemDetails + "\nSubTotal : RM"+Utilities.Round2DecimalPoint(cart.subTotal);
        cartItemDetails = cartItemDetails + "\nDiscount : RM"+Utilities.Round2DecimalPoint(cart.discount);
        cartItemDetails = cartItemDetails + "\nServiceCharge : RM"+Utilities.Round2DecimalPoint(cart.serviceCharge);
        cartItemDetails = cartItemDetails + "\nGrandTotal : RM"+Utilities.Round2DecimalPoint(cart.grandTotal);
        body.setText(cartItemDetails);
        Footer footer = new Footer();
        footer.setText("Quick access:\n00 for main menu\n99 for view cart");
        
        interactiveMsg.setBody(body);
        interactiveMsg.setFooter(footer);
                
        return interactiveMsg;
    }
    
    public static Interactive EnterName(String cartId, String msisdn, int stage) {
                
        List<Button> buttonList = new ArrayList<>();
                         
        Interactive interactiveMsg = new Interactive();
        
        Button button = new Button(new Reply("DEL"+cartId, "Cancel"));
        buttonList.add(button);
        
        Action action = new Action();
        action.setButtons(buttonList);
        interactiveMsg.setAction(action);

        interactiveMsg.setType("button");
           
        Body body = new Body();
        body.setText("Enter your name");
        Footer footer = new Footer();
        footer.setText("Quick access:\n00 for main menu\n99 for view cart");
        
        interactiveMsg.setBody(body);
        interactiveMsg.setFooter(footer);
                
        return interactiveMsg;
    }
    
    public static Interactive EnterEmail(String cartId, String msisdn, int stage) {
                
        List<Button> buttonList = new ArrayList<>();
                         
        Interactive interactiveMsg = new Interactive();
        
        Button button = new Button(new Reply("DEL"+cartId, "Cancel"));
        buttonList.add(button);
        
        Action action = new Action();
        action.setButtons(buttonList);
        interactiveMsg.setAction(action);

        interactiveMsg.setType("button");
           
        Body body = new Body();
        body.setText("Enter your email");
        Footer footer = new Footer();
        footer.setText("Quick access:\n00 for main menu\n99 for view cart");
        
        interactiveMsg.setBody(body);
        interactiveMsg.setFooter(footer);
                
        return interactiveMsg;
    }
    
    
    public static Order PlaceOrder(
                String cartId, 
                String msisdn, 
                int stage, 
                String customerName, 
                String customerEmail, 
                UserSession userSession,
                UserSessionRepository userSessionRepository,
                UserPaymentRepository userPaymentRepository
                ) {
        Order order = OrderService.PlaceOrder(cartId, null, customerName, customerEmail, msisdn);
        if (order.id!=null) {
            Payment payment = OrderService.MakePayment(null, customerName, order.id, order.total);
            if (payment.getIsSuccess()) {
                
                //save payment
                UserPayment userPayment = new UserPayment();
                userPayment.setPaymentOrderId(order.id);
                userPayment.setPaymentDetail(payment.getDetail());
                userPayment.setPaymentHash(payment.getHash());
                userPayment.setPaymentOrderId(order.id);
                userPayment.setPaymentAmount(payment.getAmount());
                userPayment.setName(customerName);
                userPayment.setEmail(customerEmail);
                userPayment.setPaymentLink(payment.getPaymentLink());
                userPayment.setMsisdn(msisdn);
                userPaymentRepository.save(userPayment);
                
                //remove session
                userSessionRepository.delete(userSession);
                
                return order;                
            }
        }
        return null;
    }
}
