package com.jc.webservice;


import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionService {
    private static final String SERVICE_URI = "http://completion.amazon.com/search/complete";
    protected final AsyncHttpClient asyncHttpClient;
    protected int requestIdCount;

    private SearchSuggestionService() {
        asyncHttpClient = new AsyncHttpClient();
        requestIdCount = 0;
    }

    private static class SingletonHelper {
        private static final SearchSuggestionService INSTANCE = new SearchSuggestionService();
    }

    public static SearchSuggestionService getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private synchronized int getRequestId() {
        requestIdCount += 1;
        if (requestIdCount < 0) {
            requestIdCount = 1;
        }
        return requestIdCount;
    }

    public int getSearchSuggestion(SearchSeggestionHandler context, String keyword) {
        if (context != null && keyword != null) {
            final WeakReference<SearchSeggestionHandler> contextReference = new WeakReference<SearchSeggestionHandler>(context);
            final int requestId = getRequestId();
            RequestParams params = new RequestParams();
            params.put("method", "completion");
            params.put("client", "amazon-search-ui");
            params.put("mkt", "1");
            params.put("search-alias", "aps");
            params.put("q", keyword);
            asyncHttpClient.get(SERVICE_URI, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                    Log.i("getSearchSuggestion", "onStart");
                    Log.i("getSearchSuggestion", "URI:" + getRequestURI());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    Log.i("getSearchSuggestion", "onSuccess");

                    String sResponse = new String(response, StandardCharsets.UTF_8);
                    Log.i("getSearchSuggestion", "sResponse: " + sResponse);
                    try {
                        JsonArray jsonArrayResponse = new JsonParser().parse(sResponse).getAsJsonArray();
                        Log.i("getSearchSuggestion", "jsonArrayResponse size: " + jsonArrayResponse.size());
                        if (jsonArrayResponse.size() == 5) {
                            String sKeyword = jsonArrayResponse.get(0).getAsString();
                            Log.i("getSearchSuggestion", "sKeyword: " + sKeyword);
                            Log.i("getSearchSuggestion", "jsonArray.get(1): " + jsonArrayResponse.get(1));
                            JsonArray jsonArraySuggestionList = jsonArrayResponse.get(1).getAsJsonArray();

                            List<String> suggestionList = new ArrayList<String>();
                            for (int indexSuggestionList = 0; indexSuggestionList < jsonArraySuggestionList.size() && indexSuggestionList < 5; indexSuggestionList++) {
                                suggestionList.add(jsonArraySuggestionList.get(indexSuggestionList).getAsString());
                            }
                            if (contextReference != null) {
                                SearchSeggestionHandler context = contextReference.get();
                                if (context != null) {
                                    context.onGetSearchSeggestionSuccess(requestId,suggestionList);
                                }
                            }
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.i("getSearchSuggestion", "onFailure status:" + statusCode);
                }
            });
            return requestId;
        }
        return -1;
    }
}
