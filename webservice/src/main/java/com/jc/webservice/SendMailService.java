package com.jc.webservice;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class SendMailService {
    private static final String SEND_MAIL_SERVICE_URI = "http://apitest.alvosecure.com/SendMailService/";
    protected final AsyncHttpClient asyncHttpClient;

    private SendMailService() {
        asyncHttpClient = new AsyncHttpClient();
    }

    private static class SingletonHelper {
        private static final SendMailService INSTANCE = new SendMailService();
    }

    public static SendMailService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void sendMail(String mailAddress, List<Map<String, String>> itemList) {
        if (itemList != null) {
            final String API_NAME = "SendMail";
            final String uri = SEND_MAIL_SERVICE_URI + API_NAME;
            RequestParams params = new RequestParams();
            params.put("userMailAddress", mailAddress);
            params.put("itemList", itemList);
            asyncHttpClient.get(uri, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                    Log.i("sendMail", "onStart URI:" + getRequestURI());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    Log.i("sendMail", "onSuccess");

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.i("sendMail", "onFailure status:" + statusCode);
                }
            });
        }
    }
}
