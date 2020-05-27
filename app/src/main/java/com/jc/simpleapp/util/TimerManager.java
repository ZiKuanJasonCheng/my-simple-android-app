package com.jc.simpleapp.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jc.simpleapp.constant.GeneralConstant;
import com.jc.simpleapp.constant.MessageConstant;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jasoncheng on 6/14/2017.
 */

public class TimerManager {
    private static final String TAG = "TimerManager";
    private Context mContext;
    private Timer timer;


    public TimerManager(Context c){
        timer=new Timer();
        mContext=c;
    }

    public void setUpTimerForCheckingPrice(final Handler handler){
        Log.d(TAG,"setUpTimerForCheckingPrice():");
        final WeakReference<Handler> handlerWeakReference = new WeakReference<Handler>(handler);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG,"timer: time's up!!");

                //Send a handler to mainService to check item prices from reportItemList
                sendMessageToMainService(handlerWeakReference, MessageConstant.MESSAGE_SEND_CHECKING_PRICE);

                //testing: showPriceChangedNotification
                NotificationManager.showPriceChangedNotification(mContext);
            }
        }, GeneralConstant.DURATION_PRICE_CHECKING,GeneralConstant.DURATION_PRICE_CHECKING);


    }

    private void sendMessageToMainService(WeakReference<Handler> reference, final int messageConstant){
        if (reference != null) {
            Handler handler = reference.get();
            if (handler != null) {
                Message message = handler.obtainMessage();
                message.what = messageConstant;
                handler.sendMessage(message);
            }
        }


    }

    public void stopTimer(){
        timer.cancel();
    }
}
