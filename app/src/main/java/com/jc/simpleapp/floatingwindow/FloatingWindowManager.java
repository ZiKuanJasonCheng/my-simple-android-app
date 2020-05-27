package com.jc.simpleapp.floatingwindow;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.jc.simpleapp.constant.MessageConstant;
import com.jc.simpleapp.R;
import com.dalimao.library.util.FloatUtil;


public class FloatingWindowManager {
    private static final String TAG = "FloatingWindowManager";
    private Context mContext;
    long touchIconStartTime;
    FloatingIcon floatingIcon;

    public FloatingWindowManager(Context c) {
        mContext = c;
    }

    public void showFloatingIcon(Handler handler) {
        if (floatingIcon == null) {
            floatingIcon = new FloatingIcon(mContext, handler);
        }
        floatingIcon.start();
    }

    public void stopFloatingIcon() {
        if (floatingIcon != null) {
            floatingIcon.stop();
            floatingIcon = null;
        }
    }

    public void showFloatingWindowForComparaingPrice(final Handler handler) {

        final FloatingWindowComparePriceInputItemView floatingWindowComparePriceInputItemView = new FloatingWindowComparePriceInputItemView(mContext);

        final EditText edtItem = (EditText) floatingWindowComparePriceInputItemView.findViewById(R.id.edtItem);
        edtItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showFloatMenu(v);
                return true;
            }
        });


        Button btnOK = (Button) floatingWindowComparePriceInputItemView.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtItem.getText().toString().isEmpty()) {
                    stopFloatingWindowForComparaingPrice();
                    Log.d(TAG, "close and show next dialog");
                    //Tell mainService what the user typed
                    Message message = handler.obtainMessage();
                    message.what = MessageConstant.MESSAGE_SEND_SEARCHING_ITEM;
                    message.getData().putString(MessageConstant.KEY_ITEM_NAME, edtItem.getText().toString());
                    handler.sendMessage(message);
                }
            }
        });

        ImageView imgClose = (ImageView) floatingWindowComparePriceInputItemView.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopFloatingWindowForComparaingPrice();
                Log.d(TAG, "close");

            }
        });

        Point startPosition = new Point(200, 150);
        FloatUtil.showSmartFloat(floatingWindowComparePriceInputItemView, Gravity.LEFT | Gravity.TOP, startPosition, null, true, true);

    }

    public void stopFloatingWindowForComparaingPrice() {

        FloatUtil.hideFloatView(mContext, FloatingWindowComparePriceInputItemView.class, false, false);
    }


    private void showFloatMenu(final View v) {
        final PopupMenu popup = new PopupMenu(mContext, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.floating_window_menu, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {


            public boolean onMenuItemClick(MenuItem item) {
                //Get a handle to the clipboard service
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                //Create a new text clip to put on the clipboard
                ClipData clipdata = null;
                String pasteData = "";

                switch (item.getItemId()) {
                    case R.id.copy:
                        Log.d(TAG, "COPY!!");

                        if (v != null) {
                            clipdata = ClipData.newPlainText("Copy a text!!", ((EditText) v).getText().toString());
                        }

                        //Set the clipboard's primary clip
                        clipboard.setPrimaryClip(clipdata);
                        return true;
                    case R.id.paste:
                        Log.d(TAG, "PASTE!!");

                        // Examines the item on the clipboard. If getText() does not return null, the clip item contains the
                        // text. Assumes that this application can only handle one item at a time.
                        ClipData.Item i = clipboard.getPrimaryClip().getItemAt(0);
                        // Gets the clipboard as text.
                        pasteData = i.getText().toString();
                        String result = "";
                        if (v != null) {
                            result = ((EditText) v).getText().toString();
                            ((EditText) v).setText(result + pasteData);
                        }

                        return true;
                    default:
                        return false;
                }
            }
        });

    }


    public void showFloatingWindowAutoPopupTrackPriceView(final Handler handler, final String itemID, final String marketplace, final String itemName, final String currentPrice, final String link) {

        final FloatingWindowAutoPopupTrackPriceView view = new FloatingWindowAutoPopupTrackPriceView(mContext);

        final EditText edtTargetPrice = (EditText) view.findViewById(R.id.edtTargetPrice);

        TextView txtItemName = (TextView) view.findViewById(R.id.txtItemName);
        txtItemName.setText("Item name: "+itemName);

        TextView txtCurrentPrice = (TextView) view.findViewById(R.id.txtCurrentPrice);
        txtCurrentPrice.setText("Current price: "+currentPrice);

        Button btnOK = (Button) view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!edtTargetPrice.getText().toString().isEmpty()){
                stopFloatingWindowAutoPopupTrackPriceView();
                Log.d(TAG, "close and show next dialog");
                //Tell mainService what the user typed
                Message message = handler.obtainMessage();
                message.what = MessageConstant.MESSAGE_SEND_TRACKING_PRICE;
                message.getData().putString(MessageConstant.KEY_ITEM_ID, itemID);
                message.getData().putString(MessageConstant.KEY_ITEM_NAME, itemName);
                message.getData().putString(MessageConstant.KEY_MARKETPLACE, marketplace);
                message.getData().putString(MessageConstant.KEY_CURRENT_PRICE, currentPrice);
                message.getData().putString(MessageConstant.KEY_TARGET_PRICE, edtTargetPrice.getText().toString());
                message.getData().putString(MessageConstant.KEY_ITEM_URL, link);
                handler.sendMessage(message);
            }
        });

        ImageView imgClose = (ImageView) view.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopFloatingWindowAutoPopupTrackPriceView();
                Log.d(TAG, "close");

            }
        });

        Point startPosition = new Point(200, 150);
        FloatUtil.showSmartFloat(view, Gravity.LEFT | Gravity.TOP, startPosition, null, true, true);

    }

    public void stopFloatingWindowAutoPopupTrackPriceView() {
        FloatUtil.hideFloatView(mContext, FloatingWindowAutoPopupTrackPriceView.class, false, false);
    }

    public void stopAll(){
        stopFloatingIcon();
        stopFloatingWindowForComparaingPrice();
        stopFloatingWindowAutoPopupTrackPriceView();
    }


}
