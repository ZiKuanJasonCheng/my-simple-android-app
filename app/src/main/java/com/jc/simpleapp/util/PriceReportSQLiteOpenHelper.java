package com.jc.simpleapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.jc.simpleapp.object.PriceReportItem;

import java.util.ArrayList;
import java.util.List;


public class PriceReportSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "OpenHelper";
    private static final String DATABASE_NAME = "inputDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "itemList";
    private static final String COLUMN_NAME_ITEMID = "itemID";
    private static final String COLUMN_NAME_MARKETPLACE = "marketplace";
    private static final String COLUMN_NAME_ITEMNAME = "name";
    private static final String COLUMN_NAME_ORIGINALPRICE = "originalPrice";
    private static final String COLUMN_NAME_CURRENTPRICE = "currentPrice";
    private static final String COLUMN_NAME_TARGETPRICE = "targetPrice";
    private static final String COLUMN_NAME_LINK = "link";
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    BaseColumns._ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_ITEMID + " TEXT NOT NULL," +
                    COLUMN_NAME_MARKETPLACE + " TEXT NOT NULL," +
                    COLUMN_NAME_ITEMNAME + " TEXT NOT NULL," +
                    COLUMN_NAME_ORIGINALPRICE + " TEXT NOT NULL," +
                    COLUMN_NAME_CURRENTPRICE + " TEXT NOT NULL," +
                    COLUMN_NAME_TARGETPRICE + " TEXT NOT NULL," +
                    COLUMN_NAME_LINK + " TEXT NOT NULL)";
                    //No need to write "CREATE TABLE IF NOT EXISTS"
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public PriceReportSQLiteOpenHelper(Context context/*, String name, SQLiteDatabase.CursorFactory factory, int version*/) {
        super(context,  DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    /**
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */

    public long addReportItem(PriceReportItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String queryString = "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_NAME_ITEMID+" = ? AND "+COLUMN_NAME_MARKETPLACE+" = ?";
        SQLiteDatabase db2 = this.getReadableDatabase();
        Cursor cursor = db2.rawQuery(queryString, new String[] {item.getId(), item.getMarketplace()});
        Log.d(TAG,"cursor.getCount()="+cursor.getCount());

        if(cursor.getCount()==0){
            values.put(COLUMN_NAME_ITEMID, item.getId());
            values.put(COLUMN_NAME_MARKETPLACE, item.getMarketplace());
            values.put(COLUMN_NAME_ITEMNAME, item.getProductName());
            values.put(COLUMN_NAME_ORIGINALPRICE, item.getOrginalPrice());
            values.put(COLUMN_NAME_CURRENTPRICE, item.getCurrentPrice());
            values.put(COLUMN_NAME_TARGETPRICE, item.getTargetPrice());
            values.put(COLUMN_NAME_LINK, item.getLink());
            long id = db.insert(TABLE_NAME, null, values);
            return id;
        }
        else{
            updateReportItem(item);
            cursor.moveToFirst();
            return cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        }

    }


    public int deleteReportItem(String itemID, String marketPlace){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_NAME_ITEMID+" =  ? AND "+COLUMN_NAME_MARKETPLACE+" = ?";
        return db.delete(TABLE_NAME, whereClause, new String[] {itemID, marketPlace});
    }

    public PriceReportItem getReportItem(String itemId, String itemMarketplace){
        PriceReportItem reportItem=null;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_NAME_ITEMID+" = ? AND "+COLUMN_NAME_MARKETPLACE+" = ?" ;
        Cursor cursor = db.rawQuery(queryString, new String[]{itemId,itemMarketplace});
        if(cursor.moveToFirst()){
            reportItem = new PriceReportItem(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ITEMID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MARKETPLACE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ITEMNAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ORIGINALPRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CURRENTPRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TARGETPRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LINK)));
        }

        return reportItem;
    }


    public List<PriceReportItem> getReportItemList(){
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = db.rawQuery(queryString, null);
        List<PriceReportItem> reportItemList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                PriceReportItem item = new PriceReportItem(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ITEMID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_MARKETPLACE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ITEMNAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ORIGINALPRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CURRENTPRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TARGETPRICE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LINK)));

                    reportItemList.add(item);
            }
            while(cursor.moveToNext());
        }
        else{

        }
        return reportItemList;
    }


    public int updateReportItem(PriceReportItem item){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ITEMID, item.getId());
        values.put(COLUMN_NAME_MARKETPLACE, item.getMarketplace());
        values.put(COLUMN_NAME_ITEMNAME, item.getProductName());
        values.put(COLUMN_NAME_ORIGINALPRICE, item.getOrginalPrice());
        values.put(COLUMN_NAME_CURRENTPRICE, item.getCurrentPrice());
        values.put(COLUMN_NAME_TARGETPRICE, item.getTargetPrice());
        values.put(COLUMN_NAME_LINK, item.getLink());

        String whereClause = COLUMN_NAME_ITEMID+" =  ? AND "+COLUMN_NAME_MARKETPLACE+" = ?";

        return db.update(TABLE_NAME, values, whereClause, new String[] {item.getId(), item.getMarketplace()});
    }

}
