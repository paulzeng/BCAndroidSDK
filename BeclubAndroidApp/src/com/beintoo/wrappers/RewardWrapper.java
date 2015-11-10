package com.beintoo.wrappers;

import com.google.gson.Gson;

import java.util.List;


public class RewardWrapper {

    private String id;
    private String name;
    private String description;
    private Double bedollars;
    private ValueCurrencyWrapper valuecurrency;
    private String expiredate;
    private String imagesmall;
    private String imagebig;
    private String tos;
    private List<String> type;
    private List<String> delivery;
    private String status;
    private String brandsid;
    private String brandname;
    private String codeprovider;
    private String providerInfo;
    private String instructions;

    public enum RewardTypes {ALL, REGULAR, ARCHIVED;
        @Override
        public String toString() {
            if(this == ALL) return "all";
            else if(this == REGULAR) return "regular";
            else if(this == ARCHIVED) return "archived";
            else return super.toString();
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description.replace("\\n", "\n");
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBedollars() {
        return bedollars;
    }

    public void setBedollars(Double bedollars) {
        this.bedollars = bedollars;
    }

    public ValueCurrencyWrapper getValuecurrency() {
        return valuecurrency;
    }

    public void setValuecurrency(ValueCurrencyWrapper valuecurrency) {
        this.valuecurrency = valuecurrency;
    }

    public String getExpiredate() {
        return expiredate;
    }

    public void setExpiredate(String expiredate) {
        this.expiredate = expiredate;
    }

    public String getImagesmall() {
        return imagesmall;
    }

    public void setImagesmall(String imagesmall) {
        this.imagesmall = imagesmall;
    }

    public String getImagebig() {
        return imagebig;
    }

    public void setImagebig(String imagebig) {
        this.imagebig = imagebig;
    }

    public String getTos() {
        return (tos!=null) ? tos.replace("\\n", "\n") : null;
    }

    public void setTos(String tos) {
        this.tos = tos;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<String> getDelivery() {
        return delivery;
    }

    public void setDelivery(List<String> delivery) {
        this.delivery = delivery;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBrandsid() {
        return brandsid;
    }

    public void setBrandsid(String brandsid) {
        this.brandsid = brandsid;
    }

    public String getBrandName() {
        return brandname;
    }

    public void setBrandName(String brandname) {
        this.brandname = brandname;
    }

    public String getCodeprovider() {
        return codeprovider;
    }

    public void setCodeprovider(String codeprovider) {
        this.codeprovider = codeprovider;
    }

    public String getProviderInfo() {
        return providerInfo;
    }

    public void setProviderInfo(String providerInfo) {
        this.providerInfo = providerInfo;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String toString(){
        return new Gson().toJson(this);
    }
}
