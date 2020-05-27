package com.jc.webservice;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyWebservice {
    private static final String WEB_SERVICE_URL = "http://apitest.secure.com/WebService/";
    protected final AsyncHttpClient asyncHttpClient;
    protected int requestIdCount;

    private MyWebservice() {
        asyncHttpClient = new AsyncHttpClient();
        requestIdCount = 0;
    }

    private static class SingletonHelper {
        private static final MyWebservice INSTANCE = new MyWebservice();
    }

    public static MyWebservice getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private synchronized int getRequestId() {
        requestIdCount += 1;
        if (requestIdCount < 0) {
            requestIdCount = 1;
        }
        return requestIdCount;
    }

    public int lookupItem(LookupItemHandler context, EMarketplace marketplace, List<String> itemIdList) {
        if (context != null && itemIdList != null) {
            final WeakReference<LookupItemHandler> contextReference = new WeakReference<LookupItemHandler>(context);
            final String API_NAME = "LookupItem";
            final String uri = WEB_SERVICE_URL + API_NAME;
            final int requestId = getRequestId();
            RequestParams params = new RequestParams();
            params.put("marketplace", marketplace.getValue());
            params.put("itemId", buildItemId(itemIdList));
            asyncHttpClient.get(uri, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                    Log.i("lookupItem", "onStart");
                    Log.i("lookupItem", "URI:" + getRequestURI());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    Log.i("lookupItem", "onSuccess");

                    String sResponse = new String(response, StandardCharsets.UTF_8);
                    List<ProductItem> productItemList = null;
                    productItemList = parseWebserviceResponse(sResponse);
                    productItemList = productItemList != null ? productItemList : new ArrayList<ProductItem>();
                    if (contextReference != null) {
                        LookupItemHandler context = contextReference.get();
                        if (context != null) {
                            context.onLookupItemSuccess(requestId, productItemList);
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.i("lookupItem", "onFailure status:" + statusCode);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    // Progress notification
                    //Log.i("asyncHttpClient", "onProgress:"+bytesWritten +"/" + totalSize);
                }
            });
            return requestId;
        }
        return -1;
    }

    public int lookupItem(LookupItemHandler context, List<LookupItemParameter> ItemParameterList) {
        if (context != null && ItemParameterList != null) {
            final WeakReference<LookupItemHandler> contextReference = new WeakReference<LookupItemHandler>(context);
            final String API_NAME = "LookupItem2";
            final String uri = WEB_SERVICE_URL + API_NAME;
            final int requestId = getRequestId();
            RequestParams params = new RequestParams();
            params.put("itemIdMap", buildItemIdMap(ItemParameterList));
            asyncHttpClient.get(uri, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                    Log.i("lookupItem", "onStart");
                    Log.i("lookupItem", "URI:" + getRequestURI());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    Log.i("lookupItem", "onSuccess");

                    String sResponse = new String(response, StandardCharsets.UTF_8);
                    List<ProductItem> productItemList = null;
                    productItemList = parseWebserviceResponse(sResponse);
                    productItemList = productItemList != null ? productItemList : new ArrayList<ProductItem>();
                    if (contextReference != null) {
                        LookupItemHandler context = contextReference.get();
                        if (context != null) {
                            context.onLookupItemSuccess(requestId, productItemList);
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.i("lookupItem", "onFailure status:" + statusCode);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    // Progress notification
                    Log.i("asyncHttpClient", "onProgress:"+bytesWritten +"/" + totalSize);
                }
            });
            return requestId;
        }
        return -1;
    }

    private Map<String, String> buildItemIdMap(List<LookupItemParameter> ItemParameterList) {  //20200221
        Map<String, List<String>> itemIdListMap = new HashMap<String, List<String>>();
        Map<String, String> itemIdMap = new HashMap<String, String>();
        for (LookupItemParameter itemParameter : ItemParameterList) {
            if (!itemIdListMap.containsKey(itemParameter.getMarketplace().getValue())) {
                itemIdListMap.put(itemParameter.getMarketplace().getValue(), new ArrayList<String>());
            }
            itemIdListMap.get(itemParameter.getMarketplace().getValue()).add(itemParameter.getItemId());
        }
        for (Map.Entry<String, List<String>> entry : itemIdListMap.entrySet()) {
            itemIdMap.put(entry.getKey(), buildItemId(entry.getValue()));
        }
        return itemIdMap;
    }

    private String buildItemId(List<String> itemIdList) {
        final String SEPARATOR = ",";
        StringBuilder itemIdBuilder = new StringBuilder();
        for (String itemId : itemIdList) {
            itemIdBuilder.append(itemId).append(SEPARATOR);
        }
        itemIdBuilder.deleteCharAt(itemIdBuilder.length() - 1);
        return itemIdBuilder.toString();
    }

    public int searchItem(SearchItemHandler context, String keyword) {
        if (context != null && keyword != null) {
            final WeakReference<SearchItemHandler> contextReference = new WeakReference<SearchItemHandler>(context);
            final String API_NAME = "SearchItem";
            final String uri = WEB_SERVICE_URL + API_NAME;
            final int requestId = getRequestId();
            RequestParams params = new RequestParams();
            params.put("keyword", keyword.trim());
            asyncHttpClient.get(uri, params, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                    Log.i("searchItem", "onStart");
                    Log.i("searchItem", "URI:" + getRequestURI());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    Log.i("searchItem", "onSuccess");

                    String sResponse = new String(response, StandardCharsets.UTF_8);
                    List<ProductItem> productItemList = null;
                    productItemList = parseWebserviceResponse(sResponse);
                    productItemList = productItemList != null ? productItemList : new ArrayList<ProductItem>();
                    if (contextReference != null) {
                        SearchItemHandler context = contextReference.get();
                        if (context != null) {
                            context.onSearchItemSuccess(requestId, productItemList);
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.i("searchItem", "onFailure status:" + statusCode);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    // Progress notification
                    //Log.i("asyncHttpClient", "onProgress:"+bytesWritten +"/" + totalSize);
                }
            });
            return requestId;
        }
        return -1;
    }

    private List<ProductItem> parseWebserviceResponse(String response) {
        if (response == null) {
            //System.out.println("parseWebserviceResponse error: 1");
            return null;
        }
        DocumentFactory factory = DocumentFactory.getInstance();
        SAXReader reader = new SAXReader(factory);
        org.dom4j.Document document = null;
        try {
            document = reader.read(new StringReader(response));
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //System.out.println("parseWebserviceResponse error: 2");
            return null;
        }
        //System.out.println("doc:" + document.asXML());
        XPath xPathItemNode = DocumentHelper.createXPath("AlvoseWebservice/Items/Item");
        List<Node> listItemNode = xPathItemNode.selectNodes(document);
        if (listItemNode.size() > 0) {
            List<ProductItem> productItemList = null;
            productItemList = new ArrayList<ProductItem>();
            for (Node nodeItem : listItemNode) {
                XPath xPath = null;
                xPath = DocumentHelper.createXPath("marketplace");
                String marketplace = xPath.selectSingleNode(nodeItem) != null
                        ? xPath.selectSingleNode(nodeItem).getText() : "";
                xPath = DocumentHelper.createXPath("itemId");
                String itemId = xPath.selectSingleNode(nodeItem) != null ? xPath.selectSingleNode(nodeItem).getText()
                        : "";
                xPath = DocumentHelper.createXPath("title");
                String title = xPath.selectSingleNode(nodeItem) != null ? xPath.selectSingleNode(nodeItem).getText()
                        : "";
                xPath = DocumentHelper.createXPath("price");
                String price = xPath.selectSingleNode(nodeItem) != null ? xPath.selectSingleNode(nodeItem).getText()
                        : "";
                xPath = DocumentHelper.createXPath("url");
                String url = xPath.selectSingleNode(nodeItem) != null ? xPath.selectSingleNode(nodeItem).getText() : "";
                xPath = DocumentHelper.createXPath("imageUrl");
                String imageUrl = xPath.selectSingleNode(nodeItem) != null ? xPath.selectSingleNode(nodeItem).getText()
                        : "";
                ProductItem newProductItem = new ProductItem(EMarketplace.from(marketplace), itemId, title, price, url, imageUrl);
                productItemList.add(newProductItem);
            }
            return productItemList;
        }
        //System.out.println("parseWebserviceResponse error: 3");
        return null;
    }
}