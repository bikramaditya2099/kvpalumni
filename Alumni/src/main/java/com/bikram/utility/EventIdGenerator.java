package com.bikram.utility;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class EventIdGenerator {

	public Long generate() 
    { 
       int len=5;
  
        // Using numeric values 
        String numbers = "123456789"; 
  
        // Using random method 
        Random rndm_method = new Random(); 
  
        char[] otp = new char[len]; 
  
        for (int i = 0; i < len; i++) 
        { 
            otp[i] = 
             numbers.charAt(rndm_method.nextInt(numbers.length())); 
        } 
        return Long.valueOf(new String(otp)); 
    } 
	
}
