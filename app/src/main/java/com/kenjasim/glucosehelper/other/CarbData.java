package com.kenjasim.glucosehelper.other;

public class CarbData {

    String foodName;
    String carbs;
    String amount;
    String dataid;

    public CarbData(){

    }

    public CarbData(String foodName, String carbs, String amount, String dataid){
        this.foodName = foodName;
        this.carbs = carbs;
        this.amount = amount;
        this.dataid = dataid;

    }

    public String getFoodName() {
        return foodName;
    }

    public String getCarbs() {
        return carbs;
    }

    public String getAmount() {
        return amount;
    }

    public String getDataid() {
        return dataid;
    }
}









