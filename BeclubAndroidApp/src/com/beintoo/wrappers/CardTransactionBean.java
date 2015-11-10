package com.beintoo.wrappers;

import java.util.Date;

public class CardTransactionBean {

    private String description; //get from related mission
    private String brandName;
    private String brandAddress;

    private Date date;

    private String status;

    private Double moneySpent;

    private Double bedollars;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the brandName
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * @param brandName the brandName to set
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * @return the brandAddress
     */
    public String getBrandAddress() {
        return brandAddress;
    }

    /**
     * @param brandAddress the brandAddress to set
     */
    public void setBrandAddress(String brandAddress) {
        this.brandAddress = brandAddress;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the moneySpent
     */
    public Double getMoneySpent() {
        return moneySpent;
    }

    /**
     * @param moneySpent the moneySpent to set
     */
    public void setMoneySpent(Double moneySpent) {
        this.moneySpent = moneySpent;
    }

    /**
     * @return the bedollars
     */
    public Double getBedollars() {
        return bedollars;
    }

    /**
     * @param bedollars the bedollars to set
     */
    public void setBedollars(Double bedollars) {
        this.bedollars = bedollars;
    }
}
