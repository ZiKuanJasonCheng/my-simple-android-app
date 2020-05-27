package com.jc.simpleapp.accessibility;

/*import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;*/
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends android.accessibilityservice.AccessibilityService {

    final static String TAG = "MyAccessibilityService";  //20200506
    private UICaptor uiCaptor;

    //Receive an event initor

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        uiCaptor.monitor(event);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        uiCaptor = new UICaptor(getApplicationContext());
    }
}