package com.beintoo.wrappers;


import com.google.gson.Gson;

public class CardsTransaction {

    public enum CardTransactionStatus {

        RETURNED, ACCREDITED, PENDING
    }

    private String business_id;
    private String store_id;
    private String card_token;
    private String purchase_date_time;
    private String id;
    private CardTransactionStatus type;
    private String amount;
    private String currency;

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getCard_token() {
        return card_token;
    }

    public void setCard_token(String card_token) {
        this.card_token = card_token;
    }

    public String getPurchase_date_time() {
        return purchase_date_time;
    }

    public void setPurchase_date_time(String purchase_date_time) {
        this.purchase_date_time = purchase_date_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CardTransactionStatus getType() {
        return type;
    }

    public void setType(CardTransactionStatus type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
