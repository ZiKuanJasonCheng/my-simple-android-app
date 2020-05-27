package com.jc.simpleapp.floatingwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.jc.simpleapp.R;


public class FloatingWindowComparePriceInputItemView extends FrameLayout{
    public FloatingWindowComparePriceInputItemView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.floating_window_compare_price_input_item, this);
    }
}
