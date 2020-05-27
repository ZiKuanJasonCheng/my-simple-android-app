package com.jc.webservice;

import java.util.List;

public interface LookupItemHandler {
    void onLookupItemSuccess(int requestId, List<ProductItem> listProductItem);
    void onLookupItemFailure(int requestId);
}
