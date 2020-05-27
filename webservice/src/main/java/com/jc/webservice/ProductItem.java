package com.jc.webservice;

public class ProductItem {
    protected EMarketplace marketplace;
    protected String itemId;
    protected String title;
    protected String price;
    protected String url;
    protected String imageUrl;

    public ProductItem(EMarketplace marketplace, String itemId, String title, String price, String url, String imageUrl) {
        this.marketplace = marketplace;
        this.itemId = itemId;
        this.title = title;
        this.price = price;
        this.url = url;
        this.imageUrl = imageUrl;
    }
    public EMarketplace getMarketplace() {
        return marketplace;
    }
    public void setMarketplace(EMarketplace marketplace) {
        this.marketplace = marketplace;
    }
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}