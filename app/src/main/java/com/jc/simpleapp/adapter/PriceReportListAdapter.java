package com.jc.simpleapp.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import com.jc.simpleapp.R;
import com.jc.simpleapp.constant.MessageConstant;
import com.jc.simpleapp.object.PriceReportItem;

public class PriceReportListAdapter extends BaseAdapter {
    private static final String TAG="PriceReportListAdapter";
    private LayoutInflater layoutInflater;
    private List<PriceReportItem> reportItemList = null;   //Report item List
    private Handler mHandler;

    public PriceReportListAdapter(Context c, List<PriceReportItem> itemList, Handler handler) {
        layoutInflater = LayoutInflater.from(c);
        reportItemList = itemList;
        mHandler=handler;
    }


    @Override
    public int getCount() {
        return reportItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.reportItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.price_report_item, null);

            holder = new ViewHolder();
            holder.txtProduct = (TextView) convertView.findViewById(R.id.txtItemName);
            holder.txtCurrentPrice = (TextView) convertView.findViewById(R.id.txtCurrentPrice);
            holder.edtTargetPrice = (EditText) convertView.findViewById(R.id.edtTargetPrice);
            holder.txtLink = (TextView) convertView.findViewById(R.id.txtLink);
            holder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PriceReportItem reportItemInfo = this.reportItemList.get(position);
        float priceDropPercentage = 0;
        String string_priceDropPercentage="";

        try{
            float originalPrice = Float.parseFloat(reportItemInfo.getOrginalPrice());
            float currentPrice = Float.parseFloat(reportItemInfo.getCurrentPrice());
            priceDropPercentage = (1 - currentPrice / originalPrice) * 100;

            if(priceDropPercentage<0.1){
                string_priceDropPercentage = "0";
            }
            else{
                string_priceDropPercentage=String.format("%.1f", priceDropPercentage);
            }
        }
        catch(NumberFormatException e){

        }


        holder.txtProduct.setText("Product: "+reportItemInfo.getProductName());
        holder.txtCurrentPrice.setText("Current Price: $"+reportItemInfo.getCurrentPrice() + " (" + string_priceDropPercentage + "% Drop)");
        holder.edtTargetPrice.setText(reportItemInfo.getTargetPrice());
        holder.txtLink.setText("Link: "+reportItemInfo.getLink());


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemID = reportItemList.get(position).getId();
                String marketplace = reportItemList.get(position).getMarketplace();
                reportItemList.remove(position);
                Message message = mHandler.obtainMessage();
                message.getData().putString(MessageConstant.KEY_ITEM_ID,itemID);
                message.getData().putString(MessageConstant.KEY_MARKETPLACE,marketplace);
                message.what = MessageConstant.MESSAGE_SEND_DELETING_REPORT_ITEM;
                mHandler.sendMessage(message);
                notifyDataSetChanged();
            }
        });

        holder.edtTargetPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG,"onFocusChange");
                reportItemList.get(position).setTargetPrice(s.toString());
                Log.d(TAG,"getTargetPrice"+reportItemList.get(position).getTargetPrice());
            }
        });


        return convertView;
    }

    private class ViewHolder {
        public TextView txtProduct;
        public TextView txtCurrentPrice;
        public EditText edtTargetPrice;
        public TextView txtLink;
        public Button btnDelete;
    }


}
