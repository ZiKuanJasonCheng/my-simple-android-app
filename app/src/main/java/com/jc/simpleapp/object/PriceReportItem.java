package com.jc.simpleapp.object;

/**
 * Created by jasoncheng on 5/19/2017.
 */

public class PriceReportItem {

    public PriceReportItem(String itemID, String marketplace){
        this.itemID = itemID;
        this.marketplace = marketplace;
    }

    public PriceReportItem(String id, String marketplace, String productName, String orginalPrice, String currentPrice, String targetPrice, String link) {
        this.itemID = id;
        this.marketplace = marketplace;
        this.productName = productName;
        this.orginalPrice = orginalPrice;
        this.currentPrice = currentPrice;
        this.targetPrice = targetPrice;
        this.link = link;
    }

    private String itemID;
    private String marketplace;
    private String productName;
    private String orginalPrice;
    private String currentPrice;
    private String targetPrice;
    private String link;

    public String getId() {
        return itemID;
    }

    public void setId(String id) {
        this.itemID = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }


    public String getTargetPrice() {
        return targetPrice;
    }

    public void setTargetPrice(String targetprice) {
        this.targetPrice = targetprice;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(String marketplace) {
        this.marketplace = marketplace;
    }

    public String getOrginalPrice() {
        return orginalPrice;
    }

    public void setOrginalPrice(String orginalPrice) {
        this.orginalPrice = orginalPrice;
    }
}
