/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalsym.whatsapp.service.service;

import java.util.List;

/**
 *
 * @author taufik
 */
public class Cart {
    public String id;
    //public String customerId;
    public String storeId;
    public List<CartItem> cartItems;
    public Double subTotal;
    public Double grandTotal;
    public Double discount;
    public Double serviceCharge;
}
