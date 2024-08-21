package com.example.androidmobilestock;

public class AnalyticModel {
    String title, title2, description, description2, amount, amount2;

    public AnalyticModel(String title, String title2, String description, String description2,
                         String amount, String amount2) {
        this.title = title;
        this.title2 = title2;
        this.description = description;
        this.description2 = description2;
        this.amount = amount;
        this.amount2 = amount2;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount2() {
        return amount2;
    }

    public void setAmount2(String amount2) {
        this.amount2 = amount2;
    }
}
