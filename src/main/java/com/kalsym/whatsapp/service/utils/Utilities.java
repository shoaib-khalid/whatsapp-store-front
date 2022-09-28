/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalsym.whatsapp.service.utils;

/**
 *
 * @author taufik
 */
public class Utilities {
    public static Double Round2DecimalPoint(Double input) {
        if (input == null) { return null; }
        return Math.round(input * 100.0) / 100.0;
    }
}
