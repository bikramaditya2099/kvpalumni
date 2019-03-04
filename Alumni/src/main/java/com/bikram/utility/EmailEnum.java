package com.bikram.utility;

public enum EmailEnum {
HI("Hi"),
REGARDS("Regards"),
VERIFIED("Your Membership is succesfully verified , Please Login to continue.");

private String value;


public String getValue() {
	return value;
}


private EmailEnum(String value) {
	this.value = value;
}

	
}
