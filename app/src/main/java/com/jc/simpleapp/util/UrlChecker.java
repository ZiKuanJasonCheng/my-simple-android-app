package com.jc.simpleapp.util;

import android.util.Log;

import com.jc.simpleapp.object.PriceReportItem;
import com.jc.webservice.EMarketplace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by jasoncheng on 5/8/2017.
 */

public class UrlChecker {
    private static final String TAG="UrlChecker";
    static String url;

    public void setURL(String inputURL){
        url=inputURL;
    }

    public static PriceReportItem checkURL(String url){
        PriceReportItem item = null;
        String itemID="", marketplace;
        if(url!=null) {
            Log.d(TAG,"checkURL: url!=null url="+url);
            //If the url is an item page of a specific website, then show a popup window
            if ( url.contains("amazon") && ( url.contains("/dp/") || url.contains("/gp/") ) ){ //手機版網址也要檢查
                Pattern p = Pattern.compile("/d/([\\w]+)/");
                Matcher m = p.matcher(url);

                if(m.find() && m.groupCount()==1){
                    Log.d(TAG,"m.group(1)="+m.group(1));

                    itemID = m.group(1);
                    item = new PriceReportItem(itemID, EMarketplace.AMAZON.getValue());
                    return item;
                }

            }
            else if (url.contains("ebay") && url.contains("/itm/") ){
                //For phone website
                Pattern p = Pattern.compile("-/([\\d]+)?");
                Matcher m = p.matcher(url);

                //For PC website
                Pattern p2 = Pattern.compile("/([\\d]+)");
                Matcher m2 = p2.matcher(url);

                if(m.find() || m2.find()){
                    if(m.groupCount()==1){
                        Log.d(TAG,"m.group(1)="+m.group(1));

                        itemID = m.group(1);
                        item = new PriceReportItem(itemID, EMarketplace.EBAY.getValue());
                        return item;
                    }
                    else if(m2.groupCount()==1){
                        Log.d(TAG,"m2.group(1)="+m2.group(1));

                        itemID = m2.group(1);
                        item = new PriceReportItem(itemID, EMarketplace.EBAY.getValue());
                        return item;
                    }
                }

            }
            else if (url.contains("bestbuy") && url.contains("skuId")){
                Pattern p = Pattern.compile("skuId=([\\d]+)");
                Matcher m = p.matcher(url);

                if(m.find() && m.groupCount()==1){
                    Log.d(TAG,"m.group(1)="+m.group(1));

                    itemID = m.group(1);
                    item = new PriceReportItem(itemID, EMarketplace.BESTBUY.getValue());
                    return item;
                }

            }
            else if (url.contains("walmart") && url.contains("ip")){
                Pattern p = Pattern.compile("/([\\d]+)");
                Matcher m = p.matcher(url);

                if(m.find() && m.groupCount()==1){
                    Log.d(TAG,"m.group(1)="+m.group(1));

                    itemID = m.group(1);
                    item = new PriceReportItem(itemID, EMarketplace.WALMART.getValue());
                    return item;
                }
            }
        }
        return null;
    }

}
