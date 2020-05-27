package com.jc.simpleapp.util;

import android.util.Log;

import com.jc.simpleapp.object.PriceReportItem;
import com.jc.webservice.SendMailService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jasoncheng on 6/2/2017.
 */

public class MailSender {
    private static final String TAG = "sendMail";

    private MailSender(){

    }

    public static void sendMailToClient(String receiverMail, List<PriceReportItem> list) {
        List<Map<String, String>> listOfMaps = new ArrayList<Map<String, String>>();
            //Map<String, String> data = new HashMap<String, String>();

            Log.d(TAG,"sendMailToClient");

            for(int i=0;i<list.size();i++){
                Log.d(TAG,"cool!!");
                Log.d(TAG,"reportItemList.get(i).getProductName()="+list.get(i).getProductName());
                Log.d(TAG,"reportItemList.get(i).getCurrentPrice()="+list.get(i).getCurrentPrice());
                Log.d(TAG,"reportItemList.get(i).getTargetPrice()="+list.get(i).getTargetPrice());
                Log.d(TAG,"reportItemList.get(i).getLink()="+list.get(i).getLink());
                Map<String, String> data = new HashMap<String, String>();
                data.put("name", list.get(i).getProductName());
                data.put("currentPrice", list.get(i).getCurrentPrice());
                data.put("targetPrice", list.get(i).getTargetPrice());
                data.put("link", list.get(i).getLink());
                listOfMaps.add(data);
            }


        Log.d(TAG,"thread.id="+Thread.currentThread().getId());
        SendMailService.getInstance().sendMail(receiverMail, listOfMaps);

    }


}
