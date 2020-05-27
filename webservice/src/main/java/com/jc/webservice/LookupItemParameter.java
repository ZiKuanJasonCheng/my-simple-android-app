package com.jc.webservice;


public class LookupItemParameter {
    private EMarketplace marketplace;
    private String itemId;

    public LookupItemParameter(EMarketplace marketplace, String itemId) {
        this.marketplace = marketplace;
        this.itemId = itemId;
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
}
