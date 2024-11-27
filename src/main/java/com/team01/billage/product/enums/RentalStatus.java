package com.team01.billage.product.enums;

public enum RentalStatus {
    AVAILABLE("대여 판매 중"),
    RENTED("대여 중");

    private final String displayName;

    RentalStatus(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
