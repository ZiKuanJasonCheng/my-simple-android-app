package com.jc.simpleapp.accessibility;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jc.simpleapp.constant.BroadcastActionConstant;
import com.jc.simpleapp.service.MainService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UICaptor {
    final static String TAG = "UICaptor";
    private String sLastSearch = "";
    private String sLastUrl = "";
    private Date date;
    private MainService mainService;
    private Context mContext;

    private List<String> analyzeUrlPatternList = Arrays.asList(
            "(?:https?://)?(?:www\\.)?google\\.[^/]*/(?:.*\\?|#)(?:.*&)?q=([^&]*).*",
            "(?:https?://)?(?:[^/]*.)?bing\\.com/.*\\?(?:.*&)?q=([^&]*).*",
            "(?:https?://)?(?:[^/]*.)?yahoo\\.[^/]*/.*\\?(?:.*&)?(?:p|q)=([^&]*).*");

    public UICaptor(Context context) {
        mContext = context;
    }

    public void monitor(AccessibilityEvent event) {   //parser
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            date = new Date();
            AccessibilityNodeInfo source;
            source = event.getSource();
            if (source != null) {
                source.refresh();
                final String VIEW_ID_GOOGLE_QUICK_SEARCH_BOX = "com.google.android.googlequicksearchbox:id/search_box";
                final String VIEW_ID_CHROME_URL_BAR = "com.android.chrome:id/url_bar";
                final String VIEW_ID_ANDROID_BROWSER_URL_BAR = "com.android.browser:id/url";
                final String VIEW_ID_SAMSUNG_BROWSER_URL_BAR = "com.sec.android.app.sbrowser:id/sbrowser_url_bar";

                if (source.getViewIdResourceName() != null && source.getViewIdResourceName().toString().equals(VIEW_ID_GOOGLE_QUICK_SEARCH_BOX)) {
                    if (source.getText() != null) {
                        if (!source.isFocused()) {
                            String sNewSearch = source.getText().toString();
                            processNewSearch(sNewSearch);
                        }
                    }
                } else if (source.getViewIdResourceName() != null && source.getViewIdResourceName().toString().equals(VIEW_ID_CHROME_URL_BAR)) {
                    if (source.getText() != null) {
                        if (!source.isFocused()) {
                            String sNewUrl = source.getText().toString();
                            processNewUrl(sNewUrl);
                        }
                    }
                } else if (source.getViewIdResourceName() != null && source.getViewIdResourceName().toString().equals(VIEW_ID_ANDROID_BROWSER_URL_BAR)) {
                    if (source.getText() != null) {
                        if (!source.isFocused()) {
                            String sNewUrl = source.getText().toString();
                            processNewUrl(sNewUrl);
                        }
                    }
                } else if (source.getViewIdResourceName() != null && source.getViewIdResourceName().toString().equals(VIEW_ID_SAMSUNG_BROWSER_URL_BAR)) {
                    if (source.getText() != null) {
                        if (!source.isFocused()) {
                            String sNewUrl = source.getText().toString();
                            if (!isDomainLocation(sNewUrl) || !sLastUrl.contains(sNewUrl)) {
                                processNewUrl(sNewUrl);
                            }
                        }
                    }
                }
            }
        }
    }

    private void processNewUrl(String sNewUrl) {
        if (!sNewUrl.isEmpty() && !sNewUrl.equals(sLastUrl)) {
            Log.i(TAG, "url:" + sNewUrl);
            Log.i(TAG, "time:" + date + " : " + date.getTime());
            // Send URL string to MainService
            updateUrl(sNewUrl);
            sLastUrl = sNewUrl;
        }
    }

    private void processNewSearch(String sNewSearch) {
        if (!sNewSearch.isEmpty() && !sNewSearch.equals(sLastSearch)) {
            Log.i(TAG, "search:" + sNewSearch);
            Log.i(TAG, "time:" + date + " : " + date.getTime());
            // Send new search string to service
            // Changed since 20200224
            updateSearchItem(sNewSearch);
            sLastSearch = sNewSearch;
        }
    }

    private String getSearchText(String sUrl) {
        for (String sPattern : analyzeUrlPatternList) {
            Pattern pattern = Pattern.compile(sPattern);
            Matcher matcher = pattern.matcher(sUrl);
            if (matcher.matches()) {
                MatchResult result = matcher.toMatchResult();
                if (result.groupCount() == 1) {
                    String sSearch = result.group(1);
                    sSearch = sSearch.replaceAll("\\+", " ");
                    try {
                        sSearch = URLDecoder.decode(sSearch, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                    }
                    return sSearch;
                }
            }
        }
        return "";
    }

    private Boolean isDomainLocation(String sUrl) {
        final String PATTERN = "(?:https?://)?([^/]+)/?";
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(sUrl);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    private void updateUrl(String url){
        Intent intentBroadcast = new Intent(BroadcastActionConstant.MONITOR_URL_UPDATED);
        intentBroadcast.putExtra(BroadcastActionConstant.KEY_URL, url);
        mContext.sendBroadcast(intentBroadcast);
    }

    private void updateSearchItem(String item){
        Intent intentBroadcast = new Intent(BroadcastActionConstant.MONITOR_SEARCH_NEW_ITEM);
        intentBroadcast.putExtra(BroadcastActionConstant.KEY_SEARCH, item);
        mContext.sendBroadcast(intentBroadcast);
    }

}
