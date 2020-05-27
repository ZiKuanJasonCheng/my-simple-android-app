package com.jc.webservice;


import java.util.HashMap;
import java.util.Map;

public enum EMarketplace {
    UNKNOWN("0"), AMAZON("1"), EBAY("2"), BESTBUY("3"), WALMART("4");

    private String value;

    private EMarketplace(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    private static final Map<String, EMarketplace> mapStringToEnum = new HashMap<String, EMarketplace>();
    static {
        for (EMarketplace marketplace : EMarketplace.values()) {
            mapStringToEnum.put(marketplace.value, marketplace);
        }
    }

    public static EMarketplace from(String sMarketplace) {
        return mapStringToEnum.get(sMarketplace) != null ? mapStringToEnum.get(sMarketplace) : UNKNOWN;
    }
}