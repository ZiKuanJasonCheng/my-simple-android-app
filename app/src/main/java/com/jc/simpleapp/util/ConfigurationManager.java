package com.jc.simpleapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;


public class ConfigurationManager {
    private static final String SHARED_PREFERENCE_NAME ="config";
    private static final String KEY_INITIAL_STATE ="initialState";
    private static final String KEY_FLOATING_ICON ="floatingIcon";
    private static final String KEY_PRICE_DROP_PERCENTAGE ="priceDropPercentage";
    private static final String KEY_SEND_EMAIL ="sendEmail";
    private static final String KEY_USER_EMAIL ="userEmail";
    private static final String KEY_TIME_RECORD_FOR_SENDING_MAILS ="timeRecordForSendingMails";

    private ConfigurationManager(){
    }

    public static boolean getInitialState(Context c){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        boolean priceDropPercentage=config.getBoolean(KEY_INITIAL_STATE,true);
        return priceDropPercentage;
    }

    public static void setInitialState(Context c, boolean initialState){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        config.edit().putBoolean(KEY_INITIAL_STATE,initialState).apply();
    }

    public static boolean getShowFloatingIcon(Context c){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        boolean allowFloatingIcon=config.getBoolean(KEY_FLOATING_ICON,true);
        return allowFloatingIcon;
    }

    public static void setShowFloatingIcon(Context c, boolean allowFloatingIcon){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        config.edit().putBoolean(KEY_FLOATING_ICON,allowFloatingIcon).apply();
    }

    public static int getPriceDropPercentage(Context c){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        int priceDropPercentage=config.getInt(KEY_PRICE_DROP_PERCENTAGE,5);
        return priceDropPercentage;
    }

    public static void setPriceDropPercentage(Context c, int priceDropPercentage){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        config.edit().putInt(KEY_PRICE_DROP_PERCENTAGE,priceDropPercentage).apply();
    }

    public static boolean getAllowEmail(Context c){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        boolean allowEmail=config.getBoolean(KEY_SEND_EMAIL,true);
        return allowEmail;
    }

    public static void setAllowEmail(Context c, boolean allowEmail){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        config.edit().putBoolean(KEY_SEND_EMAIL,allowEmail).apply();
    }

    public static String getUserEmail(Context c){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        String userEmail=config.getString(KEY_USER_EMAIL,"");
        return userEmail;
    }

    public static void setUserEmail(Context c, String userEmail){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        config.edit().putString(KEY_USER_EMAIL,userEmail).apply();
    }


    public static long getTimeRecordForSendingMails(Context c){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        long timeRecordForSendingMails=config.getLong(KEY_TIME_RECORD_FOR_SENDING_MAILS,0);
        return timeRecordForSendingMails;
    }

    public static void setTimeRecordForSendingMails(Context c, long timeRecord){
        SharedPreferences config = c.getSharedPreferences(SHARED_PREFERENCE_NAME,MODE_PRIVATE);
        config.edit().putLong(KEY_TIME_RECORD_FOR_SENDING_MAILS,timeRecord).apply();
    }
}
