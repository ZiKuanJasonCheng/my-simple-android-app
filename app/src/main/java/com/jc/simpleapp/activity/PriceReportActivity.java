package com.jc.simpleapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.jc.simpleapp.R;
import com.jc.simpleapp.constant.MessageConstant;
import com.jc.simpleapp.object.PriceReportItem;
import com.jc.simpleapp.adapter.PriceReportListAdapter;
import com.jc.simpleapp.util.NotificationManager;
import com.jc.simpleapp.util.PriceReportSQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PriceReportActivity extends Activity{
    private static final String TAG="PriceReportActivity";
    private ListView lvData;
    private TextView txtDate;

    private List<PriceReportItem> resultList=null;
    private PriceReportSQLiteOpenHelper sqLiteOpenHelper = new PriceReportSQLiteOpenHelper(this);
    private PriceReportListAdapter adapter;
    private Handler priceReportActivityHandler  = new Handler() {
        @Override
        public void handleMessage(Message inputMessage) {
            // Gets the task from the incoming Message object.
            switch (inputMessage.what) {
                case MessageConstant.MESSAGE_SEND_DELETING_REPORT_ITEM:
                    String itemID = inputMessage.getData().getString(MessageConstant.KEY_ITEM_ID);
                    String marketPlace = inputMessage.getData().getString(MessageConstant.KEY_MARKETPLACE);
                    sqLiteOpenHelper.deleteReportItem(itemID,marketPlace);
                    break;
            }
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_report);
        Log.d(TAG,"onCreate()");
        initialView();
        NotificationManager.stopPriceChangedNotification(this);
        loadData();
    }

    private void initialView(){
        txtDate = (TextView) findViewById(R.id.txtDate);
        Calendar rightNow = Calendar.getInstance();
        Log.d(TAG,"rightNow="+rightNow.getTime().toString());

        //Show today's date information
        Date date = new Date();
        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();

        String month;
        if( dateFormat.format("MM", date).toString().substring(0,1).equals("0") ){
            month=dateFormat.format("M", date).toString();
        }
        else
            month=dateFormat.format("MM", date).toString();

        String day;
        if( dateFormat.format("dd", date).toString().substring(0,1).equals("0") ){
            day=dateFormat.format("d", date).toString();
        }
        else
            day=dateFormat.format("dd", date).toString();

        String year = dateFormat.format("yyyy", date).toString();

        Log.d(TAG,"dateFormat.format(\"MM\", date).toString().substring(0,1)="+dateFormat.format("MM", date).toString().substring(0,1));
        Log.d(TAG,"dateFormat.format(\"dd\", date).toString().substring(0,1)="+dateFormat.format("dd", date).toString().substring(0,1));

        txtDate.setText(month+"/"+day+"/"+year);
        //https://stackoverflow.com/questions/454315/how-do-you-format-date-and-time-in-android

        lvData=(ListView)findViewById(R.id.lvData);


    }

    protected void onStart(){
        super.onStart();
        Log.d(TAG,"onStart()");
    }


    void loadData(){
        resultList=sqLiteOpenHelper.getReportItemList();
        adapter=new PriceReportListAdapter(this,resultList,priceReportActivityHandler);
        lvData.setAdapter(adapter);

    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG,"onDestroy()");

        for(int i=0;i<resultList.size();i++){
            sqLiteOpenHelper.updateReportItem(resultList.get(i));
        }
    }

}
