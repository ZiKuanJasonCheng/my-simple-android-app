package com.jc.simpleapp.floatingwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.jc.simpleapp.R;


public class FloatingWindowAutoPopupTrackPriceView extends FrameLayout{
    public FloatingWindowAutoPopupTrackPriceView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.floating_window_auto_popup_track_price, this);
    }
}
