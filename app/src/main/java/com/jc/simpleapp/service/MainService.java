package com.jc.simpleapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.app.Service;
import android.os.Message;

import android.util.Log;

import com.jc.simpleapp.activity.PriceReportActivity;
import com.jc.simpleapp.constant.BroadcastActionConstant;
import com.jc.simpleapp.constant.GeneralConstant;
import com.jc.simpleapp.constant.MessageConstant;
import com.jc.simpleapp.object.PriceReportItem;
import com.jc.simpleapp.util.PriceReportSQLiteOpenHelper;
import com.jc.simpleapp.util.ConfigurationManager;
import com.jc.simpleapp.util.MailSender;
import com.jc.simpleapp.util.NotificationManager;
import com.jc.simpleapp.floatingwindow.FloatingWindowManager;
import com.jc.simpleapp.util.TimerManager;
import com.jc.simpleapp.util.UrlChecker;
import com.jc.webservice.MyWebservice;
import com.jc.webservice.EMarketplace;
import com.jc.webservice.LookupItemHandler;
import com.jc.webservice.LookupItemParameter;
import com.jc.webservice.ProductItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainService extends Service implements LookupItemHandler {
    private static final String TAG = "MainService";
    private FloatingWindowManager floatingWindowManager;
    IBinder binder = new MainServiceBinder();
    private PriceReportSQLiteOpenHelper openHelper;
    private TimerManager timerManager;
    private int checkPriceRequestId = -1;
    private int popUpWindowRequestId = -1;
    private Handler mainServiceHandler = new Handler() {
        @Override
        public void handleMessage(Message inputMessage) {
            // Gets the task from the incoming Message object.
            switch (inputMessage.what) {
                case MessageConstant.MESSAGE_SEND_SEARCHING_ITEM:
                    String item = inputMessage.getData().getString(MessageConstant.KEY_ITEM_NAME);
                    searchItem(item);
                    Log.d(TAG, "item=" + item);
                    break;
                case MessageConstant.MESSAGE_TOUCH_FLOATING_ICON:
                    Log.d(TAG, "FloatingIcon is clicked");
                    floatingWindowManager.showFloatingWindowForComparaingPrice(mainServiceHandler);
                    break;
                case MessageConstant.MESSAGE_SEND_TRACKING_PRICE:

                    String itemID = inputMessage.getData().getString(MessageConstant.KEY_ITEM_ID);
                    String marketplace = inputMessage.getData().getString(MessageConstant.KEY_MARKETPLACE);
                    String itemName = inputMessage.getData().getString(MessageConstant.KEY_ITEM_NAME);
                    String originalPrice = inputMessage.getData().getString(MessageConstant.KEY_CURRENT_PRICE);
                    String currentPrice = inputMessage.getData().getString(MessageConstant.KEY_CURRENT_PRICE);
                    String targetPrice = inputMessage.getData().getString(MessageConstant.KEY_TARGET_PRICE);
                    String link = inputMessage.getData().getString(MessageConstant.KEY_ITEM_URL);

                    searchItem(itemName);
                    storeItemToDB(itemID, marketplace, itemName, originalPrice, currentPrice, targetPrice, link);
                    break;
                case MessageConstant.MESSAGE_SEND_CHECKING_PRICE:
                    //Check update of price reports. If there's any change, send notification
                    getItemUpdated();

                    break;
            }
        }


    };

    private BroadcastReceiver mainServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BroadcastActionConstant.FOREGROUND_NEED_HELP_NOTIFICATION_ONCLICK:
                    floatingWindowManager.showFloatingWindowForComparaingPrice(mainServiceHandler);
                    break;
                case BroadcastActionConstant.FOREGROUND_PRICE_CHANGED_NOTIFICATION_ONCLICK:
                    Log.d(TAG,"FOREGROUND_PRICE_CHANGED_NOTIFICATION_ONCLICK");
                    startActivity(new Intent(MainService.this,PriceReportActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case BroadcastActionConstant.CONFIGURATION_UPDATED:
                    onConfigurationUpdated();
                    break;
                case BroadcastActionConstant.MONITOR_URL_UPDATED:
                    onURLUpdated(intent.getExtras().getString(BroadcastActionConstant.KEY_URL));
                    break;
                case BroadcastActionConstant.MONITOR_SEARCH_NEW_ITEM:
                    searchItem(intent.getExtras().getString(BroadcastActionConstant.KEY_SEARCH));
                    break;
            }
        }
    };

    protected void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastActionConstant.FOREGROUND_NEED_HELP_NOTIFICATION_ONCLICK);
        intentFilter.addAction(BroadcastActionConstant.FOREGROUND_PRICE_CHANGED_NOTIFICATION_ONCLICK);
        intentFilter.addAction(BroadcastActionConstant.CONFIGURATION_UPDATED);
        intentFilter.addAction(BroadcastActionConstant.MONITOR_URL_UPDATED);
        intentFilter.addAction(BroadcastActionConstant.MONITOR_SEARCH_NEW_ITEM);
        registerReceiver(mainServiceBroadcastReceiver, intentFilter);
    }

    protected void unregisterBroadcastReceiver() {
        unregisterReceiver(mainServiceBroadcastReceiver);
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.d(TAG, "onCreate()");
        floatingWindowManager = new FloatingWindowManager(this);
        openHelper = new PriceReportSQLiteOpenHelper(this);
        // floatingWindowManager.showFloatingWindowAutoPopupTrackPriceView(mainServiceHandler,"Galaxy J7", "007", "50.99", "2");

        registerBroadcastReceiver();
        NotificationManager.showForegroundNotification(this);
        confirmStartingFloatingIcon();
        initialTimer();
    }


    @Override
    public void onDestroy() {
        stopTimer();
        floatingWindowManager.stopAll();
        NotificationManager.stopAllNotification(this);
        unregisterBroadcastReceiver();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onLookupItemSuccess(int requestId, List<ProductItem> listProductItem) {
        Log.d(TAG, "lookupItem success id: " + requestId);
        if (requestId == checkPriceRequestId) {
            //Check if items of listProductItem are updated and then store to database
            processItemPriceUpdated(listProductItem);
        }
        else if (requestId == popUpWindowRequestId) {
            if (listProductItem.size() == 1) {
                floatingWindowManager.showFloatingWindowAutoPopupTrackPriceView(mainServiceHandler, listProductItem.get(0).getItemId(), listProductItem.get(0).getMarketplace().getValue(), listProductItem.get(0).getTitle(), listProductItem.get(0).getPrice(), listProductItem.get(0).getUrl());
                Log.d(TAG, "listProductItem.get(0).getItemId()=" + listProductItem.get(0).getItemId());
                Log.d(TAG, "listProductItem.get(0).getMarketplace()=" + listProductItem.get(0).getMarketplace());
            }
        }

    }

    private void processItemPriceUpdated(List<ProductItem> listProductItem) {
        //List<PriceReportItem> reportItemList=openHelper.getReportItemList();
        boolean isChanged = false;

        for (int i = 0; i < listProductItem.size(); i++) {
            String itemId = listProductItem.get(i).getItemId();
            String itemMarketplace = listProductItem.get(i).getMarketplace().getValue();

            PriceReportItem dbReportItem = openHelper.getReportItem(itemId, itemMarketplace);

            if (dbReportItem != null) {
                String productName = listProductItem.get(i).getTitle();
                String originalPrice = dbReportItem.getOrginalPrice();
                String currentPrice = listProductItem.get(i).getPrice();
                String targetPrice = dbReportItem.getTargetPrice();
                String link = listProductItem.get(i).getUrl();


                if(!currentPrice.isEmpty()){
                    PriceReportItem updatedItem = new PriceReportItem(itemId, itemMarketplace, productName, originalPrice, currentPrice, targetPrice ,link);
                    openHelper.updateReportItem(updatedItem);
                }

                //Compare if item price is changed
                try{
                    if(Float.parseFloat(listProductItem.get(i).getPrice()) < Float.parseFloat(dbReportItem.getCurrentPrice())) {
                        //Mark the flag as true
                        isChanged=true;
                    }
                }
                catch(NumberFormatException e){

                }


            }

        }

        if(isChanged){
            //Show a notification
            NotificationManager.showPriceChangedNotification(this);
        }

        long timeRecordForSendingMails = ConfigurationManager.getTimeRecordForSendingMails(this);
        long currentTime = (new Date()).getTime();
        //Check time record of last time when sending a mail
        if(currentTime-timeRecordForSendingMails >= GeneralConstant.DURATION_SENDING_MAIL){
            //Send an email immediately
            sendMail();

            //Update time record to current time
            ConfigurationManager.setTimeRecordForSendingMails(this, currentTime);
        }

    }

    @Override
    public void onLookupItemFailure(int requestId) {
        Log.d(TAG, "lookupItem failure id: " + requestId);
    }


    private void initialTimer() {
        Log.d(TAG,"initialTimer():");
        timerManager = new TimerManager(MainService.this);
        timerManager.setUpTimerForCheckingPrice(mainServiceHandler);
    }

    private void stopTimer() {
        Log.d(TAG,"stopTimer():");
        timerManager.stopTimer();
        timerManager = null;
    }

    //Practice my binder
    public class MainServiceBinder extends Binder {
        public MainService getServiceInstance() {   //Get an instance of MainService
            return MainService.this;
        }
    }

    public void onURLUpdated(String inputURL) {
        Log.d(TAG, "onURLUpdated: inputURL=" + inputURL);
        PriceReportItem priceReportItem = UrlChecker.checkURL(inputURL);
        if (priceReportItem != null) {
            //Launch a web service, get item name, itemID, currentPrice, and website url
            List<LookupItemParameter> list = new ArrayList<LookupItemParameter>();
            list.add(new LookupItemParameter(EMarketplace.from(priceReportItem.getMarketplace()), priceReportItem.getId()));
            popUpWindowRequestId = MyWebservice.getInstance().lookupItem(this, list);
            Log.d(TAG, "lookupItem start id: " + popUpWindowRequestId);
        }
    }

    private void searchItem(String item) {
        //KH

    }

    private void storeItemToDB(String itemID, String marketplace, String itemName, String originalPrice, String currentPrice, String targetPrice, String Url) {
        PriceReportItem item = new PriceReportItem(itemID, marketplace, itemName, originalPrice, currentPrice, targetPrice, Url);
        openHelper.addReportItem(item);
    }


    private void onConfigurationUpdated() {
        confirmStartingFloatingIcon();
        Log.d(TAG, "onConfigurationUpdated()");
    }

    private void confirmStartingFloatingIcon() {
        //Check if cbIcon is checked, and then start up a floating icon
        if (ConfigurationManager.getShowFloatingIcon(MainService.this)) {
            floatingWindowManager.showFloatingIcon(mainServiceHandler);
        } else
            floatingWindowManager.stopFloatingIcon();
    }

    private void getItemUpdated() {
        List<PriceReportItem> listFromDB = openHelper.getReportItemList();
        List<LookupItemParameter> list = new ArrayList<LookupItemParameter>();

        //Classify marketplace: Amazon, e-Bay, Best Buy, and Walmart
        //EMarketplace arrEMarketplace[] = {EMarketplace.AMAZON, EMarketplace.EBAY, EMarketplace.BESTBUY, EMarketplace.WALMART};

        for (int i = 0; i < listFromDB.size(); i++) {
            list.add(new LookupItemParameter(EMarketplace.from(listFromDB.get(i).getMarketplace()), listFromDB.get(i).getId()));
        }

        if(!list.isEmpty()){
            checkPriceRequestId = MyWebservice.getInstance().lookupItem(MainService.this, list);
        }

    }

    private void sendMail() {
        Log.d(TAG, "sendMail()");
        String clientMailAddress = ConfigurationManager.getUserEmail(this);
        List<PriceReportItem> list = openHelper.getReportItemList();

        if (!clientMailAddress.equals("") && !list.isEmpty()) {
            MailSender.sendMailToClient(clientMailAddress, list);
        }

    }
}


