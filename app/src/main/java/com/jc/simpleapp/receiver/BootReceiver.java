package com.jc.simpleapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jc.simpleapp.service.MainService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i){
        if(i.getAction().equals(i.ACTION_BOOT_COMPLETED)){
            //Launch a boot service after receiving a broadcast of boot completion
            Intent onStartServiceIntent = new Intent(c, MainService.class);
            c.startService(onStartServiceIntent);
        }
    }


}
